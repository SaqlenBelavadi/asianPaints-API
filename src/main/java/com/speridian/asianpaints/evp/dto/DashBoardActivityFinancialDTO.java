package com.speridian.asianpaints.evp.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.speridian.asianpaints.evp.entity.Activity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardActivityFinancialDTO {

	private long estimatedCreativeExpences;
	
	private long actualCreativeExpences;
	
	private long estimatedGratificationExpenses;
	
	private long actualGratificationExpenses;
	
	private long estimatedOtherExpenses;
	
	private long actualOtherExpenses;
	
	private long actualLogisticsExpense;
	
	private long estimateLogisticsExpense;
	
	private long totalActual;
	
	private long totalEstimated;
	
	@JsonIgnore
	private List<Activity> activities;
}
