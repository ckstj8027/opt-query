package com.example.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.query", "com.example.queryoptimization"})
@EnableJpaRepositories(basePackages = "com.example.queryoptimization.common.repository")
@EntityScan(basePackages = "com.example.queryoptimization.common.entity")
public class QueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryApplication.class, args);
	}
	//docker run -d --name mysql-query-opt --restart always -p 3306:3306 -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=queryopt -e MYSQL_USER=queryuser -e MYSQL_PASSWORD=querypassword -v D:/query/data:/var/lib/mysql mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
}
