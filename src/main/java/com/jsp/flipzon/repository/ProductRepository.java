package com.jsp.flipzon.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.flipzon.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByNameLike(String name, PageRequest pageRequest);

	Product findByNameAndDescriptionAndPrice(String name, String description, Double price);

}
