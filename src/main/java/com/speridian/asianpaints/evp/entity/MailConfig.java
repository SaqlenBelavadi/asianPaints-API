package com.speridian.asianpaints.evp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_MAIL_CONFIG")
public class MailConfig extends AbstractEntity {
	
	@Column(name = "CONFIG_TYPE")
	private String configType;
	@Column(name = "CONFIG_VALUE")
	private String configValue;



}
