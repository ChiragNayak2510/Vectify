package com.example.Vectify.Controller;
import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

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

