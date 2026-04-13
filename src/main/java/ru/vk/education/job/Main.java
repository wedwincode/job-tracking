package ru.vk.education.job;

import ru.vk.education.job.app.CliApp;
import ru.vk.education.job.app.port.CommandHistory;
import ru.vk.education.job.infra.FileCommandHistory;
import ru.vk.education.job.infra.InMemoryUserStorage;
import ru.vk.education.job.infra.InMemoryVacancyStorage;
import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.app.port.VacancyStorage;
import ru.vk.education.job.service.MatchingService;
import ru.vk.education.job.service.StatService;

public class Main {
    public static void main(String[] args) {
        UserStorage userStorage = InMemoryUserStorage.INSTANCE;
        VacancyStorage vacancyStorage = InMemoryVacancyStorage.INSTANCE;
        CommandHistory commandHistory = new FileCommandHistory("history.log");
        MatchingService matchingService = new MatchingService(vacancyStorage);
        StatService statService = new StatService(userStorage, vacancyStorage, matchingService);

        new CliApp(userStorage, vacancyStorage, commandHistory, matchingService, statService).run();
    }
}