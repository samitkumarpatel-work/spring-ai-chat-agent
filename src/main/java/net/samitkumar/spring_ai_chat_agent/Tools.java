package net.samitkumar.spring_ai_chat_agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class Tools {

    private final Logger logger = LoggerFactory.getLogger(Tools.class);
    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final VectorStore vectorStore;

    public Tools(JsonPlaceholderClient jsonPlaceholderClient, VectorStore vectorStore) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
        this.vectorStore = vectorStore;
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

    @Tool(description = "Fetch user aboutme by user full name")
    public List<Document> getAboutByFullName(String fullName) {
        logger.info("Fetching getAboutByFullName: {}", fullName);
        return vectorStore.similaritySearch(fullName)
                .stream()
                .filter(document -> {
                    var dInS = (String)document.getMetadata().get("fullname");
                    return Objects.nonNull(dInS) && dInS.matches("(?i).*" + fullName + ".*");
                })
                .toList();
    }

    @Tool(description = "Fetch user aboutme by user username")
    public List<Document> getAboutByUsername(String username) {
        logger.info("Fetching getAboutByUsername: {}", username);
        return vectorStore.similaritySearch(username)
                .stream()
                .filter(document -> {
                    var dInS = (String)document.getMetadata().get("username");
                    return Objects.nonNull(dInS) && dInS.matches("(?i).*" + username + ".*");
                })
                .toList();
    }
}
