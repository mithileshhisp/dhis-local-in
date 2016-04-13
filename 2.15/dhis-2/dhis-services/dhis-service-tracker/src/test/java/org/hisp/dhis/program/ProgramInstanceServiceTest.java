package org.hisp.dhis.program;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.sms.config.BulkSmsGatewayConfig;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsConfigurationManager;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminder;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Chau Thu Tran
 * 
 * @version $ ProgramInstanceServiceTest.java Nov 13, 2013 1:34:55 PM $
 */
public class ProgramInstanceServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private SmsConfigurationManager smsConfigurationManager;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    private Date incidenDate;

    private Date enrollmentDate;

    private Program programA;

    private Program programB;

    private Program programC;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private ProgramInstance programInstanceA;

    private ProgramInstance programInstanceB;

    private ProgramInstance programInstanceC;

    private ProgramInstance programInstanceD;

    private TrackedEntityInstance entityInstanceA;

    private Collection<Integer> orgunitIds;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void setUpTest()
    {
        organisationUnitA = createOrganisationUnit( 'A' );
        int idA = organisationUnitService.addOrganisationUnit( organisationUnitA );

        organisationUnitB = createOrganisationUnit( 'B' );
        int idB = organisationUnitService.addOrganisationUnit( organisationUnitB );

        orgunitIds = new HashSet<Integer>();
        orgunitIds.add( idA );
        orgunitIds.add( idB );

        programA = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnitA );

        TrackedEntityInstanceReminder reminderA = new TrackedEntityInstanceReminder( "A", 0,
            "Test program message template", TrackedEntityInstanceReminder.ENROLLEMENT_DATE_TO_COMPARE,
            TrackedEntityInstanceReminder.SEND_TO_TRACKED_ENTITY_INSTANCE, null, TrackedEntityInstanceReminder.MESSAGE_TYPE_BOTH );

        TrackedEntityInstanceReminder reminderB = new TrackedEntityInstanceReminder( "B", 0,
            "Test program message template", TrackedEntityInstanceReminder.ENROLLEMENT_DATE_TO_COMPARE,
            TrackedEntityInstanceReminder.SEND_TO_TRACKED_ENTITY_INSTANCE, TrackedEntityInstanceReminder.SEND_WHEN_TO_C0MPLETED_EVENT,
            TrackedEntityInstanceReminder.MESSAGE_TYPE_BOTH );

        Set<TrackedEntityInstanceReminder> reminders = new HashSet<TrackedEntityInstanceReminder>();
        reminders.add( reminderA );
        reminders.add( reminderB );
        programA.setInstanceReminders( reminders );

        programService.addProgram( programA );

        ProgramStage stageA = new ProgramStage( "StageA", programA );
        programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "StageB", programA );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        programA.setProgramStages( programStages );
        programService.updateProgram( programA );

        programB = createProgram( 'B', new HashSet<ProgramStage>(), organisationUnitA );
        programService.addProgram( programB );

        programC = createProgram( 'C', new HashSet<ProgramStage>(), organisationUnitA );
        programService.addProgram( programC );

        entityInstanceA = createTrackedEntityInstance( 'A', organisationUnitA );
        entityInstanceService.addTrackedEntityInstance( entityInstanceA );

        TrackedEntityInstance entityInstanceB = createTrackedEntityInstance( 'B', organisationUnitB );
        entityInstanceService.addTrackedEntityInstance( entityInstanceB );

        Calendar calIncident = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calIncident );
        calIncident.add( Calendar.DATE, -70 );
        incidenDate = calIncident.getTime();

        Calendar calEnrollment = Calendar.getInstance();
        PeriodType.clearTimeOfDay( calEnrollment );
        enrollmentDate = calEnrollment.getTime();

        programInstanceA = new ProgramInstance( enrollmentDate, incidenDate, entityInstanceA, programA );
        programInstanceA.setUid( "UID-A" );

        programInstanceB = new ProgramInstance( enrollmentDate, incidenDate, entityInstanceA, programB );
        programInstanceB.setUid( "UID-B" );
        programInstanceB.setStatus( ProgramInstance.STATUS_CANCELLED );

        programInstanceC = new ProgramInstance( enrollmentDate, incidenDate, entityInstanceA, programC );
        programInstanceC.setUid( "UID-C" );
        programInstanceC.setStatus( ProgramInstance.STATUS_COMPLETED );

        programInstanceD = new ProgramInstance( enrollmentDate, incidenDate, entityInstanceB, programA );
        programInstanceD.setUid( "UID-D" );
    }

    @Test
    public void testAddProgramInstance()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );
        int idB = programInstanceService.addProgramInstance( programInstanceB );

        assertNotNull( programInstanceService.getProgramInstance( idA ) );
        assertNotNull( programInstanceService.getProgramInstance( idB ) );
    }

    @Test
    public void testDeleteProgramInstance()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );
        int idB = programInstanceService.addProgramInstance( programInstanceB );

        assertNotNull( programInstanceService.getProgramInstance( idA ) );
        assertNotNull( programInstanceService.getProgramInstance( idB ) );

        programInstanceService.deleteProgramInstance( programInstanceA );

        assertNull( programInstanceService.getProgramInstance( idA ) );
        assertNotNull( programInstanceService.getProgramInstance( idB ) );

        programInstanceService.deleteProgramInstance( programInstanceB );

        assertNull( programInstanceService.getProgramInstance( idA ) );
        assertNull( programInstanceService.getProgramInstance( idB ) );

    }

    @Test
    public void testUpdateProgramInstance()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );

        assertNotNull( programInstanceService.getProgramInstance( idA ) );

        programInstanceA.setDateOfIncident( enrollmentDate );
        programInstanceService.updateProgramInstance( programInstanceA );

        assertEquals( enrollmentDate, programInstanceService.getProgramInstance( idA ).getDateOfIncident() );
    }

    @Test
    public void testGetProgramInstanceById()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );
        int idB = programInstanceService.addProgramInstance( programInstanceB );

        assertEquals( programInstanceA, programInstanceService.getProgramInstance( idA ) );
        assertEquals( programInstanceB, programInstanceService.getProgramInstance( idB ) );
    }

    @Test
    public void testGetProgramInstanceByUid()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );

        assertEquals( "UID-A", programInstanceService.getProgramInstance( "UID-A" ).getUid() );
        assertEquals( "UID-B", programInstanceService.getProgramInstance( "UID-B" ).getUid() );
    }

    @Test
    public void testGetAllProgramInstances()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );

        assertTrue( equals( programInstanceService.getAllProgramInstances(), programInstanceA, programInstanceB ) );
    }

    @Test
    public void testGetProgramInstancesByStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService
            .getProgramInstances( ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceService.getProgramInstances( ProgramInstance.STATUS_CANCELLED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceService.getProgramInstances( ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );
    }

    @Test
    public void testGetProgramInstancesByProgram()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programA );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceService.getProgramInstances( programB );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );
    }

    @Test
    public void testGetProgramInstancesByProgramList()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs );
        assertEquals( 3, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceB ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgramList()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs,
            organisationUnitA );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceService.getProgramInstances( programs, organisationUnitB );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgramListStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );
        programs.add( programC );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs,
            organisationUnitA, ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );

        programInstances = programInstanceService.getProgramInstances( programs, organisationUnitA,
            ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );

    }

    @Test
    public void testGetProgramInstancesByProgramStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );

        programInstances = programInstanceService.getProgramInstances( programB, ProgramInstance.STATUS_CANCELLED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceB ) );

        programInstances = programInstanceService.getProgramInstances( programC, ProgramInstance.STATUS_COMPLETED );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceC ) );
    }

    @Test
    public void testGetProgramInstancesByProgramListStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programs,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testGetProgramInstancesByEntityInstanceStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<Program> programs = new HashSet<Program>();
        programs.add( programA );
        programs.add( programB );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( entityInstanceA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByEntityInstanceProgram()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceD );

        ProgramInstance programInstance = programInstanceService.enrollTrackedEntityInstance( entityInstanceA, programA, enrollmentDate,
            incidenDate, organisationUnitA );
        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstanceService.updateProgramInstance( programInstance );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( entityInstanceA, programA );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstance ) );
    }

    @Test
    public void testGetProgramInstancesByEntityInstanceProgramStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );

        ProgramInstance programInstance1 = programInstanceService.enrollTrackedEntityInstance( entityInstanceA, programA, enrollmentDate,
            incidenDate, organisationUnitA );
        programInstance1.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstanceService.updateProgramInstance( programInstance1 );

        ProgramInstance programInstance2 = programInstanceService.enrollTrackedEntityInstance( entityInstanceA, programA, enrollmentDate,
            incidenDate, organisationUnitA );
        programInstance2.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstanceService.updateProgramInstance( programInstance2 );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( entityInstanceA, programA,
            ProgramInstance.STATUS_COMPLETED );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstance1 ) );
        assertTrue( programInstances.contains( programInstance2 ) );

        programInstances = programInstanceService.getProgramInstances( entityInstanceA, programA,
            ProgramInstance.STATUS_ACTIVE );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByOuProgram()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceC );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programA,
            organisationUnitA, 0, 10 );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesbyProgramOuPeriod()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programA,
            organisationUnitA, incidenDate, enrollmentDate );
        assertEquals( 1, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
    }

    @Test
    public void testGetProgramInstancesByOuListProgramPeriod()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( programA,
            orgunitIds, incidenDate, enrollmentDate, null, null );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testCountProgramInstancesByOuListProgramPeriod()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceD );

        int count = programInstanceService.countProgramInstances( programA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 2, count );
    }

    @Test
    public void testGetProgramInstancesByOuListProgramStatusPeriod()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceD );

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstancesByStatus(
            ProgramInstance.STATUS_ACTIVE, programA, orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 2, programInstances.size() );
        assertTrue( programInstances.contains( programInstanceA ) );
        assertTrue( programInstances.contains( programInstanceD ) );
    }

    @Test
    public void testCountProgramInstancesByStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceB );
        programInstanceService.addProgramInstance( programInstanceD );

        int count = programInstanceService.countProgramInstancesByStatus( ProgramInstance.STATUS_ACTIVE, programA,
            orgunitIds, incidenDate, enrollmentDate );
        assertEquals( 2, count );
    }

    @Test
    public void testSendMessages()
    {
        TrackedEntityAttribute attribute = createTrackedEntityAttribute( 'A' );
        attribute.setValueType( TrackedEntityAttribute.TYPE_PHONE_NUMBER );
        attributeService.addTrackedEntityAttribute( attribute );

        TrackedEntityAttributeValue attributeValue = createTrackedEntityAttributeValue( 'A', entityInstanceA, attribute );
        attributeValue.setValue( "123456789" );
        attributeValueService.addTrackedEntityAttributeValue( attributeValue );

        entityInstanceA.getAttributeValues().add( attributeValue );
        entityInstanceService.updateTrackedEntityInstance( entityInstanceA );

        programInstanceService.addProgramInstance( programInstanceA );
        Collection<OutboundSms> outboundSmsList = programInstanceService.sendMessages( programInstanceA,
            TrackedEntityInstanceReminder.SEND_WHEN_TO_C0MPLETED_EVENT );
        assertEquals( 1, outboundSmsList.size() );
        assertEquals( "Test program message template", outboundSmsList.iterator().next().getMessage() );
    }

    @Test
    public void testSendMessageConversations()
    {
        this.createSMSConfiguration();
        SmsConfiguration smsConfiguration = new SmsConfiguration();
        smsConfiguration.setEnabled( true );

        programInstanceService.addProgramInstance( programInstanceB );

        Collection<MessageConversation> messages = programInstanceService.sendMessageConversations( programInstanceA,
            TrackedEntityInstanceReminder.SEND_WHEN_TO_C0MPLETED_EVENT );
        assertEquals( 1, messages.size() );
        assertEquals( "Test program message template", messages.iterator().next().getMessages().get( 0 ).getText() );
    }

    @Test
    public void testEnrollTrackedEntityInstance()
    {
        ProgramInstance programInstance = programInstanceService.enrollTrackedEntityInstance( entityInstanceA, programB, enrollmentDate,
            incidenDate, organisationUnitA );

        assertNotNull( programInstanceService.getProgramInstance( programInstance.getId() ) );
    }

    @Test
    public void testCanAutoCompleteProgramInstanceStatus()
    {
        programInstanceService.addProgramInstance( programInstanceA );
        programInstanceService.addProgramInstance( programInstanceD );

        assertTrue( programInstanceService.canAutoCompleteProgramInstanceStatus( programInstanceA ) );
        assertTrue( programInstanceService.canAutoCompleteProgramInstanceStatus( programInstanceD ) );
    }

    @Test
    public void testCompleteProgramInstanceStatus()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );
        int idD = programInstanceService.addProgramInstance( programInstanceD );

        programInstanceService.completeProgramInstanceStatus( programInstanceA );
        programInstanceService.completeProgramInstanceStatus( programInstanceD );

        assertEquals( ProgramInstance.STATUS_COMPLETED, programInstanceService.getProgramInstance( idA ).getStatus() );
        assertEquals( ProgramInstance.STATUS_COMPLETED, programInstanceService.getProgramInstance( idD ).getStatus() );
    }

    @Test
    public void testCancelProgramInstanceStatus()
    {
        int idA = programInstanceService.addProgramInstance( programInstanceA );
        int idD = programInstanceService.addProgramInstance( programInstanceD );

        programInstanceService.cancelProgramInstanceStatus( programInstanceA );
        programInstanceService.cancelProgramInstanceStatus( programInstanceD );

        assertEquals( ProgramInstance.STATUS_CANCELLED, programInstanceService.getProgramInstance( idA ).getStatus() );
        assertEquals( ProgramInstance.STATUS_CANCELLED, programInstanceService.getProgramInstance( idD ).getStatus() );
    }

    private void createSMSConfiguration()
    {
        BulkSmsGatewayConfig bulkGatewayConfig = new BulkSmsGatewayConfig();
        bulkGatewayConfig.setName( "bulksms" );
        bulkGatewayConfig.setPassword( "bulk" );
        bulkGatewayConfig.setUsername( "bulk" );
        bulkGatewayConfig.setRegion( "uk" );
        bulkGatewayConfig.setDefault( true );

        SmsConfiguration smsConfig = new SmsConfiguration();
        smsConfig.setPollingInterval( 3000 );
        smsConfig.getGateways().add( bulkGatewayConfig );
        smsConfig.setEnabled( true );
        smsConfigurationManager.updateSmsConfiguration( smsConfig );

    }
}
