package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import mnykolaichuk.prz.pracaDyplomowa.converter.YearAttributeConverter;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.EngineType;

import javax.persistence.*;
import java.time.Year;
import java.util.List;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    private Year year;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type")
    private EngineType engineType;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "vin")
    private String vin;

    @ManyToOne
    @JoinColumn(name = "car_model_id")
    private CarModel carModel;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "customer_car"
            , joinColumns = @JoinColumn(name = "car_id")
            , inverseJoinColumns = @JoinColumn(name = "customer_id"))
    private List<Customer> customers;

    @OneToMany(mappedBy = "car",
            fetch = FetchType.LAZY)
    private List<Order> orders;

    public Car() {
    }

    public Integer getId() {
        return id;
    }

    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public CarModel getCarModel() {
        return carModel;
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel;
    }

    public List<Customer> getCustomers(){
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
