package com.speridian.asianpaints.evp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.service.DownloadCsvService;

@RequestMapping("/api/evp/v1/")
@RestController
public class DownloadCsvController {
	
	@Autowired
	private DownloadCsvService downloadCsvService;

	@PostMapping("/downloadCSV")
	public void exportToCSV(@RequestParam(name="category", required = false) String category,
			@RequestParam(name = "searchCriteria", required = false) String searchCriteria
			,@RequestParam(name = "activityType", required = false)String activityType,
			@RequestParam(name = "dashBoardDetails", defaultValue =  "false")boolean dashBoardDetails) {
		
		try {
			downloadCsvService.writeCsvData(category, searchCriteria,activityType,dashBoardDetails);
		} catch (IOException | EvpException e) {
			throw new RuntimeException("Unable to export CSV data");
		}
		
	}
	
	@PostMapping("/downloadCSV/DashBoard")
	public void exportToCSVForDashBoard(@RequestParam(name = "category") String category,
			@RequestParam(name = "subcategory") String subcategory,
			@RequestParam(name = "searchCriteria", required = false) String searchCriteria) {
		try {
			downloadCsvService.writeCsvDataForDashBoard(category, subcategory, searchCriteria);
		} catch (IOException | EvpException e) {
			throw new RuntimeException("Unable to export CSV data");
		}
	}

}
