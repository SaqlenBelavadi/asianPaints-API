package com.speridian.asianpaints.evp.constants;


/**
 * @author sony.lenka
 *
 */
public enum CsvConfigs {

	PARTICIPANT_FIELDS(CsvCategories.PARTICIPANTS.getCategory(), "Employee ID,Name,Activity Id,Activity Name,Activity Tag,Participation Hours,Location,Approve,Reject",
			"employeeId,employeeName,activityId,activityName,activityTag,participationHours,activityLocation,approvedByAdmin,rejectedByAdmin"),
	FINANCE_FIELDS(CsvCategories.FINANCE.getCategory(), "Activity ID,Activity Name,Activity End Date,Location,Estimated Material Or Creative Expense,Actual Material Or Creative Expense,Estimated Logistic Expense,Actual Logistic Expense,Estimated Gratification Expense,Actual Gratification Expense,Estimated Other Expense, Actual Other Expense,Estimated Total,Actual Total",
			"activityId,activityName,activityEndDate,activityLocation,materialOrCreativeExpense,actualMaterialExpense,logisticExpense,actualLogisticExpense,gratificationExpense,actualGratificationExpense,otherExpense,actualOtherExpense,estimateTotal,actualTotal");
	

	
	private String category;

	private String csvHeader;

	private String csvDbFields;

	private CsvConfigs(String lovCategory, String csvHeader, String csvDbFields) {
		this.category = lovCategory;
		this.csvHeader = csvHeader;
		this.csvDbFields = csvDbFields;
	}

	public String getLovCategory() {
		return category;
	}

	public String getCsvHeader() {
		return csvHeader;
	}

	public String getCsvDbFields() {
		return csvDbFields;
	}

}
