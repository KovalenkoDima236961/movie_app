DELETE FROM users;
DELETE FROM verification_token;

INSERT INTO users (email, username, password, email_verified)
    VALUES ('UserA@junit.com', 'UserA', '$2a$10$hBn5gu6cGelJNiE6DDsaBOmZgyumCSzVwrOK/37FWgJ6aLIdZSSI2', true)
    , ('UserB@junit.com', 'UserB', '$2a$10$TlYbg57fqOy/1LJjispkjuSIvFJXbh3fy0J9fvHnCpuntZOITAjVG', false)
    , ('UserC@junit.com', 'UserC', '$2a$10$SYiYAIW80gDh39jwSaPyiuKGuhrLi7xTUjocL..NOx/1COWe5P03.', false);


INSERT INTO films (film_id, title, type, poster_path, release_date, vote_average, overview)
VALUES
    (101, 'Movie 101', 'Action', '/poster101.jpg', '2024-01-01', 7.5, 'Overview for movie 101'),
    (102, 'Movie 102', 'Drama', '/poster102.jpg', '2023-12-01', 8.0, 'Overview for movie 102');