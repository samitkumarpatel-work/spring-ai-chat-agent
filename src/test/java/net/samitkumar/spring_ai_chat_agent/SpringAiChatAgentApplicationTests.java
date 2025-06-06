package net.samitkumar.spring_ai_chat_agent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringAiChatAgentApplicationTests {

	@Test
	void contextLoads() {
	}

}
