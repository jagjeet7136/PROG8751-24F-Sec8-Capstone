create database ecommerce;

use ecommerce;

CREATE TABLE Product (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    long_description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discounted_price DECIMAL(10, 2) DEFAULT NULL,
    image_url VARCHAR(500),
    stock INT DEFAULT 0,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES Category(id) ON DELETE SET NULL
);


CREATE TABLE Category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO Category (name, description) VALUES
('Electronics', 'Devices, gadgets, and accessories'),
('Fashion', 'Clothing, shoes, and accessories for men, women, and children'),
('Home & Kitchen', 'Furniture, decor, and kitchen appliances'),
('Books', 'Books across different genres and categories'),
('Toys & Games', 'Toys, puzzles, and games for children and adults'),
('Health & Beauty', 'Health care products, cosmetics, and personal care items'),
('Sports & Outdoors', 'Sporting goods, outdoor gear, and fitness equipment'),
('Automotive', 'Car accessories, tools, and spare parts'),
('Groceries', 'Daily essentials, food, and beverages'),
('Jewelry', 'Rings, necklaces, bracelets, and other jewelry items');