package com.example.queryoptimization.config;

import com.example.queryoptimization.common.entity.Article;
import com.example.queryoptimization.common.entity.User;
import com.example.queryoptimization.common.repository.ArticleRepository;
import com.example.queryoptimization.common.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DataLoadingService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    public DataLoadingService(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public void loadData() {
        System.out.println("--- DataLoadingService: Starting data loading ---");
        final int DATA_COUNT = 10000;

        if (userRepository.count() > 0) {
            System.out.println("--- DataLoadingService: Data already exists. Skipping data loading. ---");
            return;
        }

        // User data initialization
        List<User> users = new ArrayList<>();
        for (int i = 0; i < DATA_COUNT; i++) {
            String firstName = "FirstName" + ThreadLocalRandom.current().nextInt(1000);
            String lastName = "LastName" + ThreadLocalRandom.current().nextInt(1000);
            User user = new User(firstName, lastName);
            user.setCreatedAt(LocalDateTime.now().minusSeconds(ThreadLocalRandom.current().nextInt(3600 * 24 * 30)));
            users.add(user);
        }
        userRepository.saveAll(users);

        // Article data initialization
        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < DATA_COUNT; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("start_keyword ");
            for (int j = 0; j < 10; j++) {
                sb.append(UUID.randomUUID().toString()).append(" ");
            }
            if (ThreadLocalRandom.current().nextBoolean()) {
                sb.append("middle_keyword ");
            }
            for (int j = 0; j < 10; j++) {
                sb.append(UUID.randomUUID().toString()).append(" ");
            }
            sb.append("end_keyword");
            String randomContent = sb.toString();
            articles.add(new Article("Article " + i, randomContent));
        }
        articleRepository.saveAll(articles);

        long userCount = userRepository.count();
        long articleCount = articleRepository.count();
        System.out.println("--- DataLoadingService: Data loading finished. Users = " + userCount + ", Articles = " + articleCount + " ---");
    }
}
