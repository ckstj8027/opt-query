package com.example.queryoptimization.pagination;

import com.example.queryoptimization.common.entity.User;
import com.example.queryoptimization.common.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaginationService {

    private final UserRepository userRepository;

    public PaginationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long runUnoptimizedQuery() {
        long startTime = System.nanoTime();
        // Fetching a page far into the dataset using OFFSET.
        // This forces the DB to scan and discard 9,990 rows.
        userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(9990, 10));
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    public long runOptimizedQuery() {
        // To simulate a real-world scenario, we first need the 'cursor' from the previous page.
        // Here, we fetch the first user to get their createdAt and id, or handle empty list.
        List<User> firstUserPage = userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 1));
        if (firstUserPage.isEmpty()) {
            // If no users are found, return 0 or throw a more specific exception if needed.
            // For now, returning 0 to prevent IndexOutOfBoundsException.
            return 0L;
        }
        User cursorUser = firstUserPage.get(0);
        LocalDateTime cursorCreatedAt = cursorUser.getCreatedAt();
        Long cursorId = cursorUser.getId();

        long startTime = System.nanoTime();
        // Now, we fetch the next 10 users using the cursor (Seek Method).
        userRepository.findByCreatedAtBeforeOrCreatedAtEqualsAndIdLessThanOrderByCreatedAtDescIdDesc(cursorCreatedAt, cursorId, PageRequest.of(0, 10));
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }
}
