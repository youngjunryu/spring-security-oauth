package com.SpringSecurityOAuth.SpringSecurityOAuth.global.exception;

public class AuthException extends BusinessException {
	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
