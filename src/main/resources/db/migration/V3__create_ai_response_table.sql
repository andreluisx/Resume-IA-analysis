CREATE TABLE ai_response (
    id SERIAL PRIMARY KEY,
    application_description TEXT NOT NULL,
    improvements_to_this_vacancy TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    essential_matches TEXT[],
    differential_matches TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
