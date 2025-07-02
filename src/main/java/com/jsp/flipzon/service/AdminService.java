package com.jsp.flipzon.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jsp.flipzon.entity.Product;
import com.jsp.flipzon.exception.NotLoggedInException;
import com.jsp.flipzon.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class AdminService {

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	ProductRepository productRepository;

	@Value("${CLOUDINARY_URL}")
	private String url;

	public String loadHome(HttpSession session) {
		isLoggedIn(session);
		return "admin-home.html";

	}

	public String loadAddProduct(HttpSession session) {
		isLoggedIn(session);
		return "add-product.html";
	}

	public String addProduct(Product product, HttpSession session) {
		isLoggedIn(session);
		product.setImageLink(saveToCloud(product.getImage()));
		productRepository.save(product);
		session.setAttribute("pass", "Product Added Success");
		return "redirect:/admin/home";
	}

	public String viewProducts(HttpSession session, ModelMap map) {
		isLoggedIn(session);
		List<Product> products = productRepository.findAll();
		if (products.isEmpty()) {
			session.setAttribute("fail", "No Products Found");
			return "redirect:/admin/home";
		} else {
			map.put("products", products);
			return "view-products.html";
		}
	}

	public String deleteProduct(HttpSession session, Long id) {
		isLoggedIn(session);
		productRepository.deleteById(id);
		session.setAttribute("pass", "Product Deleted Success");
		return "redirect:/admin/view-products";
	}

	public String editProduct(HttpSession session, Long id, ModelMap map) {
		isLoggedIn(session);
		Product product = productRepository.findById(id).get();
		map.put("product", product);
		return "edit-product.html";
	}

	public String updateProduct(Product product, HttpSession session) {
		isLoggedIn(session);
		try {
			if (product.getImage().getInputStream().available() > 0) {
				product.setImageLink(saveToCloud(product.getImage()));
			} else {
				product.setImageLink(productRepository.findById(product.getId()).get().getImageLink());
			}
			productRepository.save(product);
			session.setAttribute("pass", "Product Updated Success");
			return "redirect:/admin/view-products";
		} catch (IOException e) {
			e.printStackTrace();
			return "redirect:/admin/home";
		}

	}

	private String saveToCloud(MultipartFile image) {
		Cloudinary cloudinary = new Cloudinary(url);
		try {
			Map<String, String> map = ObjectUtils.asMap("folder", "flipzone");
			Map data = cloudinary.uploader().upload(image.getBytes(), map);
			return (String) data.get("url");
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}

	}

	public String addDummyProducts(HttpSession session) {
		isLoggedIn(session);
		Map<String, List<Map<String, Object>>> response = restTemplate.getForObject("https://dummyjson.com/products",
				Map.class);

		List<Map<String, Object>> products = response.get("products");

		for (Map<String, Object> product : products) {
			String name = (String) product.get("title");
			String description = (String) product.get("description");
			int price = (int) ((Double) product.get("price") * 85.73*100);
			Integer stock = (Integer) product.get("stock");
			List<String> images = (List<String>) product.get("images");
			String imageLink = images.get(0);

			Product product2 = new Product();
			product2.setDescription(description);
			product2.setImageLink(imageLink);
			product2.setName(name);
			product2.setPrice(price/100.0);
			product2.setStock(stock);

			productRepository.save(product2);

		}

		session.setAttribute("pass", "Dummy Records Added Success");
		return "redirect:/admin/home";
	}

	private void isLoggedIn(HttpSession session) {
		if (session.getAttribute("admin") == null)
			throw new NotLoggedInException();
	}

}
