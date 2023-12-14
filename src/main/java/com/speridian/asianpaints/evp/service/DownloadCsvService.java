package com.speridian.asianpaints.evp.service;

import java.io.IOException;

import com.speridian.asianpaints.evp.exception.EvpException;

/**
 * @author sony.lenka
 *
 */
public interface DownloadCsvService {

	public void writeCsvData(String category, String searchCriteria,String activityType,boolean dashBoardDetails) throws IOException, EvpException;
	
	public void writeCsvDataForDashBoard(String category,String subcategory, String searchCriteria) throws IOException, EvpException;

}
