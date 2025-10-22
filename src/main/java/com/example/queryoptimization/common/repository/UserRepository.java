package com.example.queryoptimization.common.repository;

import com.example.queryoptimization.common.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // For Multiple-Column Index (Unoptimized)
    @Query("SELECT u FROM User u WHERE CONCAT(u.lastName, u.firstName) = :fullName")
    List<User> findByFullNameUnoptimized(@Param("fullName") String fullName);

    // For Multiple-Column Index (Optimized)
    @Query("SELECT u FROM User u WHERE u.lastName = :lastName AND u.firstName = :firstName")
    List<User> findByLastNameAndFirstName(@Param("lastName") String lastName, @Param("firstName") String firstName);

    // For Covering Index (Unoptimized)
    @Query("SELECT u FROM User u WHERE u.firstName = :firstName")
    List<User> findUsersByFirstName(@Param("firstName") String firstName);

    // For Covering Index (Optimized)
    @Query("SELECT u.lastName FROM User u WHERE u.firstName = :firstName")
    List<String> findLastNameByFirstNameForCovering(@Param("firstName") String firstName);

    // --- For Pagination ---
    // Unoptimized: Standard offset-based pagination.
    List<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Optimized: Seek method (keyset pagination) using a composite condition.
    // Finds users created before the cursor, or created at the same time but with a smaller ID.
    @Query("SELECT u FROM User u WHERE u.createdAt < :cursorCreatedAt OR (u.createdAt = :cursorCreatedAt AND u.id < :cursorId) ORDER BY u.createdAt DESC, u.id DESC")
    List<User> findByCreatedAtBeforeOrCreatedAtEqualsAndIdLessThanOrderByCreatedAtDescIdDesc(
        @Param("cursorCreatedAt") LocalDateTime cursorCreatedAt, 
        @Param("cursorId") Long cursorId, 
        Pageable pageable
    );
}
