package com.library.domain;

/**
 * Represents a fine issued to a user for overdue library items.
 * <p>
 * A fine contains an ID, the user it belongs to, the amount owed,
 * and whether the fine has been paid.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class Fine {

    /**
     * Unique identifier for the fine.
     */
    private final String id;

    /**
     * The ID of the user who incurred the fine.
     */
    private final String userId;

    /**
     * The monetary amount of the fine.
     */
    private double amount;

    /**
     * Indicates whether the fine has been paid.
     */
    private boolean paid;

    /**
     * Creates a new Fine instance.
     *
     * @param id     the unique fine ID
     * @param userId the ID of the fined user
     * @param amount the fine amount
     * @param paid   whether the fine is already paid
     */
    public Fine(String id, String userId, double amount, boolean paid) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.paid = paid;
    }

    /**
     * Gets the fine ID.
     *
     * @return the fine ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the ID of the user who received the fine.
     *
     * @return the user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the fine amount.
     *
     * @return the amount owed
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Checks whether the fine has been paid.
     *
     * @return {@code true} if paid, otherwise {@code false}
     */
    public boolean isPaid() {
        return paid;
    }

    /**
     * Updates the fine amount.
     *
     * @param amount the new amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Marks the fine as paid or unpaid.
     *
     * @param paid new paid status
     */
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
