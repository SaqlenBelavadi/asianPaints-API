package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishOrUnPublishImages {

	
	private String activityName;
	
	private String imageName;
	
	private String imageType;
	
	private boolean publishOrUnpublish;
	
	private boolean softDelete;
	
	private String activityId;
}
