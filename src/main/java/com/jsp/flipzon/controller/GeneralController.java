package com.jsp.flipzon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jsp.flipzon.service.GeneralService;

import jakarta.servlet.http.HttpSession;

@Controller
public class GeneralController {

	@Autowired
	GeneralService generalService;

	@GetMapping("/")
	public String loadMain(ModelMap map) {
		return generalService.loadMainPage(map);
	}

	@GetMapping("/login")
	public String loadLogin() {
		return "login.html";
	}

	@PostMapping("/login")
	public String login(@RequestParam String email, @RequestParam String password, HttpSession session) {
		return generalService.login(email, password, session);
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		return generalService.logout(session);
	}

	@GetMapping("/forget-password")
	public String loadForgetPasswordPage() {
		return "forget-password.html";
	}

	@PostMapping("/forget-password")
	public String forgetPassword(@RequestParam String email, HttpSession session) {
		return generalService.handleForgetPassword(email, session);
	}

	@GetMapping("/reset-password")
	public String resetPassword() {
		return "resetOtp.html";
	}

	@PostMapping("/reset-password")
	public String resetPassword(HttpSession session, @RequestParam int otp, @RequestParam String password) {
		return generalService.resetPassword(session, otp, password);
	}

}
