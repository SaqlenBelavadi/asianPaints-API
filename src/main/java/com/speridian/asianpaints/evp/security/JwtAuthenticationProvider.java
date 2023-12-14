package com.speridian.asianpaints.evp.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Override
	public Authentication authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		String username = null;
		try {
			username = jwtUtil.getUsernameFromToken(authToken);
		} catch (IllegalArgumentException e) {
			log.error("Unable to get Jwt Token");
		} catch (ExpiredJwtException e) {
			log.error("Token has expired");
		} catch (Exception e) {
			log.error(e+Arrays.stream(e.getStackTrace()).map(Objects::toString).collect(Collectors.joining("\n")));
		}

		if (Optional.ofNullable(username).isPresent() && !username.isEmpty() && jwtUtil.validateToken(authToken)) {
			Claims claims = jwtUtil.getAllClaimsFromToken(authToken);
			String rolesMap = claims.get("role", String.class);
			List<SimpleGrantedAuthority> authorities = new ArrayList<>(1);
			authorities.add(new SimpleGrantedAuthority(rolesMap));
			return new UsernamePasswordAuthenticationToken(CommonUtils.getEmployeeIdFromUsername(username), null, authorities);
		} else {
			return null;
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}