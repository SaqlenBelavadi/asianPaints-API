package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardHeaderResponseDTO {

	private int noOfVolunteers;
	
	private int noOfUniqueVolunteers;
	
	private int totalHoursVolunteering;
	
	private int totalNoOfActivities;
	
	private double enrolledVsParticipated;
	
	
	
}
