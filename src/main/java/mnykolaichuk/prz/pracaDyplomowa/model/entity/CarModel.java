package mnykolaichuk.prz.pracaDyplomowa.model.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "car_model")
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "model")
    private String model;

    @OneToMany(mappedBy = "carModel")
    private List<Car> cars;

    @ManyToOne
    @JoinColumn(name = "car_make_id")
    private CarMake carMake;

    public CarModel() {
    }

    public CarModel(String model) {
        this.model = model;
    }

    public Integer getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public CarMake getCarMake() {
        return carMake;
    }

    public void setCarMake(CarMake carMake) {
        this.carMake = carMake;
    }

}
