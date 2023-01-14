package net.youssfi.coursesservice;

import net.youssfi.coursesservice.security.RsaKeyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfig.class)
@EnableFeignClients
public class CoursesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoursesServiceApplication.class, args);
    }

}
