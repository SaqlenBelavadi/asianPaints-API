package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_VIDEO")
public class Video extends AbstractEntity{

	@Column(name="VIDEO_URL")
	private String videoURL;
	
	@Column(name="VIDEO_NAME")
	private String videoName;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
	@Column(name="INDEX_COLUMN")
	private Long index;
	
}
