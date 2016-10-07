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
<div class="wit container myed avoid-hori-scroll-bar">
<c:choose>
    <c:when test="${fn:length(recordings) gt 0}">
        <portlet:actionURL portletMode="EDIT" var="deleteRecordingsUrl">
            <portlet:param name="action" value="deleteRecordings"/>
        </portlet:actionURL>
        <form name="deleteRecordings" action="${deleteRecordingsUrl}" method="POST">
	    	<div class="clearfix">
	        	<button id="dialog-confirm" value="Delete Recording(s)" name="Delete" style="text-transform: none;" class="btn btn-primary pull-right"
	        		onclick="javascript:return confirm('Are you sure you wish to delete the recording(s)?');" type="submit" >Delete Recording(s)</button>
	    	</div>
        
	        <table class="table">
	            <thead>
	            	<tr class="uportal-channel-table-header">
	                    <th width="15">
	                    	<input id="${namespace}selectAllRecordings" value="selectAllRecordings" name="Recordings" type="checkbox"/>
                    	</th>
	                    <th class="text-center">Previously recorded</th>
	                    <th class="text-center">Start Time</th>
	                    <th></th>
	                </tr>
	            </thead>
	            <tbody>
	                <c:forEach var="recording" items="${recordings}" varStatus="loopStatus">
	                    <tr class="${loopStatus.index % 2 == 0 ? 'uportal-channel-table-row-odd' : 'uportal-channel-table-row-even'}">
	                        <td>
	                            <c:if test="${recording.currentUserCanDelete}">
	                            	<input value="${recording.id}" class="${namespace}deleteRecording" name="deleteRecording" type="checkbox"/>
	                            </c:if>
	                        </td>
							<td class="text-center">
								<c:choose>
									<c:when test="${not empty recording.playUrl}">
										<a target="_blank" href="${recording.playUrl}">${recording.name}</a>
									</c:when>
									<c:otherwise>
										${recording.name}
									</c:otherwise>
								</c:choose>
	                      	</td>
							<td class="text-center"><fmt:formatDate value="${recording.created}" pattern="dd-MM-yyyy HH:mm" /></td>
							<td>
								<c:choose>
									<c:when test="${not empty recording.downloadUrl}">
										<a target="_blank" href="${recording.downloadUrl}" class="btn btn-primary">Download</a>
									</c:when>
									<c:otherwise>
										Not Available
									</c:otherwise>
								</c:choose>
                  			</td>
	                    </tr>
	                </c:forEach>
	            </tbody>
            </table>
	    </form>
    </c:when>
    <c:otherwise>
        <p><b>No recordings available</b></p>
        <hr/>
    </c:otherwise>
</c:choose>
<portlet:renderURL var="backUrl" portletMode="VIEW" />
<button name="Back" value="Back" type="button" class="btn btn-primary" onclick="window.location='${backUrl}'">Back</button>
</div>
<script type="text/javascript">
	up.jQuery(function() {
		var $ = up.jQuery;
		$(document).ready(function() {
			$('#${namespace}selectAllRecordings').click(function() {
				$('.${namespace}deleteRecording').attr('checked', this.checked);
			});
		});
	});
</script>