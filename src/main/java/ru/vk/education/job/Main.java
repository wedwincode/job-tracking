package ru.vk.education.job;

import ru.vk.education.job.app.CliApp;
import ru.vk.education.job.infra.InMemoryUserStorage;
import ru.vk.education.job.infra.InMemoryVacancyStorage;
import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.app.port.VacancyStorage;

public class Main {
    public static void main(String[] args) {
        UserStorage userStorage = InMemoryUserStorage.INSTANCE;
        VacancyStorage vacancyStorage = InMemoryVacancyStorage.INSTANCE;

        new CliApp(userStorage, vacancyStorage).run();
    }
}