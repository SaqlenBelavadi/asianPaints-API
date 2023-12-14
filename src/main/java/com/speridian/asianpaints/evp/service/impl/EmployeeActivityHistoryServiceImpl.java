package com.speridian.asianpaints.evp.service.impl;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryDTO;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.EmailService;
import com.speridian.asianpaints.evp.service.EmployeeActivityHistoryService;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeActivityHistoryServiceImpl implements EmployeeActivityHistoryService {

	@Autowired
	private EmployeeActivityHistoryRepository employeeActivityHistoryRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private EvpLovService evpLovService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ActivityService activityService;

	@Override
	public void updateEmployeeActivityHistory(EmployeeActivityHistoryDTO employeeActivityHistoryDTO)
			throws EvpException {

		try {
			Employee employee = null;
			boolean exisitngActivityHistory = false;
			EmployeeActivityHistory employeeActivityHistory = new EmployeeActivityHistory();
			String employeeId = employeeActivityHistoryDTO.getEmployeeId();
			String activityUUID = employeeActivityHistoryDTO.getActivityUuid();
			String departmentName=null;

			log.info("Retriving Existing Activity History for Employee and Activity");
			EmployeeActivityHistory exisitngEmployeeActivityHistory = employeeActivityHistoryRepository
					.findByEmployeeIdAndActivityUUID(employeeId, activityUUID);

			if (Optional.ofNullable(exisitngEmployeeActivityHistory).isPresent()) {
				if (exisitngEmployeeActivityHistory.getEmployeeActivityStatus().getStatus()
						.equals(employeeActivityHistoryDTO.getEnrolledOrParticipate())) {
					log.error("Employee enrolled or participate on the same activity again");
					throw new EvpException("Employee enrolled or pacrticipate on the same activity again");
				} else {
					log.info("Activity History for employee and Activity already exists");
					exisitngActivityHistory = true;
					employeeActivityHistory = exisitngEmployeeActivityHistory;
				}
			}

			if (employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.ENROLLED.getStatus())) {
				employeeActivityHistory.setApprovedByAdmin(false);
				employeeActivityHistory.setEmployeeActivityStatus(EmployeeActivityStatus.ENROLLED);
			} else if (employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.PARTICIPATED.getStatus())) {
				employeeActivityHistory.setApprovedByAdmin(true);
				employeeActivityHistory.setEmployeeActivityStatus(EmployeeActivityStatus.PARTICIPATED);
			}else if (employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.FEEDBACK.getStatus())) {
				employeeActivityHistory.setApprovedByAdmin(true);
				employeeActivityHistory.setEmployeeActivityStatus(EmployeeActivityStatus.FEEDBACK);
			}
			
			else {
				log.error("Employee Activity Status not defined");
				throw new EvpException("Employee Activity Status not defined");
			}

			Activity activity = activityRepository.findByActivityUUID(activityUUID);
			if (!exisitngActivityHistory) {
				Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
				if (employeeOpt.isPresent()) {
					employee = employeeOpt.get();
					employeeActivityHistory.setEmployeeId(employee.getEmployeeId());
					employeeActivityHistory.setEmployeeName(employee.getEmployeeName());
					departmentName=employeeOpt.get().getDepartmentName();
					employeeActivityHistory.setDepartmentName(departmentName);
				} else {
					log.error("Employee doesn't exist");
					throw new EvpException("Employee doesn't exist");
				}
				
				if (Optional.ofNullable(activity).isPresent()) {

					if (!activity.isPublished()) {
						throw new EvpException("Activity is not published");
					}

					employeeActivityHistory.setActivityLocation(employeeActivityHistoryDTO.getLocation());

					Long tagId = activity.getTagId();
					String tagName = CommonUtils.getLovMapWithIdKey(evpLovService.getTagLovMap()).get(tagId);
					employeeActivityHistory.setActivityTag(tagName);

					Long themeId = activity.getThemeNameId();
					String themeName = CommonUtils.getLovMapWithIdKey(evpLovService.getThemeLovMap()).get(themeId);
					employeeActivityHistory.setActivityTheme(themeName);

					Long modeId = activity.getModeOfParticipationId();
					String mode = CommonUtils.getLovMapWithIdKey(evpLovService.getModeLovMap()).get(modeId);
					employeeActivityHistory.setMode(mode);

					employeeActivityHistory.setActivityUUID(activityUUID);
					employeeActivityHistory.setActivityName(activity.getActivityId());
					employeeActivityHistory.setActivityPhysicalName(activity.getActivityName());
					String timeRequired = CommonUtils.getTimeRequired(activity).toString();

					employeeActivityHistory.setParticipationHours(timeRequired);

					employeeActivityHistory.setEndDate(
							Optional.ofNullable(activity.getEndDate()).isPresent() ? activity.getEndDate().toLocalDate()
									: null);
					
					employeeActivityHistory.setStartDate(
							Optional.ofNullable(activity.getStartDate()).isPresent() ? activity.getStartDate().toLocalDate()
									: null);

				} else {
					log.error("No Activity is present with given UUID");
					throw new EvpException("No Activity is present with given UUID");
				}
			}
			
			employeeActivityHistory.setRejectedByAdmin(false);
			log.info("Creating or Updating Employee Activity History");
			employeeActivityHistoryRepository.save(employeeActivityHistory);

			/*
			 * email
			 * 
			 */
