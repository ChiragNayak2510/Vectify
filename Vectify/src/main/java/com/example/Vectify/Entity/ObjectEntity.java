package com.example.Vectify.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Map;

@Entity
@Table(name = "objects")
public class ObjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Attributes stored as a key-value map in a separate table 'object_attributes'
    @ElementCollection
    @CollectionTable(name = "object_attributes", joinColumns = @JoinColumn(name = "object_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;

    // Embedding stored as a double precision array in PostgreSQL
    @Column(name = "embedding", columnDefinition = "double precision[]")
    private double[] embedding;

    @Column(name = "embedding_key")
    private String embeddingKey;

    // Many-to-one relationship with CollectionEntity
    @ManyToOne
    @JoinColumn(name = "collection_id", nullable = false)
    @JsonBackReference
    private CollectionEntity collection;

    // Default constructor
    public ObjectEntity() {}

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }

    public String getEmbeddingKey() {
        return embeddingKey;
    }

    public void setEmbeddingKey(String embeddingKey) {
        this.embeddingKey = embeddingKey;
    }

    public CollectionEntity getCollection() {
        return collection;
    }

    public void setCollection(CollectionEntity collection) {
        this.collection = collection;
    }
}
