package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.*;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "matchingPassword", message = "{mnykolaichuk.prz.MatchPassword.message}")
})
public class CustomerData {

	@ValidUsername
	private String username;

	@ValidPassword
	private String password;

	@ValidPassword
	private String matchingPassword;

	@ValidName
	private String firstName;

	@ValidName
	private String lastName;

	@ValidEmail
	private String email;

	@ValidPhoneNumber
	private String phoneNumber;

	private Integer customerId;

	public CustomerData() {

	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

}
