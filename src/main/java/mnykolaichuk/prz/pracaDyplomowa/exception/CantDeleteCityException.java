package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class CantDeleteCityException extends Exception {

    public CantDeleteCityException() {
        super();
    }


    public CantDeleteCityException(String message) {
        super(message);
    }


    public CantDeleteCityException(String message, Throwable cause) {
        super(message, cause);
    }
}
