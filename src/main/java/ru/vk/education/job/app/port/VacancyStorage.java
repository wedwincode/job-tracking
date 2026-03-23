package ru.vk.education.job.app.port;

import ru.vk.education.job.domain.Vacancy;

import java.util.List;

public interface VacancyStorage {
    void save(Vacancy vacancy);
    List<Vacancy> getAll();
}
