package com.jsp.flipzon.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class CustomerOrder {
	@Id
	private String id;
	private double totalAmount;
	@UpdateTimestamp
	private LocalDateTime creationTime;
	@OneToMany(fetch = FetchType.EAGER)
	private List<Item> items;
	private String paymentId;
	@ManyToOne
	Customer customer;

}
