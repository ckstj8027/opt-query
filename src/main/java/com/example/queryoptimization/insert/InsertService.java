package com.example.queryoptimization.insert;

import com.example.queryoptimization.common.entity.Product;
import com.example.queryoptimization.common.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class InsertService {

    private final ProductRepository productRepository;
    private static final int INSERT_COUNT = 50000; // 데이터 양을 50,000개로 늘림

    public InsertService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public long runUnoptimizedQuery() {
        productRepository.deleteAllInBatch(); // Clear table for accurate measurement

        List<Product> products = IntStream.range(0, INSERT_COUNT)
                .mapToObj(i -> new Product("Product " + i, ThreadLocalRandom.current().nextDouble(10, 100)))
                .collect(Collectors.toList());

        long startTime = System.nanoTime();
        for (Product product : products) {
            productRepository.save(product); // Single save in a loop
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    @Transactional
    public long runOptimizedQuery() {
        productRepository.deleteAllInBatch(); // Clear table for accurate measurement

        List<Product> products = IntStream.range(0, INSERT_COUNT)
                .mapToObj(i -> new Product("Product " + i, ThreadLocalRandom.current().nextDouble(10, 100)))
                .collect(Collectors.toList());

        long startTime = System.nanoTime();
        productRepository.saveAll(products); // Batch save
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    @Transactional
    public void clearProductsTable() {
        productRepository.deleteAllInBatch();
        System.out.println("--- InsertService: Products table cleared after tests. ---");
    }
}
