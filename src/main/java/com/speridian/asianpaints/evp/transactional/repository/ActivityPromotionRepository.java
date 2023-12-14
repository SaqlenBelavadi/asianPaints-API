package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.ActivityPromotion;

public interface ActivityPromotionRepository
		extends PagingAndSortingRepository<ActivityPromotion, Long>, JpaSpecificationExecutor<ActivityPromotion> {

	@Query(value = "SELECT * FROM EVP_ACTIVITY_PROMOTION  eaf  WHERE eaf.id IN (:stringids)", nativeQuery = true)
	List<ActivityPromotion> getFeedbackByIds(List<Long> stringids);
	
	
	@Query(value = "from ActivityPromotion a where a.promotionlocation in (:promotionlocations)")
	List<ActivityPromotion> findByPromotionlocations(List<String> promotionlocations);
	
	List<ActivityPromotion> findByActivityId(String activityId);

}
