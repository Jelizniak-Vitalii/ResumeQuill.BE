package com.resumequill.app.modules.users.controllers;

import com.resumequill.app.modules.auth.guards.AuthGuard;
import com.resumequill.app.modules.users.dto.UserChangeLangDto;
import com.resumequill.app.modules.users.models.UserModel;
import com.resumequill.app.modules.users.services.UsersService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@AuthGuard
@Controller
@RequestMapping("/users")
public class UsersController {
  private final UsersService usersService;

  public UsersController(UsersService usersService) {
    this.usersService = usersService;
  }

  @GetMapping
  public ResponseEntity<UserModel> get(@RequestAttribute("userId") int userId) {
    return ResponseEntity.ok(usersService.getUserById(userId));
  }

  @PostMapping("/changeLang")
  public ResponseEntity<Void> changeLang(
    @RequestAttribute("userId") int userId,
    @Valid @RequestBody UserChangeLangDto userChangeLangDto
  ) {
    usersService.updateLang(userId, userChangeLangDto.getLang());

    return ResponseEntity.noContent().build();
  }
}
