package com.example.queryoptimization.common.repository;

import com.example.queryoptimization.common.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Unoptimized: LIKE with leading wildcard cannot use a B-Tree index, resulting in a full table scan.
    @Query("SELECT a FROM Article a WHERE a.content LIKE %:keyword%")
    List<Article> findByContentContaining(@Param("keyword") String keyword);

    // Optimized: Using a Full-Text Index with MATCH() AGAINST() for natural language search.
    @Query(value = "SELECT * FROM articles WHERE MATCH(content) AGAINST(:keyword IN BOOLEAN MODE)", nativeQuery = true)
    List<Article> findByContentWithFullText(@Param("keyword") String keyword);
}
