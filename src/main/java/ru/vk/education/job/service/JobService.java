package ru.vk.education.job.service;

import org.springframework.stereotype.Service;
import ru.vk.education.job.domain.Company;
import ru.vk.education.job.domain.Experience;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.Vacancy;
import ru.vk.education.job.dto.CreateJobRequestDto;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.repository.JobRepository;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void create(CreateJobRequestDto dto) {
        List<Skill> skills = dto.tags().stream().map(Skill::new).toList();
        jobRepository.create(
                new Vacancy(
                        dto.title(),
                        new Company(dto.company()),
                        skills,
                        new Experience(dto.experience())
                )
        );
    }

    public List<JobResponseDto> getAll() {
        List<Vacancy> vacancies = jobRepository.getAll();

        return vacancies.stream()
                .map(v -> new JobResponseDto(
                        v.title(),
                        v.company().name(),
                        v.tags().stream().map(Skill::value).toList(),
                        v.experience().value()
                ))
                .toList();
    }
}
