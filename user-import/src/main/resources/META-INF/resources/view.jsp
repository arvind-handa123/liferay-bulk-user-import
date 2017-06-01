<%@page import="com.knowarth.user.model.UserMapper"%>
<%@page import="com.liferay.portal.kernel.dao.orm.QueryUtil"%>
<%@page import="com.liferay.portal.kernel.service.GroupLocalServiceUtil"%>
<%@ include file="init.jsp" %>

<jsp:useBean id="userList" class="java.util.ArrayList"
	type="java.util.List" scope="request" />
	
	
<%
String uploadProgressId = PwdGenerator.getPassword(PwdGenerator.KEY3, 4);

String csvSep = ",";

List<Role> roles = RoleLocalServiceUtil.getRoles(company.getCompanyId());
long parentOrganizationId = OrganizationConstants.ANY_PARENT_ORGANIZATION_ID;
long parentGroupId = GroupConstants.DEFAULT_PARENT_GROUP_ID;
List<Organization> organizations = OrganizationLocalServiceUtil.getOrganizations(company.getCompanyId(),parentOrganizationId);
List<Group> groups = GroupLocalServiceUtil.getCompanyGroups(themeDisplay.getCompanyId(), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
String count_good = "";
Integer total_users = 0;
if (Validator.isNotNull(renderRequest.getAttribute("count_good"))) {
	count_good = renderRequest.getAttribute("count_good").toString();
	total_users = userList.size() + Integer.parseInt(renderRequest.getAttribute("count_good").toString());
}
String error_row = "";
if (Validator.isNotNull(renderRequest.getAttribute("error_row"))) {
	error_row = renderRequest.getAttribute("error_row").toString();
}
%>

<portlet:actionURL var="uploadCsvURL" name="uploadCsv">
	<portlet:param name="jspPage" value="/view.jsp" />
</portlet:actionURL>

<liferay-ui:header title="javax.portlet.short-title">
	</liferay-ui:header>
	
<liferay-ui:upload-progress id="<%= uploadProgressId %>"
	message="uploading" redirect="<%= uploadCsvURL %>" />
<liferay-ui:success key="success"
	message='<%= LanguageUtil.format(request, "success-read-and-added", new Object [] {count_good, total_users}) %>' />
<liferay-ui:error key="expected-header-not-found-in-the-csv-file"
	message="Expected header not found in the CSV file." />
<liferay-ui:error key="error"
	message="Sorry, an error prevented the upload. Please try again." />
<liferay-ui:error key="error"
	message="Sorry, an error prevented the upload. Please try again." />
<liferay-ui:error key="non_right_value_ecountered_on_row"
	message='<%= LanguageUtil.format(request, "non_right_value_ecountered_on_row", error_row) %>' />
<liferay-ui:error key="parser_exception_on_row"
	message='<%= LanguageUtil.format(request, "parser_exception_on_row", error_row) %>' />
<liferay-ui:error key="error_on_row"
	message='<%= LanguageUtil.format(request, "error_on_row", error_row) %>' />
	

<aui:form action="<%= uploadCsvURL %>" enctype="multipart/form-data"
	method="post">
	<div class="alert alert-info">
		<liferay-ui:message key="file-must-be-csv" />
		<liferay-ui:message
			key='<%= LanguageUtil.format(request, "first-row-format", csvSep) %>' />
	</div>
	<aui:fieldset cssClass='fieldset'>
		<aui:input type="file" name="fileName" size="75"
			helpMessage="load-csv-file">
			<aui:validator name="acceptFiles">'csv,txt'</aui:validator>
		</aui:input>
		
		<aui:select label="reg-role" name="roleId" helpMessage="select-role"
			showEmptyOption="true">
			<%
	                    for (int i = 0; i < roles.size(); i++) {
	                    	
	                    	Role role=(Role)roles.get(i);
	                    	String name = role.getName();
	                    	boolean unassignableRole = false;
	                    	if (name.equals(RoleConstants.GUEST) || name.equals(RoleConstants.OWNER) || name.equals(RoleConstants.USER)) {
	                    		unassignableRole = true;
	                    	}
	                    	if((role.getType()==RoleConstants.TYPE_REGULAR) && (!unassignableRole)) {
	             %>
			<aui:option
				label='<%= role.getTitle(themeDisplay.getLanguageId()) + " - " + role.getTypeLabel()%>'
				value="<%= role.getRoleId() %>" />
			<%
	                    	}
	                    }
	             %>
		</aui:select>
		
		<%
	     if (groups.size() > 0) {
	     %>
		<aui:select label="group" name="groupId"
			helpMessage="select-group" showEmptyOption="true">
			<%
	                    for (int i = 0; i < groups.size(); i++) {
	                    	
	                    	Group group=(Group)groups.get(i);
	                    	if(group.getType() == 1){
	                    	String name = group.getDescriptiveName();
	             %>
			<aui:option label='<%= name %>'
				value="<%= group.getGroupId() %>" />
			<%
	                    	}
	                    }
	             %>
		</aui:select>
		<%
	     }
		%>
		
		
		<%
	     if (organizations.size() > 0) {
	     %>
		<aui:select label="organization" name="organizationId"
			helpMessage="select-organization" showEmptyOption="true">
			<%
	                    for (int i = 0; i < organizations.size(); i++) {
	                    	
	                    	Organization organization=(Organization)organizations.get(i);
	                    	String name = organization.getName();
	             %>
			<aui:option label='<%= organization.getName()%>'
				value="<%= organization.getOrganizationId() %>" />
			<%
	                    }
	             %>
		</aui:select>
		<%
	     }
		%>
	</aui:fieldset>
	<aui:button-row>
		<%
	 String taglibOnClick = uploadProgressId + ".startProgress(); return true;";
	 %>
		<aui:button type="submit" value="upload"
			onClick="<%= taglibOnClick %>" />
	</aui:button-row>
</aui:form>
<br />
<%
	if (userList.size()>0){ 
	%>
<liferay-ui:toggle id="table-toggle_id"
	hideMessage='<%= "&laquo; " + LanguageUtil.get(request, "hide-table") %>'
	showMessage='<%= LanguageUtil.get(request, "show-table") + "&raquo; " %>'
	showImage='<%= themeDisplay.getPathThemeImages() + "/arrows/01_down.png"%>'
	hideImage='<%= themeDisplay.getPathThemeImages() + "/arrows/01_right.png"%>'
	defaultShowContent="<%=true %>" />
<div id="table-toggle_id"
	style="display:<liferay-ui:toggle-value id="table-toggle_id"/>; padding-top:10px;">

	<table class="table table-bordered table-hover table-striped">
		<thead class="table-columns">
			<tr>
				<th class="table-first-header">
					<liferay-ui:message
						key='firstname-lastname' />
				</th>
				<th><liferay-ui:message key='email' /></th>
				<th class="table-last-header"><liferay-ui:message
						key="csv-imp-status" /></th>
			</tr>
		</thead>
		<tbody class="table-data">
			<%
	for (int i = 0; i < userList.size(); i++) {
			UserMapper userMapper=(UserMapper)userList.get(i);
    %>
			<tr>
				<td class="table-cell first"><%= userMapper.getFirstName() + " " + userMapper.getLastName()%></td>
				<td class="table-cell"><%= userMapper.getEmail()%></td>
				<td class="table-cell last"><%= userMapper.getImpStatus()%></td>
			</tr>
			<%
	}
	%>
		</tbody>
	</table>
</div>
<%
	}
	%>