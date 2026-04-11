package com.rohit.aitaskmanager.controller;


import com.rohit.aitaskmanager.config.JWTUtil;
import com.rohit.aitaskmanager.dto.AuthResponse;
import com.rohit.aitaskmanager.dto.UserRequest;
import com.rohit.aitaskmanager.exception.InvalidCredentialsException;
import com.rohit.aitaskmanager.exception.UsernameNotFoundException;
import com.rohit.aitaskmanager.models.User;
import com.rohit.aitaskmanager.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository, JWTUtil jwtUtil){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody User newuser){
        newuser.setPassword(passwordEncoder.encode(newuser.getPassword()));
        User user = userRepository.save(newuser);
        return ResponseEntity.ok("User created successfully with id: " + user.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest userRequest){

        User existing = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username invalid!"));

        if(!passwordEncoder.matches(userRequest.getPassword(), existing.getPassword())){
            throw new InvalidCredentialsException("Invalid Password!");
        }

        String token = jwtUtil.generateToken(existing.getUsername(), existing.getId());
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .username(existing.getUsername())
                .build();
        return ResponseEntity.ok(authResponse);
    }


}
