package ru.vk.education.job.service;

import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.app.port.VacancyStorage;
import ru.vk.education.job.domain.Match;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.domain.Vacancy;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchingService {
    private final VacancyStorage vacancyStorage;

    public MatchingService(VacancyStorage vacancyStorage) {
        this.vacancyStorage = vacancyStorage;
    }

    public Map<User, Vacancy> getSuggestionsForUsers(List<User> users) {
        Map<User, Vacancy> userVacancyMap = new HashMap<>();
        for (User u: users) {
            List<Vacancy> suggestions = getSuggestions(u);
            Vacancy suggestion = null;
            if (!suggestions.isEmpty()) {
                suggestion = suggestions.get(0);
            }
            userVacancyMap.put(u, suggestion);
        }
        return userVacancyMap;
    }

    public List<Vacancy> getSuggestions(User user) {
        return vacancyStorage.getAll().stream()
                .map(vacancy -> new Match(user, vacancy, calculateScore(user, vacancy)))
                .filter(match -> match.score() > 0)
                .sorted(Comparator.comparingDouble(Match::score).reversed())
                .limit(2)
                .map(Match::vacancy)
                .toList();
    }

    public long countMatches(User user) {
        return vacancyStorage.getAll().stream()
                .map(v -> calculateScore(user, v))
                .filter(s -> s > 0)
                .count();
    }

    private double calculateScore(User user, Vacancy vacancy) {
        Set<String> userSkills = user.skills().stream()
                .map(Skill::value)
                .collect(Collectors.toSet());

        Set<String> vacancySkills = vacancy.tags().stream()
                .map(Skill::value)
                .collect(Collectors.toSet());

        userSkills.retainAll(vacancySkills);

        double score = userSkills.size();

        if (user.experience().value() < vacancy.experience().value()) {
            score /= 2.0;
        }

        return score;
    }
}
