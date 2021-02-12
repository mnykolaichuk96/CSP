package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.model.enums.Stan;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidImplementationDate;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPrice;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

public class OrderAnswerData {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ValidImplementationDate
    private LocalDate implementationDate;

    @Enumerated(EnumType.STRING)
    private Stan stan;

    private Integer OrderAnswerId;

    @ValidPrice
    private Double price;

    private WorkshopData workshopData;

    public LocalDate getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Stan getStan() {
        return stan;
    }

    public void setStan(Stan stan) {
        this.stan = stan;
    }

    public Integer getOrderAnswerId() {
        return OrderAnswerId;
    }

    public void setOrderAnswerId(Integer orderAnswerId) {
        OrderAnswerId = orderAnswerId;
    }

    public WorkshopData getWorkshopData() {
        return workshopData;
    }

    public void setWorkshopData(WorkshopData workshopData) {
        this.workshopData = workshopData;
    }

}
