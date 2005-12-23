package org.drools.repo;

public class RuleRepositoryException extends RuntimeException
{
    public RuleRepositoryException(Throwable cause) {
        super(cause);
    }
    
    public RuleRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RuleRepositoryException(String message) {
        super(message);
    }

}
