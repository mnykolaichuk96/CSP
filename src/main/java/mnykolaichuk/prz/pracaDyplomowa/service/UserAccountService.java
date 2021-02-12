package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UnknowIdentifierException;

public interface UserAccountService {

    void forgottenPassword(String username) throws UnknowIdentifierException;
    void updatePassword(String password, String token) throws InvalidTokenException, UnknowIdentifierException;
    boolean comparePassword(String password, String encodePassword);
}
