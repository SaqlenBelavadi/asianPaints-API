package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import com.speridian.asianpaints.evp.entity.VoiceOfChange;

public interface VoiceOfChangeRepository extends PagingAndSortingRepository<VoiceOfChange, Long> , JpaSpecificationExecutor<VoiceOfChange> {

	 @Query("SELECT b FROM VoiceOfChange b WHERE b.imageName = :imageName")
	    Optional<VoiceOfChange> findByVoiceOfChangeImageName(@Param("imageName") String imageName);
	 
	 List<VoiceOfChange> findAllByOrderByIndex();
}
