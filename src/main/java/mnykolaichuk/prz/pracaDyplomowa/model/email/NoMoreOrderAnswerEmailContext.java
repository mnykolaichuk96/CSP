package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;

public class NoMoreOrderAnswerEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderWorkshopData orderWorkshopData = (OrderWorkshopData) context;
        put("orderWorkshopData", orderWorkshopData);
        put("customerDetailData", orderWorkshopData.getCustomerDetailData());
        setTo(orderWorkshopData.getCustomerDetailData().getEmail());

        setTemplateLocation("email/null-order-answer");
        setSubject("Nie ma ofert od warsztatów samochodowych");
    }

    public void setInformationUrl(final String showCompletedOrderListUrl){
        put("showCompletedOrderListUrl", showCompletedOrderListUrl);
    }

}