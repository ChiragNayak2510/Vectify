package com.example.Vectify.Configuration;
import com.cohere.api.Cohere;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CohereConfiguration {

    @Value("${cohere.api.key}")
    private String apiKey;

    @Bean
    public Cohere cohereClient() {
        return Cohere.builder()
                .token(apiKey)
                .clientName("spring-boot-embed")
                .build();
    }
}
