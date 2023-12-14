package com.speridian.asianpaints.evp.transactional.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.speridian.asianpaints.evp.entity.MailConfig;

public interface MailConfigRepository extends JpaRepository<MailConfig, String> {
	
	public MailConfig findByConfigType(String configType);

}
