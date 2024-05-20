package com.library.system.repository;

import com.library.system.entity.Book;
import com.library.system.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by mahbub.islam on 18/5/2024.
 */
@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, String> {

    Optional<Borrower> findByEmail(String email);
}


