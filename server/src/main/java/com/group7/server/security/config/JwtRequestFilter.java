package com.group7.server.security.config;

import com.group7.server.model.Player;
import com.group7.server.security.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** The component that deals with the JWT token authentication and filtering of requests*/
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value(value = "${security.secretKey}")
    private String mSecretKey;
    private final UserDetailsManager mUserDetailsManager;

    /**
     * Extracts the JWT token from the HTTP request, and performs filtering  and authentication operations.
     *
     * @param request the HTTP request sent to any web controller (api).
     * @param response the HTTP response that is used in the filter chain for it's internal operations.
     * @param filterChain the filtering mechanism that performs security actions and deals with authentication.
     * @throws ServletException may be thrown because of the filtering operations.
     * @throws IOException may be thrown because of the filtering operations.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authentication = request.getHeader("Authorization");
        if (authentication != null && authentication.startsWith("Bearer")) {
            //Extract the jwt token from HTTP header and extract the username
            String jwtToken = authentication.substring(7);
            String username = JwtUtil.extractUsername(jwtToken, mSecretKey);

            //Find the player by username
            Player userDetails = (Player) mUserDetailsManager.loadUserByUsername(username);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                //If authentication is not performed, create token and set authentication
                var token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(token);
            }
        }
        //Perform security filtering operations
        filterChain.doFilter(request, response);
    }
}