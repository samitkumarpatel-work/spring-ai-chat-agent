package net.samitkumar.spring_ai_chat_agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocket {

    @Bean
    public HandlerMapping handlerMapping(PromptHandler promptHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/chat", promptHandler);
        int order = -1; // before annotated controllers
        return new SimpleUrlHandlerMapping(map, order);
    }

}

@Component
class PromptHandler implements WebSocketHandler {
    private final ChatClient chatClient;
    private final Logger log = LoggerFactory.getLogger(PromptHandler.class);

    public PromptHandler(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                session
                        .receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(message -> log.info("Prompt message: {}", message))
                        .flatMap(message -> {
                            try {
                                var parsableMessage = new ObjectMapper().readValue(message, Map.class);
                                System.out.println("###Parsed message: " + parsableMessage);
                                return chatClient
                                        .prompt()
                                        .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, parsableMessage.get("contextId")))
                                        .user((String)parsableMessage.get("message"))
                                        .stream()
                                        .content()
                                        .map(session::textMessage);
                            } catch (JsonProcessingException e) {
                                return Flux.error(new RuntimeException(e));
                            }
                        })
        );
    }
}
