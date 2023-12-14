package com.speridian.asianpaints.evp.master.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.entity.AbstractEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_LOV")
public class EvpLov extends AbstractEntity {
	
	@Column(name = "LOV_CATEGORY")
	private String lovCategory;
	@Column(name = "LOV_DISPLAY_NAME")
	private String lovDisplayName;
	@Column(name = "LOV_VALUE")
	private String lovValue;
	@Column(name = "LOV_DISPLAY_ORDER")
	private String lovDisplayOrder;

}
