package com.example.queryoptimization.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SchemaInitializationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션으로 분리하여 즉시 커밋
    public void createFullTextIndex() {
        System.out.println("--- SchemaInitializationService: Attempting to create FULLTEXT index in a new transaction ---");
        
        // getResultList()를 사용하여 예외 발생 방지
        List<?> results = entityManager.createNativeQuery("SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS WHERE table_schema = DATABASE() AND table_name = 'articles' AND index_name = 'ft_content'", List.class).getResultList();
        
        if (results.isEmpty()) {
            // 인덱스가 존재하지 않을 때만 생성
            entityManager.createNativeQuery("ALTER TABLE articles ADD FULLTEXT INDEX ft_content (content)").executeUpdate();
            System.out.println("--- SchemaInitializationService: FULLTEXT index 'ft_content' created successfully. ---");
        } else {
            System.out.println("--- SchemaInitializationService: FULLTEXT index 'ft_content' already exists. ---");
        }
    }
}
