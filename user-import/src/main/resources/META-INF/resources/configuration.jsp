<%@ include file="init.jsp"%>
<portlet:defineObjects />

<%

PortletPreferences preferences = renderRequest.getPreferences();
 
String portletResource = ParamUtil.getString(request, "portletResource");
 
if (Validator.isNotNull(portletResource)) {
    preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String[] currentCustomFields = preferences.getValue("customFields","").split(",");
String passwordType = preferences.getValue("passwordType","auto");
Enumeration<String> attributeNames = user.getExpandoBridge().getAttributeNames();			

%>

<liferay-portlet:actionURL var="configurationURL"
	portletConfiguration="true" />
<div class="container-fluid">
	<div class="row">
		<div class="col-md-12">
			<div class="alert alert-info">
				<liferay-ui:message key="custom-field-help" />
			</div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<aui:form method="post" action="<%=configurationURL.toString()%>"
				name="fm"
				onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveCsvImport();" %>'>
				<aui:input name="<%=Constants.CMD%>" type="hidden"
					value="<%=Constants.UPDATE%>" />
				<liferay-ui:success key="success"
				message="csv-config-saved"/>
					
				
						<%
						List<String> listCf = Collections.list(attributeNames);
						%>
						<aui:fieldset
							cssClass='<%= renderResponse.getNamespace() + "prefList" %>'>
							<aui:input name='customFields' type="hidden"
								value="<%= StringUtil.merge(currentCustomFields) %>" />
							<%
			
							// Left list
							List leftList = new ArrayList();
			
							for (String currentCf : currentCustomFields) {
								if (!currentCf.equals(""))
									leftList.add(new KeyValuePair(currentCf, currentCf));
							}
						
							// Right list
						
							List rightList = new ArrayList();
						
							for (String availableCf : listCf) {
								if (!ArrayUtil.contains(currentCustomFields, availableCf)) {
									
									rightList.add(new KeyValuePair(availableCf, availableCf));
								}
							}
						
							rightList = ListUtil.sort(rightList, new KeyValuePairComparator(false, true));
							%>
			
							<liferay-ui:input-move-boxes leftBoxName="currentCustomFields"
								leftList="<%= leftList %>" leftReorder="true" leftTitle="current"
								rightBoxName="availableCustomFields" rightList="<%= rightList %>"
								rightTitle="available" />
								<aui:select name="passwordType" label="password-type" helpMessage="password-type-help">
								<aui:option label="Auto" value="auto" selected='<%= passwordType.equals("auto") %>'></aui:option>
								<aui:option label="Regex" value="regex" selected='<%= passwordType.equals("regex") %>'></aui:option>
							</aui:select>
						</aui:fieldset>
						
				<aui:script use="liferay-util-list-fields">
			Liferay.provide(
				window,
				'<portlet:namespace />saveCfs',
				function() {
					document.<portlet:namespace />fm.<portlet:namespace />customFields.value = Liferay.Util.listSelect(document.<portlet:namespace />fm.<portlet:namespace />currentCustomFields);
				},
				['liferay-util-list-fields']
			);
			</aui:script>
				<aui:script>
			function <portlet:namespace />saveCsvImport() {
				if  (document.<portlet:namespace />fm.<portlet:namespace />currentCustomFields.length <= 10) {
					<portlet:namespace />saveCfs();
					submitForm(document.<portlet:namespace />fm);
				} else {
					alert("Maximum number of imported custom fields is 10.");
				}
			}
			</aui:script>
			
				<aui:fieldset>
					<aui:button type="submit" />
				</aui:fieldset>
			
			</aui:form>
		</div>
	</div>
</div>