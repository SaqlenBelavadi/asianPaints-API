
package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import com.speridian.asianpaints.evp.entity.Video;

public interface VideoRepository extends PagingAndSortingRepository<Video, Long> , JpaSpecificationExecutor<Video> {


	 @Query("SELECT b FROM Video b WHERE b.videoURL = :videoURL")
	    Optional<Video> findByVideoURL(@Param("videoURL") String videoURL);
	 
	 List<Video> findAllByOrderByIndex();

}
