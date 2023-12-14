package com.speridian.asianpaints.evp.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardDepartmentWiseDataDTO {

	private Map<String, Map<String, Long>> noOfParticipants;

	private Map<String, Map<String, Long>> participantHours;

	private Map<String, Long> uniqueParticipants;
	
	private Map<String, Map<Long, Long>> totalVsUnique;
	
}
