package com.resumequill.app.modules.auth.services;

import com.resumequill.app.common.constants.Messages;
import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.modules.auth.constants.AuthConstants;
import com.resumequill.app.modules.auth.dao.PasswordResetTokenDao;
import com.resumequill.app.modules.auth.dao.TokensDao;
import com.resumequill.app.modules.auth.dto.AuthResponseDto;
import com.resumequill.app.modules.auth.dto.RegistrationDto;
import com.resumequill.app.modules.auth.models.PasswordResetToken;
import com.resumequill.app.modules.auth.models.RefreshToken;
import com.resumequill.app.modules.auth.utils.CookieUtils;
import com.resumequill.app.modules.users.models.UserModel;
import com.resumequill.app.modules.users.services.UsersService;
import com.resumequill.app.mail.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import com.resumequill.app.modules.auth.models.GoogleUserInfo;

@Service
public class AuthService {
  private static final int PASSWORD_RESET_TOKEN_EXPIRY_HOURS = 1;

  private final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final UsersService usersService;
  private final TokensDao tokensDao;
  private final GoogleService googleService;
  private final PasswordResetTokenDao passwordResetTokenDao;
  private final EmailService emailService;
  private final String frontendUrl;

  public AuthService(
    PasswordEncoder passwordEncoder,
    TokenService tokenService,
    UsersService usersService,
    TokensDao tokensDao,
    GoogleService googleService,
    PasswordResetTokenDao passwordResetTokenDao,
    EmailService emailService,
    @Value("${app.frontend.url}") String frontendUrl
  ) {
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.usersService = usersService;
    this.tokensDao = tokensDao;
    this.googleService = googleService;
    this.passwordResetTokenDao = passwordResetTokenDao;
    this.emailService = emailService;
    this.frontendUrl = frontendUrl;
  }

  private RefreshToken validateRefreshToken(String token) {
    RefreshToken refreshToken = tokensDao.findByToken(token)
      .orElseThrow(() -> {
        logger.error("Invalid refresh token used: {}", token);
        return new UnauthorizedException(Messages.AUTH_PERMISSION_UNAUTHORIZED);
      });

    if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
      logger.error("Expired refresh token used: {}", token);

      throw new UnauthorizedException(Messages.AUTH_PERMISSION_UNAUTHORIZED);
    }

