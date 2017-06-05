package com.knowarth.user.portlet;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;

import org.apache.commons.beanutils.PropertyUtils;

import com.knowarth.user.model.UserMapper;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

public class UserServiceImpl {
	private static Log _log = LogFactoryUtil.getLog(UserServiceImpl.class);

	private static UserServiceImpl INSTANCE = new UserServiceImpl();

	private UserServiceImpl() {
	}

	public static UserServiceImpl getInstance() {
		return INSTANCE;
	}

	public User addUser(ActionRequest request,
			UserMapper userBean, Long roleId, Long organizationId, Long groupId) {
		User user = addLiferayUser(request, userBean, roleId, organizationId, groupId);
		if (user != null) {
			userBean.setLiferayUserId(user.getUserId());
			if (_log.isDebugEnabled()){
				_log.debug("User: " + userBean.getFirstName() + " " + userBean.getLastName() + " was added to liferay.");
			}
		} else {
			if (_log.isDebugEnabled()){
				_log.debug("User: " + userBean.getFirstName() + " " + userBean.getLastName() + " not added to liferay: " + userBean.getImpStatus());
			}
		}
		return user;
	}

	private User addLiferayUser(ActionRequest request,
			UserMapper userBean, Long roleId, Long organizationId, Long groupId) {
		User user = null;
		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
			String portletInstanceId = (String) request.getAttribute(WebKeys.PORTLET_ID);
	    	PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletInstanceId);
	    	String passwordType = preferences.getValue("passwordType","auto");
			long creatorUserId = themeDisplay.getUserId(); 
			long companyId = themeDisplay.getCompanyId(); 
			boolean autoPassword = passwordType.equalsIgnoreCase("auto") ? Boolean.TRUE : Boolean.FALSE;
			String password1 = passwordType.equalsIgnoreCase("auto") ? StringPool.BLANK : userBean.getUsername()+123;
			String password2 = passwordType.equalsIgnoreCase("auto") ? StringPool.BLANK : userBean.getUsername()+123;
			boolean autoScreenName = false;
			String screenName = userBean.getUsername();
			String emailAddress = userBean.getEmail();
			long facebookId = 0;
			String openId = "";
			Locale locale = themeDisplay.getLocale();
			String firstName = userBean.getFirstName();
			String middleName = "";
			String lastName = userBean.getLastName();

			int prefixId = 0;

			int suffixId = 0;
			boolean male = userBean.isMale();

			int birthdayMonth = 1;
			int birthdayDay = 1;
			int birthdayYear = 1970;

			if (userBean.getBirthday() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(userBean.getBirthday());
				birthdayMonth = cal.get(Calendar.MONTH);
				birthdayDay = cal.get(Calendar.DAY_OF_MONTH);
				birthdayYear = cal.get(Calendar.YEAR);
			}

			String jobTitle = "";
			if (userBean.getJobTitle() != null) {
				jobTitle = userBean.getJobTitle();
			}

			long[] groupIds = null;
			if(groupId != 0){
				groupIds = new long[1];
				groupIds[0] = groupId;
			}

			long[] organizationIds = null;
			if (organizationId != 0 ) {
				organizationIds = new long[1];
				organizationIds[0] = organizationId;
			}
			
			long[] roleIds = null;
			if (roleId != 0 ) {
				roleIds = new long[1];
				roleIds[0] = roleId;
			}

			long[] userGroupIds = null;

			boolean sendEmail = false;

			ServiceContext serviceContext = ServiceContextFactory.getInstance(request);
			user = null;
			boolean userbyscreeenname_exists = true;
			boolean userbyemail_exists = true;

			try {
				user = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
			} catch (NoSuchUserException nsue) {
				userbyscreeenname_exists = false;
			}
			try {
				user = UserLocalServiceUtil.getUserByEmailAddress(companyId, emailAddress);
			} catch (NoSuchUserException nsue) {
				userbyemail_exists = false;
			}

			if((!userbyscreeenname_exists) & (!userbyemail_exists))
			{
				user = UserLocalServiceUtil.addUser(creatorUserId,
						companyId,
						autoPassword,
						password1,
						password2,
						autoScreenName,
						screenName,
						emailAddress,
						facebookId,
						openId,
						locale,
						firstName,
						middleName,
						lastName,
						prefixId,
						suffixId,
						male,
						birthdayMonth,
						birthdayDay,
						birthdayYear,
						jobTitle,
						groupIds,
						organizationIds,
						roleIds,
						userGroupIds,
						sendEmail,
						serviceContext);

				user.setPasswordReset(false);
				user = UserLocalServiceUtil.updateUser(user);

				UserLocalServiceUtil.updateStatus(user.getUserId(), WorkflowConstants.STATUS_APPROVED);
				Indexer indexer = IndexerRegistryUtil.getIndexer(User.class);

				indexer.reindex(user);
				userBean.setImpStatus("User imported.");
				// the user is created: here we save the custom fields
				saveCustomFields(request, user, userBean);
			} else {
				String msg_exists = "";
				if (userbyscreeenname_exists) {
					msg_exists = msg_exists + "Screen Name is not unique.";
				}
				if (userbyemail_exists) {
					msg_exists = msg_exists + " Email Address is not unique.";
				}

				userBean.setImpStatus(msg_exists);
				user = null;
			}
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}
		return user;
	}

	private boolean saveCustomFields(ActionRequest request, User user, UserMapper userBean){ 
		boolean retVal = false;
		PortletPreferences preferences;
		try {
			String portletInstanceId = (String) request.getAttribute(WebKeys.PORTLET_ID);

			preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletInstanceId);
			String customFields = preferences.getValue("customFields", "");
			if(customFields != null && !customFields.isEmpty()) {
				String custFields[] = customFields.split(",");
				String beanFieldValue = "";
				String cfieldName = "";
				int k = 0;
				for (int j = 0; j < custFields.length; j++){
					k = j + 1;
					cfieldName = "customField" + k;
					try {
						beanFieldValue = (String) PropertyUtils.getProperty(userBean, cfieldName);
						user.getExpandoBridge().setAttribute(custFields[j], beanFieldValue);
						if (_log.isDebugEnabled()){
							_log.debug("User custom field: " + custFields[j] + " " + beanFieldValue);
						}
					} catch (IllegalAccessException e) {
						_log.error(e);
					} catch (InvocationTargetException e) {
						_log.error(e);
					} catch (NoSuchMethodException e) {
						_log.error(e);
					}
					retVal = true;
				}
			} else {
				retVal = false;
			}
		} catch (PortalException e) {
			_log.error(e);
		} catch (SystemException e) {
			_log.error(e);
		}

		return retVal;
	}

}