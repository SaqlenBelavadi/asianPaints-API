package com.speridian.asianpaints.evp.security;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.speridian.asianpaints.evp.dto.Response;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		log.error("User Authentication Error. ".concat(authException.getMessage()));
		response.setHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
		response.setContentType("application/json");
		if(response.getStatus()==HttpStatus.FORBIDDEN.value()) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "UnAuthorized");
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getOutputStream().println("{ \"message\": \"" + "Authentication failed" + "\" }");
		}
		

	}

	

	
}
