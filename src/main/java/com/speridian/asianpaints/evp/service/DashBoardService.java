package com.speridian.asianpaints.evp.service;

import java.util.List;

import com.speridian.asianpaints.evp.dto.DashBoardActivityFinancialDTO;
import com.speridian.asianpaints.evp.dto.DashBoardDepartmentWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardEmployeeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardHeaderResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardLocationWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardModeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardMonthWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardPastActivityDetailsResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardThemeWiseDataDTO;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface DashBoardService {

	public DashBoardPastActivityDetailsResponseDTO getPastactivityDetails(String searchCriteria, Integer pageNo,
			Integer pageSize) throws EvpException;
	
	
	public DashBoardHeaderResponseDTO getDashBoardHeaderResponse(String searchCriteria) throws EvpException;

	
	public DashBoardActivityFinancialDTO getDashBoardFinancialDetails(String searchCriteria) throws EvpException;
	
	
	public DashBoardThemeWiseDataDTO getDashBoardThemeWiseData(List<Activity> activities,String searchCriteria) throws EvpException;
	
	
	public DashBoardModeWiseDataDTO getDashBoardModeWiseData(List<Activity> activities,String searchCriteria) throws EvpException;
	
	public DashBoardMonthWiseDataDTO getDashBoardMonthWiseData(List<Activity> activities,String searchCriteria) throws EvpException;
	
	public DashBoardEmployeeWiseDataDTO getEmployeeWiseDashBoardData(List<Activity> activities,String searchCriteria) throws EvpException;
	
	public DashBoardLocationWiseDataDTO getLocationWiseDataDTO(List<Activity> activities,String searchCriteria) throws EvpException;
	
	public DashBoardDepartmentWiseDataDTO getDepartmentWiseDataDTO(List<Activity> activities,String searchCriteria) throws EvpException;
	
}
