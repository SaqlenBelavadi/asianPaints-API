package com.speridian.asianpaints.evp.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	
	private String username;
	
	private String name;
	
	private String password;
	
	private boolean switchProfile;
	
	private String roleToSwitch; 
	
	private String email;
	
	private String officialMobile;
	
	private String personalMobile;
	
	private String employeecode;
	
	private String description;
	
	private String accessToken;
	
	private String refreshToken;
	
	private LocalDateTime accessExpiryTime;
	
	private LocalDateTime refreshTokenExpiryTime;
	
	private String defaultLocation;
	
	private String originalRole;
	
	private String assignedRole;
	
	private int totalActivityParticipated;
	
	private String totalHoursParticipated;
	
	private String emailId;
	
	private String centralPhoneNumber;

}
