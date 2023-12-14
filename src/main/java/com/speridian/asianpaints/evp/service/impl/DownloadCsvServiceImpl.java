package com.speridian.asianpaints.evp.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.speridian.asianpaints.evp.constants.CsvCategories;
import com.speridian.asianpaints.evp.constants.CsvConfigs;
import com.speridian.asianpaints.evp.constants.DashBoardCsvCategories;
import com.speridian.asianpaints.evp.dto.ActivityFinancialResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityTagResponse;
import com.speridian.asianpaints.evp.dto.DashBoardDepartmentWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardEmployeeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardLocationWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardModeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardMonthWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardThemeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponseDTO;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.DashBoardService;
import com.speridian.asianpaints.evp.service.DownloadCsvService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.util.CommonSpecification;
import com.speridian.asianpaints.evp.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DownloadCsvServiceImpl implements DownloadCsvService {

	
	@Autowired
	private HttpServletResponse response;
	
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Autowired
	private EvpLovService evpLovService;
	
	@Autowired
	private DashBoardService dashBoardService;
	

	@Override
	public void writeCsvData(String category, String searchCriteria,String activityType,boolean dashBoardDetails) throws IOException, EvpException {
		PrintWriter printWriter = response.getWriter();
		try {
		
			try (ICsvBeanWriter csvWriter = new CsvBeanWriter(printWriter, CsvPreference.STANDARD_PREFERENCE)) {
				SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);

				log.info("Exporting data into CSV file");
				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=Data.csv";
				response.setContentType("text/csv");
				response.setHeader(headerKey, headerValue);
				CsvConfigs csvConfig = null;
				log.info("Header and Field Config for CSV file");

				Optional<CsvCategories> lovCategoryOpt = Optional.ofNullable(category).isPresent() ? Arrays
						.stream(CsvCategories.values()).filter(lov -> lov.getCategory().equals(category)).findFirst()
						: Optional.empty();

				if (lovCategoryOpt.isPresent()) {
					Optional<CsvConfigs> csvConfigOpt = Stream.of(CsvConfigs.values())
							.filter(csvConfigs -> csvConfigs.getLovCategory().equals(category)).findFirst();

					if (csvConfigOpt.isPresent()) {
						csvConfig = csvConfigOpt.get();
					}
				} else {
					throw new EvpException("Category Not Present");

				}

				if (Optional.ofNullable(csvConfig).isPresent()) {
					String[] csvHeader = csvConfig.getCsvHeader().split(",");
					String[] nameMapping = csvConfig.getCsvDbFields().split(",");

					List<?> data = getCsvData(criteria, lovCategoryOpt.get(),activityType,dashBoardDetails);

					csvWriter.writeHeader(csvHeader);
					for (Object d : data) {
						csvWriter.write(d, nameMapping);
					}
				}

			}
		}

		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new EvpException("Unable to download CSV file");
		} finally {
			printWriter.close();
		}
	}
	

	private List<?> getCsvData(SearchCriteria criteria, CsvCategories lovCategory,String activityType,boolean dashBoardDetails) throws EvpException {
		List<?> data = null;
		switch (lovCategory) {
		case PARTICIPANTS:
			try {
				
				
				EmployeeActivityResponseDTO employeeActivityResponseDTO= null;
						if(dashBoardDetails) {
							employeeActivityResponseDTO= activityService.getParticipantDetailsForActivityWithCriteriaForDashBoard(criteria, 1, 1,
									true);
						}else {
							employeeActivityResponseDTO=activityService.getActivityParticipantsWithCriteria(criteria, 1, 1, false,activityType,dashBoardDetails);
						}
						
				if(Optional.ofNullable(employeeActivityResponseDTO).isPresent()) {
					data=employeeActivityResponseDTO.getEmployeeActivityHistories();
				}
				
				
			} catch (EvpException e) {
				log.error(e.getMessage());
				throw new EvpException(e.getMessage());
			}
			
			break;

		case FINANCE:
			ActivityFinancialResponseDTO activityFinancialResponseDTO= activityService.getActivityFinancialsWithCriteria(criteria, 1, 1, false,activityType);
			data=activityFinancialResponseDTO.getActivityFinancials();
			break;
		}
		return data;
	}


	@Override
	public void writeCsvDataForDashBoard(String category, String subcategory, String searchCriteria)
			throws IOException, EvpException {
		
		PrintWriter printWriter = response.getWriter();
		try {
			try (ICsvListWriter  csvWriter = new CsvListWriter(printWriter, CsvPreference.STANDARD_PREFERENCE)) {
				
				log.info("Exporting data into CSV file");
				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=Data.csv";
				response.setContentType("text/csv");
				response.setHeader(headerKey, headerValue);
				DashBoardCsvCategories dashBoardCsvCategories=null;
				
				Optional<DashBoardCsvCategories> lovCategoryOpt = Optional.ofNullable(category).isPresent() ? Arrays
						.stream(DashBoardCsvCategories.values()).filter(lov -> lov.getCategory().equals(category))
						.filter(lov -> lov.getSubCategory().equals(subcategory))
						.findFirst()
						: Optional.empty();
				
				if (lovCategoryOpt.isPresent()) {
					dashBoardCsvCategories = lovCategoryOpt.get();
				}
				
				SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
				
				Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
				Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
				Map<String, Long> themeLovMap=evpLovService.getThemeLovMap();
				Map<String, Long> modeLovMap=evpLovService.getModeLovMap();
				criteria= CommonUtils.buildParamsForSearchCriteria(criteria, locationLovMap, themeLovMap, modeLovMap, tagLovMap);
				Specification<Activity> activitySpecs= CommonSpecification.allActivitySpecification(criteria);
				
				List<Activity> activities= activityRepository.findAll(activitySpecs);
				
				activities= activityService.filterByLocation(criteria.getLocationId(), activities);
				LocalDate currentDate=LocalDate.now();
				log.info("Retirivng Past activies");
				activities = activities.stream().filter(activity -> activity.isPublished())
						.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
						.collect(Collectors.toList());
				
				writeCsvData(csvWriter, dashBoardCsvCategories, activities,searchCriteria);
				
			}
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			throw new EvpException("Unable to download CSV file");
		} finally {
			printWriter.close();
		}
		
	}


	private void writeCsvData(ICsvListWriter csvWriter, DashBoardCsvCategories dashBoardCsvCategories,
			List<Activity> activities,String searchCriteria) throws EvpException, IOException {
		switch (dashBoardCsvCategories) {
		case THEME_WISE_PARTICIPATION:

			String[] csvheaders = { "Theme Name", "No of Participants" };
			DashBoardThemeWiseDataDTO dashBoardThemeWiseDataDTO = dashBoardService
					.getDashBoardThemeWiseData(activities,searchCriteria);
			Map<String, Long> dashBoardParticipantMap = dashBoardThemeWiseDataDTO.getNoOfParticipants();
			csvWriter.writeHeader(csvheaders);
			writeDataOther(csvWriter, dashBoardParticipantMap);

			break;

		case THEME_WISE_UNIQUE_PARTICIPATION:
			String[] csvheadersUni = { "Theme Name", "No of Unique Participants" };
			DashBoardThemeWiseDataDTO dashBoardThemeWiseData = dashBoardService.getDashBoardThemeWiseData(activities,searchCriteria);
			Map<Object, Object> uniqueParticipantsMap = dashBoardThemeWiseData.getUniqueParticipants();
			csvWriter.writeHeader(csvheadersUni);

			for (Map.Entry<Object, Object> dashBoardParticipantEntry : uniqueParticipantsMap.entrySet()) {
				csvWriter.write(dashBoardParticipantEntry.getKey(), dashBoardParticipantEntry.getValue());
			}

			break;
		case THEME_WISE_PARTICIPATION_HOUR_WISE:
			String[] csvheadersHours = { "Theme Name", "Participant Hours" };
			DashBoardThemeWiseDataDTO dashBoardThemeHourWiseData = dashBoardService
					.getDashBoardThemeWiseData(activities,searchCriteria);
			Map<String, Long> participationHoursMap = dashBoardThemeHourWiseData.getParticipantHours();
			csvWriter.writeHeader(csvheadersHours);

			writeDataOther(csvWriter, participationHoursMap);
			break;

		case MODE_WISE_PARTICIPATION:

			String[] csvheadersMode = { "Mode", "No of Participants" };
			DashBoardModeWiseDataDTO dashBoardModeWiseDataDTO = dashBoardService.getDashBoardModeWiseData(activities,searchCriteria);
			Map<String, Long> dashBoardModeParticipantMap = dashBoardModeWiseDataDTO.getNoOfParticipants();
			csvWriter.writeHeader(csvheadersMode);
			writeDataOther(csvWriter, dashBoardModeParticipantMap);

			break;

		case MODE_WISE_UNIQUE_PARTICIPATION:
			String[] csvheadersModeUni = { "Mode", "No of Unique Participants" };
			DashBoardModeWiseDataDTO dashBoardModeWiseData = dashBoardService.getDashBoardModeWiseData(activities,searchCriteria);
			Map<Object, Object> dashBoardModeUniqueParticipantsMap = dashBoardModeWiseData.getUniqueParticipants();
			csvWriter.writeHeader(csvheadersModeUni);

			for (Map.Entry<Object, Object> dashBoardParticipantEntry : dashBoardModeUniqueParticipantsMap.entrySet()) {
				csvWriter.write(dashBoardParticipantEntry.getKey(), dashBoardParticipantEntry.getValue());
			}

			break;
		case MODE_WISE_PARTICIPATION_HOUR:
			String[] csvheadersModeHours = { "Mode", "Participant Hours" };
			DashBoardModeWiseDataDTO dashBoardModeHourWiseDataDTO = dashBoardService
					.getDashBoardModeWiseData(activities,searchCriteria);
			Map<String, Long> participationModeHoursMap = dashBoardModeHourWiseDataDTO.getParticipantHours();
			csvWriter.writeHeader(csvheadersModeHours);

			writeDataOther(csvWriter, participationModeHoursMap);
			break;

		case MONTH_WISE_PARTICIPATION:

			String[] csvheadersMonth = { "Month","Tag Name", "No of Participants" };
			DashBoardMonthWiseDataDTO dashBoardMonthWiseDataDTO = dashBoardService
					.getDashBoardMonthWiseData(activities,searchCriteria);
			
			Map<Month, Map<String, Long>> noOfParticipantsMap= dashBoardMonthWiseDataDTO.getNoOfParticipants();
			
			Map<String, Map<String, Long>> dashboardMonths = noOfParticipantsMap.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> entry.getValue()));
			
			csvWriter.writeHeader(csvheadersMonth);
			writeData(csvWriter, dashboardMonths);

			break;

		case MONTH_WISE_UNIQUE_PARTICIPATION:
			String[] csvheadersMonthUni = { "Month", "Tag Name","No of Unique Participants" };
			DashBoardMonthWiseDataDTO dashBoardMonthWiseData = dashBoardService.getDashBoardMonthWiseData(activities,searchCriteria);
			
			Map<Month, Map<String, Long>> dashBoardMonthUniqueParticipantsMap= dashBoardMonthWiseData.getNoOfParticipants();
			
			Map<String, Map<String, Long>> dashboardMonthsUni = dashBoardMonthUniqueParticipantsMap.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> entry.getValue()));
			csvWriter.writeHeader(csvheadersMonthUni);
			writeData(csvWriter, dashboardMonthsUni);
			break;
		case MONTH_WISE_PARTICIPATION_HOUR:
			String[] csvheadersMonthHours = { "Month","Tag Name", "Participant Hours" };
			DashBoardMonthWiseDataDTO dashBoardMonthHourWiseDataDTO = dashBoardService
					.getDashBoardMonthWiseData(activities,searchCriteria);
			
			Map<Month, Map<String, Long>> participationMonthHoursMap = dashBoardMonthHourWiseDataDTO.getParticipantHours();
			Map<String, Map<String, Long>> participationMonthHours = participationMonthHoursMap.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey().name(), entry -> entry.getValue()));
			csvWriter.writeHeader(csvheadersMonthHours);
			writeData(csvWriter, participationMonthHours);
			break;
		case EMPLOYEE_WISE_UNIQUE_PARTICIPATION:

			String[] csvheadersEmployeeHours = { "Employee Id","Employee Name", "Activities" };
			DashBoardEmployeeWiseDataDTO dashBoardEmployeeWiseDataDTO = dashBoardService
					.getEmployeeWiseDashBoardData(activities,searchCriteria);
			Map<String, Map<String, Long>> noOfActivities = dashBoardEmployeeWiseDataDTO.getNoOfActivites();
			csvWriter.writeHeader(csvheadersEmployeeHours);

			writeData(csvWriter, noOfActivities);

			break;
		case EMPLOYEE_WISE_PARTICIPATION_HOUR:

			String[] csvheadersEmployeeHoursHeader = {  "Employee Id","Employee Name", "Participation Hours" };
			DashBoardEmployeeWiseDataDTO dashBoardEmployeeWiseDataDTOs = dashBoardService
					.getEmployeeWiseDashBoardData(activities,searchCriteria);
			Map<String, Map<String, Long>> noOfHours = dashBoardEmployeeWiseDataDTOs.getNoOfHours();
			csvWriter.writeHeader(csvheadersEmployeeHoursHeader);

			writeData(csvWriter, noOfHours);

			break;
			
		case LOCATION_WISE_PARTICIPATION:

			String[] csvheadersLocation = { "Location","Tag Name", "No of Participants" };
			DashBoardLocationWiseDataDTO dashBoardLocationWiseDataDTO = dashBoardService.getLocationWiseDataDTO(activities,searchCriteria);
			Map<String, Map<String, Long>> dashBoardLocationParticipantMap = dashBoardLocationWiseDataDTO.getNoOfParticipants();
			csvWriter.writeHeader(csvheadersLocation);
			writeData(csvWriter, dashBoardLocationParticipantMap);

			break;

		case LOCATION_WISE_UNIQUE_PARTICIPATION:
			String[] csvheadersLocationUni = { "Location", "Tag Name","No of Unique Participants" };
			DashBoardLocationWiseDataDTO dashBoardLocationWiseData = dashBoardService.getLocationWiseDataDTO(activities,searchCriteria);
			Map<String, Long> dashBoardLocationUniqueParticipantsMap = dashBoardLocationWiseData.getUniqueParticipants();
			csvWriter.writeHeader(csvheadersLocationUni);

			writeDataOther(csvWriter, dashBoardLocationUniqueParticipantsMap);

			break;
		case LOCATION_WISE_PARTICIPATION_HOUR:
			String[] csvheadersLocationHours = { "Location","Tag Name", "Participant Hours" };
			DashBoardLocationWiseDataDTO dashBoardLocationHourWiseDataDTO = dashBoardService
					.getLocationWiseDataDTO(activities,searchCriteria);
					
			Map<String, Map<String, Long>> participationLocationHoursMap = dashBoardLocationHourWiseDataDTO.getParticipantHours();
			csvWriter.writeHeader(csvheadersLocationHours);

			writeData(csvWriter, participationLocationHoursMap);
			break;
			
		case DEPARTMENT_WISE_PARTICIPATION:

			String[] csvheadersDepartment = { "Department","Tag Name", "No of Participants" };
			DashBoardDepartmentWiseDataDTO dashBoardDepartmentWiseDataDTO = dashBoardService.getDepartmentWiseDataDTO(activities,searchCriteria);
			Map<String, Map<String, Long>> dashBoardDepartmentParticipantMap = dashBoardDepartmentWiseDataDTO.getNoOfParticipants();
			csvWriter.writeHeader(csvheadersDepartment);
			writeData(csvWriter, dashBoardDepartmentParticipantMap);

			break;

		case DEPARTMENT_WISE_UNIQUE_PARTICIPATION:
			String[] csvheadersDepartmentUni = { "Department", "Tag Name","No of Unique Participants" };
			DashBoardDepartmentWiseDataDTO dashBoardDepartmentWiseData = dashBoardService.getDepartmentWiseDataDTO(activities,searchCriteria);
			Map<String, Long> dashBoardDepartmentUniqueParticipantsMap = dashBoardDepartmentWiseData.getUniqueParticipants();
			csvWriter.writeHeader(csvheadersDepartmentUni);

			writeDataOther(csvWriter, dashBoardDepartmentUniqueParticipantsMap);

			break;
		case DEPARTMENT_WISE_PARTICIPATION_HOUR:
			String[] csvheadersDepartmentHours = { "Department","Tag Name", "Participant Hours" };
			DashBoardDepartmentWiseDataDTO dashBoardDepartmentHourWiseDataDTO = dashBoardService
					.getDepartmentWiseDataDTO(activities,searchCriteria);
					
			Map<String, Map<String, Long>> participationDepartmentHoursMap = dashBoardDepartmentHourWiseDataDTO.getParticipantHours();
			csvWriter.writeHeader(csvheadersDepartmentHours);

			writeData(csvWriter, participationDepartmentHoursMap);
			break;

		default:
			break;
		}
	}


	private void writeData(ICsvListWriter csvWriter, Map<String, Map<String, Long>> dashBoardParticipantMap) {
		try {
			for (Map.Entry<String, Map<String, Long>> dashBoardParticipantEntry : dashBoardParticipantMap.entrySet()) {
				dashBoardParticipantEntry.getValue().entrySet().forEach(entry -> {
					try {
						csvWriter.write(dashBoardParticipantEntry.getKey(),entry.getKey(), entry.getValue());
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				});

			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void writeDataOther(ICsvListWriter csvWriter, Map<String, Long> dashBoardParticipantMap) {
		try {
			for (Map.Entry<String, Long> dashBoardParticipantEntry : dashBoardParticipantMap.entrySet()) {
				csvWriter.write(dashBoardParticipantEntry.getKey(), dashBoardParticipantEntry.getValue());

			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}
	
	

}
