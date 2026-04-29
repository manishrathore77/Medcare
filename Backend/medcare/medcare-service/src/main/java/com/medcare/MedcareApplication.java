package com.medcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Bootstrap for the Medcare Spring Boot application.
 * <p>
 * Component scanning is limited to {@code com.medcare.service} so this class can live
 * in the parent {@code com.medcare} package without pulling in unrelated beans.
 * </p>
 *
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 */
@SpringBootApplication(scanBasePackages = "com.medcare.service")
@EnableJpaAuditing
public class MedcareApplication {

    /**
     * Program entry point.
     *
     * @param args standard Spring Boot CLI arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MedcareApplication.class, args);
    }
}
