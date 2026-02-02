package com.resumequill.app.modules.auth.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
  private int id;
  private int userId;
  private String token;
  private Instant createdAt;
  private Instant expiresAt;
}
