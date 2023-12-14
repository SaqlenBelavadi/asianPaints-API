package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrUpdateActivityDTO {

	private String activityUUID;

	private String activityId;

	private String activityName;

	private String themeName;

	private String tagName;

	private String activityLocation;

	private String modeOfParticipation;

	private String startDate;

	private String endDate;

	private String timeOfActivity;

	private String timeRequired;

	private String completeDescription;

	private String briefDescription;

	private String dosInstruction;

	private String dontInstruction;

	private String contactPerson;

	private String contanctEmail;

	private boolean needRequestFromCCSR;

	private String requestFromCCSR;

	private String csrAdminLocation;

	private String badgeToBeProvided;

	private String activityPlace;

	private String objectiveActivity;

	private String pastVideoUrl;

	private String pastVideoCaption;

	private String testimonial;

	private String testimonialPersonName;

	private Integer rating;

	private boolean created;

	private boolean published;

	private Long activityFinancialId;

	private ActivityFinancialDTO activityFinancialDTO;

	private List<String> enrolledEmployees;

	private List<ImageDTO> images;

	private String employeeParticipationStatus;

	private String activityUrl;
	
	private double averageRating;

}
