package com.group7.server.security.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.security.Keys.hmacShaKeyFor;
import static java.util.stream.Collectors.toList;

/** Performs JWT related operations.*/
public class JwtUtil {

    /** Generates a JWT token with the given arguments.*/
    public static String generateToken(Authentication user, String secretKey, Integer expirationDay) {

        return Jwts.builder()
                .setSubject(user.getName())
                .claim("authorities", getAuthorities(user))
                .setIssuedAt(new Date())
                .setExpiration(calculateExpirationDate(expirationDay))
                .signWith(hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    /** Lists the authorities of the given user.*/
    private static List<String> getAuthorities(Authentication user) {
        return user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(toList());
    }

    /** Calculates the date of expiration of the JWT token by using the day parameter.*/
    private static Date calculateExpirationDate(Integer expirationDay) {
        Instant expirationTime = LocalDate.now()
                .plusDays(expirationDay)
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(expirationTime);
    }

    /** Extracts the user from active users by using JWT token and secret key.*/
    public static String extractUsername(final String jwtToken, final String secretKey) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        return claims.getSubject();
    }
}
