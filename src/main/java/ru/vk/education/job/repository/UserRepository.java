package ru.vk.education.job.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.vk.education.job.domain.Experience;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void create(User user) {
        Integer userId = jdbcTemplate.queryForObject(
                """
                INSERT INTO users (name, experience)
                VALUES (?, ?)
                RETURNING id
                """,
                Integer.class,
                user.name(),
                user.experience().value()
        );

        if (userId == null) {
            throw new IllegalStateException("User id was not returned after insert");
        }

        List<Integer> skillIds = user.skills().stream()
                .map(skill -> getOrCreateSkillId(skill.value()))
                .toList();

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO user_skills (user_id, skill_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setInt(2, skillIds.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return skillIds.size();
                    }
                }
        );
    }

    public User getByName(String name) {
        return jdbcTemplate.query(
                """
                SELECT u.id, u.name, u.experience, s.skill
                FROM users u
                LEFT JOIN user_skills us ON u.id = us.user_id
                LEFT JOIN skills s ON us.skill_id = s.id
                WHERE u.name = ?
                ORDER BY s.skill
                """,
                rs -> {
                    String userName = null;
                    Integer experience = null;
                    List<Skill> skills = new ArrayList<>();

                    while (rs.next()) {
                        if (userName == null) {
                            userName = rs.getString("name");
                            experience = rs.getInt("experience");
                        }

                        String skill = rs.getString("skill");

                        if (skill != null) {
                            skills.add(new Skill(skill));
                        }
                    }

                    if (userName == null) {
                        return null;
                    }

                    return new User(
                            userName,
                            skills,
                            new Experience(experience)
                    );
                },
                name
        );
    }

    public List<User> getAll() {
        return jdbcTemplate.query(
                """
                SELECT u.id, u.name, u.experience, s.skill
                FROM users u
                LEFT JOIN user_skills us ON u.id = us.user_id
                LEFT JOIN skills s ON us.skill_id = s.id
                ORDER BY u.id, s.skill
                """,
                rs -> {
                    List<User> users = new ArrayList<>();

                    Integer currentUserId = null;
                    String name = null;
                    int experience = 0;
                    List<Skill> skills = new ArrayList<>();

                    while (rs.next()) {
                        int userId = rs.getInt("id");

                        if (currentUserId != null && !currentUserId.equals(userId)) {
                            users.add(new User(
                                    name,
                                    skills,
                                    new Experience(experience)
                            ));

                            skills = new ArrayList<>();
                        }

                        if (currentUserId == null || !currentUserId.equals(userId)) {
                            currentUserId = userId;
                            name = rs.getString("name");
                            experience = rs.getInt("experience");
                        }

                        String skill = rs.getString("skill");

                        if (skill != null) {
                            skills.add(new Skill(skill));
                        }
                    }

                    if (currentUserId != null) {
                        users.add(new User(
                                name,
                                skills,
                                new Experience(experience)
                        ));
                    }

                    return users;
                }
        );
    }

    private Integer getOrCreateSkillId(String skillName) {
        return jdbcTemplate.queryForObject(
                """
                INSERT INTO skills (skill)
                VALUES (?)
                ON CONFLICT (skill) DO UPDATE SET skill = EXCLUDED.skill
                RETURNING id
                """,
                Integer.class,
                skillName
        );
    }
}
