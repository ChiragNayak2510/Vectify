package com.example.Vectify.Service;

import com.example.Vectify.Entity.CollectionEntity;
import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Entity.UserEntity;
import com.example.Vectify.Repository.CollectionRepository;
import com.example.Vectify.Repository.ObjectRepository;
import com.example.Vectify.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        // Save the collection
        CollectionEntity savedCollection = collectionRepository.save(collection);

        // Save associated objects
        for (ObjectEntity object : collection.getObjects()) {
            objectRepository.save(object);
        }

        return savedCollection;
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
}
