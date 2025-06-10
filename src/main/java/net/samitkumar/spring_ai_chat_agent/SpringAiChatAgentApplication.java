package net.samitkumar.spring_ai_chat_agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;

@SpringBootApplication
public class SpringAiChatAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiChatAgentApplication.class, args);
	}

	@Bean
	CorsConfiguration corsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.addAllowedOriginPattern("*");
		corsConfiguration.addAllowedMethod("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.setAllowCredentials(true);
		return corsConfiguration;
	}
}
