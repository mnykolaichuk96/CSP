package mnykolaichuk.prz.pracaDyplomowa.model.data;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidEmail;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidName;
import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPhoneNumber;

public class CustomerDetailData {

	@ValidName
	private String firstName;

	@ValidName
	private String lastName;

	@ValidEmail
	private String email;

	@ValidPhoneNumber
	private String phoneNumber;

	public CustomerDetailData() {
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
