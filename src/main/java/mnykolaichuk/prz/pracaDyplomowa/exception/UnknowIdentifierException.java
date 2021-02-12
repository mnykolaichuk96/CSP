package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class UnknowIdentifierException extends Exception {

    public UnknowIdentifierException() {
        super();
    }


    public UnknowIdentifierException(String message) {
        super(message);
    }


    public UnknowIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
