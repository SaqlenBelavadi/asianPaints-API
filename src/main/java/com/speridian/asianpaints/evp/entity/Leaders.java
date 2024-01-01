package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_LEADERS_TALK")
public class Leaders extends AbstractEntity{

	
	@Column(name="LEADER_NAME")
	private String leaderName;
	
	@Column(name="DESIGNATION")
	private String designation;
	
	@Column(name="VIDEO_URL")
	private String url;
	
	@Column(name="UPDATED_BY")
	private String uploadedBy;
	
	@Column(name="INDEX_COLUMN")
	private Long index;
	
}
