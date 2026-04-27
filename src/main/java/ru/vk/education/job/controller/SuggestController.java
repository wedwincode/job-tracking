package ru.vk.education.job.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vk.education.job.dto.JobResponseDto;
import ru.vk.education.job.service.BackgroundJobRecommender;
import ru.vk.education.job.service.SuggestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/suggest")
public class SuggestController {

    private final SuggestService suggestService;
    private final BackgroundJobRecommender recommender;

    public SuggestController(SuggestService suggestService, BackgroundJobRecommender recommender) {
        this.suggestService = suggestService;
        this.recommender = recommender;
    }

    @GetMapping("/user/{userName}")
    public List<JobResponseDto> getSuggestions(@PathVariable String userName) {
        return suggestService.getSuggestions(userName);
    }

    @GetMapping("/last")
    public Map<String, JobResponseDto> getLastSuggestion() {
        return recommender.getLastRecommended();
    }
}
