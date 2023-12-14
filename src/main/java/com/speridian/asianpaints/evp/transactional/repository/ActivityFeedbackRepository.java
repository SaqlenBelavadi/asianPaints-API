package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.ActivityFeedback;

public interface ActivityFeedbackRepository
		extends PagingAndSortingRepository<ActivityFeedback, Long>, JpaSpecificationExecutor<ActivityFeedback> {

	@Query(value = "SELECT * FROM EVP_ACTIVITY_FEEDBACK eaf WHERE eaf.ACTIVITY_NAME = :a and eaf.EMPLOYEE_ID IN :employeeIds", nativeQuery = true)
	public List<ActivityFeedback> getActivityNameAndEmployeeIds(String a, List<String> employeeIds);

	@Query(value = "SELECT * FROM EVP_ACTIVITY_FEEDBACK  eaf  WHERE eaf.id IN (:ids)", nativeQuery = true)
	public List<ActivityFeedback> getFeedbackByIds(List<String> ids);
	
	@Query(value = "from ActivityFeedback a where a.activityName in (:activityNames)")
	public List<ActivityFeedback> findByActivityNames(Set<String> activityNames);

	@Query(value = "SELECT * FROM EVP_ACTIVITY_FEEDBACK eaf WHERE eaf.ACTIVITY_NAME = :activityName and eaf.createdBy = :employeId ORDER BY createdon DESC ", nativeQuery = true)
	public List<ActivityFeedback> getUserActivityFeedbackByactivityName(String activityName, String employeId);

	@Query(value = "SELECT * FROM EVP_ACTIVITY_FEEDBACK eaf WHERE eaf.ACTIVITY_NAME = :activityName ORDER BY createdon DESC ", nativeQuery = true)
	public List<ActivityFeedback> getActivityFeedbackActivityName(String activityName);

}
