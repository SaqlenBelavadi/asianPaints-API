package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.EmployeeActivityHistory;

public interface EmployeeActivityHistoryRepository extends PagingAndSortingRepository<EmployeeActivityHistory, Long>, JpaSpecificationExecutor<EmployeeActivityHistory> {
	

	public EmployeeActivityHistory findByEmployeeIdAndActivityUUID(String employeeId,String activityUUID);
	
	@Query("from EmployeeActivityHistory e where e.employeeId=:employeeId AND e.activityUUID in (:activityUUids)")
	public List<EmployeeActivityHistory> findByEmployeeIdAndActivityUUids(String employeeId,List<String> activityUUids);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids)")
	public List<EmployeeActivityHistory> getActivities(List<String> ids);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids) and e.employeeActivityStatus in (:employeeActivityStatuses)")
	public List<EmployeeActivityHistory> getActivityWithStatus(List<String> ids,List<EmployeeActivityStatus> employeeActivityStatuses);
	
	@Query("from EmployeeActivityHistory e where e.activityName in (:activityIds)")
	public List<EmployeeActivityHistory> getHistoryByActivityId(List<String> activityIds);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids)")
	public Page<EmployeeActivityHistory> getActivities(List<String> ids,Pageable pageable);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids) and e.employeeActivityStatus in (:employeeActivityStatuses)")
	public Page<EmployeeActivityHistory> getActivities(List<String> ids,Pageable pageable, List<EmployeeActivityStatus> employeeActivityStatuses);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids)  and e.employeeId in (:employeeIds)")
	public Page<EmployeeActivityHistory> getActivities(List<String> ids,List<String> employeeIds, Pageable pageable);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:ids) and e.employeeId in (:employeeIds)")
	public List<EmployeeActivityHistory> getActivities(List<String> ids,List<String> employeeIds);
	
	@Query("from EmployeeActivityHistory e where e.activityName in (:names)")
	public List<EmployeeActivityHistory> getByActivityName(Set<String> names);
	
	@Query("from EmployeeActivityHistory e where e.activityUUID in (:activityUUIDs) and (e.employeeActivityStatus ='PARTICIPATED' OR e.employeeActivityStatus ='FEEDBACK' OR e.employeeActivityStatus ='ENROLLED')")
	public List<EmployeeActivityHistory> getActivitiesWithStatusParticipated(List<String> activityUUIDs);
	
	public List<EmployeeActivityHistory> findByEmployeeId(String employeeId);
	
	@Query("from EmployeeActivityHistory e where e.employeeId =:employeeId and e.rejectedByAdmin=:rejectedByAdmin")
	public List<EmployeeActivityHistory> findByEmployeeIdNotRejectedByAdmin(String employeeId,boolean rejectedByAdmin);
	
	
	@Query("select e from EmployeeActivityHistory e where e.activityUUID in (:ids) and e.employeeId=:employeeId")
	public List<EmployeeActivityHistory> getActivitiesForEmployee(String employeeId,List<String> ids);
	
	@Query("from EmployeeActivityHistory e where e.employeeId in (:employeeIds)")
	public List<EmployeeActivityHistory> getByEmployeeIds(List<String> employeeIds);
	
	@Query("from EmployeeActivityHistory e where e.employeeId in (:employeeIds)")
	public Page<EmployeeActivityHistory> getByEmployeeIds(List<String> employeeIds,Pageable pageable);
	
	
	@Query("select e from EmployeeActivityHistory e where e.activityUUID in (:ids) and e.employeeId in (:employeeIds)")
	public List<EmployeeActivityHistory> getActivitiesForEmployee(List<String> employeeIds,List<String> ids);
}
