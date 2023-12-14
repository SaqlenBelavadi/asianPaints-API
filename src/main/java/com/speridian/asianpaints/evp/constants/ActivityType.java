package com.speridian.asianpaints.evp.constants;

public enum ActivityType {
	
	UPCOMING("Upcoming"),ONGOING("Ongoing"),PAST("Past"),CREATED("Created"),ALL("ALL");
	
	private String paramName;

	private ActivityType(String paramName) {
		this.paramName = paramName;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	
	
	
	

}
