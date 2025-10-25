package com.resumequill.app.modules.users.controllers;

import com.resumequill.app.modules.auth.guards.AuthGuard;
import com.resumequill.app.modules.users.models.UserModel;
import com.resumequill.app.modules.users.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
