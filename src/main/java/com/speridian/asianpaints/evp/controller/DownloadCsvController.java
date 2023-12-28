package com.speridian.asianpaints.evp.controller;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	
    @GetMapping("/downloadPdf")
    public ResponseEntity<Resource> downloadPdf() throws IOException {
        // Logic to read the PDF file
        Resource pdfFile = new ClassPathResource("evp.pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Evp guidelines for Employees.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfFile.contentLength())
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
    }

}
