package com.jsp.flipzon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.flipzon.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	boolean existsByMobile(Long mobile);

	Customer findByEmail(String email);

}
