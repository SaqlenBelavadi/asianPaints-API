package com.speridian.asianpaints.evp.exception;

import lombok.Data;

@Data
public class EvpException extends Exception {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;

	public EvpException(String message) {
		super();
		this.message = message;
	}
	
	
	

}
