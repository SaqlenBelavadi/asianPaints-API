package com.speridian.asianpaints.evp.master.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.entity.AbstractEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="EVP_EMPLOYEE_MASTER")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends AbstractEntity {
	
	@Column(name = "EMPLOYEE_ID")
	private String employeeId;
	
	@Column(name = "EMPLOYEE_NAME")
	private String employeeName;
	
	@Column(name = "SALUTATION")
	private String salutation;
	
	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "MIDDLE_NAME")
	private String middleName;
	

	@Column(name = "LAST_NAME")
	private String lastName;
	
	@Column(name = "GENDER")
	private String gender;
	
	@Column(name = "DATE_OF_BIRTH")
	private LocalDate dateOfBirth;
	
	@Column(name = "BLOOD_GROUP")
	private String bloodGroup;
	
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "PERSONAL_MOBILE")
	private String personalMobile;
	
	@Column(name = "OFFICIAL_MOBILE")
	private String officialMobile;
	
	@Column(name = "CONFIRMATION_STATUS")
	private String confirmationStatus;
	
	@Column(name = "HIRE_DATE")
	private LocalDate hireDate;
	
	@Column(name = "TENURE")
	private String tenure;
	
	@Column(name = "LAST_WORKING_DAY")
	private LocalDate lastWorkingDay;
	
	@Column(name = "ACTIVE")
	private String active;
	
	@Column(name = "CONFIRMATION_DATE")
	private LocalDate confirmationDate;
	
	@Column(name = "PAYGRADE_ID")
	private String payGradeId;
	
	@Column(name = "PAYGRADE_NAME")
	private String payGradeName;
	
	@Column(name = "LOCATION_CODE")
	private String locationCode;
	
	@Column(name = "LOCATION_NAME")
	private String locationName;
	
	@Column(name = "VERTICAL_CODE")
	private String verticalCode;
	
	@Column(name = "DEPARTMENT_CODE")
	private String departmentCode;
	
	@Column(name = "DIVISION_CODE")
	private String division_Code;
	
	@Column(name = "FUNCTION_CODE")
	private String function_Code;
	
	@Column(name = "FUNCTION_NAME")
	private String functionName;
	
	@Column(name = "VERTICAL_NAME")
	private String verticalName;
	
	@Column(name = "DEPARTMENT_NAME")
	private String departmentName;
	
	@Column(name = "DIVISION_NAME")
	private String divisionName;
	
	@Column(name = "COMPANY")
	private String company;
	
	@Column(name = "EXIT_DATE")
	private LocalDate exitDate;
	
	@Column(name = "MANAGER_ID")
	private Integer managerId;
	
	@Column(name = "MANAGER_NAME")
	private String managerName;
	
	@Column(name = "JOB_CODE")
	private Integer jobCode;
	
	@Column(name = "JOB_TITLE")
	private String jobTitle;
	
	@Column(name="ROLE")
	private String role;
	

}
