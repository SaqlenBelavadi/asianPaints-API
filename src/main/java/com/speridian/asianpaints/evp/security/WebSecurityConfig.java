/**
 * 
 */
package com.speridian.asianpaints.evp.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author priyank.mishra
 *
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.speridian.asianpaints.evp.constants.Constants;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private JwtAuthenticationProvider authProvider;
	
	@Autowired 
	private JwtRequestFilter jwtRequestFilter;
	

	@Value("${allowed.origin}")
	private String allowedOrigin;

	@Value("${allowed.origin.local}")
	private String allowedOriginLocal;

	@Value("${allowed.origin.public.app}")
	private String allowedOriginPublicApp;

	@Value("${allowed.origin.public.app.uat}")
	private String allowedOriginUat;

	@Value("${allowed.origin.public.app.prod}")
	private String allowedOriginProd;
	  
	 

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.cors().configurationSource(corsConfigurationSource()).and().csrf().disable().authorizeRequests()
				.antMatchers(Constants.PUBLIC_LIST).permitAll().anyRequest().authenticated().and().exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		
		httpSecurity.headers().httpStrictTransportSecurity().and()
				.addHeaderWriter(new StaticHeadersWriter(Constants.CSP_HEADER_KEY, Constants.CSP_HEADER_VALUE))
				.addHeaderWriter(new StaticHeadersWriter(Constants.SERVER_HEADER, Constants.SERVER_HEADER_VALUE));


	}

	@Override
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers(Constants.PUBLIC_IGNORE_LIST);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		List<String> allowedOrigins = new ArrayList<>();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedMethods(Arrays.asList(Constants.METHOD_ALLOW));
		corsConfiguration.setAllowedHeaders(Arrays.asList(Constants.ALLOWED_HEADERS));
		corsConfiguration.setExposedHeaders(Arrays.asList(Constants.DEFAULT_CSRF_HEADER_NAME));
		allowedOrigins.add(allowedOrigin);
		allowedOrigins.add(allowedOriginLocal);
		allowedOrigins.add(allowedOriginPublicApp);
		allowedOrigins.add(allowedOriginUat);
		allowedOrigins.add(allowedOriginProd);
		corsConfiguration.setAllowedOrigins(allowedOrigins);
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;

	}


}
