package com.speridian.asianpaints.evp.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GalleryResponseDTO {

	private Map<String,PublishedImages> publishedImages;
	
	private Map<String,PublishedImages> unPublishedImages;
	
	private Map<String,CreativeImages> creativeImages;
	
}
