package com.speridian.asianpaints.evp.master.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.entity.AbstractEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_LOCATION_DIVISION")
public class EvpLocationDivision extends AbstractEntity {
	
	@Column(name = "LOCATION")
	private String location;
	@Column(name = "REFERENCE")
	private String reference;
	@Column(name = "CODE")
	private String code;

}
