package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth;

import java.util.Optional;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.AuthProvider;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.Role;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.User;
import com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain.UserRepository;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.UserPrincipal;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.user.OAuth2UserInfo;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.user.OAuth2UserInfoFactory;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	public CustomOAuth2UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

		try {
			return processOAuth2User(oAuth2UserRequest, oAuth2User);
		} catch (AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
		}
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
			oAuth2UserRequest.getClientRegistration().getRegistrationId(),
			oAuth2User.getAttributes()
		);

		if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("OAuth2 provider에 이메일이 없습니다.");
		}

		Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
		User user;
		if (userOptional.isPresent()) {
			user = userOptional.get();
			if (!user.getProvider().equals(
					AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase()))) {
				throw new OAuth2AuthenticationProcessingException("이미 등록된 회원입니다.");
			}
			user = updateExistingUser(user, oAuth2UserInfo);
		} else {
			user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
		}

		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
		return userRepository.save(User.builder()
																	 .name(oAuth2UserInfo.getName())
																	 .email(oAuth2UserInfo.getEmail())
																	 .role(Role.ROLE_USER)
																	 .provider(AuthProvider.valueOf(
																			 oAuth2UserRequest.getClientRegistration().getRegistrationId()))
																	 .providerId(oAuth2UserInfo.getId())
																	 .imageUrl(oAuth2UserInfo.getImageUrl())
																	 .build());
	}

	private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
		existingUser.update(oAuth2UserInfo.getName(), oAuth2UserInfo.getImageUrl());
		return userRepository.save(existingUser);
	}
}