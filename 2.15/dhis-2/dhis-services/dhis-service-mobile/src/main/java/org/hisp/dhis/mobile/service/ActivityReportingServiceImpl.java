package org.hisp.dhis.mobile.service;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.TrackedEntityMobileSettingService;
import org.hisp.dhis.api.mobile.model.Activity;
import org.hisp.dhis.api.mobile.model.ActivityPlan;
import org.hisp.dhis.api.mobile.model.ActivityValue;
import org.hisp.dhis.api.mobile.model.Beneficiary;
import org.hisp.dhis.api.mobile.model.DataValue;
import org.hisp.dhis.api.mobile.model.OptionSet;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.Task;
import org.hisp.dhis.api.mobile.model.LWUITmodel.LostEvent;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Notification;
import org.hisp.dhis.api.mobile.model.LWUITmodel.PatientList;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Section;
import org.hisp.dhis.api.mobile.model.comparator.ActivityComparator;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.message.Message;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.comparator.ProgramStageInstanceVisitDateComparator;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminder;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityMobileSetting;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValue;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.hisp.dhis.trackedentity.TrackedEntityService;

public class ActivityReportingServiceImpl
    implements ActivityReportingService
{
    private static final String PROGRAM_STAGE_UPLOADED = "program_stage_uploaded";

    private static final String PROGRAM_STAGE_SECTION_UPLOADED = "program_stage_section_uploaded";

    private static final String SINGLE_EVENT_UPLOADED = "single_event_uploaded";
	
	private static final String SINGLE_EVENT_WITHOUT_REGISTRATION_UPLOADED = "single_event_without_registration_uploaded";
	
	private static final String PROGRAM_COMPLETED = "program_completed";

    private ActivityComparator activityComparator = new ActivityComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    private TrackedEntityInstanceService entityInstanceService;

    private TrackedEntityAttributeValueService attValueService;

    private TrackedEntityDataValueService dataValueService;

    private TrackedEntityMobileSettingService mobileSettingService;

    private ProgramStageSectionService programStageSectionService;

    private ProgramInstanceService programInstanceService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    private DataElementService dataElementService;

    private ProgramService programService;

    private ProgramStageService programStageService;

    private CurrentUserService currentUserService;

    private MessageService messageService;

    private SmsSender smsSender;

    private TrackedEntityAttributeService attributeService;
    
    private TrackedEntityService trackedEntityService;

    private Integer patientId;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Required
    public void setAttValueService( TrackedEntityAttributeValueService attValueService )
    {
        this.attValueService = attValueService;
    }

    @Required
    public void setMobileSettingService( TrackedEntityMobileSettingService mobileSettingService )
    {
        this.mobileSettingService = mobileSettingService;
    }

    public void setSetting( TrackedEntityMobileSetting setting )
    {
        this.setting = setting;
    }

    public void setGroupByAttribute( TrackedEntityAttribute groupByAttribute )
    {
        this.groupByAttribute = groupByAttribute;
    }

    @Required
    public void setEntityInstanceService( TrackedEntityInstanceService entityInstanceService )
    {
        this.entityInstanceService = entityInstanceService;
    }

    @Required
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    @Required
    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    @Required
    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    @Required
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    @Required
    public void setDataValueService( TrackedEntityDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // MobileDataSetService
    // -------------------------------------------------------------------------

    private TrackedEntityMobileSetting setting;

    private TrackedEntityAttribute groupByAttribute;

    @Override
    public ActivityPlan getCurrentActivityPlan( OrganisationUnit unit, String localeString )
    {
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.DATE, 30 );

        long upperBound = cal.getTime().getTime();

        cal.add( Calendar.DATE, -60 );
        long lowerBound = cal.getTime().getTime();

        List<Activity> items = new ArrayList<Activity>();
        Collection<TrackedEntityInstance> patients = entityInstanceService.getTrackedEntityInstances( unit, 0,
            Integer.MAX_VALUE );

        for ( TrackedEntityInstance patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                if ( programStageInstance.getDueDate().getTime() >= lowerBound
                    && programStageInstance.getDueDate().getTime() <= upperBound )
                {
                    items.add( getActivity( programStageInstance, false ) );
                }
            }
        }

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );

        return new ActivityPlan( items );
    }

    @Override
    public ActivityPlan getAllActivityPlan( OrganisationUnit unit, String localeString )
    {

        List<Activity> items = new ArrayList<Activity>();
        Collection<TrackedEntityInstance> patients = entityInstanceService.getTrackedEntityInstances( unit, 0,
            Integer.MAX_VALUE );

        for ( TrackedEntityInstance patient : patients )
        {
            for ( ProgramStageInstance programStageInstance : programStageInstanceService.getProgramStageInstances(
                patient, false ) )
            {
                items.add( getActivity( programStageInstance, false ) );
            }
        }

        if ( items.isEmpty() )
        {
            return null;
        }

        Collections.sort( items, activityComparator );
        return new ActivityPlan( items );
    }

    // -------------------------------------------------------------------------
    // DataValueService
    // -------------------------------------------------------------------------

    @Override
    public void saveActivityReport( OrganisationUnit unit, ActivityValue activityValue, Integer programStageSectionId )
        throws NotAllowedException
    {

        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( activityValue
            .getProgramInstanceId() );
        if ( programStageInstance == null )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        programStageInstance.getProgramStage();
        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>();

        ProgramStageSection programStageSection = programStageSectionService
            .getProgramStageSection( programStageSectionId );

        if ( programStageSectionId != 0 )
        {
            for ( ProgramStageDataElement de : programStageSection.getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }
        else
        {
            for ( ProgramStageDataElement de : programStageInstance.getProgramStage().getProgramStageDataElements() )
            {
                dataElements.add( de.getDataElement() );
            }
        }

        programStageInstance.getProgramStage().getProgramStageDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( activityValue.getDataValues().size() );

        for ( DataValue dv : activityValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        if ( dataElements.size() != dataElementIds.size() )
        {
            throw NotAllowedException.INVALID_PROGRAM_STAGE;
        }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                throw NotAllowedException.INVALID_PROGRAM_STAGE;
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Set ProgramStageInstance to completed
        if ( programStageSectionId == 0 )
        {
            programStageInstance.setCompleted( true );
            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }

        // Everything is fine, hence save
        saveDataValues( activityValue, programStageInstance, dataElementMap );

    }

    @Override
    public String findPatient( String keyword, int orgUnitId )
        throws NotAllowedException
    {
        Collection<TrackedEntityInstance> patients = attValueService.getTrackedEntityInstance( null, keyword );

        if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }

        Collection<TrackedEntityAttribute> displayAttributes = attributeService
            .getTrackedEntityAttributesDisplayInList();
        String resultSet = "";

        for ( TrackedEntityInstance patient : patients )
        {
            resultSet += patient.getId() + "/";
            String attText = "";
            for ( TrackedEntityAttribute displayAttribute : displayAttributes )
            {
                TrackedEntityAttributeValue value = attValueService.getTrackedEntityAttributeValue( patient,
                    displayAttribute );
                attText += value + " ";
            }
            attText = attText.trim();
            resultSet += attText + "$";
        }

        return resultSet;
    }

    @Override
    public String saveProgramStage( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage,
        int patientId, int orgUnitId )
        throws NotAllowedException
    {
        if ( mobileProgramStage.isSingleEvent() )
        {
            TrackedEntityInstance patient = entityInstanceService.getTrackedEntityInstance( patientId );
            ProgramStageInstance prStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );
            ProgramStage programStage = programStageService.getProgramStage( prStageInstance.getProgramStage().getId() );
            OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

            // ---------------------------------------------------------------------
            // Add a new program-instance
            // ---------------------------------------------------------------------
            ProgramInstance programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( new Date() );
            programInstance.setDateOfIncident( new Date() );
            programInstance.setProgram( programStage.getProgram() );
            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstance.setEntityInstance( patient );

            programInstanceService.addProgramInstance( programInstance );

            // ---------------------------------------------------------------------
            // Add a new program-stage-instance
            // ---------------------------------------------------------------------

            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( new Date() );
            programStageInstance.setExecutionDate( new Date() );
            programStageInstance.setOrganisationUnit( organisationUnit );
            programStageInstance.setCompleted( true );
            programStageInstanceService.addProgramStageInstance( programStageInstance );

            // ---------------------------------------------------------------------
            // Save value
            // ---------------------------------------------------------------------

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );

                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                TrackedEntityDataValue patientDataValue = new TrackedEntityDataValue();
                patientDataValue.setDataElement( dataElement );

                patientDataValue.setValue( value );
                patientDataValue.setProgramStageInstance( programStageInstance );
                patientDataValue.setTimestamp( new Date() );
                dataValueService.saveTrackedEntityDataValue( patientDataValue );

            }

            return SINGLE_EVENT_UPLOADED;

        }
        else
        {
            ProgramStageInstance programStageInstance = programStageInstanceService
                .getProgramStageInstance( mobileProgramStage.getId() );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> dataElements = mobileProgramStage
                .getDataElements();

            for ( int i = 0; i < dataElements.size(); i++ )
            {
                DataElement dataElement = dataElementService.getDataElement( dataElements.get( i ).getId() );
                String value = dataElements.get( i ).getValue();

                if ( dataElement.getType().equalsIgnoreCase( "date" ) && !value.trim().equals( "" ) )
                {
                    value = PeriodUtil.convertDateFormat( value );
                }

                TrackedEntityDataValue previousPatientDataValue = dataValueService.getTrackedEntityDataValue(
                    programStageInstance, dataElement );

                if ( previousPatientDataValue == null )
                {
                    TrackedEntityDataValue patientDataValue = new TrackedEntityDataValue( programStageInstance,
                        dataElement, new Date(), value );
                    dataValueService.saveTrackedEntityDataValue( patientDataValue );
                }
                else
                {
                    previousPatientDataValue.setValue( value );
                    previousPatientDataValue.setTimestamp( new Date() );
                    previousPatientDataValue.setProvidedElsewhere( false );
                    dataValueService.updateTrackedEntityDataValue( previousPatientDataValue );
                }

            }

            if ( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) != null )
            {
                programStageInstance.setExecutionDate( PeriodUtil.stringToDate( mobileProgramStage.getReportDate() ) );
            }
            else
            {
                programStageInstance.setExecutionDate( new Date() );
            }

            if ( programStageInstance.getProgramStage().getProgramStageDataElements().size() > dataElements.size() )
            {
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
                return PROGRAM_STAGE_SECTION_UPLOADED;
            }
            else
            {
                programStageInstance.setCompleted( mobileProgramStage.isCompleted() );

                // check if any compulsory value is null
                for ( int i = 0; i < dataElements.size(); i++ )
                {
                    if ( dataElements.get( i ).isCompulsory() == true )
                    {
                        if ( dataElements.get( i ).getValue() == null )
                        {
                            programStageInstance.setCompleted( false );
                            // break;
                            throw NotAllowedException.INVALID_PROGRAM_STAGE;
                        }
                    }
                }
                programStageInstanceService.updateProgramStageInstance( programStageInstance );

                // check if all belonged program stage are completed
                if ( !mobileProgramStage.isRepeatable() && isAllProgramStageFinished( programStageInstance ) == true )
                {
                    ProgramInstance programInstance = programStageInstance.getProgramInstance();
                    programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
                    programInstanceService.updateProgramInstance( programInstance );
                }
                if ( mobileProgramStage.isRepeatable() )
                {
                    Date nextDate = DateUtils.getDateAfterAddition( new Date(),
                        mobileProgramStage.getStandardInterval() );

                    return PROGRAM_STAGE_UPLOADED + "$" + PeriodUtil.dateToString( nextDate );
                }
                else
                {
                    return PROGRAM_STAGE_UPLOADED;
                }
            }
        }
    }
    
    public String completeProgramInstance( int programId )
    {
    	ProgramInstance programInstance = programInstanceService.getProgramInstance( programId );
        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstanceService.updateProgramInstance( programInstance );
        
        return PROGRAM_COMPLETED;
    }

    private boolean isAllProgramStageFinished( ProgramStageInstance programStageInstance )
    {
        ProgramInstance programInstance = programStageInstance.getProgramInstance();
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();
        if ( programStageInstances != null )
        {
            Iterator<ProgramStageInstance> iterator = programStageInstances.iterator();

            while ( iterator.hasNext() )
            {
                ProgramStageInstance each = iterator.next();
                if ( !each.isCompleted() )
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient enrollProgram( String enrollInfo, Date incidentDate )
        throws NotAllowedException
    {
        String[] enrollProgramInfo = enrollInfo.split( "-" );
        int patientId = Integer.parseInt( enrollProgramInfo[0] );
        int programId = Integer.parseInt( enrollProgramInfo[1] );

        TrackedEntityInstance patient = entityInstanceService.getTrackedEntityInstance( patientId );
        Program program = programService.getProgram( programId );

        ProgramInstance programInstance = new ProgramInstance();
        programInstance.setEnrollmentDate( new Date() );
        programInstance.setDateOfIncident( incidentDate );
        programInstance.setProgram( program );
        programInstance.setEntityInstance( patient );
        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        programInstanceService.addProgramInstance( programInstance );
        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                Date dateCreatedEvent = new Date();
                if ( programStage.getGeneratedByEnrollmentDate() )
                {
                    // dateCreatedEvent = sdf.parseDateTime( enrollmentDate
                    // ).toDate();
                }
                Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                programStageInstance.setDueDate( dueDate );

                if ( program.isSingleEvent() )
                {
                    programStageInstance.setExecutionDate( dueDate );
                }

                programStageInstanceService.addProgramStageInstance( programStageInstance );
                programInstance.getProgramStageInstances().add( programStageInstance );
            }
        }
        programInstanceService.updateProgramInstance( programInstance );
        patient.getProgramInstances().add( programInstance );
        entityInstanceService.updateTrackedEntityInstance( patient );

        patient = entityInstanceService.getTrackedEntityInstance( patientId );
        
        programInstanceService.sendMessages(programInstance, TrackedEntityInstanceReminder.SEND_WHEN_TO_EMROLLEMENT);

        return getPatientModel( patient );
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private Activity getActivity( ProgramStageInstance instance, boolean late )
    {

        Activity activity = new Activity();
        TrackedEntityInstance patient = instance.getProgramInstance().getEntityInstance();

        activity.setBeneficiary( getBeneficiaryModel( patient ) );
        activity.setDueDate( instance.getDueDate() );
        activity.setTask( getTask( instance ) );
        activity.setLate( late );
        activity.setExpireDate( DateUtils.getDateAfterAddition( instance.getDueDate(), 30 ) );

        return activity;
    }

    private Task getTask( ProgramStageInstance instance )
    {
        if ( instance == null )
            return null;

        Task task = new Task();
        task.setCompleted( instance.isCompleted() );
        task.setId( instance.getId() );
        task.setProgramStageId( instance.getProgramStage().getId() );
        task.setProgramId( instance.getProgramInstance().getProgram().getId() );
        return task;
    }

    private Beneficiary getBeneficiaryModel( TrackedEntityInstance patient )
    {
        Beneficiary beneficiary = new Beneficiary();
        List<org.hisp.dhis.api.mobile.model.PatientAttribute> patientAtts = new ArrayList<org.hisp.dhis.api.mobile.model.PatientAttribute>();
        List<TrackedEntityAttribute> atts;

        beneficiary.setId( patient.getId() );
        beneficiary.setName( patient.getName() );

        this.setSetting( getSettings() );

        if ( setting != null )
        {
            atts = setting.getAttributes();
            for ( TrackedEntityAttribute each : atts )
            {
                TrackedEntityAttributeValue value = attValueService.getTrackedEntityAttributeValue( patient, each );
                if ( value != null )
                {
                    // patientAtts.add( new TrackedEntityAttribute(
                    // each.getName(),
                    // value.getValue(), each.getValueType(),
                    // new ArrayList<String>() ) );
                }
            }

        }

        // Set attribute which is used to group beneficiary on mobile (only if
        // there is attribute which is set to be group factor)
        org.hisp.dhis.api.mobile.model.PatientAttribute beneficiaryAttribute = null;

        if ( groupByAttribute != null )
        {
            beneficiaryAttribute = new org.hisp.dhis.api.mobile.model.PatientAttribute();
            beneficiaryAttribute.setName( groupByAttribute.getName() );
            TrackedEntityAttributeValue value = attValueService.getTrackedEntityAttributeValue( patient,
                groupByAttribute );
            beneficiaryAttribute.setValue( value == null ? "Unknown" : value.getValue() );
            beneficiary.setGroupAttribute( beneficiaryAttribute );
        }

        beneficiary.setPatientAttValues( patientAtts );
        return beneficiary;
    }

    // get patient model for LWUIT
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientModel( TrackedEntityInstance patient )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientModel = new org.hisp.dhis.api.mobile.model.LWUITmodel.Patient();
        List<org.hisp.dhis.api.mobile.model.PatientAttribute> patientAtts = new ArrayList<org.hisp.dhis.api.mobile.model.PatientAttribute>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance> mobileProgramInstanceList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance>();

        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance> mobileCompletedProgramInstanceList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance>();

        patientModel.setId( patient.getId() );

        if ( patient.getOrganisationUnit() != null )
        {
            patientModel.setOrganisationUnitName( patient.getOrganisationUnit().getName() );
        }

        this.setSetting( getSettings() );

        List<TrackedEntityAttributeValue> atts = new ArrayList<TrackedEntityAttributeValue>(
            patient.getAttributeValues() );

        for ( TrackedEntityAttributeValue value : atts )
        {
            if ( value != null )
            {
                org.hisp.dhis.api.mobile.model.PatientAttribute patientAttribute = new org.hisp.dhis.api.mobile.model.PatientAttribute(
                    value.getAttribute().getName(), value.getValue(), value.getAttribute().getValueType(), false, value
                        .getAttribute().getDisplayInListNoProgram(), new OptionSet() );

                patientAtts.add( patientAttribute );
            }
        }

        patientModel.setAttributes( patientAtts );

        // Set current programs
        List<ProgramInstance> listOfProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_ACTIVE ) );

        if ( listOfProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfProgramInstance )
            {
                mobileProgramInstanceList.add( getMobileProgramInstance( each ) );
            }
        }
        patientModel.setEnrollmentPrograms( mobileProgramInstanceList );

        // Set completed programs
        List<ProgramInstance> listOfCompletedProgramInstance = new ArrayList<ProgramInstance>(
            programInstanceService.getProgramInstances( patient, ProgramInstance.STATUS_COMPLETED ) );

        if ( listOfCompletedProgramInstance.size() > 0 )
        {
            for ( ProgramInstance each : listOfCompletedProgramInstance )
            {
                mobileCompletedProgramInstanceList.add( getMobileProgramInstance( each ) );
            }
        }

        patientModel.setCompletedPrograms( mobileCompletedProgramInstanceList );

        // Set Relationship
        List<Relationship> relationships = new ArrayList<Relationship>(
            relationshipService.getRelationshipsForTrackedEntityInstance( patient ) );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship> relationshipList = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();

        for ( Relationship eachRelationship : relationships )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship relationshipMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
            relationshipMobile.setId( eachRelationship.getId() );
            if ( eachRelationship.getEntityInstanceA().getId() == patient.getId() )
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getaIsToB() );
                relationshipMobile.setPersonBName( eachRelationship.getEntityInstanceB().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getEntityInstanceB().getId() );
            }
            else
            {
                relationshipMobile.setName( eachRelationship.getRelationshipType().getbIsToA() );
                relationshipMobile.setPersonBName( eachRelationship.getEntityInstanceA().getName() );
                relationshipMobile.setPersonBId( eachRelationship.getEntityInstanceA().getId() );
            }
            relationshipList.add( relationshipMobile );
        }
        patientModel.setRelationships( relationshipList );

        // Set available enrollment relationships
        // List<RelationshipType> enrollmentRelationshipList = new
        // ArrayList<RelationshipType>(
        // relationshipTypeService.getAllRelationshipTypes() );
        // List<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>
        // enrollmentRelationshipMobileList = new
        // ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship>();
        // for ( RelationshipType enrollmentRelationship :
        // enrollmentRelationshipList )
        // {
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship
        // enrollmentRelationshipMobile = new
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship();
        // enrollmentRelationshipMobile.setId( enrollmentRelationship.getId() );
        // enrollmentRelationshipMobile.setName(
        // enrollmentRelationship.getName() );
        // enrollmentRelationshipMobile.setaIsToB(
        // enrollmentRelationship.getaIsToB() );
        // enrollmentRelationshipMobile.setbIsToA(
        // enrollmentRelationship.getbIsToA() );
        // enrollmentRelationshipMobileList.add( enrollmentRelationshipMobile );
        // }
        // patientModel.setRelationships( enrollmentRelationshipMobileList );
        return patientModel;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance getMobileProgramInstance(
        ProgramInstance programInstance )
    {
        org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance mobileProgramInstance = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance();

        mobileProgramInstance.setId( programInstance.getId() );
        mobileProgramInstance.setName( programInstance.getProgram().getName() );
        mobileProgramInstance.setStatus( programInstance.getStatus() );
        mobileProgramInstance.setDateOfEnrollment( PeriodUtil.dateToString( programInstance.getEnrollmentDate() ) );
        mobileProgramInstance.setDateOfIncident( PeriodUtil.dateToString( programInstance.getDateOfIncident() ) );
        mobileProgramInstance.setPatientId( programInstance.getEntityInstance().getId() );
        mobileProgramInstance.setProgramId( programInstance.getProgram().getId() );
        mobileProgramInstance.setProgramStageInstances( getMobileProgramStages( programInstance ) );
        return mobileProgramInstance;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> getMobileProgramStages(
        ProgramInstance programInstance )
    {
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

        for ( ProgramStageInstance eachProgramStageInstance : programInstance.getProgramStageInstances() )
        {
            // only for Mujhu database, because there is null program stage
            // instance. This condition should be removed in the future
            if ( eachProgramStageInstance != null )
            {
                ProgramStage programStage = eachProgramStageInstance.getProgramStage();

                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();
                List<org.hisp.dhis.api.mobile.model.LWUITmodel.Section> mobileSections = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.Section>();
                mobileProgramStage.setId( eachProgramStageInstance.getId() );
                /* mobileProgramStage.setName( eachProgramStage.getName() ); */
                mobileProgramStage.setName( programStage.getName() );

                // get report date
                if ( eachProgramStageInstance.getExecutionDate() != null )
                {
                    mobileProgramStage.setReportDate( PeriodUtil.dateToString( eachProgramStageInstance
                        .getExecutionDate() ) );
                }
                else
                {
                    mobileProgramStage.setReportDate( "" );
                }
                
                if ( programStage.getReportDateDescription() == null )
                {
                    mobileProgramStage.setReportDateDescription( "Report Date" );
                }
                else
                {
                    mobileProgramStage.setReportDateDescription( programStage.getReportDateDescription() );
                }

                // get due date
                if ( eachProgramStageInstance.getDueDate() != null )
                {
                    mobileProgramStage.setDueDate( PeriodUtil.dateToString( eachProgramStageInstance
                        .getDueDate() ) );
                }
                else
                {
                    mobileProgramStage.setDueDate( "" );
                }

                // is repeatable
                mobileProgramStage.setRepeatable( programStage.getIrregular() );

                if ( programStage.getStandardInterval() == null )
                {
                    mobileProgramStage.setStandardInterval( 0 );
                }
                else
                {
                    mobileProgramStage.setStandardInterval( programStage.getStandardInterval() );
                }

                // is completed
                /*
                 * mobileProgramStage.setCompleted(
                 * checkIfProgramStageCompleted( patient,
                 * programInstance.getProgram(), programStage ) );
                 */
                mobileProgramStage.setCompleted( eachProgramStageInstance.isCompleted() );

                // is single event
                mobileProgramStage.setSingleEvent( programInstance.getProgram().isSingleEvent() );

                // Set all data elements
                mobileProgramStage.setDataElements( getDataElementsForMobile( programStage, eachProgramStageInstance ) );

                // Set all program sections
                if ( programStage.getProgramStageSections().size() > 0 )
                {
                    for ( ProgramStageSection eachSection : programStage.getProgramStageSections() )
                    {
                        org.hisp.dhis.api.mobile.model.LWUITmodel.Section mobileSection = new org.hisp.dhis.api.mobile.model.LWUITmodel.Section();
                        mobileSection.setId( eachSection.getId() );
                        mobileSection.setName( eachSection.getName() );

                        // Set all data elements' id, then we could have full
                        // from
                        // data element list of program stage
                        List<Integer> dataElementIds = new ArrayList<Integer>();
                        for ( ProgramStageDataElement eachPogramStageDataElement : eachSection
                            .getProgramStageDataElements() )
                        {
                            dataElementIds.add( eachPogramStageDataElement.getDataElement().getId() );
                        }
                        mobileSection.setDataElementIds( dataElementIds );
                        mobileSections.add( mobileSection );
                    }
                }
                mobileProgramStage.setSections( mobileSections );

                mobileProgramStages.add( mobileProgramStage );
            }
        }
        return mobileProgramStages;
    }

    private List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> getDataElementsForMobile(
        ProgramStage programStage, ProgramStageInstance programStageInstance )
    {
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
            programStage.getProgramStageDataElements() );
        List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();
        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
            mobileDataElement.setId( programStageDataElement.getDataElement().getId() );

            String dataElementName;

            if ( programStageDataElement.getDataElement().getFormName() != null
                && !programStageDataElement.getDataElement().getFormName().trim().equals( "" ) )
            {
                dataElementName = programStageDataElement.getDataElement().getFormName();
            }
            else
            {
                dataElementName = programStageDataElement.getDataElement().getName();
            }

            mobileDataElement.setName( dataElementName );
            mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

            // problem
            mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

            mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

            // Value
            TrackedEntityDataValue patientDataValue = dataValueService.getTrackedEntityDataValue( programStageInstance,
                programStageDataElement.getDataElement() );
            if ( patientDataValue != null )
            {
                // Convert to standard date format before send to client
                if ( programStageDataElement.getDataElement().getType().equalsIgnoreCase( "date" )
                    && !patientDataValue.equals( "" ) )
                {
                    mobileDataElement.setValue( PeriodUtil.convertDateFormat( patientDataValue.getValue() ) );
                }
                else
                {
                    mobileDataElement.setValue( patientDataValue.getValue() );
                }
            }
            else
            {
                mobileDataElement.setValue( null );
            }

            // Option set
            if ( programStageDataElement.getDataElement().getOptionSet() != null )
            {
                mobileDataElement.setOptionSet( ModelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setOptionSet( null );
            }

            // Category Option Combo
            if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
            {
                mobileDataElement.setCategoryOptionCombos( ModelMapping
                    .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
            }
            else
            {
                mobileDataElement.setCategoryOptionCombos( null );
            }
            mobileDataElements.add( mobileDataElement );
        }
        return mobileDataElements;
    }

    private TrackedEntityMobileSetting getSettings()
    {
        TrackedEntityMobileSetting setting = null;

        Collection<TrackedEntityMobileSetting> currentSetting = mobileSettingService.getCurrentSetting();
        if ( currentSetting != null && !currentSetting.isEmpty() )
            setting = currentSetting.iterator().next();
        return setting;
    }

    private boolean isNumber( String value )
    {
        try
        {
            Double.parseDouble( value );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }
        return true;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient addRelationship(
        org.hisp.dhis.api.mobile.model.LWUITmodel.Relationship enrollmentRelationship, int orgUnitId )
        throws NotAllowedException
    {
        TrackedEntityInstance patientB;
        if ( enrollmentRelationship.getPersonBId() != 0 )
        {
            patientB = entityInstanceService.getTrackedEntityInstance( enrollmentRelationship.getPersonBId() );
        }
        else
        {
            List<TrackedEntityInstance> patients = new ArrayList<TrackedEntityInstance>();

            // remove the own searcher
            patients = removeIfDuplicated( patients, enrollmentRelationship.getPersonAId() );

            if ( patients.size() > 1 )
            {
                String patientsInfo = "";

                for ( TrackedEntityInstance each : patients )
                {
                    patientsInfo += each.getId() + "/" + each.getName() + "$";
                }

                throw new NotAllowedException( patientsInfo );
            }
            else if ( patients.size() == 0 )
            {
                throw NotAllowedException.NO_BENEFICIARY_FOUND;
            }
            else
            {
                patientB = patients.get( 0 );
            }
        }
        TrackedEntityInstance patientA = entityInstanceService.getTrackedEntityInstance( enrollmentRelationship
            .getPersonAId() );
        RelationshipType relationshipType = relationshipTypeService
            .getRelationshipType( enrollmentRelationship.getId() );

        Relationship relationship = new Relationship();
        relationship.setRelationshipType( relationshipType );
        if ( enrollmentRelationship.getChosenRelationship().equals( relationshipType.getaIsToB() ) )
        {
            relationship.setEntityInstanceA( patientA );
            relationship.setEntityInstanceB( patientB );
        }
        else
        {
            relationship.setEntityInstanceA( patientB );
            relationship.setEntityInstanceB( patientA );
        }
        relationshipService.addRelationship( relationship );
        // return getPatientModel( orgUnitId, patientA );
        return getPatientModel( patientA );
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program getAllProgramByOrgUnit( int orgUnitId, String programType )
        throws NotAllowedException
    {
        String programsInfo = "";

        int programTypeInt = Integer.valueOf( programType );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        List<Program> tempPrograms = null;

        if ( programTypeInt == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        }
        else if ( programTypeInt == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            tempPrograms = new ArrayList<Program>(
                programService.getProgramsByCurrentUser( Program.MULTIPLE_EVENTS_WITH_REGISTRATION ) );
        }

        List<Program> programs = new ArrayList<Program>();

        for ( Program program : tempPrograms )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }

        if ( programs.size() != 0 )
        {
            if ( programs.size() == 1 )
            {
                Program program = programs.get( 0 );

                return getMobileProgramWithoutData( program );
            }
            else
            {
                for ( Program program : programs )
                {
                    if ( program.getOrganisationUnits().contains( organisationUnit ) )
                    {
                        programsInfo += program.getId() + "/" + program.getName() + "$";
                    }
                }
                throw new NotAllowedException( programsInfo );
            }
        }
        else
        {
            throw NotAllowedException.NO_PROGRAM_FOUND;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Program findProgram( String programInfo )
        throws NotAllowedException
    {
        if ( isNumber( programInfo ) == false )
        {
            return null;
        }
        else
        {
            Program program = programService.getProgram( Integer.parseInt( programInfo ) );
            if ( program.isSingleEvent() )
            {
                return getMobileProgramWithoutData( program );
            }
            else
            {
                return null;
            }
        }
    }

    // If the return program is anonymous, the client side will show the entry
    // form as normal
    // If the return program is not anonymous, it is still OK because in client
    // side, we only need name and id
    private org.hisp.dhis.api.mobile.model.LWUITmodel.Program getMobileProgramWithoutData( Program program )
    {
        Comparator<ProgramStageDataElement> OrderBySortOrder = new Comparator<ProgramStageDataElement>()
        {
            public int compare( ProgramStageDataElement i1, ProgramStageDataElement i2 )
            {
                return i1.getSortOrder().compareTo( i2.getSortOrder() );
            }
        };

        org.hisp.dhis.api.mobile.model.LWUITmodel.Program anonymousProgramMobile = new org.hisp.dhis.api.mobile.model.LWUITmodel.Program();

        anonymousProgramMobile.setId( program.getId() );

        anonymousProgramMobile.setName( program.getName() );

        // if ( program.getType() == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            anonymousProgramMobile.setVersion( program.getVersion() );

            // anonymousProgramMobile.setStatus( ProgramInstance.STATUS_ACTIVE
            // );

            ProgramStage programStage = program.getProgramStages().iterator().next();

            List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>(
                programStage.getProgramStageDataElements() );
            Collections.sort( programStageDataElements, OrderBySortOrder );

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage> mobileProgramStages = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage>();

            org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage();

            List<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement> mobileProgramStageDataElements = new ArrayList<org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement>();

            mobileProgramStage.setId( programStage.getId() );
            mobileProgramStage.setName( programStage.getName() );
            mobileProgramStage.setCompleted( false );
            mobileProgramStage.setRepeatable( false );
            mobileProgramStage.setSingleEvent( true );
            mobileProgramStage.setSections( new ArrayList<Section>() );

            // get report date
            mobileProgramStage.setReportDate( PeriodUtil.dateToString( new Date() ) );

            if ( programStage.getReportDateDescription() == null )
            {
                mobileProgramStage.setReportDateDescription( "Report Date" );
            }
            else
            {
                mobileProgramStage.setReportDateDescription( programStage.getReportDateDescription() );
            }

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement = new org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement();
                mobileDataElement.setId( programStageDataElement.getDataElement().getId() );
                mobileDataElement.setName( programStageDataElement.getDataElement().getName() );
                mobileDataElement.setType( programStageDataElement.getDataElement().getType() );

                // problem
                mobileDataElement.setCompulsory( programStageDataElement.isCompulsory() );

                mobileDataElement.setNumberType( programStageDataElement.getDataElement().getNumberType() );

                mobileDataElement.setValue( "" );

                if ( programStageDataElement.getDataElement().getOptionSet() != null )
                {
                    mobileDataElement
                        .setOptionSet( ModelMapping.getOptionSet( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setOptionSet( null );
                }
                if ( programStageDataElement.getDataElement().getCategoryCombo() != null )
                {
                    mobileDataElement.setCategoryOptionCombos( ModelMapping
                        .getCategoryOptionCombos( programStageDataElement.getDataElement() ) );
                }
                else
                {
                    mobileDataElement.setCategoryOptionCombos( null );
                }
                mobileProgramStageDataElements.add( mobileDataElement );
            }
            mobileProgramStage.setDataElements( mobileProgramStageDataElements );
            mobileProgramStages.add( mobileProgramStage );
            anonymousProgramMobile.setProgramStages( mobileProgramStages );
        }

        return anonymousProgramMobile;
    }

    private List<TrackedEntityInstance> removeIfDuplicated( List<TrackedEntityInstance> patients, int patientId )
    {
        List<TrackedEntityInstance> result = new ArrayList<TrackedEntityInstance>( patients );
        for ( int i = 0; i < patients.size(); i++ )
        {
            if ( patients.get( i ).getId() == patientId )
            {
                result.remove( i );
            }
        }
        return result;
    }

    private void saveDataValues( ActivityValue activityValue, ProgramStageInstance programStageInstance,
        Map<Integer, DataElement> dataElementMap )
    {
        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : activityValue.getDataValues() )
        {
            value = dv.getValue();

            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            TrackedEntityDataValue dataValue = dataValueService.getTrackedEntityDataValue( programStageInstance,
                dataElement );
            if ( dataValue == null )
            {
                if ( value != null )
                {
                    if ( programStageInstance.getExecutionDate() == null )
                    {
                        programStageInstance.setExecutionDate( new Date() );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }

                    dataValue = new TrackedEntityDataValue( programStageInstance, dataElement, new Date(), value );

                    dataValueService.saveTrackedEntityDataValue( dataValue );
                }
            }
            else
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( new Date() );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );

                dataValueService.updateTrackedEntityDataValue( dataValue );
            }
        }
    }

    public Collection<TrackedEntityAttribute> getPatientAtts( String programId )
    {
        Collection<TrackedEntityAttribute> patientAttributes = null;

        if ( programId != null && !programId.trim().equals( "" ) )
        {
            Program program = programService.getProgram( Integer.parseInt( programId ) );
            patientAttributes = program.getTrackedEntityAttributes();
        }
        else
        {
            patientAttributes = attributeService.getAllTrackedEntityAttributes();
        }

        return patientAttributes;
    }

    public Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> getAttsForMobile()
    {
        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> list = new HashSet<org.hisp.dhis.api.mobile.model.PatientAttribute>();

        for ( TrackedEntityAttribute patientAtt : getPatientAtts( null ) )
        {
            list.add( new PatientAttribute( patientAtt.getName(), null, patientAtt.getValueType(), false, patientAtt
                .getDisplayInListNoProgram(), new OptionSet() ) );
        }

        return list;

    }

    @Override
    public Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> getPatientAttributesForMobile( String programId )
    {
        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> list = new HashSet<org.hisp.dhis.api.mobile.model.PatientAttribute>();
        for ( TrackedEntityAttribute pa : getPatientAtts( programId ) )
        {
            PatientAttribute patientAttribute = new PatientAttribute();
            String name = pa.getName();

            patientAttribute.setName( name );
            patientAttribute.setType( pa.getValueType() );
            patientAttribute.setValue( "" );

            list.add( patientAttribute );
        }
        return list;
    }

    @Required
    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Required
    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findLatestPatient()
        throws NotAllowedException
    {
        // Patient patient = entityInstanceService.getPatient( this.patientId );
        //
        // org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile =
        // getPatientModel( patient );
        return this.getPatientMobile();
    }

    @Override
    public Integer savePatient( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patient, int orgUnitId,
        String programIdText )
        throws NotAllowedException
    {
        TrackedEntityInstance patientWeb = new TrackedEntityInstance();

        patientWeb.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );

        Set<TrackedEntityAttribute> patientAttributeSet = new HashSet<TrackedEntityAttribute>();
        Set<TrackedEntityAttributeValue> patientAttributeValues = new HashSet<TrackedEntityAttributeValue>();

        Collection<org.hisp.dhis.api.mobile.model.PatientAttribute> attributesMobile = patient.getAttributes();

        if ( attributesMobile != null )
        {
            for ( org.hisp.dhis.api.mobile.model.PatientAttribute paAtt : attributesMobile )
            {

                TrackedEntityAttribute patientAttribute = attributeService.getTrackedEntityAttributeByName( paAtt.getName() );

                patientAttributeSet.add( patientAttribute );

                TrackedEntityAttributeValue patientAttributeValue = new TrackedEntityAttributeValue();

                patientAttributeValue.setEntityInstance( patientWeb );
                patientAttributeValue.setAttribute( patientAttribute );
                patientAttributeValue.setValue( paAtt.getValue() );
                patientAttributeValues.add( patientAttributeValue );

            }
        }
        
        patientWeb.setTrackedEntity(trackedEntityService.getTrackedEntityByName("Person"));
        patientId = entityInstanceService.createTrackedEntityInstance( patientWeb, null, null, patientAttributeValues );

        try
        {
            for ( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramInstance mobileProgramInstance : patient
                .getEnrollmentPrograms() )
            {
                Date incidentDate = PeriodUtil.stringToDate( mobileProgramInstance.getDateOfIncident() );
                enrollProgram( patientId + "-" + mobileProgramInstance.getProgramId(), incidentDate );
            }
        }
        catch ( Exception e )
        {
            return patientId;
        }
        
        

        TrackedEntityInstance patientNew = entityInstanceService.getTrackedEntityInstance( this.patientId );
        setPatientMobile( getPatientModel( patientNew ) );

        return patientId;

    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient findPatient( int patientId )
        throws NotAllowedException
    {
        TrackedEntityInstance patient = entityInstanceService.getTrackedEntityInstance( patientId );
        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile = getPatientModel( patient );
        return patientMobile;
    }
    
    public org.hisp.dhis.api.mobile.model.LWUITmodel.PatientList findPatients( String patientIds )
        throws NotAllowedException
    {
        PatientList patientlist = new PatientList();
        
        while ( patientIds.length() > 0 )
        {
            int patientId = Integer.parseInt( patientIds.substring( 0, patientIds.indexOf( "$" ) ) );
            TrackedEntityInstance patient = entityInstanceService.getTrackedEntityInstance( patientId );
            patientlist.getPatientList().add( getPatientModel( patient ) );
            patientIds = patientIds.substring( patientIds.indexOf( "$" ) + 1, patientIds.length() );
        }
        
        return patientlist;
    }

    @Override
    public String findPatientInAdvanced( String keyword, int orgUnitId, int programId )
        throws NotAllowedException
    {
        Set<TrackedEntityInstance> patients = new HashSet<TrackedEntityInstance>();

        Collection<TrackedEntityAttribute> attributes = attributeService.getAllTrackedEntityAttributes();

        for ( TrackedEntityAttribute displayAttribute : attributes )
        {
            Collection<TrackedEntityInstance> resultPatients = attValueService.searchTrackedEntityInstances( displayAttribute, keyword );
            // Search in specific OrgUnit
            if ( orgUnitId != 0 )
            {
                for ( TrackedEntityInstance patient : resultPatients )
                {
                    if ( patient.getOrganisationUnit().getId() == orgUnitId )
                    {
                        patients.add( patient );
                    }
                }
            }
            // Search in all OrgUnit
            else
            {
                patients.addAll( resultPatients );
            }

        }

        if ( patients.size() == 0 )
        {
            throw NotAllowedException.NO_BENEFICIARY_FOUND;
        }

        String resultSet = "";

        Collection<TrackedEntityAttribute> displayAttributes = attributeService.getTrackedEntityAttributesDisplayInList();
        for ( TrackedEntityInstance patient : patients )
        {
            resultSet += patient.getId() + "/";
            String attText = "";
            for ( TrackedEntityAttribute displayAttribute : displayAttributes )
            {
                TrackedEntityAttributeValue value = attValueService.getTrackedEntityAttributeValue( patient, displayAttribute );
                if ( value != null )
                {
                    attText += value.getValue() + " ";
                }
            }
            attText = attText.trim();
            resultSet += attText + "$";
        }
        return resultSet;
    }

    @Override
    public String findLostToFollowUp( int orgUnitId, String searchEventInfos )
        throws NotAllowedException
    {
        String[] searchEventInfosArray = searchEventInfos.split( "-" );

        int programStageStatus = 0;

        if ( searchEventInfosArray[1].equalsIgnoreCase( "Scheduled in future" ) )
        {
            programStageStatus = ProgramStageInstance.FUTURE_VISIT_STATUS;
        }
        else if ( searchEventInfosArray[1].equalsIgnoreCase( "Overdue" ) )
        {
            programStageStatus = ProgramStageInstance.LATE_VISIT_STATUS;
        }

        boolean followUp;

        if ( searchEventInfosArray[2].equalsIgnoreCase( "true" ) )
        {
            followUp = true;
        }
        else
        {
            followUp = false;
        }

        String eventsInfo = "";

        DateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd" );

        List<String> searchTextList = new ArrayList<String>();
        Collection<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();

        Calendar toCalendar = new GregorianCalendar();
        toCalendar.add( Calendar.DATE, -1 );
        toCalendar.add( Calendar.YEAR, 100 );
        Date toDate = toCalendar.getTime();

        Calendar fromCalendar = new GregorianCalendar();
        fromCalendar.add( Calendar.DATE, -1 );
        fromCalendar.add( Calendar.YEAR, -100 );

        Date fromDate = fromCalendar.getTime();

        String searchText = TrackedEntityInstance.PREFIX_PROGRAM_EVENT_BY_STATUS + "_" + searchEventInfosArray[0] + "_"
            + formatter.format( fromDate ) + "_" + formatter.format( toDate ) + "_" + orgUnitId + "_" + true + "_"
            + programStageStatus;

        searchTextList.add( searchText );
        orgUnitList.add( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        List<Integer> stageInstanceIds = entityInstanceService.getProgramStageInstances( searchTextList, orgUnitList,
            followUp, ProgramInstance.STATUS_ACTIVE, null, null );

        if ( stageInstanceIds.size() == 0 )
        {
            throw NotAllowedException.NO_EVENT_FOUND;
        }
        else if ( stageInstanceIds.size() > 0 )
        {
            for ( Integer stageInstanceId : stageInstanceIds )
            {
                ProgramStageInstance programStageInstance = programStageInstanceService
                    .getProgramStageInstance( stageInstanceId );
                TrackedEntityInstance patient = programStageInstance.getProgramInstance().getEntityInstance();
                eventsInfo += programStageInstance.getId() + "/" + patient.getName() + ", "
                    + programStageInstance.getProgramStage().getName() + "("
                    + formatter.format( programStageInstance.getDueDate() ) + ")" + "$";
            }

            throw new NotAllowedException( eventsInfo );
        }
        else
        {
            return "";
        }
    }

    @SuppressWarnings( "finally" )
    @Override
    public Notification handleLostToFollowUp( LostEvent lostEvent )
        throws NotAllowedException
    {
        Notification notification = new Notification();
        try
        {
            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( lostEvent
                .getId() );
            programStageInstance.setDueDate( PeriodUtil.stringToDate( lostEvent.getDueDate() ) );
            programStageInstance.setStatus( lostEvent.getStatus() );

            if ( lostEvent.getComment() != null )
            {
                List<MessageConversation> conversationList = new ArrayList<MessageConversation>();

                MessageConversation conversation = new MessageConversation( lostEvent.getName(),
                    currentUserService.getCurrentUser() );

                conversation
                    .addMessage( new Message( lostEvent.getComment(), null, currentUserService.getCurrentUser() ) );

                conversation.setRead( true );

                conversationList.add( conversation );

                programStageInstance.setMessageConversations( conversationList );

                messageService.saveMessageConversation( conversation );
            }

            programStageInstanceService.updateProgramStageInstance( programStageInstance );

            // send SMS
            if ( programStageInstance.getProgramInstance().getEntityInstance().getAttributeValues() != null
                && lostEvent.getSMS() != null )
            {
                List<User> recipientsList = new ArrayList<User>();
                for ( TrackedEntityAttributeValue attrValue : programStageInstance.getProgramInstance()
                    .getEntityInstance().getAttributeValues() )
                {
                    if ( attrValue.getAttribute().getValueType().equals( "phoneNumber" ) )
                    {
                        User user = new User();
                        user.setPhoneNumber( attrValue.getValue() );
                        recipientsList.add( user );
                    }

                }
                smsSender.sendMessage( lostEvent.getName(), lostEvent.getSMS(), currentUserService.getCurrentUser(),
                    recipientsList, false );
            }

            notification.setMessage( "Success" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            notification.setMessage( "Fail" );
        }
        finally
        {
            return notification;
        }
    }

    @Override
    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient generateRepeatableEvent( int orgUnitId, String eventInfo )
        throws NotAllowedException
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        String mobileProgramStageId = eventInfo.substring( 0, eventInfo.indexOf( "$" ) );

        String nextDueDate = eventInfo.substring( eventInfo.indexOf( "$" ) + 1, eventInfo.length() );

        ProgramStageInstance oldProgramStageIntance = programStageInstanceService.getProgramStageInstance( Integer
            .valueOf( mobileProgramStageId ) );

        ProgramInstance programInstance = oldProgramStageIntance.getProgramInstance();

        ProgramStageInstance newProgramStageInstance = new ProgramStageInstance( programInstance,
            oldProgramStageIntance.getProgramStage() );

        newProgramStageInstance.setDueDate( PeriodUtil.stringToDate( nextDueDate ) );

        newProgramStageInstance.setOrganisationUnit( orgUnit );

        programInstance.getProgramStageInstances().add( newProgramStageInstance );

        List<ProgramStageInstance> proStageInstanceList = new ArrayList<ProgramStageInstance>(
            programInstance.getProgramStageInstances() );

        Collections.sort( proStageInstanceList, new ProgramStageInstanceVisitDateComparator() );

        programInstance.getProgramStageInstances().removeAll( proStageInstanceList );
        programInstance.getProgramStageInstances().addAll( proStageInstanceList );

        programStageInstanceService.addProgramStageInstance( newProgramStageInstance );

        programInstanceService.updateProgramInstance( programInstance );

        org.hisp.dhis.api.mobile.model.LWUITmodel.Patient mobilePatient = getPatientModel( entityInstanceService
            .getTrackedEntityInstance( programInstance.getEntityInstance().getId() ) );

        return mobilePatient;
    }
	
	@Override
    public String saveSingleEventWithoutRegistration(
        org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStage mobileProgramStage, int orgUnitId )
        throws NotAllowedException
    {
        ProgramStage programStage = programStageService.getProgramStage( mobileProgramStage.getId() );

        Program program = programStage.getProgram();

        ProgramInstance programInstance = new ProgramInstance();

        programInstance.setEnrollmentDate( new Date() );

        programInstance.setDateOfIncident( new Date() );

        programInstance.setProgram( program );

        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );

        programInstanceService.addProgramInstance( programInstance );

        ProgramStageInstance programStageInstance = new ProgramStageInstance();

        programStageInstance.setProgramInstance( programInstance );

        programStageInstance.setProgramStage( programStage );

        programStageInstance.setDueDate( new Date() );

        programStageInstance.setExecutionDate( new Date() );

        programStageInstance.setCompleted( true );

        programStageInstance.setOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );

        programStageInstanceService.addProgramStageInstance( programStageInstance );

        for ( org.hisp.dhis.api.mobile.model.LWUITmodel.ProgramStageDataElement mobileDataElement : mobileProgramStage
            .getDataElements() )
        {

            TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();

            trackedEntityDataValue.setDataElement( dataElementService.getDataElement( mobileDataElement.getId() ) );

            String value = mobileDataElement.getValue();

            if ( value != null && !value.trim().equals( "" ) )
            {

                trackedEntityDataValue.setValue( value );

                trackedEntityDataValue.setProgramStageInstance( programStageInstance );

                trackedEntityDataValue.setProvidedElsewhere( false );

                trackedEntityDataValue.setTimestamp( new Date() );

                dataValueService.saveTrackedEntityDataValue( trackedEntityDataValue );
            }

        }
        return SINGLE_EVENT_WITHOUT_REGISTRATION_UPLOADED;
    }

    private org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile;

    public org.hisp.dhis.api.mobile.model.LWUITmodel.Patient getPatientMobile()
    {
        return patientMobile;
    }

    public void setPatientMobile( org.hisp.dhis.api.mobile.model.LWUITmodel.Patient patientMobile )
    {
        this.patientMobile = patientMobile;
    }

    @Override
    public String findVisitSchedule( int orgUnitId, int programId, String info )
        throws NotAllowedException
    {
        String status = info.substring( 0, info.indexOf( "$" ) );
        String fromDays = info.substring( info.indexOf( "$" ) + 1, info.indexOf( "/" ) );
        String toDays = info.substring( info.indexOf( "/" ) + 1 );

        // Event Status
        EventStatus eventStatus = null;

        if ( status.equals( "Schedule in future" ) )
        {
            eventStatus = EventStatus.FUTURE_VISIT;
        }
        else if ( status.equals( "Overdue" ) )
        {
            eventStatus = EventStatus.LATE_VISIT;
        }
        else if ( status.equals( "Incomplete" ) )
        {
            eventStatus = EventStatus.VISITED;
        }
        else if ( status.equals( "Completed" ) )
        {
            eventStatus = EventStatus.COMPLETED;
        }
        else if ( status.equals( "Skipped" ) )
        {
            eventStatus = EventStatus.SKIPPED;
        }

        // From/To Date
        Date fromDate = getDate( -1, fromDays );
        Date toDate = getDate( 1, toDays );

        TrackedEntityInstanceQueryParams param = new TrackedEntityInstanceQueryParams();
        List<TrackedEntityAttribute> trackedEntityAttributeList = new ArrayList<TrackedEntityAttribute>(
            attributeService.getTrackedEntityAttributesByDisplayOnVisitSchedule( true ) );

        for ( TrackedEntityAttribute trackedEntityAttribute : trackedEntityAttributeList )
        {
            QueryItem queryItem = new QueryItem( trackedEntityAttribute );
            param.addAttribute( queryItem );
        }

        param.setProgram( programService.getProgram( programId ) );
        param.addOrganisationUnit( organisationUnitService.getOrganisationUnit( orgUnitId ) );
        param.setEventStatus( eventStatus );
        param.setEventStartDate( fromDate );
        param.setEventEndDate( toDate );

        Grid programStageInstanceGrid = entityInstanceService.getTrackedEntityInstances( param );
        List<List<Object>> listOfListProgramStageInstance = programStageInstanceGrid.getRows();

        if ( listOfListProgramStageInstance.size() == 0 )
        {
            throw NotAllowedException.NO_EVENT_FOUND;
        }

        String eventsInfo = "";
        for ( List<Object> row : listOfListProgramStageInstance )
        {
            TrackedEntityInstance instance = entityInstanceService.getTrackedEntityInstance( (String)row.get(0) );
            Collection<TrackedEntityAttribute> displayAttributes = attributeService.getTrackedEntityAttributesDisplayInList();
            
            eventsInfo += instance.getId() + "/";
            String displayName = "";
            for ( TrackedEntityAttribute displayAttribute : displayAttributes )
            {
                TrackedEntityAttributeValue value = attValueService.getTrackedEntityAttributeValue( instance, displayAttribute );
                if ( value != null )
                {
                    displayName += value.getValue() + " ";
                }
            }
            eventsInfo += displayName.trim() + "$";
        }

        return eventsInfo;
    }

    public Date getDate( int operation, String adjustment )
    {
        Calendar calendar = Calendar.getInstance();

        if ( adjustment.equals( "1 day" ) )
        {
            calendar.add( Calendar.DATE, operation );
        }
        else if ( adjustment.equals( "3 days" ) )
        {
            calendar.add( Calendar.DATE, operation * 3 );
        }
        else if ( adjustment.equals( "1 week" ) )
        {
            calendar.add( Calendar.DATE, operation * 7 );
        }
        else if ( adjustment.equals( "1 month"   ))
        {
            calendar.add( Calendar.DATE, operation * 30 );
        }
        return calendar.getTime();
    }

	public TrackedEntityService getTrackedEntityService() {
		return trackedEntityService;
	}

	public void setTrackedEntityService(TrackedEntityService trackedEntityService) {
		this.trackedEntityService = trackedEntityService;
	}
    
    
}
