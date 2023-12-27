package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import com.speridian.asianpaints.evp.entity.PartnersLogo;

public interface PartnersLogoRepository extends PagingAndSortingRepository<PartnersLogo, Long> , JpaSpecificationExecutor<PartnersLogo> {
	
	 @Query("SELECT b FROM PartnersLogo b WHERE b.imageName = :imageName")
	    Optional<PartnersLogo> findByPartnersLogoImageName(@Param("imageName") String imageName);
	 
	 List<PartnersLogo> findAllByOrderByIndex();
}