package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.speridian.asianpaints.evp.entity.ActivityLocation;
import com.speridian.asianpaints.evp.entity.ActivityPromotionLocation;

public interface ActivityPromotionLocationRepostiroy extends JpaRepository<ActivityPromotionLocation, Long> {
	
	@Query("from ActivityPromotionLocation al where al.promotionId in (:promotionIds)")
	public List<ActivityPromotionLocation> findByPromotionIds(List<Long> promotionIds);
	
	
	public List<ActivityPromotionLocation> findByPromotionId(Long promotionId);
	
	@Query("from ActivityPromotionLocation a where a.location in (:locations)")
	public List<ActivityPromotionLocation> findByMultiLocation(List<String> locations);
	
	public List<ActivityPromotionLocation> findByActivityId(String activityId);

}
