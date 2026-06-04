CREATE TABLE attendance_rules (
    id BIGSERIAL PRIMARY KEY,
    average_wait_time INTEGER NOT NULL,
    accepts_emergency BOOLEAN NOT NULL,
    notes VARCHAR(500) NOT NULL,
    specialty_id BIGINT NOT NULL,
    CONSTRAINT fk_attendance_rules_specialty
        FOREIGN KEY (specialty_id) REFERENCES specialties (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_attendance_rules_specialty_id ON attendance_rules (specialty_id);
