package com.swotitup.secops;

import com.swotitup.secops.model.Customers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SwotitupSecopsApplication {

	private static final Logger log = LoggerFactory.getLogger(SwotitupSecopsApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SwotitupSecopsApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(CustomerRepository repository) {
		log.info("StartApplication...");
		return args -> {
			repository.save(new Customers("uday","kumar",22));
			repository.save(new Customers("Chris","alle",29));
			repository.save(new Customers("Roya","Smith",36));
		};
	}
}
