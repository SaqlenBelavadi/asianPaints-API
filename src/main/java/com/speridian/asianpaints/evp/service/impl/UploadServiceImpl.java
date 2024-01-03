package com.speridian.asianpaints.evp.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.CreativeImages;
import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.GalleryResponseDTO;
import com.speridian.asianpaints.evp.dto.ImageDTO;
import com.speridian.asianpaints.evp.dto.ImageResponse;
import com.speridian.asianpaints.evp.dto.PublishOrUnPublishImages;
import com.speridian.asianpaints.evp.dto.PublishedImages;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.dto.UploadResponse;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFeedback;
import com.speridian.asianpaints.evp.entity.ActivityFeedbackLocation;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.BannerPicture;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.entity.Leaders;
import com.speridian.asianpaints.evp.entity.PartnersLogo;
import com.speridian.asianpaints.evp.entity.TestimonialData;
import com.speridian.asianpaints.evp.entity.Video;
import com.speridian.asianpaints.evp.entity.VoiceOfChange;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.EmployeeService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.service.UploadService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityPictureRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.BannerPictureRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.transactional.repository.LeadersRepository;
import com.speridian.asianpaints.evp.transactional.repository.PartnersLogoRepository;
import com.speridian.asianpaints.evp.transactional.repository.TestimonialRepository;
import com.speridian.asianpaints.evp.transactional.repository.VideoRepository;
import com.speridian.asianpaints.evp.transactional.repository.VoiceOfChangeRepository;
import com.speridian.asianpaints.evp.util.CommonSpecification;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.ImageType;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UploadServiceImpl implements UploadService {

	private static final String PATH_DELIMITER = "/";

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private EvpLovService evpLovService;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private BannerPictureRepository bannerPictureRepo;

	@Autowired
	private VoiceOfChangeRepository vocRepo;
	@Autowired
	private VideoRepository videoRepo;

	@Autowired
	private TestimonialRepository testimonialDataRepo;

	@Autowired
	private PartnersLogoRepository partnersLogoRepo;

	@Autowired
	private ActivityPictureRepository activityPictureRepository;

	@Autowired
	private EmployeeActivityHistoryRepository employeeActivityHistoryRepository;

	@Value("${evp.imageStorage.url}")
	private String imageStorageUrl;

	@Value("${evp.imageStorage.apikey}")
	private String apikey;

	@Value("${evp.imageStorage.parentFolder}")
	private String parentFolder;

	@Value("${evp.imageStorage.accountName}")
	private String accountName;

	@Value("${evp.imageStorage.accountKey}")
	private String accountKey;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private LeadersRepository leadersRepo;

	@Override
	public String uploadData(MultipartFile multipartFile) throws EvpException {
		List<EmployeeActivityHistory> employeeActivityHistories = new LinkedList<>();
		Integer recordCount = 0;
		AtomicInteger failedRecord = new AtomicInteger(0);
		try {
			log.info("Uploading Employee Activity Participant Details");

			try (Reader reader = new InputStreamReader(multipartFile.getInputStream())) {
				CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
				for (String[] csvRecord : csvReader) {

					EmployeeActivityHistory employeeActivityHistory = new EmployeeActivityHistory();
					if (Optional.ofNullable(csvRecord).isPresent() && csvRecord.length > 0 && !csvRecord[0].isEmpty()) {
						boolean validData = true;
						String employeeId = csvRecord[0];
						String employeeName = csvRecord[1];
						String activityName = csvRecord[3];
						String activityId = csvRecord[2];
						String tagName = csvRecord[4];
						String participationHours = csvRecord[5];
						String location = csvRecord[6];

						String activityUUid = null;
						boolean approvedByAdmin = Boolean.valueOf(csvRecord[7]);
						boolean rejectedByAdmin = Boolean.valueOf(csvRecord[8]);

						EmployeeDTO employee = null;
						try {
							employee = employeeService.getEmployeeById(employeeId);
						} catch (Exception e) {
							log.error("Error While getting employee");
						}

						if (!Optional.ofNullable(employee).isPresent()) {
							log.error("Employee Doesn't exist " + employeeId);
							validData = false;
						}

						if (validData) {
							Activity activity = null;
							try {

								Optional<Activity> activityOpt = activityRepository.findByActivityId(activityId);

								if (activityOpt.isPresent()) {
									activity = activityOpt.get();
									LocalDate currentDate = LocalDate.now();

									if (activity.isPublished() && ((activity.getEndDate().toLocalDate()
											.compareTo(currentDate) < 0)
											|| (activity.getStartDate().toLocalDate().compareTo(currentDate) <= 0
													&& activity.getEndDate().toLocalDate()
															.compareTo(currentDate) >= 0))) {
										activityUUid = activity.getActivityUUID();
									} else {
										validData = false;
									}

								} else {
									validData = false;
									log.error("Activity Doesn't exist " + activityName);
								}
							} catch (Exception e) {

							}

							if (!Optional.ofNullable(evpLovService.getTagLovMap().get(tagName)).isPresent()) {
								log.error("Tag Doesn't exist " + tagName);
								validData = false;
							}
//							Add logic for employee location validation
							if (!Optional.ofNullable(evpLovService.getLocationLovMap().get(location)).isPresent()) {
								log.error("Location Doesn't exist " + location);
								validData = false;
							} else {
								if (!employee.getLocationName().equalsIgnoreCase(location)
										&& !employee.getDivisionName().equalsIgnoreCase(location)) {
									log.error("User's location is not valid " + location);
									validData = false;
								}
							}

							try {

								List<String> activityList = new ArrayList<>(Arrays.asList(activityUUid));
								List<EmployeeActivityHistory> existingHistory = employeeActivityHistoryRepository
										.findByEmployeeIdAndActivityUUids(employeeId, activityList);

								if (!existingHistory.isEmpty()) {
									if (Optional.ofNullable(existingHistory).isPresent()) {
										validData = false;
										log.error(
												"Employee Activity History Already Present with activity id {} and employee id {} ",
												activityUUid, employeeId);
									}
								}
							} catch (Exception e) {
								validData = false;
							}

							if (validData) {
								employeeActivityHistory.setActivityLocation(location);
								employeeActivityHistory.setActivityName(activityId);
								employeeActivityHistory.setActivityTag(tagName);
								employeeActivityHistory.setActivityPhysicalName(activityName);

								employeeActivityHistory.setDepartmentName(employee.getDepartmentName());
								employeeActivityHistory.setActivityUUID(activityUUid);
								employeeActivityHistory.setEmployeeActivityStatus(EmployeeActivityStatus.PARTICIPATED);
								employeeActivityHistory.setEmployeeId(employeeId);
								employeeActivityHistory.setEmployeeName(employeeName);
								employeeActivityHistory.setParticipationHours(participationHours);
								employeeActivityHistory.setApprovedByAdmin(approvedByAdmin);
								employeeActivityHistory.setRejectedByAdmin(rejectedByAdmin);

								if (Optional.ofNullable(activity).isPresent()) {
									employeeActivityHistory.setActivityTheme(
											CommonUtils.getLovMapWithIdKey(evpLovService.getThemeLovMap())
													.get(activity.getThemeNameId()));
									employeeActivityHistory
											.setMode(CommonUtils.getLovMapWithIdKey(evpLovService.getModeLovMap())
													.get(activity.getModeOfParticipationId()));
									employeeActivityHistory.setStartDate(activity.getStartDate().toLocalDate());
									employeeActivityHistory.setEndDate(activity.getEndDate().toLocalDate());
								}
							}

						}

						if (validData) {
							employeeActivityHistories.add(employeeActivityHistory);
						} else {
							failedRecord.incrementAndGet();
						}

					}

				}

				if (employeeActivityHistories.size() > 0) {
					recordCount = employeeActivityHistories.size();
					log.info("Saving Employee Activity History");
					employeeActivityHistoryRepository.saveAll(employeeActivityHistories);
				}

			}

			return "Successfully Uploaded " + recordCount + " employee records" + " and " + failedRecord.get()
					+ " record fail";
		}

		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("File Format is Wrong");
		}

	}

	@Override
	public List<String> uploadImages(MultipartFile[] multipartRequests, String imageType, String acticityName,
			String activityTheme, String activityTag, String startDate, String endDate, String location, String mode,
			boolean manualUpload) throws EvpException {

		try {

			MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
			StringBuilder urlBuilder = new StringBuilder();
			String url = urlBuilder.append(imageStorageUrl).append(apikey).toString();

			List<UploadResponse> uploadResponses = new LinkedList<>();
			List<ActivityPicture> activityPictures = new LinkedList<>();
			Optional<ImageType> imageTypeOpt = getImageType(imageType);

			log.info("Uploading Files To Azure Storage");
			List<String> fileNames = uploadFileToAzureStorage(multipartRequests, imageType, acticityName, imageMap, url,
					parentFolder, uploadResponses, imageTypeOpt);

			buildActivityPictures(acticityName, uploadResponses, activityPictures, imageTypeOpt, fileNames,
					activityTheme, activityTag, startDate, endDate, location, mode, manualUpload);

			log.info("Saving Activity Pictures");
			activityPictureRepository.saveAll(activityPictures);

			return fileNames;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private void buildActivityPictures(String acticityName, List<UploadResponse> uploadResponses,
			List<ActivityPicture> activityPictures, Optional<ImageType> imageTypeOpt, List<String> fileNames,
			String activityTheme, String activityTag, String startDate, String endDate, String location, String mode,
			boolean manualUpload) throws EvpException {
		try {

			String[] locationArray = location.split(",");

			Long themeId = evpLovService.getThemeLovMap().get(activityTheme);
			if (!Optional.ofNullable(themeId).isPresent()) {
				log.error("Theme Doesn't exist");
				throw new EvpException("Theme Doesn't exist");
			}

			Long modeId = evpLovService.getModeLovMap().get(mode);

			if (!Optional.ofNullable(modeId).isPresent()) {
				log.error("Mode Doesn't exist");
			}

			Optional<Long> tagIdOpt = evpLovService.getTagLovMap().entrySet().stream()
					.filter(t -> t.getKey().equals(activityTag)).map(t -> t.getValue()).findFirst();
			if (!tagIdOpt.isPresent()) {

				log.error("Tag Doesn't exist");
				throw new EvpException("Tag Doesn't exist");
			}

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";
			LocalDateTime sDate = CommonUtils.getActivityDate(startDate);
			LocalDateTime eDate = CommonUtils.getActivityDate(endDate);

			if (imageTypeOpt.isPresent() && imageTypeOpt.get().equals(ImageType.PROMOTIONS)) {
				Optional<UploadResponse> uploadOpt = uploadResponses.stream().findFirst();
				if (uploadOpt.isPresent()) {
					UploadResponse uploadResponse = uploadOpt.get();

					ActivityPicture activityPicture = new ActivityPicture();
					activityPicture.setActivityPictureId(acticityName);
					activityPicture.setActivityPictureLocation(uploadResponse.getAssetUrl());
					String fileName = fileNames.get(0);
					activityPicture.setImageName(fileName);
					activityPicture.setActivityTag(activityTag);
					activityPicture.setActivityTheme(activityTheme);
					activityPicture.setUploadedBy(employeeName);
					activityPicture.setStartDate(sDate);
					activityPicture.setEndDate(eDate);
					activityPicture.setMode(mode);
					activityPicture.setActivityLocation(locationArray[0]);
					activityPicture.setContainerLocation(uploadResponse.getFileLocation());
					activityPicture.setImageType(imageTypeOpt.get());
					activityPictures.add(activityPicture);
				}

			} else {
				IntStream.range(0, uploadResponses.size()).forEach(index -> {
					UploadResponse uploadResponse = uploadResponses.get(index);

					Stream.of(locationArray).forEach(l -> {
						ActivityPicture activityPicture = new ActivityPicture();
						activityPicture.setActivityPictureId(acticityName);
						activityPicture.setActivityPictureLocation(uploadResponse.getAssetUrl());
						String fileName = fileNames.get(index);
						activityPicture.setImageName(fileName);
						activityPicture.setActivityTag(activityTag);
						activityPicture.setActivityTheme(activityTheme);
						activityPicture.setUploadedBy(employeeName);
						activityPicture.setStartDate(sDate);
						activityPicture.setEndDate(eDate);
						activityPicture.setActivityLocation(l);
						activityPicture.setMode(mode);
						activityPicture.setContainerLocation(uploadResponse.getFileLocation());

						if (imageTypeOpt.isPresent()) {
							ImageType iType = imageTypeOpt.get();
							activityPicture.setImageType(iType);
							switch (iType) {

							case EMPLOYEE_UPLOAD:
								activityPicture.setPublished(Boolean.FALSE);
								activityPicture.setUploadedByEmployee(true);
								activityPicture.setManualUpload(manualUpload);
								break;
							default:
								break;
							}
						}

						activityPictures.add(activityPicture);
					});

				});
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}

	private List<String> uploadFileToAzureStorage(MultipartFile[] multipartRequests, String imageType,
			String activityName, MultiValueMap<String, Object> imageMap, String url, String parentFolder,
			List<UploadResponse> uploadResponses, Optional<ImageType> imageTypeOpt) throws EvpException {
		LocalDate now = LocalDate.now();

		List<String> imageNames = Arrays.stream(multipartRequests).map(m -> m.getOriginalFilename())
				.collect(Collectors.toList());
		Set<String> imageSet = imageNames.stream().distinct().collect(Collectors.toSet());

		if (imageNames.size() > imageSet.size()) {
			throw new EvpException("Duplicate Images For image typ " + imageType + " couldn't be uploaded");
		}

		Arrays.stream(multipartRequests).forEach(multipartRequest -> {

			List<ActivityPicture> activityPictureOpt = activityPictureRepository
					.findByImageNameAndActivityNameAndImageType(multipartRequest.getOriginalFilename(), activityName,
							imageTypeOpt.get());

			if (!imageTypeOpt.get().equals(ImageType.PROMOTIONS) && activityPictureOpt.size() > 0) {
				log.error("Activity Picture With name {} already exists ", multipartRequest.getOriginalFilename());
			} else {

				String fileName = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				fileName = multipartRequest.getOriginalFilename();
				File file = convert(multipartRequest);
				if (Optional.ofNullable(file).isPresent()) {
					imageMap.add("fileData", new FileSystemResource(file));
					imageMap.add("fileContainer", "aplms");

					String fileLocation = getFileLocation(activityName, parentFolder, now, imageTypeOpt, fileName);

					String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));

					imageMap.add("fileLoc", fileL);
					HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
							imageMap, headers);

					RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
					restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
					restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
					ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
							UploadResponse.class);

					if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
						log.info("Successfully Uploaded image {}", fileName);
						UploadResponse uploadResponse = responseEntity.getBody();
						uploadResponse.setActivityName(activityName);
						uploadResponse.setFileLocation(fileLocation);
						uploadResponses.add(uploadResponse);

					}
				} else {
					log.error("Couldn't upload file with name {}", fileName);
				}
			}

		});
		return imageNames;
	}

	private String getFileLocation(String activityName, String parentFolder, LocalDate now,
			Optional<ImageType> imageTypeOpt, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		if (imageTypeOpt.isPresent()) {
			String formattedDate = CommonUtils.formatLocalDateTime(now);

			switch (imageTypeOpt.get()) {
			case ADMIN_UPLOAD:

				fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
						.append(PATH_DELIMITER).append("ADMIN").append(PATH_DELIMITER).append(activityName)
						.append(PATH_DELIMITER).append(fileName).toString();
				break;
			case CREATIVE:
				fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
						.append(PATH_DELIMITER).append("CREATIVE").append(PATH_DELIMITER).append(activityName)
						.append(PATH_DELIMITER).append(fileName).toString();
				break;
			case EMPLOYEE_UPLOAD:
				fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
						.append(PATH_DELIMITER).append("EMPLOYEE").append(PATH_DELIMITER).append(activityName)
						.append(PATH_DELIMITER).append(fileName).toString();
				break;
			case PAST_VIDEOS:
				fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
						.append(PATH_DELIMITER).append("PASTVIDEO").append(PATH_DELIMITER).append(activityName)
						.append(PATH_DELIMITER).append(fileName).toString();
				break;
			case PROMOTIONS:
				fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
						.append(PATH_DELIMITER).append("PROMOTIONS").append(PATH_DELIMITER).append(activityName)
						.append(PATH_DELIMITER).append(fileName).toString();
				break;

			default:
				break;
			}
		}
		return fileLocation;
	}

	private Optional<ImageType> getImageType(String imageType) {
		Optional<ImageType> imageTypeOpt = Optional.ofNullable(ImageType.valueOf(imageType));

		return imageTypeOpt;
	}

	private static File convert(MultipartFile file) {
		log.info("Converting Multipart request to File");
		File convFile = new File(file.getOriginalFilename());
		try {
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		} catch (IOException e) {
			log.error(e.getMessage());
			return null;
		}

		return convFile;
	}

	@Override
	public Object getImagesByType(String imageType, SearchCriteria searchCriteria, Integer pageNo, Integer pageSize)
			throws EvpException {
		try {

			List<ImageDTO> imageDtos = null;
			Optional<ImageType> imageTypeOpt = getImageType(imageType);
			if (imageTypeOpt.isPresent()) {
				ImageType iType = imageTypeOpt.get();
				String activityName = searchCriteria.getActivityId();
				switch (iType) {
//				Activity Pictures For Activity Details
				case ADMIN_UPLOAD:
					if (!Optional.ofNullable(activityName).isPresent()) {
						throw new EvpException("Activity Name is required");
					}
					List<ActivityPicture> activityPictures = activityPictureRepository
							.findByActivityPictureId(activityName);

					activityPictures = removeDuplicatePictures(activityPictures);
					Map<String, String> activityMap = getActivityMap(activityPictures);

					imageDtos = CommonUtils.convertActivityPicturesToImageDTO(activityPictures, imageType, activityMap);

					return imageDtos;
				case CREATIVE:
				case PAST_VIDEOS:
				case PROMOTIONS:
					if (!Optional.ofNullable(activityName).isPresent()) {
						throw new EvpException("Activity Name is required");
					}
					List<ActivityPicture> activityPicturesByType = activityPictureRepository
							.findByActivityNameAndImageType(activityName, iType);

					activityPicturesByType = removeDuplicatePictures(activityPicturesByType);

					Map<String, String> activityMaps = getActivityMap(activityPicturesByType);
					imageDtos = CommonUtils.convertActivityPicturesToImageDTO(activityPicturesByType, imageType,
							activityMaps);

					return imageDtos;

				default:
//					Gallery Display
					return buildGalleryResponseDTO(imageType, searchCriteria, pageNo, pageSize);
				}
			}

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		return null;
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

	private Object buildGalleryResponseDTO(String imageType, SearchCriteria searchCriteria, Integer pageNo,
			Integer pageSize) throws EvpException {

		GalleryResponseDTO galleryResponseDTO = GalleryResponseDTO.builder().build();

		Map<String, PublishedImages> publishedImagesMap = new HashMap<>();

		Map<String, PublishedImages> unPublishedImagesMap = new HashMap<>();

		Map<String, CreativeImages> creativeImagesMap = new HashMap<>();

		galleryResponseDTO.setPublishedImages(publishedImagesMap);
		galleryResponseDTO.setUnPublishedImages(unPublishedImagesMap);
		galleryResponseDTO.setCreativeImages(creativeImagesMap);

		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

		Pageable pageableImages = PageRequest.of(pageNo, pageSize);

		String role = CommonUtils.getAssignedRole(request, jwtUtil);

		if (role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("ROLE_CADMIN")) {

			SearchCriteria criteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);

			getPublishedPhotosByTheme(imageType, searchCriteria, pageSize, publishedImagesMap, unPublishedImagesMap,
					pageableImages, searchCriteria, role);

			getUnPublishedPhotosByTheme(imageType, searchCriteria, pageSize, publishedImagesMap, unPublishedImagesMap,
					pageableImages, searchCriteria, role);

			getCreativesPhotoByTag(imageType, searchCriteria, pageSize, creativeImagesMap, pageable, criteria);

		} else if (role.equalsIgnoreCase("ROLE_EMPLOYEE")) {
			/*
			 * String username = CommonUtils.getUsername(request, jwtUtil); String
			 * employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			 * Optional<Employee> employeeOpt =
			 * employeeRepository.findByEmployeeId(employeeId); String userName = null; if
			 * (employeeOpt.isPresent()) { userName = employeeOpt.get().getEmployeeName();
			 * searchCriteria.setUsername(userName); }
			 */

			searchCriteria = CommonUtils.buildParamsForSearchCriteriaForGalery(searchCriteria);

			getPublishedPhotosByTheme(imageType, searchCriteria, pageSize, publishedImagesMap, unPublishedImagesMap,
					pageableImages, searchCriteria, role);

		}

		return galleryResponseDTO;
	}

	private void getCreativesPhotoByTag(String imageType, SearchCriteria searchCriteria, Integer pageSize,
			Map<String, CreativeImages> creativeImagesMap, Pageable pageable, SearchCriteria criteria)
			throws EvpException {

		if (Optional.ofNullable(searchCriteria.getTagNames()).isPresent()) {
			searchCriteria.getTagNames().forEach(tagName -> {
				CreativeImages creativeImages = CreativeImages.builder().build();

				criteria.setTagName(tagName);

				Specification<ActivityPicture> tagSpecs = CommonSpecification.allActivityPicture(criteria, true);

				Map<String, String> activityMap = new HashMap<>();
				Page<ActivityPicture> activityPicturePage = activityPictureRepository.findAll(tagSpecs, pageable);
				if (!activityPicturePage.getContent().isEmpty()) {
					activityMap = getActivityMap(activityPicturePage.getContent());
				}

				Integer totalPages = activityPicturePage.getTotalPages();
				Long totalElements = activityPicturePage.getTotalElements();

				Integer pageNumber = activityPicturePage.getPageable().getPageNumber();

				boolean hasPrevious = activityPicturePage.hasPrevious();

				boolean hasNext = activityPicturePage.hasNext();

				if (!activityPicturePage.isEmpty()) {
					buildCreativeDTO(imageType, pageSize, creativeImages, activityPicturePage, totalPages,
							totalElements, pageNumber, hasPrevious, hasNext, activityMap);

					creativeImagesMap.put(tagName, creativeImages);
				}
			});
		} else {
			throw new EvpException("Tag name cannot be blank");
		}

	}

	private void getPublishedPhotosByTheme(String imageType, SearchCriteria searchCriteria, Integer pageSize,
			Map<String, PublishedImages> publishedImagesMap, Map<String, PublishedImages> unPublishedImagesMap,
			Pageable pageable, SearchCriteria criteria, String role) throws EvpException {

		if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
			searchCriteria.getThemeNames().forEach(themeName -> {

				PublishedImages publishedImages = PublishedImages.builder().build();

				criteria.setThemeName(themeName);

				Specification<ActivityPicture> themeSpecs = CommonSpecification.allActivityPicturePublished(criteria,
						false);

				List<ActivityPicture> activityPicturePage = activityPictureRepository.findAll(themeSpecs);

				Map<String, String> activityMap = new HashMap<>();
				if (Optional.ofNullable(activityPicturePage).isPresent() && !activityPicturePage.isEmpty()) {

					activityMap = getActivityMap(activityPicturePage);

				}

				if (Optional.ofNullable(activityPicturePage).isPresent() && !activityPicturePage.isEmpty()) {

					publishedImages = buildPublishedEmployeeImageDTO(imageType, pageSize, publishedImages, themeName,
							activityPicturePage, pageable, activityMap);

					publishedImagesMap.put(themeName, publishedImages);

				}

			});
		} else {
			throw new EvpException("Theme Name cannot be blank");
		}

	}

	private void getUnPublishedPhotosByTheme(String imageType, SearchCriteria searchCriteria, Integer pageSize,
			Map<String, PublishedImages> publishedImagesMap, Map<String, PublishedImages> unPublishedImagesMap,
			Pageable pageable, SearchCriteria criteria, String role) throws EvpException {

		if (Optional.ofNullable(searchCriteria.getThemeNames()).isPresent()) {
			searchCriteria.getThemeNames().forEach(themeName -> {

				PublishedImages unPublishedImages = PublishedImages.builder().build();

				criteria.setThemeName(themeName);

				Specification<ActivityPicture> themeSpecs = CommonSpecification.allActivityPictureUnPublished(criteria,
						false);

				List<ActivityPicture> activityPicturePage = activityPictureRepository.findAll(themeSpecs);

				Map<String, String> activityMap = new HashMap<>();
				if (Optional.ofNullable(activityPicturePage).isPresent() && !activityPicturePage.isEmpty()) {

					activityMap = getActivityMap(activityPicturePage);

				}

				if (!activityPicturePage.isEmpty()) {
					unPublishedImages = buildUnpublishedEmployeeImageDTO(imageType, pageSize, pageable, themeName,
							unPublishedImages, activityPicturePage, activityMap);

					if (role.equalsIgnoreCase("ROLE_ADMIN") || role.equalsIgnoreCase("ROLE_CADMIN")) {
						unPublishedImagesMap.put(themeName, unPublishedImages);
					}

				}

			});
		} else {
			throw new EvpException("Theme Name cannot be blank");
		}

	}

	private void buildEmployeeImageDTO(String imageType, Integer pageSize, PublishedImages publishedImages,
			PublishedImages unPublishedImages, Page<ActivityPicture> activityPicturesByTheme, Integer totalPages,
			Long totalElements, Integer pageNumber, boolean hasPrevious, boolean hasNext,
			Map<String, String> activityMap) {

		List<ActivityPicture> activityPictures = removeDuplicatePictures(activityPicturesByTheme.getContent());

		List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures, imageType, activityMap);

		List<ImageDTO> publishedImagesDTO = images.stream()
				.filter(image -> image.getPublished() != null && image.getPublished()).collect(Collectors.toList());

		List<ImageDTO> unPublishedImagesDTO = images.stream()
				.filter(image -> image.getPublished() == null || !image.getPublished()).collect(Collectors.toList());

		publishedImages.setHasNext(hasNext);
		publishedImages.setHasPrevious(hasPrevious);
		publishedImages.setPageNo(pageNumber + 1);
		publishedImages.setPageSize(pageSize);
		publishedImages.setTotalPages(totalPages);
		publishedImages.setTotalElements(publishedImagesDTO.size());
		publishedImages.setImages(publishedImagesDTO);

		unPublishedImages.setHasNext(hasNext);
		unPublishedImages.setHasPrevious(hasPrevious);
		unPublishedImages.setPageNo(pageNumber + 1);
		unPublishedImages.setPageSize(pageSize);
		unPublishedImages.setTotalPages(totalPages);
		unPublishedImages.setTotalElements(unPublishedImagesDTO.size());
		unPublishedImages.setImages(unPublishedImagesDTO);

	}

	private PublishedImages buildPublishedEmployeeImageDTO(String imageType, Integer pageSize,
			PublishedImages publishedImages, String themeName, List<ActivityPicture> activityPicturesByTheme,
			Pageable pageable, Map<String, String> activityMap) {

		List<ActivityPicture> activityPictures = removeDuplicatePictures(activityPicturesByTheme);

		List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures, imageType, activityMap);

		List<ImageDTO> publishedImagesDTO = images.stream()
				.filter(image -> image.getPublished() != null && image.getPublished()).collect(Collectors.toList());

		List<List<ImageDTO>> imageDTOs = CommonUtils.batchesOfList(publishedImagesDTO, pageSize);

		publishedImages = CommonUtils.getPaginationDetailsForPublishedImages(pageable.getPageNumber(), pageSize,
				publishedImagesDTO.size());
		publishedImages.setThemeName(themeName);
		publishedImages.setImages(imageDTOs.isEmpty() ? Collections.emptyList()
				: pageable.getPageNumber() > imageDTOs.size() ? Collections.emptyList()
						: imageDTOs.get(pageable.getPageNumber() - 1));

		return publishedImages;

	}

	private PublishedImages buildUnpublishedEmployeeImageDTO(String imageType, Integer pageSize, Pageable pageable,
			String themeName, PublishedImages unPublishedImages, List<ActivityPicture> activityPicturesByTheme,
			Map<String, String> activityMap) {

		List<ActivityPicture> activityPictures = removeDuplicatePictures(activityPicturesByTheme);

		List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures, imageType, activityMap);

		List<ImageDTO> unPublishedImagesDTO = images.stream()
				.filter(image -> image.getPublished() == null || !image.getPublished()).collect(Collectors.toList());

		List<List<ImageDTO>> imageDTOs = CommonUtils.batchesOfList(unPublishedImagesDTO, pageSize);

		unPublishedImages = CommonUtils.getPaginationDetailsForPublishedImages(pageable.getPageNumber(), pageSize,
				unPublishedImagesDTO.size());
		unPublishedImages.setThemeName(themeName);
		unPublishedImages.setImages(imageDTOs.isEmpty() ? Collections.emptyList()
				: pageable.getPageNumber() > imageDTOs.size() ? Collections.emptyList()
						: imageDTOs.get(pageable.getPageNumber() - 1));

		return unPublishedImages;
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

	@Override
	public void publishOrUnPublishPhoto(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException {
		try {
			if (!publishOrUnpublishImages.isEmpty()) {
				publishOrUnpublishImages.forEach(image -> {
					List<ActivityPicture> activityPictureOpt = activityPictureRepository
							.findByImageNameAndActivityName(image.getImageName(), image.getActivityName());
					if (!activityPictureOpt.isEmpty()) {
						activityPictureOpt.forEach(activityPicture -> {
							activityPicture.setUploadedByAdmin(true);
							activityPicture.setPublished(image.isPublishOrUnpublish());
						});
						activityPictureRepository.saveAll(activityPictureOpt);
					}

				});
			} else {
				throw new EvpException("No Images Available");
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public void uploadPhotoToGallery(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException {
		try {
			List<ActivityPicture> activityPictures = new LinkedList<>();
			if (!publishOrUnpublishImages.isEmpty()) {
				publishOrUnpublishImages.forEach(image -> {
					List<ActivityPicture> activityPictureOpt = activityPictureRepository
							.findByImageNameAndActivityName(image.getImageName(), image.getActivityId());

					if (!activityPictureOpt.isEmpty()) {

						activityPictureOpt.forEach(activityPicture -> {
							if (activityPicture.getImageType().equals(ImageType.EMPLOYEE_UPLOAD)) {
								activityPicture.setUploadedByAdmin(true);
								activityPicture.setDeleted(null);
								activityPicture.setPublished(false);
								activityPictures.add(activityPicture);
							}
						});

					}

				});
				activityPictureRepository.saveAll(activityPictures);
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	@Override
	public void deletePhoto(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException {
		try {
			List<ActivityPicture> softDeletePictures = new LinkedList<>();
			List<ActivityPicture> deletePictures = new LinkedList<>();
			if (!publishOrUnpublishImages.isEmpty()) {

				publishOrUnpublishImages.forEach(image -> {
					ImageType imageType = ImageType.valueOf(image.getImageType());
					List<ActivityPicture> activityPictureOpt = null;
					if (imageType.equals(ImageType.ALL)) {
						activityPictureOpt = activityPictureRepository
								.findByImageNameAndActivityName(image.getImageName(), image.getActivityId());
					} else {
						activityPictureOpt = activityPictureRepository
								.findByImageNameAndActivityName(image.getImageName(), image.getActivityId(), imageType);
					}

					if (!activityPictureOpt.isEmpty()) {
						if (image.isSoftDelete()) {
							activityPictureOpt.forEach(activityPicture -> {
								activityPicture.setDeleted(true);
								activityPicture.setUploadedByAdmin(false);
								softDeletePictures.add(activityPicture);
							});

						} else {
							deleteImageFromContainer(deletePictures, activityPictureOpt);
						}

					}

				});

				if (deletePictures.size() > 0) {
					activityPictureRepository.deleteAll(deletePictures);
				}
				if (softDeletePictures.size() > 0) {
					activityPictureRepository.saveAll(softDeletePictures);
				}

			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private void deleteImageFromContainer(List<ActivityPicture> deletePictures,
			List<ActivityPicture> activityPictures) {
		String storageConnectionString = CommonUtils.getStorageString(accountName, accountKey);

		activityPictures.forEach(activityPicture -> {
			try {
				String fileContainer = "aplms";
				String fileName = activityPicture.getContainerLocation();

				// Retrieve storage account from connection-string.
				CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

				// Create the blob client.
				CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

				// Get a reference to a container.
				// The container name must be lower case
				CloudBlobContainer container = blobClient.getContainerReference(fileContainer);

				// Retrieve reference to blob name
				CloudBlockBlob blob = container.getBlockBlobReference(fileName);

				// Delete blob
				blob.deleteIfExists();
				deletePictures.add(activityPicture);
			} catch (Exception e) {
				log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
						.collect(Collectors.joining("\n")));

			}
		});

	}

	@Override
	public ImageResponse getImagesForActivityDetails(SearchCriteria searchCriteria, Integer pageNo, Integer pageSize)
			throws EvpException {
		List<ActivityPicture> activityPictures = null;
		Page<ActivityPicture> activityPicturesPage = null;
		try {
			Optional<Activity> existingActivityOpt = activityRepository
					.findByActivityId(searchCriteria.getActivityId());

			if (existingActivityOpt.isPresent()) {
				String role = CommonUtils.getAssignedRole(request, jwtUtil);
				String username = CommonUtils.getUsername(request, jwtUtil);
				String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
				Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
				if (role.equals("ROLE_EMPLOYEE") && employeeOpt.isPresent()) {
					activityPictures = activityPictureRepository.findByActivityNameForDetailsWithoutPage(
							Arrays.asList(searchCriteria.getActivityId()), employeeOpt.get().getEmployeeName());

				} else {
					activityPictures = activityPictureRepository
							.findByActivityNameForDetailsWithoutPage(Arrays.asList(searchCriteria.getActivityId()));

				}

				if (role.equals("ROLE_ADMIN") || role.equals("ROLE_CADMIN")) {
					activityPictures = removeDuplicatePictures(activityPictures);
				}

				Map<String, String> activityMap = new HashMap<>();
				if (!activityPictures.isEmpty()) {
					activityMap = getActivityMap(activityPictures);

				}

				List<ImageDTO> images = CommonUtils.convertActivityPicturesToImageDTO(activityPictures,
						ImageType.EMPLOYEE_UPLOAD.getImageType(), activityMap);
				ImageResponse imageResponse = CommonUtils.getPaginationDetailsForActivityPiccture(pageNo, pageSize,
						activityPictures.size());
				List<List<ImageDTO>> imageDTOs = CommonUtils.batchesOfList(images, pageSize);

				imageResponse.setImages(imageDTOs.isEmpty() ? Collections.emptyList() : imageDTOs.get(pageNo - 1));

				return imageResponse;
			} else {
				throw new EvpException("Activity doesn't exist");
			}
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
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

	@Override
	public List<String> uploadImagesToBanner(MultipartFile[] multipartRequests,Long index) throws EvpException {
		try {

			
			List<BannerPicture> allData=(List<BannerPicture>) bannerPictureRepo.findAll();
			List<Long> indexes=allData.stream().map(BannerPicture::getIndex)
					.collect(Collectors.toList());
			
			checkForIndexDuplicates(index,indexes);
			
			MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
			StringBuilder urlBuilder = new StringBuilder();
			String url = urlBuilder.append(imageStorageUrl).append(apikey).toString();

			List<UploadResponse> uploadResponses = new LinkedList<>();
			List<BannerPicture> bannerPictures = new LinkedList<>();
			
			

			log.info("Uploading Files To Azure Storage");
			List<String> fileNames = uploadBannerFileToAzureStorage(multipartRequests, imageMap, url, parentFolder,
					uploadResponses);

			buildBannerPictures(uploadResponses, bannerPictures, fileNames,index);

			log.info("Saving Activity Pictures");
			bannerPictureRepo.saveAll(bannerPictures);

			return fileNames;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private List<String> uploadBannerFileToAzureStorage(MultipartFile[] multipartRequests,
			MultiValueMap<String, Object> imageMap, String url, String parentFolder2,
			List<UploadResponse> uploadResponses) throws EvpException {

		LocalDate now = LocalDate.now();

		List<String> imageNames = Arrays.stream(multipartRequests).map(m -> m.getOriginalFilename())
				.collect(Collectors.toList());

		Set<String> imageSet = imageNames.stream().distinct().collect(Collectors.toSet());

		if (imageNames.size() > imageSet.size()) {
			throw new EvpException("Same Images with same names couldn't be uploaded");
		}

		try {
			List<BannerPicture> allBannerImages = getBannerPictures();

			List<String> names = allBannerImages.stream().map(BannerPicture::getImageName).collect(Collectors.toList());

			checkForDuplicates(multipartRequests, names);

			if (allBannerImages.size() < 4) {
				Arrays.stream(multipartRequests).forEach(multipartRequest -> {

					String fileName = null;
					HttpHeaders headers = new HttpHeaders();
					headers.setContentType(MediaType.MULTIPART_FORM_DATA);

					fileName = multipartRequest.getOriginalFilename();
					File file = convert(multipartRequest);
					if (Optional.ofNullable(file).isPresent()) {
						imageMap.add("fileData", new FileSystemResource(file));
						imageMap.add("fileContainer", "aplms");

						String fileLocation = getBannerFileLocation(parentFolder, now, fileName);

						String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));

						imageMap.add("fileLoc", fileL);
						HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
								imageMap, headers);

						RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
						restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
						restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
						ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
								request, UploadResponse.class);

						if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
							log.info("Successfully Uploaded image {}", fileName);
							UploadResponse uploadResponse = responseEntity.getBody();
							uploadResponse.setFileLocation(fileLocation);
							uploadResponses.add(uploadResponse);

						}
					} else {
						log.error("Couldn't upload file with name {}", fileName);
					}

				});
			} else {
				log.error("Maximum size is 4 for banner images");
				throw new EvpException("The maximum size of 4 has been reached");
			}
		} catch (EvpException e) {
			throw new EvpException(e.getMessage());

		}
		return imageNames;
	}

	private List<BannerPicture> getBannerPictures() {
		
		return (List<BannerPicture>) bannerPictureRepo.findAll();
	}

	private void buildBannerPictures(List<UploadResponse> uploadResponses, List<BannerPicture> bannerPictures,
			List<String> fileNames,long index) throws EvpException {

		try {

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";
			int count = 0;
			for (UploadResponse uploadResponse : uploadResponses) {

				if (!uploadResponses.isEmpty()) {

					BannerPicture bannerPicture = new BannerPicture();
					bannerPicture.setBannerPictureLocation(uploadResponse.getAssetUrl());
					String fileName = fileNames.get(count);
					count++;
					bannerPicture.setImageName(fileName);
                    bannerPicture.setIndex(index);
					bannerPicture.setUploadedBy(employeeName);
					bannerPicture.setContainerLocation(uploadResponse.getFileLocation());
					bannerPictures.add(bannerPicture);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}

	private String getBannerFileLocation(String parentFolder2, LocalDate now, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		String formattedDate = CommonUtils.formatLocalDateTime(now);

		fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
				.append(PATH_DELIMITER).append("BANNER").append(PATH_DELIMITER).append(fileName).toString();
		return fileLocation;
	}


	private void checkForDuplicates(MultipartFile[] multipartRequests, List<String> allImages) throws EvpException {

		int flag = 0;
		for (MultipartFile multipartRequest : multipartRequests) {

			String fileName = multipartRequest.getOriginalFilename();
			for (String imageName : allImages) {
				if (imageName.equals(fileName)) {
					flag = 1;
				}
			}
		}
		if (flag == 1) {
			throw new EvpException("Duplicate image found. Check for duplicates");
		}

	}
	
	private void checkForVideoDuplicates(String videoURL, List<String> allurls) throws EvpException {

		int flag = 0;
		for (String url : allurls) {
	        if (videoURL.equals(url)) {  
	            flag = 1;
	            break;  
	        }
	    }
		
		if (flag == 1) {
			throw new EvpException("Duplicate video found. Check for duplicates");
		}

	}
	
	private void checkForIndexDuplicates(Long index, List<Long> allindexs) throws EvpException {
		
		int flag = 0;
		for (Long ind : allindexs) {
	        if (index.equals(ind)) {  
	            flag = 1;
	            break;  
	        }
	    }
		
		if (flag == 1) {
			throw new EvpException("Sequenec already occupied. Please enter another sequence");
		}

	}

	@Override
	public Leaders uploadDataToLeadersTalk(String leaderName, String designation,
			String url,long index) throws EvpException {
		try {
			List<Leaders> allLeaders=(List<Leaders>) leadersRepo.findAll();
			List<Long> indexes=allLeaders.stream().map(Leaders::getIndex)
					.collect(Collectors.toList());
			
			List<String> allUrls=allLeaders.stream().map(Leaders::getUrl)
					.collect(Collectors.toList());
			
			checkForVideoDuplicates(url, allUrls);
			checkForIndexDuplicates(index,indexes);
			Leaders leader = new Leaders();
	
			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";
			
			leader.setUploadedBy(employeeName);
			leader.setIndex(index);
			leader.setLeaderName(leaderName);
			leader.setDesignation(designation);
			leader.setUrl(url);
				
			log.info("Saving Leaders Data");
			leadersRepo.save(leader);

			return leader;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

//	private String uploadLeadersImageToAzureStorage(String leaderName,
//			String designation, String description, MultiValueMap<String, Object> imageMap, String url,
//			String parentFolder2, List<UploadResponse> uploadResponses) throws EvpException {
//
//		LocalDate now = LocalDate.now();
//
//		String imageName = multipartRequests[0].getOriginalFilename();
//
//		try {
//			List<Leaders> allLeaders = (List<Leaders>) leadersRepo.findAll();
//
//			List<String> names = allLeaders.stream().map(Leaders::getImageName).collect(Collectors.toList());
//
//			checkForDuplicates(multipartRequests, names);
//
//			if (allLeaders.size() < 3) {
//				Arrays.stream(multipartRequests).forEach(multipartRequest -> {
//
//					String fileName = null;
//					HttpHeaders headers = new HttpHeaders();
//					headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//					fileName = multipartRequest.getOriginalFilename();
//					File file = convert(multipartRequest);
//					if (Optional.ofNullable(file).isPresent()) {
//						imageMap.add("fileData", new FileSystemResource(file));
//						imageMap.add("fileContainer", "aplms");
//
//						String fileLocation = getLeaderImageLocation(parentFolder, now, fileName);
//
//						String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));
//
//						imageMap.add("fileLoc", fileL);
//						HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
//								imageMap, headers);
//
//						RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
//						restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
//						restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//						ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST,
//								request, UploadResponse.class);
//
//						if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
//							log.info("Successfully Uploaded image {}", fileName);
//							UploadResponse uploadResponse = responseEntity.getBody();
//							uploadResponse.setFileLocation(fileLocation);
//							uploadResponses.add(uploadResponse);
//
//						}
//					} else {
//						log.error("Couldn't upload file with name {}", fileName);
//					}
//
//				});
//			} else {
//				log.error("Maximum size is 3 for leaders talk");
//				throw new EvpException("The maximum size of 3 has been reached.");
//			}
//		} catch (EvpException e) {
//			throw new EvpException(e.getMessage());
//
//		}
//		return imageName;
//	}

	private String getLeaderImageLocation(String parentFolder2, LocalDate now, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		String formattedDate = CommonUtils.formatLocalDateTime(now);

		fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
				.append(PATH_DELIMITER).append("LEADER").append(PATH_DELIMITER).append(fileName).toString();
		return fileLocation;
	}

//	private void buildLeaderData(List<UploadResponse> uploadResponses, String leadersName, String designation,
//			String description, List<Leaders> leaders,Long index) throws EvpException {
//
//		try {
//
//			String username = CommonUtils.getUsername(request, jwtUtil);
//			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
//			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
//			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";
//
//			Optional<UploadResponse> uploadOpt = uploadResponses.stream().findFirst();
//			if (uploadOpt.isPresent()) {
//				UploadResponse uploadResponse = uploadOpt.get();
//
//				Leaders leader = new Leaders();
//				leader.setLeaderName(leadersName);
//				leader.setLeaderPictureLocation("");
//				leader.setDesignation(designation);
//				leader.setDescription(description);
//				leader.setUploadedBy(employeeName);
//				leader.setContainerLocation("");
//				leader.setIndex(index);
//				leaders.add(leader);
//			}
//
//		} catch (Exception e) {
//			log.error(e.getMessage());
//			throw new EvpException(e.getMessage());
//		}
//
//	}


	@Override
	public List<String> uploadPartnersLogo(MultipartFile[] multipartRequests,Long index) throws EvpException {
		try {
			
			
			List<PartnersLogo> allData=(List<PartnersLogo>) partnersLogoRepo.findAll();
			List<Long> indexes=allData.stream().map(PartnersLogo::getIndex)
					.collect(Collectors.toList());
			
			checkForIndexDuplicates(index,indexes);

			MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
			StringBuilder urlBuilder = new StringBuilder();
			String url = urlBuilder.append(imageStorageUrl).append(apikey).toString();

			List<UploadResponse> uploadResponses = new LinkedList<>();
			List<PartnersLogo> partnersLogo = new LinkedList<>();

			log.info("Uploading Files To Azure Storage");
			List<String> fileNames = uploadPartnersLogoToAzureStorage(multipartRequests, imageMap, url, parentFolder,
					uploadResponses);

			buildPartnersLogo(uploadResponses, partnersLogo, fileNames,index);

			log.info("Saving Partners Logo");
			partnersLogoRepo.saveAll(partnersLogo);

			return fileNames;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private List<String> uploadPartnersLogoToAzureStorage(MultipartFile[] multipartRequests,
			MultiValueMap<String, Object> imageMap, String url, String parentFolder2,
			List<UploadResponse> uploadResponses) throws EvpException {

		LocalDate now = LocalDate.now();

		List<String> imageNames = Arrays.stream(multipartRequests).map(m -> m.getOriginalFilename())
				.collect(Collectors.toList());

		Set<String> imageSet = imageNames.stream().distinct().collect(Collectors.toSet());

		if (imageNames.size() > imageSet.size()) {
			throw new EvpException("Same Images with same names couldn't be uploaded");
		}

		try {
			List<PartnersLogo> allPartnersLogo = getPartnersLogo();

			List<String> names = allPartnersLogo.stream().map(PartnersLogo::getImageName).collect(Collectors.toList());

			checkForDuplicates(multipartRequests, names);
			Arrays.stream(multipartRequests).forEach(multipartRequest -> {

				String fileName = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				fileName = multipartRequest.getOriginalFilename();
				File file = convert(multipartRequest);
				if (Optional.ofNullable(file).isPresent()) {
					imageMap.add("fileData", new FileSystemResource(file));
					imageMap.add("fileContainer", "aplms");

					String fileLocation = getPartnersLogoFileLocation(parentFolder, now, fileName);

					String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));

					imageMap.add("fileLoc", fileL);
					HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
							imageMap, headers);

					RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
					restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
					restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
					ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
							UploadResponse.class);

					if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
						log.info("Successfully Uploaded image {}", fileName);
						UploadResponse uploadResponse = responseEntity.getBody();
						uploadResponse.setFileLocation(fileLocation);
						uploadResponses.add(uploadResponse);

					}
				} else {
					log.error("Couldn't upload file with name {}", fileName);
				}

			});
		} catch (EvpException e) {
			throw new EvpException(e.getMessage());

		}
		return imageNames;
	}

	private String getPartnersLogoFileLocation(String parentFolder2, LocalDate now, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		String formattedDate = CommonUtils.formatLocalDateTime(now);

		fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
				.append(PATH_DELIMITER).append("PARTNERS_LOGO").append(PATH_DELIMITER).append(fileName).toString();
		return fileLocation;
	}

	private void buildPartnersLogo(List<UploadResponse> uploadResponses, List<PartnersLogo> partnersLogos,
			List<String> fileNames,Long index) throws EvpException {

		try {

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";
			int count = 0;
			for (UploadResponse uploadResponse : uploadResponses) {

				if (!uploadResponses.isEmpty()) {

					PartnersLogo partnersLogo = new PartnersLogo();
					partnersLogo.setPartnersLogoLocation(uploadResponse.getAssetUrl());
					String fileName = fileNames.get(count);
					count++;
					partnersLogo.setImageName(fileName);

					partnersLogo.setUploadedBy(employeeName);
					partnersLogo.setContainerLocation(uploadResponse.getFileLocation());
					partnersLogo.setIndex(index);
					partnersLogos.add(partnersLogo);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}

	
	public List<PartnersLogo> getPartnersLogo() throws EvpException {
		List<PartnersLogo> a = (List<PartnersLogo>) partnersLogoRepo.findAll();
		return a;
	}

	@Override
	public TestimonialData uploadDataToTestimonial(MultipartFile[] multipartRequests, String testimonialName,
			String designationAndLocation, String description,Long index) throws EvpException {
		try {
			if (multipartRequests.length > 1) {
				throw new EvpException("Please upload one image for a Testimonial.");
			}

			
			List<TestimonialData> allData=(List<TestimonialData>) testimonialDataRepo.findAll();
			List<Long> indexes=allData.stream().map(TestimonialData::getIndex)
					.collect(Collectors.toList());
			
			checkForIndexDuplicates(index,indexes);
			
			MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
			StringBuilder urlBuilder = new StringBuilder();
			String url = urlBuilder.append(imageStorageUrl).append(apikey).toString();

			List<UploadResponse> uploadResponses = new LinkedList<>();
			List<TestimonialData> testimonialDatas = new LinkedList<>();

			log.info("Uploading Files To Azure Storage");
			String fileName = uploadTestimonialDataToAzureStorage(multipartRequests, testimonialName,
					designationAndLocation, description, imageMap, url, parentFolder, uploadResponses);

			buildTestimonialData(uploadResponses, testimonialName, designationAndLocation, description,
					testimonialDatas, fileName,index);

			TestimonialData testimonialData = testimonialDatas.get(0);

			log.info("Saving Testimonial Data");
			testimonialDataRepo.saveAll(testimonialDatas);

			return testimonialData;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private String uploadTestimonialDataToAzureStorage(MultipartFile[] multipartRequests, String testimonialName,
			String designationAndLocation, String description, MultiValueMap<String, Object> imageMap, String url,
			String parentFolder2, List<UploadResponse> uploadResponses) throws EvpException {

		LocalDate now = LocalDate.now();

		String imageName = multipartRequests[0].getOriginalFilename();

		try {
			List<TestimonialData> allTestimonialData = (List<TestimonialData>) testimonialDataRepo.findAll();

			List<String> names = allTestimonialData.stream().map(TestimonialData::getImageName)
					.collect(Collectors.toList());

			checkForDuplicates(multipartRequests, names);

			Arrays.stream(multipartRequests).forEach(multipartRequest -> {

				String fileName = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				fileName = multipartRequest.getOriginalFilename();
				File file = convert(multipartRequest);
				if (Optional.ofNullable(file).isPresent()) {
					imageMap.add("fileData", new FileSystemResource(file));
					imageMap.add("fileContainer", "aplms");

					String fileLocation = getTestimonialImageLocation(parentFolder, now, fileName);

					String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));

					imageMap.add("fileLoc", fileL);
					HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
							imageMap, headers);

					RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
					restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
					restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
					ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
							UploadResponse.class);

					if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
						log.info("Successfully Uploaded image {}", fileName);
						UploadResponse uploadResponse = responseEntity.getBody();
						uploadResponse.setFileLocation(fileLocation);
						uploadResponses.add(uploadResponse);

					}
				} else {
					log.error("Couldn't upload file with name {}", fileName);
				}

			});

		} catch (EvpException e) {
			throw new EvpException(e.getMessage());

		}
		return imageName;
	}

	private String getTestimonialImageLocation(String parentFolder2, LocalDate now, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		String formattedDate = CommonUtils.formatLocalDateTime(now);

		fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
				.append(PATH_DELIMITER).append("TESTIMONIAL_DATA").append(PATH_DELIMITER).append(fileName).toString();
		return fileLocation;
	}

	private void buildTestimonialData(List<UploadResponse> uploadResponses, String testimonialName,
			String designationAndLocation, String description, List<TestimonialData> testimonialDatas, String fileName,
			Long index)
			throws EvpException {

		try {

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";

			Optional<UploadResponse> uploadOpt = uploadResponses.stream().findFirst();
			if (uploadOpt.isPresent()) {
				UploadResponse uploadResponse = uploadOpt.get();

				TestimonialData testimonialData = new TestimonialData();
				testimonialData.setTestimonialName(testimonialName);
				testimonialData.setTestimonialPictureLocation(uploadResponse.getAssetUrl());
				testimonialData.setImageName(fileName);
				testimonialData.setDesignationAndLocation(designationAndLocation);
				testimonialData.setDescription(description);
				testimonialData.setUploadedBy(employeeName);
				testimonialData.setContainerLocation(uploadResponse.getFileLocation());
				testimonialData.setIndex(index);
				testimonialDatas.add(testimonialData);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}


	@Override
	public Video uploadVideo(String videoURL, String videoName,Long index) throws EvpException {

		try {
			List<Video> allVideo=(List<Video>) videoRepo.findAll();
			List<String> urls=allVideo.stream().map(Video::getVideoURL)
					.collect(Collectors.toList());
			
			checkForVideoDuplicates(videoURL,urls);
			
			List<Video> allData=(List<Video>) videoRepo.findAll();
			List<Long> indexes=allData.stream().map(Video::getIndex)
					.collect(Collectors.toList());
			
			checkForIndexDuplicates(index,indexes);

			List<UploadResponse> uploadResponses = new LinkedList<>();
			List<Video> videos = new LinkedList<>();

			buildVideo(uploadResponses, videos, videoURL,videoName,index);

			Video video = videos.get(0);

			log.info("Saving video");
			videoRepo.saveAll(videos);

			return video;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}
	
	private void buildVideo(List<UploadResponse> uploadResponses, List<Video> videos,String videoURL, String videoName
			,Long index)
			throws EvpException {

		try {

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";

			Video video = new Video();
			video.setVideoName(videoName);
			video.setVideoURL(videoURL);
			video.setUploadedBy(employeeName);
			video.setIndex(index);
			videos.add(video);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}
	}

	@Override
	public VoiceOfChange uploadFilesToVOC(MultipartFile[] multipartRequests, String speaksType, String personName, String designationOrInfo,Long index) throws EvpException {
		try {
			
			List<VoiceOfChange> allData=(List<VoiceOfChange>) vocRepo.findAll();
			List<Long> indexes=allData.stream().map(VoiceOfChange::getIndex)
					.collect(Collectors.toList());
			
			checkForIndexDuplicates(index,indexes);

			MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
			StringBuilder urlBuilder = new StringBuilder();
			String url = urlBuilder.append(imageStorageUrl).append(apikey).toString();

			List<UploadResponse> uploadResponses = new LinkedList<>();
			VoiceOfChange vocData = new VoiceOfChange();

			log.info("Uploading Files To Azure Storage");
			List<String> fileNames = uploadVOCFilesToAzure(multipartRequests, imageMap, url, parentFolder,
					uploadResponses);

			buildVOCData(uploadResponses, vocData, fileNames, speaksType, personName,designationOrInfo,index);

			log.info("Saving Voice of Change Data");
			vocRepo.save(vocData);

			return vocData;
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Unable to Upload");
		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

	private List<String> uploadVOCFilesToAzure(MultipartFile[] multipartRequests,
			MultiValueMap<String, Object> imageMap, String url, String parentFolder,
			List<UploadResponse> uploadResponses) throws EvpException {

		LocalDate now = LocalDate.now();

		List<String> fileNames = Arrays.stream(multipartRequests).map(m -> m.getOriginalFilename())
				.collect(Collectors.toList());

		int audioFlag = 0;
		int imageFlag = 0;
		String[] imageExtensions = { ".jpg", ".jpl", ".jxr", ".tif", ".tiff", ".jpeg", ".png", ".bmp" };
		for (String fileName : fileNames) {
			fileName = fileName.toLowerCase();
			if (Arrays.stream(imageExtensions).anyMatch(fileName::endsWith)) {
				imageFlag++;
			} else if (fileName.endsWith(".mp3")) {
				audioFlag++;
			} else {
				throw new EvpException(
						"Please enter any of .jpg, .jpl, .jxr, .tif, .tiff, .jpeg, .png, .bmp format for Image file "
								+ "and .mp3 for audio file");
			}
		}

		Set<String> imageSet = fileNames.stream().distinct().collect(Collectors.toSet());

		try {

			if (fileNames.size() > 2 || imageFlag != 1 || audioFlag != 1) {
				throw new EvpException("Please Upload only one Image and a Audio file");
			}

			if (fileNames.size() > imageSet.size()) {
				throw new EvpException("Same Images with same names couldn't be uploaded");
			}
             List<VoiceOfChange> allVoiceOfChangeData=(List<VoiceOfChange>) vocRepo.findAll();
             List<String> names=allVoiceOfChangeData.stream().map(VoiceOfChange::getImageName)
            		 .collect(Collectors.toList());
             
             checkForDuplicates(multipartRequests,names);
             
			 Arrays.stream(multipartRequests).forEach(multipartRequest -> {

				String fileName = null;
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);

				fileName = multipartRequest.getOriginalFilename();
				File file = convert(multipartRequest);
				if (Optional.ofNullable(file).isPresent()) {
					imageMap.add("fileData", new FileSystemResource(file));
					imageMap.add("fileContainer", "aplms");

					String fileLocation = getVOCFileLocation(parentFolder, now, fileName);

					String fileL = fileLocation.substring(0, fileLocation.indexOf('.'));

					imageMap.add("fileLoc", fileL);
					HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
							imageMap, headers);
					
					RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);
					restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
					restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
					ResponseEntity<UploadResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, request,
							UploadResponse.class);
					imageMap.clear();
					
					if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
						log.info("Successfully Uploaded image {}", fileName);
						UploadResponse uploadResponse = responseEntity.getBody();
						uploadResponse.setFileLocation(fileLocation);
						uploadResponses.add(uploadResponse);

					}
				} else {
					log.error("Couldn't upload file with name {}", fileName);
				}

			});

		} catch (EvpException e) {
			throw new EvpException(e.getMessage());

		}
		return fileNames;
	}

	private String getVOCFileLocation(String parentFolder, LocalDate now, String fileName) {
		String fileLocation = null;

		StringBuilder folderBuilder = new StringBuilder();

		String formattedDate = CommonUtils.formatLocalDateTime(now);

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		if(extension.equals("mp3"))
		{
			fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
					.append(PATH_DELIMITER).append("VOC_AUDIO").append(PATH_DELIMITER).append(fileName).toString();
		}else
		{
			fileLocation = folderBuilder.append(parentFolder).append(PATH_DELIMITER).append(formattedDate)
					.append(PATH_DELIMITER).append("VOC_IMAGE").append(PATH_DELIMITER).append(fileName).toString();
		}
		
		return fileLocation;
	}
	
	private void buildVOCData(List<UploadResponse> uploadResponses,VoiceOfChange vocData,
			List<String> fileNames,String speaksType, String personName, String designationOrInfo,Long index) throws EvpException {

		try {

			String username = CommonUtils.getUsername(request, jwtUtil);
			String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
			Optional<Employee> employee = employeeRepository.findByEmployeeId(employeeId);
			String employeeName = employee.isPresent() ? employee.get().getEmployeeName() : "";

			vocData.setDesignationOrInfo(designationOrInfo);
			vocData.setPersonName(personName);
			vocData.setSpeaksType(speaksType);	
			vocData.setUploadedBy(employeeName);
			vocData.setIndex(index);

			List<String> containerLocations=new ArrayList<>();
			for (UploadResponse uploadResponse : uploadResponses) {

				if (!uploadResponses.isEmpty()) {
					for(String fileName:fileNames)
					{
						if(uploadResponse.getAssetUrl().endsWith(".mp3") && fileName.endsWith(".mp3"))
						{
							vocData.setAudioName(fileName);
							vocData.setVocAudioLocation(uploadResponse.getAssetUrl());
							containerLocations.add(uploadResponse.getFileLocation());
						}else if(!uploadResponse.getAssetUrl().endsWith(".mp3") && !fileName.endsWith(".mp3"))
						{
							vocData.setImageName(fileName);
							vocData.setVocPictureLocation(uploadResponse.getAssetUrl());
							containerLocations.add(uploadResponse.getFileLocation());
						}
					}
					
				}
			}
			String joinedString = String.join(",", containerLocations);
			vocData.setContainerLocation(joinedString);

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}

	@Override
	public Map<String, Object> commonGetForLandingPge() throws EvpException {
		Map<String, Object> dataMap=new HashMap<>();
		try {
		dataMap.put("banner", bannerPictureRepo.findAllByOrderByIndex());
		dataMap.put("leadersTalk", leadersRepo.findAllByOrderByIndex());
		dataMap.put("voiceOfChange", vocRepo.findAllByOrderByIndex());
		dataMap.put("video", videoRepo.findAllByOrderByIndex());
		dataMap.put("testimonial", testimonialDataRepo.findAllByOrderByIndex());
		dataMap.put("partners", partnersLogoRepo.findAllByOrderByIndex());}
		catch(Exception e) {
			log.error(e.getMessage());
		}
		return dataMap;
	}
	@Override
	public void deleteBannerImages(String imageName) throws EvpException {
		
		try {
			Optional<BannerPicture> bannerPicture = bannerPictureRepo.findByBannerImageName(imageName);
			  if(bannerPicture.isPresent()) {
		        bannerPictureRepo.delete(bannerPicture.get());
			}
		        else {
				throw new EvpException("No Image present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	@Override
	public void deleteLeadersData(String imageName) throws EvpException {
		
		try {
			Optional<Leaders> leadersTalk = leadersRepo.findByLeaderImageName(imageName);
			  if(leadersTalk.isPresent()) {
		        leadersRepo.delete(leadersTalk.get());
			}
		        else {
				throw new EvpException("No data present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	@Override
	public void deleteVoiceOfChangeData(String imageName) throws EvpException {
		
		try {
			Optional<VoiceOfChange> voc = vocRepo.findByVoiceOfChangeImageName(imageName);
			  if(voc.isPresent()) {
		        vocRepo.delete(voc.get());
			}
		        else {
				throw new EvpException("No data present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	
	@Override
	public void deleteTestimonialData(String imageName) throws EvpException {
		
		try {
			Optional<TestimonialData> testimonial = testimonialDataRepo.findByTestimonialImageName(imageName);
			  if(testimonial.isPresent()) {
		        testimonialDataRepo.delete(testimonial.get());
			}
		        else {
				throw new EvpException("No data present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	
	@Override
	public void deleteVideo(String videoURL) throws EvpException {
		
		try {
			Optional<Video> vid = videoRepo.findByVideoURL(videoURL);
			  if(vid.isPresent()) {
		        videoRepo.delete(vid.get());
			}
		        else {
				throw new EvpException("No video present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	@Override
	public void deletePartnersLogo(String imageName) throws EvpException {
		
		try {
			Optional<PartnersLogo> logo = partnersLogoRepo.findByPartnersLogoImageName(imageName);
			  if(logo.isPresent()) {
		        partnersLogoRepo.delete(logo.get());
			}
		        else {
				throw new EvpException("No logo present to delete");
			}

		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

	}
	
	
}
