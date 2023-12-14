package com.speridian.asianpaints.evp.entity;

import java.sql.Clob;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.converter.ListToStringConverter;

import lombok.Data;

@Data
@Entity
@Table( name="EVP_ACTIVITY")
public class Activity extends AbstractEntity {

	@Column(name="ACTIVITYUUID")
	private String activityUUID;
	
	@Column(name="ACTIVITY_ID")
	private String activityId;

	@Column(name="ACTIVITY_NAME")
	private String activityName;

	@Column(name="THEME_NAME_ID")
	private Long themeNameId;
	
	@Column(name="TAG_ID")
	private Long tagId;

	@Column(name="ACTIVITY_LOCATION_ID")
	private Long activityLocationId;

	@Column(name="MODE_OF_PARTICIPATION_ID")
	private Long modeOfParticipationId;

	@Column(name="START_DATE")
	private LocalDateTime startDate;

	@Column(name="END_DATE")
	private LocalDateTime endDate;

	@Column(name="TIME_OF_ACTIVITY")
	private String timeOfActivity;

	@Column(name="COMPLETE_DESCRIPTION")
	private String completeDescription;

	@Column(name="BRIEF_DESCRIPTION")
	private String briefDescription;

	@Column(name="DOS_INSTRUCTION")
	private String dosInstruction;
	
	@Column(name="DONT_INSTRUCTION")
	private String dontInstruction;

	@Column(name="CONTACT_PERSON")
	private String contactPerson;

	@Column(name="CONTANCT_EMAIL")
	private String contanctEmail;

	@Column(name="NEED_REQUEST_FROMCCSR")
	private boolean needRequestFromCCSR;

	@Column(name="REQUEST_FROMCCSR")
	private String requestFromCCSR;

	@Column(name="CSR_ADMIN_LOCATION")
	private String csrAdminLocation;

	@Column(name="BADGE_TO_BE_PROVIDED")
	private String badgeToBeProvided;
	
	@Column(name="ACTIVITY_PLACE")
	private String activityPlace;
	
	@Column(name="OBJECTIVE_ACTIVITY")
	private String objectiveActivity;

	@Column(name="CREATED")
	private boolean createdActivity;

	@Column(name="PUBLISHED")
	private boolean published;
	
	@Column(name="ACTIVITY_FINANCIAL_ID")
	private Long activityFinancialId;
	
	@Column(name="TIME_REQUIRED_HOURS")
	private Long timeRequiredHours;
	
	@Column(name="TIME_REQUIRED_MINUTES")
	private Long timeRequiredMinutes;
	
	@Column(name="PAST_VIDEO_URL")
	private String pastVideoUrl;
	
	@Column(name="TESTIMONIAL_PERSON_NAME")
	private String testimonialPersonName;
	@Column(name="TESTIMONIAL")
	@Lob
	private Clob testimonial;
	@Column(name="RATING")
	private Integer rating;
	@Column(name="PAST_VIDEO_CAPTION")
	private String pastVideoCaption;
	@Column(name="MAIL_NOTIFICATION_SENT")
	private Boolean mailNotificationSent;
}
