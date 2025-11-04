package com.resumequill.app.modules.auth.filters;

import com.resumequill.app.modules.auth.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final TokenService tokenService;

  private static final List<String> PUBLIC = List.of(
    "/api/v2/auth/**", "/public/**", "/health", "/actuator/**", "/docs/**"
  );

  public JwtAuthFilter(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  private final List<AntPathRequestMatcher> publicMatchers = PUBLIC.stream()
    .map(AntPathRequestMatcher::new)
    .toList();

  private boolean isPublic(HttpServletRequest req) {
    return publicMatchers.stream().anyMatch(m -> m.matches(req));
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest req) {
    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true; // CORS
    String path = req.getServletPath(); // или:
    boolean skip = isPublic(req);
    System.out.println("shouldNotFilter: " + req.getRequestURI() + " -> " + skip);

    return skip;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
    throws IOException, ServletException {

    String token = tokenService.resolveToken(req);

    if (token == null) { chain.doFilter(req, res); return; }

    if (!tokenService.validateToken(token)) {
      res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      res.setContentType("application/json");
      res.getWriter().write("{\"error\":\"Invalid token\"}");
      return;
    }

    Long userId = tokenService.extractUserId(token);
    req.setAttribute("userId", userId);
    chain.doFilter(req, res);
  }
}
