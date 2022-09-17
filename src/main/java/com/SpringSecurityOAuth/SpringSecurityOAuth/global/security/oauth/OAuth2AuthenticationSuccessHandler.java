package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth;

import static com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository.*;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.*;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.config.AppProperties;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.exception.AuthException;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.exception.ErrorCode;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.TokenProvider;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.util.CookieUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenProvider tokenProvider;
	private final AppProperties appProperties;
	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException {
		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			logger.debug("응답이 이미 전송되었습니다." + targetUrl + "로 리다이렉트가 불가능합니다.");
			return;
		}

		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
																							.map(Cookie::getValue);

		if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
			throw new IllegalArgumentException(ErrorCode.UNAUTHORIZED_REDIRECT_URI.getMessage());
		}

		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
		String accessToken = tokenProvider.createAccessToken(authentication.getName());
		String refreshToken = tokenProvider.createRefreshToken(authentication.getName());

		CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
		CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, appProperties.getAuth().getExpirySeconds() / 60);

		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("accessToken", accessToken)
		 	.build().toUriString();
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}

	private boolean isAuthorizedRedirectUri(String uri) {
		URI clientRedirectUri = URI.create(uri);

		return appProperties.getOauth2().getAuthorizedRedirectUris()
			.stream()
			.anyMatch(authorizedRedirectUri -> {
				URI authorizedURI = URI.create(authorizedRedirectUri);
				return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
					&& authorizedURI.getPort() == clientRedirectUri.getPort();
			});
	}
}
