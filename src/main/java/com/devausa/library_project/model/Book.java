package com.devausa.library_project.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Author> authors;

    private Double downloadCount;

    @ElementCollection(targetClass = Language.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "languages", joinColumns = @JoinColumn(name = "book_id"))
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "language")
    private Set<Language> languages;

    // Default constructor
    public Book() {}

    public Book(String title, Set<Author> authors, Double downloadCount, Set<Language> languages) {
        this.title = title;
        this.authors = authors;
        this.downloadCount = downloadCount;
        this.languages = languages;
    }

    @Override
    public String toString() {
        return
                "******** Libro *******\n" +
                "Titulo: " + title + "\n" +
                "Autor: " + authors.stream()
                        .map(Author::getAuthorName)
                        .collect(Collectors.joining(", ")) + "\n" +
                "Idiomas: " + languages + "\n" +
                "Cantidad de descargas: " + downloadCount + "\n";
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public Double getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Double downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }
}
