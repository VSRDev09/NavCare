INSERT INTO admins (username, password, role)
VALUES (
    'admin',
    '$2a$10$E0opaMJUYT6LFJRrISUy9uJjUCIUSmsfH/TtGFq0oNT8hfkqYjiwq',
    'ROLE_ADMIN'
)
ON CONFLICT (username) DO NOTHING;
