ALTER TABLE ai_response
    ADD COLUMN user_vacancy_id BIGINT NOT NULL;

ALTER TABLE ai_response
    ADD CONSTRAINT uk_ai_response_user_vacancies UNIQUE (user_vacancy_id);

ALTER TABLE ai_response
    ADD CONSTRAINT fk_ai_response_user_vacancies
        FOREIGN KEY (user_vacancy_id)
        REFERENCES user_vacancies(id)
        ON DELETE CASCADE;