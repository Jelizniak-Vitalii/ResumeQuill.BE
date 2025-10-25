package com.resumequill.app.modules.auth.services;

import com.resumequill.app.modules.auth.constants.AuthConstants;
import com.resumequill.app.modules.auth.models.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private String jwtExpiration;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
      .verifyWith(getSecretKey())
      .build()
      .parseSignedClaims(token)
      .getPayload();
  }

  public String createAccessToken(int userId) {
    Instant now = Instant.now();
    return Jwts.builder()
      .subject(String.valueOf(userId))
      .claim("userId", userId)
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plusSeconds(AuthConstants.ACCESS_TTL_SEC)))
      .signWith(getSecretKey())
      .compact();
  }

  public RefreshToken createRefreshToken(int userId, String userAgent, String ip) {
    Instant expiresAt = Instant.now().plusSeconds(AuthConstants.REFRESH_TTL_SEC);
    String token = UUID.randomUUID().toString();

    return new RefreshToken(
      userId,
      token,
      userAgent,
      ip,
      expiresAt
    );
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parser()
        .verifyWith(getSecretKey())
        .build()
        .parseSignedClaims(token);

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public Long extractUserId(String token) {
    return getClaims(token).get("userId", Long.class);
  }

  public String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
