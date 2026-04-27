package ru.vk.education.job.dto;

import java.util.List;

public record CreateJobRequestDto(
        String title,
        String company,
        List<String> tags,
        int experience
) {
}
