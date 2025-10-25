package com.resumequill.app.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationDto extends LoginDto {
  @NotBlank(message = "First name must not be empty")
  @Size(max = 50, message = "First name must not exceed 50 characters")
  private String firstName;

  @Size(max = 50, message = "Last name must not exceed 50 characters")
  private String lastName;
}
