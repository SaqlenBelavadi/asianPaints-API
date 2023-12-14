package com.speridian.asianpaints.evp.master.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.entity.AbstractEntity;

import lombok.Data;

@Data
@Table
@Entity(name="EVP_THEME_TAG")
public class EvpThemeTag extends AbstractEntity {

	@Column(name="THEME_ID")
	private Long themeId;
	
	@Column(name="TAG_ID")
	private Long tagId;
	
	
}
