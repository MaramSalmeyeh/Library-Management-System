package com.library.domain;

/**
 * Represents a CD media item in the library system.
 * <p>
 * A CD contains an ID, title, artist name, and a flag indicating
 * whether the CD is currently borrowed by a user.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class CD {

    /** Unique identifier for the CD. */
    private final String id;

    /** Title of the CD (album or recording). */
    private final String title;

    /** Name of the artist who produced the CD. */
    private final String artist;

    /** Indicates whether the CD is currently borrowed. */
    private boolean borrowed;

    /**
     * Constructs a new CD with the given properties.
     *
     * @param id       unique identifier for the CD
     * @param title    the title of the CD
     * @param artist   the artist or performer of the CD
     * @param borrowed whether the CD is currently borrowed
     */
    public CD(String id, String title, String artist, boolean borrowed) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.borrowed = borrowed;
    }

    /**
     * Returns the unique ID of the CD.
     *
     * @return CD ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the CD.
     *
     * @return CD title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the artist associated with the CD.
     *
     * @return artist name
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Checks whether the CD has been borrowed.
     *
     * @return true if the CD is borrowed, false otherwise
     */
    public boolean isBorrowed() {
        return borrowed;
    }

    /**
     * Updates the borrowed status of the CD.
     *
     * @param borrowed true to mark the CD as borrowed, false to mark it available
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }
}
