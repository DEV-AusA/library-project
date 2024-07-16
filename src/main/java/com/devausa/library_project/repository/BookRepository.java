package com.devausa.library_project.repository;

import com.devausa.library_project.model.Book;
import com.devausa.library_project.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN FETCH b.languages languages JOIN FETCH b.authors WHERE languages = :language")
    List<Book> findByLanguage(@Param("language") Language language);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.languages WHERE b.id = :id")
    Optional<Book> findByIdWithAuthorsAndLanguages(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.authors LEFT JOIN FETCH b.languages")
    List<Book> findAllWithAuthorsAndLanguages();
}

