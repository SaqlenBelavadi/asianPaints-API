package com.speridian.asianpaints.evp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AsianPaintsEvpApplication extends SpringBootServletInitializer	 {
	
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AsianPaintsEvpApplication.class);
    }
	

	public static void main(String[] args) {
		SpringApplication.run(AsianPaintsEvpApplication.class, args);

	}

}
