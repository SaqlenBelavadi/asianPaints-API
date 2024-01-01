package com.speridian.asianpaints.evp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.speridian.asianpaints.evp.dto.ImageResponse;
import com.speridian.asianpaints.evp.dto.PublishOrUnPublishImages;
import com.speridian.asianpaints.evp.dto.SearchCriteria;
import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.entity.BannerPicture;
import com.speridian.asianpaints.evp.entity.Leaders;
import com.speridian.asianpaints.evp.entity.PartnersLogo;
import com.speridian.asianpaints.evp.entity.TestimonialData;
import com.speridian.asianpaints.evp.entity.Video;
import com.speridian.asianpaints.evp.entity.VoiceOfChange;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface UploadService {

	public String uploadData(MultipartFile multipartFile) throws EvpException;

	public List<String> uploadImages(MultipartFile[] multipartRequests, String imageType, String acticityName,
			String activityTheme, String activityTag, String startDate, String endDate, String location,String mode,boolean manualUpload) throws EvpException;

	public Object getImagesByType(String imageType, SearchCriteria searchCriteria, Integer pageNo, Integer pageSize)
			throws EvpException;
	
	public ImageResponse getImagesForActivityDetails(SearchCriteria searchCriteria, Integer pageNo, Integer pageSize)
			throws EvpException;

	public void publishOrUnPublishPhoto(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException;

	public void uploadPhotoToGallery(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException;

	public void deletePhoto(List<PublishOrUnPublishImages> publishOrUnpublishImages) throws EvpException;
	
	
	public Map<String, String> getActivityMap(List<ActivityPicture> activityPictures) ;
	
	public List<String> uploadImagesToBanner(MultipartFile[] multipartRequests,Long index) throws EvpException;
	
	public Leaders uploadDataToLeadersTalk(String leaderName, String designation, String description,long index) throws EvpException;

	
    public List<String> uploadPartnersLogo(MultipartFile[] multipartRequests,Long index) throws EvpException;
	
	
	public TestimonialData uploadDataToTestimonial(MultipartFile[] multipartRequests, String testimonialName, String designationAndLocation, String description,Long index) throws EvpException;

	
	public Video uploadVideo(String videoURL,String videoName,Long index) throws EvpException;
		

	public VoiceOfChange uploadFilesToVOC(MultipartFile[] multipartRequests, String speaksType, String personName,
			String designationOrInfo,Long index) throws EvpException;

	public Map<String, Object> commonGetForLandingPge() throws EvpException; 
	
	void deleteBannerImages(String imageName) throws EvpException;
	
	void deleteLeadersData(String imageName) throws EvpException;
	
	void deleteVoiceOfChangeData(String imageName) throws EvpException;
	
	void deleteVideo(String videoURL) throws EvpException;
	
	void deleteTestimonialData(String imageName) throws EvpException;
	
	void deletePartnersLogo(String imageName) throws EvpException;
}
