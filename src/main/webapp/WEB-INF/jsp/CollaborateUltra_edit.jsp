<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "spring" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>
<%@ page trimDirectiveWhitespaces="true" %>

<link rel="stylesheet" href="/BlackboardVCPortlet/css/portlet.css" type="text/css"/>
<link href="/web_files/bootstrap/css/style.css" rel="stylesheet">

<portlet:defineObjects />
<c:set var="namespace"><portlet:namespace/></c:set>

<c:if test="${!empty feedbackMessage}">
    <div class="uportal-channel-success margin-bottom-1"><spring:message code="${feedbackMessage}"/></div>
</c:if>
    
<c:if test="${!empty warningMessage}">
    <div class="uportal-channel-warning margin-bottom-1"><spring:message code="${warningMessage}"/></div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div class="uportal-channel-error margin-bottom-1">
    <ul>
    <c:forEach var="error" items="${errorMessage}">
        <li><spring:message code="${error}" text="${error}"/></li>
    </c:forEach>
    </ul>
    </div>
</c:if>
<div class="wit container myed avoid-hori-scroll-bar">
<portlet:actionURL portletMode="EDIT" var="formActionUrl"/>
<form:form commandName="sessionForm" name="createSession" action="${formActionUrl}" method="POST">
	<h3>1. Session information</h3>
	<hr> 
        
        
        
	<div class="form-horizontal">
		<c:if test="${!empty session.id}">
		    <input type="hidden" name="id" value="${session.id}"/>                    
                    <input type="hidden" name="original_startTime" value="<fmt:formatDate value="${session.startTime}" pattern="dd-MM-yyyy HH:mm"/>"/>
                    <input type="hidden" name="original_endTime" value="<fmt:formatDate value="${session.endTime}" pattern="dd-MM-yyyy HH:mm"/>"/>                    
                    <input type="hidden" name="update_mode" value="yes"/>
		</c:if>
		<input type="hidden" name="occurrenceType" value="${session.occurrenceType}"/>
		<input type="hidden" name="creatorId" value="${session.creatorId}"/>

		<fieldset class="form-group">
			<label for="name" class="control-label col-md-2">Session Name: </label>
			<div class="col-md-6">
				<c:choose>
					<c:when test="${not empty session.id}">
						<input type="hidden" name="name" value="${session.name}"/>
						${session.name}
					</c:when>
					<c:otherwise>
						<input type="text" name="name" class="form-control" value="${session.name}" />
					</c:otherwise>
				</c:choose>
			</div>
		</fieldset>
		<fieldset class="form-group">
			<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
				The session name cannot be changed once created.
			</div>
		</fieldset>
		<fieldset class="form-group">
			<label for="startdate" class="control-label col-md-2">Start Date and Time: </label>
			<div class="col-md-2">
				<input name="startdate" id="${namespace}startdatepicker" type="text" class="form-control"
						value="<fmt:formatDate value="${session.startTime}" pattern="dd-MM-yyyy"/>" />
			</div>
			<div class="col-md-2">
				<fmt:formatDate var="startHourValue" value="${session.startTime}" pattern="HH" />
				<select name="startHour">
                    <option value="00" ${startHourValue == "00" ? 'selected' : ''}>00</option>
                    <option value="01" ${startHourValue == "01" ? 'selected' : ''}>01</option>
                    <option value="02" ${startHourValue == "02" ? 'selected' : ''}>02</option>
                    <option value="03" ${startHourValue == "03" ? 'selected' : ''}>03</option>
                    <option value="04" ${startHourValue == "04" ? 'selected' : ''}>04</option>
                    <option value="05" ${startHourValue == "05" ? 'selected' : ''}>05</option>
                    <option value="06" ${startHourValue == "06" ? 'selected' : ''}>06</option>
                    <option value="07" ${startHourValue == "07" ? 'selected' : ''}>07</option>
                    <option value="08" ${startHourValue == "08" ? 'selected' : ''}>08</option>
                    <option value="09" ${startHourValue == "09" ? 'selected' : ''}>09</option>
                    <option value="10" ${startHourValue == "10" ? 'selected' : ''}>10</option>
                    <option value="11" ${startHourValue == "11" ? 'selected' : ''}>11</option>
                    <option value="12" ${startHourValue == "12" ? 'selected' : ''}>12</option>
                    <option value="13" ${startHourValue == "13" ? 'selected' : ''}>13</option>
                    <option value="14" ${startHourValue == "14" ? 'selected' : ''}>14</option>
                    <option value="15" ${startHourValue == "15" ? 'selected' : ''}>15</option>
                    <option value="16" ${startHourValue == "16" ? 'selected' : ''}>16</option>
                    <option value="17" ${startHourValue == "17" ? 'selected' : ''}>17</option>
                    <option value="18" ${startHourValue == "18" ? 'selected' : ''}>18</option>
                    <option value="19" ${startHourValue == "19" ? 'selected' : ''}>19</option>
                    <option value="20" ${startHourValue == "20" ? 'selected' : ''}>20</option>
                    <option value="21" ${startHourValue == "21" ? 'selected' : ''}>21</option>
                    <option value="22" ${startHourValue == "22" ? 'selected' : ''}>22</option>
                    <option value="23" ${startHourValue == "23" ? 'selected' : ''}>23</option>
                </select>:
                <fmt:formatDate var="startMinuteValue" value='${session.startTime}' pattern='mm' />
                <select name="startMinute">
                    <option value="00" ${startMinuteValue =="00" ? 'selected' : ''}>00</option>
                    <option value="15" ${startMinuteValue =="15" ? 'selected' : ''}>15</option>
                    <option value="30" ${startMinuteValue =="30" ? 'selected' : ''}>30</option>
                    <option value="45" ${startMinuteValue =="45" ? 'selected' : ''}>45</option>
                </select> 
			</div>
		</fieldset>
		<fieldset class="form-group">
			<label for="enddate" class="control-label col-md-2">End Date and Time: </label>
			<div class="col-md-2">
				<input name="enddate" id="${namespace}enddatepicker" type="text" class="form-control"
						value="<fmt:formatDate value="${session.endTime}" pattern="dd-MM-yyyy" />"/>
			</div>
			<div class="col-md-2">
				<fmt:formatDate var="endHourValue" value="${session.endTime}" pattern="HH" />
                <select name="endHour">
                    <option value="00" ${endHourValue == "00" ? 'selected' : ''}>00</option>
                    <option value="01" ${endHourValue == "01" ? 'selected' : ''}>01</option>
                    <option value="02" ${endHourValue == "02" ? 'selected' : ''}>02</option>
                    <option value="03" ${endHourValue == "03" ? 'selected' : ''}>03</option>
                    <option value="04" ${endHourValue == "04" ? 'selected' : ''}>04</option>
                    <option value="05" ${endHourValue == "05" ? 'selected' : ''}>05</option>
                    <option value="06" ${endHourValue == "06" ? 'selected' : ''}>06</option>
                    <option value="07" ${endHourValue == "07" ? 'selected' : ''}>07</option>
                    <option value="08" ${endHourValue == "08" ? 'selected' : ''}>08</option>
                    <option value="09" ${endHourValue == "09" ? 'selected' : ''}>09</option>
                    <option value="10" ${endHourValue == "10" ? 'selected' : ''}>10</option>
                    <option value="11" ${endHourValue == "11" ? 'selected' : ''}>11</option>
                    <option value="12" ${endHourValue == "12" ? 'selected' : ''}>12</option>
                    <option value="13" ${endHourValue == "13" ? 'selected' : ''}>13</option>
                    <option value="14" ${endHourValue == "14" ? 'selected' : ''}>14</option>
                    <option value="15" ${endHourValue == "15" ? 'selected' : ''}>15</option>
                    <option value="16" ${endHourValue== "16" ? 'selected' : ''}>16</option>
                    <option value="17" ${endHourValue == "17" ? 'selected' : ''}>17</option>
                    <option value="18" ${endHourValue == "18" ? 'selected' : ''}>18</option>
                    <option value="19" ${endHourValue == "19" ? 'selected' : ''}>19</option>
                    <option value="20" ${endHourValue == "20" ? 'selected' : ''}>20</option>
                    <option value="21" ${endHourValue == "21" ? 'selected' : ''}>21</option>
                    <option value="22" ${endHourValue == "22" ? 'selected' : ''}>22</option>
                    <option value="23" ${endHourValue == "23" ? 'selected' : ''}>23</option>
                </select>:
                <fmt:formatDate var="endMinuteValue" value='${session.endTime}' pattern='mm' />
                <select name="endMinute">
                    <option value="00" ${endMinuteValue =="00" ? 'selected' : ''}>00</option>
                    <option value="15" ${endMinuteValue =="15" ? 'selected' : ''}>15</option>
                    <option value="30" ${endMinuteValue =="30" ? 'selected' : ''}>30</option>
                    <option value="45" ${endMinuteValue =="45" ? 'selected' : ''}>45</option>
                </select>
			</div>
		</fieldset>
		<fieldset class="form-group">
			<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
				Enter dates as in dd-mm-yyyy format. Time must be entered in 15 minute increments. Start time must be in the future.
			</div>
		</fieldset>
		<fieldset class="form-group">
			<label for="boundaryTime" class="control-label col-md-2">Early Session Entry: </label>
			<div class="col-md-6">
				<select name="boundaryTime">
                    <option value="15" ${session.boundaryTime == "15" ? 'selected' : ''}>15 minutes</option>
                    <option value="30" ${session.boundaryTime == "30" ? 'selected' : ''}>30 minutes</option>
                    <option value="45" ${session.boundaryTime == "45" ? 'selected' : ''}>45 minutes</option>
                    <option value="60" ${session.boundaryTime == "60" ? 'selected' : ''}>1 hour</option>
                </select> 
			</div>
		</fieldset>
		<fieldset class="form-group">
			<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
				The period before the start of the session during which users can enter the session.
			</div>
		</fieldset>

		<c:choose>
			<c:when test="${not empty fullAccess}">
			    <input type="hidden" name="noEndDate" value="${session.noEndDate}"/>
				<input type="hidden" name="mustBeSupervised" value="${session.mustBeSupervised}"/>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Moderators may view all private chat messages in the session.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Raise Hand on Entry: </label>
					<div class="col-md-1">
						<input name="raiseHandOnEnter" type="checkbox" class="form-control" ${session.raiseHandOnEnter ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Users automatically raise their hand when they join the session.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Allow In-Session Invitations: </label>
					<div class="col-md-1">
						<input name="allowInSessionInvitees" type="checkbox" class="form-control" ${session.allowInSessionInvitees ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Moderators may send invitations to join the session from within the session.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Allow Guest: </label>
					<div class="col-md-1">
						<input name="allowGuest" type="checkbox" class="form-control" ${session.allowGuest ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Guests can be invited to the session with a fixed guest URL.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						The session does not stop at a fixed time.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Show Profile: </label>
					<div class="col-md-1">
						<input name="showProfile" type="checkbox" class="form-control" ${session.showProfile ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Each user's profile is visible. 
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Participants can use tools: </label>
					<div class="col-md-1">
						<input name="participantCanUseTools" type="checkbox" class="form-control" ${session.participantCanUseTools ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						All participants have full permissions access to session resources such as audio, whiteboard, etc.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Can Share Audio: </label>
					<div class="col-md-1">
						<input name="canShareAudio" type="checkbox" class="form-control" ${session.canShareAudio ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Users can share audio in the session.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Can Share Video: </label>
					<div class="col-md-1">
						<input name="canShareVideo" type="checkbox" class="form-control" ${session.canShareVideo ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Users can share video in the session.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Can Post Message: </label>
					<div class="col-md-1">
						<input name="canPostMessage" type="checkbox" class="form-control" ${session.canPostMessage ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Users can post messages in a shared chat room.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Can annotate whiteboard: </label>
					<div class="col-md-1">
						<input name="canAnnotateWhiteboard" type="checkbox" class="form-control" ${session.canAnnotateWhiteboard ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Users can annotate the session whiteboard.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Open Chair: </label>
					<div class="col-md-1">
						<input name="openChair" type="checkbox" class="form-control" ${session.openChair ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						The session chair is open.
					</div>
				</fieldset>
				<fieldset class="form-group">
					<label class="control-label col-md-2">Can Download Recording: </label>
					<div class="col-md-1">
						<input name="canDownloadRecording" type="checkbox" class="form-control" ${session.canDownloadRecording ? 'checked' : ''}/>
					</div>
				</fieldset>
				<fieldset class="form-group">
					<div class="col-md-6 col-md-offset-2 uportal-channel-table-caption">
						Recordings of the session can be downloaded.
					</div>
				</fieldset>
			</c:when>
			<c:otherwise>
				<input type="hidden" name="mustBeSupervised" value="${session.mustBeSupervised}"/>
                <input type="hidden" name="raiseHandOnEnter" value="${session.raiseHandOnEnter}" />
                <input type="hidden" name="allowInSessionInvitees" value="${session.allowInSessionInvitees}"/>
                <input type="hidden" name="allowGuest" value="${session.allowGuest}"/>
                <input type="hidden" name="noEndDate" value="${session.noEndDate}"/>
                <input type="hidden" name="showProfile" value="${session.showProfile}"/>
                <input type="hidden" name="participantCanUseTools" value="${session.participantCanUseTools}"/>
                <input type="hidden" name="canShareAudio" value="${session.canShareAudio}"/>
                <input type="hidden" name="canShareVideo" value="${session.canShareVideo}"/>
                <input type="hidden" name="canPostMessage" value="${session.canPostMessage}"/>
                <input type="hidden" name="canAnnotateWhiteboard" value="${session.canAnnotateWhiteboard}"/>
                <input type="hidden" name="openChair" value="${session.openChair}"/>
                <input type="hidden" name="canDownloadRecording" value="${session.canDownloadRecording}"/>
			</c:otherwise>
		</c:choose>
	</div>

	<h3>2. Moderators</h3>
    <hr>
    <c:if test="${fn:length(moderators) gt 0}">
    	<table class="table">
			<thead>
				<tr class="uportal-channel-table-header">           
                        <th></th>
	                <th class="text-center">Username</th>
	                <th class="text-center">Name</th>
	                <th class="text-center">Email address</th>	                
	            </tr>
			</thead>
			<tbody>
				<c:forEach var="moderator" items="${moderators}" varStatus="loopStatus">
	                <tr class="${loopStatus.index % 2 == 0 ? 'uportal-channel-table-row-odd' : 'uportal-channel-table-row-even'}">
                                <td class="text-center"><input value="${loopStatus.index}" name="deleteModerator" type="checkbox"/></td>
                  		<td class="text-center">
                  			<input type="hidden" name="moderatorIds" value="${moderator.id}" />
							<input type="hidden" name="moderatorUids" value="${moderator.usernameInternal}"/>
	                  		${moderator.usernameInternal}
                  		</td>
                  		<td class="text-center">
	                  		<input type="hidden" name="moderatorDisplayNames" value="${moderator.displayName}"/>
	                  		${moderator.displayName}
                  		</td>
                  		<td class="text-center">
		                	<input type="hidden" name="moderatorEmails" value="${moderator.email}"/>
		                	${moderator.email}
		                </td>
                  		
              		</tr>
				</c:forEach>
			</tbody>
    	</table>
    </c:if>
	<div class="form-horizontal">
		<c:if test="${fn:length(moderators) gt 0}">
			<fieldset class="form-group">
				<div class="col-md-2">
					<button value="Delete Moderator(s)" name="action" class="btn btn-primary" type="submit"/>Delete Moderator(s)</button>
				</div>
			</fieldset>
			<br/>
		</c:if>
		<fieldset class="form-group">
			<div class="col-md-2">
				<input id="${namespace}moderatiorUidInput" name="moderatorUid" type="text"/>
			</div>
                        <div class="col-md-2"></div>
			<div class="col-md-2">
				<button id="${namespace}addModeratorSubmit" name="action" value="Add Moderator(s)" class="btn btn-primary" type="submit">Add Moderator(s)</button>
			</div>
		</fieldset>
		<fieldset class="row">
			<p class="col-md-12">
				You can search for moderators using uun or display name. To search for multiple moderators, separate each with a comma.
			</p>
		</fieldset>
	</div>

	<h3>3. Participants</h3>
    <hr>

    <h4>Internal participants</h4>
    <c:if test="${fn:length(intParticipants) gt 0}">
		<table class="table">
			<thead>
				<tr class="uportal-channel-table-header">   
                        <th></th>
	                <th class="text-center">Username</th>
	                <th class="text-center">Name</th>
	                <th class="text-center">Email address</th>
	                
	            </tr>
			</thead>
			<tbody>
				<c:forEach var="intParticipant" items="${intParticipants}" varStatus="loopStatus">
	                <tr class="${loopStatus.index % 2 == 0 ? 'uportal-channel-table-row-odd' : 'uportal-channel-table-row-even'}">
                                <td class="text-center"><input value="${loopStatus.index}" name="deleteIntParticipant" type="checkbox"/></td>                            
                  		<td class="text-center">
                  			<input type="hidden" name="intParticipantIds" value="${intParticipant.id}" />
							<input type="hidden" name="intParticipantUids" value="${intParticipant.usernameInternal}"/>
	                  		${intParticipant.usernameInternal}
                  		</td>
                  		<td class="text-center">
		                	<input type="hidden" name="intParticipantDisplayNames" value="${intParticipant.displayName}"/>
	                  		${intParticipant.displayName}
                  		</td>
                  		<td class="text-center">
	                		<input type="hidden" name="intParticipantEmails" value="${intParticipant.email}"/>
                  			${intParticipant.email}
              			</td>                  		
              		</tr>
				</c:forEach>
			</tbody>
    	</table>
    </c:if>
    
    
    <div class="form-horizontal">
		<c:if test="${fn:length(intParticipants) gt 0}">
			<fieldset class="form-group">
				<div class="col-md-2">
					<button value="Delete Internal Participant(s)" name="action" class="btn btn-primary" type="submit">Delete Internal Participant(s)</button>
				</div>
			</fieldset>
			<br/>  
		</c:if>
		<fieldset class="form-group">
			<div class="col-md-2">
				<input id="${namespace}intParticipantInput" name="intParticipantUid" type="text"/>
			</div>
                        <div class="col-md-2"></div>
			<div class="col-md-2">
				<button id="${namespace}addIntParticipantSubmit" name="action" value="Add Participant(s)" class="btn btn-primary" type="submit">Add Participant(s)</button>
			</div>
		</fieldset>
		<fieldset class="row">
			<p class="col-md-12">
				You can search for participants using uun or display name. To search for multiple participants, separate each with a comma.
			</p>
		</fieldset>
	</div>

    <h4>External participants</h4>
    <c:if test="${fn:length(extParticipants) gt 0}">
    	<table class="table">
			<thead>
	            <tr class="uportal-channel-table-header">     
                        <th></th>
	                <th class="text-center">Display Name</th>           
	                <th class="text-center">Email address</th>	                
	            </tr>
	        </thead>
	        <tbody>
	        	<c:forEach var="extParticipant" items="${extParticipants}" varStatus="loopStatus">
					<tr class="${loopStatus.index % 2 == 0 ? 'uportal-channel-table-row-odd' : 'uportal-channel-table-row-even'}">
                                                <td><input value="${loopStatus.index}" name="deleteExtParticipant" type="checkbox"/></td>                                            
						<td class="text-center">
                  			<input type="hidden" name="extParticipantIds" value="${extParticipant.id}" />
							<input type="hidden" name="extParticipantDisplayNames" value="${extParticipant.displayName}"/>
							${extParticipant.displayName}
						</td>
						<td class="text-center">
							<input type="hidden" name="extParticipantEmails" value="${extParticipant.email}"/>
							${extParticipant.email}
						</td>
						
                    </tr>
	        	</c:forEach>
	        </tbody>
    	</table>
    </c:if>
    <div class="form-horizontal">
                <c:if test="${fn:length(extParticipants) gt 0}">
    		<fieldset class="form-group">
    			<div class="col-md-12">
				<button value="Delete External Participant(s)" name="action" class="btn btn-primary" type="submit">Delete External Participant(s)</button>
			</div>
    		</fieldset>
		</c:if>
		<fieldset class="form-group">
                        <div class="col-md-12">
                            <label for="extParticipantDisplayName" class="col-md-6">Display Name: </label>
                            <div class="col-md-6">
                                    <input id="${namespace}extParticipantDisplayNameInput" name="extParticipantDisplayName" type="text" class="form-control"/>
                            </div>
                        </div>
		</fieldset>
		<fieldset class="form-group">
                        <div class="col-md-12">
                            <label for="extParticipantEmail" class="col-md-6">Email: </label>
                            <div class="col-md-6">
                                    <input id="${namespace}extParticipantEmailInput" name="extParticipantEmail" type="text" class="form-control" />
                            </div>
                        </div>
		</fieldset>
		<fieldset class="form-group">
			<div class="col-md-12">
				<button id="${namespace}addExtParticipantSubmit" name="action" value="Add External Participant" class="btn btn-primary" type="submit">Add External Participant</button>
			</div>
		</fieldset>
		<div class="row uportal-channel-table-caption">
			<p class="col-md-12">
				Enter an external participant.
			</p>
		</div>
    </div>

    <div>
		<button class="btn btn-primary" name="action" value="Save Session" type="submit">Save Session</button>
		<portlet:renderURL var="cancelAction" portletMode="VIEW" windowState="NORMAL" />
		<button class="btn btn-primary" name="cancel" value="Cancel" onclick="window.location='${cancelAction}'" type="button">Cancel</button>
	</div>
</form:form>
</div>
<script type="text/javascript"><rs:compressJs>
up.jQuery(function() {
    var $ = up.jQuery;
    $(document).ready(function(){
        $.datepicker.setDefaults($.datepicker.regional['en-GB']);
        $("#${namespace}startdatepicker" ).datepicker({showButtonPanel: true,dateFormat: 'dd-mm-yy'});
        $("#${namespace}enddatepicker" ).datepicker({showButtonPanel: true,dateFormat: 'dd-mm-yy'});
        $('#${namespace}moderatiorUidInput').keypress(function (e) {
        	if (e.which == 13) { $('#${namespace}addModeratorSubmit').focus().click();return false; }
    	});
    	$('#${namespace}intParticipantInput').keypress(function (e) {
    		if (e.which == 13) {$('#${namespace}addIntParticipantSubmit').focus().click();return false;}
    	});
    	$('#${namespace}extParticipantDisplayNameInput').keypress(function (e) {
    		if (e.which == 13) {$('#${namespace}addExtParticipantSubmit').focus().click();return false;}
    	});
        $('#${namespace}extParticipantEmailInput').keypress(function (e) {
        	if (e.which == 13) {$('#${namespace}addExtParticipantSubmit').focus().click();return false;}
        });
    });
});</rs:compressJs>
</script>