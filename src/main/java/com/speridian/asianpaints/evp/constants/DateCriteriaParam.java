package com.speridian.asianpaints.evp.constants;

public enum DateCriteriaParam {
	TODAY("Today"), LAST_30_DAYS("Last 30 days"), THIS_MONTH_TO_DATE("This month to date"),
	THIS_YEAR_TO_DATE("This year to date"), THIS_FINANCIAL_YEAR_TO_DATE("This financial year to date"),
	THIS_QUARTER("This Quarter"), LAST_QUARTER("Last Quarter"), ALL_YEARS_TO_DATE("All years to date");

	private String criteriaParam;

	public String getCriteriaParam() {
		return criteriaParam;
	}

	public void setCriteriaParam(String criteriaParam) {
		this.criteriaParam = criteriaParam;
	}

	private DateCriteriaParam(String criteriaParam) {
		this.criteriaParam = criteriaParam;
	}

}
