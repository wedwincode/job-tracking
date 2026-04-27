package ru.vk.education.job.service;

import org.springframework.stereotype.Service;
import ru.vk.education.job.domain.Experience;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.dto.CreateUserRequestDto;
import ru.vk.education.job.dto.UserResponseDto;
import ru.vk.education.job.exception.ResourceNotFoundException;
import ru.vk.education.job.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void create(CreateUserRequestDto dto) {
        List<Skill> skills = dto.skills().stream().map(Skill::new).toList();
        userRepository.create(
                new User(
                        dto.name(),
                        skills,
                        new Experience(dto.experience())
                )
        );
    }

    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.getAll();

        return users.stream()
                .map(u -> new UserResponseDto(
                        u.name(),
                        u.skills().stream().map(Skill::value).toList(),
                        u.experience().value()
                ))
                .toList();
    }

    public UserResponseDto getByName(String name) {
        User user = userRepository.getByName(name);
        if (user == null) {
            throw new ResourceNotFoundException();
        }

        List<String> skills = user.skills().stream().map(Skill::value).toList();

        return new UserResponseDto(
                user.name(),
                skills,
                user.experience().value()
        );
    }
}
