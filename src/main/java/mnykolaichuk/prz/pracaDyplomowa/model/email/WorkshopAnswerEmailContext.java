package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;

public class WorkshopAnswerEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderWorkshopData orderWorkshopData = (OrderWorkshopData) context; // we pass the customer informati
        put("orderWorkshopData", orderWorkshopData);
        put("customerDetailData", orderWorkshopData.getCustomerDetailData());
        put("workshopData", orderWorkshopData.getOrderAnswerData().getWorkshopData());
        setTo(orderWorkshopData.getCustomerDetailData().getEmail());

        setTemplateLocation("email/workshop-answer-information");
        setSubject("Nowa oferta na zlecenie");
    }

    public void setInformationUrl(final String showOrderListUrl){
        put("showOrderListUrl", showOrderListUrl);
    }

}