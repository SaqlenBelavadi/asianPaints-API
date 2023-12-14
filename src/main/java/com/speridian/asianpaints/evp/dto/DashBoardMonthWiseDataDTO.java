package com.speridian.asianpaints.evp.dto;

import java.time.Month;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardMonthWiseDataDTO {

	private Map<Month, Map<String, Long>> noOfParticipants;
	
	private Map<Month, Map<String, Long>> participantHours;
	
	private Map<Month, Map<String, Long>> uniqueParticipants;
}
