package com.speridian.asianpaints.evp.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardEmployeeWiseDataDTO {
	
	private Map<String, Map<String, Long>> noOfActivites;

	private Map<String, Map<String, Long>> noOfHours;

}
