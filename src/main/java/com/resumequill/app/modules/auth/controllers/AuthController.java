package com.resumequill.app.modules.auth.controllers;

import com.resumequill.app.common.constants.Messages;
import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.modules.auth.constants.AuthConstants;
import com.resumequill.app.modules.auth.dto.AuthResponseDto;
import com.resumequill.app.modules.auth.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.resumequill.app.modules.auth.dto.LoginDto;
import com.resumequill.app.modules.auth.dto.RegistrationDto;
import com.resumequill.app.modules.auth.services.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<Void> login(
    @Valid @RequestBody LoginDto loginDto,
    HttpServletRequest req,
    HttpServletResponse res
  ) {
    String ip = authService.getClientIp(req);
    String userAgent = authService.getUserAgent(req);
    AuthResponseDto tokens = authService.login(loginDto.getEmail(), loginDto.getPassword(), ip, userAgent);

    authService.setAccessCookie(res, AuthConstants.ACCESS_TOKEN, tokens.getAccessToken());
    authService.setRefreshCookie(res, AuthConstants.REFRESH_TOKEN, tokens.getRefreshToken());

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/registration")
  public ResponseEntity<Void> registration(
    @Valid @RequestBody RegistrationDto registrationDto,
    HttpServletRequest req,
    HttpServletResponse res
  ) {
    String ip = authService.getClientIp(req);
    String userAgent = authService.getUserAgent(req);
    AuthResponseDto tokens = authService.register(registrationDto, ip, userAgent);

    authService.setAccessCookie(res, AuthConstants.ACCESS_TOKEN, tokens.getAccessToken());
    authService.setRefreshCookie(res, AuthConstants.REFRESH_TOKEN, tokens.getRefreshToken());

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshToken(
    HttpServletRequest req,
    HttpServletResponse res
  ) {
    String refreshToken = CookieUtils.getCookieValue(req, AuthConstants.REFRESH_TOKEN);

    if (refreshToken == null) {
      logger.error("No refresh token found in cookie");

      throw new UnauthorizedException(Messages.AUTH_PERMISSION_UNAUTHORIZED);
    }

    String ip = authService.getClientIp(req);
    String userAgent = authService.getUserAgent(req);
    AuthResponseDto tokens = authService.refreshToken(refreshToken, ip, userAgent);

    authService.setAccessCookie(res, AuthConstants.ACCESS_TOKEN, tokens.getAccessToken());
    authService.setRefreshCookie(res, AuthConstants.REFRESH_TOKEN, tokens.getRefreshToken());

    return ResponseEntity.noContent().build();
  }

//  @AuthGuard
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
    HttpServletRequest req,
    HttpServletResponse res
  ) {
    String refreshToken = CookieUtils.getCookieValue(req, AuthConstants.REFRESH_TOKEN);

    if (refreshToken != null) {
      authService.logout(refreshToken);
    }

    authService.clearCookie(res);

    return ResponseEntity.noContent().build();
  }
}
