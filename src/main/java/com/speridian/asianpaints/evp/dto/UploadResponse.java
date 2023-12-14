package com.speridian.asianpaints.evp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

	@JsonProperty("assetdata")
	private Object assetdata;
	@JsonProperty("message")
	private String message;
	@JsonProperty("assetUrl")
	private String assetUrl;
	@JsonProperty("status")
	private String status;
	
	private String fileLocation;

	@JsonIgnore
	private String activityName;

}
