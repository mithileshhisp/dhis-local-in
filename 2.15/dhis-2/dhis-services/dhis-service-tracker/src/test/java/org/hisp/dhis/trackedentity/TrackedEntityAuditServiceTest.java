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
 * @version $ TrackedEntityAuditServiceTest.java Nov 6, 2013 8:52:24 AM $
 */
public class TrackedEntityAuditServiceTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityAuditService auditService;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private TrackedEntityAudit auditA;

    private TrackedEntityAudit auditB;

    private TrackedEntityInstance entityInstanceA;

    private TrackedEntityInstance entityInstanceB;
    
    private int entityInstanceAId;
    
    private Date today;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        entityInstanceA = createTrackedEntityInstance( 'A', organisationUnit );
        entityInstanceAId = entityInstanceService.addTrackedEntityInstance( entityInstanceA );

        entityInstanceB = createTrackedEntityInstance( 'B', organisationUnit );
        entityInstanceService.addTrackedEntityInstance( entityInstanceB );

        Calendar cal = Calendar.getInstance();
        PeriodType.clearTimeOfDay( cal );
        today = cal.getTime();

        auditA = new TrackedEntityAudit( entityInstanceA, "test", today, TrackedEntityAudit.MODULE_ENTITY_INSTANCE_DASHBOARD );
        auditB = new TrackedEntityAudit( entityInstanceB, "test", today, TrackedEntityAudit.MODULE_ENTITY_INSTANCE_DASHBOARD );
    }

    @Test
    public void testSaveTrackedEntityAudit()
    {
        int idA = auditService.saveTrackedEntityAudit( auditA );
        int idB = auditService.saveTrackedEntityAudit( auditB );

        assertNotNull( auditService.getTrackedEntityAudit( idA ) );
        assertNotNull( auditService.getTrackedEntityAudit( idB ) );
    }

    @Test
    public void testDeleteTrackedEntityAudit()
    {
        int idA = auditService.saveTrackedEntityAudit( auditA );
        int idB = auditService.saveTrackedEntityAudit( auditB );

        assertNotNull( auditService.getTrackedEntityAudit( idA ) );
        assertNotNull( auditService.getTrackedEntityAudit( idB ) );

        auditService.deleteTrackedEntityAudit( auditA );

        assertNull( auditService.getTrackedEntityAudit( idA ) );
        assertNotNull( auditService.getTrackedEntityAudit( idB ) );

        auditService.deleteTrackedEntityAudit( auditB );

        assertNull( auditService.getTrackedEntityAudit( idA ) );
        assertNull( auditService.getTrackedEntityAudit( idB ) );
    }

    @Test
    public void testGetTrackedEntityAuditById()
    {
        int idA = auditService.saveTrackedEntityAudit( auditA );
        int idB = auditService.saveTrackedEntityAudit( auditB );

        assertEquals( auditA, auditService.getTrackedEntityAudit( idA ) );
        assertEquals( auditB, auditService.getTrackedEntityAudit( idB ) );
    }

    @Test
    public void testGetAllTrackedEntityAudit()
    {
        auditService.saveTrackedEntityAudit( auditA );
        auditService.saveTrackedEntityAudit( auditB );

        assertTrue( equals( auditService.getAllTrackedEntityAudit(), auditA, auditB ) );
    }

    @Test
    public void testGetTrackedEntityAuditsByEntityInstance()
    {
        auditService.saveTrackedEntityAudit( auditA );
        auditService.saveTrackedEntityAudit( auditB );

        Collection<TrackedEntityAudit> audits = auditService.getTrackedEntityAudits( entityInstanceA );
        assertEquals( 1, audits.size() );
        assertTrue( audits.contains( auditA ) );

        audits = auditService.getTrackedEntityAudits( entityInstanceB );
        assertEquals( 1, audits.size() );
        assertTrue( audits.contains( auditB ) );
    }

    @Test
    public void testGetTrackedEntityAuditByModule()
    {
        auditService.saveTrackedEntityAudit( auditA );
        auditService.saveTrackedEntityAudit( auditB );

        TrackedEntityAudit audit = auditService.getTrackedEntityAudit( entityInstanceAId, "test", today,
            TrackedEntityAudit.MODULE_ENTITY_INSTANCE_DASHBOARD );
        assertEquals( auditA, audit );

    }
}
