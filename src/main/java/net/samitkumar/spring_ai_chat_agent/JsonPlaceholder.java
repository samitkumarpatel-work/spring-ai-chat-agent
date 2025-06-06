package net.samitkumar.spring_ai_chat_agent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class JsonPlaceholder {

    @Bean
    JsonPlaceholderClient jsonPlaceholderClient(WebClient.Builder webClientBuilder) {
        WebClientAdapter adapter = WebClientAdapter
                .create(webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build());
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(JsonPlaceholderClient.class);
    }
}

record User(String id, String name, String username, String email, Address address, String phone, String website, Company company) {
    record Address(String street, String suite, String city, String zipcode, Geo geo) {
        record Geo(String lat, String lng) {}
    }
    record Company(String name, String catchPhrase, String bs) {}
}

@HttpExchange(accept = "application/json")
interface JsonPlaceholderClient {
    @GetExchange("/users")
    List<User> allUsers();

    @GetExchange("/users/{id}")
    User userById(@PathVariable String id);
}
