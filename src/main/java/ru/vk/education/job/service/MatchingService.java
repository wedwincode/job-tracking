package ru.vk.education.job.service;

import ru.vk.education.job.domain.Match;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.domain.Vacancy;
import ru.vk.education.job.port.VacancyStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MatchingService {

    public static List<Vacancy> getSuggestions(User user, List<Vacancy> vacancies) {
        return vacancies.stream()
                .map(vacancy -> new Match(user, vacancy, calculateScore(user, vacancy)))
                .filter(match -> match.score() > 0)
                .sorted(Comparator.comparingDouble(Match::score).reversed())
                .limit(2)
                .map(Match::vacancy)
                .toList();
    }

    private static double calculateScore(User user, Vacancy vacancy) {
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
