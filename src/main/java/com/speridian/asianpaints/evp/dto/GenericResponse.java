package com.speridian.asianpaints.evp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse {
	
	private Object data;
	
	private String message;
	

}
