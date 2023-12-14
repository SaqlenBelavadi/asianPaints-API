package com.speridian.asianpaints.evp.service;

import java.util.Map;

import com.speridian.asianpaints.evp.dto.EvpLOVResponse;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface EvpLovService {
	
	public EvpLOVResponse getLovsByCategory(String category) throws EvpException;
	
	public Map<String, Long> getLocationLovMap();
	
	public Map<String, Long> getThemeLovMap();
	
	public Map<String, Long> getModeLovMap();
	
	public Map<String, Long> getTagLovMap();
	
	public EvpLOVResponse getTagLovs(String themeName) throws EvpException;
	
	public EvpLOVResponse createTagByTheme(String themeName,String tagName) throws EvpException;
	
}
