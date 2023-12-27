package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_LEADERS_TALK")
public class Leaders extends AbstractEntity{

	@Column(name="LEADERS_PICTURE_LOCATION")
	private String leaderPictureLocation;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="LEADER_NAME")
	private String leaderName;
	
	@Column(name="DESIGNATION")
	private String designation;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
	@Column(name="INDEX_COLUMN")
	private Long index;
	
}
