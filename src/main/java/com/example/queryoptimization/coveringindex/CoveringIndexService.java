package com.example.queryoptimization.coveringindex;

import com.example.queryoptimization.common.entity.User;
import com.example.queryoptimization.common.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoveringIndexService {

    private final UserRepository userRepository;

    public CoveringIndexService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public long runUnoptimizedQuery() {
        long startTime = System.nanoTime();
        // Goal is to get a list of lastNames, but we fetch the whole User entity.
        List<User> users = userRepository.findUsersByFirstName("FirstName500");
        // The application then has to process this larger-than-necessary data.
        List<String> lastNames = users.stream().map(User::getLastName).collect(Collectors.toList());
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    public long runOptimizedQuery() {
        long startTime = System.nanoTime();
        // This query is "covered" by the `idx_firstname_lastname` index because it only needs `lastName` 
        // and filters by `firstName`, both of which are in the index.
        List<String> lastNames = userRepository.findLastNameByFirstNameForCovering("FirstName500");
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }
}
