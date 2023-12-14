package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPromotionDTO {

	private String promotionId;

	private String startDate;

	private String endDate;

	private String promotionTheme;

	private String promotionActivity;

	private List<String> promotionlocations;

	private String promotionlocation;

	private List<ImageDTO> images;
	
	private String activityStartDate;
	
	private String activityEndDate;
	
	private String activityId;
	
	
}
