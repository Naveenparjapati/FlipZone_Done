package com.jsp.flipzon.exception;

import lombok.Getter;

@Getter
public class NotLoggedInException extends RuntimeException {
	String message = "Invalid Session, Login Again";
}
