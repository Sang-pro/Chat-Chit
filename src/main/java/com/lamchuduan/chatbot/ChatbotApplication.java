package com.lamchuduan.chatbot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class ChatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotApplication.class, args);
	}

	@Bean
	CommandLineRunner logStartUp() {
		return args -> {
			log.info("=================================================");
			log.info("Chatbot application started successfully.");
			log.info("You can start chatting with your AI model.");
			log.info("=================================================");
			log.info("Basic API endpoints are available at: http://localhost:8081/api/chat");
			log.info("For more information, visit the documentation at: http://localhost:8081/docs");
		};
	}
}
