package net.youssfi.authservice;

import net.youssfi.authservice.security.config.JwtTokenParams;
import net.youssfi.authservice.security.config.RsaKeyConfig;
import net.youssfi.authservice.security.dtos.RegistrationRequestDTO;
import net.youssfi.authservice.security.enums.AccountStatus;
import net.youssfi.authservice.security.enums.Gender;
import net.youssfi.authservice.security.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyConfig.class, JwtTokenParams.class})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //@Bean
    CommandLineRunner commandLineRunner(AuthService authService){
        return args -> {
            authService.addRole("USER");
            authService.addRole("TEACHER");
            authService.addRole("STUDENT");
            authService.addRole("ADMIN");
            authService.register(new RegistrationRequestDTO("admin","Youssfi","Mohamed","m.youssfi@enset-media.ac.ma","1234","1234", Gender.MALE),true);
            authService.register(new RegistrationRequestDTO("prof1","YOUSSFI","Mohamed","med@youssfi.net","1234","1234", Gender.MALE),true);
            authService.register(new RegistrationRequestDTO("prof2","DAAIF","Aziz","daaif@gmail.com","1234","1234", Gender.MALE),true);
            authService.register(new RegistrationRequestDTO("student1","IBRAHIMI","Imane","ibrahimi@gmail.com","1234","1234", Gender.FEMALE),true);
            authService.register(new RegistrationRequestDTO("student2","CHIHAB","ILYAS","chihab@gmail.com","1234","1234", Gender.MALE),true);
            authService.register(new RegistrationRequestDTO("student3","NADIR","INES","nadir@gmail.com","1234","1234", Gender.MALE),true);
            authService.addRoleToUser("admin","ADMIN", false);
        };
    }
}
