package com.resumequill.app.modules.users.services;

import com.resumequill.app.modules.users.dao.UserDao;
import com.resumequill.app.modules.users.models.UserModel;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
  private final UserDao userDao;

  public UsersService(
    UserDao userDao
  ) {
    this.userDao = userDao;
  }

  public UserModel getUserById(int id) {
    UserModel user = userDao.findById(id);
    user.setPassword(null);
    return user;
  }

  public UserModel getUserByEmail(String email) {
    return userDao.findByEmail(email);
  }

  public int createUser(UserModel user) {
    return userDao.createUser(user);
  }

  public void updateUser(UserModel user) {
    userDao.update(user);
  }
}
