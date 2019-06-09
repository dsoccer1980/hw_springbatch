package ru.dsoccer1980.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.dsoccer1980.domain.Book;

import java.util.List;


public interface BookRepository extends MongoRepository<Book, String> {

    List<Book> findByAuthorId(String id);

    List<Book> findByGenreId(String id);
}
