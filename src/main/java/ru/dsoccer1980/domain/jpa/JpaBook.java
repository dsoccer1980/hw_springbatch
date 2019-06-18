package ru.dsoccer1980.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class JpaBook {
    @Id
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "author_id")
    private JpaAuthor author;

    @OneToOne
    @JoinColumn(name = "genre_id")
    private JpaGenre genre;


    public JpaBook(String name, JpaAuthor author, JpaGenre genre) {
        this.name = name;
        this.author = author;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Book{" +
                id +
                ", '" + name + '\'' +
                ", " + author +
                ", " + genre +
                '}';
    }
}
