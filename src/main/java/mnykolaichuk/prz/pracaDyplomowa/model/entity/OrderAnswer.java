package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import mnykolaichuk.prz.pracaDyplomowa.converter.LocalDateAttributeConverter;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.Stan;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "order_answer")
public class OrderAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Convert(converter = LocalDateAttributeConverter.class)
    @Column(name = "implementation_date")
    private LocalDate implementationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "stan")
    private Stan stan;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    public Integer getId() {
        return id;
    }

    public LocalDate getImplementationDate() {
        return implementationDate;
    }

    public void setImplementationDate(LocalDate implementationDate) {
        this.implementationDate = implementationDate;
    }

    public Stan getStan() {
        return stan;
    }

    public void setStan(Stan stan) {
        this.stan = stan;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
}
