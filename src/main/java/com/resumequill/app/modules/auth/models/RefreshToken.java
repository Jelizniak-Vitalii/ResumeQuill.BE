package com.resumequill.app.modules.auth.models;

import lombok.Data;

import java.time.Instant;

@Data
public class RefreshToken {
  private int id;
  private int userId;
  private String ip;
  private String token;
  private String userAgent;
  private Instant expiresAt;

  public RefreshToken() {}

  public RefreshToken(
    int id,
    int userId,
    String token,
    String userAgent,
    String ip,
    Instant expiresAt
  ) {
    this.id = id;
    this.userId = userId;
    this.token = token;
    this.userAgent = userAgent;
    this.ip = ip;
    this.expiresAt = expiresAt;
  }

  public RefreshToken(
    int userId,
    String token,
    String userAgent,
    String ip,
    Instant expiresAt
  ) {
    this.userId = userId;
    this.token = token;
    this.userAgent = userAgent;
    this.ip = ip;
    this.expiresAt = expiresAt;
  }
}
