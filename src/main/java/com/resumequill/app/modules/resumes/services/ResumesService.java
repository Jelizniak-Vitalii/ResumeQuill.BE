package com.resumequill.app.modules.resumes.services;

import com.resumequill.app.common.exceptions.NotFoundException;
import com.resumequill.app.modules.resumes.dao.ResumesDao;
import com.resumequill.app.modules.resumes.dto.CreateResumeDto;
import com.resumequill.app.modules.resumes.models.ResumeModel;
import com.resumequill.app.modules.users.models.UserModel;
import com.resumequill.app.modules.users.services.UsersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResumesService {
  private final ResumesDao resumesDao;
  private final UsersService usersService;

  public ResumesService(
    ResumesDao resumesDao,
    UsersService usersService
  ) {
    this.resumesDao = resumesDao;
    this.usersService = usersService;
  }

  @Transactional
  public ResumeModel createResume(int userId, CreateResumeDto data) {
    UserModel user = usersService.getUserById(userId);

    if (user == null) {
      throw new IllegalStateException("User with id " + userId + " does not exist");
    }

    return resumesDao.create(userId, data.getData().toString());
  }

  @Transactional
  public ResumeModel getResume(int userId, int resumeId) {
    return resumesDao.findById(userId, resumeId)
      .orElseThrow(() -> new NotFoundException("Resume with id " + resumeId + " does not exist"));
  }

  @Transactional
  public List<ResumeModel> getResumes(int userId) {
    return resumesDao.findAllByUser(userId);
  }

  @Transactional
  public void updateResume(int userId, int resumeId, String data) {
    int updated = resumesDao.updateData(userId, resumeId, data);

    if (updated == 0) {
      throw new NotFoundException("Resume with id " + resumeId + " does not exist");
    }
  }

  @Transactional
  public void deleteResume(int userId, int resumeId) {
    int rows = resumesDao.delete(userId, resumeId);
    if (rows == 0) {
      throw new NotFoundException("Resume with id " + resumeId + " does not exist");
    }
  }
}
