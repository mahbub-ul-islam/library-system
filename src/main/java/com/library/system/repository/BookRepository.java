package com.library.system.repository;

import com.library.system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByIsbn(String isbn);
}
