package com.speridian.asianpaints.evp.master.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.master.entity.EvpThemeTag;

public interface EvpThemeTagRepository extends PagingAndSortingRepository<EvpThemeTag, Long> {
	

	public List<EvpThemeTag> findByThemeId(Long themeId);
	
}
