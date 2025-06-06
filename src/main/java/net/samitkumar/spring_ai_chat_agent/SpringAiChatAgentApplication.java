package net.samitkumar.spring_ai_chat_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;

import javax.sql.DataSource;

@SpringBootApplication
public class SpringAiChatAgentApplication {

	private final Tools tools;

	public SpringAiChatAgentApplication(Tools tools) {
		this.tools = tools;
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringAiChatAgentApplication.class, args);
	}

	@Bean
	ChatClient chatClient(ChatClient.Builder chatClientBuilder, PromptChatMemoryAdvisor promptChatMemoryAdvisor) {
		return chatClientBuilder
				.defaultAdvisors(promptChatMemoryAdvisor)
				.defaultTools(tools)
				.build();
	}

	@Bean
	PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
		var jdbc = JdbcChatMemoryRepository
				.builder()
				.dataSource(dataSource)
				.build();

		var chatMessageWindow = MessageWindowChatMemory
				.builder()
				.chatMemoryRepository(jdbc)
				.build();

		return PromptChatMemoryAdvisor
				.builder(chatMessageWindow)
				.build();
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
