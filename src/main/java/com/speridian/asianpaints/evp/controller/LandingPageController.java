package com.speridian.asianpaints.evp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.speridian.asianpaints.evp.dto.CreateOrUpdateActivityDTO;
import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.entity.Leaders;
import com.speridian.asianpaints.evp.entity.TestimonialData;
import com.speridian.asianpaints.evp.entity.Video;
import com.speridian.asianpaints.evp.entity.VoiceOfChange;
import com.speridian.asianpaints.evp.service.ActivityService;
import com.speridian.asianpaints.evp.service.UploadService;


@RestController
@RequestMapping("/api/evp/v1/")
public class LandingPageController {
	
	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private ActivityService activityService;
	@PostMapping("/Upload/Images/Banner")
	public ResponseEntity<GenericResponse> uploadPicture(@RequestParam("images") MultipartFile[] images) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			List<String> fileNames = uploadService.uploadImagesToBanner(images);
			genericResponse.setData(fileNames);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
//	@GetMapping("/Images/Banner")
//	public ResponseEntity<List<BannerPicture>> getBannerImages(){
//
//		try {
//			
//			List<BannerPicture> bannerPicture=   uploadService.getBannerPictures();
//			return ResponseEntity.ok(bannerPicture);
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.emptyList());
//		}	
//	}
	
	@PostMapping("/Upload/LeadersTalk")
	public ResponseEntity<GenericResponse> uploadLeadersTalk(@RequestParam("images") MultipartFile[] images,
			@RequestParam("leaderName") String leaderName,
				@RequestParam("designation") String designation,
					@RequestParam("description") String description) {
		
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			Leaders leader = uploadService.uploadDataToLeadersTalk(images, leaderName, designation, description);
			genericResponse.setData(leader);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
//	@GetMapping("/Data/LeadersTalk")
//	public ResponseEntity<List<Leaders>> getLeadersData(){
//
//		try {
//			
//			List<Leaders> leaders=   uploadService.getLeadersData();
//			return ResponseEntity.ok(leaders);
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.emptyList());
//		}	
//	}
	
	@PostMapping("/Upload/Logo/Partner")
	public ResponseEntity<GenericResponse> uploadPartnersLogo(@RequestParam("images") MultipartFile[] images) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			List<String> fileNames = uploadService.uploadPartnersLogo(images);
			genericResponse.setData(fileNames);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
//	@GetMapping("/Logo/Partners")
//	public ResponseEntity<List<PartnersLogo>> getPartnersLogo(){
//
//		try {
//			
//			List<PartnersLogo> partnersLogo=   uploadService.getPartnersLogo();
//			return ResponseEntity.ok(partnersLogo);
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.emptyList());
//		}	
//	}
	
	@PostMapping("/Upload/TestimonialData")
	public ResponseEntity<GenericResponse> uploadTestimonialData(@RequestParam("images") MultipartFile[] images,
			@RequestParam("testimonialName") String testimonialName,
				@RequestParam("designationAndLocation") String designationAndLocation,
					@RequestParam("description") String description) {
		
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			TestimonialData testimonialData = uploadService.uploadDataToTestimonial(images, testimonialName, designationAndLocation, description);
			genericResponse.setData(testimonialData);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
//	@GetMapping("/Data/Testimonial")
//	public ResponseEntity<List<TestimonialData>> getTestimonialData(){
//
//		try {
//			
//			List<TestimonialData> testimonialData=   uploadService.getTestimonialData();
//			return ResponseEntity.ok(testimonialData);
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.emptyList());
//		}	
//	}
	
	@PostMapping("/Upload/Video")
	public ResponseEntity<GenericResponse> uploadVideos(@RequestParam("videoURL") String videoURL,
			@RequestParam("videoName") String videoName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			Video videoData = uploadService.uploadVideo(videoURL,videoName);
			genericResponse.setData(videoData);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
//	@GetMapping("/Data/Video")
//	public ResponseEntity<List<Video>> getVideo(){
//
//		try {
//			
//			List<Video> video= uploadService.getVideo();
//			return ResponseEntity.ok(video);
//		}catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Collections.emptyList());
//		}	
//	}
	
	@PostMapping("/Upload/VoiceOfChange")
	public ResponseEntity<GenericResponse> uploadVOC(@RequestParam("imageAndAudio") MultipartFile[] multipartRequests,
				@RequestParam("speaksType") String speaksType,@RequestParam("personName") String personName,@RequestParam("designationOrInfo") String designationOrInfo) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			VoiceOfChange vocData = uploadService.uploadFilesToVOC(multipartRequests, speaksType,personName,designationOrInfo);
			genericResponse.setData(vocData);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@GetMapping("/LandingPageDetails")
	public ResponseEntity<GenericResponse> commonGet()
	{
		GenericResponse genericResponse=GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity=null;
		try {
			Map<String, Object> dataMap= uploadService.commonGetForLandingPge();
			genericResponse.setData(dataMap);
			return ResponseEntity.ok(genericResponse);
		}catch(Exception e)
		{
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	
	@GetMapping("/LocationWise")
	public ResponseEntity<GenericResponse> getUniquePastActivities() {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {

			List<CreateOrUpdateActivityDTO> pastActivityList = activityService.getLocationWisePastActivities();
			genericResponse.setData(pastActivityList);
			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);

		}

		return responseEntity;
	}

	@DeleteMapping("Delete/BannerImages")
	public ResponseEntity<GenericResponse> deleteBannerImages(@RequestParam("imageName") String imageName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deleteBannerImages(imageName);
			genericResponse.setData("Image deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@DeleteMapping("Delete/LeadersTalkData")
	public ResponseEntity<GenericResponse> deleteLeadersTalk(@RequestParam("imageName") String imageName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deleteLeadersData(imageName);
			genericResponse.setData("Leaders Data deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@DeleteMapping("Delete/VoiceOfChangeData")
	public ResponseEntity<GenericResponse> deleteVoiceOfChange(@RequestParam("imageName") String imageName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deleteVoiceOfChangeData(imageName);
			genericResponse.setData("Voice Of Change data deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@DeleteMapping("Delete/Video")
	public ResponseEntity<GenericResponse> deleteVideo(@RequestParam("videoURL") String videoURL) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deleteVideo(videoURL);
			genericResponse.setData("Video deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@DeleteMapping("Delete/TestimonialData")
	public ResponseEntity<GenericResponse> deleteActivity(@RequestParam("imageName") String imageName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deleteTestimonialData(imageName);
			genericResponse.setData("Testimonial Data deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@DeleteMapping("Delete/PartnersLogo")
	public ResponseEntity<GenericResponse> deletePartnersLogo(@RequestParam("imageName") String  imageName) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {
			uploadService.deletePartnersLogo(imageName);
			genericResponse.setData("Partners Logo deleted successfully");
			responseEntity = ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
}
