package ru.dsoccer1980.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.dsoccer1980.domain.Author;

import java.util.Optional;


public interface AuthorRepository extends MongoRepository<Author, String> {

    Optional<Author> findByName(String name);

}
