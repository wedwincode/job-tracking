package ru.vk.education.job.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vk.education.job.dto.CreateUserRequestDto;
import ru.vk.education.job.dto.UserResponseDto;
import ru.vk.education.job.service.UserService;

@RestController()
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping()
    public void create(@RequestBody CreateUserRequestDto dto) {
        userService.create(dto);
    }

    @GetMapping("/{name}")
    public UserResponseDto getByName(@PathVariable String name) {
        return userService.getByName(name);
    }
}