//			

			if (employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.ENROLLED.getStatus())) {
				sendEmployeeParticipateEmail(activity,employeeActivityHistoryDTO,EmployeeActivityStatus.ENROLLED);
			}
			if (employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.FEEDBACK.getStatus()) || employeeActivityHistoryDTO.getEnrolledOrParticipate()
					.equals(EmployeeActivityStatus.PARTICIPATED.getStatus())) {
				sendEmployeeParticipateEmail(activity,employeeActivityHistoryDTO,EmployeeActivityStatus.PARTICIPATED);
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

	private void sendEmployeeParticipateEmail(Activity activity,EmployeeActivityHistoryDTO employeeActivityHistoryDTO,EmployeeActivityStatus employeeActivityStatus) {
		Executors.newSingleThreadExecutor().submit(()->{
			FileSystemResource fileSystemResource=null;
		log.info("sendEmployeeParticipateEmail");
		Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeActivityHistoryDTO.getEmployeeId());
		if (employeeOpt.isPresent()) {
			Employee e = employeeOpt.get();
			if (Optional.ofNullable(e.getEmail()).isPresent()) {

			EmailTemplateData emailTemplateData= EmailTemplateData.builder().employeeName(e.getEmployeeName()).activityName(activity.getActivityName())
					.activityLink(employeeActivityHistoryDTO.getActivityUrl())
					.build();
			
			EmailType emailType=null;
			
			switch (employeeActivityStatus) {
			case ENROLLED:
				emailType=EmailType.ENROLL_ACTIVITY;
				break;
			case PARTICIPATED:
			case FEEDBACK:
				emailType=EmailType.CONFIRM_PARTICIPATION;
				
				try {
					String path=activityService.downloadCertificate(e.getEmployeeName(), activity.getActivityName(),e.getEmployeeId());
					File file=new File(path);
					fileSystemResource=new FileSystemResource(file);
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
		});

	}

	private void sendEmployeeEnrolsEmail(String url) {
		Executors.newSingleThreadExecutor().submit(()->{
		log.info("sendEmployeeEnrolsEmail");
		String username = CommonUtils.getUsername(request, jwtUtil);
		log.info("username : ".concat(username));
		String employeeId = CommonUtils.getEmployeeIdFromUsername(username);
		log.info("employeeId : ".concat(employeeId));
		Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
		if (employeeOpt.isPresent()) {
			Employee e = employeeOpt.get();
			if (Optional.ofNullable(e.getEmail()).isPresent()) {


			} else {
				log.error("email not existing for login user ");
			}

		} else {
			log.error("User is not valid");
		}
		});

	}

}
