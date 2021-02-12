package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import mnykolaichuk.prz.pracaDyplomowa.validation.CompositeKey;

import javax.persistence.*;

@Entity
@Table(name = "customer_car")
@IdClass(CompositeKey.class)
public class CustomerCar {

    @Id
    @Column(name = "customer_id")
    private Integer customerId;

    @Id
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "car_verified")
    private Integer carVerified;        // 0 - false; 1 - true; !0 - false

    public CustomerCar() {
    }

    public CustomerCar(Integer customerId, Integer carId, Integer carVerified) {
        this.customerId = customerId;
        this.carId = carId;
        this.carVerified = carVerified;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getCarVerified() {
        return carVerified;
    }

    public void setCarVerified(Integer accountVerified) {
            this.carVerified = accountVerified;
    }
}


