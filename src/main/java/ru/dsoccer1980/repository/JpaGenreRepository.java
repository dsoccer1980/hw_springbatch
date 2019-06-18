package ru.dsoccer1980.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dsoccer1980.domain.jpa.JpaGenre;

public interface JpaGenreRepository extends JpaRepository<JpaGenre, Long> {


}
