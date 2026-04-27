package ru.vk.education.job.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.dto.UserResponseDto;

import java.util.Map;

@Component
public class BackgroundJobRecommender {
    private final UserService userService;
    private final SuggestService suggestService;
    private volatile Map<String, JobResponseDto> lastRecommended = Map.of();

    public BackgroundJobRecommender(UserService userService, SuggestService suggestService) {
        this.userService = userService;
        this.suggestService = suggestService;
    }

    @Async
    @Scheduled(fixedRate = 10_000)
    public void run() {
        lastRecommended = suggestService.getSuggestionsForUsers(
                userService.getAll().stream().map(UserResponseDto::name).toList()
        );
    }

    public Map<String, JobResponseDto> getLastRecommended() {
        return lastRecommended;
    }
}