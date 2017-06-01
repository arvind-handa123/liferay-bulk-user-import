package com.knowarth.user.config;


import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

@Component(configurationPid = "com.knowarth.user.configuration.UserImportConfiguration", configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true, property = {
		"javax.portlet.name=com_knowarth_user_portlet_LRUserImportPortlet" }, service = ConfigurationAction.class)
public class UserImportConfigurationAction extends DefaultConfigurationAction {
	
	private static Log _log = LogFactoryUtil.getLog(UserImportConfigurationAction.class);

	@Override
	public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
			throws Exception {
		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);
		if (!cmd.equals(Constants.UPDATE)) {
			return;
		}

		String tabs2 = ParamUtil.getString(actionRequest, "tabs2", "basic-csv");
		String customFields = "";


		customFields = ParamUtil.getString(actionRequest, "customFields", "");
		if (_log.isDebugEnabled()) {
			_log.debug("customFields " + customFields);
		}
		try {
			String portletResource = ParamUtil.getString(actionRequest, "portletResource");
			PortletPreferences preferences = PortletPreferencesFactoryUtil.getPortletSetup(actionRequest,
					portletResource);
			preferences.setValue("customFields", customFields);
			preferences.store();

			SessionMessages.add(actionRequest, "success");
			SessionMessages.add(actionRequest,
					portletConfig.getPortletName() + SessionMessages.KEY_SUFFIX_REFRESH_PORTLET, portletResource);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
		_userImportConfiguration = ConfigurableUtil.createConfigurable(
				UserImportConfiguration.class, properties);
    }
	private volatile UserImportConfiguration _userImportConfiguration;
	

}
