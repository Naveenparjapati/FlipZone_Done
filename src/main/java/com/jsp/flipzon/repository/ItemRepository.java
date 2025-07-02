package com.jsp.flipzon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jsp.flipzon.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
