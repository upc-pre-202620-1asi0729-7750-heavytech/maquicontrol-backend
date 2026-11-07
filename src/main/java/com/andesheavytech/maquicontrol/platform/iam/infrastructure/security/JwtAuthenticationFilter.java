package com.andesheavytech.maquicontrol.platform.iam.infrastructure.security;

import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.UserAccountJpaRepository;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final UserAccountJpaRepository accounts;
    public JwtAuthenticationFilter(JwtService jwt, UserAccountJpaRepository accounts) { this.jwt=jwt; this.accounts=accounts; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        var header=request.getHeader("Authorization");
        if(header!=null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null) try {
            var account=accounts.findById(jwt.accountId(header.substring(7))).filter(a -> a.isActive()).orElse(null);
            if(account!=null) { var authorities=account.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_"+r.getName().name())).toList(); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(account.getId(), null, authorities)); }
        } catch (RuntimeException ignored) { SecurityContextHolder.clearContext(); }
        chain.doFilter(request,response);
    }
}
