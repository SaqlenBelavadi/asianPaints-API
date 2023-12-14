package com.speridian.asianpaints.evp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.util.ImageType;

import lombok.Data;

@Data
@Entity
@Table(name = "EVP_ACTIVITY_PICTURE")
public class ActivityPicture extends AbstractEntity {
	
	@Column(name="ACTIVITY_PICTURE_ID")
	private String activityPictureId;
	
	@Column(name="ACTIVITY_PICTURE_LOCATION")
	private String activityPictureLocation;
	
	@Enumerated(EnumType.STRING)
	@Column(name="IMAGE_TYPE")
	private ImageType imageType;
	
	@Column(name="IMAGE_NAME")
	private String imageName;
	
	@Column(name="COVER_PHOTO")
	private boolean coverPhoto;
	
	@Column(name="UPLOADED_BY_ADMIN")
	private boolean uploadedByAdmin;
	
	@Column(name="UPLOADED_BY_EMPLOYEE")
	private boolean uploadedByEmployee;
	
	@Column(name="QUESTIONNAIRE_PHOTO")
	private boolean questionnairePhoto;
	
	@Column(name="PUBLISHED")
	private Boolean published;
	
	@Column(name="ACTIVITY_THEME")
	private String activityTheme;
	
	@Column(name="ACTIVITY_TAG")
	private String activityTag;
	
	@Column(name="DELETED")
	private Boolean deleted;
	@Column(name="CAPTION")
	private String caption;
	
	@Column(name="UPLOADED_BY")
	private String uploadedBy;
	
	@Column(name="MODES")
	private String mode;
	
	@Column(name="START_DATE")
	private LocalDateTime startDate;

	@Column(name="END_DATE")
	private LocalDateTime endDate;
	
	@Column(name="ACTIVITY_LOCATION")
	private String activityLocation;
	
	@Column(name="CONTAINER_LOCATION")
	private String containerLocation;
	
	@Column(name="FEEDBACK_ID")
	private Long feedBackId;
	
	@Column(name="PROMOTION_ID")
	private Long promotionId;
	
	@Column(name="MANUAL_UPLOAD")
	private Boolean manualUpload;
	

}
