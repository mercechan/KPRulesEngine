package org.kp.rulesengine.exception;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2218699054381288632L;
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
