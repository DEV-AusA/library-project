package com.devausa.library_project.repository;

import com.devausa.library_project.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByAuthorName(String authorName);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.book")
    List<Author> findAllWithBooks();

    @Query("SELECT a FROM Author a WHERE LOWER(a.authorName) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Author> searchAuthorByName(@Param("authorName") String authorName);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.book WHERE a.birthYear <= :year AND (a.deathYear >= :year OR a.deathYear IS NULL)")
    List<Author> findByBirthYearLessThanOrDeathYearGreaterThanOrDeathYearNull(@Param("year") int year);
}
