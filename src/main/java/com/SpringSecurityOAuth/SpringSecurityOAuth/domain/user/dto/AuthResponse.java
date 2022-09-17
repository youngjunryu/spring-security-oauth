package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.dto;


public class AuthResponse {
	private String accessToken;
	private String refreshToken;
	private String grantType = "Bearer";

	public AuthResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getGrantType() {
		return grantType;
	}
}
