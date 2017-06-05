package com.knowarth.user.portlet;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;

import com.knowarth.user.model.UserMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class UserHelper {
	
	private static Log _log = LogFactoryUtil.getLog(UserHelper.class);

	private CSVReader userReader = CSVReader.getInstance();

	private List<UserMapper> users = new ArrayList<UserMapper>();

	private static UserHelper INSTANCE = new UserHelper();

	private UserHelper() {
		init();
	}

	private void init() {
	}

	public static UserHelper getInstance() {
		return INSTANCE;
	}

	public List<UserMapper> getUsers(ActionRequest actionRequest, String TheFile) {
		if (_log.isDebugEnabled()){
			_log.debug("Initialising users.");
		}
		users = userReader.readUsers(actionRequest, TheFile);
		return users;
	}

	public void setUsers(List<UserMapper> users) {
		this.users = users;
	}
	
}