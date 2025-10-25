package com.resumequill.app.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {
  @NotBlank(message = "Email must not be empty")
  @Email(message = "Invalid email format")
  @Size(max = 255, message = "Email must not exceed 255 characters")
  private String email;

  @NotBlank(message = "Password must not be empty")
  @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
  private String password;
}
