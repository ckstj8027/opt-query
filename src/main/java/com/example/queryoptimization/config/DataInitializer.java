package com.example.queryoptimization.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SchemaInitializationService schemaInitializationService;
    private final DataLoadingService dataLoadingService;

    public DataInitializer(SchemaInitializationService schemaInitializationService, DataLoadingService dataLoadingService) {
        this.schemaInitializationService = schemaInitializationService;
        this.dataLoadingService = dataLoadingService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- DataInitializer: Orchestrating initialization process ---");

        // 1. 스키마 관련 작업 (FULLTEXT 인덱스)을 별도 트랜잭션으로 먼저 실행
        schemaInitializationService.createFullTextIndex();

        // 2. 데이터 로딩을 별도 트랜잭션으로 실행
        dataLoadingService.loadData();

        System.out.println("--- DataInitializer: Initialization process finished. ---");
    }
}
