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
    private static final int INSERT_COUNT = 50000; // 5만 건으로 테스트

    public InsertService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public long runUnoptimizedQuery() {
        productRepository.deleteAllInBatch(); // Clear table for accurate measurement

        long startTime = System.nanoTime();
        // 루프 내에서 객체 생성과 save를 반복하여 건건이 처리하는 시나리오를 정확히 시뮬레이션
        for (int i = 0; i < INSERT_COUNT; i++) {
            Product product = new Product("Product " + i, ThreadLocalRandom.current().nextDouble(10, 100));
            productRepository.save(product);
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000; // ms
    }

    @Transactional
    public long runOptimizedQuery() {
        productRepository.deleteAllInBatch(); // Clear table for accurate measurement

        // 데이터를 리스트에 모두 모았다가 한 번에 저장하는 시나리오
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
