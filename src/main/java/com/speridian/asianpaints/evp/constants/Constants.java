package com.speridian.asianpaints.evp.constants;

public class Constants {

	public static final String LOGOUT_URL = "/api/evp/v1/Logout";
	public static final String CSP_HEADER_KEY = "X-Content-Security-Policy";
	public static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";
	public static final String AUTHORIZATION = "Authorization";
	public static final String EVP_MANAGEMENT_ENDPOINT_PREFIX = "/actuator";
	public static final String[] PUBLIC_LIST = { "/api/evp/v1/Login", "/api/evp/v1/RefreshToken", "/actuator/**",
			"/actuator/prometheus/**" };
	public static final String[] CSRF_PUBLIC_LIST = { "/api/evp/v1/Login", "/api/evp/v1/RefreshToken",
			"/api/evp/v1/Portal", "/portal", EVP_MANAGEMENT_ENDPOINT_PREFIX, "/actuator/prometheus" };

	public static final String[] PUBLIC_IGNORE_LIST = { "/v2/api-docs", "/configuration/ui", "/swagger-resources/**",
			"/configuration/security", "/swagger-ui.html", "/webjars/**" };

	public static final String[] ALLOWED_HEADERS = { AUTHORIZATION, "Content-Type", "observe", "Accept", "Set-Cookie",
			"Rate-Limit-Remaining", "X-Content-Type-Options", "X-XSS-Protection", "Cache-Control", "X-Frame-Options",
			CSP_HEADER_KEY, "Strict-Transport-Security", DEFAULT_CSRF_HEADER_NAME, "Access-Control-Allow-Headers" };

	public static final String CSRF_API_URL = "/**";

	public static final String CSP_HEADER_VALUE = "script-src 'self'";

	public static final String SERVER_HEADER = "Server";

	public static final String SERVER_HEADER_VALUE = "EVP Server";

	public static final String[] METHOD_ALLOW = {

			"GET", "POST", "PUT", "DELETE", "OPTIONS" };

	public static final String ROLE_EMPLOYEE = "ROLE_EMPLOYEE";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	public static final String PUBLISHED = "Published";

	public static final String UNPUBLISHED = "UnPublished";

	public static final String TRUE = "TRUE";

	public static final String FALSE = "FALSE";

}
