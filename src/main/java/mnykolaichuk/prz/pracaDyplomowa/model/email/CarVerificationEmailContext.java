package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import org.springframework.web.util.UriComponentsBuilder;

public class CarVerificationEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context1, T context2, T context3){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        CustomerDetailData sourceCustomerDetailData = (CustomerDetailData) context1; // we pass the customer informati
        CustomerDetailData targetCustomerDetailData = (CustomerDetailData) context2;
        CarData carData = (CarData) context3;
        put("sourceCustomerDetailData", sourceCustomerDetailData);
        put("targetCustomerDetailData", targetCustomerDetailData);
        put("carData", carData);

        setTo(targetCustomerDetailData.getEmail());
        setTemplateLocation("email/car-verification");
        setSubject("Dodanie samochód z listy");
    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/verify/car").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}
