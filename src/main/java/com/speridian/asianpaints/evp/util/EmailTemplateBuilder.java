package com.speridian.asianpaints.evp.util;

import java.util.HashMap;
import java.util.Map;

import com.speridian.asianpaints.evp.constants.EmailTemplates;
import com.speridian.asianpaints.evp.constants.EmailType;
import com.speridian.asianpaints.evp.dto.EmailTemplateData;

public class EmailTemplateBuilder {

	public static Map<String, String> buildEmailTemplate(EmailType emailType,EmailTemplateData emailTemplateData) {

		Map<String, String> emailMap=new HashMap<>();
		
		switch (emailType) {
		case ENROLL_ACTIVITY:
			String emailSubject=EmailTemplates.EMPLOYEE_ENROLL_ACTIVITY_SUBJECT;
			String emailBody=EmailTemplates.EMPLOYEE_ENROLL_ACTIVITY.replaceAll("VOLUNTEER_NAME", emailTemplateData.getEmployeeName())
			.replaceAll("LINK", emailTemplateData.getActivityLink() == null ? ""
					: emailTemplateData.getActivityLink());
			emailMap.put("SUBJECT", emailSubject);
			emailMap.put("BODY", emailBody);
			break;

		case CONFIRM_PARTICIPATION:

			String subject=EmailTemplates.EMPLOYEE_CONFIRMS_PARTICIPATION_SUBJECT;
			String body=EmailTemplates.EMPLOYEE_CONFIRMS_PARTICIPATION.replaceAll("VOLUNTEER_NAME", emailTemplateData.getEmployeeName())
			.replaceAll("ACTIVITY_NAME", emailTemplateData.getActivityName());
			emailMap.put("SUBJECT", subject);
			emailMap.put("BODY", body);
			break;

		case NEED_SUPPORT_CCSR:

			String needSupportCCSRSubject=EmailTemplates.NEED_SUPPORT_FROM_SUBJECT.replaceAll("ActivityCreator", emailTemplateData.getCreatedBy())
			.replaceAll("ActivityName", emailTemplateData.getActivityName())
			.replaceAll("Location", emailTemplateData.getLocation());
			String needSupportCCSRBody=EmailTemplates.NEED_SUPPORT_FROM_CCSR.replaceAll("REQUEST_FROM_CCSR", emailTemplateData.getRequestFromCCSR())
			.replaceAll("ActivityName", emailTemplateData.getActivityName())
			.replaceAll("ADMIN_NAME", emailTemplateData.getCreatedBy());
			emailMap.put("SUBJECT", needSupportCCSRSubject);
			emailMap.put("BODY", needSupportCCSRBody);
			break;

		case PUBLISH_ACTIVITY:
			String publishActivitySubject=EmailTemplates.ADMIN_PUBLISH_ACTIVITY_SUBJECT
			.replaceAll("ActivityName", emailTemplateData.getActivityName())
			.replaceAll("ActivityCreator", emailTemplateData.getCreatedBy())
			.replaceAll("Location", emailTemplateData.getLocation())
			.replaceAll("DateOfActivity", emailTemplateData.getCreatedDate());
			
			String publishActivityBody = EmailTemplates.ADMIN_PUBLISH_ACTIVITY;
			publishActivityBody = publishActivityBody.replaceAll("ACTIVITY_NAME", emailTemplateData.getActivityName())
					.replaceAll("SCHEDULED_BY", emailTemplateData.getCreatedBy())
					.replaceAll("LOCATION", emailTemplateData.getLocation())
					.replaceAll("EMATERIAL_EXPENSE",
							emailTemplateData.getActivityFinancials().getMaterialOrCreativeExpense())
					.replaceAll("ELOGISTIC_EXPENSE", emailTemplateData.getActivityFinancials().getLogisticExpense())
					.replaceAll("EGRATIFICATION_EXPENSE",
							emailTemplateData.getActivityFinancials().getGratificationExpense())
					.replaceAll("EOTHER_EXPENSE", emailTemplateData.getActivityFinancials().getOtherExpense())
					.replaceAll("AMATERIAL_EXPENSE",
							emailTemplateData.getActivityFinancials().getActualMaterialExpense() == null ? ""
									: emailTemplateData.getActivityFinancials().getActualMaterialExpense())
					.replaceAll("ALOGISTIC_EXPENSE",
							emailTemplateData.getActivityFinancials().getActualLogisticExpense() == null ? ""
									: emailTemplateData.getActivityFinancials().getActualLogisticExpense())
					.replaceAll("AGRATIFICATION_EXPENSE",
							emailTemplateData.getActivityFinancials().getActualGratificationExpense() == null ? ""
									: emailTemplateData.getActivityFinancials().getActualGratificationExpense())
					.replaceAll("AOTHER_EXPENSE",
							emailTemplateData.getActivityFinancials().getActualOtherExpense() == null ? ""
									: emailTemplateData.getActivityFinancials().getActualOtherExpense())
					.replaceAll("ACTIVITY_LINK",
							emailTemplateData.getActivityLink() == null ? ""
									: emailTemplateData.getActivityLink());
			emailMap.put("SUBJECT", publishActivitySubject);
			emailMap.put("BODY", publishActivityBody);
			break;

		case REJECTED_ATTENDANCE:
			String rejectedAttendanceSubject=EmailTemplates.REJECTED_ATTENDANCE_BY_ADMIN_SUBJECT;
			String rejectedAttendance=EmailTemplates.REJECTED_ATTENDANCE_BY_ADMIN.replaceAll("EMPLOYEE_NAME", emailTemplateData.getEmployeeName())
					.replaceAll("ACTIVITY_NAME", emailTemplateData.getActivityName());
			emailMap.put("SUBJECT", rejectedAttendanceSubject);
			emailMap.put("BODY", rejectedAttendance);
			break;

		default:
			break;
		}
		
		return emailMap;

	}

}
