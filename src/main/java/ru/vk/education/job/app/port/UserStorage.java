package ru.vk.education.job.app.port;

import ru.vk.education.job.domain.User;

import java.util.List;

public interface UserStorage {
    void save(User user);
    User getByName(String name);
    List<User> getAll();
}
