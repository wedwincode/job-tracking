package ru.vk.education.job.domain;

public record Match(
        User user,
        Vacancy vacancy,
        double score
) {
}
