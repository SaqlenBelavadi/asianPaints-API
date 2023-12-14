package com.speridian.asianpaints.evp.constants;

public enum EmployeeActivityStatus {

	ENROLLED("Enrolled"), PARTICIPATED("Participated"), FEEDBACK("Feedback");

	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private EmployeeActivityStatus(String status) {
		this.status = status;
	}

}
