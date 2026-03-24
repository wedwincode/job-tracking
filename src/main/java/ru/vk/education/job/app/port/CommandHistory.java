package ru.vk.education.job.app.port;

import java.util.List;

public interface CommandHistory {
    void save(String command);
    List<String> getAll();
}
