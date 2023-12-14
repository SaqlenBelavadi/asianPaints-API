package com.speridian.asianpaints.evp.transaction;

import java.util.Optional;

import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.entity.ActivityFinancial;

public interface ActivityTransaction {
	
	public void createOrUpdateActivityFinancial(ActivityFinancial activityFinancial, Activity existingActivity);

	public Activity createOrUpdateActivity(Activity existingActivity);
	
	public void deleteActivityAndFinancialDetails(Activity existingActivity, Long activityFinacialId,
			Optional<ActivityFinancial> financialOpt);
	
}
