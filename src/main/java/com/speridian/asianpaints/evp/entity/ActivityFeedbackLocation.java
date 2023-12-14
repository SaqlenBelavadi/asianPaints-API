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
@Table(name = "EVP_FEEDBACK_LOCATION")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFeedbackLocation extends AbstractEntity{
	
	
	@Column(name = "FEEDBACK_ID")
	private Long feedbackId;
	
	@Column(name = "ACTIVITY_ID")
	private String activityId;
	
	@Column(name = "ACTIVITY_LOCATION_ID")
	private String location;

}
