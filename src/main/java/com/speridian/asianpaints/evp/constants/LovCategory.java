package com.speridian.asianpaints.evp.constants;

public enum LovCategory {
	LOCATIONS("Locations"),THEMES("Themes"),MODE_OF_PARTICIPATION("Modes"),TAG("Tag");
	
	private String categoryName;
	

	private LovCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	

}
