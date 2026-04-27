package ru.vk.education.job.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.dto.UserResponseDto;
import ru.vk.education.job.service.StatService;

import java.util.List;

@RestController
@RequestMapping("/stat")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/experience/{experience}")
    public List<JobResponseDto> getVacanciesByExperience(@PathVariable int experience) {
        return statService.getVacanciesByExp(experience);
    }

    @GetMapping("/match/{match}")
    public List<UserResponseDto> getUsersByMatch(@PathVariable int match) {
        return statService.getUsersByMatch(match);
    }

    @GetMapping("/skills/{limit}")
    public List<String> getTopSkill(@PathVariable int limit) {
        return statService.getTopSkills(limit);
    }
}
