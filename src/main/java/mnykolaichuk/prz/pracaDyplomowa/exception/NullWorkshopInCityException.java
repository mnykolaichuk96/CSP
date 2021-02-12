package mnykolaichuk.prz.pracaDyplomowa.exception;

public class NullWorkshopInCityException extends Exception {

    public NullWorkshopInCityException() {
        super();
    }


    public NullWorkshopInCityException(String message) {
        super(message);
    }


    public NullWorkshopInCityException(String message, Throwable cause) {
        super(message, cause);
    }
}
