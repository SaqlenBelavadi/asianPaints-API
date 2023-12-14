package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UpcomingActivities {
	
	
	private List<CreateOrUpdateActivityDTO> upcomingActivities;
	
	
	private Integer pageNo;
	
	private Integer pageSize;
	
	private Integer totalPages;
	
	private Integer totalElements;
	
	private boolean hasNext;
	
	private boolean hasPrevious;
	
	
	

}
