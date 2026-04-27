package ru.vk.education.job.dto;

import java.util.List;

public record UserResponseDto(
        String name,
        List<String> skills,
        int experience
) {
}
