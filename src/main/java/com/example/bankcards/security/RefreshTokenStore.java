package com.example.bankcards.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Refresh token store
 */
@Service
public class RefreshTokenStore {
    private final Map<String, String> storage = new ConcurrentHashMap<>();
    // key = username, value = jti

    /**
     * Save token to store
     * @param username name of user
     * @param jti token
     */
    public void save(String username, String jti) {
        storage.put(username, jti);
    }

    /**
     * Check is token valid
     * @param username name of user
     * @param jti token
     * @return boolean
     */
    public boolean isValid(String username, String jti) {
        return jti.equals(storage.get(username));
    }

    /**
     * Remove token from storage
     * @param username name of user
     */
    public void delete(String username) {
        storage.remove(username);
    }
}
