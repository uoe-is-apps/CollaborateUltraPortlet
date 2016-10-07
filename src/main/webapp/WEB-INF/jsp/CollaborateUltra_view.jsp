<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" isELIgnored="false"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "spring" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>

<link rel="stylesheet" href="/BlackboardVCPortlet/css/portlet.css" type="text/css"/>
<link href="/web_files/bootstrap/css/style.css" rel="stylesheet">

<portlet:defineObjects />
<c:set var="namespace"><portlet:namespace /></c:set>

<c:if test="${!empty feedbackMessage}">
    <div class="uportal-channel-success margin-bottom-1"><spring:message code="${feedbackMessage}"/></div>
</c:if>
<c:if test="${!empty warningMessage}">
    <div class="uportal-channel-warning margin-bottom-1"><spring:message code="${warningMessage}"/></div>
</c:if>


<portlet:actionURL portletMode="EDIT" var="deleteSessionsUrl">
	<portlet:param name="action" value="deleteSessions"/>
</portlet:actionURL>
<div class="wit container myed avoid-hori-scroll-bar">
<table width="100%">
<tbody><tr>
<form name="deleteSessions" action="${deleteSessionsUrl}" method="POST">
	<div class="clearfix">
		<portlet:renderURL var="createSessionUrl"  portletMode="EDIT" windowState="MAXIMIZED" />
		<td><button value="Schedule Session" name="Schedule Session" class="btn btn-primary pull-left"
				type="button" onclick="window.location='${createSessionUrl}'">Schedule Session</button></td>
		<td><button id="dialog-confirm" type="submit" value="Delete Session(s)" name="Delete"
				style="text-transform: none;" class="btn btn-primary pull-right"
				onclick="javascript:return confirm('Are you sure you wish to delete the session(s)?');">Delete Session(s)</button></td>
	</div>
</tr></tbody>
</table>
	<c:choose>
		<c:when test="${fn:length(sessions) gt 0}">
			<table class="table" width="100%">
				<thead>
					<tr class="uportal-channel-table-header">
						<th width="15">
							<input id="${namespace}selectAllSessions" value="selectAllSessions" name="selectAllSessions" type="checkbox"/>
						</th>
		                <th class="text-center">Session Name</th>
		                <th class="text-center">Start Date and Time</th>
		                <th class="text-center">End Date and Time</th>
		                <th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="session" items="${sessions}" varStatus="loopStatus">
						<portlet:renderURL var="viewSessionUrl">
						    <portlet:param name="sessionId" value="${session.id}" />
							<portlet:param name="action" value="viewSession" />
						</portlet:renderURL>
						<portlet:renderURL var="editSessionUrl" portletMode="EDIT" windowState="MAXIMIZED" >
						    <portlet:param name="sessionId" value="${session.id}" />
						    <portlet:param name="action" value="editSession" />
						</portlet:renderURL>
						<portlet:renderURL var="viewRecordingsUrl">
						    <portlet:param name="sessionId" value="${session.id}" />
						    <portlet:param name="action" value="viewSessionRecordings" />
						</portlet:renderURL>
						<tr class="${loopStatus.index % 2 == 0 ? 'uportal-channel-table-row-odd' : 'uportal-channel-table-row-even'}"">
							<td>
								<c:if test="${session.currentUserCanEdit}">
									<input value="${session.id}" class="${namespace}deleteSession" name="deleteSession" type="checkbox" />
								</c:if>
							</td>
							<td class="text-center">
								<a href="${viewSessionUrl}">${session.name}</a>
							</td>
							<td class="text-center">
								<fmt:formatDate value="${session.startTime}" pattern="dd-MM-yyyy HH:mm" />
							</td>
							<td class="text-center">
								<fmt:formatDate value="${session.endTime}" pattern="dd-MM-yyyy HH:mm" />
							</td>
							<td>
								<c:if test="${session.currentUserCanEdit}">
									<button value="Edit" name="${session.id}" onclick="window.location='${editSessionUrl}'" class="btn btn-sm btn-primary" type="button">Edit</button>
								</c:if>
								<button value="Recordings" name="${session.id}" onclick="window.location='${viewRecordingsUrl}'" class="btn btn-sm btn-primary" type="button">Recordings</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
		<c:otherwise>
			<b>No sessions available</b>
			<hr />
		</c:otherwise>
	</c:choose>
</form>
</div>
<script type="text/javascript"><rs:compressJs>
	up.jQuery(function() {
		var $ = up.jQuery;
		$(document).ready(function() {
			$('#${namespace}selectAllSessions').click(function() {
				$('.${namespace}deleteSession').attr('checked', this.checked);
			});
		});
	});
</rs:compressJs></script>