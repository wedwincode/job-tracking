package ru.vk.education.job.dto;

import java.util.List;

public record CreateUserRequestDto(
    String name,
    List<String> skills,
    int experience
) {
}
