-- ============================
-- TABELA: users
-- ============================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- ============================
-- TABELA: vacancy
-- ============================
CREATE TABLE vacancy (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    applications INT DEFAULT 0,
    approvals INT DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================
-- ITENS ESSENCIAIS (ElementCollection)
-- ============================
CREATE TABLE vacancy_essential (
    vacancy_id BIGINT NOT NULL,
    essential_item TEXT,
    CONSTRAINT fk_vacancy_essential_vacancy
        FOREIGN KEY (vacancy_id) REFERENCES vacancy(id)
        ON DELETE CASCADE
);

-- ============================
-- ITENS DIFERENCIAIS (ElementCollection)
-- ============================
CREATE TABLE vacancy_differential (
    vacancy_id BIGINT NOT NULL,
    differential_item TEXT,
    CONSTRAINT fk_vacancy_differential_vacancy
        FOREIGN KEY (vacancy_id) REFERENCES vacancy(id)
        ON DELETE CASCADE
);

-- ============================
-- TABELA DE RELAÇÃO: user_vacancies
-- ============================
CREATE TABLE user_vacancies (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vacancy_id BIGINT NOT NULL,
    role VARCHAR(40) NOT NULL,
    status VARCHAR(40) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_vacancies_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_vacancies_vacancy
        FOREIGN KEY (vacancy_id) REFERENCES vacancy(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_user_vacancy UNIQUE (user_id, vacancy_id)
);
