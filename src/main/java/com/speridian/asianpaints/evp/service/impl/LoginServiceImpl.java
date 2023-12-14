package com.speridian.asianpaints.evp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.LoginResponse;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;
import com.speridian.asianpaints.evp.entity.UserLogin;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.entity.EvpLocationDivision;
import com.speridian.asianpaints.evp.master.entity.EvpLov;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.master.repository.EvpLocationDivisionRepository;
import com.speridian.asianpaints.evp.master.repository.EvpLovRepository;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.service.LoginService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;
import com.speridian.asianpaints.evp.transactional.repository.EmployeeActivityHistoryRepository;
import com.speridian.asianpaints.evp.transactional.repository.UserLoginRepository;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.JwtTokenUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

	@Value("${evp.adlogin.url}")
	private String adLoginUrl;

	@Value("${jwt.jjwt.expiration}")
	private String expirationTime;

	@Value("${jwt.jjwt.jwtRefreshExpirationMs}")
	private String refreshTokenExpirationTime;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserLoginRepository userLoginRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private JwtTokenUtil jwtUtil;

	@Autowired
	private EmployeeActivityHistoryRepository employeeActivityHistoryRepository;
	
	@Autowired
	private EvpLocationDivisionRepository evpLocationDivisionRepository;
	
	@Autowired
	private EvpLovService evpLovService;
	
	@Autowired
	private EvpLovRepository evpLovRepository;

	@Override
	public LoginResponse login(LoginResponse loginResponse) throws EvpException {

		try {
			String username = loginResponse.getUsername();
			RestTemplate restTemplate = CommonUtils.buildRestTemplate(true, username, loginResponse.getPassword());
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<LoginResponse> httpEntity = new HttpEntity<LoginResponse>(loginResponse, httpHeaders);
			log.info("Authenticating user with username " + username + " using ad login url " + adLoginUrl);
			ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(adLoginUrl, HttpMethod.POST,
					httpEntity, LoginResponse.class);
			if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {

				loginResponse = buildSuccessLoginResponse(username, responseEntity);
			} else {
				log.error(responseEntity.getStatusCode() + " " + responseEntity.getBody().toString());
			}

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}

		catch (HttpClientErrorException e) {
			log.error(e.getMessage());
			throw new EvpException("Please enter correct username and password.");
		} catch (Exception e) {

			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return loginResponse;
	}

	@Override
	public LoginResponse refreshToken(LoginResponse loginResponse) throws EvpException {

		try {
			UserLogin existingUser = null;
			String refreshToken = loginResponse.getRefreshToken();
			String username = loginResponse.getUsername();

			Optional<UserLogin> userLoginOpt = userLoginRepository.findByUsername(username);
			if (userLoginOpt.isPresent()) {
				existingUser = userLoginOpt.get();
				if (refreshToken.equals(existingUser.getRefreshToken())
						&& existingUser.getRefreshTokenExpiryTime().isAfter(LocalDateTime.now())) {
					log.info("Refresh token is valid");
					Employee employee = validateUser(username);
					String roleAssigned =loginResponse.getAssignedRole();
					
					
					String location = validateUserLocationAndGet(employee);

					loginResponse = setLoginResponseForRefreshToken(username, loginResponse, employee, roleAssigned);

					if (employee.getRole().contains("ADMIN") || employee.getRole().contains("EMPLOYEE")) {
						loginResponse.setDefaultLocation(location);
					} else {
						Optional<String> locationOpt= evpLovService.getLocationLovMap().entrySet().stream().filter(l->l.getKey().equalsIgnoreCase("Head Office"))
								.map(entry->entry.getKey())
								.findFirst();
								
								if(locationOpt.isPresent()) {
									loginResponse.setDefaultLocation(locationOpt.get());
								}
					}

					existingUser = updateUserLogin(loginResponse, userLoginOpt);
					log.info("Saving Or Updating User Login");
					userLoginRepository.save(existingUser);

				} else {
					log.error("Refresh token is not valid");
					throw new EvpException("Refresh token is not valid");
				}

			} else {
				log.info("User Login doesn't exist for the user " + username);

				throw new EvpException("User Login doesn't exist for the user " + username);
			}

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return loginResponse;
	}

	private LoginResponse buildSuccessLoginResponse(String username, ResponseEntity<LoginResponse> responseEntity)
			throws EvpException {
		LoginResponse loginResponse;
		UserLogin existingUser;
		log.info("Authentication Successful");

		Employee employee = validateUser(username);
		
		String location = validateUserLocationAndGet(employee);
		
		loginResponse = responseEntity.getBody();
		loginResponse.setUsername(username);
		setLoginResponse(username, loginResponse, employee, employee.getRole());

		if (!Optional.ofNullable(employee.getRole()).isPresent()) {
			log.error("Please assign role for log in");
			throw new EvpException("Please assign role for log in");
		}

		if (employee.getRole().contains("ROLE_ADMIN") || employee.getRole().contains("ROLE_EMPLOYEE")) {
			loginResponse.setDefaultLocation(location);
		} else {
			Optional<String> locationOpt= evpLovService.getLocationLovMap().entrySet().stream().filter(l->l.getKey().equalsIgnoreCase("Head Office"))
			.map(entry->entry.getKey())
			.findFirst();
			
			if(locationOpt.isPresent()) {
				loginResponse.setDefaultLocation(locationOpt.get());
			}
			
			
		}
		Optional<UserLogin> userLoginOpt = userLoginRepository.findByUsername(username);
		existingUser = updateUserLogin(loginResponse, userLoginOpt);
		log.info("Saving Or Updating User Login");
		userLoginRepository.save(existingUser);

		String employeeId = employee.getEmployeeId();
		if (employee.getRole().contains("EMPLOYEE")) {
			getActivityCountsForEmployee(loginResponse, employeeId);
		}

		return loginResponse;
	}

	@Override
	public  String validateUserLocationAndGet(Employee employee) throws EvpException {
		String location=null;
		
		String locationName=employee.getLocationName();
		
		String divisionName=employee.getDivisionName();
		
		List<EvpLocationDivision> evpLocationDivisions= (List<EvpLocationDivision>) evpLocationDivisionRepository.findAll();
		
		Optional<EvpLocationDivision> evpLocationDivisionOpt= evpLocationDivisions.stream().filter(evpLocationDivision->evpLocationDivision.getCode().equalsIgnoreCase(locationName) || evpLocationDivision.getLocation().equalsIgnoreCase(divisionName)).findFirst();
		
		if(!evpLocationDivisionOpt.isPresent()) {
			log.info("User's Location doesn't exist");
			throw new EvpException("User is not allowed to Log in. Contact Administrator");
		}else {
			EvpLocationDivision evpLocationDivision= evpLocationDivisionOpt.get();
			String locationDisplayValue=evpLocationDivision.getLocation();
			if(Optional.ofNullable(evpLovService.getLocationLovMap().get(locationDisplayValue)).isPresent()) {
				Optional<String> locationOpt= evpLovService.getLocationLovMap().entrySet().stream().filter(entry->entry.getKey().equalsIgnoreCase(locationDisplayValue)).map(entry->entry.getKey()).findFirst();
			if(locationOpt.isPresent()) {
				location=locationOpt.get();
			}
			}else {
				log.info("User's Location doesn't exist in LOV");
				throw new EvpException("User is not allowed to Log in. Contact Administrator");
			}
		}
		return location;
	}

	private void getActivityCountsForEmployee(LoginResponse loginResponse, String employeeId) {
		List<EmployeeActivityHistory> employeeActivityHistory = employeeActivityHistoryRepository
				.findByEmployeeId(employeeId);

		if (Optional.ofNullable(employeeActivityHistory).isPresent() && employeeActivityHistory.size() > 0) {
			
			LocalDate currentDate=LocalDate.now();
			
			employeeActivityHistory=employeeActivityHistory.stream()
					.filter(activity -> activity.getStartDate()!=null)
					.filter(p -> (p.getEndDate().compareTo(currentDate)) < 0 || (p.getStartDate().compareTo(currentDate) <= 0 && p.getEndDate().compareTo(currentDate) >= 0) )
					.filter(p->p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.PARTICIPATED)
					|| p.getEmployeeActivityStatus().equals(EmployeeActivityStatus.FEEDBACK))
					.filter(p->!p.isRejectedByAdmin())
					.collect(Collectors.toList());
			
			
			
			AtomicInteger h=new AtomicInteger(0);
			AtomicInteger m=new AtomicInteger(0);
			int noOfActivities = employeeActivityHistory.size();
			List<String> participationHours = employeeActivityHistory.stream()
					.map(EmployeeActivityHistory::getParticipationHours).collect(Collectors.toList());
			participationHours.forEach(p->{
				Integer hours=Integer.parseInt(p.split(" ")[0]);
				Integer minutes=Integer.parseInt(p.split(" ")[2]);
				h.addAndGet(hours);
				m.addAndGet(minutes);
				if(m.get()>=60) {
					h.addAndGet(1);
					m.set(m.get()-60);
				}
				
			});
			String minutesPrefix="";
			if(m.get()>10) {
				minutesPrefix=String.valueOf(m.get());
			}else if(m.get()!=0){
				minutesPrefix="0"+String.valueOf(m.get());
			}
			String totalHoursParticipated=h.get()+"."+minutesPrefix;
			loginResponse.setTotalActivityParticipated(noOfActivities);
			loginResponse.setTotalHoursParticipated(totalHoursParticipated);
		}
	}

	private Employee validateUser(String username) throws EvpException {
		String employeeId = CommonUtils.getEmployeeIdFromUsername(username);

		Optional<Employee> employeeOpt = employeeRepository.findByEmployeeId(employeeId);

		if (employeeOpt.isPresent()) {
			return employeeOpt.get();
		} else {
			throw new EvpException("User is not valid");
		}

	}

	private LoginResponse setLoginResponse(String username, LoginResponse loginResponse, Employee employee,
			String originalRole) {
		String accessToken = jwtTokenUtil.generateToken(username, employee.getRole());
		String refreshToken = jwtTokenUtil.generateRefreshToken();
		loginResponse.setAccessToken(accessToken);
		loginResponse.setRefreshToken(refreshToken);
		LocalDateTime jwtExpirationTime = LocalDateTime.now().plus(Long.parseLong(expirationTime), ChronoUnit.SECONDS);
		LocalDateTime refreshExpirationTime = LocalDateTime.now().plus(Long.parseLong(refreshTokenExpirationTime),
				ChronoUnit.SECONDS);
		loginResponse.setRefreshTokenExpiryTime(refreshExpirationTime);
		loginResponse.setAccessExpiryTime(jwtExpirationTime);
		loginResponse.setEmployeecode(employee.getEmployeeId());
		loginResponse.setOfficialMobile(employee.getOfficialMobile());
		loginResponse.setPersonalMobile(employee.getPersonalMobile());
		loginResponse.setEmail(employee.getEmail());
		loginResponse.setOriginalRole(originalRole);
		loginResponse.setName(employee.getEmployeeName());
		
		List<EvpLov> evpLovs= evpLovRepository.findByLovCategory("CADMIN");
		Map<String, String> map= evpLovs.stream().collect(Collectors.toMap(EvpLov::getLovDisplayName,EvpLov::getLovValue));
		String email=map.get("CADMINEMAIL");
		String phoneNumber=map.get("CADMINPHONE");
		loginResponse.setEmailId(email);
		loginResponse.setCentralPhoneNumber(phoneNumber);
		return loginResponse;
	}
	
	private LoginResponse setLoginResponseForRefreshToken(String username, LoginResponse loginResponse, Employee employee,
			String originalRole) {
		String accessToken = jwtTokenUtil.generateToken(username, originalRole);
		String refreshToken = jwtTokenUtil.generateRefreshToken();
		loginResponse.setAccessToken(accessToken);
		loginResponse.setRefreshToken(refreshToken);
		LocalDateTime jwtExpirationTime = LocalDateTime.now().plus(Long.parseLong(expirationTime), ChronoUnit.SECONDS);
		LocalDateTime refreshExpirationTime = LocalDateTime.now().plus(Long.parseLong(refreshTokenExpirationTime),
				ChronoUnit.SECONDS);
		loginResponse.setRefreshTokenExpiryTime(refreshExpirationTime);
		loginResponse.setAccessExpiryTime(jwtExpirationTime);
		loginResponse.setEmployeecode(employee.getEmployeeId());
		loginResponse.setOfficialMobile(employee.getOfficialMobile());
		loginResponse.setPersonalMobile(employee.getPersonalMobile());
		loginResponse.setEmail(employee.getEmail());
		loginResponse.setOriginalRole(employee.getRole());
		loginResponse.setName(employee.getEmployeeName());
		List<EvpLov> evpLovs= evpLovRepository.findByLovCategory("CADMIN");
		Map<String, String> map= evpLovs.stream().collect(Collectors.toMap(EvpLov::getLovDisplayName,EvpLov::getLovValue));
		String email=map.get("CADMINEMAIL");
		String phoneNumber=map.get("CADMINPHONE");
		loginResponse.setEmailId(email);
		loginResponse.setCentralPhoneNumber(phoneNumber);
		return loginResponse;
	}
	
	private LoginResponse setLoginResponseForSwitchRole(String username, LoginResponse loginResponse, Employee employee,
			String switchRole,String originalRole) {
		String accessToken = jwtTokenUtil.generateToken(username, "ROLE_"+switchRole);
		String refreshToken = jwtTokenUtil.generateRefreshToken();
		loginResponse.setAccessToken(accessToken);
		loginResponse.setRefreshToken(refreshToken);
		LocalDateTime jwtExpirationTime = LocalDateTime.now().plus(Long.parseLong(expirationTime), ChronoUnit.SECONDS);
		LocalDateTime refreshExpirationTime = LocalDateTime.now().plus(Long.parseLong(refreshTokenExpirationTime),
				ChronoUnit.SECONDS);
		loginResponse.setRefreshTokenExpiryTime(refreshExpirationTime);
		loginResponse.setAccessExpiryTime(jwtExpirationTime);
		loginResponse.setEmployeecode(employee.getEmployeeId());
		loginResponse.setOfficialMobile(employee.getOfficialMobile());
		loginResponse.setPersonalMobile(employee.getPersonalMobile());
		loginResponse.setEmail(employee.getEmail());
		loginResponse.setOriginalRole(originalRole);
		loginResponse.setName(employee.getEmployeeName());
		List<EvpLov> evpLovs= evpLovRepository.findByLovCategory("CADMIN");
		Map<String, String> map= evpLovs.stream().collect(Collectors.toMap(EvpLov::getLovDisplayName,EvpLov::getLovValue));
		String email=map.get("CADMINEMAIL");
		String phoneNumber=map.get("CADMINPHONE");
		loginResponse.setEmailId(email);
		loginResponse.setCentralPhoneNumber(phoneNumber);
		return loginResponse;
	}

	private UserLogin updateUserLogin(LoginResponse loginResponse, Optional<UserLogin> userLoginOpt) {
		UserLogin existingUser = null;
		String username = loginResponse.getUsername();
		String accessToken = loginResponse.getAccessToken();
		String refreshToken = loginResponse.getRefreshToken();
		LocalDateTime accessExpiryTime = loginResponse.getAccessExpiryTime();
		LocalDateTime refreshTokenExpiryTime = loginResponse.getRefreshTokenExpiryTime();
		if (userLoginOpt.isPresent()) {
			log.info("Existing User");

			existingUser = userLoginOpt.get();

			existingUser.setAccessToken(accessToken);

			existingUser.setRefreshToken(refreshToken);

			existingUser.setAccessExpiryTime(accessExpiryTime);

			existingUser.setRefreshTokenExpiryTime(refreshTokenExpiryTime);

		} else {
			log.info("Creating New User Login");
			existingUser = UserLogin.builder().username(username).accessToken(accessToken)
					.accessExpiryTime(accessExpiryTime).refreshToken(refreshToken)
					.refreshTokenExpiryTime(refreshTokenExpiryTime).build();
		}
		return existingUser;
	}

	@Override
	public LoginResponse switchProfile(LoginResponse loginResponse) throws EvpException {

		try {
			UserLogin existingUser = null;
			String username = loginResponse.getUsername();

			String orginalRole = null;
			String rolesAssigned = CommonUtils.getAssignedRole(request, jwtUtil);

			Optional<UserLogin> userLoginOpt = userLoginRepository.findByUsername(username);
			if (userLoginOpt.isPresent()) {
				existingUser = userLoginOpt.get();
				if (Optional.ofNullable(loginResponse.isSwitchProfile()).isPresent()
						&& loginResponse.isSwitchProfile()) {
					log.info("Switching Employee Profile");
					Employee employee = validateUser(username);
					orginalRole = employee.getRole();
					if (employee.getRole().equals("ROLE_EMPLOYEE")) {
						log.error("Switching profile not allowed");
						throw new EvpException("Switching profile not allowed");
					}
					if (!Optional.ofNullable(loginResponse.getRoleToSwitch()).isPresent()) {
						throw new EvpException("Provide valid role for switching profile");
					}
					if (rolesAssigned.equalsIgnoreCase("ROLE_"+loginResponse.getRoleToSwitch())) {
						throw new EvpException("Same role cannot be assigned again");
					} 
					
					String location=validateUserLocationAndGet(employee);
					
					if (employee.getRole().contains("ROLE_ADMIN")) {
						loginResponse.setDefaultLocation(location);
					} else {
						Optional<String> locationOpt= evpLovService.getLocationLovMap().entrySet().stream().filter(l->l.getKey().equalsIgnoreCase("Head Office"))
								.map(entry->entry.getKey())
								.findFirst();
								
								if(locationOpt.isPresent()) {
									loginResponse.setDefaultLocation(locationOpt.get());
								}
					}

					loginResponse = setLoginResponseForSwitchRole(username, loginResponse, employee, loginResponse.getRoleToSwitch(),orginalRole);

					getActivityCountsForEmployee(loginResponse, employee.getEmployeeId());

					existingUser = updateUserLogin(loginResponse, userLoginOpt);
					log.info("Saving Or Updating User Login");
					userLoginRepository.save(existingUser);

				}

			} else {
				log.info("User Login doesn't exist for the user " + username);

				throw new EvpException("User Login doesn't exist for the user " + username);
			}

		} catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return loginResponse;

	}

	@Override
	public void logout() throws EvpException {
		log.info("logout");
		try {
			String username = CommonUtils.getUsername(request, jwtUtil);
			log.info("username : ".concat(username));

			Optional<UserLogin> user = userLoginRepository.findByUsername(username);
			if (user.isPresent()) {
				userLoginRepository.delete(user.get());
			}

		} catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}

}
