package com.speridian.asianpaints.evp.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.speridian.asianpaints.evp.constants.LovCategory;
import com.speridian.asianpaints.evp.dto.EvpLOVResponse;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.service.EvpLovService;

/**
 * @author sony.lenka
 *
 */
@RestController
@RequestMapping("/api/evp/v1/")
public class EvpLovController {
	
	
	@Autowired
	private EvpLovService evpLovService;
	
	
	
	@GetMapping("/lov")
	public ResponseEntity<EvpLOVResponse> getLovsByCategory(@RequestParam("category") String category){
		EvpLOVResponse evpLOVResponse=null;
		try {
			 evpLOVResponse= evpLovService.getLovsByCategory(category);
			return ResponseEntity.ok(evpLOVResponse);
		} catch (EvpException e) {
			evpLOVResponse=EvpLOVResponse.builder().message(e.getMessage()).build();
			return ResponseEntity.internalServerError().body(evpLOVResponse);
		}
		
		
	}
	
	@GetMapping("/lov/tags")
	public ResponseEntity<EvpLOVResponse> getTagLovs(@RequestParam("themeName") String themeName){
		EvpLOVResponse evpLOVResponse=null;
		try {
			 evpLOVResponse= evpLovService.getTagLovs(themeName);
			return ResponseEntity.ok(evpLOVResponse);
		} catch (EvpException e) {
			evpLOVResponse=EvpLOVResponse.builder().message(e.getMessage()).build();
			return ResponseEntity.internalServerError().body(evpLOVResponse);
		}
		
		
	}
	
	@GetMapping("/lovs")
	public List<String> getLovs() throws EvpException{
			return Arrays.asList(LovCategory.values()).stream().map(LovCategory::getCategoryName).collect(Collectors.toList());
		
	}
	
	@PostMapping("/lov")
	public ResponseEntity<EvpLOVResponse> createTagLovForTheme(@RequestParam("themeName") String themeName,@RequestParam("tagName") String tagName){
		
		EvpLOVResponse evpLOVResponse=null;
		
		try {
			evpLOVResponse=evpLovService.createTagByTheme(themeName, tagName);
			
			return ResponseEntity.ok(evpLOVResponse);
		}catch (Exception e) {
			evpLOVResponse=EvpLOVResponse.builder().message(e.getMessage()).build();
			return ResponseEntity.internalServerError().body(evpLOVResponse);
		}
		
	}
	

}
