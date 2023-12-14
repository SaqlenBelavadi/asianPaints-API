package com.speridian.asianpaints.evp.constants;

public enum TimeRequired {
	
	ZEROT0TWO("0 - 2"),TWOTO4("2 - 4"),FOURTO6("4 - 6"),SIXO8("6 - 8"),ABOVE8("Above 8"),ALL("ALL");
	private String timeRequired;

	public String getTimeRequired() {
		return timeRequired;
	}

	public void setTimeRequired(String timeRequired) {
		this.timeRequired = timeRequired;
	}

	private TimeRequired(String timeRequired) {
		this.timeRequired = timeRequired;
	}
	
	

}
