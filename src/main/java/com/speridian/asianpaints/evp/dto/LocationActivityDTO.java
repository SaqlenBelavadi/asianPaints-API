package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationActivityDTO {

	private String location;
	
	private List<String> activityIdName;
	
	private String currentActivity;
	
}
