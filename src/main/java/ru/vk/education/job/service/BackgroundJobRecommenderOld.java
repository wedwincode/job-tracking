package ru.vk.education.job.service;

import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.domain.Vacancy;

import java.util.Map;

@Deprecated
public class BackgroundJobRecommenderOld implements Runnable {
    private final UserStorage userStorage;
    private final MatchingService matchingService;

    public BackgroundJobRecommenderOld(UserStorage userStorage, MatchingService matchingService) {
        this.userStorage = userStorage;
        this.matchingService = matchingService;
    }

    @Override
    public void run() {
        Map<User, Vacancy> userVacancyMap = matchingService.getSuggestionsForUsers(userStorage.getAll());
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<User, Vacancy> entry: userVacancyMap.entrySet()) {
            User u = entry.getKey();
            Vacancy v = entry.getValue();
            sb.append(u.name());

            if (v != null) {
                sb.append(", лучшее предложение - ")
                        .append(v.title())
                        .append(" at ")
                        .append(v.company().name());
            } else {
                sb.append(", подходящих вакансий пока нет");
            }
            sb.append(System.lineSeparator());
        }
        System.out.print(sb);
    }
}