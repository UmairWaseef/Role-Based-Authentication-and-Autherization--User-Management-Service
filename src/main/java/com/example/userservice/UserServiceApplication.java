package com.example.userservice;

import com.example.userservice.User.Role;
import com.example.userservice.User.Status;
import com.example.userservice.auth.AuthenticationService;
import com.example.userservice.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.example.userservice.User.Role.ADMIN;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			// Create a RegisterRequest object for the admin
			RegisterRequest admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("umairwaseef812@gmail.com")
					.password("adminpassword")
					.role(Role.ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());


		};


	}
}