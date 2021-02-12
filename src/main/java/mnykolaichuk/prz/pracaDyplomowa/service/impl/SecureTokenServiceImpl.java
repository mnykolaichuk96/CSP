package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.SecureTokenRepository;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Car;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.service.SecureTokenService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Service
public class SecureTokenServiceImpl implements SecureTokenService {

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Value("${jdj.secure.token.validity}")
    private int tokenValidityInSeconds;

    @Autowired
    SecureTokenRepository secureTokenRepository;

    @Override
    public SecureToken createSecureToken() {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
        SecureToken secureToken = new SecureToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));  //токен буде дійсний 8 годин
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    @Override
    public SecureToken createSecureTokenForCar(Car car, Customer sourceCustomer) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()));
        SecureToken secureToken = new SecureToken();
        secureToken.setVerificationCarId(car.getId());
        secureToken.setVerificationCarCustomerId(sourceCustomer.getId());
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));  //токен буде дійсний 8 годин
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    @Override
    public void saveSecureToken(SecureToken token) {
        secureTokenRepository.save(token);
    }

    @Override
    public SecureToken findByToken(String token) {
        return secureTokenRepository.findByToken(token);
    }

    @Override
    public void removeToken(SecureToken token) {
        token.setCustomerDetail(null);
        token.setWorkshop(null);
        secureTokenRepository.deleteSecureTokenById(token.getId());
    }

    @Override
    public SecureToken findByCustomerDetail(CustomerDetail customerDetail) {
        try {
            return secureTokenRepository.findByCustomerDetail(customerDetail);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }
}
