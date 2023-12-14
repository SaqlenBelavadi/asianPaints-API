package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_PARTNERS_LOGO")
public class PartnersLogo extends AbstractEntity{

	@Column(name="PARTNERS_LOGO_LOCATION")
	private String partnersLogoLocation;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
}
