package com.speridian.asianpaints.evp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeActivityHistoryDTO {

	
	private String employeeId;
	
	private String activityUuid;
	
	private String enrolledOrParticipate;
	
	private String activityUrl;
	
	private String location;
	
	
}
