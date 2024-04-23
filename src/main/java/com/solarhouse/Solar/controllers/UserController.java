package com.solarhouse.Solar.controllers;

import com.solarhouse.Solar.entities.User;
import com.solarhouse.Solar.repository.UserRepository;
import com.solarhouse.Solar.service.AuthService;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(path = "/api")
public class UserController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthService authService;

    @GetMapping(path = "/getAllUsers")
    public @ResponseBody Iterable<User> getAllUsers() {
        return repository.findAll();
    }

    @GetMapping(path = "/getUser/{id}")
    public @ResponseBody Optional<User> getUserById(@PathVariable Long id) {
        return repository.findById(id);
    }

    @PostMapping(path = "/postUser")
    public @ResponseBody ResponseEntity<String> postUser(@RequestBody User user) {
        user.setPassword(Hashing.sha256()
                .hashString(user.getPassword(), StandardCharsets.UTF_8)
                .toString());
        user.setDatecreation(new Date());
        Optional<User> userExist = repository.findByEmail(user.getEmail());
        if (userExist.isEmpty()) {
            repository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("email already exists");
        }
    }

    @PostMapping(path = "/authUser")
    public @ResponseBody ResponseEntity<Map<String, Object>> authUser(@RequestBody Map<String, String> json) {
        return this.authService.authenticate(json.get("email"), json.get("password"));
    }
}