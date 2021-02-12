package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;

public class ImplementationEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderWorkshopData orderWorkshopData = (OrderWorkshopData) context; // we pass the customer informati
        put("orderWorkshopData", orderWorkshopData);
        put("workshopData", orderWorkshopData.getOrderAnswerData().getWorkshopData());
        put("customerDetailData", orderWorkshopData.getCustomerDetailData());
        setTo(orderWorkshopData.getOrderAnswerData().getWorkshopData().getEmail());

        setTemplateLocation("email/implementation-information");
        setSubject("Warsztat został wybrany do realizacji zlecenia");
    }

    public void setInformationUrl(final String showImplementationOrderListUrl){
        put("showImplementationOrderListUrl", showImplementationOrderListUrl);
    }

}