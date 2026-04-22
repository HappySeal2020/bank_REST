package com.example.bankcards.service.impl;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Interface for managing JWT
 * provides operations for generate tokens, extract roles, extract Jti, extract token type
 */
public interface JwtService {

     /**
      * generate access token
      * @param user Spring Security user
      * @return token
      */
     String generateAccessToken(UserDetails user);

     /**
      * generate refresh token
      * @param user Spring Security user
      * @return token
      */
     String generateRefreshToken(UserDetails user);

     /**
      * extract username from token
      * @param token token
      * @return username
      */
     String extractUsername(String token);

     /**
      * Checks whether the token is refreshing
      * @param token token
      * @return yes or no
      */
     boolean isRefreshToken(String token);

     /**
      * extract roles from token
      * @param token token
      * @return roles
      */
     List<String> extractRoles(String token);

     /**
      * extract Jti from token
      * @param token token
      * @return Jti
      */
     String extractJti(String token);

     /**
      * extract type of token
      * @param token token
      * @return type of token
      */
     String extractTokenType(String token);
}
