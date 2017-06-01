<%@ include file="init.jsp"%>
<portlet:defineObjects />

<%

PortletPreferences preferences = renderRequest.getPreferences();
 
String portletResource = ParamUtil.getString(request, "portletResource");
 
if (Validator.isNotNull(portletResource)) {
    preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String csvSeparator = preferences.getValue("csvSeparator","EXCEL_NORTH_EUROPE_PREFERENCE");
String[] currentCustomFields = preferences.getValue("customFields","").split(",");
Enumeration<String> attributeNames = user.getExpandoBridge().getAttributeNames();			

%>

<liferay-portlet:actionURL var="configurationURL"
	portletConfiguration="true" />
<aui:form method="post" action="<%=configurationURL.toString()%>"
	name="fm"
	onSubmit='<%= "event.preventDefault(); " + renderResponse.getNamespace() + "saveCsvImport();" %>'>
	<aui:input name="<%=Constants.CMD%>" type="hidden"
		value="<%=Constants.UPDATE%>" />
	<liferay-ui:success key="success"
	message="csv-config-saved"/>
		
	<div class="alert alert-info">
				<liferay-ui:message key="custom-field-help" />
			</div>
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

			</aui:fieldset>
			
			<aui:select name="passwordType" label="password-type">
				<aui:option label="Auto" value="auto"></aui:option>
				<aui:option label="Regex" value="regex"></aui:option>
			</aui:select>
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

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>

</aui:form>