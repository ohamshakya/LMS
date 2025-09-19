package com.project.lms.admin.repository;

import com.project.lms.admin.entity.Book;
import org.springframework.data.domain.Page;
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


    @Query("""
        SELECT b FROM Book b
        WHERE (
            :query IS NULL
            OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """)
    Page<Book> searchByMultipleFields(@Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Book b")
    Integer countBooks();

    @Query("SELECT COUNT(b) FROM Book b where b.isAvailable = TRUE")
    Integer availableBooks();

    @Query("SELECT b FROM Book b WHERE b.isAvailable = TRUE")
    List<Book> availableBook();

    @Query("""
    SELECT r.book FROM Rating r
    GROUP BY r.book.id
    ORDER BY AVG(r.rating) DESC
""")
   Page<Book> findTopRatedBooks(Pageable pageable);

    Page<Book> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("""
    SELECT b FROM Book b
    LEFT JOIN Borrow br ON br.book.id = b.id
    GROUP BY b.id
    ORDER BY COUNT(br.id) DESC
""")
    Page<Book> findMostBorrowedBooks(Pageable pageable);





}
