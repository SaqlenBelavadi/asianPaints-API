package com.speridian.asianpaints.evp.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
	
	private String employeeId;
	
	private String employeeName;
	
	private String salutation;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	private String gender;
	
	private LocalDate dateOfBirth;
	
	private String bloodGroup;
	
	private String email;
	
	private String personalMobile;
	
	private String officialMobile;
	
	private String confirmationStatus;
	
	private LocalDate hireDate;
	
	private String tenure;
	
	private LocalDate lastWorkingDay;
	
	private String active;
	
	private LocalDate confirmationDate;
	
	private String payGradeId;
	
	private String payGradeName;
	
	private String locationCode;
	
	private String locationName;
	
	private String verticalCode;
	
	private String departmentCode;
	
	private String division_Code;
	
	private String function_Code;
	
	private String functionName;
	
	private String verticalName;
	
	private String departmentName;
	
	private String divisionName;
	
	private String company;
	
	private LocalDate exitDate;
	
	private Integer managerId;
	
	private String managerName;
	
	private Integer jobCode;
	
	private String jobTitle;
	
	private String role;

}
