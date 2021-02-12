package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;
import org.springframework.web.util.UriComponentsBuilder;

public class AccountVerificationEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        if(isFromCustomerDetailData(context)) {
            CustomerDetailData customerDetailData = (CustomerDetailData) context; // we pass the customer informati
            put("name", customerDetailData.getFirstName() + " " + customerDetailData.getLastName());
            setTo(customerDetailData.getEmail());
        }
        else {
            WorkshopData workshopData = (WorkshopData) context; // we pass the customer informati
            put("name", workshopData.getWorkshopName());
            setTo(workshopData.getEmail());
        }
        // here we set thymeleaf .html for view mail
        setTemplateLocation("email/email-verification");
        setSubject("Weryfikacja poczty elektronicznej");
    }

    public void setToken(String token) {
        put("token", token);
    }

    public void buildVerificationUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/verify/user").queryParam("token", token).toUriString();
        put("verificationURL", url);
    }
}