package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "EVP_PROMOTION_LOCATION")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPromotionLocation extends AbstractEntity{
	
	
	@Column(name = "PROMOTION_ID")
	private Long promotionId;
	
	@Column(name = "ACTIVITY_ID")
	private String activityId;
	
	@Column(name = "ACTIVITY_LOCATION_ID")
	private String location;

}
