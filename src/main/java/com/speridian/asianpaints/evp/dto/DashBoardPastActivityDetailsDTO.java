package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardPastActivityDetailsDTO {

	
	private String createdDate;
	
	private String activityId;
	
	private String activityUUID;
	
	private String activityName;
	
	private String activityLocation;
	
	private String activityTag;
	
	private Integer totalHours;
	
	private Integer totalParticipants;
	
	private Integer rating;
	
	
}
