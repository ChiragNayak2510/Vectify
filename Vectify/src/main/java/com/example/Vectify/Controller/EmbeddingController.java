package com.example.Vectify.Controller;

import com.cohere.api.Cohere;
import com.cohere.api.requests.EmbedRequest;
import com.cohere.api.types.EmbedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    @Autowired
    private Cohere cohereClient;

    @PostMapping("/generate")
    public EmbedResponse generateEmbeddings(@RequestBody List<String> texts) {
        try {

            return cohereClient.embed(EmbedRequest.builder()
                    .texts(texts)
                    .model("embed-english-v3.0")
                    .inputType(com.cohere.api.types.EmbedInputType.CLASSIFICATION)
                    .build());
        } catch (Exception e) {
            // Handle any potential errors
            throw new RuntimeException("Failed to generate embeddings", e);
        }
    }
}

