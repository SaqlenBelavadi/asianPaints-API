package com.speridian.asianpaints.evp.controller;



import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.speridian.asianpaints.evp.dto.GenericResponse;
import com.speridian.asianpaints.evp.dto.ImageResponse;
import com.speridian.asianpaints.evp.dto.PublishOrUnPublishImages;
import com.speridian.asianpaints.evp.dto.Response;
import com.speridian.asianpaints.evp.dto.SearchCriteria;

import com.speridian.asianpaints.evp.service.UploadService;
import com.speridian.asianpaints.evp.util.CommonUtils;


@RestController
@RequestMapping("/api/evp/v1/")
public class UploadController {

	@Autowired
	private UploadService uploadService;
	

	@PostMapping("/Upload/ParticipantDetails")
	public ResponseEntity<GenericResponse> uploadParticipationData(@RequestParam("file") MultipartFile file) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
		try {

			String message=uploadService.uploadData(file);
			Response response = new Response("Uploaded the file successfully: " + file.getOriginalFilename()+" "+ message,
					"Data Uploaded ");
			genericResponse.setData(response);
			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {

			genericResponse.setData(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}

		return responseEntity;
	}

	@PostMapping("/Upload/Images")
	public ResponseEntity<GenericResponse> uploadPicture(@RequestParam("images") MultipartFile[] images,
			@RequestParam("imageType") String imageType, @RequestParam("activityName") String activityName,
			@RequestParam("activityTheme") String activityTheme, @RequestParam("activityTag") String activityTag,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
			@RequestParam("activityLocation") String activityLocation, @RequestParam("mode") String mode,
			@RequestParam(name="manualUpload",defaultValue = "true") boolean manualUpload) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;
					
		try {
			List<String> fileNames = uploadService.uploadImages(images, imageType, activityName, activityTheme,
					activityTag, startDate, endDate, activityLocation,mode,manualUpload);
			genericResponse.setData(fileNames);
			genericResponse.setMessage("Successfully Uploaded");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	


	@GetMapping("/Images")
	public ResponseEntity<GenericResponse> getImagesByType(@RequestParam("imageType") String imageType,
			@RequestParam(name = "searchCriteria", required = false) String searchCriteria,
			@RequestParam(name = "pageNo", required = false) Integer pageNo,
			@RequestParam(name = "pageSize", required = false) Integer pageSize) {
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;

		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);

			Object data = uploadService.getImagesByType(imageType, criteria, pageNo, pageSize);

			genericResponse.setData(data);

			return ResponseEntity.ok(genericResponse);

		} catch (Exception e) {

			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@PostMapping("/Images/PublishOrUnPublish")
	public ResponseEntity<Response> publishOrUnpublishPhoto(
			@RequestBody List<PublishOrUnPublishImages> publishOrUnpublishImages) {
		Response genericResponse = Response.builder().build();
		ResponseEntity<Response> responseEntity = null;

		try {
			uploadService.publishOrUnPublishPhoto(publishOrUnpublishImages);
			genericResponse.setMessage("Successfully Published Or Unpublished Images");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@PostMapping("/Images/UploadToGallery")
	public ResponseEntity<Response> uploadPhotoToGallery(
			@RequestBody List<PublishOrUnPublishImages> publishOrUnpublishImages) {
		Response genericResponse = Response.builder().build();
		ResponseEntity<Response> responseEntity = null;
		try {
			uploadService.uploadPhotoToGallery(publishOrUnpublishImages);
			genericResponse.setMessage("Successfully Uploaded To Employee's Gallery");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}

	@DeleteMapping("/Images")
	public ResponseEntity<Response> deletePhoto(@RequestBody List<PublishOrUnPublishImages> publishOrUnpublishImages) {
		Response genericResponse = Response.builder().build();
		ResponseEntity<Response> responseEntity = null;
		try {
			uploadService.deletePhoto(publishOrUnpublishImages);
			genericResponse.setMessage("Successfully Deleted ");
			return ResponseEntity.ok(genericResponse);
		} catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
	}
	
	@GetMapping("/Images/ActivityDetails")
	public ResponseEntity<GenericResponse> getImagesForActivity(
			@RequestParam(name = "searchCriteria", required = false) String searchCriteria,
			@RequestParam(name = "pageNo", required = false) Integer pageNo,
			@RequestParam(name = "pageSize", required = false) Integer pageSize){
		GenericResponse genericResponse = GenericResponse.builder().build();
		ResponseEntity<GenericResponse> responseEntity = null;

		try {
			SearchCriteria criteria = CommonUtils.buildSearchCriteria(searchCriteria);
			ImageResponse activityPictures=   uploadService.getImagesForActivityDetails(criteria, pageNo, pageSize);
			genericResponse.setData(activityPictures);
			return ResponseEntity.ok(genericResponse);
		}catch (Exception e) {
			genericResponse.setMessage(e.getMessage());
			responseEntity = ResponseEntity.internalServerError().body(genericResponse);
		}
		return responseEntity;
		
	}
	
}