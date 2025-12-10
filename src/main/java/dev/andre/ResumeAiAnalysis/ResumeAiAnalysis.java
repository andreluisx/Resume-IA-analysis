package dev.andre.ResumeAiAnalysis;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class ResumeAiAnalysis {

	public static void main(String[] args) {
		SpringApplication.run(ResumeAiAnalysis.class, args);
	}

}
