package ru.dsoccer1980.domain.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "author")
public class JpaAuthor {
    @Id
    private Long id;

    private String name;

    public JpaAuthor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" + id + ", '" + name + '\'' + '}';
    }
}
