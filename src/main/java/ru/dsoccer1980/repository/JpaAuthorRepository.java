package ru.dsoccer1980.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dsoccer1980.domain.Author;
import ru.dsoccer1980.domain.jpa.JpaAuthor;

import java.util.Optional;

public interface JpaAuthorRepository extends JpaRepository<JpaAuthor, Long> {

    Optional<Author> findByName(String name);

}
