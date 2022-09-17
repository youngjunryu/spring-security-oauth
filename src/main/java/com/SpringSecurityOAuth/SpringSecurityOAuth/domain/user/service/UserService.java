package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.User;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.UserRepository;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto.AuthResponse;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto.ReIssueRequest;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.TokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
  private final UserRepository userRepository;
  private final TokenProvider tokenProvider;

  public AuthResponse reIssue(ReIssueRequest reIssueRequest) {
    String email = reIssueRequest.getEmail();
    userRepository.findByEmail(email)
                  .orElseThrow(
                      () -> new UsernameNotFoundException("유저를 찾을 수 없습니다. email: " + email));

    tokenProvider.checkRefreshToken(reIssueRequest.getEmail(), reIssueRequest.getRefreshToken());

    return new AuthResponse(tokenProvider.createAccessToken(email),
                            tokenProvider.createRefreshToken(email));
  }

  public void logout(HttpServletRequest request, User user) {
    tokenProvider.logout(request, user.getEmail());
  }
}
