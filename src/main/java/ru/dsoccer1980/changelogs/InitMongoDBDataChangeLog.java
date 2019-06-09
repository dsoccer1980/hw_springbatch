package ru.dsoccer1980.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.dsoccer1980.domain.Author;
import ru.dsoccer1980.domain.Book;
import ru.dsoccer1980.domain.Genre;

@ChangeLog(order = "001")
public class InitMongoDBDataChangeLog {

    private static final Author AUTHOR1 = new Author("1", "Стругацкий");
    private static final Author AUTHOR2 = new Author("2", "Уэллс");
    private static final Author AUTHOR3 = new Author("3", "Пушкин");
    private static final Genre GENRE1 = new Genre("10", "Фантастика");
    private static final Genre GENRE2 = new Genre("11", "Классика");
    private static final Book BOOK1 = new Book("100", "Трудно быть Богом", AUTHOR1, GENRE1);
    private static final Book BOOK2 = new Book("101", "Машина времени", AUTHOR2, GENRE1);
    private static final Book BOOK3 = new Book("102", "Онегин", AUTHOR3, GENRE2);
    private Author springDataAuthor;
    private Genre springDataGenre;
    private Book springDataBook;

    @ChangeSet(order = "000", id = "dropDB", author = "stvort", runAlways = true)
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", id = "initAuthors", author = "stvort", runAlways = true)
    public void initAuthors(MongoTemplate template) {
        springDataAuthor = template.save(AUTHOR1);
        springDataAuthor = template.save(AUTHOR2);
        springDataAuthor = template.save(AUTHOR3);
    }

    @ChangeSet(order = "002", id = "initGenres", author = "stvort", runAlways = true)
    public void initGenres(MongoTemplate template) {
        springDataGenre = template.save(GENRE1);
        springDataGenre = template.save(GENRE2);
    }

    @ChangeSet(order = "003", id = "initBooks", author = "stvort", runAlways = true)
    public void initBooks(MongoTemplate template) {
        springDataBook = template.save(BOOK1);
        springDataBook = template.save(BOOK2);
        springDataBook = template.save(BOOK3);
    }
}