package com.speridian.asianpaints.evp.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.text.log.SysoLogger;
import com.speridian.asianpaints.evp.constants.ActivityType;
import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.ActivityFinancialDTO;
import com.speridian.asianpaints.evp.dto.ActivityFinancialResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityList;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityTagResponse;
import com.speridian.asianpaints.evp.dto.CreateOrUpdateActivityDTO;
import com.speridian.asianpaints.evp.dto.CreatedActivities;
import com.speridian.asianpaints.evp.dto.CreativeImages;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryResponseDTO;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponse;
import com.speridian.asianpaints.evp.dto.EmployeeActivityResponseDTO;
import com.speridian.asianpaints.evp.dto.GalleryResponseDTO;
import com.speridian.asianpaints.evp.dto.ImageDTO;
import com.speridian.asianpaints.evp.dto.OngoingActivities;
import com.speridian.asianpaints.evp.dto.PastActivities;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.dto.UpcomingActivities;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityFinancial;
import com.speridian.asianpaints.evp.entity.ActivityLocation;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.ActivityPromotion;
import com.speridian.asianpaints.evp.entity.ActivityPromotionLocation;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.entity.MailConfig;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.EmailService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.service.LoginService;
import com.speridian.asianpaints.evp.service.UploadService;
import com.speridian.asianpaints.evp.transaction.ActivityTransaction;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFeedbackRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFinancialRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityLocationRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPictureRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPromotionLocationRepostiroy;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPromotionRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.transactional.repository.MailConfigRepository;
import com.speridian.asianpaints.evp.util.CommonSpecification;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.ImageType;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author sony.lenka
 *
 */
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {

	@Autowired
	private EvpLovService evpLovService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private ActivityFinancialRepository activityFinancialRepository;

	@Autowired
	private ActivityTransaction activityTransaction;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private EmployeeActivityHistoryRepository employeeActivityHistoryRepository;

	@Autowired
	private ActivityPictureRepository activityPictureRepository;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Autowired
	private UploadService uploadService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private LoginService loginService;

	@Value("${authorised.signature}")
	private String signature;

	@Value("${certificate.path}")
	private String certificatePath;

	@Value("${authorised.name}")
	private String name;

	@Value("${authorised.designation}")
	private String designation;
	@Value("${evp.apigee.central.admin.emailId}")
	private String centralAdminEmailId;

	@Autowired
	private MailConfigRepository mailConfigRepository;

	@Autowired
	private ActivityPromotionRepository activityPromotionRepository;

	@Autowired
	private ActivityPromotionLocationRepostiroy activityPromotionLocationRepostiroy;

	@Autowired
	private ActivityFeedbackRepository activityFeedbackRepository;

	@Autowired
	private ActivityLocationRepository activityLocationRepository;

	@Override
	public CreateOrUpdateActivityDTO createOrUpdateActivity(CreateOrUpdateActivityDTO createOrUpdateActivityDTO)
			throws EvpException {

		try {
			String activityLocation = createOrUpdateActivityDTO.getActivityLocation();

			if (!Optional.ofNullable(activityLocation).isPresent()) {
				throw new EvpException("Activity Cannot be created without Location");
			}
			if (!Optional.ofNullable(createOrUpdateActivityDTO.getActivityId()).isPresent()) {
				throw new EvpException("Activity Cannot be created without Activity Id");
			}

			String[] locations = activityLocation.split(",");

			List<String> locationList = Arrays.asList(locations);
			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
			if (employeeOpt.isPresent()) {
				String role = employeeOpt.get().getRole();

				if (role != null && !role.equalsIgnoreCase("ROLE_CADMIN")) {
					if (locationList.size() > 1) {
						throw new EvpException("Multi Location is not allowed for role " + role);
					}
				} else if (role == null) {
					throw new EvpException("Invalid role for creating activity ");
				}

			}

			String themeName = createOrUpdateActivityDTO.getThemeName();

			createActivityForLocations(createOrUpdateActivityDTO, activityLocation, locationList, themeName);

			if (Optional.ofNullable(createOrUpdateActivityDTO).isPresent() && createOrUpdateActivityDTO.isPublished()) {

				Optional<Activity> activityOpt = activityRepository
						.findByActivityId(createOrUpdateActivityDTO.getActivityId());
				if (activityOpt.isPresent()) {

					Activity activity = activityOpt.get();
					if (!Optional.ofNullable(activity.getMailNotificationSent()).isPresent()) {
						activity.setMailNotificationSent(true);
						activityRepository.save(activity);
						String url = Optional.ofNullable(createOrUpdateActivityDTO.getActivityUrl()).isPresent()
								? createOrUpdateActivityDTO.getActivityUrl()
								: new String();

						log.info("sendActivityCreatedEmail");
						log.info("employeeId : ".concat(employeeId));
						if (Optional.ofNullable(createOrUpdateActivityDTO.getActivityUUID()).isPresent()) {
							sendActivityCreatedEmail(createOrUpdateActivityDTO,
									createOrUpdateActivityDTO.getActivityFinancialDTO(), url, employeeOpt,
									centralAdminEmailId);
						}
						if (createOrUpdateActivityDTO.isNeedRequestFromCCSR()
								&& Optional.ofNullable(createOrUpdateActivityDTO.getActivityUUID()).isPresent()) {
							sendNeedSupportFromCCSRTeamEmail(createOrUpdateActivityDTO, centralAdminEmailId,
									employeeOpt);
						}

					}

				}

			}

			return createOrUpdateActivityDTO;
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private CreateOrUpdateActivityDTO createActivityForLocations(CreateOrUpdateActivityDTO createOrUpdateActivityDTO,
			String activityLocation, List<String> locationList, String themeName) throws EvpException {
		try {
			return createActivityForLocation(createOrUpdateActivityDTO, locationList, themeName);
		} catch (EvpException e) {
			throw new EvpException(e.getMessage());
		}

	}

	private CreateOrUpdateActivityDTO createActivityForLocation(CreateOrUpdateActivityDTO createOrUpdateActivityDTO,
			List<String> activityLocation, String themeName) throws EvpException {
		Long tagId = null;
		ActivityFinancial activityFinancial = null;
		Activity existingActivity = null;

		try {

			String modeOfParticipation = createOrUpdateActivityDTO.getModeOfParticipation();

			String tagName = createOrUpdateActivityDTO.getTagName();

			log.info("Creating Or updating activity with theme name " + themeName + "  activity location "
					+ activityLocation + " mode " + modeOfParticipation);

			List<Long> locationIds = activityLocation.stream()
					.map(location -> evpLovService.getLocationLovMap().get(location))
					.filter(location -> location != null).collect(Collectors.toList());

			Long themeId = evpLovService.getThemeLovMap().get(themeName);

			if (!Optional.ofNullable(themeId).isPresent()) {
				log.error("Theme Doesn't exist");
				throw new EvpException("Theme Doesn't exist");
			}

			Long modeId = evpLovService.getModeLovMap().get(modeOfParticipation);

			if (!Optional.ofNullable(modeId).isPresent()) {
				log.error("Mode Doesn't exist");
			}

			Optional<Long> tagIdOpt = evpLovService.getTagLovMap().entrySet().stream()
					.filter(t -> t.getKey().equals(tagName)).map(t -> t.getValue()).findFirst();
			if (tagIdOpt.isPresent()) {
				tagId = tagIdOpt.get();
			} else {
				log.error("Tag Doesn't exist");
				throw new EvpException("Tag Doesn't exist");
			}

			String activityUUId = createOrUpdateActivityDTO.getActivityUUID();

			if (Optional.ofNullable(activityUUId).isPresent() && !activityUUId.isEmpty()) {
				log.info("Activity UUID {} ", activityUUId);
				existingActivity = activityRepository.findByActivityUUID(activityUUId);

			}

			if (Optional.ofNullable(existingActivity).isPresent()) {
				log.info("Activity Is Present");
			}

			if (Optional.ofNullable(existingActivity).isPresent()) {
				Long activityFinancialId = existingActivity.getActivityFinancialId();

				Optional<ActivityFinancial> activityFinancialOpt = activityFinancialRepository
						.findById(activityFinancialId);

				if (activityFinancialOpt.isPresent()) {

					activityFinancial = activityFinancialOpt.get();
				} else {
					log.error("Activity Financial doesn't exist");
					throw new EvpException("Activity Financial doesn't exist");
				}
			}

			activityFinancial = CommonUtils.convertActivityFinancialDtoToEntity(
					createOrUpdateActivityDTO.getActivityFinancialDTO(), activityFinancial);

			LocalDateTime startDate = CommonUtils.getActivityDate(createOrUpdateActivityDTO.getStartDate());
			LocalDateTime endDate = CommonUtils.getActivityDate(createOrUpdateActivityDTO.getEndDate());

			if (startDate.isAfter(endDate)) {
				log.error("Start Date Cannot be greater than End Date");
				throw new EvpException("Start Date Cannot be greater than End Date");
			}

			existingActivity = CommonUtils.convertActivityDtoToEntity(createOrUpdateActivityDTO, existingActivity,
					themeId, modeId, tagId, startDate, endDate);

			activityTransaction.createOrUpdateActivityFinancial(activityFinancial, existingActivity);

			Activity activity = activityTransaction.createOrUpdateActivity(existingActivity);

			updateActivityLocations(locationIds, activity);

			try {
				if (createOrUpdateActivityDTO.getImages() != null) {
					Map<String, ImageDTO> imageMap = createOrUpdateActivityDTO.getImages().stream()
							.collect(Collectors.toMap(ImageDTO::getImageName, Function.identity()));
					List<ActivityPicture> activityPictures = activityPictureRepository
							.findByImageNamesAndActivityId(imageMap.keySet(), activity.getActivityId());

					activityPictures.forEach(activityPicture -> {
						ImageDTO imageDTO = imageMap.get(activityPicture.getImageName());

						activityPicture.setCoverPhoto(imageDTO.getCoverPhoto());
						activityPicture.setCaption(imageDTO.getCaption());

					});
					if (!activityPictures.isEmpty()) {
						activityPictureRepository.saveAll(activityPictures);
					}
				}
			} catch (Exception e) {
				throw new EvpException("Activity Pictures Couldn't be saved");
			}

			createOrUpdateActivityDTO.setActivityUUID(activity.getActivityUUID());

			return createOrUpdateActivityDTO;
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private void updateActivityLocations(List<Long> locationIds, Activity activity) {
		String activityId = activity.getActivityId();
		List<ActivityLocation> activityLocations = new ArrayList<>(locationIds.size());
		locationIds.forEach(location -> {
			ActivityLocation aLocation = new ActivityLocation();
			aLocation.setActivityId(activityId);
			aLocation.setLocationId(location);
			activityLocations.add(aLocation);

		});

		List<ActivityLocation> deleteActivityLocations = activityLocationRepository
				.findByActivityId(activity.getActivityId());

		if (deleteActivityLocations.size() > 0) {
			activityLocationRepository.deleteAll(deleteActivityLocations);
		}

		if (activityLocations.size() > 0) {
			activityLocationRepository.saveAll(activityLocations);
		}
	}

	@Override
	public void deleteActivity(String activityNameOrUUId) throws EvpException {
		Activity existingActivity = null;
		try {

			String[] deleteParameters = activityNameOrUUId.split("=");
			if (deleteParameters[0].equals("activityUUID")) {

				String activityUUID = deleteParameters[1];

				existingActivity = activityRepository.findByActivityUUID(activityUUID);

				if (!Optional.ofNullable(existingActivity).isPresent()) {
					log.error("Activity doesn't exist");
					throw new EvpException("Activity doesn't exist");
				}

			} else if (deleteParameters[0].equals("activityName")) {
				Optional<Activity> activityOpt = activityRepository.findByActivityName(deleteParameters[1]);
				if (activityOpt.isPresent()) {
					existingActivity = activityOpt.get();
				} else {
					log.error("Activity doesn't exist");
					throw new EvpException("Activity doesn't exist");
				}
			} else {
				log.error("Activity doesn't exist");
				throw new EvpException("Activity doesn't exist");
			}

			if (Optional.ofNullable(existingActivity).isPresent()) {
				String activityId = existingActivity.getActivityId();
				Set<String> activityIdSet = new HashSet<>();
				activityIdSet.add(activityId);

				Long activityFinacialId = existingActivity.getActivityFinancialId();

				Optional<ActivityFinancial> financialOpt = activityFinancialRepository.findById(activityFinacialId);

				activityTransaction.deleteActivityAndFinancialDetails(existingActivity, activityFinacialId,
						financialOpt);

				log.info("Deleting Activity Pictures for activity name {}", existingActivity.getActivityName());

				List<ActivityPicture> activityPictures = activityPictureRepository
						.findByAllActivityPictureId(activityId);
				activityPictureRepository.deleteAll(activityPictures);

				log.info("Deleting Activity Promotions for activity name {}", existingActivity.getActivityName());
				List<ActivityPromotion> activityPromotions = activityPromotionRepository.findByActivityId(activityId);
				activityPromotionRepository.deleteAll(activityPromotions);

				List<ActivityPromotionLocation> activityPromotionLocations = activityPromotionLocationRepostiroy
						.findByActivityId(activityId);
				activityPromotionLocationRepostiroy.deleteAll(activityPromotionLocations);

				log.info("Deleting Activity Feedbacks for activity name {}", existingActivity.getActivityName());
				List<ActivityFeedback> activityFeedBacks = activityFeedbackRepository
						.findByActivityNames(activityIdSet);
				activityFeedbackRepository.deleteAll(activityFeedBacks);
				;

				List<ActivityLocation> activityLocations = activityLocationRepository.findByActivityId(activityId);
				activityLocationRepository.deleteAll(activityLocations);

				log.info("Deleting Employee Activity History for activity name {}", existingActivity.getActivityName());
				List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
						.getByActivityName(activityIdSet);
				employeeActivityHistoryRepository.deleteAll(employeeActivityHistories);
				;

			}

		} catch (EvpException e) {
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
	public ActivityList getAllActitiesByCriteria(SearchCriteria searchCriteria, Integer pageNo, Integer pageSize,
			boolean getEnrolledEmployees, boolean financialDetails) throws EvpException {

		try {
			Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
			Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
			Map<String, Long> modeLovMap = evpLovService.getModeLovMap();
			Map<String, Long> tagLovMap = evpLovService.getTagLovMap();

			searchCriteria = CommonUtils.buildParamsForSearchCriteria(searchCriteria, locationLovMap, themeLovMap,
					modeLovMap, tagLovMap);

			validateUserLocation(searchCriteria, locationLovMap);

			Sort sort = Sort.by(Direction.DESC, "createdOn");
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);

			return buildActivityList(searchCriteria, locationLovMap, themeLovMap, modeLovMap, tagLovMap, pageable,
					getEnrolledEmployees, financialDetails);

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@SuppressWarnings("unchecked")
	private void validateUserLocation(SearchCriteria searchCriteria, Map<String, Long> locationLovMap)
			throws EvpException {

		log.info("Validating Users Location");
		String locationName = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		Optional<SimpleGrantedAuthority> auth = (Optional<SimpleGrantedAuthority>) authentication.getAuthorities()
				.stream().filter(authority -> authority.getAuthority().equalsIgnoreCase("ROLE_EMPLOYEE")
						|| authority.getAuthority().equalsIgnoreCase("ROLE_ADMIN"))
				.findFirst();

		if (auth.isPresent()) {
			String employeeId = (String) authentication.getPrincipal();

			if (Optional.ofNullable(employeeId).isPresent()) {
				Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);

				if (employeeOpt.isPresent()) {
					locationName = loginService.validateUserLocationAndGet(employeeOpt.get());
				} else {
					throw new EvpException("User doesn't exist");
				}

			}

			if (Optional.ofNullable(searchCriteria.getLocationId()).isPresent()
					&& searchCriteria.getLocationId().size() > 1) {
				throw new EvpException("Cannot search in multiple locations");
			} else if (Optional.ofNullable(searchCriteria.getLocationId()).isPresent()
					&& searchCriteria.getLocationId().size() == 1) {
				Long locationId = locationLovMap.get(locationName);
				if (Optional.ofNullable(locationId).isPresent()
						&& !locationId.equals(searchCriteria.getLocationId().stream().findFirst().get())) {
					log.error("Location Name doesn't match with default location");
					new EvpException("Location Name doesn't match with default location");
				} else if (!Optional.ofNullable(locationId).isPresent()) {
					log.error("Location Name doesn't match with default location");
					throw new EvpException("Location Name doesn't match with default location");
				}

			}
			log.info("Users location validated");
		}
	}

	private ActivityList buildActivityList(SearchCriteria searchCriteria, Map<String, Long> locationLovMap,
			Map<String, Long> themeLovMap, Map<String, Long> modeLovMap, Map<String, Long> tagLovMap, Pageable pageable,
			boolean getEnrolledEmployees, boolean financialDetails) throws EvpException {

		Map<Long, String> lovMapWithIdKey = CommonUtils.getLovMapWithIdKey(locationLovMap);
		Map<Long, String> lovMapWithIdKey2 = CommonUtils.getLovMapWithIdKey(themeLovMap);
		Map<Long, String> lovMapWithIdKey3 = CommonUtils.getLovMapWithIdKey(modeLovMap);
		Map<Long, String> lovMapWithIdKey4 = CommonUtils.getLovMapWithIdKey(tagLovMap);

		String username = CommonUtils.getUsername(request, jwtUtil);
		String employeeId = CommonUtils.getEmployeeIdFromUsername(username);

		if (!Optional.ofNullable(searchCriteria.getEmployeeId()).isPresent()
				&& !Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()) {

			List<Long> activityLocationIds = searchCriteria.getLocationId();

			Sort sort = Sort.by(Sort.Direction.ASC, "createdOn");
			Specification<Activity> activitySpecs = CommonSpecification.activitySpecification(searchCriteria);
			List<Activity> activities = activityRepository.findAll(activitySpecs, sort);

			activities = filterByLocation(activityLocationIds, activities);

			Map<String, List<Long>> activityLocationMap = getActivityLocationMap(activities);
			if (financialDetails) {
				filterLocationIdsForActivityFinancial(activityLocationIds, activityLocationMap);
			} else {
				filterLocationIds(activityLocationIds, activityLocationMap);
			}

			LocalDate currentDate = LocalDate.now();
			log.info("Retirivng Ongoing activies");
			List<Activity> onGoingActivites = activities.stream().filter(activity -> activity.isPublished())
					.filter(activity -> activity.getStartDate().toLocalDate().compareTo(currentDate) <= 0)
					.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) >= 0)
					.collect(Collectors.toList());

			onGoingActivites = sortActivityList(onGoingActivites);

			log.info("Retirivng Past activies");
			List<Activity> pastActivities = activities.stream().filter(activity -> activity.isPublished())
					.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
					.collect(Collectors.toList());

			pastActivities = sortActivityList(pastActivities);

			log.info("Retirivng UpComing activies");
			List<Activity> upcomingActivities = activities.stream().filter(activity -> activity.isPublished())
					.filter(activity -> activity.getStartDate().toLocalDate().compareTo(currentDate) > 0)
					.collect(Collectors.toList());

			upcomingActivities = sortActivityList(upcomingActivities);
			log.info("Retirivng Created activies");
			List<Activity> createdActivities = activities.stream().filter(activity -> !activity.isPublished())
					.filter(activity -> activity.getCreatedBy().equals(employeeId))
					.filter(activity -> activity.isCreatedActivity()).collect(Collectors.toList());

			createdActivities = sortActivityList(createdActivities);

			log.info("Building DTO for Ongoing activies");
			OngoingActivities ongoingActivities = (OngoingActivities) CommonUtils.getPaginationDetailsForActivity(
					pageable.getPageNumber() + 1, pageable.getPageSize(), onGoingActivites.size(), ActivityType.ONGOING,
					getEnrolledEmployees, onGoingActivites, lovMapWithIdKey, lovMapWithIdKey2, lovMapWithIdKey3,
					lovMapWithIdKey4, activityLocationMap);
			log.info("Building DTO for UpComing activies");
			UpcomingActivities upcomingActivitiess = (UpcomingActivities) CommonUtils.getPaginationDetailsForActivity(
					pageable.getPageNumber() + 1, pageable.getPageSize(), upcomingActivities.size(),
					ActivityType.UPCOMING, getEnrolledEmployees, upcomingActivities, lovMapWithIdKey, lovMapWithIdKey2,
					lovMapWithIdKey3, lovMapWithIdKey4, activityLocationMap);
			log.info("Building DTO for Created activies");
			PastActivities pastActivitiess = (PastActivities) CommonUtils.getPaginationDetailsForActivity(
					pageable.getPageNumber() + 1, pageable.getPageSize(), pastActivities.size(), ActivityType.PAST,
					getEnrolledEmployees, pastActivities, lovMapWithIdKey, lovMapWithIdKey2, lovMapWithIdKey3,
					lovMapWithIdKey4, activityLocationMap);
			log.info("Building DTO for Created activies");
			CreatedActivities createdActivitiess = (CreatedActivities) CommonUtils.getPaginationDetailsForActivity(
					pageable.getPageNumber() + 1, pageable.getPageSize(), createdActivities.size(),
					ActivityType.CREATED, getEnrolledEmployees, createdActivities, lovMapWithIdKey, lovMapWithIdKey2,
					lovMapWithIdKey3, lovMapWithIdKey4, activityLocationMap);

			if (getEnrolledEmployees) {
				getEnrolledOrParticipatedEmployees(ongoingActivities, upcomingActivitiess, pastActivitiess);
			}

			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			if (role.equals("ROLE_EMPLOYEE")) {

				getEmployeeParticipantStatus(ongoingActivities, upcomingActivitiess, pastActivitiess);

			}
			if (getEnrolledEmployees) {
				getAdminUploadImages(ongoingActivities, upcomingActivitiess, pastActivitiess, createdActivitiess);
			}

			return ActivityList.builder().ongoingActivities(ongoingActivities).pastActivities(pastActivitiess)
					.createdActivities(createdActivitiess).upcomingActivities(upcomingActivitiess).build();
		} else if (Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()) {
			CreateOrUpdateActivityDTO createOrUpdateActivityDTO = null;
			log.info("Retirivng activies for Id {}", searchCriteria.getActivityUUID());

			Activity activity = activityRepository.findByActivityUUID(searchCriteria.getActivityUUID());

			Map<String, List<Long>> activityLocationMap = getActivityLocationMap(Arrays.asList(activity));
			List<CreateOrUpdateActivityDTO> createOrUpdateActivityDTOs = CommonUtils.convertActivitiesListToDTO(
					lovMapWithIdKey, lovMapWithIdKey2, lovMapWithIdKey3, lovMapWithIdKey4, Arrays.asList(activity),
					activityLocationMap);
			Optional<CreateOrUpdateActivityDTO> activityOptional = createOrUpdateActivityDTOs.stream().findFirst();
			if (activityOptional.isPresent()) {
				createOrUpdateActivityDTO = activityOptional.get();
				log.info("Retirivng activityFinancial for id {}", activity.getActivityFinancialId());

				Optional<ActivityFinancial> activityFinancialOpt = activityFinancialRepository
						.findById(activity.getActivityFinancialId());

				if (activityFinancialOpt.isPresent()) {
					ActivityFinancialDTO activityFinancialDTO = CommonUtils
							.convertActivityFinancialToDTO(activityFinancialOpt.get(), null, null, null, null, null);
					createOrUpdateActivityDTO.setActivityFinancialDTO(activityFinancialDTO);
				}

				List<ActivityPicture> activityPictures = activityPictureRepository.findByActivityNameAndImageType(
						createOrUpdateActivityDTO.getActivityId(), ImageType.ADMIN_UPLOAD);

				Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);

				activityPictures = removeDuplicatePictures(activityPictures);

				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures,
						ImageType.ADMIN_UPLOAD.getImageType(), activityMap);

				createOrUpdateActivityDTO.setImages(images);

				String role = CommonUtils.getAssignedRole(request, jwtUtil);

				if (role.equals("ROLE_EMPLOYEE")) {
					log.info("Retriving Employee Participant Status");

					EmployeeActivityHistory employeeActivityHistory = employeeActivityHistoryRepository
							.findByEmployeeIdAndActivityUUID(employeeId, createOrUpdateActivityDTO.getActivityUUID());

					if (Optional.ofNullable(employeeActivityHistory).isPresent()
							&& Optional.ofNullable(employeeActivityHistory.getEmployeeActivityStatus()).isPresent()) {
						createOrUpdateActivityDTO.setEmployeeParticipationStatus(
								employeeActivityHistory.getEmployeeActivityStatus().getStatus());
					}

				}

			} else {
				log.error("Activity doesn't exist");
				throw new EvpException("Activity doesn't exist");
			}
			if (getEnrolledEmployees) {
				getEnrolledOrParticipatedEmployees(createOrUpdateActivityDTO);
			}
			return ActivityList.builder().activity(createOrUpdateActivityDTO).build();

		}

		else if (Optional.ofNullable(searchCriteria.getEmployeeId()).isPresent()) {

			log.info("Retrieving Employee specific Activities");

			Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
			Specification<Activity> activitySpecs = CommonSpecification.activitySpecification(searchCriteria);
			List<Activity> activitiesPage = activityRepository.findAll(activitySpecs, sort);

			sortActivityList(activitiesPage);

			List<Long> activityLocationIds = searchCriteria.getLocationId();

			activitiesPage = filterByLocation(activityLocationIds, activitiesPage);

			Map<String, List<Long>> activityLocationMap = getActivityLocationMap(activitiesPage);

			filterLocationIds(activityLocationIds, activityLocationMap);

			List<String> activityIds = new ArrayList<>(activityLocationMap.keySet());

			List<CreateOrUpdateActivityDTO> createOrUpdateActivityDTOs = CommonUtils.convertActivitiesListToDTO(
					lovMapWithIdKey, lovMapWithIdKey2, lovMapWithIdKey3, lovMapWithIdKey4, activitiesPage,
					activityLocationMap);

			createOrUpdateActivityDTOs = filterForEmployeeId(searchCriteria, createOrUpdateActivityDTOs);

			List<List<CreateOrUpdateActivityDTO>> activityDTOS = CommonUtils.batchesOfList(createOrUpdateActivityDTOs,
					pageable.getPageSize());

			EmployeeActivityHistoryResponseDTO activityHisotry = CommonUtils
					.getPaginationDetailsForEmployeeActivityHistory(pageable.getPageNumber() + 1,
							pageable.getPageSize(), createOrUpdateActivityDTOs.size());

			activityHisotry.setActivities(
					activityDTOS.isEmpty() ? Collections.emptyList() : activityDTOS.get(pageable.getPageNumber()));

			if (getEnrolledEmployees) {
				getEnrolledOrParticipatedEmployees(activityHisotry);
			}

			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			if (role.equals("ROLE_EMPLOYEE")) {
				log.info("Retriving Employee Participant Status");
				List<String> activityUUids = activityHisotry.getActivities().stream()
						.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());

				List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
						.findByEmployeeIdAndActivityUUids(employeeId, activityUUids);

				Map<String, EmployeeActivityStatus> employeeActivityHistoryMap = employeeActivityHistories.stream()
						.collect(Collectors.toMap(EmployeeActivityHistory::getActivityName,
								EmployeeActivityHistory::getEmployeeActivityStatus));

				activityHisotry.getActivities().forEach(activity -> {
					activity.setEmployeeParticipationStatus(
							employeeActivityHistoryMap.get(activity.getActivityId()).getStatus());
				});

			}

			Map<String, CreateOrUpdateActivityDTO> activityMap = activityHisotry.getActivities().stream()
					.collect(Collectors.toMap(CreateOrUpdateActivityDTO::getActivityId, Function.identity()));

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(activityIds);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));

			Map<String, String> activityIdMap = uploadService.getActivityMap(activityPictures);

			activityMap.entrySet().forEach(entry -> {
				String activityId = entry.getKey();

				List<ActivityPicture> activityPicture = activityWiseEmployees.get(activityId);
				List<ImageDTO> imageDto = CommonUtils.convertActivityPicturesToImageDTO(activityPicture, employeeId,
						activityIdMap);

				entry.getValue().setImages(imageDto);
			});

			return ActivityList.builder().employeeActivityHistoryResponse(activityHisotry).build();

		}
		return null;

	}

	private List<Activity> sortActivityList(List<Activity> activityList) {

		Comparator<Activity> comp = Comparator.comparing(Activity::getCreatedOn, Comparator.reverseOrder());
		return activityList.stream().sorted(comp).collect(Collectors.toList());
	}

	private List<ActivityFinancial> sortActivityFinancialList(List<ActivityFinancial> activityList) {

		Comparator<ActivityFinancial> comp = Comparator.comparing(ActivityFinancial::getCreatedOn,
				Comparator.reverseOrder());
		return activityList.stream().sorted(comp).collect(Collectors.toList());
	}

	private void filterLocationIds(List<Long> activityLocationIds, Map<String, List<Long>> activityLocationMap) {
		if (activityLocationIds.size() == 1) {
			Long locationId = activityLocationIds.get(0);
			activityLocationMap.entrySet().forEach(entry -> {
				Optional<Long> l1 = entry.getValue().stream().filter(l -> l != null).filter(l -> l.equals(locationId))
						.findFirst();
				if (l1.isPresent()) {
					activityLocationMap.put(entry.getKey(), Arrays.asList(locationId));
				}

			});
		}
	}

	private void filterLocationIdsForActivityFinancial(List<Long> activityLocationIds,
			Map<String, List<Long>> activityLocationMap) {
		if (activityLocationIds.size() == 1) {
			Long locationId = activityLocationIds.get(0);
			activityLocationMap.entrySet().forEach(entry -> {
				Optional<Long> l1 = entry.getValue().stream().filter(l -> l != null).filter(l -> l.equals(locationId))
						.findFirst();
				if (l1.isPresent()) {
					activityLocationMap.put(entry.getKey(), Arrays.asList(locationId));
				}

			});
		} else {
			activityLocationMap.entrySet().forEach(entry -> {
				List<Long> l1 = entry.getValue().stream().filter(l -> l != null)
						.filter(l -> activityLocationIds.contains(l)).collect(Collectors.toList());
				if (!l1.isEmpty()) {
					activityLocationMap.put(entry.getKey(), l1);
				}

			});
		}
	}

	public List<Activity> filterByLocation(List<Long> activityLocationIds, List<Activity> activities) {

		if (Optional.ofNullable(activityLocationIds).isPresent() && !activityLocationIds.isEmpty()) {

			List<ActivityLocation> activityLocations = activityLocationRepository
					.findByLocationIds(activityLocationIds);
			List<String> activityIds = activityLocations.stream().map(ActivityLocation::getActivityId)
					.collect(Collectors.toList());
			activities = activities.stream().filter(activity -> activityIds.contains(activity.getActivityId()))
					.collect(Collectors.toList());
		}

		return activities;
	}

	private void getEmployeeParticipantStatus(OngoingActivities ongoingActivities, UpcomingActivities upcomingActivitie,
			PastActivities pastActivities) {
		log.info("Retriving Employee Participant status");
		List<String> onGoingActivityIds = new ArrayList<>(1);
		if (Optional.ofNullable(ongoingActivities).isPresent()
				&& Optional.ofNullable(ongoingActivities.getOngoingActivities()).isPresent()) {
			onGoingActivityIds = ongoingActivities.getOngoingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());
		}
		List<String> pastActivityIds = new ArrayList<>(1);
		if (Optional.ofNullable(pastActivities).isPresent()
				&& Optional.ofNullable(pastActivities.getPastActivities()).isPresent()) {
			pastActivityIds = pastActivities.getPastActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());
		}
		List<String> upcomingActivityIds = new ArrayList<>(1);
		if (Optional.ofNullable(upcomingActivitie).isPresent()
				&& Optional.ofNullable(upcomingActivitie.getUpcomingActivities()).isPresent()) {
			upcomingActivityIds = upcomingActivitie.getUpcomingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());
		}

		onGoingActivityIds.addAll(upcomingActivityIds);
		onGoingActivityIds.addAll(pastActivityIds);

		String username = CommonUtils.getUsername(request, jwtUtil);
		String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
		List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
				.getActivitiesForEmployee(employeeId, onGoingActivityIds);

		getEmployeeParticipantStatusFromMap(ongoingActivities, upcomingActivitie, pastActivities,
				employeeActivityHistories);
	}

	private void getEmployeeParticipantStatusFromMap(OngoingActivities ongoingActivities,
			UpcomingActivities upcomingActivitie, PastActivities pastActivities,
			List<EmployeeActivityHistory> employeeActivityHistories) {

		try {
			Map<String, EmployeeActivityStatus> employeeActivityStatusMap = employeeActivityHistories.stream()
					.filter(e -> e.getActivityName() != null)
					.collect(Collectors.toMap(EmployeeActivityHistory::getActivityName,
							EmployeeActivityHistory::getEmployeeActivityStatus));
			if (Optional.ofNullable(ongoingActivities).isPresent()
					&& Optional.ofNullable(ongoingActivities.getOngoingActivities()).isPresent()) {
				ongoingActivities.getOngoingActivities().forEach(activity -> {
					EmployeeActivityStatus employeeActivityStatus = employeeActivityStatusMap
							.get(activity.getActivityId());
					if (Optional.ofNullable(employeeActivityStatus).isPresent()) {
						activity.setEmployeeParticipationStatus(employeeActivityStatus.getStatus());
					}
				});
			}

			if (Optional.ofNullable(pastActivities).isPresent()
					&& Optional.ofNullable(pastActivities.getPastActivities()).isPresent()) {
				pastActivities.getPastActivities().forEach(activity -> {
					EmployeeActivityStatus employeeActivityStatus = employeeActivityStatusMap
							.get(activity.getActivityId());
					if (Optional.ofNullable(employeeActivityStatus).isPresent()) {
						activity.setEmployeeParticipationStatus(employeeActivityStatus.getStatus());
					}
				});
			}
			if (Optional.ofNullable(upcomingActivitie).isPresent()
					&& Optional.ofNullable(upcomingActivitie.getUpcomingActivities()).isPresent()) {
				upcomingActivitie.getUpcomingActivities().forEach(activity -> {
					EmployeeActivityStatus employeeActivityStatus = employeeActivityStatusMap
							.get(activity.getActivityId());
					if (Optional.ofNullable(employeeActivityStatus).isPresent()) {
						activity.setEmployeeParticipationStatus(employeeActivityStatus.getStatus());
					}
				});
			}
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}

	}

	private List<CreateOrUpdateActivityDTO> filterForEmployeeId(SearchCriteria searchCriteria,
			List<CreateOrUpdateActivityDTO> activities) {
		List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
				.findByEmployeeId(searchCriteria.getEmployeeId());
		if (Optional.ofNullable(employeeActivityHistories).isPresent() && !employeeActivityHistories.isEmpty()) {
			List<String> activityUUids = employeeActivityHistories.stream()
					.filter(employeeActivityHistory -> !employeeActivityHistory.isRejectedByAdmin())
					.map(employeeActivityHistory -> employeeActivityHistory.getActivityUUID())
					.collect(Collectors.toList());
			return activities.stream().filter(activity -> activityUUids.contains(activity.getActivityUUID()))
					.collect(Collectors.toList());

		} else {
			return new ArrayList<>();
		}
	}

	private void getEnrolledOrParticipatedEmployees(
			EmployeeActivityHistoryResponseDTO employeeActivityHistoryResponse) {
		List<String> activityUUids = null;

		if (Optional.ofNullable(employeeActivityHistoryResponse.getActivities()).isPresent()) {
			activityUUids = employeeActivityHistoryResponse.getActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());

			List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
					.getActivities(activityUUids);

			Map<String, List<String>> activityWiseEmployees = employeeActivityHistories.stream()
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityUUID,
							Collectors.mapping(EmployeeActivityHistory::getEmployeeName, Collectors.toList())));

			employeeActivityHistoryResponse.getActivities().stream().forEach(activity -> {
				activity.setEnrolledEmployees(activityWiseEmployees.get(activity.getActivityUUID()));
			});

		}
	}

	private void getEnrolledOrParticipatedEmployees(CreateOrUpdateActivityDTO createOrUpdateActivityDTO) {
		List<String> activityUUids = Arrays.asList(createOrUpdateActivityDTO.getActivityUUID());
		List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
				.getActivities(activityUUids);
		List<String> enrolledEMployees = employeeActivityHistories.stream()
				.map(EmployeeActivityHistory::getEmployeeName).collect(Collectors.toList());

		createOrUpdateActivityDTO.setEnrolledEmployees(enrolledEMployees);

	}

	private void getEnrolledOrParticipatedEmployees(OngoingActivities ongoingActivities,
			UpcomingActivities upcomingActivitie, PastActivities pastActivities) {
		List<String> ongoingActivityUUIDS = null;
		List<String> upcomingActivityUUIDS = null;
		List<String> pastActivityUUIDS = null;

		if (Optional.ofNullable(ongoingActivities.getOngoingActivities()).isPresent()) {
			ongoingActivityUUIDS = ongoingActivities.getOngoingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());

			List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
					.getActivities(ongoingActivityUUIDS);

			Map<String, List<String>> activityWiseEmployees = employeeActivityHistories.stream()
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityUUID,
							Collectors.mapping(EmployeeActivityHistory::getEmployeeName, Collectors.toList())));

			ongoingActivities.getOngoingActivities().stream().forEach(activity -> {
				activity.setEnrolledEmployees(activityWiseEmployees.get(activity.getActivityUUID()));
			});

		}

		if (Optional.ofNullable(upcomingActivitie.getUpcomingActivities()).isPresent()) {
			upcomingActivityUUIDS = upcomingActivitie.getUpcomingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());

			List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
					.getActivities(upcomingActivityUUIDS);

			Map<String, List<String>> activityWiseEmployees = employeeActivityHistories.stream()
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityUUID,
							Collectors.mapping(EmployeeActivityHistory::getEmployeeName, Collectors.toList())));
			upcomingActivitie.getUpcomingActivities().stream().forEach(activity -> {
				activity.setEnrolledEmployees(activityWiseEmployees.get(activity.getActivityUUID()));
			});

		}

		if (Optional.ofNullable(pastActivities.getPastActivities()).isPresent()) {
			pastActivityUUIDS = pastActivities.getPastActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityUUID).collect(Collectors.toList());

			List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
					.getActivitiesWithStatusParticipated(pastActivityUUIDS);

			Map<String, List<String>> activityWiseEmployees = employeeActivityHistories.stream()
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityUUID,
							Collectors.mapping(EmployeeActivityHistory::getEmployeeName, Collectors.toList())));
			pastActivities.getPastActivities().stream().forEach(activity -> {
				activity.setEnrolledEmployees(activityWiseEmployees.get(activity.getActivityUUID()));
			});
		}

	}

	private void getAdminUploadImages(OngoingActivities ongoingActivities, UpcomingActivities upcomingActivitie,
			PastActivities pastActivities, CreatedActivities createdActivities) {
		List<String> ongoingActivityUUIDS = null;
		List<String> upcomingActivityUUIDS = null;
		List<String> pastActivityUUIDS = null;
		List<String> createdActivity = null;

		if (Optional.ofNullable(ongoingActivities.getOngoingActivities()).isPresent()) {
			ongoingActivityUUIDS = ongoingActivities.getOngoingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(ongoingActivityUUIDS);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);

			ongoingActivities.getOngoingActivities().stream().forEach(activity -> {
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(
						activityWiseEmployees.get(activity.getActivityId()), ImageType.ADMIN_UPLOAD.getImageType(),
						activityMap);
				activity.setImages(images);
			});

		}

		if (Optional.ofNullable(upcomingActivitie.getUpcomingActivities()).isPresent()) {
			upcomingActivityUUIDS = upcomingActivitie.getUpcomingActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(upcomingActivityUUIDS);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
			upcomingActivitie.getUpcomingActivities().stream().forEach(activity -> {
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(
						activityWiseEmployees.get(activity.getActivityId()), ImageType.ADMIN_UPLOAD.getImageType(),
						activityMap);
				activity.setImages(images);
			});

		}

		if (Optional.ofNullable(pastActivities.getPastActivities()).isPresent()) {
			pastActivityUUIDS = pastActivities.getPastActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(pastActivityUUIDS);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
			pastActivities.getPastActivities().stream().forEach(activity -> {
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(
						activityWiseEmployees.get(activity.getActivityId()), ImageType.ADMIN_UPLOAD.getImageType(),
						activityMap);
				activity.setImages(images);
			});

		}
		if (Optional.ofNullable(createdActivities.getCreatedActivities()).isPresent()) {
			createdActivity = createdActivities.getCreatedActivities().stream()
					.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(createdActivity);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
			createdActivities.getCreatedActivities().stream().forEach(activity -> {
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(
						activityWiseEmployees.get(activity.getActivityId()), ImageType.ADMIN_UPLOAD.getImageType(),
						activityMap);
				activity.setImages(images);
			});

		}

	}

	@Override
	public Map<String, List<ActivityTagResponse>> getActivityByTags(String location) throws EvpException {

		try {
			log.info("Started Finding Activities By Tag");
			Sort sort = Sort.by(Sort.Direction.ASC, "createdOn");

			Map<String, List<ActivityTagResponse>> activityMap = new LinkedHashMap<>();
			log.info("Retrieving Tag Lov Map");
			Map<String, Long> tagMap = evpLovService.getTagLovMap();

			log.info("Retrieving Tag Lov Map with Tag Name");
			Map<Long, String> tagIdMap = CommonUtils.getLovMapWithIdKey(tagMap);
			String[] locationArray = location.split(",");

			Stream.of(locationArray).forEach(l -> {
				List<Activity> activityList = null;
				if (Optional.ofNullable(l).isPresent()) {
					log.info("Location is " + l);
					Long locationId = evpLovService.getLocationLovMap().get(l);
					if (!Optional.ofNullable(locationId).isPresent()) {
						log.error("Location doesn't exist");
					}
					log.info("Finding Activities for location {}", l);

					List<ActivityLocation> activityLocations = activityLocationRepository
							.findByLocationIds(Arrays.asList(locationId));

					Set<String> activityIds = activityLocations.stream().map(ActivityLocation::getActivityId)
							.collect(Collectors.toSet());

					activityList = activityRepository.findByActivityIds(new ArrayList<>(activityIds), sort);

				}
				log.info("Retrieval is Completed");
				if (Optional.ofNullable(activityList).isPresent() && !activityList.isEmpty()) {
					log.info("Grouping By Activity Tag Id");
					Map<Long, List<Activity>> activityTagIdMap = activityList.stream().collect(Collectors.groupingBy(
							Activity::getTagId, Collectors.mapping(Function.identity(), Collectors.toList())));

					log.info("Mapping Tag Name to Activity Map");
					activityTagIdMap.entrySet().stream().forEach(entry -> {

						String tagName = tagIdMap.get(entry.getKey());

						List<ActivityTagResponse> activityIdMap = entry.getValue().stream().map(activity -> {
							return ActivityTagResponse.builder().activityId(activity.getActivityId())
									.activityName(activity.getActivityName()).build();

						}).collect(Collectors.toList());

						if (Optional.ofNullable(activityMap.get(tagName)).isPresent()) {
							activityMap.get(tagName).addAll(activityIdMap);
							Set<ActivityTagResponse> activityTagResponses = activityMap.get(tagName).stream()
									.collect(Collectors.toCollection(() -> new TreeSet<>(
											Comparator.comparing(ActivityTagResponse::getActivityId))));
							activityMap.put(tagName, new ArrayList<>(activityTagResponses));
						} else {
							activityMap.put(tagName, activityIdMap);
						}

					});
				} else {
					log.error("No Activity Present for location {}", l);
				}
			});

			log.info("Retrieval is Completed");
			return activityMap;

		}

		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public EmployeeActivityResponseDTO getParticipantDetailsForActivity(String activityUUID, Integer pageNo,
			Integer pageSize, boolean paginationRequired, String activityType) throws EvpException {
		try {
			Page<EmployeeActivityHistory> employeeActivityHistoriesPage = null;
			List<EmployeeActivityHistory> employeeActivityHistories = null;
			EmployeeActivityResponseDTO employeeActivityResponse = EmployeeActivityResponseDTO.builder().build();
			Activity activityOpt = activityRepository.findByActivityUUID(activityUUID);
			if (!Optional.ofNullable(activityOpt).isPresent()) {
				log.error("No Activity is present with given id " + activityUUID);
				throw new EvpException("No Activity is present with given id " + activityUUID);
			}
			log.info("Retriving Activity History for activity id {}", activityUUID);

			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

			List<EmployeeActivityStatus> statuses = new ArrayList<>();

			if (Optional.ofNullable(activityType).isPresent() && !activityType.isEmpty()) {
				ActivityType aType = ActivityType.valueOf(activityType);

				switch (aType) {
				case ONGOING:
					statuses.add(EmployeeActivityStatus.ENROLLED);
					statuses.add(EmployeeActivityStatus.PARTICIPATED);
					statuses.add(EmployeeActivityStatus.FEEDBACK);
					break;
				case PAST:
					statuses.add(EmployeeActivityStatus.PARTICIPATED);
					statuses.add(EmployeeActivityStatus.FEEDBACK);
					statuses.add(EmployeeActivityStatus.ENROLLED);
					break;

				case UPCOMING:
					statuses.add(EmployeeActivityStatus.ENROLLED);
					break;

				default:
					break;
				}
			}

			if (paginationRequired) {
				if (!statuses.isEmpty()) {
					employeeActivityHistoriesPage = employeeActivityHistoryRepository
							.getActivities(Arrays.asList(activityUUID), pageable, statuses);
				} else {
					employeeActivityHistoriesPage = employeeActivityHistoryRepository
							.getActivities(Arrays.asList(activityUUID), pageable);
					employeeActivityHistoriesPage = employeeActivityHistoryRepository
							.getActivities(Arrays.asList(activityUUID), pageable);
				}

				employeeActivityHistories = employeeActivityHistoriesPage.getContent();

			} else {
				if (!statuses.isEmpty()) {
					employeeActivityHistories = employeeActivityHistoryRepository
							.getActivityWithStatus(Arrays.asList(activityUUID), statuses);
				} else {
					employeeActivityHistories = employeeActivityHistoryRepository
							.getActivities(Arrays.asList(activityUUID));
				}

			}

			if (Optional.ofNullable(employeeActivityHistoriesPage.getContent()).isPresent()
					&& employeeActivityHistoriesPage.getContent().isEmpty()
					&& Optional.ofNullable(employeeActivityHistories).isPresent()
					&& employeeActivityHistories.isEmpty()) {
				log.error("No Activity history is present with given id " + activityUUID);
				return EmployeeActivityResponseDTO.builder().employeeActivityHistories(Collections.emptyList()).build();
			}

			List<String> activityIds = employeeActivityHistories.stream().map(EmployeeActivityHistory::getActivityName)
					.collect(Collectors.toList());

			List<Activity> activities = activityRepository.findByActivityIds(activityIds);
			Map<String, String> activityMap = activities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));

			return buildEmployeeActivityHistoryResponse(pageSize, employeeActivityResponse,
					employeeActivityHistoriesPage, employeeActivityHistories, paginationRequired, activityMap);
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private EmployeeActivityResponseDTO buildEmployeeActivityHistoryResponse(Integer pageSize,
			EmployeeActivityResponseDTO employeeActivityResponse,
			Page<EmployeeActivityHistory> employeeActivityHistoriesPage,
			List<EmployeeActivityHistory> employeeActivityHistoryList, boolean paginationRequired,
			Map<String, String> activityMap) {
		List<EmployeeActivityHistory> employeeActivityHistories = null;
		if (paginationRequired) {
			employeeActivityHistories = employeeActivityHistoriesPage.getContent();
			Integer totalPages = employeeActivityHistoriesPage.getTotalPages();
			Long totalElements = employeeActivityHistoriesPage.getTotalElements();

			Integer pageNumber = employeeActivityHistoriesPage.getPageable().getPageNumber();

			boolean hasPrevious = employeeActivityHistoriesPage.hasPrevious();

			boolean hasNext = employeeActivityHistoriesPage.hasNext();

			employeeActivityResponse.setHasNext(hasNext);
			employeeActivityResponse.setHasPrevious(hasPrevious);
			employeeActivityResponse.setPageNo(pageNumber + 1);
			employeeActivityResponse.setPageSize(pageSize);
			employeeActivityResponse.setTotalPages(totalPages);
			employeeActivityResponse.setTotalElements(totalElements.intValue());
		} else {
			employeeActivityHistories = employeeActivityHistoryList;
		}

		List<EmployeeActivityResponse> employeeActivityResponses = CommonUtils
				.convertEmployeeActivityHistoryToDTO(employeeActivityHistories, activityMap);

		employeeActivityResponse.setEmployeeActivityHistories(employeeActivityResponses);
		return employeeActivityResponse;
	}

	@Override
	public void approveEmployeeParticipation(List<EmployeeActivityHistory> employeeActivityHistories)
			throws EvpException {

		try {
			log.info("Validating Users Location");
			Optional<SimpleGrantedAuthority> auth = CommonUtils.getAuthRole();

			List<String> employeeIds = employeeActivityHistories.stream().map(EmployeeActivityHistory::getEmployeeId)
					.collect(Collectors.toList());

			List<String> activityUuids = employeeActivityHistories.stream()
					.map(EmployeeActivityHistory::getActivityUUID).collect(Collectors.toList());

			List<EmployeeActivityHistory> existingHistories = employeeActivityHistoryRepository
					.getActivitiesForEmployee(employeeIds, activityUuids);

			existingHistories.forEach(exist -> {
				employeeActivityHistories.forEach(e -> {
					if (e.getActivityUUID().equals(exist.getActivityUUID())
							&& e.getEmployeeId().equals(exist.getEmployeeId())) {
						exist.setApprovedByAdmin(e.isApprovedByAdmin());
						exist.setRejectedByAdmin(e.isRejectedByAdmin());
					}
				});
			});

			employeeActivityHistoryRepository.saveAll(existingHistories);

			sendRejectedAttendanceEmail(existingHistories);
			/*
			 * send email Rejected attendance by admin
			 *
			 */

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}

	@Override
	public EmployeeActivityResponseDTO getParticipantDetailsForActivityWithCriteria(SearchCriteria searchCriteria,
			Integer pageNo, Integer pageSize, boolean paginationRequired, String activityType) throws EvpException {
		EmployeeActivityResponseDTO employeeActivityResponse = EmployeeActivityResponseDTO.builder().build();
		Page<EmployeeActivityHistory> employeeActivityHistoriesPage = null;
		List<EmployeeActivityHistory> emplActivityHistories = null;
		try {

			searchCriteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);

			log.info("Retriving Activity History for activities");
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			List<EmployeeActivityStatus> statuses = new ArrayList<>();

			if (Optional.ofNullable(activityType).isPresent() && !activityType.isEmpty()) {
				ActivityType aType = ActivityType.valueOf(activityType);

				switch (aType) {
				case ONGOING:
					statuses.add(EmployeeActivityStatus.ENROLLED);
					statuses.add(EmployeeActivityStatus.PARTICIPATED);
					statuses.add(EmployeeActivityStatus.FEEDBACK);
					break;
				case PAST:
					statuses.add(EmployeeActivityStatus.PARTICIPATED);
					statuses.add(EmployeeActivityStatus.FEEDBACK);
					statuses.add(EmployeeActivityStatus.ENROLLED);
					break;

				case UPCOMING:
					statuses.add(EmployeeActivityStatus.ENROLLED);
					break;

				default:
					break;
				}
			}
			searchCriteria.setStatuses(statuses);
			Specification<EmployeeActivityHistory> employeeActivityHistorySpecification = CommonSpecification
					.allActivityParticipationSpecification(searchCriteria, true, false);
			if (paginationRequired) {
				employeeActivityHistoriesPage = employeeActivityHistoryRepository
						.findAll(employeeActivityHistorySpecification, pageable);

				emplActivityHistories = employeeActivityHistoriesPage.getContent();

			} else {
				emplActivityHistories = employeeActivityHistoryRepository.findAll(employeeActivityHistorySpecification);

			}

			if (Optional.ofNullable(employeeActivityHistoriesPage).isPresent()
					&& Optional.ofNullable(employeeActivityHistoriesPage.getContent()).isPresent()
					&& employeeActivityHistoriesPage.getContent().isEmpty()
					&& Optional.ofNullable(emplActivityHistories).isPresent() && emplActivityHistories.isEmpty()) {
				log.error("No Activity history is present  ");
				throw new EvpException("No Activity history is present ");
			}

			List<String> activityIds = emplActivityHistories.stream().map(EmployeeActivityHistory::getActivityName)
					.collect(Collectors.toList());

			List<Activity> activities = activityRepository.findByActivityIds(activityIds);
			Map<String, String> activityMap = activities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));

			return buildEmployeeActivityHistoryResponse(pageSize, employeeActivityResponse,
					employeeActivityHistoriesPage, emplActivityHistories, paginationRequired, activityMap);

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public EmployeeActivityResponseDTO getParticipantDetailsForActivityWithCriteriaForDashBoard(
			SearchCriteria searchCriteria, Integer pageNo, Integer pageSize, boolean paginationRequired)
			throws EvpException {
		List<String> activityUUids = new LinkedList<>();
		EmployeeActivityResponseDTO employeeActivityResponse = EmployeeActivityResponseDTO.builder().build();
		Page<EmployeeActivityHistory> employeeActivityHistoriesPage = null;
		List<EmployeeActivityHistory> emplActivityHistories = null;
		try {

			ActivityList activityList = getAllActitiesByCriteria(searchCriteria, pageNo, pageSize, false, false);

			if (Optional.ofNullable(activityList.getPastActivities()).isPresent()
					&& Optional.ofNullable(activityList.getPastActivities().getPastActivities()).isPresent()) {
				List<String> pastActivityIds = activityList.getPastActivities().getPastActivities().stream()
						.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

				activityUUids.addAll(pastActivityIds);

			}

			if (Optional.ofNullable(activityList.getOngoingActivities()).isPresent()
					&& Optional.ofNullable(activityList.getOngoingActivities().getOngoingActivities()).isPresent()) {
				List<String> ongoingActivityIds = activityList.getOngoingActivities().getOngoingActivities().stream()
						.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

				activityUUids.addAll(ongoingActivityIds);

			}

			if (Optional.ofNullable(activityList.getUpcomingActivities()).isPresent()
					&& Optional.ofNullable(activityList.getUpcomingActivities().getUpcomingActivities()).isPresent()) {
				List<String> upcomingActivityIds = activityList.getUpcomingActivities().getUpcomingActivities().stream()
						.map(CreateOrUpdateActivityDTO::getActivityId).collect(Collectors.toList());

				activityUUids.addAll(upcomingActivityIds);

			}

			searchCriteria.setActivityId(null);
			searchCriteria.setActivityIds(null);
			searchCriteria.setActivityNames(activityUUids);
			searchCriteria.setPastActivity(true);

			log.info("Retriving Activity History for activities");
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			List<EmployeeActivityStatus> statuses = new ArrayList<>();

			statuses.add(EmployeeActivityStatus.PARTICIPATED);
			statuses.add(EmployeeActivityStatus.FEEDBACK);

			searchCriteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);
			searchCriteria.setEmployeeIds(null);
			searchCriteria.setEmployeeId(null);
			searchCriteria.setStatuses(statuses);
			;

//			Modified
			Specification<EmployeeActivityHistory> employeeSpecification = CommonSpecification
					.allActivityParticipationSpecification(searchCriteria, true, true);

			log.info("Retriving Activity History for activities");

			if (paginationRequired) {
				employeeActivityHistoriesPage = employeeActivityHistoryRepository.findAll(employeeSpecification,
						pageable);

				emplActivityHistories = employeeActivityHistoriesPage.getContent();

			} else {
				emplActivityHistories = employeeActivityHistoryRepository.findAll(employeeSpecification);
			}

			if (Optional.ofNullable(employeeActivityHistoriesPage).isPresent()
					&& Optional.ofNullable(employeeActivityHistoriesPage.getContent()).isPresent()
					&& employeeActivityHistoriesPage.getContent().isEmpty()
					&& Optional.ofNullable(emplActivityHistories).isPresent() && emplActivityHistories.isEmpty()) {
				log.error("No Activity history is present  ");
				employeeActivityResponse.setEmployeeActivityHistories(Collections.emptyList());
				return employeeActivityResponse;
			}

			List<String> activityIds = emplActivityHistories.stream().map(EmployeeActivityHistory::getActivityName)
					.collect(Collectors.toList());

			List<Activity> activities = activityRepository.findByActivityIds(activityIds);
			Map<String, String> activityMap = activities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));

			return buildEmployeeActivityHistoryResponse(pageSize, employeeActivityResponse,
					employeeActivityHistoriesPage, emplActivityHistories, paginationRequired, activityMap);

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public EmployeeActivityResponseDTO getActivityParticipantsWithCriteria(SearchCriteria searchCriteria,
			Integer pageNo, Integer pageSize, boolean paginationRequired, String activityType, boolean dashBoardDetails)
			throws EvpException {
		EmployeeActivityResponseDTO employeeActivityHistories = null;
		try {

			if (!Optional.ofNullable(searchCriteria.getFieldValueToSearch()).isPresent()
					&& Optional.ofNullable(searchCriteria.getActivityUUID()).isPresent()
					&& searchCriteria.isEmptyWithoutUUID()) {
				employeeActivityHistories = getParticipantDetailsForActivity(searchCriteria.getActivityUUID(), pageNo,
						pageSize, paginationRequired, activityType);
			} else if (Optional.ofNullable(searchCriteria.getFieldValueToSearch()).isPresent()) {

				String fieldValueToSearch = searchCriteria.getFieldValueToSearch();
				searchCriteria.setFieldValueToSearch(null);
				;
				if (dashBoardDetails || Optional.ofNullable(activityType).isPresent()) {

					Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
					Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
					Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
					Map<String, Long> modeLovMap = evpLovService.getModeLovMap();

					searchCriteria = CommonUtils.buildParamsForSearchCriteria(searchCriteria, locationLovMap,
							themeLovMap, modeLovMap, tagLovMap);

					LocalDate currentDate = LocalDate.now();

					Specification<Activity> activitySpecs = CommonSpecification.activitySpecification(searchCriteria);

					List<Activity> activities = activityRepository.findAll(activitySpecs);

					activities = filterByLocation(searchCriteria.getLocationId(), activities);

					/*
					 * List<Activity> pastActivities=null; if(!dashBoardDetails) { pastActivities =
					 * activities.stream().filter(activity -> activity.isPublished())
					 * .filter(activity ->
					 * activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
					 * .collect(Collectors.toList()); }else { pastActivities=activities; }
					 */

					if (Optional.ofNullable(activities).isPresent()) {
						List<String> pastActivityIds = activities.stream().map(Activity::getActivityId)
								.collect(Collectors.toList());
						searchCriteria.setActivityId(null);
						searchCriteria.setActivityIds(null);
						searchCriteria.setActivityNames(pastActivityIds);
//						searchCriteria.setPastActivity(true);

					}
				}

				searchCriteria.setFieldValueToSearch(fieldValueToSearch);
				searchCriteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);
				searchCriteria.setEmployeeIds(null);
				searchCriteria.setEmployeeId(null);
				EmployeeActivityResponseDTO employeeActivityResponse = EmployeeActivityResponseDTO.builder().build();
				Page<EmployeeActivityHistory> employeeActivityHistoriesPage = null;
				List<EmployeeActivityHistory> emplActivityHistories = null;

				boolean excludeRejectedByAdmins = false;

				if (Optional.ofNullable(activityType).isPresent() && !activityType.isEmpty()) {
					excludeRejectedByAdmins = false;
				} else {
					excludeRejectedByAdmins = true;
				}

				Specification<EmployeeActivityHistory> employeeSpecification = CommonSpecification
						.allActivityParticipationSpecification(searchCriteria, excludeRejectedByAdmins,
								dashBoardDetails);

				log.info("Retriving Activity History for activities");
				Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
				if (paginationRequired) {
					employeeActivityHistoriesPage = employeeActivityHistoryRepository.findAll(employeeSpecification,
							pageable);

					emplActivityHistories = employeeActivityHistoriesPage.getContent();

				} else {
					emplActivityHistories = employeeActivityHistoryRepository.findAll(employeeSpecification);
				}

				if (Optional.ofNullable(employeeActivityHistoriesPage).isPresent()
						&& Optional.ofNullable(employeeActivityHistoriesPage.getContent()).isPresent()
						&& employeeActivityHistoriesPage.getContent().isEmpty()
						&& Optional.ofNullable(emplActivityHistories).isPresent() && emplActivityHistories.isEmpty()) {
					log.error("No Activity history is present  ");
					throw new EvpException("No Activity history is present ");
				}

				List<String> activityIds = emplActivityHistories.stream().map(EmployeeActivityHistory::getActivityName)
						.collect(Collectors.toList());

				List<Activity> activities = activityRepository.findByActivityIds(activityIds);
				Map<String, String> activityMap = activities.stream()
						.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));

				return buildEmployeeActivityHistoryResponse(pageSize, employeeActivityResponse,
						employeeActivityHistoriesPage, emplActivityHistories, paginationRequired, activityMap);
			}

			else {
				employeeActivityHistories = getParticipantDetailsForActivityWithCriteria(searchCriteria, pageNo,
						pageSize, paginationRequired, activityType);
			}
			return employeeActivityHistories;
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public ActivityFinancialResponseDTO getActivityFinancialsWithCriteria(SearchCriteria searchCriteria, Integer pageNo,
			Integer pageSize, boolean paginationRequired, String activityType) throws EvpException {
		List<Long> activityFinancialIds = new LinkedList<>();
		Map<Long, CreateOrUpdateActivityDTO> activityMap = new LinkedHashMap<>();
		ActivityFinancialResponseDTO activityFinancialResponseDTO = ActivityFinancialResponseDTO.builder().build();
		Page<ActivityFinancial> activityFinancialPage = null;
		List<ActivityFinancial> activityFinancials = null;
		try {
			ActivityList activityList = getAllActitiesByCriteria(searchCriteria, pageNo, pageSize, false, true);
			ActivityType aType = null;
			if (Optional.ofNullable(activityType).isPresent()) {
				aType = ActivityType.valueOf(activityType);
			}
			if (Optional.ofNullable(aType).isPresent() && aType.equals(ActivityType.PAST)) {
				if (Optional.ofNullable(activityList.getPastActivities()).isPresent()
						&& Optional.ofNullable(activityList.getPastActivities().getPastActivities()).isPresent()) {
					List<Long> pastActivityFinancialIds = activityList.getPastActivities().getPastActivities().stream()
							.map(CreateOrUpdateActivityDTO::getActivityFinancialId).collect(Collectors.toList());

					activityFinancialIds.addAll(pastActivityFinancialIds);

					activityMap.putAll(activityList.getPastActivities().getPastActivities().stream().collect(
							Collectors.toMap(CreateOrUpdateActivityDTO::getActivityFinancialId, Function.identity())));
				}
			} else {
				if (Optional.ofNullable(activityList.getOngoingActivities()).isPresent() && Optional
						.ofNullable(activityList.getOngoingActivities().getOngoingActivities()).isPresent()) {
					List<Long> ongoingActivityFinancialIds = activityList.getOngoingActivities().getOngoingActivities()
							.stream().map(CreateOrUpdateActivityDTO::getActivityFinancialId)
							.collect(Collectors.toList());

					activityFinancialIds.addAll(ongoingActivityFinancialIds);

					activityMap.putAll(activityList.getOngoingActivities().getOngoingActivities().stream().collect(
							Collectors.toMap(CreateOrUpdateActivityDTO::getActivityFinancialId, Function.identity())));

				}

				if (Optional.ofNullable(activityList.getPastActivities()).isPresent()
						&& Optional.ofNullable(activityList.getPastActivities().getPastActivities()).isPresent()) {
					List<Long> pastActivityFinancialIds = activityList.getPastActivities().getPastActivities().stream()
							.map(CreateOrUpdateActivityDTO::getActivityFinancialId).collect(Collectors.toList());

					activityFinancialIds.addAll(pastActivityFinancialIds);

					activityMap.putAll(activityList.getPastActivities().getPastActivities().stream().collect(
							Collectors.toMap(CreateOrUpdateActivityDTO::getActivityFinancialId, Function.identity())));
				}

				if (Optional.ofNullable(activityList.getUpcomingActivities()).isPresent() && Optional
						.ofNullable(activityList.getUpcomingActivities().getUpcomingActivities()).isPresent()) {
					List<Long> ongoingActivityFinancialIds = activityList.getUpcomingActivities()
							.getUpcomingActivities().stream().map(CreateOrUpdateActivityDTO::getActivityFinancialId)
							.collect(Collectors.toList());

					activityFinancialIds.addAll(ongoingActivityFinancialIds);

					activityMap.putAll(activityList.getUpcomingActivities().getUpcomingActivities().stream().collect(
							Collectors.toMap(CreateOrUpdateActivityDTO::getActivityFinancialId, Function.identity())));

				}

				if (Optional.ofNullable(activityList.getCreatedActivities()).isPresent() && Optional
						.ofNullable(activityList.getCreatedActivities().getCreatedActivities()).isPresent()) {
					List<Long> pastActivityFinancialIds = activityList.getCreatedActivities().getCreatedActivities()
							.stream().map(CreateOrUpdateActivityDTO::getActivityFinancialId)
							.collect(Collectors.toList());

					activityFinancialIds.addAll(pastActivityFinancialIds);

					activityMap.putAll(activityList.getCreatedActivities().getCreatedActivities().stream().collect(
							Collectors.toMap(CreateOrUpdateActivityDTO::getActivityFinancialId, Function.identity())));

				}
			}

			log.info("Retriving Activity History for activities");
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

			List<String> activityFinanceIds = activityFinancialIds.stream().map(String::valueOf)
					.collect(Collectors.toList());

			if (paginationRequired) {

				activityFinancialPage = activityFinancialRepository.getAllByIdsSortedByCreatedDate(activityFinanceIds,
						pageable);

				activityFinancials = activityFinancialPage.getContent();

			} else {
				activityFinancials = activityFinancialRepository.getAllByIdsSortedByCreatedDate(activityFinanceIds);
			}

			if (Optional.ofNullable(activityFinancialPage).isPresent()
					&& Optional.ofNullable(activityFinancialPage.getContent()).isPresent()
					&& activityFinancialPage.getContent().isEmpty()
					&& Optional.ofNullable(activityFinancials).isPresent() && activityFinancials.isEmpty()) {
				log.error("No Activity financial is present  ");
				activityFinancialResponseDTO.setActivityFinancials(Collections.emptyList());
				return activityFinancialResponseDTO;
			}

			activityFinancials = sortActivityFinancialList(activityFinancials);

			return buildActivityFinancialResponse(pageSize, paginationRequired, activityMap,
					activityFinancialResponseDTO, activityFinancialPage, activityFinancials);

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private ActivityFinancialResponseDTO buildActivityFinancialResponse(Integer pageSize, boolean paginationRequired,
			Map<Long, CreateOrUpdateActivityDTO> activityMap, ActivityFinancialResponseDTO activityFinancialResponseDTO,
			Page<ActivityFinancial> activityFinancialPage, List<ActivityFinancial> activityFinancials) {
		List<ActivityFinancial> financials = activityFinancials;
		if (paginationRequired) {

			Integer totalPages = activityFinancialPage.getTotalPages();
			Long totalElements = activityFinancialPage.getTotalElements();

			Integer pageNumber = activityFinancialPage.getPageable().getPageNumber();

			boolean hasPrevious = activityFinancialPage.hasPrevious();

			boolean hasNext = activityFinancialPage.hasNext();

			activityFinancialResponseDTO.setHasNext(hasNext);
			activityFinancialResponseDTO.setHasPrevious(hasPrevious);
			activityFinancialResponseDTO.setPageNo(pageNumber + 1);
			activityFinancialResponseDTO.setPageSize(pageSize);
			activityFinancialResponseDTO.setTotalPages(totalPages);
			activityFinancialResponseDTO.setTotalElements(totalElements.intValue());
		}

		List<ActivityFinancialDTO> activityFinancialDTOs = CommonUtils.convertActivityFinancialToDTO(financials,
				activityMap);
		activityFinancialResponseDTO.setActivityFinancials(activityFinancialDTOs);
		return activityFinancialResponseDTO;
	}

	@Override
	public List<Activity> getAllActivitiesByCriteria(SearchCriteria searchCriteria) throws EvpException {
		List<Activity> activities = null;
		try {
			Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
			Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
			Map<String, Long> modeLovMap = evpLovService.getModeLovMap();
			Map<String, Long> tagLovMap = evpLovService.getTagLovMap();

			searchCriteria = CommonUtils.buildParamsForSearchCriteria(searchCriteria, locationLovMap, themeLovMap,
					modeLovMap, tagLovMap);
			Specification<Activity> activitySpecification = CommonSpecification
					.allActivitySpecification(searchCriteria);
			activities = activityRepository.findAll(activitySpecification);

			activities = filterByLocation(searchCriteria.getLocationId(), activities);

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		return activities;
	}

	@Override
	public CreateOrUpdateActivityDTO getActivityDetails(SearchCriteria searchCriteria) throws EvpException {
		try {
			String imageType = null;
			List<ActivityPicture> activityPictures = null;
			Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
			Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
			Map<String, Long> modeLovMap = evpLovService.getModeLovMap();
			Map<String, Long> tagLovMap = evpLovService.getTagLovMap();

			Map<Long, String> lovMapWithIdKey = CommonUtils.getLovMapWithIdKey(locationLovMap);
			Map<Long, String> lovMapWithIdKey2 = CommonUtils.getLovMapWithIdKey(themeLovMap);
			Map<Long, String> lovMapWithIdKey3 = CommonUtils.getLovMapWithIdKey(modeLovMap);
			Map<Long, String> lovMapWithIdKey4 = CommonUtils.getLovMapWithIdKey(tagLovMap);

			String role = CommonUtils.getAssignedRole(request, jwtUtil);
			searchCriteria = CommonUtils.buildParamsForSearchCriteria(searchCriteria, locationLovMap, themeLovMap,
					modeLovMap, tagLovMap);

			log.info("Retriving Activity Details with activity name {}", searchCriteria.getActivityId());

			searchCriteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);

			Specification<Activity> activitySpecs = CommonSpecification.allActivitySpecification(searchCriteria);

			List<Activity> activityList = activityRepository.findAll(activitySpecs);

			activityList = filterByLocation(searchCriteria.getLocationId(), activityList);

			Optional<Activity> activityOpt = activityList.stream().findFirst();

			if (activityOpt.isPresent()) {
				List<ActivityLocation> activityLocations = null;
				if (Optional.ofNullable(searchCriteria.getLocationId()).isPresent()
						&& searchCriteria.getLocationId().size() == 1) {
					activityLocations = activityLocationRepository.findByActivityIdsAndLocationIds(
							activityOpt.get().getActivityId(), searchCriteria.getLocationId().get(0));
				} else {
					activityLocations = activityLocationRepository.findByActivityId(activityOpt.get().getActivityId());
				}

				String locationName = activityLocations.stream()
						.map(activityLocation -> lovMapWithIdKey.get(activityLocation.getLocationId()))
						.collect(Collectors.joining(","));

				String themeName = lovMapWithIdKey2.get(activityOpt.get().getThemeNameId());
				String tagName = lovMapWithIdKey4.get(activityOpt.get().getTagId());
				String mode = lovMapWithIdKey3.get(activityOpt.get().getModeOfParticipationId());

				CreateOrUpdateActivityDTO createOrUpdateActivityDTO = CommonUtils
						.convertActivityToDTO(activityOpt.get(), themeName, locationName, mode, tagName);

				getEnrolledOrParticipatedEmployees(createOrUpdateActivityDTO);
				Optional<ActivityFinancial> actiOptional = activityFinancialRepository
						.findById(activityOpt.get().getActivityFinancialId());

				if (actiOptional.isPresent()) {
					ActivityFinancialDTO activityFinancialDTO = CommonUtils.convertActivityFinancialToDTO(
							actiOptional.get(), createOrUpdateActivityDTO.getActivityId(),
							createOrUpdateActivityDTO.getActivityName(),
							createOrUpdateActivityDTO.getActivityLocation(), createOrUpdateActivityDTO.getEndDate(),
							createOrUpdateActivityDTO.getActivityUUID());
					createOrUpdateActivityDTO.setActivityFinancialDTO(activityFinancialDTO);

				}

				getAverageRating(createOrUpdateActivityDTO);

				if (role.equals("ROLE_ADMIN") || role.equals("ROLE_CADMIN")) {
					imageType = ImageType.ADMIN_UPLOAD.getImageType();
					activityPictures = activityPictureRepository
							.findByActivityNameAdminUpload(Arrays.asList(createOrUpdateActivityDTO.getActivityId()));

					activityPictures = removeDuplicatePictures(activityPictures);

				} else if (role.equals("ROLE_EMPLOYEE")) {
					imageType = ImageType.ADMIN_UPLOAD.getImageType();
					String username = CommonUtils.getUsername(request, jwtUtil);
					String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
					activityPictures = activityPictureRepository
							.findByActivityNameAdminUpload(Arrays.asList(createOrUpdateActivityDTO.getActivityId()));

					activityPictures = removeDuplicatePictures(activityPictures);

					EmployeeActivityHistory employeeActivityHistoryOpt = employeeActivityHistoryRepository
							.findByEmployeeIdAndActivityUUID(employeeId, activityOpt.get().getActivityUUID());

					if (Optional.ofNullable(employeeActivityHistoryOpt).isPresent() && Optional
							.ofNullable(employeeActivityHistoryOpt.getEmployeeActivityStatus()).isPresent()) {
						createOrUpdateActivityDTO.setEmployeeParticipationStatus(
								employeeActivityHistoryOpt.getEmployeeActivityStatus().getStatus());
					}

				}
				Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures, imageType,
						activityMap);

				createOrUpdateActivityDTO.setImages(images);

				List<ActivityPicture> pastVideos = activityPictureRepository.findByActivityNameAndImageType(
						createOrUpdateActivityDTO.getActivityId(), ImageType.PAST_VIDEOS);
				Optional<ActivityPicture> videoOpt = pastVideos.stream().findFirst();

				if (videoOpt.isPresent()) {
					createOrUpdateActivityDTO.setPastVideoUrl(videoOpt.get().getActivityPictureLocation());

				}

				return createOrUpdateActivityDTO;

			} else {
				return CreateOrUpdateActivityDTO.builder().build();
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");

		}

	}

	private List<ActivityPicture> removeDuplicatePictures(List<ActivityPicture> activityPictures) {
		List<ActivityPicture> uniqueActivityPicture = new ArrayList<>();

		activityPictures.forEach(activityPicture -> {
			if (uniqueActivityPicture.isEmpty()) {
				uniqueActivityPicture.add(activityPicture);
			} else {
				Optional<ActivityPicture> opt = uniqueActivityPicture.stream()
						.filter(aPicture -> activityPicture.getImageName().equals(aPicture.getImageName())
								&& activityPicture.getActivityPictureId().equals(aPicture.getActivityPictureId())
								&& activityPicture.getImageType().equals(aPicture.getImageType()))
						.findFirst();
				if (!opt.isPresent()) {
					uniqueActivityPicture.add(activityPicture);
				}
			}
		});
		return uniqueActivityPicture;
	}

	private void getAverageRating(CreateOrUpdateActivityDTO createOrUpdateActivityDTO) {

		String activityId = createOrUpdateActivityDTO.getActivityId();
		Set<String> activityIds = new HashSet<>();
		activityIds.add(activityId);

		List<ActivityFeedback> activityFeedBacks = activityFeedbackRepository.findByActivityNames(activityIds);

		if (activityFeedBacks != null && !activityFeedBacks.isEmpty()) {
			Double noOfFeedbacks = Double.valueOf(activityFeedBacks.size());
			Double totalRating = activityFeedBacks.stream().map(ActivityFeedback::getRating)
					.collect(Collectors.summingDouble(p -> p));
			Double averageRating = (totalRating / noOfFeedbacks);
			createOrUpdateActivityDTO.setAverageRating(averageRating.doubleValue());
		}

	}

	private void sendActivityCreatedEmail(CreateOrUpdateActivityDTO activity, ActivityFinancialDTO activityFinancial,
			String string, Optional<Employee> employeeOpt, String centralAdminEmailId) {

		log.info("sendActivityCreatedEmail");
		String createdDate = CommonUtils.formatLocalDateTimeWithTime(LocalDateTime.now());

		if (employeeOpt.isPresent()) {
			Employee e = employeeOpt.get();
			if (Optional.ofNullable(e.getEmail()).isPresent()) {

				EmailTemplateData emailTemplateData = EmailTemplateData.builder()
						.activityFinancials(activity.getActivityFinancialDTO()).activityName(activity.getActivityName())
						.location(activity.getActivityLocation()).createdDate(createdDate)
						.createdBy(e.getEmployeeName()).employeeName(e.getEmployeeName())
						.activityLink(activity.getActivityUrl()).build();

				emailService.sendEmail(EmailType.PUBLISH_ACTIVITY, emailTemplateData, e.getEmail(), null);

				if (centralAdminEmailId != null) {
					emailService.sendEmail(EmailType.PUBLISH_ACTIVITY, emailTemplateData, centralAdminEmailId, null);
				}

			} else {
				log.error("email not existing for login user ");
			}

		} else {
			log.error("User is not valid");
		}

	}

	private void sendRejectedAttendanceEmail(List<EmployeeActivityHistory> employeeActivityHistories) {

		List<EmployeeActivityHistory> eActivityHistories = employeeActivityHistories.stream()
				.filter(EmployeeActivityHistory::isRejectedByAdmin).collect(Collectors.toList());

		List<String> employeeIds = eActivityHistories.stream().map(EmployeeActivityHistory::getEmployeeId)
				.collect(Collectors.toList());
		List<Employee> employees = employeeRepository.findByEmployeeIds(employeeIds);

		Map<String, Employee> emplMap = employees.stream()
				.collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));

		List<String> activityIds = eActivityHistories.stream().map(EmployeeActivityHistory::getActivityName)
				.collect(Collectors.toList());
		List<Activity> activities = activityRepository.findByActivityIds(activityIds);
		Map<String, Activity> activityMap = activities.stream()
				.collect(Collectors.toMap(Activity::getActivityId, Function.identity()));

		for (EmployeeActivityHistory eah : eActivityHistories) {

			if (eah.isRejectedByAdmin()) {

				Employee employee = emplMap.get(eah.getEmployeeId());

				Activity activity = activityMap.get(eah.getActivityName());

				log.info("ActivityUUID : " + eah.getActivityUUID());

				if (Optional.ofNullable(employee).isPresent() && Optional.ofNullable(activity).isPresent()) {
					if (Optional.ofNullable(employee.getEmail()).isPresent()) {

						EmailTemplateData emailTemplateData = EmailTemplateData.builder()
								.employeeName(employee.getEmployeeName()).activityName(activity.getActivityName())
								.build();

						emailService.sendEmail(EmailType.REJECTED_ATTENDANCE, emailTemplateData, employee.getEmail(),
								null);

					} else {
						log.error("email not existing for login user ");
					}

				} else {
					log.error("User is not valid / activity is not exist");
				}

			}

		}
	}

	private void sendNeedSupportFromCCSRTeamEmail(CreateOrUpdateActivityDTO createOrUpdateActivityDTO,
			String centralCsrEmailId, Optional<Employee> employeeOpt) {

		if (employeeOpt.isPresent()) {
			Employee e = employeeOpt.get();
			EmailTemplateData emailTemplateData = EmailTemplateData.builder().createdBy(e.getEmployeeName())
					.activityName(createOrUpdateActivityDTO.getActivityName())
					.requestFromCCSR(createOrUpdateActivityDTO.getRequestFromCCSR())
					.location(createOrUpdateActivityDTO.getActivityLocation()).createdBy(e.getEmployeeName()).build();
			if (Optional.ofNullable(centralCsrEmailId).isPresent()) {

				emailService.sendEmail(EmailType.NEED_SUPPORT_CCSR, emailTemplateData, centralCsrEmailId, null);
			}
		}

	}

	@Override
	public ActivityPromotionDTO getActivityDetailsForPromotion(String promotionId) throws EvpException {
		ActivityPromotionDTO activityPromotionDTO = null;
		try {

			Optional<ActivityPromotion> activityPromotionOpt = activityPromotionRepository
					.findById(Long.valueOf(promotionId));

			if (activityPromotionOpt.isPresent()) {
				ActivityPromotion activityPromotion = activityPromotionOpt.get();

				List<ActivityPromotionLocation> activityPromotionLocations = activityPromotionLocationRepostiroy
						.findByPromotionIds(Arrays.asList(activityPromotion.getId()));

				Map<Long, List<String>> activityPromotionLocationMap = activityPromotionLocations.stream()
						.collect(Collectors.groupingBy(ActivityPromotionLocation::getPromotionId,
								Collectors.mapping(ActivityPromotionLocation::getLocation, Collectors.toList())));

				List<String> locations = activityPromotionLocationMap.get(activityPromotion.getId());
				if (!locations.isEmpty()) {
					String l = locations.stream().collect(Collectors.joining(","));

					activityPromotion.setPromotionlocation(l);
				}

				List<ActivityPicture> activityPictures = activityPictureRepository
						.findByPromotionIds(Arrays.asList(activityPromotion.getId()));
				Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);

				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures,
						ImageType.PROMOTIONS.getImageType(), activityMap);

				activityPromotionDTO = ActivityPromotionDTO.builder().promotionId(activityPromotion.getId().toString())
						.activityEndDate(
								CommonUtils.formatLocalDateTimeWithTime(activityPromotion.getActivityEndDate()))
						.activityStartDate(
								CommonUtils.formatLocalDateTimeWithTime(activityPromotion.getActivityStartDate()))
						.promotionTheme(activityPromotion.getPromotionTheme())
						.activityId(activityPromotion.getActivityId())
						.promotionActivity(activityPromotion.getPromotionActivity())
						.promotionlocation(activityPromotion.getPromotionlocation())
						.startDate(CommonUtils.formatLocalDateTimeWithTime(activityPromotion.getStartDate()))
						.endDate(CommonUtils.formatLocalDateTimeWithTime(activityPromotion.getEndDate()))
						.activityId(activityPromotion.getActivityId()).images(images).build();

			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		return activityPromotionDTO;

	}

	public Map<String, List<Long>> getActivityLocationMap(List<Activity> activities) {
		Map<String, List<Long>> activityLocationMap = new HashMap<>();
		if (activities != null && !activities.isEmpty()) {
			List<String> activityIds = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());

			List<ActivityLocation> activityLocations = activityLocationRepository.findByActivityIds(activityIds);

			activityLocationMap = activityLocations.stream()
					.collect(Collectors.groupingBy(ActivityLocation::getActivityId,
							Collectors.mapping(ActivityLocation::getLocationId, Collectors.toList())));

		}
		return activityLocationMap;
	}

	@Override
	public String downloadCertificate(String participantName, String activityName, String employeeId) throws Exception {
		String path = null;
		try {

			String filePath = certificatePath + employeeId;
			log.info("Configured Path {}", certificatePath);
			log.info("Configured Path {}", filePath);

			Path systemPath = Paths.get(filePath);
			if (!Files.exists(systemPath)) {
				log.info("Directory Doesn't exist");
				File f = new File(filePath);
				log.info("Directory Creating");
				f.mkdir();
			}

			path = filePath + "/" + activityName.concat(".pdf");
			log.info("Loading PDF template");

			StringBuilder text = new StringBuilder();
			InputStream ioStream = this.getClass().getClassLoader().getResourceAsStream("index.html");

			try (InputStreamReader isr = new InputStreamReader(ioStream, "UTF-8");
					BufferedReader br = new BufferedReader(isr);) {
				String line;
				while ((line = br.readLine()) != null) {
					text.append(line);
				}
				ioStream.close();
			}
			log.info("Reading Mail Configs");
			List<MailConfig> mailConfigs = mailConfigRepository.findAll();
			Map<String, String> mailMap = mailConfigs.stream()
					.collect(Collectors.toMap(MailConfig::getConfigType, MailConfig::getConfigValue));
			String name = mailMap.get("NAME");

			log.info("Name {}", name);
			String designation = mailMap.get("DESIGNATION");
			log.info("Designation {}", designation);
			String imageUri = mailMap.get("SIGNATURE_IMAGE");
			log.info("Image URI {}", imageUri);
			URL u = new URL(imageUri);

			byte[] imageData = downloadUrl(u);
			String encodedString = Base64.getEncoder().encodeToString(imageData);
			log.info("Encoded String {}", encodedString);

			String htmlText = text.toString();
			htmlText = htmlText.replace("CERTIFICATE_HOLDER", participantName);
			log.info(participantName);
			htmlText = htmlText.replace("CERTIFICATE_PROGRAM", activityName);
			log.info(activityName);
			htmlText = htmlText.replace("CERTIFICATE_YEAR", String.valueOf(LocalDate.now().getYear()));

			htmlText = htmlText.replace("NAME", name);

			htmlText = htmlText.replace("DESIGNATION", designation);
			htmlText = htmlText.replace("IMAGE_DATA", encodedString);
			createPdf("", htmlText, path);

			log.info("PDF generated Completely");
			log.info("Returning Path {}", path);
			return path;

		} catch (Exception e) {
			log.info(e.getMessage());
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
		return null;

	}

	public static void createPdf(String baseUri, String src, String path) throws IOException {
		log.info("Started Generating PDF");
		com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(path);
		PdfDocument pdf = new PdfDocument(writer);
		PageSize pageSize = PageSize.A4.rotate();
		pdf.setDefaultPageSize(pageSize);
		ConverterProperties properties = new ConverterProperties();
		properties.setBaseUri(baseUri);

		HtmlConverter.convertToPdf(src, pdf, properties);
		log.info("Generated PDF");
	}

	@Override
	public String getActivityId(String location, String theme) {
		String[] locationArray = location.split(",");

		if (locationArray.length == 1) {
			Optional<String> locationOpt = Stream.of(locationArray).findFirst();
			if (locationOpt.isPresent()) {
				String activityId = CommonUtils.generateActivityId(locationOpt.get(), theme);
				return activityId;
			}
		} else if (locationArray.length > 1) {
			String activityId = CommonUtils.generateActivityIdForMultiLocation(theme);
			return activityId;
		}

		return null;
	}

	@Override
	public GalleryResponseDTO getImages(SearchCriteria searchCriteria, Integer pageNo, Integer pageSize) {
		GalleryResponseDTO galleryResponseDTO = GalleryResponseDTO.builder().build();
		Map<String, CreativeImages> creativeImagesMap = new HashMap<>();
		galleryResponseDTO.setCreativeImages(creativeImagesMap);
		;
		SearchCriteria criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		Specification<ActivityPicture> specs = CommonSpecification.allActivityPicture(criteria, true);
		Page<ActivityPicture> activityPicturePage = activityPictureRepository.findAll(specs, pageable);

		Long totalElements = activityPicturePage.getTotalElements();

		CreativeImages creativeImages = CreativeImages.builder().build();
		Map<String, String> activityMap = getActivityMap(activityPicturePage.getContent());

		if (!activityPicturePage.isEmpty()) {
			buildCreativeDTO(ImageType.CREATIVE.getImageType(), pageSize, creativeImages, activityPicturePage,
					activityPicturePage.getTotalPages(), totalElements,
					activityPicturePage.getPageable().getPageNumber(), activityPicturePage.hasPrevious(),
					activityPicturePage.hasNext(), activityMap);

			creativeImagesMap.put(criteria.getTagName(), creativeImages);
		}

		return galleryResponseDTO;
	}

	@Override
	public List<CreateOrUpdateActivityDTO> getActivityDetailsForPromotion(String themeName, String location)
			throws EvpException {
		List<CreateOrUpdateActivityDTO> activityList = null;
		try {

			LocalDate currentDate = LocalDate.now();
			Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
			Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
			Map<String, Long> modeLovMap = evpLovService.getModeLovMap();
			Map<String, Long> tagLovMap = evpLovService.getTagLovMap();

			Map<Long, String> lovMapWithIdKey = CommonUtils.getLovMapWithIdKey(locationLovMap);
			Map<Long, String> lovMapWithIdKey2 = CommonUtils.getLovMapWithIdKey(themeLovMap);
			Map<Long, String> lovMapWithIdKey3 = CommonUtils.getLovMapWithIdKey(modeLovMap);
			Map<Long, String> lovMapWithIdKey4 = CommonUtils.getLovMapWithIdKey(tagLovMap);

			SearchCriteria searchCriteria = SearchCriteria.builder().themeName(themeName).location(location).build();

			searchCriteria = CommonUtils.buildParamsForSearchCriteria(searchCriteria, locationLovMap, themeLovMap,
					modeLovMap, tagLovMap);
			Specification<Activity> activitySpecification = CommonSpecification
					.allActivitySpecification(searchCriteria);
			List<Activity> activities = activityRepository.findAll(activitySpecification);

			List<String> pastActivities = activities.stream().filter(activity -> activity.isPublished())
					.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
					.map(Activity::getActivityId).collect(Collectors.toList());

			activities = activities.stream().filter(activity -> activity.isPublished())
					.filter(activity -> !pastActivities.contains(activity.getActivityId()))
					.collect(Collectors.toList());

			activities = filterByLocation(searchCriteria.getLocationId(), activities);

			Map<String, List<Long>> activityLocationMap = getActivityLocationMap(activities);

			filterLocationIds(searchCriteria.getLocationId(), activityLocationMap);

			activityList = CommonUtils.convertActivitiesListToDTO(lovMapWithIdKey, lovMapWithIdKey2, lovMapWithIdKey3,
					lovMapWithIdKey4, activities, activityLocationMap);

			Map<String, String> activityMap = activityList.stream().collect(Collectors
					.toMap(CreateOrUpdateActivityDTO::getActivityId, CreateOrUpdateActivityDTO::getActivityName));

			List<String> activityIds = activityList.stream().map(CreateOrUpdateActivityDTO::getActivityId)
					.collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNamesAndImageType(activityIds, ImageType.PROMOTIONS);

			Map<String, List<ActivityPicture>> activityPictureMap = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));

			activityList.forEach(ac -> {
				List<ActivityPicture> pictures = activityPictureMap.get(ac.getActivityId());
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(pictures,
						ImageType.PROMOTIONS.getImageType(), activityMap);
				ac.setImages(images);
			});

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		return activityList;

	}

	public Map<String, String> getActivityMap(List<ActivityPicture> activityPictures) {

		Map<String, String> activityMap = new HashMap<>();
		if (!activityPictures.isEmpty()) {
			List<String> activityIds = activityPictures.stream().map(ActivityPicture::getActivityPictureId)
					.collect(Collectors.toList());
			List<Activity> activities = activityRepository.findByActivityIds(activityIds);
			activityMap = activities.stream()
					.collect(Collectors.toMap(Activity::getActivityId, Activity::getActivityName));
		}

		return activityMap;

	}

	private void buildCreativeDTO(String imageType, Integer pageSize, CreativeImages creativeImages,
			Page<ActivityPicture> activityPicturesByTag, Integer totalPages, Long totalElements, Integer pageNumber,
			boolean hasPrevious, boolean hasNext, Map<String, String> activityMap) {
		List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPicturesByTag.getContent(),
				imageType, activityMap);

		creativeImages.setHasNext(hasNext);
		creativeImages.setHasPrevious(hasPrevious);
		creativeImages.setPageNo(pageNumber + 1);
		creativeImages.setPageSize(pageSize);
		creativeImages.setTotalPages(totalPages);
		creativeImages.setTotalElements(totalElements.intValue());
		creativeImages.setImages(images);
	}

	private byte[] downloadUrl(URL toDownload) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			byte[] chunk = new byte[4096];
			int bytesRead;
			InputStream stream = toDownload.openStream();

			while ((bytesRead = stream.read(chunk)) > 0) {
				outputStream.write(chunk, 0, bytesRead);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return outputStream.toByteArray();
	}

	@Override
	public List<CreateOrUpdateActivityDTO> getLocationWisePastActivities() {
		Map<String, Long> locationLovMap = evpLovService.getLocationLovMap();
		Map<String, Long> themeLovMap = evpLovService.getThemeLovMap();
		Map<String, Long> modeLovMap = evpLovService.getModeLovMap();
		Map<String, Long> tagLovMap = evpLovService.getTagLovMap();
		Map<Long, String> lovMapWithIdKey = CommonUtils.getLovMapWithIdKey(locationLovMap);
		Map<Long, String> lovMapWithIdKey2 = CommonUtils.getLovMapWithIdKey(themeLovMap);
		Map<Long, String> lovMapWithIdKey3 = CommonUtils.getLovMapWithIdKey(modeLovMap);
		Map<Long, String> lovMapWithIdKey4 = CommonUtils.getLovMapWithIdKey(tagLovMap);

		Sort sort = Sort.by(Sort.Direction.DESC, "createdOn");
		List<Activity> activities = (List<Activity>) activityRepository.findAll(sort);
		Map<String, List<Long>> activityLocationMap = getActivityLocationMap(activities);
		LocalDate currentDate = LocalDate.now();
		List<Activity> pastActivities = activities.stream().filter(activity -> activity.isPublished())
				.filter(activity -> activity.getEndDate().toLocalDate().compareTo(currentDate) < 0)
				.collect(Collectors.toList());

		List<String> allLocations = new ArrayList<>(locationLovMap.keySet());

		List<CreateOrUpdateActivityDTO> activityList = CommonUtils.convertActivitiesListToDTO(lovMapWithIdKey,
				lovMapWithIdKey2, lovMapWithIdKey3, lovMapWithIdKey4, pastActivities, activityLocationMap);

//		Map<String, List<CreateOrUpdateActivityDTO>> locationWiseActivityMap = createLocationWiseActivityMap(
//				activityList, allLocations);

		List<CreateOrUpdateActivityDTO> uniqueActivities = createUnique(activityList);

		getEnrolledOrParticipatedEmployeesForPast(uniqueActivities);

		String role = CommonUtils.getAssignedRole(request, jwtUtil);
		if (role.equals("ROLE_EMPLOYEE")) {

			getEmployeeParticipantStatusForPast(uniqueActivities);

		}

		getAdminUploadImagesForPast(uniqueActivities);

		return uniqueActivities;

	}

	public Map<String, List<CreateOrUpdateActivityDTO>> createLocationWiseActivityMap(
			List<CreateOrUpdateActivityDTO> activityList, List<String> allLocations) {
		Map<String, List<CreateOrUpdateActivityDTO>> locationWiseActivityMap = new HashMap<>();
		for (CreateOrUpdateActivityDTO activity : activityList) {
			String location = activity.getActivityLocation();
			List<String> indLocations = Arrays.asList(location.split(","));
			for (String indLocation : indLocations) {
				if (allLocations.contains(indLocation)) {
					locationWiseActivityMap.computeIfAbsent(indLocation, k -> new ArrayList<>()).add(activity);
				}
			}
		}
		return locationWiseActivityMap;
	}

	public List<CreateOrUpdateActivityDTO> createUnique(List<CreateOrUpdateActivityDTO> activities){
		
		List<CreateOrUpdateActivityDTO> uniqueActivities=new ArrayList<>();
		for(CreateOrUpdateActivityDTO activity : activities)
		{
			if(uniqueActivities.size()>=10)
			{
				break;
				
			}else if(!uniqueActivities.contains(activity))
			{
				uniqueActivities.add(activity);
			}

		}
		
		return uniqueActivities;
	}
	
