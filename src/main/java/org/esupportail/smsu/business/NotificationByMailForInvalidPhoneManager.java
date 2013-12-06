package org.esupportail.smsu.business;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.esupportail.commons.services.i18n.I18nService;
import org.esupportail.commons.services.ldap.LdapUser;
import org.esupportail.commons.services.logging.Logger;
import org.esupportail.commons.services.logging.LoggerImpl;
import org.esupportail.smsu.exceptions.ldap.LdapUserNotFoundException;
import org.esupportail.smsu.services.client.SmsuapiWS;
import org.esupportail.smsu.services.ldap.LdapUtils;
import org.esupportail.smsu.services.smtp.SmtpServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Business layer concerning smsu notification invalid mail.
 *
 */
public class NotificationByMailForInvalidPhoneManager {
	
	@Autowired private I18nService i18nService;
	@Autowired private LdapUtils ldapUtils;
	@Autowired private SmtpServiceUtils smtpServiceUtils;
	@Autowired private SmsuapiWS smsuapiWS;

	/**
	 * Log4j logger.
	 */
	private final Logger logger = new LoggerImpl(getClass());
	
	///////////////////////////////////////
	//  constructor
	//////////////////////////////////////
	/**
	 * Bean constructor.
	 */
	public NotificationByMailForInvalidPhoneManager() {
		
	}
	
	///////////////////////////////////////
	//  Private method
	//////////////////////////////////////
	/**
	 * Get list of phone numbers in blacklist.
	 * @return return set of phone numbers 
	 */
	private Set<String> getListePhoneNumberInBlackList() {
		return smsuapiWS.getListPhoneNumbersInBlackList(); 
	}
	
	/**
	 * retrieve mail based on uid.
	 * @param uid
	 * @return
	 */
	private String getMail(final String uid) {
		if (logger.isDebugEnabled()) {
			logger.debug("Get the mail for user:  " + uid);
		}
		String mail = null;
		try {
			mail = ldapUtils.getUserEmailAdressByUid(uid);
			if (logger.isDebugEnabled()) {
				logger.debug("The mail is:  " + mail);
			}
		} catch (LdapUserNotFoundException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
		
		return mail;
	}

	///////////////////////////////////////
	//  Public method
	//////////////////////////////////////
	/**
	 * sendMails method called by Quartz task.
	 * @return
	 */
	public void sendMails() {
		if (logger.isDebugEnabled()) {
			logger.debug("Enter to sendMails method");
		}
		// 1 - Retrieve the list of phone numbers in blacklist 
		Set<String> listPhones = getListePhoneNumberInBlackList();
		
			for (String phoneNumber : listPhones) {
				if (StringUtils.isEmpty(phoneNumber)) // ensure weird numbers are discarded
					continue;

				if (logger.isDebugEnabled()) {
					logger.debug("search ldapUser for phone number:" + phoneNumber);
				}
			// 2 - Retrieve the ldapUser 	
			List<LdapUser>  list = ldapUtils.searchLdapUsersByPhoneNumber(phoneNumber);
		if (list.size() == 1) {
    		String uid = list.get(0).getId();
    		String mail = getMail(uid);
			    		if (mail != null) {
			    		// 3 - Send mail
			        	String subject = i18nService.getString("MSG.SUBJECT.MAIL.TO.INVALIDPHONE", 
			        					 i18nService.getDefaultLocale());
			        	
			    		String textBody = i18nService.getString("MSG.TEXTBOX.MAIL.TO.INVALIDPHONE",
			    					  i18nService.getDefaultLocale(), phoneNumber);
			        	
			        	smtpServiceUtils.sendOneMessage(mail, subject, textBody);
			    		} else {
			    		logger.error("no mail for blacklisted uid " + uid + " with phone number " + phoneNumber);
			    		}
    		} else {
    		logger.info("no user found for once-invalid-so-blacklisted phone number: " + phoneNumber);
    		}
			}	
	}

	///////////////////////////////////////
	//  setters for spring objects
	//////////////////////////////////////
	/**
	 * @param ldapUtils
	 */
	public void setLdapUtils(final LdapUtils ldapUtils) {
		this.ldapUtils = ldapUtils;
	}
	
	/**
	 * @param listPhoneNumbersInBlackListClient
	 */
	public void setListPhoneNumbersInBlackListClient(
			final ListPhoneNumbersInBlackListClient listPhoneNumbersInBlackListClient) {
		this.listPhoneNumbersInBlackListClient = listPhoneNumbersInBlackListClient;
	}

	/**
	 * @param smtpServiceUtils
	 */
	public void setSmtpServiceUtils(final SmtpServiceUtils smtpServiceUtils) {
		this.smtpServiceUtils = smtpServiceUtils;
	}
	
	//////////////////////////////////////////////////////////////
	// Getter and Setter of i18nService
	//////////////////////////////////////////////////////////////
	/**
	 * Set the i18nService.
	 * @param i18nService
	 */
	public void setI18nService(final I18nService i18nService) {
		this.i18nService = i18nService;
	}

	public I18nService getI18nService() {
		return i18nService;
	}

}
