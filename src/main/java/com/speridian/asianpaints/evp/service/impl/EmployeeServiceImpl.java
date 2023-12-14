package com.speridian.asianpaints.evp.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.speridian.asianpaints.evp.dto.EmployeeDTO;
import com.speridian.asianpaints.evp.dto.EmployeeResponseDTO;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.Employee;
import com.speridian.asianpaints.evp.master.repository.EmployeeRepository;
import com.speridian.asianpaints.evp.service.EmployeeService;
import com.speridian.asianpaints.evp.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
	
	private Map<String, Employee> employeeMap;
	
	@PostConstruct
	public void  intializeEmployeeMap() {
		
		List<Employee> employeeList= (List<Employee>) employeeRepository.findAll();
		employeeMap= employeeList.stream().collect(Collectors.toMap(Employee::getEmployeeId, Function.identity()));
		
	}

	@Autowired
	private EmployeeRepository employeeRepository;

	@Override
	public EmployeeResponseDTO getAllAdmins(Integer pageNo,Integer pageSize,String employeeId) throws EvpException {
		List<EmployeeDTO> employeeDTOs=null;
		EmployeeResponseDTO employeeResponse=EmployeeResponseDTO.builder().build();
		try {
			
			Pageable pageable= PageRequest.of(pageNo-1 , pageSize);
			
			Page<Employee> employeePage = null;
			if (Optional.ofNullable(employeeId).isPresent()) {
				employeeId="%"+employeeId+"%";
				List<String> roles=Arrays.asList("ROLE_ADMIN","ROLE_CADMIN");
				employeePage = employeeRepository.findByRoleAndEmployeeId(roles, employeeId, pageable);
			} else {
				employeePage = employeeRepository.findAllAdmins(pageable);
			}
			
			if(!employeePage.isEmpty()) {
				employeeDTOs=employeePage.getContent().stream().map(employee->CommonUtils.convertEntityToEmployeeDTO(employee)).collect(Collectors.toList());	
			}else {
				employeeDTOs=Collections.emptyList();
			}
			
			employeeResponse.setEmployeeList(employeeDTOs);
			
			Integer totalPages = employeePage.getTotalPages();
			Long totalElements = employeePage.getTotalElements();

			Integer pageNumber = employeePage.getPageable().getPageNumber();

			boolean hasPrevious = employeePage.hasPrevious();

			boolean hasNext = employeePage.hasNext();
			
			employeeResponse.setHasNext(hasNext);
			employeeResponse.setHasPrevious(hasPrevious);
			employeeResponse.setPageNo(pageNumber+1);
			employeeResponse.setPageSize(pageSize);
			employeeResponse.setTotalPages(totalPages);
			employeeResponse.setTotalElements(totalElements.intValue());
			
			
		} catch (Exception e) {

			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return employeeResponse;
	}

	@Override
	public List<EmployeeDTO> getAllEmployees() throws EvpException {
		List<EmployeeDTO> employeeDTOs=null;
		
		try {
			List<Employee> employees= (List<Employee>) employeeRepository.findAll();
			
			employeeDTOs=employees.stream().map(employee->CommonUtils.convertEntityToEmployeeDTO(employee)).collect(Collectors.toList());
			
		} catch (Exception e) {

			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return employeeDTOs;
	}

	@Override
	public EmployeeDTO getEmployeeById(String employeeId) throws EvpException {
		EmployeeDTO employeeDTO=null;
		
		try {
			
			
			Optional<Employee> employeeOpt= employeeRepository.findByEmployeeId(employeeId);
			
			if(employeeOpt.isPresent()) {
				employeeDTO=CommonUtils.convertEntityToEmployeeDTO(employeeOpt.get());
			}else {
				throw new EvpException("Employee Doesn't exist");
			}
			
			
		} catch (EvpException e) {

			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}catch (Exception e) {

			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

		return employeeDTO;
	}

	@Override
	public EmployeeDTO createAdmins(EmployeeDTO employeeDTO, boolean existingAdmin) throws EvpException {
		
		try {
			Employee existingEmployee =null;
			Optional<Employee> employeeOpt= employeeRepository.findByEmployeeId(employeeDTO.getEmployeeId());
			
			if(existingAdmin) {
				
				if(!employeeOpt.isPresent()) {
					log.error("Employee Doesn't exist");
					throw new EvpException("Employee Doesn't exist");
				}
				 existingEmployee = employeeOpt.get();
				if(Optional.ofNullable(existingEmployee.getRole()).isPresent() && (existingEmployee.getRole().equals("ROLE_ADMIN") || existingEmployee.getRole().equals("ROLE_CADMIN"))) {
					log.error("Couldn't Assign Admin role to existing Admin");
					throw new EvpException("Couldn't Assign Admin role to existing Admin");
				}
				
			}else {
				if(employeeOpt.isPresent()) {
					log.error("Employee Already exist");
					throw new EvpException("Employee Already exist");
				}
				existingEmployee=null;
			}
			log.info("Creating Admin with Employee Id "+ employeeDTO.getEmployeeId());
			String role=employeeDTO.getRole();
			
			if(!role.equals("ROLE_ADMIN") && !role.equals("ROLE_CADMIN")) {
				throw new EvpException("Role doesn't exist");
			}
			
			existingEmployee=CommonUtils.convertDTOToEmployee(employeeDTO,existingEmployee, role);
			existingEmployee=employeeRepository.save(existingEmployee);
			return CommonUtils.convertEntityToEmployeeDTO(existingEmployee);
			
		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		
	}

	@Override
	public void deleteAdmin(String employeeId) throws EvpException {
		
		try {
			log.info("Retriving Existing Employee With Employee Id "+employeeId);
			Optional<Employee> employeeOpt= employeeRepository.findByEmployeeId(employeeId);
			if(employeeOpt.isPresent()) {
				
				Employee employee = employeeOpt.get();
				if(employee.getRole().equals("ROLE_ADMIN") || employee.getRole().equals("ROLE_CADMIN")) {
					log.info("Deleting Admin With Employee Id "+employeeId);	
					
					employee.setRole(null);
					employeeRepository.save(employee);
				}else {
					throw new EvpException("Employee Doesn't has Admin role");
				}
			}else {
				throw new EvpException("Employee Doesn't exist");
			}
			
		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		
	}

	@Override
	public Map<String, Employee> getEmployeeMap() {
		return employeeMap;
	}

	@Override
	public EmployeeDTO updateEmployeeDetails(EmployeeDTO employeeDTO) throws EvpException {
		try {
			if(Optional.ofNullable(employeeDTO.getEmployeeId()).isPresent()) {
				throw new EvpException("Employee Doesn't exist");
			}
			log.info("Retrieving Employee details with employee Id {}",employeeDTO.getEmployeeId());
			String employeeId=CommonUtils.getEmployeeIdFromUsername(employeeDTO.getEmployeeId());
			Optional<Employee> employeeOpt= employeeRepository.findByEmployeeId(employeeId);
			if(!employeeOpt.isPresent()) {
				throw new EvpException("Employee Doesn't exist");
			}
			Employee employee= employeeOpt.get();
			
			employee.setOfficialMobile(employeeDTO.getOfficialMobile());
			employee.setEmail(employeeDTO.getEmail());
			employeeRepository.save(employee);
			
		}catch (EvpException e) {
			log.error(e.getMessage());
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		return employeeDTO;
	}
	
	
	 

}
