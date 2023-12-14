package com.speridian.asianpaints.evp.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speridian.asianpaints.evp.constants.ActivityType;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackDTO;
import com.speridian.asianpaints.evp.dto.ActivityFinancialDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionResponseDTO;
import com.speridian.asianpaints.evp.dto.CreateOrUpdateActivityDTO;
import com.speridian.asianpaints.evp.dto.CreatedActivities;
import com.speridian.asianpaints.evp.dto.DashBoardPastActivityDetailsResponseDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryResponseDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponse;
import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.ImageDTO;
import com.speridian.asianpaints.evp.dto.ImageResponse;
import com.speridian.asianpaints.evp.dto.OngoingActivities;
import com.speridian.asianpaints.evp.dto.PastActivities;
import com.speridian.asianpaints.evp.dto.PublishedImages;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.dto.UpcomingActivities;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityFinancial;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CommonUtils {

	public CommonUtils() throws EvpException {
		throw new EvpException("Object creation not possible");
	}

	public static Activity convertActivityDtoToEntity(CreateOrUpdateActivityDTO createOrUpdateActivityDTO,
			Activity activity, Long themeId, Long modeId, Long tagId, LocalDateTime startDate, LocalDateTime endDate)
			throws SerialException, SQLException {

		Long timeRequiredHours = null;
		Long timeRequiredMinutes = null;
		String timeRequired = createOrUpdateActivityDTO.getTimeRequired();

		if (Optional.ofNullable(timeRequired).isPresent()) {

			String[] timeRequiredArray = timeRequired.split(" ");

			timeRequiredHours = Long.parseLong(timeRequiredArray[0]);
			timeRequiredMinutes = Long.parseLong(timeRequiredArray[2]);

		}

		if (!Optional.ofNullable(activity).isPresent()) {

			SerialClob serialClob = new SerialClob(createOrUpdateActivityDTO.getTestimonial() != null
					? createOrUpdateActivityDTO.getTestimonial().toCharArray()
					: new char[] {});

			return EVPWebGenericBuilder.of(Activity::new).with(Activity::setActivityUUID, UUID.randomUUID().toString())
					.with(Activity::setActivityName, createOrUpdateActivityDTO.getActivityName())
					.with(Activity::setActivityPlace, createOrUpdateActivityDTO.getActivityPlace())
					.with(Activity::setBadgeToBeProvided, createOrUpdateActivityDTO.getBadgeToBeProvided())
					.with(Activity::setBriefDescription, createOrUpdateActivityDTO.getBriefDescription())
					.with(Activity::setCompleteDescription, createOrUpdateActivityDTO.getCompleteDescription())
					.with(Activity::setContactPerson, createOrUpdateActivityDTO.getContactPerson())
					.with(Activity::setContanctEmail, createOrUpdateActivityDTO.getContanctEmail())
					.with(Activity::setCreatedActivity, createOrUpdateActivityDTO.isCreated())
					.with(Activity::setPublished, createOrUpdateActivityDTO.isPublished())
					.with(Activity::setCsrAdminLocation, createOrUpdateActivityDTO.getCsrAdminLocation())
					.with(Activity::setDosInstruction, createOrUpdateActivityDTO.getDosInstruction())
					.with(Activity::setDontInstruction, createOrUpdateActivityDTO.getDontInstruction())
					.with(Activity::setCsrAdminLocation, createOrUpdateActivityDTO.getCsrAdminLocation())
					.with(Activity::setEndDate, endDate).with(Activity::setStartDate, startDate)
					.with(Activity::setNeedRequestFromCCSR, createOrUpdateActivityDTO.isNeedRequestFromCCSR())
					.with(Activity::setRequestFromCCSR, createOrUpdateActivityDTO.getRequestFromCCSR())
					.with(Activity::setObjectiveActivity, createOrUpdateActivityDTO.getObjectiveActivity())
					.with(Activity::setPublished, createOrUpdateActivityDTO.isPublished())
					.with(Activity::setTimeOfActivity, createOrUpdateActivityDTO.getTimeOfActivity())
					.with(Activity::setTimeRequiredHours, timeRequiredHours)
					.with(Activity::setTimeRequiredMinutes, timeRequiredMinutes).with(Activity::setThemeNameId, themeId)
					.with(Activity::setTagId, tagId)
					.with(Activity::setPastVideoUrl, createOrUpdateActivityDTO.getPastVideoUrl())
					.with(Activity::setModeOfParticipationId, modeId).with(Activity::setTestimonial, serialClob)
					.with(Activity::setTestimonialPersonName, createOrUpdateActivityDTO.getTestimonialPersonName())
					.with(Activity::setRating, createOrUpdateActivityDTO.getRating())
					.with(Activity::setPastVideoCaption, createOrUpdateActivityDTO.getPastVideoCaption())
					.with(Activity::setActivityId, createOrUpdateActivityDTO.getActivityId())
					.build();

		} else {
			SerialClob serialClob = new SerialClob(createOrUpdateActivityDTO.getTestimonial().toCharArray());
			return EVPWebGenericBuilder.of(() -> activity)
					.with(Activity::setActivityName, createOrUpdateActivityDTO.getActivityName())
					.with(Activity::setActivityPlace, createOrUpdateActivityDTO.getActivityPlace())
					.with(Activity::setBadgeToBeProvided, createOrUpdateActivityDTO.getBadgeToBeProvided())
					.with(Activity::setBriefDescription, createOrUpdateActivityDTO.getBriefDescription())
					.with(Activity::setCompleteDescription, createOrUpdateActivityDTO.getCompleteDescription())
					.with(Activity::setContactPerson, createOrUpdateActivityDTO.getContactPerson())
					.with(Activity::setContanctEmail, createOrUpdateActivityDTO.getContanctEmail())
					.with(Activity::setCreatedActivity,
							createOrUpdateActivityDTO.isPublished() == true ? false
									: createOrUpdateActivityDTO.isCreated())
					.with(Activity::setPublished, createOrUpdateActivityDTO.isPublished())
					.with(Activity::setCsrAdminLocation, createOrUpdateActivityDTO.getCsrAdminLocation())
					.with(Activity::setDosInstruction, createOrUpdateActivityDTO.getDosInstruction())
					.with(Activity::setDontInstruction, createOrUpdateActivityDTO.getDontInstruction())
					.with(Activity::setCsrAdminLocation, createOrUpdateActivityDTO.getCsrAdminLocation())
					.with(Activity::setEndDate, endDate).with(Activity::setStartDate, startDate)
					.with(Activity::setNeedRequestFromCCSR, createOrUpdateActivityDTO.isNeedRequestFromCCSR())
					.with(Activity::setRequestFromCCSR, createOrUpdateActivityDTO.getRequestFromCCSR())
					.with(Activity::setObjectiveActivity, createOrUpdateActivityDTO.getObjectiveActivity())
					.with(Activity::setPublished, createOrUpdateActivityDTO.isPublished())
					.with(Activity::setTimeOfActivity, createOrUpdateActivityDTO.getTimeOfActivity())
					.with(Activity::setTimeRequiredHours, timeRequiredHours)
					.with(Activity::setTimeRequiredMinutes, timeRequiredMinutes).with(Activity::setThemeNameId, themeId)
					.with(Activity::setTagId, tagId)
					.with(Activity::setPastVideoUrl, createOrUpdateActivityDTO.getPastVideoUrl())
					.with(Activity::setModeOfParticipationId, modeId).with(Activity::setTestimonial, serialClob)
					.with(Activity::setTestimonialPersonName, createOrUpdateActivityDTO.getTestimonialPersonName())
					.with(Activity::setRating, createOrUpdateActivityDTO.getRating())
					.with(Activity::setPastVideoCaption, createOrUpdateActivityDTO.getPastVideoCaption()).build();
		}

	}

	public static ActivityFinancial convertActivityFinancialDtoToEntity(ActivityFinancialDTO activityFinancialDTO,
			ActivityFinancial activityFinancial) {
		if (!Optional.ofNullable(activityFinancial).isPresent()) {

			return EVPWebGenericBuilder.of(ActivityFinancial::new)
					.with(ActivityFinancial::setMaterialOrCreativeExpense,
							activityFinancialDTO.getMaterialOrCreativeExpense())
					.with(ActivityFinancial::setLogisticExpense, activityFinancialDTO.getLogisticExpense())
					.with(ActivityFinancial::setOtherExpense, activityFinancialDTO.getOtherExpense())
					.with(ActivityFinancial::setGratificationExpense, activityFinancialDTO.getGratificationExpense())
					.with(ActivityFinancial::setActualGratificationExpense,
							activityFinancialDTO.getActualGratificationExpense())
					.with(ActivityFinancial::setActualLogisticExpense, activityFinancialDTO.getActualLogisticExpense())
					.with(ActivityFinancial::setActualMaterialExpense, activityFinancialDTO.getActualMaterialExpense())
					.with(ActivityFinancial::setActualOtherExpense, activityFinancialDTO.getActualOtherExpense())
					.build();

		} else {

			return EVPWebGenericBuilder.of(() -> activityFinancial)
					.with(ActivityFinancial::setMaterialOrCreativeExpense,
							activityFinancialDTO.getMaterialOrCreativeExpense())
					.with(ActivityFinancial::setLogisticExpense, activityFinancialDTO.getLogisticExpense())
					.with(ActivityFinancial::setOtherExpense, activityFinancialDTO.getOtherExpense())
					.with(ActivityFinancial::setGratificationExpense, activityFinancialDTO.getGratificationExpense())
					.with(ActivityFinancial::setActualGratificationExpense,
							activityFinancialDTO.getActualGratificationExpense())
					.with(ActivityFinancial::setActualLogisticExpense, activityFinancialDTO.getActualLogisticExpense())
					.with(ActivityFinancial::setActualMaterialExpense, activityFinancialDTO.getActualMaterialExpense())
					.with(ActivityFinancial::setActualOtherExpense, activityFinancialDTO.getActualOtherExpense())
					.build();
		}
	}

	public static List<ActivityFinancialDTO> convertActivityFinancialToDTO(List<ActivityFinancial> activityFinancials,
			Map<Long, CreateOrUpdateActivityDTO> activityMap) {

		List<ActivityFinancialDTO> activityFinancialDTOs = new LinkedList<>();
		activityFinancials.stream().forEach(activityFinancial -> {
			String activityId = null;
			String activityName = null;
			String activityLocation = null;
			String activityEndDate = null;
			String activityUUID = null;

			CreateOrUpdateActivityDTO activity = activityMap.get(activityFinancial.getId());
			if (Optional.ofNullable(activity).isPresent()) {
				activityId = activity.getActivityId();
				activityUUID = activity.getActivityUUID();
				activityName = activity.getActivityName();
				activityLocation = activity.getActivityLocation();
				activityEndDate = activity.getEndDate().split("T")[0];
			}

			ActivityFinancialDTO activityFinancialDTO = convertActivityFinancialToDTO(activityFinancial, activityId,
					activityName, activityLocation, activityEndDate, activityUUID);

			activityFinancialDTOs.add(activityFinancialDTO);
		});

		return activityFinancialDTOs;

	}

	public static ActivityFinancialDTO convertActivityFinancialToDTO(ActivityFinancial activityFinancial,
			String activityId, String activityName, String activityLocation, String activityEndDate,
			String activityUUID) {
		Long total = 0L;
		String materialExpense = activityFinancial.getMaterialOrCreativeExpense();
		String logisticExpense = activityFinancial.getLogisticExpense();
		String otherExpense = activityFinancial.getOtherExpense();
		String gratificationExpense = activityFinancial.getGratificationExpense();

		if (Optional.ofNullable(materialExpense).isPresent() && !materialExpense.isEmpty()) {
			total=Long.sum(total,Long.valueOf(materialExpense.split(" ")[0]));
		}
		if (Optional.ofNullable(logisticExpense).isPresent() && !logisticExpense.isEmpty()) {
			total = Long.sum(total,Long.valueOf(logisticExpense.split(" ")[0]));
		}
		if (Optional.ofNullable(otherExpense).isPresent() && !otherExpense.isEmpty()) {
			total = Long.sum(total,Long.valueOf(otherExpense.split(" ")[0]));
		}
		if (Optional.ofNullable(gratificationExpense).isPresent() && !gratificationExpense.isEmpty()) {
			total = Long.sum(total,Long.valueOf(gratificationExpense.split(" ")[0]));
		}

		String actualGratiFication = activityFinancial.getActualGratificationExpense();
		String actualLogistic = activityFinancial.getActualLogisticExpense();
		String actualOtherGratification = activityFinancial.getActualOtherExpense();
		String actualMaterialExpense = activityFinancial.getActualMaterialExpense();

		Long totalActual = 0L;
		if (Optional.ofNullable(actualGratiFication).isPresent() && !actualGratiFication.isEmpty()) {
			totalActual=Long.sum(totalActual , Long.valueOf(actualGratiFication.split(" ")[0]));
		}
		if (Optional.ofNullable(actualLogistic).isPresent() && !actualGratiFication.isEmpty()) {
			totalActual =Long.sum(totalActual , Long.valueOf(actualLogistic.split(" ")[0]));
		}
		if (Optional.ofNullable(actualOtherGratification).isPresent() && !actualGratiFication.isEmpty()) {
			totalActual = Long.sum(totalActual ,Long.valueOf(actualOtherGratification.split(" ")[0]));
		}
		if (Optional.ofNullable(actualMaterialExpense).isPresent() && !actualGratiFication.isEmpty()) {
			totalActual = Long.sum(totalActual ,Long.valueOf(actualMaterialExpense.split(" ")[0]));
		}

		ActivityFinancialDTO activityFinancialDTO = EVPWebGenericBuilder.of(ActivityFinancialDTO::new)
				.with(ActivityFinancialDTO::setMaterialOrCreativeExpense,
						activityFinancial.getMaterialOrCreativeExpense())
				.with(ActivityFinancialDTO::setLogisticExpense, activityFinancial.getLogisticExpense())
				.with(ActivityFinancialDTO::setOtherExpense, activityFinancial.getOtherExpense())
				.with(ActivityFinancialDTO::setGratificationExpense, activityFinancial.getGratificationExpense())
				.with(ActivityFinancialDTO::setActualGratificationExpense,
						activityFinancial.getActualGratificationExpense())
				.with(ActivityFinancialDTO::setActualLogisticExpense, activityFinancial.getActualLogisticExpense())
				.with(ActivityFinancialDTO::setActualMaterialExpense, activityFinancial.getActualMaterialExpense())
				.with(ActivityFinancialDTO::setActualOtherExpense, activityFinancial.getActualOtherExpense())
				.with(ActivityFinancialDTO::setActivityId, activityId)
				.with(ActivityFinancialDTO::setActivityEndDate, activityEndDate)
				.with(ActivityFinancialDTO::setActivityLocation, activityLocation)
				.with(ActivityFinancialDTO::setActivityName, activityName)
				.with(ActivityFinancialDTO::setActivityUUId, activityUUID)
				.with(ActivityFinancialDTO::setActualTotal, totalActual)
				.with(ActivityFinancialDTO::setEstimateTotal, total).build();
		return activityFinancialDTO;
	}

	public static EmployeeDTO convertEntityToEmployeeDTO(Employee employee) {
		return EVPWebGenericBuilder.of(EmployeeDTO::new).with(EmployeeDTO::setEmployeeId, employee.getEmployeeId())
				.with(EmployeeDTO::setActive, employee.getActive())
				.with(EmployeeDTO::setBloodGroup, employee.getBloodGroup())
				.with(EmployeeDTO::setCompany, employee.getCompany())
				.with(EmployeeDTO::setConfirmationDate, employee.getConfirmationDate())
				.with(EmployeeDTO::setConfirmationStatus, employee.getConfirmationStatus())
				.with(EmployeeDTO::setDateOfBirth, employee.getDateOfBirth())
				.with(EmployeeDTO::setDepartmentCode, employee.getDepartmentCode())
				.with(EmployeeDTO::setDepartmentName, employee.getDepartmentName())
				.with(EmployeeDTO::setDivision_Code, employee.getDivision_Code())
				.with(EmployeeDTO::setDivisionName, employee.getDivisionName())
				.with(EmployeeDTO::setEmail, employee.getEmail())
				.with(EmployeeDTO::setEmployeeName, employee.getEmployeeName())
				.with(EmployeeDTO::setExitDate, employee.getExitDate())
				.with(EmployeeDTO::setFirstName, employee.getFirstName())
				.with(EmployeeDTO::setFunction_Code, employee.getFunction_Code())
				.with(EmployeeDTO::setFunctionName, employee.getFunctionName())
				.with(EmployeeDTO::setGender, employee.getGender())
				.with(EmployeeDTO::setHireDate, employee.getHireDate())
				.with(EmployeeDTO::setJobCode, employee.getJobCode())
				.with(EmployeeDTO::setJobTitle, employee.getJobTitle())
				.with(EmployeeDTO::setLastName, employee.getLastName())
				.with(EmployeeDTO::setLastWorkingDay, employee.getLastWorkingDay())
				.with(EmployeeDTO::setLocationCode, employee.getLocationCode())
				.with(EmployeeDTO::setLocationName, employee.getLocationName())
				.with(EmployeeDTO::setManagerId, employee.getManagerId())
				.with(EmployeeDTO::setManagerName, employee.getManagerName())
				.with(EmployeeDTO::setMiddleName, employee.getMiddleName())
				.with(EmployeeDTO::setOfficialMobile, employee.getOfficialMobile())
				.with(EmployeeDTO::setPayGradeId, employee.getPayGradeId())
				.with(EmployeeDTO::setPayGradeName, employee.getPayGradeName())
				.with(EmployeeDTO::setPersonalMobile, employee.getPersonalMobile())
				.with(EmployeeDTO::setSalutation, employee.getSalutation())
				.with(EmployeeDTO::setTenure, employee.getTenure())
				.with(EmployeeDTO::setVerticalCode, employee.getVerticalCode())
				.with(EmployeeDTO::setVerticalName, employee.getVerticalName())
				.with(EmployeeDTO::setRole, employee.getRole())
				.build();
	}

	public static Employee convertDTOToEmployee(EmployeeDTO employeeDTO, Employee employee, String role) {

		if (Optional.ofNullable(employee).isPresent()) {
			return EVPWebGenericBuilder.of(() -> employee).with(Employee::setEmployeeId, employeeDTO.getEmployeeId())
					.with(Employee::setActive, employeeDTO.getActive())
					.with(Employee::setBloodGroup, employeeDTO.getBloodGroup())
					.with(Employee::setCompany, employeeDTO.getCompany())
					.with(Employee::setConfirmationDate, employeeDTO.getConfirmationDate())
					.with(Employee::setConfirmationStatus, employeeDTO.getConfirmationStatus())
					.with(Employee::setDateOfBirth, employeeDTO.getDateOfBirth())
					.with(Employee::setDepartmentCode, employeeDTO.getDepartmentCode())
					.with(Employee::setDepartmentName, employeeDTO.getDepartmentName())
					.with(Employee::setDivision_Code, employeeDTO.getDivision_Code())
					.with(Employee::setDivisionName, employeeDTO.getDivisionName())
					.with(Employee::setEmail, employeeDTO.getEmail())
					.with(Employee::setEmployeeName, employeeDTO.getEmployeeName())
					.with(Employee::setExitDate, employeeDTO.getExitDate())
					.with(Employee::setFirstName, employeeDTO.getFirstName())
					.with(Employee::setFunction_Code, employeeDTO.getFunction_Code())
					.with(Employee::setFunctionName, employeeDTO.getFunctionName())
					.with(Employee::setGender, employeeDTO.getGender())
					.with(Employee::setHireDate, employeeDTO.getHireDate())
					.with(Employee::setJobCode, employeeDTO.getJobCode())
					.with(Employee::setJobTitle, employeeDTO.getJobTitle())
					.with(Employee::setLastName, employeeDTO.getLastName())
					.with(Employee::setLastWorkingDay, employeeDTO.getLastWorkingDay())
					.with(Employee::setLocationCode, employeeDTO.getLocationCode())
					.with(Employee::setLocationName, employeeDTO.getLocationName())
					.with(Employee::setManagerId, employeeDTO.getManagerId())
					.with(Employee::setManagerName, employeeDTO.getManagerName())
					.with(Employee::setMiddleName, employeeDTO.getMiddleName())
					.with(Employee::setOfficialMobile, employeeDTO.getOfficialMobile())
					.with(Employee::setPayGradeId, employeeDTO.getPayGradeId())
					.with(Employee::setPayGradeName, employeeDTO.getPayGradeName())
					.with(Employee::setPersonalMobile, employeeDTO.getPersonalMobile())
					.with(Employee::setSalutation, employeeDTO.getSalutation())
					.with(Employee::setTenure, employeeDTO.getTenure())
					.with(Employee::setVerticalCode, employeeDTO.getVerticalCode())
					.with(Employee::setVerticalName, employeeDTO.getVerticalName()).with(Employee::setRole, role)
					.build();
		} else {
			return EVPWebGenericBuilder.of(Employee::new).with(Employee::setEmployeeId, employeeDTO.getEmployeeId())
					.with(Employee::setActive, employeeDTO.getActive())
					.with(Employee::setBloodGroup, employeeDTO.getBloodGroup())
					.with(Employee::setCompany, employeeDTO.getCompany())
					.with(Employee::setConfirmationDate, employeeDTO.getConfirmationDate())
					.with(Employee::setConfirmationStatus, employeeDTO.getConfirmationStatus())
					.with(Employee::setDateOfBirth, employeeDTO.getDateOfBirth())
					.with(Employee::setDepartmentCode, employeeDTO.getDepartmentCode())
					.with(Employee::setDepartmentName, employeeDTO.getDepartmentName())
					.with(Employee::setDivision_Code, employeeDTO.getDivision_Code())
					.with(Employee::setDivisionName, employeeDTO.getDivisionName())
					.with(Employee::setEmail, employeeDTO.getEmail())
					.with(Employee::setEmployeeName, employeeDTO.getEmployeeName())
					.with(Employee::setExitDate, employeeDTO.getExitDate())
					.with(Employee::setFirstName, employeeDTO.getFirstName())
					.with(Employee::setFunction_Code, employeeDTO.getFunction_Code())
					.with(Employee::setFunctionName, employeeDTO.getFunctionName())
					.with(Employee::setGender, employeeDTO.getGender())
					.with(Employee::setHireDate, employeeDTO.getHireDate())
					.with(Employee::setJobCode, employeeDTO.getJobCode())
					.with(Employee::setJobTitle, employeeDTO.getJobTitle())
					.with(Employee::setLastName, employeeDTO.getLastName())
					.with(Employee::setLastWorkingDay, employeeDTO.getLastWorkingDay())
					.with(Employee::setLocationCode, employeeDTO.getLocationCode())
					.with(Employee::setLocationName, employeeDTO.getLocationName())
					.with(Employee::setManagerId, employeeDTO.getManagerId())
					.with(Employee::setManagerName, employeeDTO.getManagerName())
					.with(Employee::setMiddleName, employeeDTO.getMiddleName())
					.with(Employee::setOfficialMobile, employeeDTO.getOfficialMobile())
					.with(Employee::setPayGradeId, employeeDTO.getPayGradeId())
					.with(Employee::setPayGradeName, employeeDTO.getPayGradeName())
					.with(Employee::setPersonalMobile, employeeDTO.getPersonalMobile())
					.with(Employee::setSalutation, employeeDTO.getSalutation())
					.with(Employee::setTenure, employeeDTO.getTenure())
					.with(Employee::setVerticalCode, employeeDTO.getVerticalCode())
					.with(Employee::setVerticalName, employeeDTO.getVerticalName()).with(Employee::setRole, role)
					.build();
		}

	}

	public static SearchCriteria buildSearchCriteria(String searchCriteria) throws EvpException {

		if (Optional.ofNullable(searchCriteria).isPresent() && !searchCriteria.isEmpty()) {
			String[] searchCriteriaParams = searchCriteria.split(":");
			Map<String, Object> criteriaMap = new HashMap<>(searchCriteriaParams.length);
			Arrays.stream(searchCriteriaParams).forEach(param -> {
				String[] parameters = param.split("=");
				if (Optional.ofNullable(parameters).isPresent() && parameters.length > 1) {
					String key = parameters[0];
					String value = parameters[1];
					criteriaMap.put(key, value);
				}

			});

			if (!criteriaMap.isEmpty()) {
				SearchCriteria criteria = SearchCriteria.builder()
						.modeOfParticipation((String) criteriaMap.get("modeOfParticipation"))
						.themeName((String) criteriaMap.get("themeName")).location((String) criteriaMap.get("location"))
						.startDate((String) criteriaMap.get("startDate")).endDate((String) criteriaMap.get("endDate"))
						.tagName((String) criteriaMap.get("tagName")).employeeId((String) criteriaMap.get("employeeId"))
						.activityUUID((String) criteriaMap.get("activityUUID"))
						.timeRequired((String) criteriaMap.get("timerequired"))
						.rating((String) criteriaMap.get("rating")).activityId((String) criteriaMap.get("activityId"))
						.themeName((String) criteriaMap.get("themeName"))
						.imageName((String) criteriaMap.get("imageName"))
						.fieldValueToSearch((String) criteriaMap.get("fieldValueToSearch"))
						.build();

				if (criteriaMap.size() > 0 && criteria.isEmpty()) {
					throw new EvpException("Search Criteria is Empty");
				} else {
					return criteria;
				}
			}
		}
		return null;
	}

	public static CreateOrUpdateActivityDTO convertActivityToDTO(Activity activity, String theme, String location,
			String mode, String tag) throws SQLException {

		StringBuilder timeRequiredBuilder = getTimeRequired(activity);

		return EVPWebGenericBuilder.of(CreateOrUpdateActivityDTO::new)
				.with(CreateOrUpdateActivityDTO::setActivityUUID, activity.getActivityUUID())
				.with(CreateOrUpdateActivityDTO::setActivityName, activity.getActivityName())
				.with(CreateOrUpdateActivityDTO::setActivityPlace, activity.getActivityPlace())
				.with(CreateOrUpdateActivityDTO::setBadgeToBeProvided, activity.getBadgeToBeProvided())
				.with(CreateOrUpdateActivityDTO::setBriefDescription, activity.getBriefDescription())
				.with(CreateOrUpdateActivityDTO::setCompleteDescription, activity.getCompleteDescription())
				.with(CreateOrUpdateActivityDTO::setContactPerson, activity.getContactPerson())
				.with(CreateOrUpdateActivityDTO::setContanctEmail, activity.getContanctEmail())
				.with(CreateOrUpdateActivityDTO::setCreated, activity.isCreatedActivity())
				.with(CreateOrUpdateActivityDTO::setPublished, activity.isPublished())
				.with(CreateOrUpdateActivityDTO::setCsrAdminLocation, activity.getCsrAdminLocation())
				.with(CreateOrUpdateActivityDTO::setDosInstruction, activity.getDosInstruction())
				.with(CreateOrUpdateActivityDTO::setDontInstruction, activity.getDontInstruction())
				.with(CreateOrUpdateActivityDTO::setCsrAdminLocation, activity.getCsrAdminLocation())
				.with(CreateOrUpdateActivityDTO::setEndDate, formatLocalDateTimeWithTime(activity.getEndDate()))
				.with(CreateOrUpdateActivityDTO::setStartDate, formatLocalDateTimeWithTime(activity.getStartDate()))
				.with(CreateOrUpdateActivityDTO::setNeedRequestFromCCSR, activity.isNeedRequestFromCCSR())
				.with(CreateOrUpdateActivityDTO::setRequestFromCCSR, activity.getRequestFromCCSR())
				.with(CreateOrUpdateActivityDTO::setObjectiveActivity, activity.getObjectiveActivity())
				.with(CreateOrUpdateActivityDTO::setPublished, activity.isPublished())
				.with(CreateOrUpdateActivityDTO::setTimeOfActivity, activity.getTimeOfActivity())
				.with(CreateOrUpdateActivityDTO::setTimeRequired, timeRequiredBuilder.toString())
				.with(CreateOrUpdateActivityDTO::setThemeName, theme)
				.with(CreateOrUpdateActivityDTO::setActivityLocation, location)
				.with(CreateOrUpdateActivityDTO::setTagName, tag)
				.with(CreateOrUpdateActivityDTO::setActivityFinancialId, activity.getActivityFinancialId())
				.with(CreateOrUpdateActivityDTO::setPastVideoUrl, activity.getPastVideoUrl())
				.with(CreateOrUpdateActivityDTO::setModeOfParticipation, mode)
				.with(CreateOrUpdateActivityDTO::setTestimonial,
						activity.getTestimonial() != null ? CommonUtils.clobToString(activity.getTestimonial()) : "")
				.with(CreateOrUpdateActivityDTO::setTestimonialPersonName, activity.getTestimonialPersonName())
				.with(CreateOrUpdateActivityDTO::setRating, activity.getRating())
				.with(CreateOrUpdateActivityDTO::setPastVideoCaption, activity.getPastVideoCaption())
				.with(CreateOrUpdateActivityDTO::setActivityId, activity.getActivityId()).build();
	}

	public static StringBuilder getTimeRequired(Activity activity) {
		Long timeRequiredHours = activity.getTimeRequiredHours();
		Long timeRequiredMinutes = activity.getTimeRequiredMinutes();
		StringBuilder timeRequiredBuilder = new StringBuilder();

		if (Optional.ofNullable(timeRequiredHours).isPresent()
				&& Optional.ofNullable(timeRequiredMinutes).isPresent()) {

			timeRequiredBuilder.append(timeRequiredHours).append(" Hours ").append(timeRequiredMinutes)
					.append(" minutes");

		}
		return timeRequiredBuilder;
	}

	public static List<CreateOrUpdateActivityDTO> convertActivitiesListToDTO(Map<Long, String> locationLovMap,
			Map<Long, String> themeLovMap, Map<Long, String> modeLovMap, Map<Long, String> tagLovMap,
			List<Activity> activityList, Map<String, List<Long>> activityLocationMap) {
		return activityList.stream().map(activity -> {
			String theme = themeLovMap.get(activity.getThemeNameId());
			String mode = modeLovMap.get(activity.getModeOfParticipationId());
			String tag = tagLovMap.get(activity.getTagId());
			String location="";
			List<Long> locationIds = activityLocationMap.get(activity.getActivityId());
			if(Optional.ofNullable(locationIds).isPresent()) {
				location = locationIds.stream().map(locationId -> locationLovMap.get(locationId))
						.collect(Collectors.joining(","));

			}else {
				log.info("Activity Id "+activity.getActivityId()+" doesn't have location");
			}
			
			try {
				return convertActivityToDTO(activity, theme, location, mode, tag);
			} catch (SQLException e) {
				return null;
			}

		}).collect(Collectors.toList());
	}

	public static LocalDateTime getActivityDate(String date) throws EvpException {
		try {
			DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd/MM/yyyy hh:mm:ss a").toFormatter();
					LocalDateTime startDate = LocalDateTime.parse(date, dateTimeFormatter);

			return formatLocalDateTime(startDate);
		} catch (Exception e) {
			throw new EvpException("Please provide valid date");
		}

	}

	public static LocalDateTime getActivityDateParam(String date) throws EvpException {
		try {
			LocalDate localDate = getLocalDate(date);

			return localDate.atStartOfDay();
		} catch (Exception e) {
			throw new EvpException("Please provide valid date");
		}

	}

	public static LocalDate getLocalDate(String date) {
		DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd/MM/yyyy").toFormatter();

		LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);
		return localDate;
	}

	private static LocalDateTime formatLocalDateTime(LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd/MM/yyyy hh:mm:ss a").toFormatter();
		String formatted = dateTimeFormatter1.format(localDateTime);
		return LocalDateTime.parse(formatted, dateTimeFormatter1);
	}

	public static String formatLocalDateTimeWithTime(LocalDateTime localDateTime) {
		DateTimeFormatter dateTimeFormatter1 = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd/MM/yyyy hh:mm:ss a").toFormatter();
		String formatted = dateTimeFormatter1.format(localDateTime);
		return formatted;
	}

	public static String formatLocalDateTime(LocalDate now) {
		DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("dd-MM-yyyy").toFormatter();
		String formatted = dateTimeFormatter.format(now);
		return formatted;
	}

	public static RestTemplate buildRestTemplate(boolean login, String username, String password) {

		if (login) {

			return buildRestTemplateBuilder().basicAuthentication(username, password).build();
		} else {
			return new RestTemplate();
		}

	}

	private static RestTemplateBuilder buildRestTemplateBuilder() {
		return new RestTemplateBuilder();
	}

	public static String getEmployeeIdFromUsername(String username) {
		username = username.split("@")[0];
		if (username.contains("P00")) {
			username = username.split("P00")[1];
		}
		return username;

	}

	public static Map<Long, String> getLovMapWithIdKey(Map<String, Long> lovMap) {
		return lovMap.entrySet().stream().collect(Collectors.toMap(lov -> lov.getValue(), lov -> lov.getKey()));

	}

	public static SearchCriteria buildParamsForSearchCriteria(SearchCriteria searchCriteria,
			Map<String, Long> locationLovMap, Map<String, Long> themeLovMap, Map<String, Long> modeLovMap,
			Map<String, Long> tagLovMap) {
		String activityName = searchCriteria.getActivityId();
		if (Optional.ofNullable(activityName).isPresent()) {
			String[] activityNameArray = activityName.split(",");
			List<String> activityNames = Stream.of(activityNameArray).collect(Collectors.toList());
			searchCriteria.setActivityIds(activityNames);
		}

		String location = searchCriteria.getLocation();
		if (Optional.ofNullable(location).isPresent()) {
			String[] locationArray = location.split(",");
			List<Long> locationId = Stream.of(locationArray).map(l -> locationLovMap.get(l))
					.collect(Collectors.toList());
			searchCriteria.setLocationId(locationId);
		}

		String themeName = searchCriteria.getThemeName();
		if (Optional.ofNullable(themeName).isPresent()) {
			String[] themeArray = themeName.split(",");
			List<Long> themeId = Stream.of(themeArray).map(t -> themeLovMap.get(t)).collect(Collectors.toList());
			searchCriteria.setThemeNameId(themeId);
		}
		String modeOfParticipation = searchCriteria.getModeOfParticipation();
		if (Optional.ofNullable(modeOfParticipation).isPresent()) {
			String[] modeArray = modeOfParticipation.split(",");
			List<Long> modeId = Stream.of(modeArray).map(m -> modeLovMap.get(m)).collect(Collectors.toList());
			searchCriteria.setModeOfParticipationId(modeId);
		}

		String tagName = searchCriteria.getTagName();
		if (Optional.ofNullable(tagName).isPresent()) {
			String[] tagArray = tagName.split(",");
			List<Long> tagId = Stream.of(tagArray).map(t -> tagLovMap.get(t)).collect(Collectors.toList());
			searchCriteria.setTagIds(tagId);

		}

		String activityId = searchCriteria.getActivityId();
		if (Optional.ofNullable(activityId).isPresent()) {
			String[] activityIdArray = activityId.split(",");
			List<String> activityIds = Stream.of(activityIdArray).collect(Collectors.toList());
			searchCriteria.setActivityIds(activityIds);
		}

		String employeeId = searchCriteria.getEmployeeId();
		if (Optional.ofNullable(employeeId).isPresent()) {
			String[] employeeIdArray = employeeId.split(",");
			List<String> employeeIds = Stream.of(employeeIdArray).collect(Collectors.toList());
			searchCriteria.setEmployeeIds(employeeIds);
		}
		

		return searchCriteria;
	}

	public static SearchCriteria buildParamsForSearchCriteriaForGalery(SearchCriteria searchCriteria) {
		String activityName = searchCriteria.getActivityId();
		if (Optional.ofNullable(activityName).isPresent()) {
			String[] activityNameArray = activityName.split(",");
			List<String> activityNames = Stream.of(activityNameArray).collect(Collectors.toList());
			searchCriteria.setActivityIds(activityNames);
		}

		String location = searchCriteria.getLocation();
		if (Optional.ofNullable(location).isPresent()) {
			String[] locationArray = location.split(",");
			List<String> locationId = Stream.of(locationArray).collect(Collectors.toList());
			searchCriteria.setLocations(locationId);
		}

		String themeName = searchCriteria.getThemeName();
		if (Optional.ofNullable(themeName).isPresent()) {
			String[] themeArray = themeName.split(",");
			List<String> themeId = Stream.of(themeArray).collect(Collectors.toList());
			searchCriteria.setThemeNames(themeId);
		}
		String modeOfParticipation = searchCriteria.getModeOfParticipation();
		if (Optional.ofNullable(modeOfParticipation).isPresent()) {
			String[] modeArray = modeOfParticipation.split(",");
			List<String> modeId = Stream.of(modeArray).collect(Collectors.toList());
			searchCriteria.setModeOfParticipations(modeId);
		}

		String tagName = searchCriteria.getTagName();
		if (Optional.ofNullable(tagName).isPresent()) {
			String[] tagArray = tagName.split(",");
			List<String> tagId = Stream.of(tagArray).collect(Collectors.toList());
			searchCriteria.setTagNames(tagId);

		}

		if (Optional.ofNullable(searchCriteria.getImageName()).isPresent()) {
			String[] activityNameArray = searchCriteria.getImageName().split(",");
			List<String> activityNames = Stream.of(activityNameArray).collect(Collectors.toList());
			searchCriteria.setImageNames(activityNames);
		}

		String activityId = searchCriteria.getActivityId();
		if (Optional.ofNullable(activityId).isPresent()) {
			String[] activityIdArray = activityId.split(",");
			List<String> activityIds = Stream.of(activityIdArray).collect(Collectors.toList());
			searchCriteria.setActivityIds(activityIds);
		}

		return searchCriteria;
	}

	public static <T> List<List<T>> batchesOfList(List<T> source, int length) {
		int size = source.size();
		if (size <= 0)
			return Collections.emptyList();
		int fullChunks = (size - 1) / length;
		return IntStream.range(0, fullChunks + 1)
				.mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length))
				.collect(Collectors.toList());
	}

	public static EmployeeActivityHistoryResponseDTO getPaginationDetailsForEmployeeActivityHistory(Integer pageNo,
			Integer pageSize, Integer totalNumberOfActivities) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		if (totalNumberPages > 0) {
			return EmployeeActivityHistoryResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious).pageNo(pageNo)
					.pageSize(pageSize).totalPages(Long.valueOf(totalNumberPages).intValue())
					.totalElements(totalNumberOfActivities.intValue()).build();

		} else {
			return EmployeeActivityHistoryResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.totalPages(Long.valueOf(totalNumberPages).intValue()).build();
		}

	}
	
	
	public static ActivityPromotionResponseDTO getPaginationForActivityPromotion(Integer pageNo,
			Integer pageSize, Integer totalNumberOfActivities) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		if (totalNumberPages > 0) {
			return ActivityPromotionResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious).pageNo(pageNo)
					.pageSize(pageSize).totalPages(Long.valueOf(totalNumberPages).intValue())
					.totalElements(totalNumberOfActivities.intValue()).build();

		} else {
			return ActivityPromotionResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.totalPages(Long.valueOf(totalNumberPages).intValue()).build();
		}

	}
	
	
	public static PublishedImages getPaginationDetailsForPublishedImages(Integer pageNo,
			Integer pageSize, Integer totalNumberOfActivities) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		if (totalNumberPages > 0) {
			return PublishedImages.builder().hasNext(hasNext).hasPrevious(hasPrevious).pageNo(pageNo)
					.pageSize(pageSize).totalPages(Long.valueOf(totalNumberPages).intValue())
					.totalElements(totalNumberOfActivities.intValue()).build();

		} else {
			return PublishedImages.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.totalPages(Long.valueOf(totalNumberPages).intValue()).build();
		}

	}
	
	
	public static ImageResponse getPaginationDetailsForActivityPiccture(Integer pageNo,
			Integer pageSize, Integer totalNumberOfActivities) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		if (totalNumberPages > 0) {
			return ImageResponse.builder().hasNext(hasNext).hasPrevious(hasPrevious).pageNo(pageNo)
					.pageSize(pageSize).totalPages(Long.valueOf(totalNumberPages).intValue())
					.totalElements(totalNumberOfActivities.intValue()).build();

		} else {
			return ImageResponse.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.totalPages(Long.valueOf(totalNumberPages).intValue()).build();
		}

	}

	public static DashBoardPastActivityDetailsResponseDTO getPaginationDetailsForPastActivity(Integer pageNo,
			Integer pageSize, Integer totalNumberOfActivities) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		if (totalNumberPages > 0) {
			return DashBoardPastActivityDetailsResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.pageNo(pageNo).pageSize(pageSize).totalPages(Long.valueOf(totalNumberPages).intValue())
					.totalElements(totalNumberOfActivities.intValue()).build();

		} else {
			return DashBoardPastActivityDetailsResponseDTO.builder().hasNext(hasNext).hasPrevious(hasPrevious)
					.totalPages(Long.valueOf(totalNumberPages).intValue()).build();
		}

	}

	public static Object getPaginationDetailsForActivity(Integer pageNo, Integer pageSize,
			Integer totalNumberOfActivities, ActivityType activityType, boolean getEnrolledEmployees,
			List<Activity> activities, Map<Long, String> locationLovMap, Map<Long, String> themeLovMap,
			Map<Long, String> modeLovMap, Map<Long, String> tagLovMap, Map<String, List<Long>> activityLocationMap) {
		Boolean hasNext = Boolean.FALSE;
		Boolean hasPrevious = Boolean.FALSE;
		long totalNumberPages = 0L;
		if (totalNumberOfActivities > 0) {
			totalNumberPages = totalNumberOfActivities / pageSize;
			totalNumberPages = totalNumberOfActivities % pageSize > 0 ? totalNumberPages + 1 : totalNumberPages;
			if (pageNo < totalNumberPages) {
				hasNext = Boolean.TRUE;

			} else if (pageNo.equals(Long.valueOf(totalNumberPages).intValue())) {
				hasNext = Boolean.FALSE;
			}
			if (pageNo.equals(1)) {
				hasPrevious = false;

			} else if (pageNo > 1 || pageNo < totalNumberPages) {
				hasPrevious = Boolean.TRUE;
			}
		}

		List<CreateOrUpdateActivityDTO> activityList = convertActivitiesListToDTO(locationLovMap, themeLovMap,
				modeLovMap, tagLovMap, activities, activityLocationMap);

		switch (activityType) {
		case ONGOING:
			OngoingActivities ongoingActivities = OngoingActivities.builder().build();
			if (getEnrolledEmployees) {
				ongoingActivities.setHasNext(hasNext);
				ongoingActivities.setHasPrevious(hasPrevious);
				ongoingActivities.setPageNo(pageNo);
				ongoingActivities.setPageSize(pageSize);

				ongoingActivities.setTotalElements(totalNumberOfActivities.intValue());
				ongoingActivities.setTotalPages(Long.valueOf(totalNumberPages).intValue());

			}

			if (getEnrolledEmployees) {
				List<List<CreateOrUpdateActivityDTO>> batchesOngiong = batchesOfList(activityList, pageSize);

				if (batchesOngiong.isEmpty()) {
					ongoingActivities.setOngoingActivities(Collections.emptyList());
				} else if (batchesOngiong.size() <= pageNo - 1) {
					ongoingActivities.setOngoingActivities(Collections.emptyList());
				} else {
					ongoingActivities.setOngoingActivities(batchesOngiong.get(pageNo - 1));
				}

			} else {
				ongoingActivities.setOngoingActivities(activityList);
			}

			return ongoingActivities;

		case UPCOMING:
			UpcomingActivities upcomingActivities = UpcomingActivities.builder().build();
			if (getEnrolledEmployees) {
				upcomingActivities.setHasNext(hasNext);
				upcomingActivities.setHasPrevious(hasPrevious);
				upcomingActivities.setPageNo(pageNo);
				upcomingActivities.setPageSize(pageSize);

				upcomingActivities.setTotalElements(totalNumberOfActivities.intValue());
				upcomingActivities.setTotalPages(Long.valueOf(totalNumberPages).intValue());

			}
			if (getEnrolledEmployees) {
				List<List<CreateOrUpdateActivityDTO>> batchesUpcoming = batchesOfList(activityList, pageSize);

				if (batchesUpcoming.isEmpty()) {
					upcomingActivities.setUpcomingActivities(Collections.emptyList());
				} else if (batchesUpcoming.size() <= pageNo - 1) {
					upcomingActivities.setUpcomingActivities(Collections.emptyList());
				} else {
					upcomingActivities.setUpcomingActivities(batchesUpcoming.get(pageNo - 1));
				}
			} else {
				upcomingActivities.setUpcomingActivities(activityList);
			}

			return upcomingActivities;
		case PAST:
			PastActivities pastActivities = PastActivities.builder().build();
			if (getEnrolledEmployees) {
				pastActivities.setHasNext(hasNext);
				pastActivities.setHasPrevious(hasPrevious);
				pastActivities.setPageNo(pageNo);
				pastActivities.setPageSize(pageSize);

				pastActivities.setTotalElements(totalNumberOfActivities.intValue());
				pastActivities.setTotalPages(Long.valueOf(totalNumberPages).intValue());

			}
			if (getEnrolledEmployees) {
				List<List<CreateOrUpdateActivityDTO>> batchesPast = batchesOfList(activityList, pageSize);

				if (batchesPast.isEmpty()) {
					pastActivities.setPastActivities(Collections.emptyList());
				} else if (batchesPast.size() <= pageNo - 1) {
					pastActivities.setPastActivities(Collections.emptyList());
				} else {
					pastActivities.setPastActivities(batchesPast.get(pageNo - 1));
				}
			} else {
				pastActivities.setPastActivities(activityList);
			}

			return pastActivities;

		case CREATED:
			CreatedActivities createdActivities = CreatedActivities.builder().build();
			if (getEnrolledEmployees) {
				createdActivities.setHasNext(hasNext);
				createdActivities.setHasPrevious(hasPrevious);
				createdActivities.setPageNo(pageNo);
				createdActivities.setPageSize(pageSize);

				createdActivities.setTotalElements(totalNumberOfActivities.intValue());
				createdActivities.setTotalPages(Long.valueOf(totalNumberPages).intValue());

			}
			if (getEnrolledEmployees) {
				List<List<CreateOrUpdateActivityDTO>> batchesCreated = batchesOfList(activityList, pageSize);

				if (batchesCreated.isEmpty()) {
					createdActivities.setCreatedActivities(Collections.emptyList());
				} else if (batchesCreated.size() <= pageNo - 1) {
					createdActivities.setCreatedActivities(Collections.emptyList());
				} else {
					createdActivities.setCreatedActivities(batchesCreated.get(pageNo - 1));
				}
			} else {
				createdActivities.setCreatedActivities(activityList);
			}
			return createdActivities;

		default:
			return null;
		}

	}

	public static List<ImageDTO> convertActivityPicturesToImageDTO(List<ActivityPicture> activityPictures,
			String imageType, Map<String, String> activityMap) {
		if (Optional.ofNullable(activityPictures).isPresent() && !activityPictures.isEmpty()) {
			return activityPictures.stream().map(activityPicture -> {
				return convertToImageDTO(imageType, activityMap, activityPicture);
			}).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	public static ImageDTO convertToImageDTO(String imageType, Map<String, String> activityMap,
			ActivityPicture activityPicture) {
		ImageDTO imageDTO = ImageDTO.builder().build();
		imageDTO.setActivityId(activityPicture.getActivityPictureId());
		imageDTO.setActivityName(activityMap.get(activityPicture.getActivityPictureId()));
		imageDTO.setImageName(activityPicture.getImageName());
		imageDTO.setImageType(imageType);
		imageDTO.setImageUrl(activityPicture.getActivityPictureLocation());
		imageDTO.setPublished(activityPicture.getPublished());
		imageDTO.setUploadedByAdmin(activityPicture.isUploadedByAdmin());
		imageDTO.setCoverPhoto(activityPicture.isCoverPhoto());
		imageDTO.setCaption(activityPicture.getCaption());
		imageDTO.setContainerLocation(activityPicture.getContainerLocation());
		imageDTO.setCoverPhoto(activityPicture.isCoverPhoto());
		imageDTO.setDeleted(activityPicture.getDeleted());
		imageDTO.setEndDate(activityPicture.getEndDate());
		imageDTO.setStartDate(activityPicture.getStartDate());
		imageDTO.setMode(activityPicture.getMode());
		imageDTO.setUploadedBy(activityPicture.getUploadedBy());
		imageDTO.setActivityLocation(activityPicture.getActivityLocation());
		imageDTO.setActivityTag(activityPicture.getActivityTag());
		imageDTO.setUploadedDate(activityPicture.getCreatedOn().toLocalDate().toString());
		imageDTO.setFeedbackId(activityPicture.getFeedBackId());
		imageDTO.setPromotionId(activityPicture.getPromotionId());
		
		return imageDTO;
	}

	public static Optional<SimpleGrantedAuthority> getAuthRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Optional<SimpleGrantedAuthority> auth = (Optional<SimpleGrantedAuthority>) authentication.getAuthorities()
				.stream().filter(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")).findFirst();
		return auth;
	}
	@SuppressWarnings("unchecked")
	public static String getAssignedRole(HttpServletRequest request, JwtTokenUtil jwtUtil) {
		String rolesAssigned = null;
		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			try {
				String jwtToken = requestTokenHeader.substring(7);
				String[] parts = jwtToken.split("\\.");
				
				Map<String, String> map= new ObjectMapper().readValue(decode(parts[1]), Map.class);
				
				rolesAssigned =  map.get("role");
			}catch (Exception e) {
				log.error(e.getMessage());	
			}
			
		}
		return rolesAssigned;
	}
	@SuppressWarnings("unchecked")
	public static String getUsername(HttpServletRequest request, JwtTokenUtil jwtUtil) {
		String username = null;
		try {
		final String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			String jwtToken = requestTokenHeader.substring(7);
			String[] parts = jwtToken.split("\\.");
			
			
			Map<String, String> map= new ObjectMapper().readValue(decode(parts[1]), Map.class);
			username= map.get("sub");
		}
		}catch (Exception e) {
			log.error(e.getMessage());		
			}
		return username;
	}

	private static String decode(String jwtToken) {
		return new String(Base64.getUrlDecoder().decode(jwtToken));
	}

	public static String getStorageString(String accountName, String accountKey) {
		StringBuilder sb = new StringBuilder();
		return sb.append("DefaultEndpointsProtocol=https;AccountName=").append(accountName).append("AccountKey=")
				.append(accountKey).append("EndpointSuffix=core.windows.net").toString();

	}

	public static ActivityFeedback convertactivityFeedbackDtoToActivityFeedback(ActivityFeedbackDTO dto,
			LocalDateTime startdate, LocalDateTime endDate) throws SerialException, SQLException {
		SerialClob serialClob = new SerialClob(dto.getFeedback().toCharArray());
		return EVPWebGenericBuilder.of(ActivityFeedback::new)
				.with(ActivityFeedback::setActivityName, dto.getActivityId())
				.with(ActivityFeedback::setEmployeeId, dto.getEmployeeId()).with(ActivityFeedback::setEndDate, endDate)
				.with(ActivityFeedback::setFeedback, serialClob).with(ActivityFeedback::setLocation, dto.getLocation())
				.with(ActivityFeedback::setMode, dto.getMode()).with(ActivityFeedback::setRating, dto.getRating())
				.with(ActivityFeedback::setStartDate, startdate)
				.with(ActivityFeedback::setStartDate, startdate).with(ActivityFeedback::setTagName, dto.getTagName())
				.with(ActivityFeedback::setThemeName, dto.getThemeName())
				.with(ActivityFeedback::setTimeRequired, dto.getTimeRequired())
				.with(ActivityFeedback::setManualUpload, dto.isManualUpload())
				.build();
	}

	public static LocalDateTime stringToLocalDateTime(String stringDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(stringDate, formatter);

	}

	public static String generateActivityId(String location, String theme) {
		StringBuilder activityIdBuilder = new StringBuilder();
		String lo = location.substring(0, 2);
		String th = theme.substring(0, 2);

		int leftLimit = 48; // letter 'a'
		int rightLimit = 57; // letter 'z'
		int targetStringLength = 6;

		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return activityIdBuilder.append(lo).append(th).append(generatedString).toString();

	}
	
	public static String generateActivityIdForMultiLocation(String theme) {
		StringBuilder activityIdBuilder = new StringBuilder();
		String th = theme.substring(0, 2);

		int leftLimit = 48; // letter 'a'
		int rightLimit = 57; // letter 'z'
		int targetStringLength = 6;

		Random random = new Random();

		String generatedString = random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

		return activityIdBuilder.append("MUL").append(th).append(generatedString).toString();

	}

	public static String clobToString(Clob data) {
		StringBuilder sb = new StringBuilder();
		
		if(Optional.ofNullable(data).isPresent()) {
			try {
				Reader reader = data.getCharacterStream();
				BufferedReader br = new BufferedReader(reader);

				String line;
				while (null != (line = br.readLine())) {
					sb.append(line);
				}
				br.close();
			} catch (SQLException e) {
				// handle this exception
			} catch (IOException e) {
				// handle this exception
			}
		}
		
		
		
		return sb.toString();
	}

	public static List<EmployeeActivityResponse> convertEmployeeActivityHistoryToDTO(
			List<EmployeeActivityHistory> employeeActivityHistories, Map<String, String> activityMap) {

		List<EmployeeActivityResponse> employeeActivityResponses = new ArrayList<>(employeeActivityHistories.size());

		employeeActivityHistories.forEach(employeeActivityHistory -> {
			EmployeeActivityResponse employeeActivityResponse=EmployeeActivityResponse.builder().activityId(employeeActivityHistory.getActivityName())
			.activityId(employeeActivityHistory.getActivityName())
			.activityName(activityMap.get(employeeActivityHistory.getActivityName()))
			.activityLocation(employeeActivityHistory.getActivityLocation())
			.activityTag(employeeActivityHistory.getActivityTag())
			.activityTheme(employeeActivityHistory.getActivityTheme())
			.activityUUID(employeeActivityHistory.getActivityUUID())
			.employeeActivityStatus(employeeActivityHistory.getEmployeeActivityStatus())
			.approvedByAdmin(employeeActivityHistory.isApprovedByAdmin())
			.rejectedByAdmin(employeeActivityHistory.isRejectedByAdmin())
			.employeeId(employeeActivityHistory.getEmployeeId())
			.employeeName(employeeActivityHistory.getEmployeeName())
			.mode(employeeActivityHistory.getMode())
			.participationHours(employeeActivityHistory.getParticipationHours())
			.departmentName(employeeActivityHistory.getDepartmentName())
			.build();
			employeeActivityResponses.add(employeeActivityResponse);
		});
		
		return employeeActivityResponses;

	}


}
