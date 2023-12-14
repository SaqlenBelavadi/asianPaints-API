package com.speridian.asianpaints.evp.service.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.speridian.asianpaints.evp.constants.LovCategory;
import com.speridian.asianpaints.evp.dto.EvpLOVResponse;
import com.speridian.asianpaints.evp.dto.LovResponse;
import com.speridian.asianpaints.evp.exception.EvpException;
import com.speridian.asianpaints.evp.master.entity.EvpLocationDivision;
import com.speridian.asianpaints.evp.master.entity.EvpLov;
import com.speridian.asianpaints.evp.master.entity.EvpThemeTag;
import com.speridian.asianpaints.evp.master.repository.EvpLocationDivisionRepository;
import com.speridian.asianpaints.evp.master.repository.EvpLovRepository;
import com.speridian.asianpaints.evp.master.repository.EvpThemeTagRepository;
import com.speridian.asianpaints.evp.service.EvpLovService;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sony.lenka
 *
 */
@Service
@Slf4j
@Data
public class EvpLovServiceImpl implements EvpLovService {

	@Autowired
	private EvpLovRepository evpLovRepository;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	private Map<String, Long> locationLovMap;
	
	private Map<String, Long> themeLovMap;
	
	private Map<String, Long> modeLovMap;
	
	private List<EvpLov> tagLovMap;
	
	private Map<String, Long> tagMap;
	
	
	@Autowired
	private EvpThemeTagRepository evpThemeTagRepository;
	
	@Autowired
	private EvpLocationDivisionRepository evpLocationDivisionRepository;
	
	
	@PostConstruct
	public void intializeLovMaps() {
		log.info("Intializing LOV lists ");
		List<EvpLov> evpLovs = (List<EvpLov>) evpLovRepository.findAll();
		locationLovMap = evpLovs.stream()
				.filter(lov -> lov.getLovCategory().equals(LovCategory.LOCATIONS.getCategoryName()))
				.collect(Collectors.toMap(EvpLov::getLovValue, EvpLov::getId));
		themeLovMap = evpLovs.stream().filter(lov -> lov.getLovCategory().equals(LovCategory.THEMES.getCategoryName()))
				.collect(Collectors.toMap(EvpLov::getLovValue, EvpLov::getId));
		modeLovMap = evpLovs.stream()
				.filter(lov -> lov.getLovCategory().equals(LovCategory.MODE_OF_PARTICIPATION.getCategoryName()))
				.collect(Collectors.toMap(EvpLov::getLovValue, EvpLov::getId));
		tagLovMap = evpLovs.stream().filter(lov -> lov.getLovCategory().equals(LovCategory.TAG.getCategoryName()))
				.collect(Collectors.toList());
		tagMap = evpLovs.stream().filter(lov -> lov.getLovCategory().equals(LovCategory.TAG.getCategoryName()))
				.collect(Collectors.toMap(EvpLov::getLovValue, EvpLov::getId));

	}

	@Override
	public EvpLOVResponse getLovsByCategory(String category) throws EvpException {
		try {
			log.info("Retiriving lovs by category {} ", category);
			Optional<LovCategory> lovCategory = Arrays.asList(LovCategory.values()).stream()
					.filter(lov -> lov.getCategoryName().equals(category)).findFirst();
			if (!lovCategory.isPresent()) {
				log.error("Lov Category doesn't exist");
				throw new EvpException("Lov Category doesn't exist");
			}

			List<EvpLov> evpLovs = evpLovRepository.findByLovCategory(category);
			
			log.info("Converting LOVs to DTO");
			List<LovResponse> lovResponses = evpLovs.stream()
					.map(evp -> LovResponse.builder().lovDisplayName(evp.getLovDisplayName())
							.displayOrder(evp.getLovDisplayOrder()).lovValue(evp.getLovValue()).build())
					.collect(Collectors.toList());
			
			log.info("Lovs Response",lovResponses.toString());
			

			return EvpLOVResponse.builder().lovCategory(category).lovResponses(lovResponses).build();
		} 
		catch (EvpException e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}

	}
	
