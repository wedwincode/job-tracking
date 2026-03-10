package ru.vk.education.job.infra;

import ru.vk.education.job.domain.Vacancy;
import ru.vk.education.job.port.VacancyStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public enum InMemoryVacancyStorage implements VacancyStorage {

    INSTANCE;

    private final Map<String, Vacancy> vacanciesByTitle;

    InMemoryVacancyStorage() {
        this.vacanciesByTitle = new LinkedHashMap<>();
    }

    @Override
    public void save(Vacancy vacancy) {
        vacanciesByTitle.putIfAbsent(vacancy.title(), vacancy);
    }

    @Override
    public List<Vacancy> getAll() {
        return new ArrayList<>(vacanciesByTitle.values());
    }
}
