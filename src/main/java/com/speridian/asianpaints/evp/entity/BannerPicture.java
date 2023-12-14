package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_BANNER_PICTURE")
public class BannerPicture extends AbstractEntity{

	@Column(name="BANNER_PICTURE_LOCATION")
	private String bannerPictureLocation;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
}
