package com.knowarth.user.model;


import java.util.Date;

public class UserMapper {
	long liferayUserId;
	String username;
	String email;
	String firstName;
	String lastName;
	boolean male;
	String jobTitle;
	Date birthday;
	String impStatus;
	String customField1;
	String customField2;
	String customField3;
	String customField4;
	String customField5;
	String customField6;
	String customField7;
	String customField8;
	String customField9;
	String customField10;

	public long getLiferayUserId() {
		return liferayUserId;
	}

	public void setLiferayUserId(long liferayUserId) {
		this.liferayUserId = liferayUserId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getImpStatus() {
		return impStatus;
	}

	public void setImpStatus(String impStatus) {
		this.impStatus = impStatus;
	}

	public String getCustomField1() {
		return customField1;
	}

	public void setCustomField1(String customField1) {
		this.customField1 = customField1;
	}

	public String getCustomField2() {
		return customField2;
	}

	public void setCustomField2(String customField2) {
		this.customField2 = customField2;
	}

	public String getCustomField3() {
		return customField3;
	}

	public void setCustomField3(String customField3) {
		this.customField3 = customField3;
	}

	public String getCustomField4() {
		return customField4;
	}

	public void setCustomField4(String customField4) {
		this.customField4 = customField4;
	}

	public String getCustomField5() {
		return customField5;
	}

	public void setCustomField5(String customField5) {
		this.customField5 = customField5;
	}

	public String getCustomField6() {
		return customField6;
	}

	public void setCustomField6(String customField6) {
		this.customField6 = customField6;
	}

	public String getCustomField7() {
		return customField7;
	}

	public void setCustomField7(String customField7) {
		this.customField7 = customField7;
	}

	public String getCustomField8() {
		return customField8;
	}

	public void setCustomField8(String customField8) {
		this.customField8 = customField8;
	}

	public String getCustomField9() {
		return customField9;
	}

	public void setCustomField9(String customField9) {
		this.customField9 = customField9;
	}

	public String getCustomField10() {
		return customField10;
	}

	public void setCustomField10(String customField10) {
		this.customField10 = customField10;
	}

	@Override
	public String toString() {
		return "UserMapper [liferayUserId=" + liferayUserId + ", username=" + username + ", email=" + email
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", male=" + male + ", jobTitle=" + jobTitle
				+ ", birthday=" + birthday + ", impStatus=" + impStatus + ", customField1=" + customField1
				+ ", customField2=" + customField2 + ", customField3=" + customField3 + ", customField4=" + customField4
				+ ", customField5=" + customField5 + ", customField6=" + customField6 + ", customField7=" + customField7
				+ ", customField8=" + customField8 + ", customField9=" + customField9 + ", customField10="
				+ customField10 + "]";
	}


}