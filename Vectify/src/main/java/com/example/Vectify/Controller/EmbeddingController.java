package com.example.Vectify.Controller;

import com.cohere.api.Cohere;
import com.cohere.api.requests.EmbedRequest;
import com.cohere.api.types.EmbedFloatsResponse;
import com.cohere.api.types.EmbedResponse;
import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Repository.CollectionRepository;
import com.example.Vectify.Request.VectorRequest;
import com.example.Vectify.Service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    @Autowired
    private Cohere cohereClient;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private CollectionRepository collectionRepository;

    @PostMapping("/generate")
    public EmbedResponse generateEmbeddings(@RequestBody List<String> texts) {
        try {
            return cohereClient.embed(EmbedRequest.builder()
                    .texts(texts)
                    .model("embed-english-v3.0")
                    .inputType(com.cohere.api.types.EmbedInputType.CLASSIFICATION)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embeddings", e);
        }
    }

    @PostMapping("/vector")
    public ResponseEntity<CollectionEntity> createVectorAttribute(@RequestBody VectorRequest request) {
        Long id = request.getId();
        String attribute = request.getAttribute();

        List<String> texts = collectionService.getAttributeValuesByKey(id, attribute);

        EmbedResponse embedResponse = cohereClient.embed(EmbedRequest.builder()
                .texts(texts)
                .model("embed-english-v3.0")
                .inputType(com.cohere.api.types.EmbedInputType.CLASSIFICATION)
                .build());
        System.out.println(embedResponse);

        Optional<EmbedFloatsResponse> optionalEmbeddings = embedResponse.getEmbeddingsFloats();
        if (optionalEmbeddings.isEmpty()) {
            throw new RuntimeException("No embeddings returned by the Cohere API");
        }

        EmbedFloatsResponse embedFloatsResponse = optionalEmbeddings.get();
        List<List<Double>> embeddingsList = embedFloatsResponse.getEmbeddings();

        // Convert List<List<Double>> to double[][]
        double[][] embeddings = new double[embeddingsList.size()][];
        for (int i = 0; i < embeddingsList.size(); i++) {
            List<Double> innerList = embeddingsList.get(i);
            embeddings[i] = new double[innerList.size()];
            for (int j = 0; j < innerList.size(); j++) {
                embeddings[i][j] = innerList.get(j);
            }
        }

        CollectionEntity collection = collectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collection not found"));
        int i = 0;
        for (ObjectEntity object : collection.getObjects()) {
            String attributeValue = object.getAttributes().get(attribute);
            if (attributeValue != null && i < embeddings.length) {
                object.setEmbedding(embeddings[i]);
                object.setEmbeddingKey(attribute);
                i++;
            }
        }

        collectionRepository.save(collection);

        return ResponseEntity.ok(collection);
    }
}
