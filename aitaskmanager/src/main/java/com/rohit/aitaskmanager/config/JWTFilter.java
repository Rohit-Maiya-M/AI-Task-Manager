package com.rohit.aitaskmanager.config;

import com.rohit.aitaskmanager.exception.AccessDeniedException;
import com.rohit.aitaskmanager.exception.InvalidCredentialsException;
import com.rohit.aitaskmanager.models.User;
import com.rohit.aitaskmanager.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public JWTFilter(JWTUtil jwtUtil, UserRepository userRepository){
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )throws IOException, ServletException {
        final String authHeader = request.getHeader("Authorization");
        final String username;
        final String token;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);
        username = jwtUtil.extractUsername(token);

        if(username != null){
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AccessDeniedException("Access Denied!"));

            Long userId = jwtUtil.extractClaimsAll(token).get("id", Long.class);

            if(user.getId() != userId){
                throw new InvalidCredentialsException("Invalid Credentials!");
            }


            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getId(),
                    null,
                    new ArrayList<>()
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        }

    }

}
