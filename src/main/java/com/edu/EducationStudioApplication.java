package com.edu;

import com.edu.config.FileStorageProperties;
import com.edu.config.ImageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class, ImageProperties.class})
public class EducationStudioApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(EducationStudioApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(EducationStudioApplication.class);
	}
}
