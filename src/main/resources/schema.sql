CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE CHECK(name <> '')
);

CREATE TABLE IF NOT EXISTS mpas (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE CHECK(name <> '')
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER,
    genre BIGINT REFERENCES genres(id),
    mpa BIGINT REFERENCES mpas(id)
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT REFERENCES films(id),
    genre_id BIGINT REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR,
    login VARCHAR,
    name VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT REFERENCES films(id),
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS friends (
    user_id BIGINT REFERENCES users(id),
    friend_id BIGINT REFERENCES users(id),
    CHECK(user_id <> friend_id)
);
