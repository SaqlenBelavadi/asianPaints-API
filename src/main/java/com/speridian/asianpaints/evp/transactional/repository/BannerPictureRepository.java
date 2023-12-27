package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.speridian.asianpaints.evp.entity.BannerPicture;

public interface BannerPictureRepository extends PagingAndSortingRepository<BannerPicture, Long> , JpaSpecificationExecutor<BannerPicture> {
	
    @Query("SELECT b FROM BannerPicture b WHERE b.imageName = :imageName ORDER BY b.index")
    Optional<BannerPicture> findByBannerImageName(@Param("imageName") String imageName);
    
    @Query("SELECT b FROM BannerPicture b ORDER BY b.index ASC")
    List<BannerPicture> findByBannerList();
   
   	List<BannerPicture> findAllByOrderByIndex();
}
