package ru.vk.education.job.repository;

import org.springframework.stereotype.Repository;
import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.infra.InMemoryUserStorage;

import java.util.List;

@Repository
public class UserRepository {

    private final UserStorage users = InMemoryUserStorage.INSTANCE;

    public void create(User user) {
        users.save(user);
    }

    public List<User> getAll() {
        return users.getAll();
    }

    public User getByName(String name) {
        return users.getByName(name);
    }
}
