package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import org.springframework.web.util.UriComponentsBuilder;

public class WorkshopAnswerUnregisteredEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderWorkshopData orderWorkshopData = (OrderWorkshopData) context; // we pass the customer informati
        put("orderWorkshopData", orderWorkshopData);
        put("customerDetailData", orderWorkshopData.getCustomerDetailData());
        put("workshopData", orderWorkshopData.getOrderAnswerData().getWorkshopData());
        setTo(orderWorkshopData.getCustomerDetailData().getEmail());

        setTemplateLocation("email/workshop-answer-unregistered-information");
        setSubject("Nowa oferta na zlecenie");
    }
    public void setToken(String token) {
        put("token", token);
    }

    public void buildOfferChooseUrl(final String baseURL, final String token){
        final String url= UriComponentsBuilder.fromHttpUrl(baseURL)
                .path("/verify/offerChoose").queryParam("token", token).toUriString();
        put("offerChooseUrl", url);
    }

}