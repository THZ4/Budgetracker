package de.example.budgetracker.config;

import de.example.budgetracker.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter implements Filter {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public void doFilter(
    ServletRequest request,
    ServletResponse response,
    FilterChain chain
  ) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String authHeader = httpRequest.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      if (!jwtUtil.validateToken(token)) {
        ((HttpServletResponse) response).sendError(
            HttpServletResponse.SC_UNAUTHORIZED,
            "Invalid JWT token"
          );
        return;
      }

      // User ist Angemeldet
      String username = jwtUtil.extractUsername(token);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
        username,
        null,
        new ArrayList<>()
      );
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    chain.doFilter(request, response);
  }
}
