package com.speridian.asianpaints.evp.constants;

public enum DashBoardCsvCategories {
	
	THEME_WISE_PARTICIPATION("ThemeWise","ParticipationWise"),
	THEME_WISE_UNIQUE_PARTICIPATION("ThemeWise","UniqueParticipationWise"),
	THEME_WISE_PARTICIPATION_HOUR_WISE("ThemeWise","ParticipationHoursWise"),
	MODE_WISE_PARTICIPATION("ModeWise","ParticipationWise"),
	MODE_WISE_UNIQUE_PARTICIPATION("ModeWise","UniqueParticipationWise"),
	MODE_WISE_PARTICIPATION_HOUR("ModeWise","ParticipationHoursWise"),
	
	MONTH_WISE_PARTICIPATION("MonthWise","ParticipationWise"),
	MONTH_WISE_UNIQUE_PARTICIPATION("MonthWise","UniqueParticipationWise"),
	MONTH_WISE_PARTICIPATION_HOUR("MonthWise","ParticipationHoursWise"),
	EMPLOYEE_WISE_UNIQUE_PARTICIPATION("EmployeeWise","ActivityWise"),
	EMPLOYEE_WISE_PARTICIPATION_HOUR("EmployeeWise","ParticipationHoursWise"),
	LOCATION_WISE_PARTICIPATION("LocationWise","ParticipationWise"),
	LOCATION_WISE_UNIQUE_PARTICIPATION("LocationWise","UniqueParticipationWise"),
	LOCATION_WISE_PARTICIPATION_HOUR("LocationWise","ParticipationHoursWise"),
	DEPARTMENT_WISE_PARTICIPATION("DepartmentWise","ParticipationWise"),
	DEPARTMENT_WISE_UNIQUE_PARTICIPATION("DepartmentWise","UniqueParticipationWise"),
	DEPARTMENT_WISE_PARTICIPATION_HOUR("DepartmentWise","ParticipationHoursWise");
	
	private String category;

	private String subCategory;

	private DashBoardCsvCategories(String category, String subCategory) {
		this.category = category;
		this.subCategory = subCategory;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	
	
	
	
}
