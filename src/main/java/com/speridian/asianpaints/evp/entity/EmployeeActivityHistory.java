package com.speridian.asianpaints.evp.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "EVP_EMPLOYEE_ACTIVITY_HISTORY")
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeActivityHistory extends AbstractEntity {

	@Column(name = "EMPLOYEE_ID")
	private String employeeId;

	@Column(name = "ACTIVITY_UUID")
	private String activityUUID;

	@Column(name = "ACTIVITY_NAME")
	private String activityName;
	
	@Column(name = "ACTIVITY_PHYSICAL_NAME")
	private String activityPhysicalName;

	@Column(name = "EMPLOYEE_NAME")
	private String employeeName;

	@Column(name = "ACTIVITY_TAG")
	private String activityTag;

	@Column(name = "ACTIVITY_THEME")
	private String activityTheme;

	@Column(name = "END_DATE")
	private LocalDate endDate;
	
	@Column(name = "START_DATE")
	private LocalDate startDate;

	@Column(name = "MODES")
	private String mode;

	@Column(name = "DEPARTMENT_NAME")
	private String departmentName;

	@Column(name = "ACTIVITY_LOCATION")
	private String activityLocation;

	@Column(name = "PARTICICPATION_HOURS")
	private String participationHours;

	@Column(name = "APPROVED_BY_ADMIN")
	private boolean approvedByAdmin;

	@Column(name = "REJECTED_BY_ADMIN")
	private boolean rejectedByAdmin;

	@Enumerated(EnumType.STRING)
	@Column(name = "EMPLOYEE_ACTIVITY_STATUS")
	private EmployeeActivityStatus employeeActivityStatus;

}
