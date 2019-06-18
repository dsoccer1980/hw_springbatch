DROP TABLE IF EXISTS Author CASCADE;
DROP TABLE IF EXISTS Genre CASCADE;
DROP TABLE IF EXISTS Book;

CREATE TABLE Author(
                     id BIGINT PRIMARY KEY,
                     name VARCHAR(255)
);
CREATE UNIQUE INDEX author_name ON Author (name);

CREATE TABLE Genre(
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(255)
);
CREATE UNIQUE INDEX genre_name ON Genre (name);

CREATE TABLE Book (
                    id BIGINT PRIMARY KEY,
                    name VARCHAR(255) ,
                    author_id BIGINT,
                    genre_id BIGINT,
                    CONSTRAINT name_author_genre UNIQUE (name, author_id, genre_id),
                    FOREIGN KEY(author_id) REFERENCES Author(id) ON DELETE CASCADE,
                    FOREIGN KEY(genre_id) REFERENCES Genre(id) ON DELETE CASCADE
)