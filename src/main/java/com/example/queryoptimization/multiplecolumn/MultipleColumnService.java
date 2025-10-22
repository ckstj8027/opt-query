package com.example.queryoptimization.multiplecolumn;

import com.example.queryoptimization.common.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MultipleColumnService {

    private final UserRepository userRepository;

    public MultipleColumnService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long runUnoptimizedQuery() {
        long startTime = System.nanoTime();
        // Using a function on indexed columns prevents the database from using the index.
        userRepository.findByFullNameUnoptimized("LastName500FirstName500");
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    public long runOptimizedQuery() {
        long startTime = System.nanoTime();
        // This query can efficiently use the (lastName, firstName) composite index.
        userRepository.findByLastNameAndFirstName("LastName500", "FirstName500");
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }
}
