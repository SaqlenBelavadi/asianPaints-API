package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashBoardDetailsDTO {

	private DashBoardActivityFinancialDTO financialDetails;
	
	private DashBoardThemeWiseDataDTO dashBoardThemeWiseData;
	
	private DashBoardModeWiseDataDTO dashBoardModeWiseDataDTO;
	
	private DashBoardMonthWiseDataDTO dashBoardMonthWiseDataDTO;
	
	private DashBoardEmployeeWiseDataDTO dashBoardEmployeeWiseDataDTO;
	
	private DashBoardLocationWiseDataDTO dashBoardLocationWiseDataDTO;
	
	private DashBoardDepartmentWiseDataDTO dashBoardDepartmentWiseDataDTO;
	
}
