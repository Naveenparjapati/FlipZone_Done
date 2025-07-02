package com.jsp.flipzon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.flipzon.entity.Customer;
import com.jsp.flipzon.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@GetMapping("/register")
	public String loadRegister() {
		return "register.html";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute Customer customer, HttpSession session) {
		return customerService.register(customer, session);
	}

	@GetMapping("/otp")
	public String loadOtp() {
		return "otp.html";
	}

	@PostMapping("/submit-otp")
	public String submitOtp(@RequestParam int otp, HttpSession session) {
		return customerService.submitOtp(otp, session);
	}

	@GetMapping("/home")
	public String loadHome(HttpSession session) {
		return customerService.loadHome(session);
	}

	@GetMapping("/view-products")
	public String viewProducts(HttpSession session, ModelMap map, @RequestParam(defaultValue = "") String name,
			@RequestParam(defaultValue = "name") String sort, @RequestParam(defaultValue = "false") boolean desc,
			@RequestParam(defaultValue = "1") Integer page) {
		return customerService.viewProducts(session, map, name, sort, desc, page);
	}

	@GetMapping("/add-to-cart/{id}")
	public String addToCart(@PathVariable Long id, HttpSession session) {
		return customerService.addToCart(id, session);
	}

	@GetMapping("/view-cart")
	public String loadCart(HttpSession session, ModelMap map) {
		return customerService.viewCart(session, map);
	}

	@GetMapping("/cart/increase/{id}")
	public String increaseItem(@PathVariable Long id, HttpSession session) {
		return customerService.increaseItem(id, session);
	}

	@GetMapping("/cart/decrease/{id}")
	public String decreaseItem(@PathVariable Long id, HttpSession session) {
		return customerService.decreaseItem(id, session);
	}

	@GetMapping("/checkout")
	public String checkout(HttpSession session, ModelMap map) {
		return customerService.checkout(session, map);
	}

	@PostMapping("/success/{id}")
	public String paymentSuccess(@PathVariable String id, @RequestParam String razorpay_payment_id,
			HttpSession session) {
		return customerService.paymentSuccess(id, razorpay_payment_id, session);
	}

	@GetMapping("/orders")
	public String displayPastOrders(HttpSession session, ModelMap map) {
		return customerService.displayPastOrders(session, map);
	}
}
