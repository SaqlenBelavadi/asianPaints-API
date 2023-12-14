package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.Activity;

public interface ActivityRepository extends PagingAndSortingRepository<Activity, Long> , JpaSpecificationExecutor<Activity> {
	
	
	public Activity findByActivityUUID(String activityUUID);
	
	public Optional<Activity> findByActivityName(String activityName);
	
	public List<Activity> findByActivityLocationId(Long activityLocationId);
	
	public Optional<Activity> findByActivityId(String activityId);
	
	@Query("from Activity a where a.activityId in (:activityIds) ")
	public List<Activity> findByActivityIds(List<String> activityIds);
	
	@Query("from Activity a where a.activityId in (:activityIds) ")
	public List<Activity> findByActivityIds(List<String> activityIds,Sort sort);

}
