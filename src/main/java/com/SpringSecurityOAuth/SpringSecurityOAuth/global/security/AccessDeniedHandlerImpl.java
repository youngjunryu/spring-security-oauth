package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 인가 X
 */
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException exception) throws IOException {
    response.setStatus(HttpStatus.FORBIDDEN.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    new ObjectMapper().writeValue(response.getWriter(),
                                  new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                                                    exception.getMessage()));
  }
}
