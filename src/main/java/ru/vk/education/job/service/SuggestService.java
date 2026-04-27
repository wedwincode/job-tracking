package ru.vk.education.job.service;

import org.springframework.stereotype.Service;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.dto.UserResponseDto;
import ru.vk.education.job.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SuggestService {

    private final JobService jobService;
    private final UserService userService;

    public SuggestService(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    public Map<String, JobResponseDto> getSuggestionsForUsers(List<String> users) {
        Map<String, JobResponseDto> userVacancyMap = new HashMap<>();
        for (String u: users) {
            List<JobResponseDto> suggestions = getSuggestions(u);
            JobResponseDto suggestion = null;
            if (!suggestions.isEmpty()) {
                suggestion = suggestions.get(0);
            }
            userVacancyMap.put(u, suggestion);
        }
        return userVacancyMap;
    }

    public List<JobResponseDto> getSuggestions(String userName) {
        UserResponseDto user = userService.getByName(userName);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        return jobService.getAll().stream()
                .map(vacancy -> Map.entry(vacancy, calculateScore(user, vacancy)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<JobResponseDto, Double>comparingByValue().reversed())
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();
    }

    public long countMatches(UserResponseDto user) {
        return jobService.getAll().stream()
                .map(v -> calculateScore(user, v))
                .filter(s -> s > 0)
                .count();
    }

    private double calculateScore(UserResponseDto user, JobResponseDto vacancy) {
        Set<String> userSkills = new HashSet<>(user.skills());
        Set<String> vacancySkills = new HashSet<>(vacancy.tags());
        userSkills.retainAll(vacancySkills);

        double score = userSkills.size();
        if (user.experience() < vacancy.experience()) {
            score /= 2.0;
        }

        return score;
    }
}
