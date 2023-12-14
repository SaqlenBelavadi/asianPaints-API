package com.speridian.asianpaints.evp.entity;

import java.sql.Clob;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_ACTIVITY_FEEDBACK")
public class ActivityFeedback extends AbstractEntity {

	@Column(name = "EMPLOYEE_ID")
	private String employeeId;

	@Column(name = "ACTIVITY_NAME")
	private String activityName;

	@Column(name = "RATING")
	private int rating;

	@Lob
	@Column(name = "ACTIVITY_FEEDBACK")
	private Clob feedback;

	@Column(name = "ACTIVITY_LOCATION")
	private String location;

	@Column(name = "ACTIVITY_THEME")
	private String themeName;

	@Column(name = "ACTIVITY_TAG")
	private String tagName;

	@Column(name = "START_DATE")
	private LocalDateTime startDate;

	@Column(name = "END_DATE")
	private LocalDateTime endDate;

	@Column(name = "MODES")
	private String mode;

	@Column(name = "TIME_REQUIRED")
	private String timeRequired;

	@Column(name = "PUBLISHED")
	private Boolean published;

	@Column(name = "UPLOADED_BY_ADMIN")
	private boolean uploadedByAdmin;

	@Column(name = "ACTIVITY_DELETED")
	private boolean deleted;
	
	@Column(name="MANUAL_UPLOAD")
	private Boolean manualUpload;

}
