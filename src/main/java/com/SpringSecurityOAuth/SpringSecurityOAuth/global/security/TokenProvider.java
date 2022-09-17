package com.SpringSecurityOAuth.SpringSecurityOAuth.global.security;

import java.time.Duration;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.SpringSecurityOAuth.SpringSecurityOAuth.global.config.AppProperties;
import com.SpringSecurityOAuth.SpringSecurityOAuth.global.redis.RedisService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenProvider {
  private final AppProperties appProperties;
  private final RedisService redisService;

  public String createAccessToken(String username) {
    int expirySeconds = appProperties.getAuth().getExpirySeconds();
    return createToken(username, expirySeconds);
  }

  public String createRefreshToken(String username) {
    int expirySeconds = appProperties.getAuth().getExpirySeconds() * 48 * 14;
    String refreshToken = createToken(username, expirySeconds);
    redisService.setValues(username, refreshToken, Duration.ofMillis(expirySeconds));
    return refreshToken;
  }

  private String createToken(String username, int expirySeconds) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirySeconds);

    return Jwts.builder()
               .setSubject(username)
               .setIssuedAt(new Date())
               .setExpiration(expiryDate)
               .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getClientSecret())
               .compact();
  }

  public void logout(HttpServletRequest request, String username) {
    String token = request.getHeader("Authorization").substring(7);
    long expiredAccessTokenTime = getExpiredTime(token).getTime() - new Date().getTime();
    redisService.setValues(appProperties.getAuth().getBlacklistPrefix() + token, username,
                           Duration.ofMillis(expiredAccessTokenTime));
    redisService.deleteValues(username);
  }

  private Date getExpiredTime(String token) {
    Claims claims = Jwts.parser()
                      .setSigningKey(appProperties.getAuth().getClientSecret())
                      .parseClaimsJws(token)
                      .getBody();

    return claims.getExpiration();
  }

  public String getUserEmailFromToken(String token) {
    Claims claims = Jwts.parser()
                        .setSigningKey(appProperties.getAuth().getClientSecret())
                        .parseClaimsJws(token)
                        .getBody();

    return claims.getSubject();
  }

  public boolean validateAccessToken(HttpServletRequest request, String token) {
    try {
      String expiredAccessToken = redisService.getValues(appProperties.getAuth().getBlacklistPrefix() + token);
      if (expiredAccessToken != null) {
        throw new ExpiredJwtException(null, null, null);
      }

      Jwts.parser().setSigningKey(appProperties.getAuth().getClientSecret()).parseClaimsJws(token);
      return true;
    } catch (SignatureException ex) {
      request.setAttribute("exception", "Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      request.setAttribute("exception", "Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      request.setAttribute("exception", "Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      request.setAttribute("exception", "Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      request.setAttribute("exception", "JWT claims string is empty.");
    }

    return false;
  }

  public void checkRefreshToken(String username, String refreshToken) {
    String redisRefreshToken = redisService.getValues(username);
    if (!redisRefreshToken.equals(refreshToken)) {
      throw new IllegalArgumentException("Invalid Refresh token");
    }
  }
}
