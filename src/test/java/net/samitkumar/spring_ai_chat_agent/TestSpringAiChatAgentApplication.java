package net.samitkumar.spring_ai_chat_agent;

import org.springframework.boot.SpringApplication;

public class TestSpringAiChatAgentApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringAiChatAgentApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
