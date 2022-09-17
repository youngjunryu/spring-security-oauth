package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 인증되지 않은 사용자의 요청을 처리
 * 예를 들어, 토큰이 없음 or 토큰이 유효하지 않음
 */
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException {
		String exceptionMsg = (String)request.getAttribute("exception");

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		new ObjectMapper().writeValue(response.getWriter(),
																	new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exceptionMsg));
	}
}

