package mnykolaichuk.prz.pracaDyplomowa.model.email;

import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderCustomerData;

public class InformationAboutAccountDeletingEmailContext extends AbstractEmailContext {

    @Override
    public <T> void init(T context1, T context2){
        //we can do any common configuration setup here
        // like setting up some base URL and context
        OrderCustomerData orderCustomerData = (OrderCustomerData) context1;
        CustomerDetailData customerDetailData = (CustomerDetailData) context2;
        put("orderCustomerData", orderCustomerData);
        put("workshopData", orderCustomerData.getOrderAnswerDataList().get(0).getWorkshopData());
        put("customerDetailData", customerDetailData);
        setTo(orderCustomerData.getOrderAnswerDataList().get(0).getWorkshopData().getEmail());
        setTemplateLocation("email/email-information-about-account-deleting");
        setSubject("Użytkownik usunął konto");
    }

    public void setInformationUrl(final String showImplementationOrderList){
        put("showImplementationOrderListUrl", showImplementationOrderList);
    }
}