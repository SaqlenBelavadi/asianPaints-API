package com.speridian.asianpaints.evp.exception;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.speridian.asianpaints.evp.dto.Response;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerController {

	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			
			
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
				errorAttributes.remove("exception");
				return errorAttributes;
			}

			
		};
	}
	
	

	
	@ExceptionHandler(EvpException.class)
	public ResponseEntity<Response> handleCustomException(HttpServletResponse res, EvpException ex) {
		Response response = new Response(ex.getMessage(), null);
		ResponseEntity<Response> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);
		return responseEntity;
	}
	  
	/*
	 * @ExceptionHandler(AccessDeniedException.class) public void
	 * handleAccessDeniedException(HttpServletResponse res) throws IOException {
	 * res.sendError(HttpStatus.FORBIDDEN.value(), "Access denied"); }
	 */
	 

	/*
	 * @ExceptionHandler(UnAuthorizedException.class) public
	 * ResponseEntity<Response> handleUnAuthorizedException(HttpServletResponse res,
	 * UnAuthorizedException ex) {
	 * 
	 * Collection<String> headerNames = new HashSet<>(res.getHeaderNames());
	 * MultiValueMap<String, String> headerMap = new
	 * LinkedMultiValueMap<>(res.getHeaderNames().size()); headerNames.stream()
	 * .filter(header -> !header.contains(HttpHeaders.SET_COOKIE) &&
	 * !header.contains(Constants.DEFAULT_CSRF_HEADER_NAME)) .forEach(header ->
	 * headerMap.put(header, Arrays.asList(res.getHeader(header))));
	 * 
	 * res.reset(); HttpHeaders httpHeaders = new HttpHeaders(headerMap); Response
	 * response = new Response(ex.getMessage(), null); return
	 * ResponseEntity.status(ex.getHttpStatus()).headers(httpHeaders).body(response)
	 * ;
	 * 
	 * }
	 */

	@ExceptionHandler(Exception.class)
	public void handleException(HttpServletResponse res) throws IOException {
		res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong");

	}

}