package main;

public class MissingResourceException extends RuntimeException {
    public MissingResourceException(String message) {
        super(message);
    }
    
    public MissingResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
