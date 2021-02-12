package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;

public class CompletedEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context1, T context2){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderWorkshopData orderWorkshopData = (OrderWorkshopData) context1; // we pass the customer informati
        WorkshopData workshopData = (WorkshopData) context2;
        put("orderWorkshopData", orderWorkshopData);
        put("customerDetailData", orderWorkshopData.getCustomerDetailData());
        put("workshopData", workshopData);
        setTo(orderWorkshopData.getCustomerDetailData().getEmail());

        setTemplateLocation("email/completed-information");
        setSubject("Zlecenie zrealizowane");
    }

    public void setInformationUrl(final String showCompletedOrderListUrl){
        put("showCompletedOrderListUrl", showCompletedOrderListUrl);
    }

}