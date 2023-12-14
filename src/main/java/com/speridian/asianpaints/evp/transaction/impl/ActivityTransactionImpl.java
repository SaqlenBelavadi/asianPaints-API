package com.speridian.asianpaints.evp.transaction.impl;

import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFinancial;
import com.speridian.asianpaints.evp.transaction.ActivityTransaction;
import com.speridian.asianpaints.evp.transactional.repository.ActivityFinancialRepository;
import com.speridian.asianpaints.evp.transactional.repository.ActivityRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ActivityTransactionImpl implements ActivityTransaction {

	@Autowired
	private ActivityFinancialRepository activityFinancialRepository;
	
	@Autowired
	private ActivityRepository activityRepository;
	
	@Override
	@Transactional(value = TxType.REQUIRED, rollbackOn = Exception.class)
	public void createOrUpdateActivityFinancial(ActivityFinancial activityFinancial, Activity existingActivity) {
		log.info("Save or Update Activity Finacial");
		activityFinancial=activityFinancialRepository.save(activityFinancial);
		
		existingActivity.setActivityFinancialId(activityFinancial.getId());
		
	}


	@Transactional(value = TxType.REQUIRED, rollbackOn = Exception.class)
	@Override
	public Activity createOrUpdateActivity(Activity existingActivity) {
		log.info("Create or Update Activity");
		return activityRepository.save(existingActivity);
	}


	@Override
	@Transactional(value = TxType.REQUIRED, rollbackOn = Exception.class)
	public void deleteActivityAndFinancialDetails(Activity existingActivity, Long activityFinacialId,
			Optional<ActivityFinancial> financialOpt) {

		if(financialOpt.isPresent()) {
			log.info("Deleting Activity Financial with id {}",activityFinacialId);
			activityFinancialRepository.delete(financialOpt.get());
		}
		log.info("Deleting Activity Financial with name {}",existingActivity.getActivityName());
		activityRepository.delete(existingActivity);
	
		
	}
	
	
	

}
