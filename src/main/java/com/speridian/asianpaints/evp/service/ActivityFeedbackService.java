package com.speridian.asianpaints.evp.service;

import java.util.Map;

import com.speridian.asianpaints.evp.dto.ActivityFeedbackDTO;
import com.speridian.asianpaints.evp.dto.ActivityFeedbackResponseDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionDTO;
import com.speridian.asianpaints.evp.dto.ActivityPromotionResponseDTO;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface ActivityFeedbackService {

	void addActivityFeedBack(ActivityFeedbackDTO feedbackDTO) throws EvpException;

	ActivityFeedbackResponseDTO getActivityFeedBack(SearchCriteria criteria, Integer pageNo, Integer pageSize);

	void deleteActivityFeedBack(String ids) throws EvpException;

	void UploadToGallery(String ids) throws EvpException;

	Map<String, Object> getGalleryFeedbacks(Integer pageNo, Integer pageSize, SearchCriteria searchCriteria);

	void deleteGalleryFeedbacks(String ids) throws EvpException;

	ActivityFeedbackResponseDTO getActivityFeedBack(String searchCriteria, Integer pageNo, Integer pageSize);

	void addActivityPromotion(ActivityPromotionDTO dto) throws EvpException;

	void deleteActivityPromotion(String ids) throws EvpException;

	ActivityPromotionResponseDTO getActivityPromotion(SearchCriteria criteria, Integer pageNo, Integer pageSize);

	void publishOrUnpublish(String ids, String status) throws EvpException;

}
