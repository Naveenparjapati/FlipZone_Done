package com.jsp.flipzon.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jsp.flipzon.config.AES;
import com.jsp.flipzon.entity.Customer;
import com.jsp.flipzon.entity.Product;
import com.jsp.flipzon.repository.CustomerRepository;
import com.jsp.flipzon.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class GeneralService {

	@Value("${admin.email}")
	private String adminEmail;
	@Value("${admin.password}")
	private String adminPassword;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	CustomerRepository customerRepository;

	public String login(String email, String password, HttpSession session) {
		if (email.equals(adminEmail) && password.equals(adminPassword)) {
			session.setAttribute("admin", "admin");
			session.setAttribute("pass", "Login Success, Welcome Admin");
			return "redirect:/admin/home";
		} else {
			Customer customer = customerRepository.findByEmail(email);
			if (customer == null) {
				session.setAttribute("fail", "Invalid Email");
				return "redirect:/login";
			} else {
				if (AES.decrypt(customer.getPassword()).equals(password)) {
					session.setAttribute("pass", "Login Success, Welcome " + customer.getName());
					session.setAttribute("customer", customer);
					return "redirect:/customer/home";
				} else {
					session.setAttribute("fail", "Invalid Password");
					return "redirect:/login";
				}
			}
		}
	}

	public void removeMessage() {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		HttpServletRequest request = attributes.getRequest();
		HttpSession session = request.getSession(true);
		session.removeAttribute("pass");
		session.removeAttribute("fail");
	}

	public String logout(HttpSession session) {
		session.removeAttribute("admin");
		session.setAttribute("fail", "Logout Success");
		return "redirect:/";
	}

	public String loadMainPage(ModelMap map) {
		List<Product> products=productRepository.findAll();
		map.put("products", products);
		return "main";
	}

}
