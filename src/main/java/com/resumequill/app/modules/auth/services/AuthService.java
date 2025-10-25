package com.resumequill.app.modules.auth.services;

import com.resumequill.app.common.constants.Messages;
import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.common.handlers.GlobalExceptionHandler;
import com.resumequill.app.modules.auth.constants.AuthConstants;
import com.resumequill.app.modules.auth.dao.TokensDao;
import com.resumequill.app.modules.auth.dto.AuthResponseDto;
import com.resumequill.app.modules.auth.dto.RegistrationDto;
import com.resumequill.app.modules.auth.models.RefreshToken;
import com.resumequill.app.modules.auth.utils.CookieUtils;
import com.resumequill.app.modules.users.models.UserModel;
import com.resumequill.app.modules.users.services.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthService {
  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final UsersService usersService;
  private final TokensDao tokensDao;

  public AuthService(
    PasswordEncoder passwordEncoder,
    TokenService tokenService,
    UsersService usersService,
    TokensDao tokensDao
  ) {
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.usersService = usersService;
    this.tokensDao = tokensDao;
  }

  private RefreshToken validateRefreshToken(String token) {
    RefreshToken refreshToken = tokensDao.findByToken(token)
      .orElseThrow(() -> new UnauthorizedException(Messages.AUTH_INVALID_TOKEN));

    if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
      logger.error("Expired refresh token used: {}", token);

      throw new UnauthorizedException(Messages.AUTH_INVALID_TOKEN);
    }

    return refreshToken;
  }

  @Transactional
  public AuthResponseDto login(String email, String password, String ip, String userAgent) {
    UserModel user = usersService.getUserByEmail(email);

    if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
      logger.error("Invalid login attempt for email: {}", email);

      throw new IllegalStateException(Messages.AUTH_INVALID_CREDENTIALS);
    }

    String accessToken = tokenService.createAccessToken(user.getId());
    RefreshToken refreshToken = tokenService.createRefreshToken(user.getId(), ip, userAgent);

    tokensDao.create(refreshToken);

    logger.error("User logged in: {}", email);

    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }

  @Transactional
  public AuthResponseDto register(RegistrationDto registrationDto, String ip, String userAgent) {
    UserModel user = usersService.getUserByEmail(registrationDto.getEmail());

    if (user != null) {
      logger.error("Attempt to register with existing email: {}", registrationDto.getEmail());

      throw new IllegalStateException(String.format(Messages.USER_EMAIL_ALREADY_EXISTS, registrationDto.getEmail()));
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

    logger.error("New user registered: {}", registrationDto.getEmail());

    return new AuthResponseDto(accessToken, refreshToken.getToken());
  }

  @Transactional
  public void logout(String token) {
    logger.error("Logging out token: {}", token);

    tokensDao.deleteByToken(token);
  }

  @Transactional
  public AuthResponseDto refreshToken(String token, String ip, String userAgent) {
    RefreshToken refreshToken = validateRefreshToken(token);

    tokensDao.deleteByToken(token);

    String accessToken = tokenService.createAccessToken(refreshToken.getUserId());
    RefreshToken newRefreshToken = tokenService.createRefreshToken(refreshToken.getUserId(), ip, userAgent);

    tokensDao.create(newRefreshToken);

    logger.error("Refresh token rotated for userId: {}", refreshToken.getUserId());

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

  public void setAccessCookie(
    HttpServletResponse res,
    String name,
    String value
  ) {
    CookieUtils.addCookie(res, name, value, AuthConstants.ACCESS_TTL_SEC, "Lax");
  }

  public void setRefreshCookie(
    HttpServletResponse res,
    String name,
    String value
  ) {
    CookieUtils.addCookie(res, name, value, AuthConstants.REFRESH_TTL_SEC, "Strict");
  }

  public void clearCookie(HttpServletResponse res) {
    CookieUtils.clearCookie(res, AuthConstants.ACCESS_TOKEN);
    CookieUtils.clearCookie(res, AuthConstants.REFRESH_TOKEN);
  }
}
