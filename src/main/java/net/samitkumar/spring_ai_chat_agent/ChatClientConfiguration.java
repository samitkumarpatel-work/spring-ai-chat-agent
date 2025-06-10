package net.samitkumar.spring_ai_chat_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ChatClientConfiguration {
    private final Tools tools;

    public ChatClientConfiguration(Tools tools) {
        this.tools = tools;
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder, PromptChatMemoryAdvisor promptChatMemoryAdvisor, VectorStore vectorStore) {
        return chatClientBuilder
                .defaultAdvisors(promptChatMemoryAdvisor/*, new QuestionAnswerAdvisor(vectorStore)*/)
                .defaultTools(tools)
                .defaultSystem("""
                      You are a helpful AI assistant. You can answer questions, provide information, and assist with user details from the provided tool by id or name or username.
                      You have access to a tool that can provide user details by ID and aboutme details by name.
                      Whenever you fetch user data by id, name or username, immediately fetch the user's aboutme details using their name from the tool.

                      Follow these rules:
                        1. You must answer questions about user data and aboutme details.
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
