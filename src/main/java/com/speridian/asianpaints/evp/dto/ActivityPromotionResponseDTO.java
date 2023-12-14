package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPromotionResponseDTO {

	private List<ActivityPromotionDTO> activityPromotion;

	private Integer pageNo;

	private Integer pageSize;

	private Integer totalPages;

	private Integer totalElements;

	private boolean hasNext;

	private boolean hasPrevious;

}
