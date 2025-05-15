package com.company.kanban.service.interfaces;

import com.company.kanban.model.entity.User;

public interface UserService {

    User getUser(Long id);

    User getUser(String username);

    User saveUser(User user);

    void deleteUser(Long id);
}
