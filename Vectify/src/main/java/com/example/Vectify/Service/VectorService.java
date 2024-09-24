package com.example.Vectify.Service;

import com.example.Vectify.Entity.ObjectEntity;
import com.example.Vectify.Repository.ObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VectorService {

    @Autowired
    private ObjectRepository objectRepository;

    // Method to find 10 closest objects based on dot product similarity
    public List<ObjectEntity> findClosestObjects(String attribute, double[] wordEmbedding, Long collectionId) {
        // Call the repository method and return the closest objects
        return objectRepository.findClosestEmbedding(attribute, wordEmbedding, collectionId);
    }



    // Helper method to convert double[] to a comma-separated string
    private String convertArrayToString(double[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(array[i]);
        }
        return sb.toString();
    }
}
