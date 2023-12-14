package com.speridian.asianpaints.evp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.speridian.asianpaints.evp.dto.DashBoardActivityFinancialDTO;
import com.speridian.asianpaints.evp.dto.DashBoardDepartmentWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardDetailsDTO;
import com.speridian.asianpaints.evp.dto.DashBoardEmployeeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardHeaderResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardLocationWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardModeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardMonthWiseDataDTO;
import com.speridian.asianpaints.evp.dto.DashBoardPastActivityDetailsResponseDTO;
import com.speridian.asianpaints.evp.dto.DashBoardThemeWiseDataDTO;
import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.service.DashBoardService;

@RestController
@RequestMapping("/api/evp/v1")
public class DashBoardController {
	
	
	@Autowired
	private DashBoardService dashBoardService;

	@GetMapping("/DashBoard/PastActivity")
	public ResponseEntity<GenericResponse> getPactActivityDetails(@RequestParam("searchCriteria") String searchCriteria,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			DashBoardPastActivityDetailsResponseDTO pastActivityDetails = dashBoardService
					.getPastactivityDetails(searchCriteria, pageNo, pageSize);
			genericResponse.setData(pastActivityDetails);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@GetMapping("/DashBoard/Header")
	public ResponseEntity<GenericResponse> getDashBoardHeaderResponse(
			@RequestParam("searchCriteria") String searchCriteria) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			DashBoardHeaderResponseDTO headerReponse= dashBoardService.getDashBoardHeaderResponse(searchCriteria);
			genericResponse.setData(headerReponse);
			responseEntity = ResponseEntity.ok(genericResponse);
		}catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@GetMapping("/DashBoard/DashBoardDetails")
	public ResponseEntity<GenericResponse> getDashBoardActivityFinancials(
			@RequestParam("searchCriteria") String searchCriteria) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		DashBoardDetailsDTO dashBoardDetails=DashBoardDetailsDTO.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			DashBoardActivityFinancialDTO dashBoardActivityFinancials= dashBoardService.getDashBoardFinancialDetails(searchCriteria);
			dashBoardDetails.setFinancialDetails(dashBoardActivityFinancials);
			
			DashBoardThemeWiseDataDTO dashBoardThemeWiseData= dashBoardService.getDashBoardThemeWiseData(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardThemeWiseData(dashBoardThemeWiseData);
			
			DashBoardModeWiseDataDTO dashBoardModeWiseDataDTO= dashBoardService.getDashBoardModeWiseData(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardModeWiseDataDTO(dashBoardModeWiseDataDTO);
			
			DashBoardMonthWiseDataDTO dashBoardMonthWiseData= dashBoardService.getDashBoardMonthWiseData(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardMonthWiseDataDTO(dashBoardMonthWiseData);
			
			DashBoardEmployeeWiseDataDTO dashBoardEmployeeWiseDataDTO= dashBoardService.getEmployeeWiseDashBoardData(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardEmployeeWiseDataDTO(dashBoardEmployeeWiseDataDTO);
			
			DashBoardLocationWiseDataDTO dashBoardLocationWiseDataDTO= dashBoardService.getLocationWiseDataDTO(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardLocationWiseDataDTO(dashBoardLocationWiseDataDTO);
			
			DashBoardDepartmentWiseDataDTO  dashBoardDepartmentWiseDataDTO=dashBoardService.getDepartmentWiseDataDTO(dashBoardActivityFinancials.getActivities(),searchCriteria);
			dashBoardDetails.setDashBoardDepartmentWiseDataDTO(dashBoardDepartmentWiseDataDTO);
			
			genericResponse.setData(dashBoardDetails);
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;

	}

}
