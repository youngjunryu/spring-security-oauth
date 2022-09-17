package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @GetMapping("/auth/redirect")
  @ResponseStatus(HttpStatus.OK)
  public String test(
      @CookieValue("refresh_token") String refreshToken,
      @RequestParam(value="accessToken") String accessToken
  ) {
    return "accessToken: " + accessToken + " refreshToken: " + refreshToken;
  }
}
