package com.resumequill.app.modules.auth.interceptors;

import com.resumequill.app.modules.auth.constants.AuthConstants;
import com.resumequill.app.modules.auth.guards.AuthGuard;
import com.resumequill.app.common.constants.Messages;
import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.modules.auth.services.TokenService;
import com.resumequill.app.modules.auth.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(JwtInterceptor.class);
  private final TokenService tokenService;

  public JwtInterceptor(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  public String resolveToken(HttpServletRequest request) {
    String header = request.getHeader(AuthConstants.AUTH_HEADER);
    if (header != null && header.startsWith(AuthConstants.BEARER_PREFIX)) {
      return header.substring(AuthConstants.BEARER_PREFIX.length()).trim();
    }

    return CookieUtils.getCookieValue(request, AuthConstants.ACCESS_TOKEN);
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // CORS preflight

    if (!(handler instanceof HandlerMethod method)) {
      return true;
    }

    AuthGuard authGuard = method.getMethodAnnotation(AuthGuard.class);
    AuthGuard classAuthGuard = method.getBeanType().getAnnotation(AuthGuard.class);

    if (authGuard == null && classAuthGuard == null) {
      return true;
    }

    String token = resolveToken(request);

    if (token == null || !tokenService.validateToken(token)) {
      logger.error("Invalid JWT token used in request - {}", request.getRequestURI());

      throw new UnauthorizedException(Messages.AUTH_PERMISSION_UNAUTHORIZED);
    }

    String userId = String.valueOf(tokenService.extractUserId(token));

    MDC.put("userId", userId);

    request.setAttribute("userId", userId);

    return true;
  }

  @Override
  public void afterCompletion(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    Exception ex
  ) {
    MDC.remove("userId");
  }
}
