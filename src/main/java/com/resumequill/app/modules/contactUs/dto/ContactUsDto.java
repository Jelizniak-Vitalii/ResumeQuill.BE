package com.resumequill.app.modules.contactUs.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactUsDto {
  @NotBlank(message = "Subject must not be empty")
  private String subject;

  @NotBlank(message = "Email must not be empty")
  @Email(message = "Invalid email format")
  private String email;

  @NotBlank(message = "Message must not be empty")
  private String message;
}
