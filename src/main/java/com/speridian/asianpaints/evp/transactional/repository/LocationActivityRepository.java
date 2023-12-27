package com.speridian.asianpaints.evp.transactional.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.SelectedActivity;

public interface LocationActivityRepository  extends PagingAndSortingRepository<SelectedActivity, Long> , JpaSpecificationExecutor<SelectedActivity> {

	@Query("Select s from SelectedActivity s where s.location=:location")
	public Optional<SelectedActivity> findByLocation(String location);
	

}
