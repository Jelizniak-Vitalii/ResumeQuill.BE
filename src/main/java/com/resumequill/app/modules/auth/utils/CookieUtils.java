package com.resumequill.app.modules.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;

@Component
public class CookieUtils {
  public static String getCookieValue(HttpServletRequest request, String cookieName) {
    if (request.getCookies() != null) {
      return Arrays.stream(request.getCookies())
        .filter(cookie -> cookieName.equals(cookie.getName()))
        .map(Cookie::getValue)
        .filter(value -> value != null && !value.isBlank())
        .map(String::trim)
        .findFirst()
        .orElse(null);
    }
    return null;
  }

  public static void addCookie(
    HttpServletResponse res,
    String name,
    String value,
    long maxAgeSec,
    String sameSite
  ) {
    ResponseCookie c = ResponseCookie.from(name, value)
      .httpOnly(true).secure(true)
      .sameSite(sameSite)        // "Lax" для access, "Strict" для refresh
      .path("/")
      .maxAge(Duration.ofSeconds(maxAgeSec))
      .build();
    res.addHeader("Set-Cookie", c.toString());
  }

  public static void clearCookie(HttpServletResponse res, String name) {
    ResponseCookie c = ResponseCookie.from(name, "")
      .httpOnly(true)
      .secure(true)
      .path("/")
      .sameSite("Strict")
      .maxAge(Duration.ZERO)
      .build();
    res.addHeader("Set-Cookie", c.toString());
  }
}
