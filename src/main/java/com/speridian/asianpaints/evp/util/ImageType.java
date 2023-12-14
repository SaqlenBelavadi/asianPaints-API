package com.speridian.asianpaints.evp.util;

public enum ImageType {

	ADMIN_UPLOAD("AdminUpload"),CREATIVE("Creative"),EMPLOYEE_UPLOAD("EmployeeUpload"),PAST_VIDEOS("PastVideos"),PROMOTIONS("PROMOTION"),ALL("ALL");
	
	private String imageType;

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	private ImageType(String imageType) {
		this.imageType = imageType;
	}
	
	
	
}
