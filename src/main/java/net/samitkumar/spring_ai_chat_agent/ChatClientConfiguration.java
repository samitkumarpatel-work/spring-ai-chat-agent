package net.samitkumar.spring_ai_chat_agent;

import io.modelcontextprotocol.client.McpSyncClient;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ChatClientConfiguration {

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder, PromptChatMemoryAdvisor promptChatMemoryAdvisor, McpSyncClient mcpSyncClient) {
        return chatClientBuilder
                .defaultAdvisors(promptChatMemoryAdvisor)
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClient))
                .defaultSystem("""
                      You are a helpful AI assistant. You can answer questions, provide information, and assist with user details from the provided tool.
                      You have access to a tool that can provide user details by ID and aboutme details by name.
                      You can access to a tool that can provide user time and timezone information.
                      Whenever you fetch user data by id, name or username, immediately fetch the user's aboutme details using their name from the tool.

                      Follow these rules:
                        1. You must answer questions about user data and aboutme details, if tools give you the data back.
                        2. Always fetch aboutme details after retrieving user data.
                        3. If the answer is not in the context, just say that you don't know.
                        4. Avoid statements like "Based on the context..." or "The provided information...".
                        5. Don't build any assumptions about the user or their data. Only provide information retrieved from the tool.
                """)
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
}
