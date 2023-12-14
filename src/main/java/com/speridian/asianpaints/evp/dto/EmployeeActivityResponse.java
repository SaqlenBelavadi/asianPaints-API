package com.speridian.asianpaints.evp.dto;

import java.time.LocalDate;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EmployeeActivityResponse {
	
	
	private String employeeId;

	private String activityUUID;

	private String activityName;

	private String employeeName;

	private String activityTag;

	private String activityTheme;

	private LocalDate endDate;

	private String mode;

	private String departmentName;

	private String activityLocation;

	private String participationHours;

	private boolean approvedByAdmin;

	private boolean rejectedByAdmin;

	private EmployeeActivityStatus employeeActivityStatus;
	
	private String activityId;

}
