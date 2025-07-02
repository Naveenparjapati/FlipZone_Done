package com.jsp.flipzon.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class MyExceptionHandler {
	@ExceptionHandler(NotLoggedInException.class)
	public String handle(NotLoggedInException exception, HttpSession session) {
		session.setAttribute("fail", exception.getMessage());
		return "redirect:/login";
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public String handle() {
		return "404.html";
	}
}
