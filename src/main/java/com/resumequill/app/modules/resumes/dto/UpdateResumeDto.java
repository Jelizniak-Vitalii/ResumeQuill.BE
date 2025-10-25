package com.resumequill.app.modules.resumes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateResumeDto extends CreateResumeDto {
  @NotNull(message = "Resume id is required")
  @Positive(message = "Resume id must be > 0")
  private int id;
}
