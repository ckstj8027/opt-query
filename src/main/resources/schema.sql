
-- Drop tables and sequences if they exist to ensure a clean state
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS product_sequence;

-- Create users table
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Create articles table
CREATE TABLE articles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TEXT,
    title VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Add FULLTEXT index to the articles table
ALTER TABLE articles ADD FULLTEXT INDEX ft_content (content);

-- Create products table for SEQUENCE generation
CREATE TABLE products (
    id BIGINT NOT NULL,
    name VARCHAR(255),
    price DOUBLE,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Table to simulate sequence for products
CREATE TABLE product_sequence (
    next_val BIGINT
) ENGINE=InnoDB;

-- Initialize the sequence table
INSERT INTO product_sequence VALUES (1);

