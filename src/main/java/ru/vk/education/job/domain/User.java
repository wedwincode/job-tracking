package ru.vk.education.job.domain;

import java.util.Comparator;
import java.util.List;

public record User(
        String name,
        List<Skill> skills,
        Experience experience
) {
    public User {
        skills = skills.stream()
                .distinct()
                .sorted(Comparator.comparing(Skill::value))
                .toList();
    }
}
