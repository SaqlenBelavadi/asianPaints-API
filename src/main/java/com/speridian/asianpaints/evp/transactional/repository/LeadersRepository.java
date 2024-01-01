package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import com.speridian.asianpaints.evp.entity.Leaders;

public interface LeadersRepository extends PagingAndSortingRepository<Leaders, Long> , JpaSpecificationExecutor<Leaders> {

	 @Query("SELECT b FROM Leaders b WHERE b.url = :imageName")
	    Optional<Leaders> findByLeaderImageName(@Param("imageName") String url);
	 
	 List<Leaders> findAllByOrderByIndex();
}
