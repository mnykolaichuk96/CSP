package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.Car;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import org.springframework.stereotype.Service;

@Service
public interface SecureTokenService {

    public SecureToken createSecureToken();
    public SecureToken createSecureTokenForCar(Car car, Customer fromCustomer);
    public void saveSecureToken(SecureToken token);
    public SecureToken findByToken(String token);
    SecureToken findByCustomerDetail(CustomerDetail customerDetail);
    public void removeToken(SecureToken token);
}
