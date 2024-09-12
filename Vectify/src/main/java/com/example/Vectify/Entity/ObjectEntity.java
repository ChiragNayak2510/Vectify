package com.example.Vectify.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "objects")
public class ObjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // This is where we store the vector embedding as an array of floats
    @Column(name = "embedding", columnDefinition = "float[]")
    private float[] embedding;

    @ManyToOne
    @JoinColumn(name = "collection_id")
    private CollectionEntity collection;

    // Getters and setters
}
