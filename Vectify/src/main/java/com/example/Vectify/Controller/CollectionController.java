package com.example.Vectify.Controller;

import com.cohere.api.Cohere;
import com.cohere.api.requests.EmbedRequest;
import com.cohere.api.types.EmbedFloatsResponse;
import com.cohere.api.types.EmbedResponse;
import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Request.SearchRequest;
import com.example.Vectify.Service.CollectionService;
import com.example.Vectify.Service.VectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private VectorService vectorService;

    @Autowired
    private Cohere cohereClient;

    // Get all collections
    @GetMapping
    public List<CollectionEntity> getAllCollections() {
        return collectionService.getAllCollections();
    }

    // Get a collection by ID
    @GetMapping("/{id}")
    public ResponseEntity<CollectionEntity> getCollectionById(@PathVariable Long id) {
        return collectionService.getCollectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new collection
    @PostMapping
    public CollectionEntity createCollection(@RequestBody CollectionEntity collection) {
        return collectionService.createCollection(collection);
    }

    @GetMapping("/search")
    public List<ObjectEntity> vectorSearch(@RequestBody SearchRequest searchRequest) {
        String attribute = searchRequest.getAttribute();
        String word = searchRequest.getWord();
        Long collectionId = searchRequest.getCollectionId();

        EmbedResponse embedResponse = cohereClient.embed(EmbedRequest.builder()
                .texts(List.of(word))
                .model("embed-english-v3.0")
                .inputType(com.cohere.api.types.EmbedInputType.CLASSIFICATION)
                .build());

        List<Double> wordEmbeddingList = embedResponse.getEmbeddingsFloats()
                .orElseThrow(() -> new RuntimeException("No embeddings returned by the Cohere API"))
                .getEmbeddings().get(0);

        double[] wordEmbedding = wordEmbeddingList.stream().mapToDouble(Double::doubleValue).toArray();

        return vectorService.findClosestObjects(attribute, wordEmbedding, collectionId);
    }

    private static List<Double> getDoubles(EmbedResponse embedResponse) {
        Optional<EmbedFloatsResponse> optionalEmbeddings = embedResponse.getEmbeddingsFloats();

        // Step 2: Handle the case when no embeddings are returned by the Cohere API
        if (optionalEmbeddings.isEmpty() || optionalEmbeddings.get().getEmbeddings().isEmpty()) {
            throw new RuntimeException("No embeddings returned by the Cohere API");
        }

        EmbedFloatsResponse embedFloatsResponse = optionalEmbeddings.get();
        return embedFloatsResponse.getEmbeddings().get(0);
    }


    // Update an existing collection
    @PutMapping("/{id}")
    public ResponseEntity<CollectionEntity> updateCollection(@PathVariable Long id, @RequestBody CollectionEntity collection) {
        return collectionService.updateCollection(id, collection)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a collection
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        if (collectionService.deleteCollection(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
