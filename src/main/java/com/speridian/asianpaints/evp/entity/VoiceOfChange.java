package com.speridian.asianpaints.evp.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_VOC")
public class VoiceOfChange extends AbstractEntity{

	@Column(name="VOC_PICTURE_LOCATION")
	private String vocPictureLocation;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="VOC_AUDIO_LOCATION")
	private String vocAudioLocation;
	
	@Column(name="AUDIO_NAME")
	private String audioName;
	
	@Column(name="SPEAKS_TYPE")
	private String speaksType;
	
	@Column(name="PERSON_NAME")
	private String personName;
	
	@Column(name="DESIGNATION_OR_INFO")
	private String designationOrInfo;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
	@Column(name="INDEX_COLUMN")
	private Long index;
	
}
