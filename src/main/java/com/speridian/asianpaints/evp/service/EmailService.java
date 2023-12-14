package com.speridian.asianpaints.evp.service;

import org.springframework.core.io.FileSystemResource;

import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;

public interface EmailService {

	void sendEmail(EmailType emailType, EmailTemplateData emailTemplateData,String emailId,FileSystemResource fileSystemResource) ;
}
