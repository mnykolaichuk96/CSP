package mnykolaichuk.prz.pracaDyplomowa.model.data;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCustomerData {

    @NotEmpty(message = "{mnykolaichuk.prz.ValidNotEmptyMessage}")
    private String description;

    @NotNull(message = "is required")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime creationDate;


    @NotNull(message = "is required")
    @Size(min = 1, message = "is required")
    private String cityName;

    private Integer orderId;

    private List<OrderAnswerData> orderAnswerDataList;

    private CarData carData;

    public OrderCustomerData() {
    }

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

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<OrderAnswerData> getOrderAnswerDataList() {
        return orderAnswerDataList;
    }

    public void setOrderAnswerDataList(List<OrderAnswerData> orderAnswerDataList) {
        this.orderAnswerDataList = orderAnswerDataList;
    }

    public CarData getCarData() {
        return carData;
    }

    public void setCarData(CarData carData) {
        this.carData = carData;
    }
}
