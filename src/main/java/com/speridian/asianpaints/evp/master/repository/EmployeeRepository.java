package com.speridian.asianpaints.evp.master.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.master.entity.Employee;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
	
	
	public Optional<Employee> findByEmployeeId(String employeeId);
	
	public List<Employee> findByRole(String role);
	
	public Page<Employee> findByRole(String role,Pageable pageable);
	
	@Query("from Employee e where e.role='ROLE_ADMIN' or e.role='ROLE_CADMIN'")
	public Page<Employee> findAllAdmins(Pageable pageable);
	
	@Query("from Employee e where e.role in (:roles) and e.employeeId like :employeeId")
	public Page<Employee> findByRoleAndEmployeeId(List<String> roles,String employeeId,Pageable pageable);
	
	@Query("from Employee e where e.departmentName in (:departmentName)")
	public List<Employee> findByDepartmentName(List<String> departmentName);
	
	@Query("from Employee e where lower(e.divisionName) in (:divisionName)")
	public List<Employee> findByDivisionName(List<String> divisionName);
	
	@Query("from Employee e where e.employeeId in (:employeeIds)")
	public List<Employee> findByEmployeeIds(List<String> employeeIds);
	
	

}
