package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_TESTIMONIAL_DATA")
public class TestimonialData extends AbstractEntity{

	@Column(name="TESTIMONIAL_PICTURE_LOCATION")
	private String testimonialPictureLocation;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="TESTIMONIAL_NAME")
	private String testimonialName;
	
	@Column(name="DESIGNATION_AND_LOCATION")
	private String designationAndLocation;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
}
