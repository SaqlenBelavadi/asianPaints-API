package com.speridian.asianpaints.evp.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.speridian.asianpaints.evp.dto.ActivityFinancialResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityList;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityTagResponse;
import com.speridian.asianpaints.evp.dto.CreateOrUpdateActivityDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponseDTO;
import com.speridian.asianpaints.evp.dto.GalleryResponseDTO;
import com.speridian.asianpaints.evp.dto.LocationActivityDTO;
import com.speridian.asianpaints.evp.dto.PastActivities;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.entity.SelectedActivity;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface ActivityService {

	public CreateOrUpdateActivityDTO createOrUpdateActivity(CreateOrUpdateActivityDTO createOrUpdateActivityDTO)
			throws EvpException;

	public void deleteActivity(String activityNameOrUUId) throws EvpException;

	public Map<String, List<ActivityTagResponse>> getActivityByTags(String location) throws EvpException;

	public ActivityList getAllActitiesByCriteria(SearchCriteria searchCriteria, Integer pageNo, Integer pageSize,
			boolean getEnrolledEmployees, boolean financialDetails) throws EvpException;

	public EmployeeActivityResponseDTO getParticipantDetailsForActivity(String activityUUID, Integer pageNo,
			Integer pageSize, boolean paginationRequired, String activityType) throws EvpException;

	public EmployeeActivityResponseDTO getParticipantDetailsForActivityWithCriteria(SearchCriteria searchCriteria,
			Integer pageNo, Integer pageSize, boolean paginationRequired, String activityType) throws EvpException;

	public void approveEmployeeParticipation(List<EmployeeActivityHistory> employeeActivityHistories)
			throws EvpException;

	public EmployeeActivityResponseDTO getActivityParticipantsWithCriteria(SearchCriteria searchCriteria,
			Integer pageNo, Integer pageSize, boolean paginationRequired, String activityType, boolean dashBoardDetails)
			throws EvpException;

	public ActivityFinancialResponseDTO getActivityFinancialsWithCriteria(SearchCriteria searchCriteria, Integer pageNo,
			Integer pageSize, boolean paginationRequired, String activityType) throws EvpException;

	public List<Activity> getAllActivitiesByCriteria(SearchCriteria searchCriteria) throws EvpException;

	public CreateOrUpdateActivityDTO getActivityDetails(SearchCriteria searchCriteria) throws EvpException;

	public ActivityPromotionDTO getActivityDetailsForPromotion(String promotionId) throws EvpException;

	public String downloadCertificate(String participantName, String activityName, String employeeId) throws Exception;

	public String getActivityId(String location, String theme);

	public List<Activity> filterByLocation(List<Long> activityLocationIds, List<Activity> activities);

	public GalleryResponseDTO getImages(SearchCriteria criteria, Integer pageNo, Integer pageSize);

	public Map<String, List<Long>> getActivityLocationMap(List<Activity> activities);

	public List<CreateOrUpdateActivityDTO> getActivityDetailsForPromotion(String themeName, String location)
			throws EvpException;

	public EmployeeActivityResponseDTO getParticipantDetailsForActivityWithCriteriaForDashBoard(
			SearchCriteria searchCriteria, Integer pageNo, Integer pageSize, boolean paginationRequired)
			throws EvpException;

	public List<CreateOrUpdateActivityDTO> getLocationWisePastActivities() throws EvpException;
	
	public List<LocationActivityDTO> getLocationActivitiesForConfig() throws Exception;
	
	public SelectedActivity setAvtivityIdAndName(String location, String nameId);
	
	public List<SelectedActivity> getActivityConfig();
	
	public void deleteActivityConfig(String activityNameId);
}