	@Override
	public EvpLOVResponse getTagLovs(String themeName) throws EvpException {
		
		EvpLOVResponse evpLovResponse=EvpLOVResponse.builder().build();
		try {
			
			if(!Optional.ofNullable(themeName).isPresent()) {
				throw new EvpException("Theme Name is Empty");
			}
			log.info("Retiriving tags by theme {} ", themeName);
			
			Long themeId=themeLovMap.get(themeName);
			
			List<EvpThemeTag> evpThemeTagList= evpThemeTagRepository.findByThemeId(themeId);
			
			if(evpThemeTagList.isEmpty()) {
				throw new EvpException("No Tags present for theme "+themeName);
			}else {
				List<Long> themeTagIds= evpThemeTagList.stream().map(EvpThemeTag::getTagId).collect(Collectors.toList());
				
				List<EvpLov> tagLovs= tagLovMap.stream().filter(tagLov->themeTagIds.contains(tagLov.getId())).collect(Collectors.toList());
				
				List<LovResponse> lovResponses= tagLovs.stream().map(taglov->LovResponse.builder().displayOrder(taglov.getLovDisplayOrder()).lovDisplayName(taglov.getLovDisplayName()).lovValue(taglov.getLovValue()).build()).collect(Collectors.toList());
				
				evpLovResponse.setLovResponses(lovResponses);
				
				evpLovResponse.setLovCategory(LovCategory.TAG.getCategoryName());
				
				return evpLovResponse;
			}
			
			
		}catch (EvpException e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
		
	}
	
	@Override
	public Map<String, Long> getLocationLovMap(){
		return locationLovMap;
	}
	
	@Override
	public Map<String, Long> getThemeLovMap(){
		return themeLovMap;
	}
	
	@Override
	public Map<String, Long> getModeLovMap(){
		return modeLovMap;
	}
	
	@Override
	public Map<String, Long> getTagLovMap(){
		return tagMap;
	}

	@Override
	public EvpLOVResponse createTagByTheme(String themeName, String tagName) throws EvpException {
		EvpLOVResponse evpLovResponse=EvpLOVResponse.builder().build();
		try {
			log.info("Creating tags by theme {} ", themeName);
			Long themeId=themeLovMap.get(themeName);
			
			if(!Optional.ofNullable(tagName).isPresent()) {
				throw new EvpException("Tag Name cannot be blank");
			}
			
			List<EvpThemeTag> evpThemeTagList= evpThemeTagRepository.findByThemeId(themeId);
			
			List<Long> themeTagIds= evpThemeTagList.stream().map(EvpThemeTag::getTagId).collect(Collectors.toList());
			
			
			
			if(Optional.ofNullable(tagMap.get(tagName)).isPresent()) {
				log.error("Tag Name with name "+tagName+"is already Present");
				throw new EvpException("Same Activity tag is already exists");
			}
			
			List<EvpLov> tagLovs= tagLovMap.stream().filter(tagLov->themeTagIds.contains(tagLov.getId())).collect(Collectors.toList());
			
			
			Optional<String> maxLovDisplayOrder=tagLovs.stream().map(EvpLov::getLovDisplayOrder).max(Comparator.comparing(v->v));
			
			if(	maxLovDisplayOrder.isPresent() || Optional.ofNullable(themeId).isPresent()) {
				
				
					log.info("Finding maximum order id");
					Integer lovOrder=maxLovDisplayOrder.isPresent()? Integer.parseInt(maxLovDisplayOrder.get())+1:1;
					EvpLov evpLov=new EvpLov();
					evpLov.setLovCategory(LovCategory.TAG.getCategoryName());
					evpLov.setLovDisplayName(tagName);
					evpLov.setLovValue(tagName);
					evpLov.setLovDisplayOrder(String.valueOf(lovOrder));
					log.info("Creating new Evp Lov for tag {}",tagName);
					evpLov= evpLovRepository.save(evpLov);
					
					log.info("Creating new Evp theme tag link for tag {}",tagName);
					EvpThemeTag evpThemeTag=new EvpThemeTag();
					evpThemeTag.setThemeId(themeId);
					evpThemeTag.setTagId(evpLov.getId());
					evpThemeTagRepository.save(evpThemeTag);
					
					log.info("Refreshing LOV maps");
					intializeLovMaps();
					
					evpLovResponse.setMessage("Successfully Created tag with name "+tagName);
					
					return evpLovResponse;
				
				
			}
			else {
				throw new EvpException("No Element Present");
			}
			
			
		}catch (EvpException e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException(e.getMessage());
		}
		catch (Exception e) {
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
			throw new EvpException("Internal Server Error");
		}
	}
	
	

}
