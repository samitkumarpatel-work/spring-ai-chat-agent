package net.samitkumar.spring_ai_chat_agent;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
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

	@Bean
	McpSyncClient mcpSyncClient() {
		var mcp = McpClient
				.sync(HttpClientSseClientTransport.builder("http://localhost:8081").build()).build();
		mcp.initialize();
		return mcp;
	}
}
