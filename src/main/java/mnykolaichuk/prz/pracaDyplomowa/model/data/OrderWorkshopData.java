package mnykolaichuk.prz.pracaDyplomowa.model.data;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class OrderWorkshopData {

    @NotEmpty(message = "{mnykolaichuk.prz.ValidNotEmptyMessage}")
    private String description;

    @NotNull(message = "is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime creationDate;

    @NotNull(message = "is required")
    private String cityName;

    private Integer orderAnswerId;

    private OrderAnswerData orderAnswerData;

    private CustomerDetailData customerDetailData;

    private CarData carData;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getOrderAnswerId() {
        return orderAnswerId;
    }

    public void setOrderAnswerId(Integer orderAnswerId) {
        this.orderAnswerId = orderAnswerId;
    }

    public OrderAnswerData getOrderAnswerData() {
        return orderAnswerData;
    }

    public void setOrderAnswerData(OrderAnswerData orderAnswerData) {
        this.orderAnswerData = orderAnswerData;
    }

    public CustomerDetailData getCustomerDetailData() {
        return customerDetailData;
    }

    public void setCustomerDetailData(CustomerDetailData customerDetailData) {
        this.customerDetailData = customerDetailData;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }

}
