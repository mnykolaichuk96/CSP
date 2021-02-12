package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class CarAlreadyHasOwnerException extends Exception {

    public CarAlreadyHasOwnerException() {
        super();
    }


    public CarAlreadyHasOwnerException(String message) {
        super(message);
    }


    public CarAlreadyHasOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
