package com.jsp.flipzon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.jsp.flipzon.config.AES;
import com.jsp.flipzon.controller.AdminController;
import com.jsp.flipzon.entity.Customer;
import com.jsp.flipzon.entity.CustomerOrder;
import com.jsp.flipzon.entity.Item;
import com.jsp.flipzon.entity.Product;
import com.jsp.flipzon.exception.NotLoggedInException;
import com.jsp.flipzon.repository.CustomerOrderRepository;
import com.jsp.flipzon.repository.CustomerRepository;
import com.jsp.flipzon.repository.ItemRepository;
import com.jsp.flipzon.repository.ProductRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	private final AdminController adminController;

	@Value("${razor-pay.api.key}")
	String key;
	@Value("${razor-pay.api.secret}")
	String secret;

	@Autowired
	CustomerOrderRepository orderRepository;
	@Autowired
	JavaMailSender sender;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	ProductRepository productRepository;

	CustomerService(AdminController adminController) {
		this.adminController = adminController;
	}

	public String register(Customer customer, HttpSession session) {
		if (customerRepository.existsByEmail(customer.getEmail())
				|| customerRepository.existsByMobile(customer.getMobile())) {
			session.setAttribute("fail", "* Account Already Exists");
			return "redirect:/customer/register";
		} else {
			customer.setPassword(AES.encrypt(customer.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			session.setAttribute("otp", otp);
			session.setAttribute("register", customer);
			sendOtp(otp, customer.getEmail());
			session.setAttribute("pass", "Otp Sent Success");
			return "redirect:/customer/otp";
		}
	}

	private void sendOtp(int otp, String email) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("OTP Verification");
		message.setText("Your OTP is " + otp + " , Enter this otp to create Account");
		try {
			sender.send(message);
		} catch (MailAuthenticationException e) {
			System.err.println("Sending Mail Failed but OTP is : " + otp);
		}
	}

	public String submitOtp(int otp, HttpSession session) {
		int generatedOtp = (int) session.getAttribute("otp");
		Customer customer = (Customer) session.getAttribute("register");
		if (otp == generatedOtp) {
			customerRepository.save(customer);
			session.setAttribute("pass", "Account Created Success");
			return "redirect:/login";
		} else {
			session.setAttribute("fail", "Invalid Otp, Try Again");
			return "redirect:/customer/otp";
		}
	}

	public String loadHome(HttpSession session) {
		getCustomerFromSession(session);
		return "customer-home.html";
	}

	public String viewProducts(HttpSession session, ModelMap map, String name, String sort, boolean desc,
			Integer page) {
		getCustomerFromSession(session);

		Sort way = desc ? Sort.by(sort).descending() : Sort.by(sort);

		Page<Product> productsPage = productRepository.findByNameLike("%" + name + "%",
				PageRequest.of(page - 1, 9, way));

		map.put("page", productsPage.getNumber() + 1);
		map.put("total", productsPage.getTotalPages());
		map.put("prev", productsPage.hasPrevious());
		map.put("next", productsPage.hasNext());
		map.put("products", productsPage.getContent());
		return "products.html";

	}

	public Customer getCustomerFromSession(HttpSession session) {
		if (session.getAttribute("customer") == null)
			throw new NotLoggedInException();
		else
			return (Customer) session.getAttribute("customer");
	}

	public String addToCart(Long id, HttpSession session) {
		Customer customer = getCustomerFromSession(session);
		Product product = productRepository.findById(id).get();
		if (product.getStock() < 1) {
			session.setAttribute("fail", "Out of Stock");
			return "redirect:/customer/view-products";
		} else {
			product.setStock(product.getStock() - 1);
			productRepository.save(product);
			List<Item> items = customer.getItems();
			if (items == null) {
				items = new ArrayList<Item>();
			}

			boolean flag = true;
			for (Item item : items) {
				if (item.getName().equals(product.getName()) && item.getPrice().equals(product.getPrice())
						&& item.getDescription().equals(product.getDescription())) {
					item.setQuantity(item.getQuantity() + 1);
					itemRepository.save(item);
					flag = false;
					break;
				}
			}

			if (flag) {
				Item item = new Item();
				item.setImageLink(product.getImageLink());
				item.setDescription(product.getDescription());
				item.setName(product.getName());
				item.setPrice(product.getPrice());
				item.setQuantity(1);
				itemRepository.save(item);
				items.add(item);
				customer.setItems(items);
				customerRepository.save(customer);
			}
			session.setAttribute("customer", customerRepository.findById(customer.getId()).get());
			session.setAttribute("pass", "Product Added to Cart");
			return "redirect:/customer/view-products";
		}
	}

	public String viewCart(HttpSession session, ModelMap map) {
		Customer customer = getCustomerFromSession(session);
		List<Item> items = customer.getItems();
		if (items == null || items.isEmpty()) {
			session.setAttribute("fail", "No Items in Cart");
			return "redirect:/customer/home";
		} else {
			map.put("items", items);
			map.put("total", items.stream().mapToDouble(x -> (x.getPrice() * x.getQuantity())).sum());
			return "cart.html";
		}
	}

	public String increaseItem(Long id, HttpSession session) {
		Customer customer = getCustomerFromSession(session);
		Item item = itemRepository.findById(id).get();

		Product product = productRepository.findByNameAndDescriptionAndPrice(item.getName(), item.getDescription(),
				item.getPrice());
		if (product.getStock() > 0) {
			item.setQuantity(item.getQuantity() + 1);
			itemRepository.save(item);

			product.setStock(product.getStock() - 1);
			productRepository.save(product);

			session.setAttribute("pass", "Item Quantity Increased Success");
		} else {
			session.setAttribute("fail", "Out of Stock");
		}

		session.setAttribute("customer", customerRepository.findById(customer.getId()).get());
		return "redirect:/customer/view-cart";
	}

	public String decreaseItem(Long id, HttpSession session) {
		Customer customer = getCustomerFromSession(session);

		Item item = itemRepository.findById(id).get();
		Product product = productRepository.findByNameAndDescriptionAndPrice(item.getName(), item.getDescription(),
				item.getPrice());

		if (item.getQuantity() > 1) {
			item.setQuantity(item.getQuantity() - 1);
			itemRepository.save(item);
		} else {
			for (Item item2 : customer.getItems()) {
				if (item2.getId().equals(item.getId())) {
					customer.getItems().remove(item2);
					break;
				}
			}
			customerRepository.save(customer);
			itemRepository.delete(item);
		}
		product.setStock(product.getStock() + 1);
		productRepository.save(product);
		session.setAttribute("customer", customerRepository.findById(customer.getId()).get());
		session.setAttribute("pass", "Item Quantity Reduced Success");
		return "redirect:/customer/view-cart";
	}

	public String checkout(HttpSession session, ModelMap map) {
		Customer customer = getCustomerFromSession(session);
		double totalAmount = customer.getItems().stream().mapToDouble(x -> (x.getPrice() * x.getQuantity())).sum();
		RazorpayClient razorpay;
		try {
			razorpay = new RazorpayClient(key, secret);
			JSONObject orderRequest = new JSONObject();
			orderRequest.put("amount", (int) (totalAmount * 100));
			orderRequest.put("currency", "INR");

			Order order = razorpay.orders.create(orderRequest);
			String orderId = order.get("id");

			CustomerOrder customerOrder = new CustomerOrder();
			customerOrder.setCustomer(customer);
			customerOrder.setTotalAmount(totalAmount);
			customerOrder.setId(orderId);

			customerOrder.setItems(customer.getItems());

			orderRepository.save(customerOrder);

			map.put("key", key);
			map.put("amount", totalAmount * 100);
			map.put("orderId", orderId);
			map.put("url", "/customer/success/" + orderId);
			map.put("customer", customer);

			return "payment.html";

		} catch (RazorpayException e) {
			e.printStackTrace();
			throw new NotLoggedInException();
		}

	}

	public String paymentSuccess(String id, String razorpay_payment_id, HttpSession session) {
		Customer customer = getCustomerFromSession(session);
		CustomerOrder order = orderRepository.findById(id).get();
		order.setPaymentId(razorpay_payment_id);
		orderRepository.save(order);
		customer.setItems(null);
		customerRepository.save(customer);
		session.setAttribute("customer", customerRepository.findById(customer.getId()).get());

		session.setAttribute("pass", "Purchase Success");
		return "redirect:/customer/home";
	}

	public String displayPastOrders(HttpSession session, ModelMap map) {
		Customer customer = getCustomerFromSession(session);
		List<CustomerOrder> orders = orderRepository.findByCustomer(customer);
		if (orders.isEmpty()) {
			session.setAttribute("fail", "No Orders Yet");
			return "redirect:/customer/home";
		} else {
			map.put("orders", orders);
			return "orders.html";
		}
	}
}
