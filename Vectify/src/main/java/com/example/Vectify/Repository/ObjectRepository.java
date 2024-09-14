package com.example.Vectify.Repository;

import com.example.Vectify.Entity.ObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObjectRepository extends JpaRepository<ObjectEntity, Long> {
}
