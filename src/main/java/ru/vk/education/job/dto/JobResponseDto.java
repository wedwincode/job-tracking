package ru.vk.education.job.dto;

import java.util.List;

public record JobResponseDto(
        String title,
        String company,
        List<String> tags,
        int experience
) {
}
