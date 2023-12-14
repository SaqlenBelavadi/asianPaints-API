package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.speridian.asianpaints.evp.entity.ActivityFeedbackLocation;

public interface ActivityFeedbackLocationRepostiroy extends JpaRepository<ActivityFeedbackLocation, Long> {
	
	@Query("from ActivityFeedbackLocation al where al.feedbackId in (:feedbackIds)")
	public List<ActivityFeedbackLocation> findByFeedBackIds(List<Long> feedbackIds);
	
	
	public List<ActivityFeedbackLocation> findByFeedbackId(Long feedbackId);
	
	@Query("from ActivityFeedbackLocation a where a.location in (:locations)")
	public List<ActivityFeedbackLocation> findByMultiLocation(List<String> locations);
	
	public List<ActivityFeedbackLocation> findByActivityId(String activityId);

}
