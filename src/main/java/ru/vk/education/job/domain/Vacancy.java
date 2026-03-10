package ru.vk.education.job.domain;

import java.util.Comparator;
import java.util.List;

public record Vacancy(
        String title,
        Company company,
        List<Skill> tags,
        Experience experience
) {
    public Vacancy {
        tags = tags.stream()
                .distinct()
                .sorted(Comparator.comparing(Skill::value))
                .toList();
    }
}
