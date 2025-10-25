package com.resumequill.app.modules.resumes.controllers;

import com.resumequill.app.modules.auth.guards.AuthGuard;
import com.resumequill.app.modules.resumes.dto.CreateResumeDto;
import com.resumequill.app.modules.resumes.dto.UpdateResumeDto;
import com.resumequill.app.modules.resumes.models.ResumeModel;
import com.resumequill.app.modules.resumes.services.ResumesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@AuthGuard
@RestController
@RequestMapping(value = "/resumes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ResumesController {
  private final ResumesService resumesService;

  public ResumesController(ResumesService resumesService) {
    this.resumesService = resumesService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<ResumeModel> resume(@RequestAttribute("userId") int userId, @PathVariable("id") int id) {
    ResumeModel resume = resumesService.getResume(userId, id);
    return ResponseEntity.ok(resume);
  }

  @GetMapping
  public ResponseEntity<List<ResumeModel>> resumes(@RequestAttribute("userId") int userId) {
    List<ResumeModel> resumes = resumesService.getResumes(userId);
    return ResponseEntity.ok(resumes);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResumeModel> create(@RequestAttribute("userId") int userId, @Valid @RequestBody CreateResumeDto data) {
    ResumeModel resume = resumesService.createResume(userId, data);
    return ResponseEntity.created(URI.create("/resumes/" + resume.getId())).body(resume);
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> update(@RequestAttribute("userId") int userId, @Valid @RequestBody UpdateResumeDto data) {
    resumesService.updateResume(userId, data.getId(), data.getData().toString());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> delete(@RequestAttribute("userId") int userId, @PathVariable("id") int id) {
    resumesService.deleteResume(userId, id);
    return ResponseEntity.noContent().build();
  }
}

// Handle /api/?id=12 - @RequestParam("id") int id
