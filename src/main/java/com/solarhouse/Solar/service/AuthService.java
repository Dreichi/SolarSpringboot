package com.solarhouse.Solar.service;

import com.solarhouse.Solar.entities.User;
import com.solarhouse.Solar.repository.UserRepository;
import com.google.common.hash.Hashing;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AuthService {
    private Key secret = new SecretKeySpec(
            Base64.getDecoder().decode("34dd36e949943cc11155a864cc4df88cd61f09cf1330fc07fa36f445ae6e133a"),
            SignatureAlgorithm.HS256.getJcaName());

    @Autowired
    private UserRepository UserRepository;

    public ResponseEntity<Map<String, Object>> authenticate(String email, String password) {
        ResponseEntity<Map<String, Object>> result;
        Optional<User> userOptionnal = this.UserRepository.findByEmail(email);

        if (userOptionnal.isPresent()) {
            User user = userOptionnal.get();
            if (authUser(user, password)) {
                Map<String, Object> mapResult = new HashMap<>();
                mapResult.put("token", generateJwt(user));
                mapResult.put("user", user);
                result = ResponseEntity.ok(mapResult);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("error", "password doesn't match");
                result = ResponseEntity.ok(map);
            }
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("error", "email not found");
            result = ResponseEntity.ok(map);
        }
        return result;
    }

    private boolean authUser(User user, String password) {
        boolean isConnected = false;
        String hashPassword = Hashing.sha256()
                .hashString(password, StandardCharsets.UTF_8)
                .toString();
        if (Objects.equals(user.getPassword(), hashPassword)) {
            isConnected = true;
        }
        return isConnected;
    }

    private String generateJwt(User user) {
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("email", user.getEmail())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(this.secret)
                .compact();
    }
}