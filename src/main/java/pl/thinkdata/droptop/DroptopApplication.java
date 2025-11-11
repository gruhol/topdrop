package pl.thinkdata.droptop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DroptopApplication {

	public static void main(String[] args) {
		SpringApplication.run(DroptopApplication.class, args);
	}

}
