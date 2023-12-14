package com.speridian.asianpaints.evp.constants;

public enum CsvCategories {
	
	PARTICIPANTS("Participants"),FINANCE("Finance");
	
	
	private String category;
	
	private CsvCategories(String category) {
		this.category = category;
	}

	

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	

}
