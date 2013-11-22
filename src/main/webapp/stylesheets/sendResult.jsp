<%@include file="_include.jsp"%>

<e:page stringsVar="msgs"
	locale="#{sessionController.locale}"
	authorized="#{sendSMSController.pageAuthorized}" footer="">

	<%@include file="_navigation.jsp"%>
	<f:verbatim><br><br><br></f:verbatim>
	<e:paragraph style="color:green;" value="#{msgs['MSG.SENDING.FOR.BACKOFFICE']}" rendered="#{sendSMSController.isShowMsgSending}"/>
	<f:verbatim><br></f:verbatim>
	<e:paragraph style="color:green;" value="#{msgs['MSG.WAITTING.FOR.APPROVAL']}" rendered="#{sendSMSController.isShowMsgWainting}"/>
	<f:verbatim><br></f:verbatim>
	<e:paragraph style="color:green;" value="#{msgs['MSG.WAITTING.FOR.NORECIPIENTFOUND']}" rendered="#{sendSMSController.isShowMsgNoRecipientFound}"/>
	<f:verbatim><br><br><br></f:verbatim>
</e:page>
	