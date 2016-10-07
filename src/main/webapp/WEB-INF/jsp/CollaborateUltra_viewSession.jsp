<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<link rel="stylesheet" href="/BlackboardVCPortlet/css/portlet.css" type="text/css"/>
<link href="/web_files/bootstrap/css/style.css" rel="stylesheet">

<portlet:defineObjects />
<%PortletPreferences prefs = renderRequest.getPreferences();%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<c:choose>
	<c:when test="${!empty errorMessage}">
		<div class="uportal-channel-error">${errorMessage}</div>
	</c:when>
	<c:otherwise>
        <div class="wit container myed margin-bottom-1 avoid-hori-scroll-bar">
            <fieldset class="row">
                <div class="col-md-2">Session name: </div>
                <div class="col-md-4">${session.name}</div>
            </fieldset>
            <fieldset class="row">
                <div class="col-md-2">Start time: </div>
                <div class="col-md-4">
                    <fmt:formatDate value="${session.startTime}" pattern="dd-MM-yyyy HH:mm" />
                </div>
            </fieldset>
            <fieldset class="row">
                <div class="col-md-2">End time: </div>
                <div class="col-md-4">
                    <fmt:formatDate value="${session.endTime}" pattern="dd-MM-yyyy HH:mm" />
                </div>
            </fieldset>
            <br/>

            <c:if test="${not empty guestUrl}">
                <fieldset class="row">
                    <div class="col-md-2">Guest link: </div>
                    <div class="col-md-4">
                        <a id="launch-guest-collaborate-session" href="${guestUrl}" target="_blank" style="display:none">${guestUrl}</a>
                        <script> up.jQuery(document).ready(function(){up.jQuery("#launch-guest-collaborate-session ").show(); }); </script>
                    </div>

                </fieldset>
            </c:if>
        </div>

            <c:if test="${session.currentUserCanEdit}">
              <div class="wit container myed margin-bottom-1 avoid-hori-scroll-bar">
                <fieldset class="row">
                    <portlet:renderURL var="editSessionUrl" portletMode="EDIT" windowState="MAXIMIZED" >
                        <portlet:param name="sessionId" value="${session.id}" />
                        <portlet:param name="action" value="editSession" />
                    </portlet:renderURL>  
                	<div class="col-md-2">
                	    <button value="Edit Session" name="${session.id}" class="btn btn-sm btn-primary" onclick="window.location='${editSessionUrl}'" type="button">Edit Session</button>
                	</div>

                </fieldset>
               </div>
            </c:if>        
            
            <c:if test="${! empty showCSVDownload}">
              <div class="wit container myed margin-bottom-1 avoid-hori-scroll-bar">
    		    <fieldset class="row">
    		        <form target="_blank" action="/BlackboardVCPortlet/csvDownload" method="POST">
    		            <input type="hidden" name="sessionId" value="${session.id}"/>
    		            <input type="hidden" name="uid" value="${uid}"/>
    		          <div class="col-md-2">  <button type="submit" class="btn btn-sm btn-primary" name="downloadSubmit" value="Download participant list (CSV file)">Download participant list (CSV file)</button></div>
    		        </form>

    		    </fieldset>
    		   </div>
            </c:if> 

            <c:choose>
                <c:when test="${empty launchSessionUrl}">
                  <div class="wit container myed margin-bottom-1 avoid-hori-scroll-bar">
                    <fieldset class="row"><b>Session is now closed</b></div>
                    <br/>
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="wit container myed margin-bottom-1 avoid-hori-scroll-bar">
                    <fieldset class="row">
                    	<div class="col-md-2"><a class="btn btn-primary btn-block-sm" id="launch-collaborate-session" href="${launchSessionUrl}" target="_blank" ><i class="glyphicon glyphicon-new-window"></i>  Launch session (Will open a new window)</a></div>
    					<script> up.jQuery(document).ready(function(){up.jQuery("#launch-collaborate-session ").show(); }); </script>
                    </fieldset>
                  </div>
                </c:otherwise>
            </c:choose>
        </div>
	</c:otherwise>
</c:choose>

<portlet:renderURL var="backUrl" portletMode="VIEW" />
<div class="wit container myed avoid-hori-scroll-bar">
<button name="Back" value="Back" type="button" class="btn btn-sm btn-primary" onclick="window.location='${backUrl}'">Back</button>
</div>