    return refreshToken;
  }

  @Transactional
  public AuthResponseDto login(String email, String password, String ip, String userAgent) {
    UserModel user = usersService.getUserByEmail(email);

    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
      logger.error("Invalid login attempt for email: {}", email);

      throw new UnauthorizedException(Messages.AUTH_INVALID_CREDENTIALS);
    }

    String accessToken = tokenService.createAccessToken(user.getId());
    RefreshToken refreshToken = tokenService.createRefreshToken(user.getId(), ip, userAgent);

    tokensDao.create(refreshToken);

    logger.info("User logged in: {}", email);

    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }

  @Transactional
  public AuthResponseDto register(RegistrationDto registrationDto, String ip, String userAgent) {
    UserModel user = usersService.getUserByEmail(registrationDto.getEmail());

    if (user != null) {
      logger.error("Attempt to register with existing email: {}", registrationDto.getEmail());

      throw new UnauthorizedException(String.format(Messages.USER_EMAIL_ALREADY_EXISTS, registrationDto.getEmail()));
    }

    UserModel candidate = new UserModel();
    candidate.setEmail(registrationDto.getEmail());
    candidate.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
    candidate.setFirstName(registrationDto.getFirstName());
    candidate.setLastName(registrationDto.getLastName());

    int userId = usersService.createUser(candidate);

    String accessToken = tokenService.createAccessToken(userId);
    RefreshToken refreshToken = tokenService.createRefreshToken(userId, ip, userAgent);

    tokensDao.create(refreshToken);

    logger.info("New user registered: {}", registrationDto.getEmail());

    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }

  @Transactional
  public void logout(String token) {
    logger.error("Logging out token: {}", token);

    tokensDao.deleteByToken(token);
  }

  @Transactional
  public AuthResponseDto loginWithGoogle(String idToken, String ip, String userAgent) {
    GoogleUserInfo userInfo = googleService.verifyIdToken(idToken);

    UserModel user = usersService.getUserByGoogleId(userInfo.googleId());

    if (user == null) {
      user = usersService.getUserByEmail(userInfo.email());

      if (user != null) {
        usersService.linkGoogleAccount(user.getId(), userInfo.googleId());
        logger.info("Linked Google account to existing user: {}", userInfo.email());
      } else {
        UserModel newUser = new UserModel();
        newUser.setEmail(userInfo.email());
        newUser.setFirstName(userInfo.firstName());
        newUser.setLastName(userInfo.lastName());
        newUser.setGoogleId(userInfo.googleId());
        newUser.setImage(userInfo.picture());

        int userId = usersService.createOAuthUser(newUser);
        newUser.setId(userId);
        user = newUser;

        logger.info("Created new user via Google OAuth: {}", userInfo.email());
      }
    }

    String accessToken = tokenService.createAccessToken(user.getId());
    RefreshToken refreshToken = tokenService.createRefreshToken(user.getId(), ip, userAgent);

    tokensDao.create(refreshToken);

    logger.info("User logged in via Google: {}", userInfo.email());

    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }

  @Transactional
  public AuthResponseDto refreshToken(String token, String ip, String userAgent) {
    RefreshToken refreshToken = validateRefreshToken(token);

    tokensDao.deleteByToken(token);

    String accessToken = tokenService.createAccessToken(refreshToken.getUserId());
    RefreshToken newRefreshToken = tokenService.createRefreshToken(refreshToken.getUserId(), ip, userAgent);

    tokensDao.create(newRefreshToken);

    logger.info("Refresh token rotated for userId: {}", refreshToken.getUserId());

    return new AuthResponseDto(accessToken, newRefreshToken.getToken());
  }

  public String getUserAgent(HttpServletRequest req) {
    return Optional.ofNullable(req.getHeader("User-Agent")).orElse("Unknown");
  }

  public String getClientIp(HttpServletRequest req) {
    String xfHeader = req.getHeader("X-Forwarded-For");
    if (xfHeader == null) {
      return req.getRemoteAddr();
    }

    return xfHeader.split(",")[0];
  }

  public void setAccessCookie(HttpServletResponse res, String name, String value) {
    CookieUtils.addCookie(res, name, value, AuthConstants.ACCESS_TTL_SEC, "Lax");
  }

  public void setRefreshCookie(HttpServletResponse res, String name, String value) {
    CookieUtils.addCookie(res, name, value, AuthConstants.REFRESH_TTL_SEC, "Strict");
  }

  public void clearCookie(HttpServletResponse res) {
    CookieUtils.clearCookie(res, AuthConstants.ACCESS_TOKEN);
    CookieUtils.clearCookie(res, AuthConstants.REFRESH_TOKEN);
  }

  @Transactional
  public void forgotPassword(String email) {
    UserModel user = usersService.getUserByEmail(email);

    if (user == null) {
      logger.warn("Password reset requested for non-existent email: {}", email);
      return;
    }

    if (user.getPassword() == null) {
      logger.warn("Password reset requested for OAuth-only user: {}", email);
      return;
    }

    passwordResetTokenDao.deleteByUserId(user.getId());

    PasswordResetToken resetToken = new PasswordResetToken();
    resetToken.setUserId(user.getId());
    resetToken.setToken(UUID.randomUUID().toString());
    resetToken.setExpiresAt(Instant.now().plus(PASSWORD_RESET_TOKEN_EXPIRY_HOURS, ChronoUnit.HOURS));

    passwordResetTokenDao.create(resetToken);

    String resetLink = frontendUrl + "/reset-password?token=" + resetToken.getToken();
    emailService.sendPasswordResetEmail(email, resetLink);

    logger.info("Password reset email sent to: {}", email);
  }

  @Transactional
  public void resetPassword(String token, String newPassword) {
    PasswordResetToken resetToken = passwordResetTokenDao.findByToken(token)
      .orElseThrow(() -> {
        logger.error("Invalid password reset token: {}", token);
        return new UnauthorizedException("Invalid or expired reset token");
      });

    if (resetToken.getExpiresAt().isBefore(Instant.now())) {
      passwordResetTokenDao.deleteByToken(token);
      logger.error("Expired password reset token: {}", token);
      throw new UnauthorizedException("Invalid or expired reset token");
    }

    usersService.updatePassword(resetToken.getUserId(), passwordEncoder.encode(newPassword));
    passwordResetTokenDao.deleteByToken(token);

    logger.info("Password reset successful for userId: {}", resetToken.getUserId());
  }
}
