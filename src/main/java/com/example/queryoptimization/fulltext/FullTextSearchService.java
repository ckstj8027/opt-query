package com.example.queryoptimization.fulltext;

import com.example.queryoptimization.common.repository.ArticleRepository;
import org.springframework.stereotype.Service;

@Service
public class FullTextSearchService {

    private final ArticleRepository articleRepository;

    public FullTextSearchService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public long runUnoptimizedQuery() {
        long startTime = System.nanoTime();
        articleRepository.findByContentContaining("middle_keyword");
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }

    public long runOptimizedQuery() {
        long startTime = System.nanoTime();
        articleRepository.findByContentWithFullText("+middle_keyword");
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }
}
