package org.drools.repository;

/**
 * The main exception thrown by classes in this package. May contain an error message and/or another
 * nested exception.
 * 
 * @author btruitt
 */
public class RulesRepositoryException extends RuntimeException {

    /**
     * version id for serialization purposes
     */
    private static final long serialVersionUID = 400L;

    /**
     * Default constructor. constructs a RulesRepositoryException object with null as its detail 
     * message
     */
    public RulesRepositoryException() {
        //nothing extra
    }

    /**
     * Constructs a new instance of this class with the specified detail message.
     * 
     * @param message the message to set for the exception
     */
    public RulesRepositoryException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance of this class with the specified root cause.
     * 
     * @param rootCause root failure cause
     */
    public RulesRepositoryException(Throwable rootCause) {
        super(rootCause);
    }
    
    /**
     * Constructs a new instance of this class with the specified detail message and root cause.
     * 
     * @param message the message to set for the exception
     * @param rootCause root failure cause
     */
    public RulesRepositoryException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
