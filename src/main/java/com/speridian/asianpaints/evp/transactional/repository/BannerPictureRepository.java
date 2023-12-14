package com.speridian.asianpaints.evp.transactional.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.speridian.asianpaints.evp.entity.BannerPicture;

public interface BannerPictureRepository extends PagingAndSortingRepository<BannerPicture, Long> , JpaSpecificationExecutor<BannerPicture> {
	
    @Query("SELECT b FROM BannerPicture b WHERE b.imageName = :imageName")
    Optional<BannerPicture> findByBannerImageName(@Param("imageName") String imageName);
	
}
