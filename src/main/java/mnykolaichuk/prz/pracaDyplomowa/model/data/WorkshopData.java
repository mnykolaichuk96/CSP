package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "matchingPassword", message = "{mnykolaichuk.prz.MatchPassword.message}")
})
public class WorkshopData {

	@ValidUsername
	private String username;

	@ValidPassword
	private String password;

	@ValidPassword
	private String matchingPassword;

	@NotEmpty(message = "{mnykolaichuk.prz.ValidNotEmptyMessage}")
	private String workshopName;

	@NotEmpty(message = "{mnykolaichuk.prz.ValidNotEmptyMessage}")
	private String address;

	@ValidEmail
	private String email;

	@ValidPhoneNumber
	private String phoneNumber;

	@NotNull(message = "is required")
	@Size(min = 1, message = "is required")
	private String cityName;

	Integer workshopId;

	public WorkshopData() {

	}

	public Integer getWorkshopId() {
		return workshopId;
	}

	public void setWorkshopId(Integer workshopId) {
		this.workshopId = workshopId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMatchingPassword() {
		return matchingPassword;
	}

	public void setMatchingPassword(String matchingPassword) {
		this.matchingPassword = matchingPassword;
	}

	public String getWorkshopName() {
		return workshopName;
	}

	public void setWorkshopName(String workshopName) {
		this.workshopName = workshopName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

}
