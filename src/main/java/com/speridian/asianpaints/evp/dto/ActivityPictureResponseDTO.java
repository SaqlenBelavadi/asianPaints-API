package com.speridian.asianpaints.evp.dto;

import java.util.List;

import com.speridian.asianpaints.evp.entity.ActivityPicture;

import lombok.Builder;
import lombok.Data;

@Data
public class ActivityPictureResponseDTO {

	private List<ActivityPicture> activityPicture;

	private Integer pageNo;

	private Integer pageSize;

	private Integer totalPages;

	private Integer totalElements;

	private boolean hasNext;

	private boolean hasPrevious;

}
