package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import mnykolaichuk.prz.pracaDyplomowa.converter.LocalDateTimeAttributeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ordering")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "creation_date")
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "order",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.REFRESH})
    private List<OrderAnswer> orderAnswers;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "customer_detail_id")
    private CustomerDetail customerDetail;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    public Order() {
    }

    public Integer getId() {
        return id;
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

    public List<OrderAnswer> getOrderAnswers() {
        return orderAnswers;
    }

    public void setOrderAnswers(List<OrderAnswer> orderAnswers) {
        this.orderAnswers = orderAnswers;
    }

    public CustomerDetail getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(CustomerDetail customerDetail) {
        this.customerDetail = customerDetail;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

}
