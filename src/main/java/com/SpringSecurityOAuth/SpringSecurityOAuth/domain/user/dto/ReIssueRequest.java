package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReIssueRequest {

  @NotBlank
  private String email;

  @NotBlank
  private String refreshToken;
}
