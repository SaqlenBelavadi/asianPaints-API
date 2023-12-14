package com.speridian.asianpaints.evp.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ActivityFeedbackDTO {

	private String feedbackId;

	private String employeeId;

	private String activityName;
	
	private String activityId;

	private int rating;

	private String feedback;

	private String location;

	private String themeName;

	private String tagName;

	private String startDate;

	private String endDate;

	private String mode;

	private String timeRequired;

	private List<String> employeeIds;

	private String fullName;

	private LocalDateTime createdDate;
	
	private boolean uploadedByAdmin;
	
	private boolean publishOrUnPublish;
	
	private List<String> imageNames;
	
	private List<ImageDTO> images;
	
	private boolean manualUpload;
	

}
