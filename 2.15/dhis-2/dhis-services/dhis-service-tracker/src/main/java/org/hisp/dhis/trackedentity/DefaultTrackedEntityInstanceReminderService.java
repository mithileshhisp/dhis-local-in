package org.hisp.dhis.trackedentity;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminder;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminderService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;

/**
 * @author Chau Thu Tran
 * 
 * @version $ DefaultTrackedEntityInstanceReminderService.java Aug 7, 2013
 *          9:54:59 AM $
 */
public class DefaultTrackedEntityInstanceReminderService
    implements TrackedEntityInstanceReminderService
{
    private static final String ATTRIBUTE = "attributeid";

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile( "\\{(" + ATTRIBUTE + ")=(\\w+)\\}" );

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<TrackedEntityInstanceReminder> reminderStore;

    public void setReminderStore( GenericIdentifiableObjectStore<TrackedEntityInstanceReminder> reminderStore )
    {
        this.reminderStore = reminderStore;
    }

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public TrackedEntityInstanceReminder getReminder( int id )
    {
        return reminderStore.get( id );
    }

    @Override
    public TrackedEntityInstanceReminder getReminderByName( String name )
    {
        return reminderStore.getByName( name );
    }

    @Override
    public String getMessageFromTemplate( TrackedEntityInstanceReminder reminder, ProgramInstance programInstance,
        I18nFormat format )
    {
        TrackedEntityInstance entityInstance = programInstance.getEntityInstance();
        String templateMessage = reminder.getTemplateMessage();
        String template = templateMessage;

        String organisationunitName = entityInstance.getOrganisationUnit().getName();
        String programName = programInstance.getProgram().getName();
        String daysSinceEnrollementDate = DateUtils.daysBetween( new Date(), programInstance.getEnrollmentDate() ) + "";
        String daysSinceIncidentDate = DateUtils.daysBetween( new Date(), programInstance.getDateOfIncident() ) + "";
        String incidentDate = format.formatDate( programInstance.getDateOfIncident() );
        String erollmentDate = format.formatDate( programInstance.getEnrollmentDate() );

        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME,
            programName );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME,
            organisationunitName );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_INCIDENT_DATE,
            incidentDate );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_ENROLLMENT_DATE,
            erollmentDate );
        templateMessage = templateMessage.replace(
            TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_ENROLLMENT_DATE, daysSinceEnrollementDate );
        templateMessage = templateMessage.replace(
            TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_INCIDENT_DATE, daysSinceIncidentDate );

        Matcher matcher = ATTRIBUTE_PATTERN.matcher( template );

        while ( matcher.find() )
        {
            String match = matcher.group();
            String value = "";

            if ( matcher.group( 1 ).equals( ATTRIBUTE ) )
            {
                String uid = matcher.group( 2 );
                for ( TrackedEntityAttributeValue attributeValue : programInstance.getEntityInstance()
                    .getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getUid().equals( uid ) )
                    {
                        value = attributeValue.getValue();
                    }
                }

            }

            templateMessage = templateMessage.replace( match, value );
        }

        return templateMessage;
    }

    @Override
    public String getMessageFromTemplate( TrackedEntityInstanceReminder reminder,
        ProgramStageInstance programStageInstance, I18nFormat format )
    {
        TrackedEntityInstance entityInstance = programStageInstance.getProgramInstance().getEntityInstance();
        String templateMessage = reminder.getTemplateMessage();

        String organisationunitName = entityInstance.getOrganisationUnit().getName();
        String programName = programStageInstance.getProgramInstance().getProgram().getName();
        String programStageName = programStageInstance.getProgramStage().getName();
        String daysSinceDueDate = DateUtils.daysBetween( new Date(), programStageInstance.getDueDate() ) + "";
        String dueDate = format.formatDate( programStageInstance.getDueDate() );

        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME,
            programName );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME,
            programStageName );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME,
            organisationunitName );
        templateMessage = templateMessage.replace( TrackedEntityInstanceReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE,
            daysSinceDueDate );

        Matcher matcher = ATTRIBUTE_PATTERN.matcher( templateMessage );

        while ( matcher.find() )
        {
            String match = matcher.group();

            if ( matcher.group( 1 ).equals( ATTRIBUTE ) )
            {
                String uid = matcher.group( 2 );
                for ( TrackedEntityAttributeValue attributeValue : programStageInstance.getProgramInstance()
                    .getEntityInstance().getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getUid().equals( uid ) )
                    {
                        templateMessage = templateMessage.replace( match, attributeValue.getValue() );
                        break;
                    }
                }
            }
        }

        return templateMessage;
    }

    @Override
    public Set<String> getPhonenumbers( TrackedEntityInstanceReminder reminder, TrackedEntityInstance entityInstance )
    {
        Set<String> phoneNumbers = new HashSet<String>();
        switch ( reminder.getSendTo() )
        {
        case TrackedEntityInstanceReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED:
            Collection<User> users = entityInstance.getOrganisationUnit().getUsers();
            for ( User user : users )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            break;
        case TrackedEntityInstanceReminder.SEND_TO_ATTRIBUTE_TYPE_USERS:
            if ( entityInstance.getAttributeValues() != null )
            {
                for ( TrackedEntityAttributeValue attributeValue : entityInstance.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_USERS ) )
                    {
                        User user = userService.getUser( Integer.parseInt( attributeValue.getValue() ) );
                        if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                        {
                            phoneNumbers.add( user.getPhoneNumber() );
                        }
                    }
                }
            }
            break;
        case TrackedEntityInstanceReminder.SEND_TO_ORGUGNIT_REGISTERED:
            if ( entityInstance.getOrganisationUnit().getPhoneNumber() != null
                && !entityInstance.getOrganisationUnit().getPhoneNumber().isEmpty() )
            {
                phoneNumbers.add( entityInstance.getOrganisationUnit().getPhoneNumber() );
            }
            break;
        case TrackedEntityInstanceReminder.SEND_TO_USER_GROUP:
            for ( User user : reminder.getUserGroup().getMembers() )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            break;
        default:
            if ( entityInstance.getAttributeValues() != null )
            {
                for ( TrackedEntityAttributeValue attributeValue : entityInstance.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_PHONE_NUMBER ) )
                    {
                        phoneNumbers.add( attributeValue.getValue() );
                    }
                }
            }
            break;
        }
        return phoneNumbers;
    }

    public Set<User> getUsers( TrackedEntityInstanceReminder reminder, TrackedEntityInstance entityInstance )
    {
        Set<User> users = new HashSet<User>();

        switch ( reminder.getSendTo() )
        {
        case TrackedEntityInstanceReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED:
            users.addAll( entityInstance.getOrganisationUnit().getUsers() );
            break;
        case TrackedEntityInstanceReminder.SEND_TO_ATTRIBUTE_TYPE_USERS:
            if ( entityInstance.getAttributeValues() != null )
            {
                for ( TrackedEntityAttributeValue attributeValue : entityInstance.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_USERS ) )
                    {
                        users.add( userService.getUser( Integer.parseInt( attributeValue.getValue() ) ) );
                    }
                }
            }
            break;
        case TrackedEntityInstanceReminder.SEND_TO_USER_GROUP:
            if ( reminder.getUserGroup().getMembers().size() > 0 )
            {
                users.addAll( reminder.getUserGroup().getMembers() );
            }
            break;
        default:
            break;
        }
        return users;
    }
}
