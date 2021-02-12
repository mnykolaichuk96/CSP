package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class CantDeleteCarModelException extends Exception {

    public CantDeleteCarModelException() {
        super();
    }


    public CantDeleteCarModelException(String message) {
        super(message);
    }


    public CantDeleteCarModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
