package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailTemplateData {

	private String activityName;
	
	private String createdBy;
	
	private String createdDate;
	
	private String employeeName;
	
	private String activityLink;
	
	private String adminName;
	
	private String location;
	
	private String requestFromCCSR;
	
	private ActivityFinancialDTO activityFinancials;
	
	
}
