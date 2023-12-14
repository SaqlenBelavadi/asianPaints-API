package com.speridian.asianpaints.evp.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.EmployeeResponseDTO;
import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.service.EmployeeService;

@RestController
@RequestMapping("/api/evp/v1/")
public class EvpAdminController {

	@Autowired
	private EmployeeService employeeService;

	@GetMapping("/Admins")
	public ResponseEntity<GenericResponse> getAllAdmins(@RequestParam("pageNo")Integer pageNo,@RequestParam("pageSize")Integer pageSize,@RequestParam(name="employeeId",required = false)String employeeId) {
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			EmployeeResponseDTO adminList = employeeService.getAllAdmins(pageNo,pageSize,employeeId);
			genericResponse.setData(adminList);
			responseEntity = ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}

		return responseEntity;
	}
	
	@DeleteMapping("/Admins")
	public ResponseEntity<GenericResponse> deleteAdmin(@RequestParam("employeeId")String employeeId){
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			employeeService.deleteAdmin(employeeId);
			genericResponse.setMessage("Successfully deleted Admin");
		}catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@GetMapping("/Employees")
	public ResponseEntity<GenericResponse> getAllEmployees(
			@RequestParam(name = "employeeId", required = false) String employeeId,
			@RequestParam(name="pageNo",required = false)Integer pageNo,@RequestParam(name="pageSize",required = false)Integer pageSize) {
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {

			if (Optional.ofNullable(employeeId).isPresent() && !employeeId.isEmpty()) {
				EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);
				genericResponse.setData(employeeDTO);
			} else {
				List<EmployeeDTO> employeeDTOs = employeeService.getAllEmployees();
				List<String> employeeIds = employeeDTOs.stream().map(employee -> employee.getEmployeeId())
						.collect(Collectors.toList());

				genericResponse.setData(employeeIds);
			}

			responseEntity = ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}

		return responseEntity;
	}

	@PostMapping("/Admins")
	public ResponseEntity<GenericResponse> createAdmins(@RequestBody EmployeeDTO employeeDTO,
			@RequestParam("existingAdmin") boolean existingAdmin) {
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();

		try {
			employeeDTO = employeeService.createAdmins(employeeDTO, existingAdmin);
			genericResponse.setData(employeeDTO);
			genericResponse.setMessage("Admin created with employee id "+employeeDTO.getEmployeeId());
			responseEntity = ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}

		return responseEntity;
	}
	@PostMapping("/Employees/Update")
	public ResponseEntity<GenericResponse> updateEmployeeDetails(@RequestBody EmployeeDTO employeeDTO){
		ResponseEntity<GenericResponse> responseEntity = null;
		GenericResponse genericResponse = GenericResponse.builder().build();
		try {
			employeeDTO=employeeService.updateEmployeeDetails(employeeDTO);
			genericResponse.setData(employeeDTO);
			genericResponse.setMessage("Successfully Updated Employee Details");
			responseEntity = ResponseEntity.ok(genericResponse);
		}catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

}
