package net.samitkumar.spring_ai_chat_agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Tools {

    private final Logger logger = LoggerFactory.getLogger(Tools.class);
    private final JsonPlaceholderClient jsonPlaceholderClient;

    public Tools(JsonPlaceholderClient jsonPlaceholderClient) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
    }

    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Fetch all users")
    public List<User> getAllUsers() {
        logger.info("Fetching all users from JSON Placeholder API");
        return jsonPlaceholderClient.allUsers();
    }

    @Tool(description = "Fetch user by ID")
    public User getUserById(String id) {
        logger.info("Fetching user with ID: {}", id);
        return jsonPlaceholderClient.userById(id);
    }
}
