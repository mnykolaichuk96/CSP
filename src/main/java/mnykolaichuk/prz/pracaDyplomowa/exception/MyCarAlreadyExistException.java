package mnykolaichuk.prz.pracaDyplomowa.exception;

/**
 * Exception thrown by system in case some one try to register with already exisiting email
 * id in the system.
 */
public class MyCarAlreadyExistException extends Exception {

    public MyCarAlreadyExistException() {
        super();
    }


    public MyCarAlreadyExistException(String message) {
        super(message);
    }


    public MyCarAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
