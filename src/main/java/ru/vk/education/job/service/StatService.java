package ru.vk.education.job.service;

import org.springframework.stereotype.Service;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.dto.UserResponseDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatService {

    private final UserService userService;
    private final JobService jobService;
    private final SuggestService suggestService;

    public StatService(UserService userService, JobService jobService, SuggestService suggestService) {
        this.userService = userService;
        this.jobService = jobService;
        this.suggestService = suggestService;
    }

    public List<JobResponseDto> getVacanciesByExp(int exp) {
        return jobService.getAll().stream()
                .filter(v -> v.experience() >= exp)
                .sorted(Comparator.comparing(JobResponseDto::title))
                .toList();
    }

    public List<UserResponseDto> getUsersByMatch(int match) {
        return userService.getAll().stream()
                .filter(user -> suggestService.countMatches(user) >= match)
                .sorted(Comparator.comparing(UserResponseDto::name))
                .toList();
    }

    public List<String> getTopSkills(int limit) {
        return userService.getAll().stream()
                .flatMap(user -> user.skills().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();
    }
}
