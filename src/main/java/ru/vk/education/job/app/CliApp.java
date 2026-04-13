package ru.vk.education.job.app;

import ru.vk.education.job.app.port.CommandHistory;
import ru.vk.education.job.domain.Company;
import ru.vk.education.job.domain.Experience;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;
import ru.vk.education.job.domain.Vacancy;
import ru.vk.education.job.app.port.UserStorage;
import ru.vk.education.job.app.port.VacancyStorage;
import ru.vk.education.job.service.BackgroundJobRecommender;
import ru.vk.education.job.service.MatchingService;
import ru.vk.education.job.service.StatService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CliApp {
    private static final long INITIAL_DELAY = 10;
    private static final long SCHEDULING_PERIOD = 60;

    private static final String USER_PREFIX = "user ";
    private static final String JOB_PREFIX = "job ";

    private final UserStorage userStorage;
    private final VacancyStorage vacancyStorage;
    private final CommandHistory commandHistory;
    private final MatchingService matchingService;
    private final StatService statService;
    private final ScheduledExecutorService executor;

    public CliApp(
            UserStorage userStorage,
            VacancyStorage vacancyStorage,
            CommandHistory commandHistory,
            MatchingService matchingService,
            StatService statService
    ) {
        this.userStorage = userStorage;
        this.vacancyStorage = vacancyStorage;
        this.commandHistory = commandHistory;
        this.matchingService = matchingService;
        this.statService = statService;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void run() {
        restoreState();
        BackgroundJobRecommender jobRecommender = new BackgroundJobRecommender(userStorage, matchingService);
        executor.scheduleAtFixedRate(
                jobRecommender,
                INITIAL_DELAY,
                SCHEDULING_PERIOD,
                TimeUnit.SECONDS
        );
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String raw = scanner.nextLine();
            boolean result = processUserCommand(raw);
            if (!result) {
                stopExecutor();
                System.exit(0);
            }
        }
    }

    private void stopExecutor() {
        executor.shutdown();

        try {
            boolean isStopped = executor.awaitTermination(5, TimeUnit.SECONDS);
            if (!isStopped) {
                System.err.println("Executor did not terminate within timeout, forcing shutdown...");
                executor.shutdownNow();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate after forced shutdown");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate after forced shutdown");
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void restoreState() {
        List<String> commands = commandHistory.getAll();
        for (String command : commands) {
            if (isRestoreCommand(command)) {
                applyRestoreCommand(command);
            }
        }
    }

    private boolean isRestoreCommand(String raw) {
        return raw.startsWith(USER_PREFIX) || raw.startsWith(JOB_PREFIX);
    }

    private void applyRestoreCommand(String raw) {
        if (raw.startsWith(USER_PREFIX)) {
            User user = parseUser(raw);
            userStorage.save(user);
        } else if (raw.startsWith(JOB_PREFIX)) {
            Vacancy vacancy = parseVacancy(raw);
            vacancyStorage.save(vacancy);
        }
    }

    private boolean processUserCommand(String raw) {
        if (raw.equals("exit")) {
            return false;
        }

        if (raw.equals("history")) {
            printHistory();
            commandHistory.save(raw);
            return true;
        }

        if (raw.equals("user-list")) {
            printUsers();
            commandHistory.save(raw);
            return true;
        }

        if (raw.equals("job-list")) {
            printVacancies();
            commandHistory.save(raw);
            return true;
        }

        if (raw.startsWith(USER_PREFIX)) {
            User user = parseUser(raw);
            userStorage.save(user);
            commandHistory.save(raw);
            return true;
        }

        if (raw.startsWith(JOB_PREFIX)) {
            Vacancy vacancy = parseVacancy(raw);
            vacancyStorage.save(vacancy);
            commandHistory.save(raw);
            return true;
        }

        if (raw.startsWith("suggest ")) {
            String username = parseUsername(raw);
            User user = userStorage.getByName(username);
            if (user != null) {
                List<Vacancy> suggestions = matchingService.getSuggestions(user);
                printVacancies(suggestions);
            }
            commandHistory.save(raw);
            return true;
        }

        if (raw.startsWith("stat ")) {
            if (raw.startsWith("stat --exp")) {
                List<Vacancy> vacancies = statService.getVacanciesByExp(parseStatParam(raw, "exp"));
                printVacancies(vacancies);
            } else if (raw.startsWith("stat --match")) {
                List<User> users = statService.getUsersByMatch(parseStatParam(raw, "match"));
                printUsers(users);
            } else if (raw.startsWith("stat --top-skills")) {
                List<Skill> skills = statService.getUsersByTopSkill(parseStatParam(raw, "top-skills"));
                printSkills(skills);
            } else {
                throw new IllegalArgumentException("stat syntax is invalid");
            }
            commandHistory.save(raw);
        }

        return true;
    }

    private void printHistory() {
        commandHistory.getAll().forEach(System.out::println);
    }

    private static User parseUser(String raw) {
        String name = null;
        List<Skill> skills = new ArrayList<>();
        Experience experience = null;
        for (String arg: split(raw, "user")) {
            arg = arg.trim();

            if (arg.startsWith("--skills")) {
                String[] rawSkills = arg.replace("--skills=", "").split(",");
                skills = Arrays.stream(rawSkills)
                        .map(String::trim)
                        .map(Skill::new)
                        .toList();
            } else if (arg.startsWith("--exp")) {
                String rawExperience = arg.replace("--exp=", "");
                experience = new Experience(Integer.parseInt(rawExperience));
            } else {
                name = arg;
            }
        }

        return new User(name, skills, experience);
    }

    private static Vacancy parseVacancy(String raw) {
        String title = null;
        Company company = null;
        List<Skill> tags = new ArrayList<>();
        Experience experience = null;

        for (String arg: split(raw, "job")) {
            arg = arg.trim();

            if (arg.startsWith("--company")) {
                String rawCompany = arg.replace("--company=", "");
                company = new Company(rawCompany);
            } else if (arg.startsWith("--tags")) {
                String[] rawTags = arg.replace("--tags=", "").split(",");
                tags = Arrays.stream(rawTags)
                        .map(String::trim)
                        .map(Skill::new)
                        .toList();
            } else if (arg.startsWith("--exp")) {
                String rawExperience = arg.replace("--exp=", "");
                experience = new Experience(Integer.parseInt(rawExperience));
            } else {
                title = arg;
            }
        }

        return new Vacancy(title, company, tags, experience);
    }

    private static String parseUsername(String raw) {
        String username = raw.substring("suggest".length()).trim();
        if (username.isBlank()) {
            throw new IllegalArgumentException("suggest syntax is invalid");
        }
        return username;
    }

    private static int parseStatParam(String raw, String param) {
        String value = raw.substring(("stat --" + param).length()).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("stat syntax is invalid");
        }
        return Integer.parseInt(value);
    }

    private static String[] split(String raw, String prefix) {
        return raw.replace(prefix, "").strip().split("\\s+");
    }

    private void printUsers() {
        printUsers(userStorage.getAll());
    }

    private static void printUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User user: users) {
            sb.append(user.name()).append(" ");

            String skills = user.skills().stream()
                    .map(Skill::value)
                    .collect(Collectors.joining(","));
            sb.append(skills);

            sb.append(" ").append(user.experience().value()).append(System.lineSeparator());
        }
        System.out.print(sb);
    }

    private void printVacancies() {
        printVacancies(vacancyStorage.getAll());
    }

    private static void printVacancies(List<Vacancy> vacancies) {
        StringBuilder sb = new StringBuilder();
        for (Vacancy vacancy: vacancies) {
            sb.append(vacancy.title())
                    .append(" at ")
                    .append(vacancy.company().name())
                    .append(System.lineSeparator());
        }
        System.out.print(sb);
    }

    private void printSkills(List<Skill> skills) {
        for (Skill skill: skills) {
            System.out.println(skill.value());
        }
    }
}
