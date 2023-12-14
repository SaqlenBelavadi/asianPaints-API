package com.speridian.asianpaints.evp.transactional.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.ActivityFinancial;

public interface ActivityFinancialRepository extends PagingAndSortingRepository<ActivityFinancial, Long> {
	
	@Query(value = "SELECT * FROM EVP_ACTIVITY_FINANCIAL  where ID in (:ids)",nativeQuery = true)
	public Page<ActivityFinancial> getAllByIds(List<String> ids,Pageable pageable);
	
	@Query(value = "SELECT * FROM EVP_ACTIVITY_FINANCIAL  where ID in (:ids)",nativeQuery = true)
	public List<ActivityFinancial> getAllByIds(List<String> ids);
	
	
	@Query(value = "SELECT * FROM EVP_ACTIVITY_FINANCIAL  where ID in (:ids) ORDER BY createdon DESC",nativeQuery = true)
	public Page<ActivityFinancial> getAllByIdsSortedByCreatedDate(List<String> ids,Pageable pageable);
	
	@Query(value = "SELECT * FROM EVP_ACTIVITY_FINANCIAL  where ID in (:ids) ORDER BY createdon DESC",nativeQuery = true)
	public List<ActivityFinancial> getAllByIdsSortedByCreatedDate(List<String> ids);

}
