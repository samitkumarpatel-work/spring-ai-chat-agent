package net.samitkumar.spring_ai_chat_agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
public class ChatClientRouter {
    private final ChatClient chatClient;
    private final ChatMemoryRepository chatMemoryRepository;

    public ChatClientRouter(ChatClient chatClient, JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        this.chatClient = chatClient;
        this.chatMemoryRepository = jdbcChatMemoryRepository;
    }

    @GetMapping("{userId}/chat")
    @ResponseBody
    Flux<String> chatController(@PathVariable String userId, @RequestParam String prompt) {
        return
                chatClient
                        .prompt()
                        .user(prompt)
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, userId))
                        .stream()
                        .content();
    }

    @GetMapping("/chat-history")
    @ResponseBody
    @CrossOrigin
    List<String> chatHistoryController() {
        return chatMemoryRepository.findConversationIds();
    }

    @GetMapping("/chat-history/{sessionId}")
    @ResponseBody
    @CrossOrigin
    List<Message> chatHistoryControllerById(@PathVariable String sessionId) {
        return chatMemoryRepository.findByConversationId(sessionId);
    }

}