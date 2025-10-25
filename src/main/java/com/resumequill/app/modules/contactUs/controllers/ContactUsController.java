package com.resumequill.app.modules.contactUs.controllers;

import com.resumequill.app.modules.contactUs.dto.ContactUsDto;
import com.resumequill.app.modules.contactUs.services.ContactUsService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/contactUs", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactUsController {
  private final ContactUsService contactUsService;

  public ContactUsController(ContactUsService contactUsService) {
    this.contactUsService = contactUsService;
  }

  @PostMapping
  public ResponseEntity<Void> send(@Valid @RequestBody ContactUsDto data) {
    return ResponseEntity.noContent().build();
  }
}
