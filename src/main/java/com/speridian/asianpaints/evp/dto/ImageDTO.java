package com.speridian.asianpaints.evp.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageDTO {

	private String uploadedDate;
	
	private String imageType;

	private String imageName;

	private String imageUrl;

	private String activityId;
	
	private String activityName;

	private Boolean published;

	private Boolean uploadedByAdmin;

	private Boolean coverPhoto;

	private String caption;
	
	private String activityTheme;
	
	private String activityTag;
	
	private Boolean deleted;
	
	private String uploadedBy;
	
	private String mode;
	
	private LocalDateTime startDate;

	private LocalDateTime endDate;
	
	private String activityLocation;
	
	private String containerLocation;
	
	private Long feedbackId;
	
	private Long promotionId;

}
