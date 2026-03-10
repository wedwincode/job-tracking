package ru.vk.education.job.infra;

import ru.vk.education.job.domain.User;
import ru.vk.education.job.port.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum InMemoryUserStorage implements UserStorage {

    INSTANCE;

    private final Map<String, User> usersByName;

    InMemoryUserStorage() {
        this.usersByName = new LinkedHashMap<>();
    }

    @Override
    public void save(User user) {
        usersByName.putIfAbsent(user.name(), user);
    }

    @Override
    public User getByName(String name) {
        return usersByName.get(name);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(usersByName.values());
    }
}
