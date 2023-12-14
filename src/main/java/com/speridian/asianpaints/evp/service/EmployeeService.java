package com.speridian.asianpaints.evp.service;

import java.util.List;
import java.util.Map;

import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.EmployeeResponseDTO;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;

public interface EmployeeService {
	
	public EmployeeResponseDTO getAllAdmins(Integer pageNo,Integer pageSize,String employeeId) throws EvpException;
	
	public List<EmployeeDTO> getAllEmployees() throws EvpException;
	
	public EmployeeDTO getEmployeeById(String employeeId) throws EvpException;
	
	public EmployeeDTO createAdmins(EmployeeDTO employeeDTO, boolean existingAdmin) throws EvpException;
	
	public void deleteAdmin(String employeeId) throws EvpException;
	
	public Map<String, Employee> getEmployeeMap() ;
	
	public EmployeeDTO updateEmployeeDetails(EmployeeDTO employeeDTO) throws EvpException;

}
