package com.library.domain.model;

/**
 * Represents a CD in the library system
 * @author Your Name
 * @version 1.0
 */
public class CD {
    private String id;
    private String title;
    private String artist;
    private boolean available;

    public CD(String id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.available = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}