package com.example.Vectify.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collections")
public class CollectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference // Ignore this reference during serialization to avoid cycles
    private UserEntity user;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "collection")
    @JsonManagedReference
    private List<ObjectEntity> objects = new ArrayList<>();

    // Default constructor
    public CollectionEntity() {}

    // Parameterized constructor
    public CollectionEntity(Long id, String name, UserEntity user) {
        this.id = id;
        this.name = name;
        this.user = user;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<ObjectEntity> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectEntity> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        return "CollectionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", user=" + user +
                ", objects=" + objects +
                '}';
    }
}
