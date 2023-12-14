package com.speridian.asianpaints.evp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "EVP_ACTIVITY_PROMOTION")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPromotion extends AbstractEntity {

	@Column(name = "START_DATE")
	private LocalDateTime startDate;

	@Column(name = "END_DATE")
	private LocalDateTime endDate;

	@Column(name = "PROMOTION_THEME")
	private String promotionTheme;

	@Column(name = "PROMOTION_ACTIVITY")
	private String promotionActivity;

	@Column(name = "PROMOTION_LOCATION")
	private String promotionlocation;

	@Column(name = "PROMOTION_DELETED")
	private boolean deleted;
	
	@Column(name = "ACTIVITY_START_DATE")
	private LocalDateTime activityStartDate;
	
	@Column(name = "ACTIVITY_END_DATE")
	private LocalDateTime activityEndDate; 
	
	@Column(name = "ACTIVITY_ID")
	private String activityId; 
	
	@Column(name = "MODES")
	private String mode;
	
	@Column(name = "TAG_NAME")
	private String tagName;

}
