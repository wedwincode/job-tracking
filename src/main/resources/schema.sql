CREATE TABLE IF NOT EXISTS users
(
    id         INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    experience INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS skills
(
    id    INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    skill VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS jobs
(
    id           INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title        VARCHAR(100) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    experience   INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS user_skills
(
    user_id  INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (skill_id) REFERENCES skills (id)
);

CREATE TABLE IF NOT EXISTS job_skills
(
    job_id   INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (job_id, skill_id),
    FOREIGN KEY (job_id) REFERENCES jobs (id),
    FOREIGN KEY (skill_id) REFERENCES skills (id)
);
