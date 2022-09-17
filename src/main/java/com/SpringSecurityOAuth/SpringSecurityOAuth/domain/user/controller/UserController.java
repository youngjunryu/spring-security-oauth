package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.User;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto.AuthResponse;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto.ReIssueRequest;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.service.UserService;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/users")
@RestController
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PutMapping("/logout")
  public void logout(
      HttpServletRequest request,
      @CurrentUser User user
  ) {
    userService.logout(request, user);
  }

  @PostMapping("/re-issue")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse reIssue(
      @Valid @RequestBody ReIssueRequest reIssueRequest
  ) {
    return userService.reIssue(reIssueRequest);
  }
}
