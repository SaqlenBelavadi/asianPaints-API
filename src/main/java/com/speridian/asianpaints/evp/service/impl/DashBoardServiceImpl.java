package com.speridian.asianpaints.evp.service.impl;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.DashBoardActivityFinancialDTO;
import com.speridian.asianpaints.evp.dto.DashBoardDepartmentWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardEmployeeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardHeaderResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardLocationWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardModeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardMonthWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardPastActivityDetailsDTO;
import com.speridian.asianpaints.evp.dto.DashBoardPastActivityDetailsResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardThemeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityFinancial;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.DashBoardService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFeedbackRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFinancialRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.util.CommonSpecification;
import com.speridian.asianpaints.evp.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DashBoardServiceImpl implements DashBoardService {

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private EmployeeActivityHistoryRepository eemploActivityHistoryRepository;

	@Autowired
	private EvpLovService evpLovService;
	
	@Autowired
	private ActivityFeedbackRepository activityFeedbackRepository;
	
	@Autowired
	private ActivityFinancialRepository activityFinancialRepositroy;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	

	@Override
	public DashBoardPastActivityDetailsResponseDTO getPastactivityDetails(String searchCriteria, Integer pageNo,
			Integer pageSize) throws EvpException {
		DashBoardPastActivityDetailsResponseDTO activityHisotry=DashBoardPastActivityDetailsResponseDTO.builder().dashBoardPastActivityDetailsDTOs(Collections.emptyList()).build();
		try {

			List<DashBoardPastActivityDetailsDTO> dashBoardPastActivityDetailsDTOs=new ArrayList<>();
			Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
			Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
			Map<String, Long> themeLovMap=evpLovService.getThemeLovMap();
			Map<String, Long> modeLovMap=evpLovService.getModeLovMap();

			Map<Long, String> lovMapWithIdKey = CommonUtils.getLovMapWithIdKey(locationLovMap);
			Map<Long, String> lovMapWithIdKey4 = CommonUtils.getLovMapWithIdKey(tagLovMap);

			SearchCriteria searCriteria = CommonUtils.buildSearchCriteria(searchCriteria);
			searCriteria = CommonUtils.buildParamsForSearchCriteria(searCriteria,locationLovMap,themeLovMap,modeLovMap,tagLovMap);

			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			
			
			List<Activity> pastActivities = getActivities(searchCriteria);
			Map<String, Activity> activityMap = pastActivities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Function.identity()));

			if (!activityMap.isEmpty()) {
				
				List<String> activityUUids= pastActivities.stream().map(Activity::getActivityId).collect(Collectors.toList());
				SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
				criteria.setActivityId(null);
				criteria.setActivityIds(null);
				criteria.setActivityNames(activityUUids);
				
				List<EmployeeActivityStatus> statuses=new ArrayList<>();

				statuses.add(EmployeeActivityStatus.PARTICIPATED);
				statuses.add(EmployeeActivityStatus.FEEDBACK);
				statuses.add(EmployeeActivityStatus.ENROLLED);
				
				criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
				criteria.setEmployeeIds(null);
				criteria.setEmployeeId(null);
				criteria.setStatuses(statuses);
			
//				Modified
				Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
				
				
				List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);

								
				employeeActivityHistories=employeeActivityHistories.stream()
					.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) 
							|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
					.collect(Collectors.toList());

				Map<String, List<EmployeeActivityHistory>> employeeActivityMap = employeeActivityHistories.stream()
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityName,
								Collectors.mapping(Function.identity(), Collectors.toList())));
				
				List<ActivityFeedback> activityFeedBacks= activityFeedbackRepository.findByActivityNames(activityMap.keySet());
				
				Map<String, List<ActivityFeedback>> activityFeedBackMap= activityFeedBacks.stream()
						.collect(Collectors.groupingBy(ActivityFeedback::getActivityName,
						Collectors.mapping(Function.identity(), Collectors.toList())));
				
				Map<String, List<Long>> activityLocationMap= activityService.getActivityLocationMap(pastActivities);
				
				filterLocationIds(searCriteria.getLocationId(), activityLocationMap);

				activityMap.entrySet().forEach(entry -> {
					Integer average=0;
					Integer totalParticiapants=0;
					Integer participantHours=0;
					String activityName = entry.getKey();
					Activity activity = entry.getValue();
					LocalDate createdDate = activity.getCreatedOn().toLocalDate();

					String activityId = activity.getActivityId();
					

					List<Long> locationIds = activityLocationMap.get(activity.getActivityId());

					String location = locationIds.stream().map(l -> lovMapWithIdKey.get(l)).collect(Collectors.joining(","));
					
					String tagName = lovMapWithIdKey4.get(activity.getTagId());
					List<EmployeeActivityHistory> employeeActivity = employeeActivityMap.get(activityName);
					List<ActivityFeedback> activityFeedback= activityFeedBackMap.get(activityName);
					
					if (Optional.ofNullable(activityFeedback).isPresent()) {
						Integer totalRating = activityFeedback.stream().map(ActivityFeedback::getRating)
								.filter(p -> Optional.ofNullable(p).isPresent()).collect(Collectors.summingInt(p -> p));
						if (activityFeedback.size() > 0) {
							average = totalRating / activityFeedback.size();
						}
					}
					
					
					if (Optional.ofNullable(employeeActivity).isPresent()) {
						totalParticiapants = employeeActivity.size();
						participantHours = employeeActivity.stream().map(EmployeeActivityHistory::getParticipationHours)
								.filter(p -> Optional.ofNullable(p).isPresent()).map(p -> {
									return Integer.parseInt(p.split(" ")[0]);
									
									})
								.collect(Collectors.summingInt(Integer::intValue));
					}
					
					DashBoardPastActivityDetailsDTO dashBoardPastActivityDetails = DashBoardPastActivityDetailsDTO
							.builder().activityId(activityId).activityName(activity.getActivityName()).activityLocation(location)
							.activityTag(tagName).createdDate(createdDate.toString()).totalHours(participantHours)
							.totalParticipants(totalParticiapants).rating(average)
							.activityUUID(activity.getActivityUUID())
							.build();
					dashBoardPastActivityDetailsDTOs.add(dashBoardPastActivityDetails);

				});
				

				List<List<DashBoardPastActivityDetailsDTO>> activityDTOS = CommonUtils.batchesOfList(dashBoardPastActivityDetailsDTOs,
						pageSize);

				 activityHisotry = CommonUtils.getPaginationDetailsForPastActivity(
						pageNo+1 , pageSize, dashBoardPastActivityDetailsDTOs.size());
				
				activityHisotry.setDashBoardPastActivityDetailsDTOs(activityDTOS.isEmpty() ? Collections.emptyList() : 
					activityDTOS.get(pageable.getPageNumber()));
				
				return activityHisotry;
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return activityHisotry;
	}

	@Override
	public DashBoardHeaderResponseDTO getDashBoardHeaderResponse(String searchCriteria) throws EvpException {
		DashBoardHeaderResponseDTO dashBoardHeaderResponse= DashBoardHeaderResponseDTO.builder().build();
		
		try {
			int noOfActivities=0;
			int noOfVolunteers=0;
			int noOfUniqueVolunters=0;
			int totalParticipationHours=0;
			double percentage=0.0;
			List<Activity> activities = getAllActivities(searchCriteria);
			
			
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
				
				 noOfActivities=activities.size();
				List<String> activityUUids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
				SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
				criteria.setActivityId(null);
				criteria.setActivityIds(null);
				criteria.setActivityNames(activityUUids);
				
				List<EmployeeActivityStatus> statuses=new ArrayList<>();

				statuses.add(EmployeeActivityStatus.PARTICIPATED);
				statuses.add(EmployeeActivityStatus.FEEDBACK);
				statuses.add(EmployeeActivityStatus.ENROLLED);
				
				criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
				criteria.setEmployeeIds(null);
				criteria.setEmployeeId(null);
				criteria.setStatuses(statuses);;
			
//				Modified
				Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecificationForDashBoardHeader(criteria);
				
				
				List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
				if(Optional.ofNullable(employeeActivityHistories).isPresent() && !employeeActivityHistories.isEmpty()) {
					
					List<EmployeeActivityHistory> participatedHistories=employeeActivityHistories.stream()
							.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
							|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
							.collect(Collectors.toList());
					
					 noOfVolunteers=participatedHistories.size();
					Set<String> employeeIds= participatedHistories.stream().map(EmployeeActivityHistory::getEmployeeId).collect(Collectors.toSet());
					 noOfUniqueVolunters=employeeIds.size();
					
					 totalParticipationHours = participatedHistories.stream()
							.map(EmployeeActivityHistory::getParticipationHours)
							.filter(p -> Optional.ofNullable(p).isPresent())
							.map(p->{
									return Integer.parseInt(p.split(" ")[0]);
									
							})
							.collect(Collectors.summingInt(p -> p));
					
					List<String> totalEnrolledEmployees = employeeActivityHistories.stream()
							.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
									|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.ENROLLED)
									|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
							.map(EmployeeActivityHistory::getEmployeeId).collect(Collectors.toList());
					int totalEnrolledSize = totalEnrolledEmployees
							.size();
							
						Long totalEnrolled=Long.valueOf(totalEnrolledSize);

					List<String> participatedEmployees = participatedHistories.stream()
								.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
										|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
								.map(EmployeeActivityHistory::getEmployeeId).collect(Collectors.toList());
					int totalParticipatedSize = participatedEmployees.size();
					
					Long totalParticipated=Long.valueOf(totalParticipatedSize);
					
					if (Optional.ofNullable(totalEnrolled).isPresent()
							&& Optional.ofNullable(totalParticipated).isPresent() && totalEnrolled > 0L
							&& totalParticipated > 0L) {

						 percentage = ( (double)totalParticipated / totalEnrolled ) * 100;
						 percentage=(int)percentage;
						
					}
						
				}
				
				dashBoardHeaderResponse.setEnrolledVsParticipated(percentage);
				dashBoardHeaderResponse.setTotalNoOfActivities(noOfActivities);
				dashBoardHeaderResponse.setNoOfVolunteers(noOfVolunteers);
				dashBoardHeaderResponse.setNoOfUniqueVolunteers(noOfUniqueVolunters);
				dashBoardHeaderResponse.setTotalHoursVolunteering(totalParticipationHours);
			}
			
			
			return dashBoardHeaderResponse;
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			return dashBoardHeaderResponse;
		}
		
	}

	private List<Activity> getActivities(String searchCriteria) throws EvpException {
		Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
		Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
		Map<String, Long> themeLovMap=evpLovService.getThemeLovMap();
		Map<String, Long> modeLovMap=evpLovService.getModeLovMap();
		
		
		SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
		criteria=CommonUtils.buildParamsForSearchCriteria(criteria, locationLovMap, themeLovMap, modeLovMap, tagLovMap);
		
		
		Specification<Activity> activitySpecs= CommonSpecification.allActivitySpecification(criteria);
		LocalDate currentDate=LocalDate.now();
		List<Activity> activities= activityRepository.findAll(activitySpecs);
		
		
		log.info("Retirivng Past activies");
		activities = activities.stream().filter(activity -> activity.isPublished())
				.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
				.collect(Collectors.toList());
		 
		
		activities=activityService.filterByLocation(criteria.getLocationId(), activities);
		return activities;
	}
	
	
	private List<Activity> getAllActivities(String searchCriteria) throws EvpException {
		Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
		Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
		Map<String, Long> themeLovMap=evpLovService.getThemeLovMap();
		Map<String, Long> modeLovMap=evpLovService.getModeLovMap();
		
		
		SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
		criteria=CommonUtils.buildParamsForSearchCriteria(criteria, locationLovMap, themeLovMap, modeLovMap, tagLovMap);
		
		
		Specification<Activity> activitySpecs= CommonSpecification.allActivitySpecification(criteria);
		List<Activity> activities= activityRepository.findAll(activitySpecs);
		
		activities=activityService.filterByLocation(criteria.getLocationId(), activities);
		return activities;
	}

	@Override
	public DashBoardActivityFinancialDTO getDashBoardFinancialDetails(String searchCriteria) throws EvpException {
		
		DashBoardActivityFinancialDTO dashBoardActivityFinancialDTO=DashBoardActivityFinancialDTO.builder().build();
		
		try {
			
			
			 List<Activity> activities = getAllActivities(searchCriteria);
			 
			 if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
				 
				 
				 Map<String,Activity> activityMap= activities.stream().collect(Collectors.toMap(Activity::getActivityId, Function.identity()));
				 
				 

					List<EmployeeActivityHistory> employeeActivityHistories = eemploActivityHistoryRepository
							.getByActivityName(activityMap.keySet());
					
					employeeActivityHistories=employeeActivityHistories.stream()
						.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) 
								|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
						.collect(Collectors.toList());

					Set<String> activityIds = employeeActivityHistories.stream()
							.map(EmployeeActivityHistory::getActivityName)
							.collect(Collectors.toSet());
				 
					activities=activityMap.entrySet().stream().filter(entry->activityIds.contains(entry.getKey())).map(entry->entry.getValue()).collect(Collectors.toList());
					
					dashBoardActivityFinancialDTO.setActivities(activities);
				 
				 List<Long> activityFinacialIds= activities.stream().map(Activity::getActivityFinancialId).collect(Collectors.toList());
				 
				 List<ActivityFinancial> activityFinancials= (List<ActivityFinancial>) activityFinancialRepositroy.findAllById(activityFinacialIds);
				
				 getActivityFinancialTotals(dashBoardActivityFinancialDTO, activityFinancials);
				 
				 
			 }
			 
			 
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardActivityFinancialDTO;
	}

	private void getActivityFinancialTotals(DashBoardActivityFinancialDTO dashBoardActivityFinancialDTO,
			List<ActivityFinancial> activityFinancials) {
		long estimatedCreativeExpences;
		long actualCreativeExpences;
		long estimatedGratificationExpenses;
		long actualGratificationExpenses;
		long estimatedOtherExpenses;
		long actualOtherExpenses;
		long actualLogisticExpense;
		long estimatedLogisticsExpense;
		long totalActual;
		long totalEstimated;
		actualCreativeExpences= activityFinancials.stream()
			.map(ActivityFinancial::getActualMaterialExpense)
			.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
			.map(p->{
					return Integer.parseInt(p.split(" ")[0]);
					
			})
			.collect(Collectors.summingInt(p -> p));
		 
		 estimatedCreativeExpences= activityFinancials.stream()
					.map(ActivityFinancial::getMaterialOrCreativeExpense)
					.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
					.map(p->{
							return Integer.parseInt(p.split(" ")[0]);
							
					})
					.collect(Collectors.summingInt(p -> p));
		 
		 
		 estimatedGratificationExpenses= activityFinancials.stream()
					.map(ActivityFinancial::getGratificationExpense)
					.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
					.map(p->{
							return Integer.parseInt(p.split(" ")[0]);
							
					})
					.collect(Collectors.summingInt(p -> p));
				 
				 actualGratificationExpenses= activityFinancials.stream()
							.map(ActivityFinancial::getActualGratificationExpense)
							.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
							.map(p->{
									return Integer.parseInt(p.split(" ")[0]);
									
							})
							.collect(Collectors.summingInt(p -> p));
				 
				 
				 actualOtherExpenses= activityFinancials.stream()
							.map(ActivityFinancial::getActualOtherExpense)
							.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
							.map(p->{
									return Integer.parseInt(p.split(" ")[0]);
									
							})
							.collect(Collectors.summingInt(p -> p));
						 
				 estimatedOtherExpenses= activityFinancials.stream()
									.map(ActivityFinancial::getOtherExpense)
									.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
									.map(p->{
											return Integer.parseInt(p.split(" ")[0]);
											
									})
									.collect(Collectors.summingInt(p -> p));
				 
				 actualLogisticExpense= activityFinancials.stream()
							.map(ActivityFinancial::getActualLogisticExpense)
							.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
							.map(p->{
									return Integer.parseInt(p.split(" ")[0]);
									
							})
							.collect(Collectors.summingInt(p -> p));
						 
				 estimatedLogisticsExpense= activityFinancials.stream()
									.map(ActivityFinancial::getLogisticExpense)
									.filter(p -> Optional.ofNullable(p).isPresent() && !p.isEmpty())
									.map(p->{
											return Integer.parseInt(p.split(" ")[0]);
											
									})
									.collect(Collectors.summingInt(p -> p));
				 
				 totalActual=actualOtherExpenses+actualGratificationExpenses+actualCreativeExpences+actualLogisticExpense;
				 totalEstimated=estimatedOtherExpenses+estimatedGratificationExpenses+estimatedCreativeExpences+estimatedLogisticsExpense;
				 
				 
				 dashBoardActivityFinancialDTO.setActualCreativeExpences(actualCreativeExpences);
				 dashBoardActivityFinancialDTO.setActualGratificationExpenses(actualGratificationExpenses);
				 dashBoardActivityFinancialDTO.setActualOtherExpenses(actualOtherExpenses);
				 dashBoardActivityFinancialDTO.setEstimatedCreativeExpences(estimatedCreativeExpences);
				 dashBoardActivityFinancialDTO.setEstimatedGratificationExpenses(estimatedGratificationExpenses);
				 dashBoardActivityFinancialDTO.setEstimatedOtherExpenses(estimatedOtherExpenses);
				 dashBoardActivityFinancialDTO.setActualLogisticsExpense(actualLogisticExpense);
				 dashBoardActivityFinancialDTO.setEstimateLogisticsExpense(estimatedLogisticsExpense);
				 dashBoardActivityFinancialDTO.setTotalActual(totalActual);
				 dashBoardActivityFinancialDTO.setTotalEstimated(totalEstimated);
	}

	@Override
	public DashBoardThemeWiseDataDTO getDashBoardThemeWiseData(List<Activity> activities,String searchCriteria) throws EvpException {
		DashBoardThemeWiseDataDTO dashBoardThemeWiseDataDTO=DashBoardThemeWiseDataDTO.builder().build();
		
		try {
			Map<String, Long> participantHoursMap=new HashMap<>();
			
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			Map<String, Long> noOfParticipants = getThemeWiseParticipationMap(employeeActivityHistories);
			dashBoardThemeWiseDataDTO.setNoOfParticipants(noOfParticipants);
			
			Map<Object, Object> unmiqueParticipants = getUniqueParticipationMap(employeeActivityHistories);
			dashBoardThemeWiseDataDTO.setUniqueParticipants(unmiqueParticipants);
			
			getParticipationHourMap(participantHoursMap, employeeActivityHistories);
			
			dashBoardThemeWiseDataDTO.setParticipantHours(participantHoursMap);
			
			Set<String> themeNames= evpLovService.getThemeLovMap().keySet();
			
			themeNames.forEach(themeName->{
				if(Optional.ofNullable(noOfParticipants).isPresent() && !Optional.ofNullable(noOfParticipants.get(themeName)).isPresent() ) {
					noOfParticipants.put(themeName, 0L);
				}
				
				if(Optional.ofNullable(unmiqueParticipants).isPresent() && !Optional.ofNullable(unmiqueParticipants.get(themeName)).isPresent()) {
					unmiqueParticipants.put(themeName, 0L);
				}
				
				if(Optional.ofNullable(participantHoursMap).isPresent() && !Optional.ofNullable(participantHoursMap.get(themeName)).isPresent()) {
					participantHoursMap.put(themeName, 0L);
				}
			});
			}
			
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardThemeWiseDataDTO;
	
	}

	private void getParticipationHourMap(Map<String, Long> participantHoursMap,
			List<EmployeeActivityHistory> employeeActivityHistories) {
		Map<String, List<String>> participantHoursGroupedMap= employeeActivityHistories.stream()
				.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
				.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTheme,Collectors.mapping(EmployeeActivityHistory::getParticipationHours, Collectors.toList())));
		
		if(participantHoursGroupedMap!=null) {
			participantHoursGroupedMap.entrySet().stream().forEach(entry->{
				;
				Integer participantHours=entry.getValue().stream().map(p->Integer.parseInt(p.split(" ")[0])).collect(Collectors.summingInt(p -> p));
				participantHoursMap.put(entry.getKey(), Long.valueOf(participantHours));
			
			});
		}
		
	}

	private Map<Object, Object> getUniqueParticipationMap(List<EmployeeActivityHistory> employeeActivityHistories) {
		Map<String, Set<String>> noOfUniqueParticipants= employeeActivityHistories.stream()
				.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
				.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTheme, Collectors.mapping(EmployeeActivityHistory::getEmployeeId, Collectors.toSet())));
		
		Map<Object, Object> unmiqueParticipants=noOfUniqueParticipants.entrySet().stream().collect(Collectors.toMap(entry->entry.getKey(),entry->entry.getValue().size()));
		return unmiqueParticipants;
	}

	private Map<String, Long> getThemeWiseParticipationMap(List<EmployeeActivityHistory> employeeActivityHistories) {
		Map<String, Long> noOfParticipants= employeeActivityHistories.stream()
				.filter(e->e.getActivityTheme()!=null)
				.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
		.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTheme, Collectors.mapping(Function.identity(), Collectors.counting())));
		return noOfParticipants;
	}

	@Override
	public DashBoardModeWiseDataDTO getDashBoardModeWiseData(List<Activity> activities,String searchCriteria) throws EvpException {

		DashBoardModeWiseDataDTO dashBoardModeWiseDataDTO=DashBoardModeWiseDataDTO.builder().build();
		
		try {
			Map<String, Long> participantHoursMap=new HashMap<>();
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
			
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			
			Map<String, Long> noOfParticipants= employeeActivityHistories.stream()
					.filter(e->e.getMode()!=null)
					.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
			.collect(Collectors.groupingBy(EmployeeActivityHistory::getMode, Collectors.mapping(Function.identity(), Collectors.counting())));
			dashBoardModeWiseDataDTO.setNoOfParticipants(noOfParticipants);
			Map<String, Set<String>> noOfUniqueParticipants= employeeActivityHistories.stream()
					.filter(e->e.getMode()!=null)
					.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getMode, Collectors.mapping(EmployeeActivityHistory::getEmployeeId, Collectors.toSet())));
			
			Map<Object, Object> unmiqueParticipants=noOfUniqueParticipants.entrySet().stream().collect(Collectors.toMap(entry->entry.getKey(),entry->entry.getValue().size()));
			dashBoardModeWiseDataDTO.setUniqueParticipants(unmiqueParticipants);
			
			Map<String, List<String>> participantHoursGroupedMap= employeeActivityHistories.stream()
					.filter(e->e.getMode()!=null)
					.filter(e->e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) || e.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getMode,Collectors.mapping(EmployeeActivityHistory::getParticipationHours, Collectors.toList())));
		
			
			if(Optional.ofNullable(participantHoursGroupedMap).isPresent() ) {
			participantHoursGroupedMap.entrySet().stream().forEach(entry->{
				;
				Integer participantHours=entry.getValue().stream().map(p->Integer.parseInt(p.split(" ")[0])).collect(Collectors.summingInt(p -> p));
				participantHoursMap.put(entry.getKey(), Long.valueOf(participantHours));
			
			});
			
			dashBoardModeWiseDataDTO.setParticipantHours(participantHoursMap);
			}
			
			Set<String> modes= evpLovService.getModeLovMap().keySet();
			
			modes.forEach(mode->{
				if( Optional.ofNullable(noOfParticipants).isPresent() && !Optional.ofNullable(noOfParticipants.get(mode)).isPresent() ) {
					noOfParticipants.put(mode, 0L);
				}
				
				if(Optional.ofNullable(unmiqueParticipants).isPresent() && !Optional.ofNullable(unmiqueParticipants.get(mode)).isPresent() ) {
					unmiqueParticipants.put(mode, 0L);
				}
				
				if(Optional.ofNullable(participantHoursMap).isPresent() && !Optional.ofNullable(participantHoursMap.get(mode)).isPresent()) {
					participantHoursMap.put(mode, 0L);
				}
			});
			}
			
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardModeWiseDataDTO;
	
	
	}
	
	@Override
	public DashBoardMonthWiseDataDTO getDashBoardMonthWiseData(List<Activity> activities,String searchCriteria) throws EvpException {


		DashBoardMonthWiseDataDTO dashBoardMonthWiseDataDTO=DashBoardMonthWiseDataDTO.builder().build();
		
		try {
			
			Map<Month, Map<String, Long>> noOfParticipationMap=new EnumMap<>(Month.class);
			
			Map<Month, Map<String, Long>> hoursParticipationMap=new EnumMap<>(Month.class);
			
			Map<Month, Map<String, Long>> uniqueParticipationMap=new EnumMap<>(Month.class);
			
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			
			Map<Month, List<EmployeeActivityHistory>> noOfParticipants= employeeActivityHistories.stream()
					.filter(employeeActivity->employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) 
							|| employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
			.collect(Collectors.groupingBy(employeeActivity->Optional.ofNullable(employeeActivity.getEndDate()).isPresent()
					?employeeActivity.getEndDate().getMonth():LocalDate.now().getMonth(), 
							Collectors.mapping(Function.identity(), Collectors.toList())));
			
			
			noOfParticipants.entrySet().forEach(entry -> {
				Map<String, Long> groupedByActivityMap = entry.getValue().stream()
						.filter(e -> e.getActivityTag() != null)
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
								Collectors.mapping(Function.identity(), Collectors.counting())));
				noOfParticipationMap.put(entry.getKey(), groupedByActivityMap);

			});
			
			
			dashBoardMonthWiseDataDTO.setNoOfParticipants(noOfParticipationMap);
			
			
			Map<Month, List<String>> monthWiseActivityMap= employeeActivityHistories.stream()
			.filter(employeeActivity->employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED) 
					|| employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
			.filter(e -> e.getActivityTag() != null)
			.collect(Collectors.groupingBy(employeeActivity->Optional.ofNullable(employeeActivity.getEndDate()).isPresent()
					?employeeActivity.getEndDate().getMonth():LocalDate.now().getMonth(), 
							Collectors.mapping(EmployeeActivityHistory::getActivityTag, Collectors.toList())));
			
			Map<String, Set<String>> activityWiseMap= employeeActivityHistories.stream()
				.filter(employeeActivity->employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
						|| employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
				.filter(e -> e.getActivityTag() != null)
				.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag, 
								Collectors.mapping(EmployeeActivityHistory::getEmployeeId, Collectors.toSet())));
			
			monthWiseActivityMap.entrySet().forEach(entry->{
				entry.getValue().forEach(e->{
					Set<String> employeeIds= activityWiseMap.get(e);
					Long size=Long.valueOf(employeeIds.size());
					Map<String, Long> innerMap=new HashMap<>();
					
					innerMap.put(e, size);

					if(uniqueParticipationMap.get(entry.getKey())!=null){
						uniqueParticipationMap.get(entry.getKey()).putAll(innerMap);
					}else {
						uniqueParticipationMap.put(entry.getKey(),innerMap);
					}
					
					
				});
				
			});
			
			
			
			dashBoardMonthWiseDataDTO.setUniqueParticipants(uniqueParticipationMap);
			
			Map<String, Long> activityWiseHourMap= employeeActivityHistories.stream().filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
					.filter(e -> e.getActivityTag() != null)
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
					Collectors.summingLong(e->Optional.ofNullable(e.getParticipationHours()).isPresent()?
							Long.valueOf(e.getParticipationHours().split(" ")[0]):0L)));
			
			monthWiseActivityMap.entrySet().forEach(entry->{
				entry.getValue().forEach(e->{
					Long hours=activityWiseHourMap.get(e);
					Map<String, Long> innerMap=new HashMap<>();
					innerMap.put(e, hours);
					
					if(hoursParticipationMap.get(entry.getKey())!=null){
						hoursParticipationMap.get(entry.getKey()).putAll(innerMap);
					}else {
						hoursParticipationMap.put(entry.getKey(),innerMap);
					}
				});
			});
			
			dashBoardMonthWiseDataDTO.setParticipantHours(hoursParticipationMap);
			
			Arrays.asList(Month.values()).forEach(month->{
				if(Optional.ofNullable(hoursParticipationMap).isPresent() && !Optional.ofNullable(hoursParticipationMap.get(month)).isPresent()) {
					hoursParticipationMap.put(month, new HashMap<>());
				}
				
				if(Optional.ofNullable(uniqueParticipationMap).isPresent() && !Optional.ofNullable(uniqueParticipationMap.get(month)).isPresent()) {
					uniqueParticipationMap.put(month, new HashMap<>());
				}
				
				if(Optional.ofNullable(noOfParticipationMap).isPresent() && !Optional.ofNullable(noOfParticipationMap.get(month)).isPresent()) {
					noOfParticipationMap.put(month, new HashMap<>());
				}
			});
			}
			
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardMonthWiseDataDTO;
	
	
	
	}
	
	@Override
	public DashBoardEmployeeWiseDataDTO getEmployeeWiseDashBoardData(List<Activity> activities,String searchCriteria) throws EvpException {


		DashBoardEmployeeWiseDataDTO dashBoardEmployeeWiseDataDTO=DashBoardEmployeeWiseDataDTO.builder().build();
		
		try {
			Map<String, Map<String, Long>> noOfActivitiesHoursMap=new HashMap<>();
			
			Map<String, Map<String, Long>> noOfActivitiesMap=new HashMap<>();
			
			Map<String, Long> participantHoursMap=new HashMap<>();
			
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			if(Optional.ofNullable(employeeActivityHistories).isPresent()) {
				
				Map<String, List<EmployeeActivityHistory>> employeeActivityHistoryMap= employeeActivityHistories.stream()
						.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
								|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getEmployeeId, Collectors.mapping(Function.identity(), Collectors.toList())));
				
				Map<String, List<EmployeeActivityHistory>> employeeActivityHistoryMapTop10 = employeeActivityHistoryMap
						.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().size())).limit(10)
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
				
				employeeActivityHistoryMapTop10.entrySet().stream().forEach(entry->{
					String employeeName=null;
					int noOfActivities=entry.getValue().size();
					
					Optional<EmployeeActivityHistory> employeeNameOpt= entry.getValue().stream().findFirst();
					
					if(employeeNameOpt.isPresent()) {
						employeeName=employeeNameOpt.get().getEmployeeName();
					}
					Map<String, Long> innerMap=new HashMap<>();
					innerMap.put(employeeName, Long.valueOf(noOfActivities));
					noOfActivitiesMap.put(entry.getKey(), innerMap);
				});
				
				Map<String, String> employeeIdNameMap=new HashMap<>();
				employeeActivityHistoryMap.entrySet().forEach(participant->{
					String employeeName=null;
					String employeeId=participant.getKey();
					Optional<EmployeeActivityHistory> emploOptional= participant.getValue().stream().findFirst();
					if(emploOptional.isPresent()) {
						employeeName=emploOptional.get().getEmployeeName();
					}
					employeeIdNameMap.put(employeeId, employeeName);
				});
				
				employeeActivityHistoryMap.entrySet().stream().forEach(entry->{
					Long totalHours=entry.getValue().stream().filter(p->p.getParticipationHours()!=null).map(p->p.getParticipationHours().split(" ")[0]).map(Long::parseLong).collect(Collectors.summingLong(Long::intValue));
					
					participantHoursMap.put(entry.getKey(), totalHours);
				});
				
				Map<String, Long> participantHoursMapTop10 = participantHoursMap
						.entrySet().stream().sorted(Comparator.comparingLong(entry -> entry.getValue())).limit(10)
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
				
				
				participantHoursMapTop10.entrySet().forEach(hours->{
					String employeeName=employeeIdNameMap.get(hours.getKey());
					Long hoursParti=hours.getValue();
					String employeeId =hours.getKey();
					
					Map<String, Long> innerMap=new HashMap<>();
					innerMap.put(employeeName, hoursParti);
					noOfActivitiesHoursMap.put(employeeId, innerMap);
				});
				
				
			}
				
			}
			
		
			dashBoardEmployeeWiseDataDTO.setNoOfActivites(noOfActivitiesMap);
			
			dashBoardEmployeeWiseDataDTO.setNoOfHours(noOfActivitiesHoursMap);
		}
			catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardEmployeeWiseDataDTO;
	
	}
	
	@Override
	public DashBoardLocationWiseDataDTO getLocationWiseDataDTO(List<Activity> activities,String searchCriteria) throws EvpException{
		DashBoardLocationWiseDataDTO dashBoardLocationWiseDataDTO=DashBoardLocationWiseDataDTO.builder().build();
		
		try {
			
			Map<String, Map<String, Long>> noOfParticipationMap=new HashMap<>();
			Map<String, Map<String, Long>> hoursParticipationMap=new HashMap<>();
			Map<String, Map<Long, Long>> totalVsParticipatedMap=new HashMap<>(); 
			dashBoardLocationWiseDataDTO.setTotalVsParticipatedMap(totalVsParticipatedMap);
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			
			
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			
			Map<String, List<EmployeeActivityHistory>> noOfParticipantsLocation= employeeActivityHistories.stream()
					.filter(employeeActivity->employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
							|| employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
			.collect(Collectors.groupingBy(employeeActivity->employeeActivity.getActivityLocation(), 
							Collectors.mapping(Function.identity(), Collectors.toList())));
			
			
			noOfParticipantsLocation.entrySet().forEach(entry -> {
				Map<String, Long> groupedByActivityMap = entry.getValue().stream()
						.filter(e -> e.getActivityTag() != null)
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
								Collectors.mapping(Function.identity(), Collectors.counting())));
				
				if(	noOfParticipationMap.get(entry.getKey())!=null) {
					noOfParticipationMap.get(entry.getKey()).putAll(groupedByActivityMap);
				}else {
					noOfParticipationMap.put(entry.getKey(),groupedByActivityMap);
				}

			});
			
			
			dashBoardLocationWiseDataDTO.setNoOfParticipants(noOfParticipationMap);
			
		
			
			noOfParticipantsLocation.entrySet().forEach(entry -> {
				Map<String, Long> groupedByActivityMap = entry.getValue().stream()
						.filter(e -> e.getActivityTag() != null)
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
								Collectors.summingLong(e->Optional.ofNullable(e.getParticipationHours()).isPresent()?
										Long.valueOf(e.getParticipationHours().split(" ")[0]):0L)));
				
				if(	hoursParticipationMap.get(entry.getKey())!=null) {
					hoursParticipationMap.get(entry.getKey()).putAll(groupedByActivityMap);
				}else {
					hoursParticipationMap.put(entry.getKey(),groupedByActivityMap);
				}

			});
			
			
			
			dashBoardLocationWiseDataDTO.setParticipantHours(hoursParticipationMap);
			
			Set<String> divisionNames= evpLovService.getLocationLovMap().keySet();
			
			List<String> divisionNamesUpper= divisionNames.stream()
					.map(divisionName->divisionName.toLowerCase())
					.collect(Collectors.toList());
			List<Employee> employeeList= employeeRepository.findByDivisionName(divisionNamesUpper);
			
			Map<String, Long> departwiseEmployeeMap= employeeList.stream().collect(Collectors.groupingBy(employee->employee.getDivisionName().toLowerCase(), Collectors.counting()));
			Map<String, Integer> locationSizeMap=new HashMap<>();
			 
		 noOfParticipantsLocation.entrySet().stream().forEach(entry->{
			 Integer size=entry.getValue().stream().map(EmployeeActivityHistory::getEmployeeId).collect(Collectors.toSet()).size();
			 locationSizeMap.put(entry.getKey(), size);
		 });
		 
		 Map<String, Long> uniqueParticipantMap=new HashMap<>();
		  locationSizeMap.entrySet().forEach(entry->{
			  uniqueParticipantMap.put(entry.getKey(), Long.valueOf(entry.getValue()));
		 });
		 
		 dashBoardLocationWiseDataDTO.setUniqueParticipants(uniqueParticipantMap);
		 locationSizeMap.entrySet().forEach(entry->{
				Long totalParticipated=Long.valueOf(entry.getValue());
				Long totalEmployees=departwiseEmployeeMap.get(entry.getKey().toLowerCase());
				Map<Long, Long> innerMap=new HashMap<>();
				innerMap.put(totalParticipated, totalEmployees);
				totalVsParticipatedMap.put(entry.getKey(), innerMap);
			});
			
			
			evpLovService.getLocationLovMap().keySet().forEach(month->{
				if(Optional.ofNullable(hoursParticipationMap).isPresent() && !Optional.ofNullable(hoursParticipationMap.get(month)).isPresent()) {
					hoursParticipationMap.put(month, new HashMap<>());
				}
				
				if(Optional.ofNullable(uniqueParticipantMap).isPresent() && !Optional.ofNullable(uniqueParticipantMap.get(month)).isPresent()) {
					uniqueParticipantMap.put(month, 0L);
				}
				
				if(Optional.ofNullable(noOfParticipationMap).isPresent() && !Optional.ofNullable(noOfParticipationMap.get(month)).isPresent()) {
					noOfParticipationMap.put(month, new HashMap<>());
				}
				if(Optional.ofNullable(totalVsParticipatedMap).isPresent() && !Optional.ofNullable(totalVsParticipatedMap.get(month)).isPresent()) {
					totalVsParticipatedMap.put(month, new HashMap<>());
				}
			});
			}
			
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardLocationWiseDataDTO;
	}
	
	@Override
	public DashBoardDepartmentWiseDataDTO getDepartmentWiseDataDTO(List<Activity> activities,String searchCriteria) throws EvpException{
		DashBoardDepartmentWiseDataDTO dashBoardDepartmentWiseDataDTO=DashBoardDepartmentWiseDataDTO.builder().build();
		
		try {
			
			Map<String, Map<String, Long>> noOfParticipationMap=new HashMap<>();
			Map<String, Map<String, Long>> uniquearticipationMap=new HashMap<>();
			Map<String, Map<String, Long>> hoursParticipationMap=new HashMap<>();
			Map<String, Map<Long, Long>> totalVsParticipatedMap=new HashMap<>(); 
			Map<String, Integer> uniqueParticipatedMap=new HashMap<>();
			dashBoardDepartmentWiseDataDTO.setNoOfParticipants(noOfParticipationMap);
			
			dashBoardDepartmentWiseDataDTO.setParticipantHours(hoursParticipationMap);
			dashBoardDepartmentWiseDataDTO.setTotalVsUnique(totalVsParticipatedMap);
			if(Optional.ofNullable(activities).isPresent() && !activities.isEmpty()) {
			
			List<String> activityUuids= activities.stream().map(Activity::getActivityId).collect(Collectors.toList());
			
			
			SearchCriteria criteria= CommonUtils.buildSearchCriteria(searchCriteria);
			criteria.setActivityId(null);
			criteria.setActivityIds(null);
			criteria.setActivityNames(activityUuids);
			
			List<EmployeeActivityStatus> statuses=new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);
			statuses.add(EmployeeActivityStatus.ENROLLED);
			
			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			criteria.setEmployeeIds(null);
			criteria.setEmployeeId(null);
			criteria.setStatuses(statuses);;
		
//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification= CommonSpecification.allActivityParticipationSpecification(criteria,true,true);
			
			
			List<EmployeeActivityHistory> employeeActivityHistories= eemploActivityHistoryRepository.findAll(employeeSpecification);
			
			if(Optional.ofNullable(employeeActivityHistories).isPresent()) {
			
				Map<String, List<EmployeeActivityHistory>> employeeActivityHistoryMap= employeeActivityHistories.stream()
						.filter(employeeActivity->employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
								|| employeeActivity.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK)
								)
						.filter(employeeActivity->employeeActivity.getDepartmentName()!=null)
						.collect(Collectors.groupingBy(EmployeeActivityHistory::getDepartmentName, Collectors.mapping(Function.identity(), Collectors.toList())));
			
				
				Map<String, List<EmployeeActivityHistory>> employeeActivityHistoryMapTop10 = employeeActivityHistoryMap
						.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().size())).limit(10)
						.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
				
				employeeActivityHistoryMapTop10.entrySet().stream().forEach(entry -> {
					;
					Map<String, Long> actiivtyMap = entry.getValue().stream()
							.filter(e -> e.getActivityTag() != null)
							.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
									Collectors.mapping(EmployeeActivityHistory::getEmployeeId, Collectors.counting())));
					noOfParticipationMap.put(entry.getKey(), actiivtyMap);
					

					if(	noOfParticipationMap.get(entry.getKey())!=null) {
						noOfParticipationMap.get(entry.getKey()).putAll(actiivtyMap);
					}else {
						noOfParticipationMap.put(entry.getKey(),actiivtyMap);
					}
				});
				
				employeeActivityHistoryMapTop10.entrySet().stream().forEach(entry -> {
					;
					Map<String, Set<String>> actiivtyMap = entry.getValue().stream()
							.filter(e -> e.getActivityTag() != null)
							.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
									Collectors.mapping(EmployeeActivityHistory::getEmployeeId, Collectors.toSet())));

					Map<String, Long> innerMap = actiivtyMap.entrySet().stream()
							.collect(Collectors.toMap(e -> e.getKey(), e -> Long.valueOf(e.getValue().size())));
					
					if(uniquearticipationMap.get(entry.getKey())!=null) {
						uniquearticipationMap.get(entry.getKey()).putAll(innerMap);
					}else {
						uniquearticipationMap.put(entry.getKey(),innerMap);
					}

				});
				
				
				
				employeeActivityHistoryMapTop10.entrySet().stream().forEach(entry -> {
					;
					Map<String, Long> actiivtyMap = entry.getValue().stream()
							.filter(e -> e.getActivityTag() != null)
							.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityTag,
									Collectors.summingLong(e->Optional.ofNullable(e.getParticipationHours()).isPresent()?
											Long.valueOf(e.getParticipationHours().split(" ")[0]):0L)));
					

					if(	hoursParticipationMap.get(entry.getKey())!=null) {
						hoursParticipationMap.get(entry.getKey()).putAll(actiivtyMap);
					}else {
						hoursParticipationMap.put(entry.getKey(),actiivtyMap);
					}
				});
				
				
				
				List<Employee> employeeList= employeeRepository.findByDepartmentName(new ArrayList<>( uniquearticipationMap.keySet()));
				
				Map<String, Long> departwiseEmployeeMap= employeeList.stream().collect(Collectors.groupingBy(Employee::getDepartmentName, Collectors.counting()));
				
				
				employeeActivityHistoryMapTop10.entrySet().stream().forEach(entry->{
					
					
					String departMentName=entry.getKey();
					Set<String> employeeIds= entry.getValue().stream().map(EmployeeActivityHistory::getEmployeeId).collect(Collectors.toSet());
					uniqueParticipatedMap.put(departMentName, employeeIds.size());
				});
				
				 Map<String, Long> uniqueParticipantMap=new HashMap<>();
				 uniqueParticipatedMap.entrySet().forEach(entry->{
					  uniqueParticipantMap.put(entry.getKey(), Long.valueOf(entry.getValue()));
				 });
				 
				 dashBoardDepartmentWiseDataDTO.setUniqueParticipants(uniqueParticipantMap);
				
				uniqueParticipatedMap.entrySet().forEach(entry->{
					Long totalEmployees=departwiseEmployeeMap.get(entry.getKey());
					Map<Long, Long> innerMap=new HashMap<>();
					innerMap.put(totalEmployees, Long.valueOf(entry.getValue()));
					totalVsParticipatedMap.put(entry.getKey(), innerMap);
				});
				
				
			}
			
			
			}
			
		}catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return dashBoardDepartmentWiseDataDTO;
	}
	
	private void filterLocationIds(List<Long> activityLocationIds, Map<String, List<Long>> activityLocationMap) {
		if (activityLocationIds.size() == 1) {
			Long locationId = activityLocationIds.get(0);
			activityLocationMap.entrySet().forEach(entry -> {
				Optional<Long> l1 = entry.getValue().stream().filter(l -> l.equals(locationId)).findFirst();
				if (l1.isPresent()) {
					activityLocationMap.put(entry.getKey(), Arrays.asList(locationId));
				}

			});
		}
	}

}
