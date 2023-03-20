package com.carrot.nara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CarrotNaraApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarrotNaraApplication.class, args);
	}

}
