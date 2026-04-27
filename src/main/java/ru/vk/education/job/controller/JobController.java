package ru.vk.education.job.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vk.education.job.dto.CreateJobRequestDto;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.service.JobService;

import java.util.List;

@RestController()
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping()
    public void create(@RequestBody CreateJobRequestDto dto) {
        jobService.create(dto);
    }

    @GetMapping()
    public List<JobResponseDto> getAll() {
        return jobService.getAll();
    }
}