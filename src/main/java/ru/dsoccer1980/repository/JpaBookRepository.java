package ru.dsoccer1980.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dsoccer1980.domain.jpa.JpaBook;

public interface JpaBookRepository extends JpaRepository<JpaBook, Long> {

}
