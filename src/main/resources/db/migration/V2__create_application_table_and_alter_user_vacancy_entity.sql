CREATE TABLE application_attachments (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255),
    content_type VARCHAR(100),
    size BIGINT,
    path VARCHAR(500),
    user_vacancy_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_application_user_vacancy
        FOREIGN KEY (user_vacancy_id)
        REFERENCES user_vacancies(id)
        ON DELETE CASCADE
);

