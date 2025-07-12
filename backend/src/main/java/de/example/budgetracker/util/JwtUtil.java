package de.example.budgetracker.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String SECRET = "mein-geheimes-jwt-passwort-mehr-als-32-zeichen";
  private final long EXPIRATION_TIME = 86400000; // 24 Stunden

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET.getBytes());
  }

  public String generateToken(String username) {
    return Jwts
      .builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .signWith(getSigningKey(), SignatureAlgorithm.HS256)
      .compact();
  }

  public String extractUsername(String token) {
    return Jwts
      .parserBuilder()
      .setSigningKey(getSigningKey())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts
        .parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }
}
