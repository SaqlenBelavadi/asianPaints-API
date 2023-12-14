package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_ACTIVITY_LOCATION")
public class ActivityLocation extends AbstractEntity{
	
	@Column(name = "ACTIVITY_ID")
	private String activityId;
	
	@Column(name = "ACTIVITY_LOCATION_ID")
	private Long locationId;

}
