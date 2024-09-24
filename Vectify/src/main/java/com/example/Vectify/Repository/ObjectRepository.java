package com.example.Vectify.Repository;

import com.example.Vectify.Entity.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {

    @Query(value = "SELECT o.* FROM objects o " +
            "JOIN object_attributes oa ON o.id = oa.object_id " +
            "WHERE oa.attribute_key = :attribute " +
            "AND o.collection_id = :collectionId " +  // Filter by collection ID
            "AND o.embedding IS NOT NULL " +  // Ensure embedding is not null
            "ORDER BY dot_product(o.embedding, :embedding) DESC " +
            "LIMIT 1", nativeQuery = true)
    List<ObjectEntity> findClosestEmbedding(@Param("attribute") String attribute,
                                            @Param("embedding") double[] embedding,
                                            @Param("collectionId") Long collectionId);
}

