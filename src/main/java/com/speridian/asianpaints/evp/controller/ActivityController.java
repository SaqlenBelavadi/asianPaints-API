package com.speridian.asianpaints.evp.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackDTO;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityFinancialResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityList;
import com.speridian.asianpaints.evp.dto.ActivityPictureResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityTagResponse;
import com.speridian.asianpaints.evp.dto.CreateOrUpdateActivityDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponseDTO;
import com.speridian.asianpaints.evp.dto.GalleryResponseDTO;
import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.service.ActivityFeedbackService;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.EmployeeActivityHistoryService;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.EmailTemplateBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sony.lenka
 *
 */
@Slf4j
@RestController
@RequestMapping("/api/evp/v1/")
public class ActivityController {

	@Value("${evp.apigee.emailApi}")
	private String emailApigeeUrl;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private EmployeeActivityHistoryService employeeActivityHistoryService;

	@Autowired
	private ActivityFeedbackService activityFeedbackService;

	@PostMapping("/Activity")
	public ResponseEntity<GenericResponse> createOrUpdateActivity(
			@RequestBody CreateOrUpdateActivityDTO createOrUpdateActivityDTO) {
		GenericResponse genericResponse = GenericResponse.builder().build();

		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			CreateOrUpdateActivityDTO createOrUpdateActivityDTOs = activityService
					.createOrUpdateActivity(createOrUpdateActivityDTO);
			genericResponse.setMessage("Activity Successfully Created");
			genericResponse.setData(createOrUpdateActivityDTOs);
			responseEntity = ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);

		}
		return responseEntity;
	}

	@DeleteMapping("/Activity")
	public ResponseEntity<GenericResponse> deleteActivity(
			@RequestParam("activityNameOrUUID") String activityNameOrUUID) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityService.deleteActivity(activityNameOrUUID);
			genericResponse.setMessage("Activity Successfully Deleted");
			responseEntity = ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/Tags")
	public ResponseEntity<GenericResponse> getActivityByTags(
			@RequestParam(name = "location", required = false) String location) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			Map<String, List<ActivityTagResponse>> activityMap = activityService.getActivityByTags(location);
			genericResponse.setData(activityMap);
			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity")
	public ResponseEntity<GenericResponse> getAllActivities(@RequestParam("searchCriteria") String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {

			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			ActivityList activityList = activityService.getAllActitiesByCriteria(criteria, pageNo, pageSize, true,
					false);
			genericResponse.setData(activityList);
			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);

		}

		return responseEntity;
	}

	@PostMapping("/Activity/EnrollOrParticipate")
	public ResponseEntity<GenericResponse> employeeActivityEnrollOrParticate(
			@RequestBody EmployeeActivityHistoryDTO employeeActivityHistory) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {

			employeeActivityHistoryService.updateEmployeeActivityHistory(employeeActivityHistory);
			if (employeeActivityHistory.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.ENROLLED.getStatus())) {
				genericResponse.setMessage("Successfully Enrolled for the Activity ");
			} else if (employeeActivityHistory.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.PARTICIPATED.getStatus())) {
				genericResponse.setMessage("Successfully Participated for the Activity ");
			}
			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;

	}

	@GetMapping("/Activity/ParticipantDetails")
	public ResponseEntity<GenericResponse> getParticipantDetails(@RequestParam("searchCriteria") String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(name = "activityType", required = false) String activityType,
			@RequestParam(name = "dashBoardDetails", required = false) boolean dashBoardDetails) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		EmployeeActivityResponseDTO employeeActivityHistories = null;
		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);

			if (dashBoardDetails && !Optional.ofNullable(criteria.getFieldValueToSearch()).isPresent()) {
				employeeActivityHistories = activityService
						.getParticipantDetailsForActivityWithCriteriaForDashBoard(criteria, pageNo, pageSize, true);
			} else {
				employeeActivityHistories = activityService.getActivityParticipantsWithCriteria(criteria, pageNo,
						pageSize, true, activityType, dashBoardDetails);
			}

			genericResponse.setData(employeeActivityHistories);
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/ActivityFinancials")
	public ResponseEntity<GenericResponse> getActivityFinancials(@RequestParam("searchCriteria") String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(name = "activityType", required = false) String activityType) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		ActivityFinancialResponseDTO activityFinancials = null;
		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			activityFinancials = activityService.getActivityFinancialsWithCriteria(criteria, pageNo, pageSize, true,
					activityType);

			genericResponse.setData(activityFinancials);
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@PostMapping("/Activity/ApproveOrReject")
	public ResponseEntity<GenericResponse> activityParticipationApproveOrReject(
			@RequestBody List<EmployeeActivityHistory> employeeActivityHistory) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityService.approveEmployeeParticipation(employeeActivityHistory);
			genericResponse.setMessage("Successfully Approved Or Rejected Employee Participation");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;

	}

	@PostMapping("/Activity/Details")
	public ResponseEntity<GenericResponse> getActivityDetails(@RequestParam("searchCriteria") String searchCriteria) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			CreateOrUpdateActivityDTO createOrUpdateActivityDTO = activityService
					.getActivityDetails(CommonUtils.buildSearchCriteria(searchCriteria));
			genericResponse.setData(createOrUpdateActivityDTO);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);

		}
		return responseEntity;
	}

	/*
	 * 
	 * 
	 * Feedback API
	 * 
	 * 
	 */

	@PostMapping("/Activity/Feedback")
	public ResponseEntity<GenericResponse> addActivityFeedBack(@RequestBody ActivityFeedbackDTO feedbackDTO) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.addActivityFeedBack(feedbackDTO);
			genericResponse.setData("Activity feedback added successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/Feedback")
	public ResponseEntity<GenericResponse> getActivityFeedBack(@RequestParam("searchCriteria") String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1", required = false) Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {

			ActivityFeedbackResponseDTO data = activityFeedbackService.getActivityFeedBack(searchCriteria, pageNo,
					pageSize);
			genericResponse.setData(data);
			return ResponseEntity.ok(genericResponse);
		} catch (

		Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@DeleteMapping("/Activity/Feedback")
	public ResponseEntity<GenericResponse> deleteActivityFeedBack(@RequestParam("id") String ids) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.deleteActivityFeedBack(ids);
			genericResponse.setData("Activity feedback deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (

		Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	/*
	 * 
	 * 
	 * Gallery API
	 * 
	 * 
	 */

	@PostMapping("/Activity/Feedback/UploadToGallery")
	public ResponseEntity<GenericResponse> UploadToGallery(@RequestParam("Id") String ids) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.UploadToGallery(ids);
			genericResponse.setData("Activity feedback uploaded successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@PostMapping("/Activity/Feedback/PublishOrUnpublish")
	public ResponseEntity<GenericResponse> publishOrUnpublish(@RequestParam("Id") String ids,
			@RequestParam("status") String status) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.publishOrUnpublish(ids, status);
			genericResponse.setData("Apublished or Unpublished successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/Feedback/Gallery")
	public ResponseEntity<GenericResponse> getGalleryFeedbacks(
			@RequestParam(value = "searchCriteria", required = false) String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			Map<String, Object> data = activityFeedbackService.getGalleryFeedbacks(pageNo, pageSize, criteria);
			genericResponse.setData(data);
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@DeleteMapping("/Activity/Feedback/Gallery")
	public ResponseEntity<GenericResponse> deleteGalleryFeedbacks(@RequestParam("Id") String ids) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.deleteGalleryFeedbacks(ids);
			genericResponse.setData("Activity feedback deleted from gallery successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	/*
	 * ActivityPromotion
	 * 
	 * 
	 */

	@PostMapping("/Activity/Promotion")
	public ResponseEntity<GenericResponse> addActivityPromotion(@RequestBody ActivityPromotionDTO dto) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.addActivityPromotion(dto);
			genericResponse.setData("Activity Promotion added successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@DeleteMapping("/Activity/Promotion")
	public ResponseEntity<GenericResponse> deleteActivityPromotion(@RequestParam("Id") String ids) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			activityFeedbackService.deleteActivityPromotion(ids);
			genericResponse.setData("Activity Promotion deleted from gallery successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/Promotion")
	public ResponseEntity<GenericResponse> getActivityPromotion(
			@RequestParam(value = "searchCriteria", required = false) String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			ActivityPromotionResponseDTO res = activityFeedbackService.getActivityPromotion(criteria, pageNo, pageSize);
			genericResponse.setData(res);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Activity/Promotion/Listing")
	public ResponseEntity<GenericResponse> getActivitiesForPromotion(
			@RequestParam(name = "promotionId", required = false) String promotionId,

			@RequestParam(name = "themeName", required = false) String themeName,
			@RequestParam(name = "location", required = false) String location) {

		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			if (Optional.ofNullable(promotionId).isPresent()) {
				ActivityPromotionDTO res = activityService.getActivityDetailsForPromotion(promotionId);
				genericResponse.setData(res);
			} else if (Optional.ofNullable(themeName).isPresent() && Optional.ofNullable(location).isPresent()) {
				List<CreateOrUpdateActivityDTO> res = activityService.getActivityDetailsForPromotion(themeName,
						location);
				genericResponse.setData(res);
			}

			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;

	}

	@PostMapping("/Activity/ActivityId")
	public ResponseEntity<GenericResponse> generateActivityIds(
			@RequestParam(value = "themeName", required = true) String themeName,
			@RequestParam(value = "location", required = true) String location) throws Exception {
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			String activityId = activityService.getActivityId(location, themeName);
			genericResponse.setData(activityId);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;

	}

	@GetMapping("/Activity/Image")
	public ResponseEntity<GenericResponse> getImages(
			@RequestParam(value = "searchCriteria", required = false) String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			GalleryResponseDTO res = activityService.getImages(criteria, pageNo, pageSize);
			genericResponse.setData(res);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

}
