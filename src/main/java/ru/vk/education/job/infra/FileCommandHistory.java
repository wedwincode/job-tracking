package ru.vk.education.job.infra;

import ru.vk.education.job.app.port.CommandHistory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class FileCommandHistory implements CommandHistory {

    private final Path path;

    public FileCommandHistory(String fileName) {
        this.path = Path.of(fileName);
    }

    @Override
    public void save(String command) {
        try {
            Files.writeString(
                    path,
                    command + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new RuntimeException("failed to save command", e);
        }
    }

    @Override
    public List<String> getAll() {
        try {
            if (Files.notExists(path)) {
                return Collections.emptyList();
            }
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException("failed to read commands", e);
        }
    }
}
