package com.speridian.asianpaints.evp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity  {
	
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HIBERNATE_SEQUENCE")
	@SequenceGenerator(sequenceName = "HIBERNATE_SEQUENCE", initialValue = 1, allocationSize = 1, name = "HIBERNATE_SEQUENCE")
	private Long id;
	
	@CreatedBy
	@JsonIgnore
	@Column(name="createdby",updatable = false)
	private String createdBy;

	@CreatedDate
	@CreationTimestamp
	@JsonIgnore
	@Column(name="createdon",updatable = false)
	private LocalDateTime createdOn;

	@LastModifiedBy
	@JsonIgnore
	@Column(name="modifiedby")
	private String modifiedBy;

	@LastModifiedDate
	@JsonIgnore
	@UpdateTimestamp
	@Column(name="modifiedon")
	private LocalDateTime modifiedOn;

}
