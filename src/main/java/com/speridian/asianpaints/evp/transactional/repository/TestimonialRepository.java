package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import com.speridian.asianpaints.evp.entity.TestimonialData;

public interface TestimonialRepository extends PagingAndSortingRepository<TestimonialData, Long> , JpaSpecificationExecutor<TestimonialData> {


	 @Query("SELECT b FROM TestimonialData b WHERE b.imageName = :imageName")
	    Optional<TestimonialData> findByTestimonialImageName(@Param("imageName") String imageName);
	 
	 List<TestimonialData> findAllByOrderByIndex();

}
