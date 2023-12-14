package com.speridian.asianpaints.evp.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardModeWiseDataDTO {

	
	private Map<String, Long> noOfParticipants;
	
	private Map<String, Long> participantHours;
	
	private Map<Object, Object> uniqueParticipants;
	
}
