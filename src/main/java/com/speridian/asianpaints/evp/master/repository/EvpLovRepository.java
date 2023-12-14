package com.speridian.asianpaints.evp.master.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.master.entity.EvpLov;

public interface EvpLovRepository extends PagingAndSortingRepository<EvpLov, Long> {
	
	
	public List<EvpLov> findByLovCategory(String lovCategory);

}
