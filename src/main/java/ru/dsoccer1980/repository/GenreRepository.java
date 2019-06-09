package ru.dsoccer1980.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.dsoccer1980.domain.Genre;

import java.util.Optional;


public interface GenreRepository extends MongoRepository<Genre, String> {

    Optional<Genre> findByName(String name);

}
