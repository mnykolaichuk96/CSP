package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class CantDeleteWorkshopWhileImplementationExistException extends Exception {

    public CantDeleteWorkshopWhileImplementationExistException() {
        super();
    }


    public CantDeleteWorkshopWhileImplementationExistException(String message) {
        super(message);
    }


    public CantDeleteWorkshopWhileImplementationExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
