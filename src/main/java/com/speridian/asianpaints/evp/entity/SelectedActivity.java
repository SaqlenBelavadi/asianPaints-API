package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_SELECTED_ACTIVITY")
public class SelectedActivity extends AbstractEntity{

	@Column(name="ACT_LOCATION")
	private String location;
	
	@Column(name="NAME_ID")
	private String nameAndId;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
}
