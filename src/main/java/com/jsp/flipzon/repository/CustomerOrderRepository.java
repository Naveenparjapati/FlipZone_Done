package com.jsp.flipzon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.flipzon.entity.Customer;
import com.jsp.flipzon.entity.CustomerOrder;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, String> {

	List<CustomerOrder> findByCustomer(Customer customer);

}
