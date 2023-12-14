package com.speridian.asianpaints.evp.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;
import com.speridian.asianpaints.evp.entity.MailConfig;
import com.speridian.asianpaints.evp.service.EmailService;
import com.speridian.asianpaints.evp.transactional.repository.MailConfigRepository;
import com.speridian.asianpaints.evp.util.CommonUtils;
import com.speridian.asianpaints.evp.util.EmailTemplateBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

	@Value("${evp.apigee.emailApi}")
	private String emailApigeeUrl;
	
	@Autowired
	private MailConfigRepository mailConfigRepository;

	@Override
	public void sendEmail(EmailType emailType, EmailTemplateData emailTemplateData, String emailId,
			FileSystemResource fileSystemResource) {
		try {
			MailConfig mailConfig= mailConfigRepository.findByConfigType("MAIL_SERVICE");
			
			if(mailConfig!=null) {
			Executors.newSingleThreadExecutor().submit(() -> {
				String emailService=mailConfig.getConfigValue();
				log.info("Mail Host {}",mailConfig.getConfigValue());
				MultiValueMap<String, Object> imageMap = new LinkedMultiValueMap<String, Object>();
				Map<String, String> emailMap = EmailTemplateBuilder.buildEmailTemplate(emailType, emailTemplateData);
				String subject = emailMap.get("SUBJECT");
				String body = emailMap.get("BODY");
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				imageMap.add("reqFrom", emailService==null?"quotation@apps.asianpaints.com":emailService);
				imageMap.add("reqSubject", subject);
				imageMap.add("reqBody", body);
				imageMap.add("reqTo", emailId);

				if (Optional.ofNullable(fileSystemResource).isPresent()) {
					imageMap.add("reqAttachment1", fileSystemResource);
				}

				HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(
						imageMap, headers);

				RestTemplate restTemplate = CommonUtils.buildRestTemplate(false, null, null);

				restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
				restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
				ResponseEntity<String> responseEntity = restTemplate.exchange(emailApigeeUrl, HttpMethod.POST, request,
						String.class);
				if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {

					log.info("Email Sent Successfully {}", responseEntity.getBody());

				} else {
					log.error("Email Error Code " + responseEntity.getStatusCode());
				}

				
				if (Optional.ofNullable(fileSystemResource).isPresent()) {
					try {
						Files.deleteIfExists(fileSystemResource.getFile().toPath());
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
				 

			});
			}
		} catch (Exception e) {
			log.error("Unable to send email");
			log.error(e + Arrays.asList(e.getStackTrace()).stream().map(Objects::toString)
					.collect(Collectors.joining("\n")));
		}
	}

}
