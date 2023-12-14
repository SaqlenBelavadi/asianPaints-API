package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LovResponse {
	
	private String lovDisplayName;
	
	private String lovValue;
	
	private String displayOrder;

}
