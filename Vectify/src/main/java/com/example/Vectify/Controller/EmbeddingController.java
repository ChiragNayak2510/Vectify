package com.example.Vectify.Controller;

import com.cohere.api.Cohere;
import com.cohere.api.requests.EmbedRequest;
import com.cohere.api.types.EmbedResponse;
import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Repository.CollectionRepository;
import com.example.Vectify.Request.VectorRequest;
import com.example.Vectify.Service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    @Autowired
    private Cohere cohereClient;
    @Autowired
    CollectionService collectionService;

    @Autowired
    CollectionRepository collectionRepository;
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

    @PostMapping("/vector")
    public ResponseEntity<CollectionEntity> createVectorAttribute(@RequestBody VectorRequest request) {
        Long id = request.getId();
        String attribute = request.getAttribute();

        // Step 1: Get the list of strings based on the attribute key from the service
        List<String> texts = collectionService.getAttributeValuesByKey(id, attribute);

        // Step 2: Generate vector embeddings using the Cohere API
        EmbedResponse embedResponse = cohereClient.embed(EmbedRequest.builder()
                .texts(texts)
                .model("embed-english-v3.0")
                .inputType(com.cohere.api.types.EmbedInputType.CLASSIFICATION)
                .build());

        List<float[]> embeddings = Arrays.asList(new float[]{1.2f, 2.4f}, new float[]{3.6f, 7.2f}); // Placeholder

        // Step 3: Retrieve the collection entity to update
        CollectionEntity collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        // Step 4: Update the ObjectEntity embeddings with the generated vectors
        int i = 0;
        for (ObjectEntity object : collection.getObjects()) {
            String attributeValue = object.getAttributes().get(attribute);
            if (attributeValue != null && i < embeddings.size()) {
                object.setEmbedding(embeddings.get(i)); // Update embedding
                object.setEmbeddingKey(attribute); // Set the embedding key
                i++;
            }
        }

        // Step 5: Save the updated collection
        collectionRepository.save(collection);

        // Step 6: Return the updated collection
        return ResponseEntity.ok(collection);
    }
}

