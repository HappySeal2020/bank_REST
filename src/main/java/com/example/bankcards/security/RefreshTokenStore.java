package com.example.bankcards.security;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenStore {
    private final Map<String, String> storage = new ConcurrentHashMap<>();
    // key = username, value = jti

    public void save(String username, String jti) {
        storage.put(username, jti);
    }

    public boolean isValid(String username, String jti) {
        return jti.equals(storage.get(username));
    }

    public void delete(String username) {
        storage.remove(username);
    }
}
