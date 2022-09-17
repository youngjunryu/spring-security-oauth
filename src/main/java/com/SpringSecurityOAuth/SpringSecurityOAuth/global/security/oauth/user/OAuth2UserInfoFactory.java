package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.user;

import java.util.Map;

import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.AuthProvider;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.OAuth2AuthenticationProcessingException;

public class OAuth2UserInfoFactory {
	private OAuth2UserInfoFactory() {
		throw new IllegalStateException("OAuth2UserInfoFactory의 인스턴스는 생성할 수 없습니다.");
	}

	public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
		if (registrationId.equalsIgnoreCase(AuthProvider.google.toString())) {
			return new GoogleOAuth2UserInfo(attributes);
		}
		throw new OAuth2AuthenticationProcessingException(registrationId + " 로그인은 지원하지 않습니다.");
	}
}