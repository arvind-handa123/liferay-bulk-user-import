package com.knowarth.user.portlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import com.knowarth.user.config.UserImportConfiguration;
import com.knowarth.user.model.UserMapper;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author nikunj.malaviya
 */
@Component(configurationPid = "com.knowarth.user.configuration.UserImportConfiguration", immediate = true, property = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=user-import Portlet", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp", "javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class LRUserImportPortlet extends MVCPortlet {

	private static Log _log = LogFactoryUtil.getLog(LRUserImportPortlet.class);

	private volatile UserImportConfiguration _userImportConfiguration;

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_userImportConfiguration = ConfigurableUtil.createConfigurable(UserImportConfiguration.class, properties);
	}

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		renderRequest.setAttribute(UserImportConfiguration.class.getName(), _userImportConfiguration);
		super.render(renderRequest, renderResponse);
	}

	public void uploadCsv(javax.portlet.ActionRequest actionRequest, javax.portlet.ActionResponse actionResponse)
			throws PortletException, IOException {

		try {
			if (_log.isDebugEnabled()) {
				_log.debug("We are in try");
			}

			UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(actionRequest);
			if (uploadRequest.getSize("fileName") == 0) {
				throw new IOException("UploadCsv-File size is 0");
			}

			String sourceFileName = uploadRequest.getFileName("fileName");
			File file = uploadRequest.getFile("fileName");

			if (_log.isDebugEnabled()) {
				_log.debug("File name:" + sourceFileName);
			}

			String action = ParamUtil.getString(uploadRequest, "import_type");
			_log.info("action " + action);

			Long roleId = ParamUtil.getLong(uploadRequest, "roleId");
			Long organizationId = ParamUtil.getLong(uploadRequest, "organizationId");
			Long groupId = ParamUtil.getLong(uploadRequest, "groupId");

			UserServiceImpl usi = UserServiceImpl.getInstance();
			int count = 1;
			int count_good = 0;

			if (_log.isDebugEnabled()) {
				_log.debug("roleId " + roleId);
				_log.debug("organizationId " + organizationId);
				_log.debug("##### Started importing #####");
			}

			UserHelper userCacheEngine = UserHelper.getInstance();

			if (_log.isDebugEnabled()) {
				_log.debug("Now we're going to add users to portal");
			}
			List<UserMapper> users = userCacheEngine.getUsers(actionRequest, file.getPath());
			if (users != null) {
				List<UserMapper> usersBad = new ArrayList<UserMapper>();
				for (UserMapper user : users) {
					if (_log.isDebugEnabled()) {
						_log.debug("Processing " + count + " user. " + user.getFirstName() + " " + user.getLastName());
					}
					count = count + 1;
					usi.addUser(actionRequest, user, roleId, organizationId, groupId);
					if (!user.getImpStatus().equals("User imported.")) {
						if (_log.isDebugEnabled()) {
							_log.debug(" User not added to portal");
						}
						usersBad.add(user);
					} else {
						if (_log.isDebugEnabled()) {
							_log.debug(" User added to portal");
						}
						count_good = count_good + 1;
					}
				}
				if (_log.isDebugEnabled()) {
					_log.debug(users.size() + " Users were read from the CSV file");
					_log.debug(count_good + " Users were added to portal.");
					_log.debug("##### Finished importing. #####");
				}

				SessionMessages.add(actionRequest, "success");
				/*
				 * we're using a session variable to hold all the beans of the
				 * CSV users for very large import this is not very good TODO:
				 * split the file or don't use memory but a file to write the
				 * status of the imported user
				 */
				actionRequest.setAttribute("userList", usersBad);
				actionRequest.setAttribute("count_good", count_good);

			} else {
				SessionMessages.add(actionRequest, "error");
			}
		} catch (NullPointerException e) {
			if (_log.isErrorEnabled()) {
				_log.error("Error: " + e);
			}
			SessionMessages.add(actionRequest, "error");
		}

		catch (IOException e1) {
			if (_log.isErrorEnabled()) {
				_log.error("Error Reading The File. Error: " + e1);
			}
			SessionMessages.add(actionRequest, "error");
		}

	}

}