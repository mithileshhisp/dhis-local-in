/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
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

package org.hisp.dhis.patient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ PatientAuditServiceTest.java Nov 6, 2013 8:52:24 AM $
 */
public class PatientAuditServiceTest
    extends DhisSpringTest
{
    @Autowired
    private PatientAuditService patientAuditService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private PatientAudit patientAuditA;

    private PatientAudit patientAuditB;

    private PatientAudit patientAuditC;

    private Patient patientA;

    private Patient patientB;
    
    private int patientAId;
    
    private Date today;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        patientA = createPatient( 'A', organisationUnit );
        patientAId = patientService.savePatient( patientA );

        patientB = createPatient( 'B', organisationUnit );
        patientService.savePatient( patientB );

        Calendar cal = Calendar.getInstance();
        PeriodType.clearTimeOfDay( cal );
        today = cal.getTime();

        patientAuditA = new PatientAudit( patientA, "test", today, PatientAudit.MODULE_PATIENT_DASHBOARD );
        patientAuditB = new PatientAudit( patientA, "test", today, PatientAudit.MODULE_TABULAR_REPORT );
        patientAuditC = new PatientAudit( patientB, "test", today, PatientAudit.MODULE_PATIENT_DASHBOARD );
    }

    @Test
    public void testSavePatientAudit()
    {
        int idA = patientAuditService.savePatientAudit( patientAuditA );
        int idB = patientAuditService.savePatientAudit( patientAuditB );

        assertNotNull( patientAuditService.getPatientAudit( idA ) );
        assertNotNull( patientAuditService.getPatientAudit( idB ) );
    }

    @Test
    public void testDeletePatientAudit()
    {
        int idA = patientAuditService.savePatientAudit( patientAuditA );
        int idB = patientAuditService.savePatientAudit( patientAuditB );

        assertNotNull( patientAuditService.getPatientAudit( idA ) );
        assertNotNull( patientAuditService.getPatientAudit( idB ) );

        patientAuditService.deletePatientAudit( patientAuditA );

        assertNull( patientAuditService.getPatientAudit( idA ) );
        assertNotNull( patientAuditService.getPatientAudit( idB ) );

        patientAuditService.deletePatientAudit( patientAuditB );

        assertNull( patientAuditService.getPatientAudit( idA ) );
        assertNull( patientAuditService.getPatientAudit( idB ) );
    }

    @Test
    public void testGetPatientAuditById()
    {
        int idA = patientAuditService.savePatientAudit( patientAuditA );
        int idB = patientAuditService.savePatientAudit( patientAuditB );

        assertEquals( patientAuditA, patientAuditService.getPatientAudit( idA ) );
        assertEquals( patientAuditB, patientAuditService.getPatientAudit( idB ) );
    }

    @Test
    public void testGetAllPatientAudit()
    {
        patientAuditService.savePatientAudit( patientAuditA );
        patientAuditService.savePatientAudit( patientAuditB );

        assertTrue( equals( patientAuditService.getAllPatientAudit(), patientAuditA, patientAuditB ) );
    }

    @Test
    public void testGetPatientAuditsByPatient()
    {
        patientAuditService.savePatientAudit( patientAuditA );
        patientAuditService.savePatientAudit( patientAuditB );
        patientAuditService.savePatientAudit( patientAuditC );

        Collection<PatientAudit> patientAudits = patientAuditService.getPatientAudits( patientA );
        assertEquals( 2, patientAudits.size() );
        assertTrue( patientAudits.contains( patientAuditA ) );
        assertTrue( patientAudits.contains( patientAuditB ) );

        patientAudits = patientAuditService.getPatientAudits( patientB );
        assertEquals( 1, patientAudits.size() );
        assertTrue( patientAudits.contains( patientAuditC ) );
    }

    @Test
    public void testGetPatientAuditByModule()
    {
        patientAuditService.savePatientAudit( patientAuditA );
        patientAuditService.savePatientAudit( patientAuditB );

        PatientAudit patientAudit = patientAuditService.getPatientAudit( patientAId, "test", today,
            PatientAudit.MODULE_PATIENT_DASHBOARD );
        assertEquals( patientAuditA, patientAudit );

    }
}
