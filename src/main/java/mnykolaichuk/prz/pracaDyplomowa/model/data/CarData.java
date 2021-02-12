package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.model.enums.EngineType;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidVin;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidYear;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CarData {

	private Integer id;

	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String make;

	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String model;

	@ValidYear
	private String year;

	@Enumerated(EnumType.STRING)
	@NotNull(message = "is required")
	private EngineType engineType;

	@NotNull(message = "*")
	@Size(min = 1, message = "*")
	private String registrationNumber;

	@ValidVin
	private String vin;

	private String username;

	public CarData() {

	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
