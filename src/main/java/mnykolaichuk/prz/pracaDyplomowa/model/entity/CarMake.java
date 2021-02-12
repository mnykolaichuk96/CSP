package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "car_make")
public class CarMake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "make")
    private String make;

    @OneToMany(mappedBy = "carMake",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<CarModel> carModels;

    public CarMake() {
    }

    public CarMake(String make) {
        this.make = make;
    }

    public Integer getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public List<CarModel> getCarModels() {
        return carModels;
    }

    public void setCarModels(List<CarModel> carModels) {
        this.carModels = carModels;
    }

}
