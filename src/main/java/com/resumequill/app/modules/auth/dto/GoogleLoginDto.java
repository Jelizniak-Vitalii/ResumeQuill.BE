package com.resumequill.app.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginDto {
  @NotBlank(message = "ID token is required")
  private String idToken;
}
