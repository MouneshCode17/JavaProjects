
package com.app.dev.bikeapp.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.app.dev.bikeapp.repository.UserRepository;
import com.app.dev.bikeapp.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("Hi bro");
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);

    if(!jwtService.isTokenValid(token)){

        filterChain.doFilter(request, response);
        return;
    }

    String userId = jwtService.extractUserId(token);

    UUID userUUID = UUID.fromString(userId);

    var user = userRepository.findById(userUUID)
        .orElse(null);

    if(user == null){
        filterChain.doFilter(request, response);
    }

    var authentication = new UsernamePasswordAuthenticationToken(
        user,
        null,
        List.of(new SimpleGrantedAuthority("ROLE_" +user.getRole().name()))
    );

    System.out.println("JWT USER: " + user.getEmail());
System.out.println("JWT ROLE: " + user.getRole());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    System.out.println(
    "AUTHENTICATED: " +
    SecurityContextHolder.getContext().getAuthentication()
);
    filterChain.doFilter(request,response);

        
    }

}