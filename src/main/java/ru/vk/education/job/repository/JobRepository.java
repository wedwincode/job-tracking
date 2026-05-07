package ru.vk.education.job.repository;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.vk.education.job.domain.Company;
import ru.vk.education.job.domain.Experience;
import ru.vk.education.job.domain.Skill;
import ru.vk.education.job.domain.Vacancy;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JobRepository {

    private final JdbcTemplate jdbcTemplate;

    public JobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void create(Vacancy vacancy) {
        Integer jobId = jdbcTemplate.queryForObject(
                """
                INSERT INTO jobs (title, company_name, experience)
                VALUES (?, ?, ?)
                RETURNING id
                """,
                Integer.class,
                vacancy.title(),
                vacancy.company().name(),
                vacancy.experience().value()
        );

        if (jobId == null) {
            throw new IllegalStateException("Job id was not returned after insert");
        }

        List<Integer> skillIds = vacancy.tags().stream()
                .map(skill -> getOrCreateSkillId(skill.value()))
                .toList();

        jdbcTemplate.batchUpdate(
                """
                INSERT INTO job_skills (job_id, skill_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, jobId);
                        ps.setInt(2, skillIds.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return skillIds.size();
                    }
                }
        );
    }

    public List<Vacancy> getAll() {
        return jdbcTemplate.query(
                """
                SELECT j.id, j.title, j.company_name, j.experience, s.skill
                FROM jobs j
                LEFT JOIN job_skills js ON j.id = js.job_id
                LEFT JOIN skills s ON js.skill_id = s.id
                ORDER BY j.id
                """,
                rs -> {
                    Map<Integer, String> titles = new LinkedHashMap<>();
                    Map<Integer, String> companyNames = new LinkedHashMap<>();
                    Map<Integer, Integer> experiences = new LinkedHashMap<>();
                    Map<Integer, List<String>> tags = new LinkedHashMap<>();

                    while (rs.next()) {
                        int jobId = rs.getInt("id");

                        titles.putIfAbsent(jobId, rs.getString("title"));
                        companyNames.putIfAbsent(jobId, rs.getString("company_name"));
                        experiences.putIfAbsent(jobId, rs.getInt("experience"));
                        tags.putIfAbsent(jobId, new ArrayList<>());

                        String skill = rs.getString("skill");

                        if (skill != null) {
                            tags.get(jobId).add(skill);
                        }
                    }

                    return titles.keySet().stream()
                            .map(jobId -> new Vacancy(
                                    titles.get(jobId),
                                    new Company(companyNames.get(jobId)),
                                    tags.get(jobId).stream()
                                            .map(Skill::new)
                                            .toList(),
                                    new Experience(experiences.get(jobId))
                            ))
                            .toList();
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
