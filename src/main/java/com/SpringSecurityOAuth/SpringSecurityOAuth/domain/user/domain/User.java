package com.SpringSecurityOAuth.SpringSecurityOAuth.domain.user.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Table(name="users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private String name;

  private String email;

  private String password;

  private Role role;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

  private String providerId;

  private String imageUrl;

  @Builder
  public User(String name, String email, Role role, AuthProvider provider,
              String providerId, String imageUrl) {
    this.name = name;
    this.email = email;
    this.role = role;
    this.provider = provider;
    this.providerId = providerId;
    this.imageUrl = imageUrl;
  }

  public String roleName() {
    return role.name();
  }

  public void update(String name, String imageUrl) {
    this.name = name;
    this.imageUrl = imageUrl;
  }
}
