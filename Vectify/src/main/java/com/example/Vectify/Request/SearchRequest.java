package com.example.Vectify.Request;

public class SearchRequest {
    private String attribute;
    private String word;
    private Long collectionId; // Add collectionId

    // Constructors
    public SearchRequest() {}

    public SearchRequest(String attribute, String word, Long collectionId) {
        this.attribute = attribute;
        this.word = word;
        this.collectionId = collectionId;
    }

    // Getters and Setters
    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }
}
