package ru.dsoccer1980.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dsoccer1980.domain.jpa.JpaAuthor;

public interface JpaAuthorRepository extends JpaRepository<JpaAuthor, Long> {


}
