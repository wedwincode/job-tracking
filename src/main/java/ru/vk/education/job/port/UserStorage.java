package ru.vk.education.job.port;

import ru.vk.education.job.domain.User;

import java.util.List;

public interface UserStorage {
    void save(User user);
    User getByName(String name);
    List<User> getAll();
}
