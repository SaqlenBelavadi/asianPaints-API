package com.speridian.asianpaints.evp.service.impl;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.speridian.asianpaints.evp.constants.Constants;
import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackDTO;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionResponseDTO;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryDTO;
import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.ImageDTO;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityFeedbackLocation;
import com.speridian.asianpaints.evp.entity.ActivityLocation;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.ActivityPromotion;
import com.speridian.asianpaints.evp.entity.ActivityPromotionLocation;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.ActivityFeedbackService;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.EmailService;
import com.speridian.asianpaints.evp.service.EmployeeActivityHistoryService;
import com.speridian.asianpaints.evp.service.EmployeeService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.service.UploadService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFeedbackLocationRepostiroy;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFeedbackRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityLocationRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPictureRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPromotionLocationRepostiroy;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPromotionRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.util.CommonSpecification;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.ImageType;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActivityFeedbackServiceImpl implements ActivityFeedbackService {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private ActivityFeedbackRepository activityFeedbackRepository;
	
	@Autowired
	private ActivityFeedbackLocationRepostiroy activityFeedBackLocationRepository;

	@Autowired
	private EvpLovService evpLovService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Autowired
	private ActivityPromotionRepository activityPromotionRepository;
	
	@Autowired
	private ActivityPromotionLocationRepostiroy activityPromotionLocationRepostiroy;

	@Autowired
	private ActivityPictureRepository activityPictureRepository;

	@Autowired
	private EmployeeActivityHistoryRepository employeeActivityHistoryRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private UploadService uploadService;
	
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ActivityService activityService;
	
	@Autowired
	private ActivityLocationRepository activityLocationRepository;

//	Change of Activity Name to Activity Id
	@Override
	public void addActivityFeedBack(ActivityFeedbackDTO feedbackDTO) throws EvpException {

		log.info("addActivityFeedBack");
		try {

			
			String imageName=null;
			Optional<Activity> activity = activityRepository.findByActivityId(feedbackDTO.getActivityId());
			if (!activity.isPresent()) {
				log.error("Invalid Activity");
				throw new EvpException("Invalid Activity");
			}

			Long themeId = evpLovService.getThemeLovMap().get(feedbackDTO.getThemeName());

			if (!Optional.ofNullable(themeId).isPresent()) {
				log.error("Theme Doesn't exist");
				throw new EvpException("Theme Doesn't exist");
			}

			Long modeId = evpLovService.getModeLovMap().get(feedbackDTO.getMode());

			if (!Optional.ofNullable(modeId).isPresent()) {
				log.error("Mode Doesn't exist");
				throw new EvpException("Mode Doesn't exist");
			}

			Optional<Long> tagIdOpt = evpLovService.getTagLovMap().entrySet().stream()
					.filter(t -> t.getKey().equals(feedbackDTO.getTagName())).map(t -> t.getValue()).findFirst();
			if (!tagIdOpt.isPresent()) {
				log.error("Tag Doesn't exist");
				throw new EvpException("Tag Doesn't exist");
			}

			log.info("startDate : ".concat(feedbackDTO.getStartDate()).concat(" ,endDate :  ")
					.concat(feedbackDTO.getEndDate()));
			LocalDateTime startDate = CommonUtils.getActivityDate(feedbackDTO.getStartDate());
			LocalDateTime endDate = CommonUtils.getActivityDate(feedbackDTO.getEndDate());
			String locationArray[] = feedbackDTO.getLocation().split(",");
			
			
				ActivityFeedback afb = CommonUtils.convertactivityFeedbackDtoToActivityFeedback(feedbackDTO, startDate,
						endDate);
				if (Optional.ofNullable(feedbackDTO.getFeedbackId()).isPresent()) {
					log.info("ActivityFeedback id : ".concat(feedbackDTO.getFeedbackId()));
					afb.setId(Long.valueOf(feedbackDTO.getFeedbackId()));
					
					List<ActivityFeedbackLocation> activityFeedBackLocations= activityFeedBackLocationRepository.findByFeedbackId(Long.valueOf(feedbackDTO.getFeedbackId()));
					activityFeedBackLocationRepository.deleteAll(activityFeedBackLocations);
				}
				
				
				

			ActivityFeedback activityFeedBack=activityFeedbackRepository.save(afb);
			log.info("Activity feedback added successfully");
			List<ActivityFeedbackLocation> activityFeedBackLocations = new ArrayList<>();
			Stream.of(locationArray).forEach(id->{
				ActivityFeedbackLocation activityFeedbackLocation=new ActivityFeedbackLocation();
				activityFeedbackLocation.setActivityId(activityFeedBack.getActivityName());
				activityFeedbackLocation.setFeedbackId(activityFeedBack.getId());
				activityFeedbackLocation.setLocation(id);
				activityFeedBackLocations.add(activityFeedbackLocation);
			});
			
			activityFeedBackLocationRepository.saveAll(activityFeedBackLocations);
			
			String activityId=feedbackDTO.getActivityId();
			
			List<ActivityPicture> pictureOptional= activityPictureRepository.findByImageNamesAndActivityNameAndImageType(feedbackDTO.getImageNames(), activityId,ImageType.EMPLOYEE_UPLOAD);
			
			if(!pictureOptional.isEmpty()) {
				pictureOptional.forEach(activityPicture->{
					activityPicture.setFeedBackId(activityFeedBack.getId());
				});
				
					
				
			}
			
			activityPictureRepository.saveAll(pictureOptional);
			

			String role=CommonUtils.getAssignedRole(request, jwtUtil);
			if(role.equals("ROLE_EMPLOYEE")) {
				log.info("get employe history for EmployeeId = ".concat(feedbackDTO.getEmployeeId())
						.concat(" , activityUUID = ").concat(activity.get().getActivityUUID()));
				EmployeeActivityHistory e = employeeActivityHistoryRepository
						.findByEmployeeIdAndActivityUUID(feedbackDTO.getEmployeeId(), activity.get().getActivityUUID());
				if (Optional.ofNullable(e).isPresent()) {
					e.setApprovedByAdmin(true);
					e.setEmployeeActivityStatus(EmployeeActivityStatus.FEEDBACK);
					employeeActivityHistoryRepository.save(e);
					log.info("employee ActivityHistory added successfully");
				}
				sendEmployeeParticipateEmail(activity.get(), e, EmployeeActivityStatus.PARTICIPATED);
			}
			
			

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public ActivityFeedbackResponseDTO getActivityFeedBack(SearchCriteria criteria, Integer pageNo, Integer pageSize) {
		log.info("getActivityFeedBack");

		if (!Optional.ofNullable(criteria).isPresent()) {
			criteria = new SearchCriteria();
		}

		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		criteria.setRole(role);

		String username = CommonUtils.getUsername(request, jwtUtil);
		criteria.setUsername(username);

		Specification<ActivityFeedback> specification = CommonSpecification.getActivityFeedbackSpecification(criteria);
		Sort sort = Sort.by(Direction.DESC, "createdOn");
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

		Page<ActivityFeedback> activityFeedbacks = activityFeedbackRepository.findAll(specification, pageable);

		List<ActivityFeedback> activityFeedBacks=new ArrayList<>();
		
		if(role.equals("ROLE_EMPLOYEE") || role.equals("ROLE_ADMIN")) {
			
			List<ActivityFeedbackLocation> activityFeedbackLocations= activityFeedBackLocationRepository.findByMultiLocation(criteria.getLocations());
			
			List<Long> locationFeedBackIds=activityFeedbackLocations.stream().map(ActivityFeedbackLocation::getFeedbackId).collect(Collectors.toList());
			
			activityFeedBacks= activityFeedbacks.getContent().stream().filter(feedback->locationFeedBackIds.contains(feedback.getId())).collect(Collectors.toList());
			
		}else {
			activityFeedBacks=activityFeedbacks.getContent();
		}
		
		ActivityFeedbackResponseDTO activityFeedbackResponseDTO = ActivityFeedbackResponseDTO.builder().build();

		Map<String, String> activityFeedBackMap = getActivityMapByFeedback(activityFeedbacks.getContent());

		activityFeedbackResponseDTO= buildActivityFeedbackRespons(pageSize, activityFeedbackResponseDTO, activityFeedbacks, true,
				activityFeedBackMap,null,activityFeedBacks);
		
		return activityFeedbackResponseDTO;

	}

	private ActivityFeedbackResponseDTO buildActivityFeedbackRespons(Integer pageSize,
			ActivityFeedbackResponseDTO employeeActivityResponse, Page<ActivityFeedback> activityFeedbacpage,
			boolean paginationRequired, Map<String, String> activityFeedBackMap,Map<Long, List<ImageDTO>> feedBackImageDTO,List<ActivityFeedback> activityFeedback) {

		if (paginationRequired) {
			Integer totalPages = activityFeedbacpage.getTotalPages();
			Long totalElements = activityFeedbacpage.getTotalElements();
			Integer pageNumber = activityFeedbacpage.getPageable().getPageNumber();
			boolean hasPrevious = activityFeedbacpage.hasPrevious();
			boolean hasNext = activityFeedbacpage.hasNext();
			employeeActivityResponse.setHasNext(hasNext);
			employeeActivityResponse.setHasPrevious(hasPrevious);
			employeeActivityResponse.setPageNo(pageNumber + 1);
			employeeActivityResponse.setPageSize(pageSize);
			employeeActivityResponse.setTotalPages(totalPages);
			employeeActivityResponse.setTotalElements(totalElements.intValue());
		}

		List<ActivityFeedbackDTO> activityFeedbackDto = getFeedbackDtoFromEntity(activityFeedback, activityFeedBackMap,feedBackImageDTO);
		employeeActivityResponse.setActivityFeedback(activityFeedbackDto);

		return employeeActivityResponse;
	}

	private List<ActivityFeedbackDTO> getFeedbackDtoFromEntity(List<ActivityFeedback> activityFeedback,
			Map<String, String> activityFeedBackMap,Map<Long, List<ImageDTO>> feedBackImageDTO) {

		List<ActivityFeedbackDTO> activityFeedbackDto = new ArrayList<>();
		for (ActivityFeedback a : activityFeedback) {
			ActivityFeedbackDTO d = new ActivityFeedbackDTO();
			d.setActivityId(a.getActivityName());
			d.setActivityName(activityFeedBackMap.get(a.getActivityName()));
			d.setEmployeeId(a.getEmployeeId());
			d.setEndDate(CommonUtils.formatLocalDateTimeWithTime(a.getEndDate()));
			d.setFeedback(CommonUtils.clobToString(a.getFeedback()));
			d.setLocation(a.getLocation());
			d.setMode(a.getMode());
			d.setRating(a.getRating());
			d.setStartDate(CommonUtils.formatLocalDateTimeWithTime(a.getStartDate()));
			d.setTagName(a.getTagName());
			d.setThemeName(a.getThemeName());
			d.setTimeRequired(a.getTimeRequired());
			d.setFeedbackId(a.getId().toString());
			d.setUploadedByAdmin(a.isUploadedByAdmin());
			d.setPublishOrUnPublish(a.getPublished() == null ? false : true);
			EmployeeDTO employe = new EmployeeDTO();
			try {
				employe = employeeService.getEmployeeById(a.getCreatedBy());
			} catch (EvpException e) {
				log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
						.collect(Collectors.joining("\n")));
				e.printStackTrace();
			}
			d.setFullName(employe.getEmployeeName());
			d.setCreatedDate(a.getCreatedOn());
			if(feedBackImageDTO!=null && !feedBackImageDTO.isEmpty()) {
				d.setImages(feedBackImageDTO.get(a.getId()));
			}
			
			activityFeedbackDto.add(d);
		}

		return activityFeedbackDto;

	}

	@Override
	public void deleteActivityFeedBack(String ids) throws EvpException {
		log.info("deleteActivityFeedBack");
		try {

			if(Optional.ofNullable(ids).isPresent() && !ids.isEmpty()) {
				
				String[]  idArray=ids.split(",");
				List<Long> feedbackIds= Stream.of(idArray).map(Long::valueOf).collect(Collectors.toList());
				List<ActivityFeedback> activityFeedbacks= (List<ActivityFeedback>) activityFeedbackRepository.findAllById(feedbackIds);
				
				activityFeedbackRepository.deleteAll(activityFeedbacks);
				
				List<ActivityFeedbackLocation> activityFeedbackLocations= activityFeedBackLocationRepository.findByFeedBackIds(feedbackIds);
				activityFeedBackLocationRepository.deleteAll(activityFeedbackLocations);
				
			}else {
				throw new EvpException("No Ids Present");
			}
			
		

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}
		
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public void UploadToGallery(String ids) throws EvpException {
		log.info("uploadFeedbackToGalary");
		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		log.info("role : ".concat(role));

		try {
			if (role.equals(Constants.ROLE_ADMIN) || role.equals("ROLE_CADMIN")) {
				List<String> Stringids = Arrays.asList(ids.split(","));
				List<ActivityFeedback> list = activityFeedbackRepository.getFeedbackByIds(Stringids);
				for (ActivityFeedback activityFeedback : list) {
					activityFeedback.setDeleted(false);;
					activityFeedback.setUploadedByAdmin(true);
				}

				activityFeedbackRepository.saveAll(list);

			} else {
				log.error("Only admin can access");
				throw new EvpException("Internal Server Error");
			}
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public Map<String, Object> getGalleryFeedbacks(Integer pageNo, Integer pageSize, SearchCriteria criteria) {

		log.info("getGalleryFeedbacks");
		if (!Optional.ofNullable(criteria).isPresent()) {
			criteria = new SearchCriteria();
		}

		List<String> theamNames = new ArrayList<>();
		if (Optional.ofNullable(criteria.getThemeName()).isPresent()) {
			theamNames = Arrays.asList(criteria.getThemeName().split(","));
		} else {
			for (Entry<String, Long> theme : evpLovService.getThemeLovMap().entrySet()) {
				theamNames.add(theme.getKey());
			}

		}

		criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);

		Map<String, Object> m = new HashMap<>();

		// criteria.setThemeNames(null);

		for (String theme : theamNames) {

			log.info("theme : ".concat(theme));
			Map<String, Object> m2 = new HashMap<>();

			List<String> list = new ArrayList<>();
			list.add(theme);
			criteria.setThemeNames(list);
			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			
			// published
			log.info("published");
			criteria.setUploadedByAdmin(Constants.TRUE);
			criteria.setPublishOrUnpublish(Constants.TRUE);
			criteria.setDeletedByAdmin(Constants.FALSE);;
			ActivityFeedbackResponseDTO published = getActivityFeedBack(criteria, pageNo, pageSize);
			m2.put("published", published);

			if(role.equals("ROLE_ADMIN") || role.equals("ROLE_CADMIN")) {
				// unpblished
				log.info("unPublished");
				criteria.setUploadedByAdmin(Constants.TRUE);
				criteria.setPublishOrUnpublish(Constants.FALSE);
				criteria.setDeletedByAdmin(Constants.FALSE);
				ActivityFeedbackResponseDTO unpblished = getActivityFeedBack(criteria, pageNo, pageSize);
				m2.put("unPublished", unpblished);

				
			}
			
			m.put(theme, m2);
		

		}

		return m;

	}

	@Override
	public void deleteGalleryFeedbacks(String ids) throws EvpException {

		log.info("uploadFeedbackToGalary");
		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		log.info("role : ".concat(role));

		try {
			if (role.equals(Constants.ROLE_ADMIN) || role.equals("ROLE_CADMIN")) {
				List<String> Stringids = Arrays.asList(ids.split(","));
				List<ActivityFeedback> list = activityFeedbackRepository.getFeedbackByIds(Stringids);
				for (ActivityFeedback activityFeedback : list) {
					activityFeedback.setDeleted(true);
				}
				activityFeedbackRepository.saveAll(list);

			} else {
				log.error("Only admin can access");
				throw new EvpException("Internal Server Error");
			}
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public ActivityFeedbackResponseDTO getActivityFeedBack(String searchCriteria, Integer pageNo, Integer pageSize) {
		ActivityFeedbackResponseDTO activityFeedbackResponseDTO = ActivityFeedbackResponseDTO.builder().build();
		try {
			log.info("getActivityFeedBack by  activityName");

			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			log.info("role : ".concat(role));

			Pageable pageable = null;
			if (pageNo != null && pageSize != null) {
				pageable = PageRequest.of(pageNo - 1, pageSize);
			}

			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			criteria = CommonUtils.buildParamsForSearchCriteria(criteria, evpLovService.getLocationLovMap(), evpLovService.getThemeLovMap(), evpLovService.getModeLovMap(), evpLovService.getTagLovMap());

			criteria.setRole(role);
			
			Specification<ActivityFeedback> feedbackSpecs = CommonSpecification
					.getActivityFeedbackSpecificationForActivityDetails(criteria);

			Page<ActivityFeedback> feedbacks = null;
			if (pageable != null) {
				feedbacks = activityFeedbackRepository.findAll(feedbackSpecs, pageable);
			} else {
				feedbacks = new PageImpl<>(activityFeedbackRepository.findAll(feedbackSpecs));
			}
			
			
			
			List<ActivityFeedback> activityFeedBacks=new ArrayList<>();
			
			if(role.equals("ROLE_EMPLOYEE") || role.equals("ROLE_ADMIN")) {
				
				List<ActivityFeedbackLocation> activityFeedbackLocations= activityFeedBackLocationRepository.findByMultiLocation(Arrays.asList(criteria.getLocation()));
				
				List<Long> locationFeedBackIds=activityFeedbackLocations.stream().map(ActivityFeedbackLocation::getFeedbackId).collect(Collectors.toList());
				
				activityFeedBacks= feedbacks.getContent().stream().filter(feedback->locationFeedBackIds.contains(feedback.getId())).collect(Collectors.toList());
				
				
			}else {
				activityFeedBacks=feedbacks.getContent();
			}

			Map<String, String> activityFeedBackMap = getActivityMapByFeedback(feedbacks.getContent());

			Map<Long, List<ImageDTO>> feedBackImageDTO=new HashMap<>();
			List<ImageDTO> images =null;
			if (criteria.getEmployeeId() != null) {
				Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(criteria.getEmployeeId());

				if (employeeOpt.isPresent()) {
					List<ActivityPicture> activityPictures = activityPictureRepository
							.findByActivityName(criteria.getActivityIds(), employeeOpt.get().getEmployeeName());
					if (!activityPictures.isEmpty()) {
						Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);

						images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures,
								ImageType.EMPLOYEE_UPLOAD.getImageType(), activityMap);

						feedBackImageDTO = images.stream().filter(image->image.getFeedbackId()!=null).collect(Collectors.groupingBy(ImageDTO::getFeedbackId,
								Collectors.mapping(Function.identity(), Collectors.toList())));
					}
				}
			}
				else {
					List<ActivityPicture> activityPictures= activityPictureRepository.findByActivityName(criteria.getActivityIds());	
					if (!activityPictures.isEmpty()) {

						Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);

						images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures,
								ImageType.EMPLOYEE_UPLOAD.getImageType(), activityMap);
						if(images!=null) {
						feedBackImageDTO=images.stream()
								.filter(image->image.getFeedbackId()!=null)
								.collect(Collectors.groupingBy(ImageDTO::getFeedbackId, Collectors.mapping(Function.identity(),Collectors.toList())));
						}
						}
				}
				
				return buildActivityFeedbackRespons(pageSize, activityFeedbackResponseDTO, feedbacks, true,activityFeedBackMap,feedBackImageDTO,activityFeedBacks);
			} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			
		}
		return activityFeedbackResponseDTO;

	}

	private ActivityPromotion getActivityPromotionFromdto(ActivityPromotionDTO dto,ActivityPromotion existing)
			throws EvpException {
		try {

			String activityName = dto.getPromotionActivity();
			String activityId = dto.getActivityId();
			Optional<Activity> activityOpt = activityRepository.findByActivityId(activityId);
			LocalDateTime startDate = null;
			LocalDateTime endDate = null;
			Activity activity=null;
			String tagName=null;
			String mode=null;
			if (activityOpt.isPresent()) {
				activityName = activityOpt.get().getActivityName();
				 activity = activityOpt.get();
				startDate = activity.getStartDate();
				endDate = activity.getEndDate();
				mode=CommonUtils.getLovMapWithIdKey(evpLovService.getModeLovMap()).get(activity.getModeOfParticipationId());
				tagName=CommonUtils.getLovMapWithIdKey(evpLovService.getTagLovMap()).get(activity.getTagId());
			}
			if(Optional.ofNullable(existing).isPresent()) {
				existing.setActivityEndDate(endDate);
				existing.setActivityStartDate(startDate);
				existing.setActivityId(activityId);
				existing.setEndDate(CommonUtils.getActivityDate(dto.getEndDate()));
				existing.setStartDate(CommonUtils.getActivityDate(dto.getStartDate()));
				existing.setPromotionActivity(activityName);
				existing.setPromotionTheme(dto.getPromotionTheme());
				existing.setMode(mode);
				existing.setTagName(tagName);
				return existing;
			}else {

				return ActivityPromotion.builder().activityEndDate(endDate).activityEndDate(endDate)
						.activityStartDate(startDate).activityId(activityId)
						.endDate(CommonUtils.getActivityDate(dto.getEndDate()))
						.startDate(CommonUtils.getActivityDate(dto.getStartDate())).promotionActivity(activityName)
						.promotionTheme(dto.getPromotionTheme())
						.activityId(activity.getActivityId())
						.mode(mode)
						.tagName(tagName)
						.build();
			}
		
		

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public void addActivityPromotion(ActivityPromotionDTO dto) throws EvpException {

		boolean existingPromotion=false;
		try {
		List<ActivityPromotionLocation> promotionLocation = new ArrayList<>();
		
		
		LocalDateTime startDate=CommonUtils.getActivityDate(dto.getStartDate());
		
		LocalDateTime endDate=CommonUtils.getActivityDate(dto.getEndDate());
		
		String activityId=dto.getActivityId();
		
		Optional<Activity> activity= activityRepository.findByActivityId(activityId);
		
		
		List<String> promotionLocations= dto.getPromotionlocations();
		
		List<Long> pIds= promotionLocations.stream().map(p->evpLovService.getLocationLovMap().get(p)).collect(Collectors.toList());
		
		List<ActivityLocation> aPromoLocations= activityLocationRepository.findByActivityId(activityId);
		List<Long> activityLocations= aPromoLocations.stream().map(ActivityLocation::getLocationId).collect(Collectors.toList());
		
		Optional<Long> location= pIds.stream().filter(pLocation->!activityLocations.contains(pLocation)).findFirst();
		if(location.isPresent()) {
			throw new EvpException("Activity doesn't have specified location");
		}
		
		if(activity.isPresent()) {
			if(activity.get().getEndDate().toLocalDate().compareTo(startDate.toLocalDate()) < 0) {
				throw new EvpException("Activity is already completed");
			}
		}
		Long themeId = evpLovService.getThemeLovMap().get(dto.getPromotionTheme());
		ActivityPromotion activityPromotion=null;
		List<String> locations=dto.getPromotionlocations();
		if (!Optional.ofNullable(themeId).isPresent()) {
			log.error("Theme Doesn't exist");
			throw new EvpException("Theme Doesn't exist");
		}

		if(Optional.ofNullable(dto.getPromotionId()).isPresent()) {
			Optional<ActivityPromotion> existingOpt= activityPromotionRepository.findById(Long.valueOf(dto.getPromotionId()));
			
			if(existingOpt.isPresent()) {
				List<Boolean> validDataCheckList=new ArrayList<>(1);
				activityPromotion= existingOpt.get();
				
				List<ActivityPromotionLocation> activityPromotionLocations= activityPromotionLocationRepostiroy.findByMultiLocation(dto.getPromotionlocations());
				
				List<Long> feedBackIds= activityPromotionLocations.stream().map(ActivityPromotionLocation::getPromotionId).filter(promotionId->!Long.valueOf(dto.getPromotionId()).equals(promotionId)).collect(Collectors.toList());
				
				
				List<ActivityPromotion> activityExistingPromotions= activityPromotionRepository.getFeedbackByIds(feedBackIds);
				
				Optional<ActivityPromotion> activityExistingPromotionOpt = activityExistingPromotions.stream()
						.filter(a -> a.getActivityId().equals(dto.getActivityId())).findFirst();
				
				
				if(activityExistingPromotionOpt.isPresent() && !activityExistingPromotionOpt.get().getActivityId().equals(dto.getActivityId())) {
					throw new EvpException("Promotion for same activity already exists");
				}
				
				if(!activityPromotion.getActivityId().equals(dto.getActivityId())) {	
					throw new EvpException("Promotion for same location already exists");
				}
				
				
				if(activityExistingPromotionOpt.isPresent() && !activityExistingPromotionOpt.get().getActivityId().equals(dto.getActivityId())) {
					Optional<ActivityPromotion> activityPromOpt = activityExistingPromotions.stream().filter(
							activityExistingPromotion -> activityExistingPromotion.getStartDate().toLocalDate().compareTo(startDate.toLocalDate()) >= 0
									&& activityExistingPromotion.getEndDate().toLocalDate().compareTo(endDate.toLocalDate()) <= 0)
							.findFirst();
					
					
					if (activityPromOpt.isPresent()) {
						throw new EvpException("Activity Promotion in same date range already exists");
					}
				}
					activityExistingPromotions.forEach(activityExistingPromotion->{
						
						Date s1 = Date.from(startDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
						Date e1 = Date.from(endDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
						Date s2 = Date.from(activityExistingPromotion.getStartDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
						Date e2 = Date.from(activityExistingPromotion.getEndDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
						if((s1.before(s2) && e1.after(s2) ||
							       s1.before(e2) && e1.after(e2) ||
							       s1.before(s2) && e1.after(e2) ||
							       s1.after(s2) && e1.before(e2) )){
							log.info(activityExistingPromotion.getStartDate().toLocalDate()+"--------------"+activityExistingPromotion.getEndDate().toLocalDate());
							validDataCheckList.add(true);
							}else if(s1.equals(s2)|| e1.equals(e2)|| s1.equals(e2)||e1.equals(s2)) {
								validDataCheckList.add(true);
							}
					});
							
					
					
					
					if(!validDataCheckList.isEmpty()) {
						throw new EvpException("Activity Promotion in same date range already exists");
					}
				
				
				
				
				activityPromotion = getActivityPromotionFromdto(dto,activityPromotion);
				existingPromotion=true;
			}else {
				throw new EvpException("Activity Promotion Doesn't exist with given Id");
			}
		}else {
			List<Boolean> validDataCheckList=new ArrayList<>(1);
			List<ActivityPromotionLocation> activityPromotionLocations= activityPromotionLocationRepostiroy.findByMultiLocation(dto.getPromotionlocations());
			
			List<Long> feedBackIds= activityPromotionLocations.stream().map(ActivityPromotionLocation::getPromotionId).collect(Collectors.toList());
			
			
			List<ActivityPromotion> activityExistingPromotions= activityPromotionRepository.getFeedbackByIds(feedBackIds);
			
			
			Optional<ActivityPromotion> activityPromOpt = activityExistingPromotions.stream().filter(
					activityExistingPromotion -> activityExistingPromotion.getStartDate().toLocalDate().compareTo(startDate.toLocalDate()) >= 0
							&& activityExistingPromotion.getEndDate().toLocalDate().compareTo(endDate.toLocalDate()) <= 0)
					.findFirst();
			
			
			if (activityPromOpt.isPresent()) {
				throw new EvpException("Activity Promotion in same date range already exists");
			}
			 
			activityExistingPromotions.forEach(activityExistingPromotion->{
				
				Date s1 = Date.from(startDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date e1 = Date.from(endDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date s2 = Date.from(activityExistingPromotion.getStartDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				Date e2 = Date.from(activityExistingPromotion.getEndDate().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
				if((s1.before(s2) && e1.after(s2) ||
					       s1.before(e2) && e1.after(e2) ||
					       s1.before(s2) && e1.after(e2) ||
					       s1.after(s2) && e1.before(e2) )){
					log.info(activityExistingPromotion.getStartDate().toLocalDate()+"--------------"+activityExistingPromotion.getEndDate().toLocalDate());
					validDataCheckList.add(true);
					}else if(s1.equals(s2)|| e1.equals(e2)|| s1.equals(e2)||e1.equals(s2)) {
						validDataCheckList.add(true);
					}
			});
					
			
			
			
			if(!validDataCheckList.isEmpty()) {
				throw new EvpException("Activity Promotion in same date range already exists");
			}
			
			
			
			activityPromotion = getActivityPromotionFromdto(dto,null);
			
		}
		
		ActivityPromotion a=  activityPromotionRepository.save(activityPromotion);
			ActivityPicture activityPicture=null;
			List<ActivityPicture> activityPictures= activityPictureRepository.findByActivityNameAndImageType(dto.getActivityId(), ImageType.PROMOTIONS);
			
			if(activityPictures.size()>0) {
				Optional<ActivityPicture> activityPictureOpt= activityPictures.stream().filter(aPicture->aPicture.getPromotionId()==null).findFirst();
				if(activityPictureOpt.isPresent()) {
					activityPicture=activityPictureOpt.get();
					activityPicture.setPromotionId(a.getId());
				}
			}
			if(Optional.ofNullable(activityPicture).isPresent()) {
				activityPictureRepository.save(activityPicture);
			}
		
		
		
		List<ActivityPromotionLocation> activityPromoLocations= activityPromotionLocationRepostiroy.findByPromotionId(a.getId());
	
		if(activityPromoLocations.size()>0) {
			activityPromotionLocationRepostiroy.deleteAll(activityPromoLocations);
		}
		
		locations.forEach(l->{
			ActivityPromotionLocation activityPromotionLocation= ActivityPromotionLocation.builder().activityId(a.getActivityId()).promotionId(a.getId())
			.location(l).build();
			promotionLocation.add(activityPromotionLocation);
		});
		activityPromotionLocationRepostiroy.saveAll(promotionLocation);
		}catch (EvpException e) {
			
			log.error(e.getMessage());	
			
			if (!existingPromotion) {
				List<ActivityPicture> activityPictures = activityPictureRepository
						.findByActivityNameAndImageTypeForOrphanPromotions(dto.getActivityId(), ImageType.PROMOTIONS);
				activityPictureRepository.deleteAll(activityPictures);
			}
			throw new EvpException(e.getMessage());
		}
		
		catch (Exception e) {
			
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
			
		}
		
	}

	@Override
	public void deleteActivityPromotion(String ids) throws EvpException {

		log.info("uploadFeedbackToGalary");
		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		log.info("role : ".concat(role));

		try {
			if (role.equals(Constants.ROLE_ADMIN) || role.equals("ROLE_CADMIN")) {
				List<String> promoIds = Arrays.asList(ids.split(","));
				List<Long> promotionIds= promoIds.stream().map(Long::valueOf).collect(Collectors.toList());
				
				List<ActivityPromotion> list = activityPromotionRepository.getFeedbackByIds(promotionIds);
				if (!list.isEmpty()) {

					List<ActivityPromotionLocation> activityPromotionLocations = activityPromotionLocationRepostiroy
							.findByPromotionIds(promotionIds);
					activityPromotionLocationRepostiroy.deleteAll(activityPromotionLocations);

					activityPromotionRepository.deleteAll(list);
					
					List<Long> promoPicIds= promotionIds.stream().map(promotionId->Long.valueOf(promotionId)).collect(Collectors.toList());
					
					List<ActivityPicture> activityPictures= activityPictureRepository.findByPromotionIds(promoPicIds);
					activityPictureRepository.deleteAll(activityPictures);
				}

			} else {
				log.error("Only admin can access");
				throw new EvpException("Internal Server Error");
			}
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public ActivityPromotionResponseDTO getActivityPromotion(SearchCriteria criteria, Integer pageNo,
			Integer pageSize) {
		ActivityPromotionResponseDTO activityPromotionResponseDTO = ActivityPromotionResponseDTO.builder().build();
		List<ActivityPromotion> promotions = null;
		try {
			Map<Long, ActivityPicture> promotionMap = new HashMap<>();
			if (!Optional.ofNullable(criteria).isPresent()) {
				criteria = new SearchCriteria();
			}

			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			criteria.setRole(role);

			String username = CommonUtils.getUsername(request, jwtUtil);
			criteria.setUsername(username);

			criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(criteria);
			
			String[] locationArray=criteria.getLocation().split(",");
			

			List<String> locationIds= Arrays.asList(locationArray);
			
			Specification<ActivityPromotion> specification = CommonSpecification
					.getActivityPromotionSpecification(criteria);

			Sort sort = Sort.by(Direction.DESC, "createdOn");

			promotions = activityPromotionRepository.findAll(specification,sort);
			
			

			if (role.equals("ROLE_ADMIN") || role.equals("ROLE_CADMIN")) {

				if (!promotions.isEmpty()) {

					List<Long> promotionIds = promotions.stream().map(ActivityPromotion::getId)
							.collect(Collectors.toList());
					List<ActivityPromotionLocation> activityPromotionLocations = activityPromotionLocationRepostiroy
							.findByPromotionIds(promotionIds);
					
					Map<Long, List<String>> activityPromotionLocationMap = activityPromotionLocations.stream()
							.collect(Collectors.groupingBy(ActivityPromotionLocation::getPromotionId,
									Collectors.mapping(ActivityPromotionLocation::getLocation, Collectors.toList())));
					
					
					promotions=filterByLocation(locationIds, promotions);
					
					
					filterLocationIds(locationIds, activityPromotionLocationMap);
					
					

					promotions.forEach(activityPromotion -> {
						List<String> locations = activityPromotionLocationMap.get(activityPromotion.getId());
						if (!locations.isEmpty()) {
							String l = locations.stream().collect(Collectors.joining(","));

							activityPromotion.setPromotionlocation(l);
						}

					});

					List<ActivityPicture> promotionPictures = activityPictureRepository
							.findByPromotionIds(promotionIds);

					promotionMap = promotionPictures.stream()
							.collect(Collectors.toMap(ActivityPicture::getPromotionId, Function.identity()));

				}
				

				return buildActivityFeedbackRespons(pageSize,pageNo, activityPromotionResponseDTO, promotions, true,
						promotionMap);
			} else {
				LocalDate currentDate = LocalDate.now();
				
				Optional<ActivityPromotion> acOpt = promotions.stream()
						.filter(p -> p.getStartDate().toLocalDate().compareTo(currentDate) <= 0
								&& p.getEndDate().toLocalDate().compareTo(currentDate) >= 0)
						.findFirst();

				if (acOpt.isPresent()) {
					List<ActivityPicture> promotionPictures = activityPictureRepository
							.findByPromotionIds(Arrays.asList(acOpt.get().getId()));

					promotionMap = promotionPictures.stream()
							.collect(Collectors.toMap(ActivityPicture::getPromotionId, Function.identity()));

					List<ActivityPromotionLocation> activityPromotionLocations = activityPromotionLocationRepostiroy
							.findByPromotionIds(Arrays.asList(acOpt.get().getId()));
					Map<Long, List<String>> activityPromotionLocationMap = activityPromotionLocations.stream()
							.collect(Collectors.groupingBy(ActivityPromotionLocation::getPromotionId,
									Collectors.mapping(ActivityPromotionLocation::getLocation, Collectors.toList())));

					List<String> locations = activityPromotionLocationMap.get(acOpt.get().getId());
					if (!locations.isEmpty()) {
						String l = locations.stream().collect(Collectors.joining(","));

						acOpt.get().setPromotionlocation(l);
					}

					promotions = Arrays.asList(acOpt.get());
					promotions=filterByLocation(locationIds, promotions);

					return buildActivityFeedbackRespons(pageSize,pageNo, activityPromotionResponseDTO, promotions, false,
							promotionMap);
				}
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			return activityPromotionResponseDTO;
		}

		return activityPromotionResponseDTO;

	}

	private ActivityPromotionResponseDTO buildActivityFeedbackRespons(Integer pageSize,Integer pageNumber,
			ActivityPromotionResponseDTO activityPromotionResponseDTO, List<ActivityPromotion> activityFeedbacks,
			boolean paginationRequired,Map<Long, ActivityPicture> promotionMap) {

		ActivityPromotionResponseDTO res = new ActivityPromotionResponseDTO();
		if (paginationRequired) {
			
			res=CommonUtils.getPaginationForActivityPromotion(pageNumber, pageSize, activityFeedbacks.size());
			List<List<ActivityPromotion>> activityPromotions= CommonUtils.batchesOfList(activityFeedbacks, pageSize);
			res.setActivityPromotion(
					activityPromotions.isEmpty() ? Collections.emptyList() : getPromotionsDtoFromEntity(activityPromotions.get(pageNumber-1),promotionMap));
		}else {
			res.setActivityPromotion(getPromotionsDtoFromEntity(activityFeedbacks,promotionMap));
		}

		

		return res;

	}

	@Override
	public void publishOrUnpublish(String ids, String status) throws EvpException {

		log.info("publishOrUnpublish");
		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		log.info("role : ".concat(role));

		try {
			if (role.equals(Constants.ROLE_ADMIN) || role.equals("ROLE_CADMIN")) {
				List<String> Stringids = Arrays.asList(ids.split(","));
				List<ActivityFeedback> list = activityFeedbackRepository.getFeedbackByIds(Stringids);

				for (ActivityFeedback a : list) {
					if (status.equals(Constants.PUBLISHED)) {
						a.setPublished(true);
					}
					if (status.equals(Constants.UNPUBLISHED)) {
						a.setPublished(false);
					}
				}
				activityFeedbackRepository.saveAll(list);

			} else {
				log.error("Only admin can access");
				throw new EvpException("Internal Server Error");
			}
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private List<ActivityPromotionDTO> getPromotionsDtoFromEntity(List<ActivityPromotion> list,Map<Long, ActivityPicture> promotionMap) {

		List<ActivityPromotionDTO> resp = new ArrayList<>();
		List<ActivityPicture> activityPictures= new ArrayList<>(promotionMap.values());
		
		Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
		for (ActivityPromotion entity : list) {
			List<ImageDTO> images =null;
			ActivityPicture activityPicture= promotionMap.get(entity.getId());
			if(Optional.ofNullable(activityPicture).isPresent()) {
				images=Arrays.asList(CommonUtils.convertToImageDTO(
						ImageType.PROMOTIONS.getImageType(), activityMap,activityPicture));
			}else {
				images=Collections.emptyList();
			}
			

			ActivityPromotionDTO activityPromotionDTO = ActivityPromotionDTO.builder()
					.promotionId(entity.getId().toString())
					.activityEndDate(CommonUtils.formatLocalDateTimeWithTime(entity.getActivityEndDate()))
					.activityStartDate(CommonUtils.formatLocalDateTimeWithTime(entity.getActivityStartDate()))
					.promotionTheme(entity.getPromotionTheme()).activityId(entity.getActivityId())
					.promotionActivity(entity.getPromotionActivity()).promotionlocation(entity.getPromotionlocation())
					.startDate(CommonUtils.formatLocalDateTimeWithTime(entity.getStartDate()))
					.endDate(CommonUtils.formatLocalDateTimeWithTime(entity.getEndDate()))
					.activityId(entity.getActivityId()).images(images).build();

			resp.add(activityPromotionDTO);
		}

		return resp;
	}

	public Map<String, String> getActivityMapByFeedback(List<ActivityFeedback> activityFeedBacks) {

		Map<String, String> activityMap = new HashMap<>();
		if (!activityFeedBacks.isEmpty()) {
			List<String> activityIds = activityFeedBacks.stream().map(ActivityFeedback::getActivityName)
					.collect(Collectors.toList());
			List<Activity> activities = activityRepository.findByActivityIds(activityIds);
			activityMap = activities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));
		}

		return activityMap;

	}
	
	
	private void sendEmployeeParticipateEmail(Activity activity,EmployeeActivityHistory employeeActivityHistory,EmployeeActivityStatus employeeActivityStatus) {
		log.info("sendEmployeeParticipateEmail");
		FileSystemResource fileSystemResource=null;
		log.info("Searching Employee");
		Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeActivityHistory.getEmployeeId());
		log.info("Found Employee ",employeeOpt.get());
		if (employeeOpt.isPresent()) {
			log.info("Employee Found");
			Employee e = employeeOpt.get();
			if (Optional.ofNullable(e.getEmail()).isPresent()) {
				log.info("Email Id {}",e.getEmail());

			EmailTemplateData emailTemplateData= EmailTemplateData.builder().employeeName(e.getEmployeeName()).activityName(activity.getActivityName())
					.build();
			log.info("Email Template Data is Created ");
			EmailType emailType=null;
			log.info("EMployee Activity Status {}",employeeActivityStatus.getStatus());
			switch (employeeActivityStatus) {
			case ENROLLED:
				emailType=EmailType.ENROLL_ACTIVITY;
				break;
			case PARTICIPATED:
			case FEEDBACK:
				emailType=EmailType.CONFIRM_PARTICIPATION;
				try {
					log.info("Started creating PDF Certificate");
					String path=activityService.downloadCertificate(e.getEmployeeName(), activity.getActivityName(),e.getEmployeeId());
					log.info("Generated File Path {}",path );
					File file=new File(path);
					log.info("FIle path",path );
					if(path!=null) {
					fileSystemResource=new FileSystemResource(file);
					}
				}catch (Exception e1) {
					log.error(e1.getMessage());
				}
				break;
			default:
				break;
			}
			
			emailService.sendEmail(emailType, emailTemplateData, e.getEmail(),fileSystemResource);
				
			} else {
				log.error("email not existing for login user ");
			}

		} else {
			log.error("User is not valid");
		}

	}
	
	public List<ActivityPromotion> filterByLocation(List<String> activityLocationIds, List<ActivityPromotion> activities) {

		if (Optional.ofNullable(activityLocationIds).isPresent() && !activityLocationIds.isEmpty()) {

			List<ActivityPromotionLocation> activityLocations = activityPromotionLocationRepostiroy
					.findByMultiLocation(activityLocationIds);
			List<Long> activityIds = activityLocations.stream().map(ActivityPromotionLocation::getPromotionId)
					.collect(Collectors.toList());
			activities = activities.stream().filter(activity -> activityIds.contains(activity.getId()))
					.collect(Collectors.toList());
		}

		return activities;
	}
	
	private void filterLocationIds(List<String> activityLocationIds, Map<Long, List<String>> activityLocationMap) {
		if (activityLocationIds.size() == 1) {
			String locationId = activityLocationIds.get(0);
			activityLocationMap.entrySet().forEach(entry -> {
				Optional<String> l1 = entry.getValue().stream().filter(l -> l != null).filter(l -> l.equals(locationId))
						.findFirst();
				if (l1.isPresent()) {
					activityLocationMap.put(entry.getKey(), Arrays.asList(locationId));
				}

			});
		}else {
			activityLocationMap.entrySet().forEach(entry -> {
				List<String> l1 = entry.getValue().stream().filter(l -> l != null).filter(l -> activityLocationIds.contains(l))
						.collect(Collectors.toList());
				if (!l1.isEmpty()) {
					activityLocationMap.put(entry.getKey(), l1);
				}

			});
		}
	}
	

	

}
