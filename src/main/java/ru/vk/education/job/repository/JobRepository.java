package ru.vk.education.job.repository;

import org.springframework.stereotype.Repository;
import ru.vk.education.job.app.port.VacancyStorage;
import ru.vk.education.job.domain.Vacancy;
import ru.vk.education.job.infra.InMemoryVacancyStorage;

import java.util.List;

@Repository
public class JobRepository {

    private final VacancyStorage vacancies = InMemoryVacancyStorage.INSTANCE;

    public void create(Vacancy vacancy) {
        vacancies.save(vacancy);
    }

    public List<Vacancy> getAll() {
        return vacancies.getAll();
    }
}
