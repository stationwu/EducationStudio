package com.edu;

import com.edu.config.FileStorageProperties;
import com.edu.config.ImageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class, ImageProperties.class})
public class EducationStudioApplication {

	public static void main(String[] args) {
		SpringApplication.run(EducationStudioApplication.class, args);
	}
}
