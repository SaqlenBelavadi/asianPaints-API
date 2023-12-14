package com.speridian.asianpaints.evp.dto;

import lombok.Data;


@Data
public class ActivityFinancialDTO {
	
	
	private String activityId;
	
	private String activityUUId;
	
	private String activityName;
	
	private String activityEndDate;
	
	private String activityLocation;
	
	private String materialOrCreativeExpense;

	private String logisticExpense;

	private String gratificationExpense;

	private String otherExpense;

	private String actualMaterialExpense;

	private String actualLogisticExpense;

	private String actualGratificationExpense;

	private String actualOtherExpense;
	
	private Long actualTotal;
	
	private Long estimateTotal;
	

}