//	public List<CreateOrUpdateActivityDTO> createUnique(
//			Map<String, List<CreateOrUpdateActivityDTO>> locationWiseActivity) {
//		List<CreateOrUpdateActivityDTO> uniqueActivities = new ArrayList<>();
//		for (String location : locationWiseActivity.keySet()) {
//
//			List<CreateOrUpdateActivityDTO> locationActivityList = locationWiseActivity.get(location);
//			for (int i = 0; i < locationActivityList.size(); i++) {
//
//				if (uniqueActivities.contains(locationActivityList.get(i)) && i != locationActivityList.size() - 1) {
//
//				} else {
//					uniqueActivities.add(locationActivityList.get(i));
//					break;
//				}
//			}
//
//		}
//
//		return uniqueActivities;
//	}

	
	private void getEnrolledOrParticipatedEmployeesForPast(List<CreateOrUpdateActivityDTO> pastActivities) {
		List<String> pastActivityUUIDS = null;

		if (Optional.ofNullable(pastActivities).isPresent()) {
			pastActivityUUIDS = pastActivities.stream().map(CreateOrUpdateActivityDTO::getActivityUUID)
					.collect(Collectors.toList());

			List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
					.getActivitiesWithStatusParticipated(pastActivityUUIDS);

			Map<String, List<String>> activityWiseEmployees = employeeActivityHistories.stream()
					.collect(Collectors.groupingBy(EmployeeActivityHistory::getActivityUUID,
							Collectors.mapping(EmployeeActivityHistory::getEmployeeName, Collectors.toList())));
			pastActivities.stream().forEach(activity -> {
				activity.setEnrolledEmployees(activityWiseEmployees.get(activity.getActivityUUID()));
			});
		}

	}

	private void getEmployeeParticipantStatusForPast(List<CreateOrUpdateActivityDTO> pastActivities) {
		log.info("Retriving Employee Participant status");
		List<String> activityIds = new ArrayList<>(1);
		List<String> pastActivityIds = new ArrayList<>(1);
		if (Optional.ofNullable(pastActivities).isPresent() && Optional.ofNullable(pastActivities).isPresent()) {
			pastActivityIds = pastActivities.stream().map(CreateOrUpdateActivityDTO::getActivityUUID)
					.collect(Collectors.toList());
		}
		activityIds.addAll(pastActivityIds);

		String username = CommonUtils.getUsername(request, jwtUtil);
		String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
		List<EmployeeActivityHistory> employeeActivityHistories = employeeActivityHistoryRepository
				.getActivitiesForEmployee(employeeId, activityIds);

		Map<String, EmployeeActivityStatus> employeeActivityStatusMap = employeeActivityHistories.stream()
				.filter(e -> e.getActivityName() != null).collect(Collectors.toMap(
						EmployeeActivityHistory::getActivityName, EmployeeActivityHistory::getEmployeeActivityStatus));

		if (Optional.ofNullable(pastActivities).isPresent() && Optional.ofNullable(pastActivities).isPresent()) {
			pastActivities.forEach(activity -> {
				EmployeeActivityStatus employeeActivityStatus = employeeActivityStatusMap.get(activity.getActivityId());
				if (Optional.ofNullable(employeeActivityStatus).isPresent()) {
					activity.setEmployeeParticipationStatus(employeeActivityStatus.getStatus());
				}
			});
		}

	}

	private void getAdminUploadImagesForPast(List<CreateOrUpdateActivityDTO> pastActivities) {
		List<String> pastActivityUUIDS = null;

		if (Optional.ofNullable(pastActivities).isPresent()) {
			pastActivityUUIDS = pastActivities.stream().map(CreateOrUpdateActivityDTO::getActivityId)
					.collect(Collectors.toList());

			List<ActivityPicture> activityPictures = activityPictureRepository
					.findByActivityNameAdminUpload(pastActivityUUIDS);

			Map<String, List<ActivityPicture>> activityWiseEmployees = activityPictures.stream()
					.collect(Collectors.groupingBy(ActivityPicture::getActivityPictureId,
							Collectors.mapping(Function.identity(), Collectors.toList())));
			Map<String, String> activityMap = uploadService.getActivityMap(activityPictures);
			pastActivities.stream().forEach(activity -> {
				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(
						activityWiseEmployees.get(activity.getActivityId()), ImageType.ADMIN_UPLOAD.getImageType(),
						activityMap);
				activity.setImages(images);
			});

		}

	}

}
