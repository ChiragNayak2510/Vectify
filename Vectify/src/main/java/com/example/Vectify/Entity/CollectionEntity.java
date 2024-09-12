package com.example.Vectify.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "collections")
public class CollectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(columnDefinition = "jsonb")
    private String data;

    // Getters and setters (or other methods) can be added here
}
