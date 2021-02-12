package mnykolaichuk.prz.pracaDyplomowa.model.data;

public class CarModelData {

    private String make;

    private String model;

    private Integer modelId;

    private Integer carInSystemCount;

    public CarModelData() {
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getCarInSystemCount() {
        return carInSystemCount;
    }

    public void setCarInSystemCount(Integer carInSystemCount) {
        this.carInSystemCount = carInSystemCount;
    }
}
