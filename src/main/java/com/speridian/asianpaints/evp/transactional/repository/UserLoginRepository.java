package com.speridian.asianpaints.evp.transactional.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.speridian.asianpaints.evp.entity.UserLogin;

public interface UserLoginRepository extends PagingAndSortingRepository<UserLogin, Long> {
	
	public Optional<UserLogin>  findByUsername(String username);

}
