package com.example.Vectify.Service;

import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Entity.UserEntity;
import com.example.Vectify.Repository.CollectionRepository;
import com.example.Vectify.Repository.ObjectRepository;
import com.example.Vectify.Repository.UserRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectionService {

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ObjectRepository objectRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CollectionEntity> getAllCollections() {
        return collectionRepository.findAll();
    }

    public Optional<CollectionEntity> getCollectionById(Long id) {
        return collectionRepository.findById(id);
    }

    public CollectionEntity createCollection(CollectionEntity collection) {
        // Ensure the user is valid
        UserEntity user = collection.getUser();
        if (user != null) {
            user = userRepository.findById(user.getId()).orElse(null);
            if (user != null) {
                user.getCollections().add(collection);
                userRepository.save(user);
            }
        }

        // Set the collection reference for each object before saving the collection
        for (ObjectEntity object : collection.getObjects()) {
            object.setCollection(collection);  // Set the reference to the collection
        }

        return collectionRepository.save(collection);
    }

    public CollectionEntity createCollectionFromCsv(MultipartFile file, Long userId, String collectionName) throws Exception {
        List<ObjectEntity> objects = getObjects(file,userId,collectionName);
        CollectionEntity collection = new CollectionEntity();
        collection.setName(collectionName);

        // Set user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        collection.setUser(user);

        // Set the collection reference in each ObjectEntity
        for (ObjectEntity object : objects) {
            object.setCollection(collection);  // Important: Set the collection reference here
        }

        // Set objects in the collection
        collection.setObjects(objects);

        // Save collection and associated objects
        return collectionRepository.save(collection);
    }

    public List<ObjectEntity> getObjects(MultipartFile file, Long userId, String collectionName) throws Exception{
        List<ObjectEntity> objects = new ArrayList<>();
        // Read the CSV file from MultipartFile
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] headers = csvReader.readNext();
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                ObjectEntity object = new ObjectEntity();
                Map<String, String> attributes = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    if (i < nextLine.length) {
                        attributes.put(headers[i], nextLine[i]);
                    }
                }
                object.setAttributes(attributes);
                objects.add(object);
            }
        }
        return objects;
    }



    public Optional<CollectionEntity> updateCollection(Long id, CollectionEntity updatedCollection) {
        return collectionRepository.findById(id)
                .map(existingCollection -> {
                    existingCollection.setName(updatedCollection.getName());
                    existingCollection.setUser(updatedCollection.getUser());

                    // Update associated objects
                    for (ObjectEntity updatedObject : updatedCollection.getObjects()) {
                        if (updatedObject.getId() != null) {
                            objectRepository.findById(updatedObject.getId()).ifPresent(existingObject -> {
                                existingObject.setAttributes(updatedObject.getAttributes());
                                existingObject.setEmbedding(updatedObject.getEmbedding());
                                objectRepository.save(existingObject);
                            });
                        } else {
                            objectRepository.save(updatedObject);
                        }
                    }

                    return collectionRepository.save(existingCollection);
                });
    }

    public boolean deleteCollection(Long id) {
        return collectionRepository.findById(id)
                .map(collection -> {
                    UserEntity user = collection.getUser();
                    if (user != null) {
                        user.getCollections().remove(collection);
                        userRepository.save(user);
                    }

                    // Delete associated objects
                    for (ObjectEntity object : collection.getObjects()) {
                        objectRepository.deleteById(object.getId());
                    }

                    collectionRepository.deleteById(id);
                    return true;
                })
                .orElse(false);
    }
    public List<String> getAttributeValuesByKey(Long collectionId, String attributeKey) {
        // Fetch the collection by ID
        CollectionEntity collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        // Extract attribute values from each ObjectEntity based on the attribute key
        return collection.getObjects().stream()
                .map(object -> object.getAttributes().get(attributeKey)) // Get attribute value by key
                .filter(Objects::nonNull) // Ensure only non-null values are returned
                .collect(Collectors.toList());
    }
}
