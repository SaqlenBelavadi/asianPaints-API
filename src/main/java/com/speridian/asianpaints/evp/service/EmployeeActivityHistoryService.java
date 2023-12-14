package com.speridian.asianpaints.evp.service;

import com.speridian.asianpaints.evp.constants.EmployeeActivityStatus;
import com.speridian.asianpaints.evp.dto.EmployeeActivityHistoryDTO;
import com.speridian.asianpaints.evp.entity.Activity;
import com.speridian.asianpaints.evp.exception.EvpException;

public interface EmployeeActivityHistoryService {
	
	public void updateEmployeeActivityHistory(EmployeeActivityHistoryDTO EmployeeActivityHistoryDTO) throws EvpException;
	

}
