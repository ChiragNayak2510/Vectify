package com.example.Vectify.Repository;

import com.example.Vectify.Entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionEntity, Long> {
    // Define any custom query methods if needed
}
