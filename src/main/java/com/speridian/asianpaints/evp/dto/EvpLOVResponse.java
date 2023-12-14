package com.speridian.asianpaints.evp.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvpLOVResponse {

	private String lovCategory;
	
	private List<LovResponse> lovResponses;
	
	private String message;
	
}
