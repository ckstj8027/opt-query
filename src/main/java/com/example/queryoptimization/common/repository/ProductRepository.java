package com.example.queryoptimization.common.repository;

import com.example.queryoptimization.common.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
