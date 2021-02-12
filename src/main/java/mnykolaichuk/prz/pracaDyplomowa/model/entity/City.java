package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "city")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", unique = true)
    private String cityName;

    @OneToMany(mappedBy = "city",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL)
    private List<Workshop> workshops;

    @OneToMany(mappedBy = "city",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST)
    private List<Order> orders;

    public City() {
    }


    public City(String cityName) {
        this.cityName = cityName;
    }

    public Integer getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(List<Workshop> workshops) {
        this.workshops = workshops;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
