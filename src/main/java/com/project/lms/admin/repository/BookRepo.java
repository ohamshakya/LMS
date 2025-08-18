package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Book;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book,Integer> {

    Book findByAuthor(@Param("authorName") String authorName);

    Book findByIsbn(String isbn);


    @Query("SELECT b FROM Book b " +
            "WHERE (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) " +
            "AND (:publisher IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))) " +
            "AND (:genre IS NULL OR LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%')))")
    List<Book> searchBooks(@Param("author") String author,
                           @Param("publisher") String publisher,
                           @Param("genre") String genre);

    @Query("SELECT COUNT(b) FROM Book b")
    Integer countBooks();

    @Query("SELECT COUNT(b) FROM Book b where b.isAvailable = TRUE")
    Integer availableBooks();

    @Query("SELECT b FROM Book b WHERE b.isAvailable = TRUE")
    List<Book> availableBook();

    @Query("SELECT r.book.id FROM Rating r GROUP BY r.book.id ORDER BY AVG(r.rating) DESC")
    List<Integer> findTopRatedBookIds(Pageable pageable);



}
