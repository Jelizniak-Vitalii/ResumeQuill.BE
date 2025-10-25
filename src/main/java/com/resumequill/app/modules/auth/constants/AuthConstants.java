package com.resumequill.app.modules.auth.constants;

public final class AuthConstants {
  public static final String ACCESS_TOKEN  = "access_token";
  public static final String REFRESH_TOKEN = "refresh_token";
  public static final String AUTH_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static long ACCESS_TTL_SEC = 15 * 60;          // 15 мин
  public static long REFRESH_TTL_SEC = 30L * 24 * 3600; // 30 дней
}
