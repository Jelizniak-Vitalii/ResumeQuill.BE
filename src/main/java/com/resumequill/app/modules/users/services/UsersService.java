package com.resumequill.app.modules.users.services;

import com.resumequill.app.modules.users.dao.UserDao;
import com.resumequill.app.modules.users.models.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UsersService {
  private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

  private final UserDao userDao;

  public UsersService(
    UserDao userDao
  ) {
    this.userDao = userDao;
  }

  public UserModel getUserById(int id) {
    UserModel user = userDao.findById(id);
    user.setPassword(null);

    logger.info("Getting user by id: {}", id);
    return user;
  }

  public UserModel getUserByEmail(String email) {
    logger.info("Getting user by email: {}", email);

    return userDao.findByEmail(email);
  }

  public int createUser(UserModel user) {
    return userDao.createUser(user);
  }

  public void updateUser(UserModel user) {
    userDao.update(user);
  }

  public void updateLang(int userId, String lang) {
    userDao.updateLang(userId, lang);

    logger.info("Updating user lang for user id: {}", userId);
  }
}
