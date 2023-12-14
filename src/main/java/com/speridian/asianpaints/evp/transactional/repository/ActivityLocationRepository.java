package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.speridian.asianpaints.evp.entity.ActivityLocation;

public interface ActivityLocationRepository extends JpaRepository<ActivityLocation	,Long> {
	
	@Query("from ActivityLocation a where a.locationId in (:locationIds)")
	public List<ActivityLocation> findByLocationIds(List<Long> locationIds);
	
	@Query("from ActivityLocation a where a.activityId in (:activityIds) and a.locationId in (:locationIds)")
	public List<ActivityLocation> findByActivityIdsAndLocationIds(List<String> activityIds, List<Long> locationIds);

	
	@Query("from ActivityLocation a where a.activityId =:activityId and a.locationId =:locationId")
	public List<ActivityLocation> findByActivityIdsAndLocationIds(String activityId, Long locationId);
	
	public List<ActivityLocation> findByActivityId(String activityId);
	
	@Query("from ActivityLocation a where a.activityId in (:activityIds)")
	public List<ActivityLocation> findByActivityIds(List<String> activityIds);
	

}
