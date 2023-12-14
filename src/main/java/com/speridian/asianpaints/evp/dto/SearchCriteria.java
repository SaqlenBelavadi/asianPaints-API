package com.speridian.asianpaints.evp.dto;

import java.util.List;
import java.util.Optional;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {


	private String activityUUID;
	
	private String activityId;
	
	private List<String> activityIds;

	private String modeOfParticipation;

	private List<String> modeOfParticipations;

	private List<Long> modeOfParticipationId;

	private String themeName;

	private List<String> themeNames;

	private List<Long> themeNameId;

	private String tagName;

	private List<String> tagNames;

	private List<Long> tagIds;

	private String location;

	private List<String> locations;

	private List<Long> locationId;

	private String startDate;

	private String endDate;

	private String employeeId;
	
	private List<String> employeeIds;
	
	private List<String> employeeNames;
	
	private List<String> activityNames;

	private String timeRequired;

	private String username;

	private String rating;

	private String role;

	private String uploadedByAdmin;

	private String deletedByAdmin;
	
	private String imageName;
	
	private List<String> imageNames;
	
	private String publishOrUnpublish;
	
	private String fieldToBeSearched;
	
	private String fieldValueToSearch;
	
	private boolean pastActivity;
	
	private List<EmployeeActivityStatus> statuses;
	

	public boolean isEmptyWithoutUUID() {

		if ( !Optional.ofNullable(modeOfParticipation).isPresent()
				&& !Optional.ofNullable(themeName).isPresent() && !Optional.ofNullable(location).isPresent()
				&& !Optional.ofNullable(startDate).isPresent() && !Optional.ofNullable(endDate).isPresent()
				&& !Optional.ofNullable(employeeId).isPresent() 
				&& !Optional.ofNullable(timeRequired).isPresent()
				&& !Optional.ofNullable(imageName).isPresent()
				&& !Optional.ofNullable(activityId).isPresent()
				) {
			return true;

		}

		return false;

	}

	public boolean isEmpty() {

		if (!Optional.ofNullable(modeOfParticipation).isPresent()
				&& !Optional.ofNullable(themeName).isPresent() && !Optional.ofNullable(location).isPresent()
				&& !Optional.ofNullable(startDate).isPresent() && !Optional.ofNullable(endDate).isPresent()
				&& !Optional.ofNullable(employeeId).isPresent() && !Optional.ofNullable(activityUUID).isPresent()
				&& !Optional.ofNullable(rating).isPresent() && !Optional.ofNullable(tagName).isPresent()
				&& !Optional.ofNullable(timeRequired).isPresent() && !Optional.ofNullable(uploadedByAdmin).isPresent()
				&& !Optional.ofNullable(modeOfParticipation).isPresent()
				&& !Optional.ofNullable(deletedByAdmin).isPresent()
				&& !Optional.ofNullable(imageName).isPresent()
				&& !Optional.ofNullable(activityId).isPresent()
				) {
			return true;

		}

		return false;

	}

}
