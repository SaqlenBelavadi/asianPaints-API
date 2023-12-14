package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.ActivityPicture;
import com.speridian.asianpaints.evp.util.ImageType;

public interface ActivityPictureRepository extends PagingAndSortingRepository<ActivityPicture, Long> , JpaSpecificationExecutor<ActivityPicture>{

	@Query("from ActivityPicture a where a.activityPictureId=:activityPictureId and a.imageType='ADMIN_UPLOAD'")
	public List<ActivityPicture> findByActivityPictureId(String activityPictureId);

	@Query("from ActivityPicture a where a.imageType='CREATIVE' and a.activityPictureId in (:activityList) and a.published is NULL")
	public Page<ActivityPicture> findByActivityTagAndActivityName(List<String> activityList, Pageable pageable);

	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.published!=NULL and a.deleted is NULL and a.uploadedBy=:username")
	public Page<ActivityPicture> findByActivityThemeAndActivityName(List<String> activityList,String username, Pageable pageable);

	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.published!=NULL and a.deleted is NULL")
	public Page<ActivityPicture> findByActivityThemeAndActivityName(List<String> activityList, Pageable pageable);

	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName and a.imageName=:imageName and  a.imageType=:imageType")
	public List<ActivityPicture> findByImageNameAndActivityName(String imageName, String activityName,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName and a.imageName=:imageName and  a.imageType='EMPLOYEE_UPLOAD'")
	public List<ActivityPicture> findByImageNameAndActivityName(String imageName, String activityName);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.published=true and a.deleted is NULL")
	public Page<ActivityPicture> findImagesForEmployee(List<String> activityList, Pageable pageable);
	
	@Query("select a from ActivityPicture a where a.imageName in (:imageNames)")
	public List<ActivityPicture> findByImageNames(Set<String> imageNames);
	
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.published!=NULL and a.deleted is NULL and a.uploadedBy=:username")
	public List<ActivityPicture> findByActivityName(List<String> activityList,String username);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.deleted is NULL and a.uploadedBy=:username")
	public List<ActivityPicture> findByActivityNameForDetails(List<String> activityList,String username);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.deleted is NULL and a.uploadedBy=:username")
	public Page<ActivityPicture> findByActivityNameForDetails(List<String> activityList,String username,Pageable pageable);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList) and a.deleted is NULL and a.uploadedBy=:username and ( a.manualUpload='0' or a.manualUpload is NULL)")
	public List<ActivityPicture> findByActivityNameForDetailsWithoutPage(List<String> activityList,String username);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList)")
	public List<ActivityPicture> findByActivityName(List<String> activityList);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList)")
	public Page<ActivityPicture> findByActivityNameForDetails(List<String> activityList,Pageable pageable);
	
	@Query("from ActivityPicture a where a.imageType='EMPLOYEE_UPLOAD' and a.activityPictureId in (:activityList)")
	public List<ActivityPicture> findByActivityNameForDetailsWithoutPage(List<String> activityList);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName and a.imageName=:imageName and  a.imageType=:imageType")
	public List<ActivityPicture> findByImageNameAndActivityNameAndImageType(String imageName, String activityName,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName and a.imageName in (:imageNames) and  a.imageType=:imageType")
	public List<ActivityPicture> findByImageNamesAndActivityNameAndImageType(List<String>  imageNames, String activityName,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName  and  a.imageType=:imageType and a.promotionId IS NULL")
	public List<ActivityPicture> findByActivityNameAndImageTypeForOrphanPromotions(String activityName,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName  and  a.imageType=:imageType")
	public List<ActivityPicture> findByActivityNameAndImageType(String activityName,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.activityPictureId=:activityName and a.imageName=:imageName")
	public List<ActivityPicture> findByImageNameAndActivityNameAndImageType(String imageName, String activityName);
	
	@Query("from ActivityPicture a where a.imageType='ADMIN_UPLOAD' and a.activityPictureId in (:activityList)")
	public List<ActivityPicture> findByActivityNameAdminUpload(List<String> activityList);
	
	@Query("select a from ActivityPicture a where a.activityPictureId in (:activityNames)  and  a.imageType=:imageType")
	public List<ActivityPicture> findByActivityNamesAndImageType(List<String> activityNames,ImageType imageType);
	
	@Query("select a from ActivityPicture a where a.imageName in (:imageNames) and  a.activityPictureId =:activityId")
	public List<ActivityPicture> findByImageNamesAndActivityId(Set<String> imageNames,String activityId);
	
	@Query("select a from ActivityPicture a where a.promotionId in (:promotionIds) and a.imageType='PROMOTIONS'")
	public List<ActivityPicture> findByPromotionIds(List<Long> promotionIds);
	
	@Query("from ActivityPicture a where a.activityPictureId=:activityPictureId")
	public List<ActivityPicture> findByAllActivityPictureId(String activityPictureId);
}
