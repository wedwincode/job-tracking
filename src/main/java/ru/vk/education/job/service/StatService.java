package ru.vk.education.job.service;

import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.app.port.VacancyStorage;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.domain.Vacancy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatService {
    private final UserStorage userStorage;
    private final VacancyStorage vacancyStorage;

    public StatService(UserStorage userStorage, VacancyStorage vacancyStorage) {
        this.userStorage = userStorage;
        this.vacancyStorage = vacancyStorage;
    }

    public List<Vacancy> getVacanciesByExp(int exp) {
        return vacancyStorage.getAll().stream()
                .filter(v -> v.experience().value() >= exp)
                .sorted(Comparator.comparing(Vacancy::title))
                .toList();
    }

    public List<User> getUsersByMatch(int match) {
        List<Vacancy> vacancies = vacancyStorage.getAll();

        return userStorage.getAll().stream()
                .filter(user -> MatchingService.countMatches(user, vacancies) >= match)
                .sorted(Comparator.comparing(User::name))
                .toList();
    }

    public List<Skill> getUsersByTopSkill(int limit) {
        return userStorage.getAll().stream()
                .flatMap(user -> user.skills().stream())
                .map(Skill::value)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new Skill(e.getKey()))
                .sorted(Comparator.comparing(Skill::value))
                .toList();
    }
}
