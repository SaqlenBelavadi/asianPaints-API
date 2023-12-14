package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityList {

	private OngoingActivities ongoingActivities;
	
	private UpcomingActivities upcomingActivities;
	
	private CreatedActivities createdActivities;
	
	private PastActivities pastActivities;
	
	private EmployeeActivityHistoryResponseDTO employeeActivityHistoryResponse;
	
	private CreateOrUpdateActivityDTO activity;
	
}
