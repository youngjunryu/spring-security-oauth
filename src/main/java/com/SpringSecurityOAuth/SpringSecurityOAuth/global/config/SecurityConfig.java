package com.SpringSecurityOAuth.SpringSecurityOAuth.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.AccessDeniedHandlerImpl;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.CustomAuthenticationEntryPoint;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.CustomUserDetailsService;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.TokenAuthenticationFilter;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.TokenProvider;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.CustomOAuth2UserService;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.OAuth2AuthenticationFailureHandler;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.security.oauth.OAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private static final String ADMIN = "ADMIN";
  private static final String USER = "USER";

  private final CustomUserDetailsService customUserDetailsService;
  private final TokenProvider tokenProvider;
  private final AuthenticationConfiguration authenticationConfiguration;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Bean
  public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
    return new HttpCookieOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
          .formLogin().disable()
          .csrf().disable()
          .headers().disable()
          .httpBasic().disable()
          .rememberMe().disable()
          .logout().disable()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
          .exceptionHandling()
          .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        .and()
          .exceptionHandling()
          .accessDeniedHandler(new AccessDeniedHandlerImpl())
        .and()
          .authorizeRequests()
            .antMatchers("/auth/**", "/oauth2/**").permitAll()
            .antMatchers("/api/users/re-issue").permitAll()
            .antMatchers("/api/users/test").hasAnyRole(USER, ADMIN)
            .antMatchers("/api/users/logout").hasAnyRole(USER, ADMIN)
            .anyRequest().permitAll()
        .and()
          .oauth2Login()
          .authorizationEndpoint()
          .baseUri("/oauth2/authorization")
          .authorizationRequestRepository(cookieAuthorizationRequestRepository())
        .and()
          .redirectionEndpoint()
          .baseUri("/*/oauth2/code/*")
        .and()
          .userInfoEndpoint()
          .userService(customOAuth2UserService)
        .and()
          .successHandler(oAuth2AuthenticationSuccessHandler)
          .failureHandler(oAuth2AuthenticationFailureHandler)
        .and()
          .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
