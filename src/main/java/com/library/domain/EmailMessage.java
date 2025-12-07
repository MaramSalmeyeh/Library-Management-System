package com.library.domain;

/**
 * Represents an email message used by the library notification system.
 * <p>
 * An {@code EmailMessage} contains a recipient address, a subject line,
 * and the message body. It is typically used for sending reminders
 * or notifications to users.
 * </p>
 *
 * @author Maram
 * @version 1.0
 */
public class EmailMessage {

    /**
     * The recipient's email address.
     */
    private final String to;

    /**
     * The subject line of the email.
     */
    private final String subject;

    /**
     * The content of the email message.
     */
    private final String body;

    /**
     * Creates a new EmailMessage with the specified information.
     *
     * @param to      the recipient's email address
     * @param subject the email subject
     * @param body    the body of the email
     */
    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /**
     * Gets the recipient's email address.
     *
     * @return the email address
     */
    public String getTo() {
        return to;
    }

    /**
     * Gets the email subject.
     *
     * @return the subject line
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the message body.
     *
     * @return the email content
     */
    public String getBody() {
        return body;
    }
}
