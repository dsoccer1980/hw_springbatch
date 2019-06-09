package ru.dsoccer1980.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.dsoccer1980.domain.Comment;

import java.util.List;


public interface CommentRepository extends MongoRepository<Comment, String> {

    List<Comment> findByBookId(String bookId);

    void deleteByBookId(String id);

}
