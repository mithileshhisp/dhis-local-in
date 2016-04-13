package org.hisp.dhis.trackedentityattributevalue;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityAttributeValueServiceTest.java Nov 11, 2013 9:45:10 AM $
 */
public class TrackedEntityAttributeValueServiceTest
    extends DhisSpringTest
{

    @Autowired
    private TrackedEntityAttributeValueService attributeValueService;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    private TrackedEntityAttribute attributeA;

    private TrackedEntityAttribute attributeB;

    private TrackedEntityAttribute attributeC;

    private TrackedEntityInstance entityInstanceA;

    private TrackedEntityInstance entityInstanceB;

    private TrackedEntityInstance entityInstanceC;

    private TrackedEntityInstance entityInstanceD;

    private int entityInstanceAId;

    private int entityInstanceBId;

    private TrackedEntityAttributeValue attributeValueA;

    private TrackedEntityAttributeValue attributeValueB;

    private TrackedEntityAttributeValue attributeValueC;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        entityInstanceA = createTrackedEntityInstance( 'A', organisationUnit );
        entityInstanceB = createTrackedEntityInstance( 'B', organisationUnit );
        entityInstanceC = createTrackedEntityInstance( 'C', organisationUnit );
        entityInstanceD = createTrackedEntityInstance( 'D', organisationUnit );

        entityInstanceAId = entityInstanceService.addTrackedEntityInstance( entityInstanceA );
        entityInstanceBId = entityInstanceService.addTrackedEntityInstance( entityInstanceB );
        entityInstanceService.addTrackedEntityInstance( entityInstanceC );
        entityInstanceService.addTrackedEntityInstance( entityInstanceD );

        attributeA = createTrackedEntityAttribute( 'A' );
        attributeB = createTrackedEntityAttribute( 'B' );
        attributeC = createTrackedEntityAttribute( 'C' );

        attributeService.addTrackedEntityAttribute( attributeA );
        attributeService.addTrackedEntityAttribute( attributeB );
        attributeService.addTrackedEntityAttribute( attributeC );

        attributeValueA = new TrackedEntityAttributeValue( attributeA, entityInstanceA, "A" );
        attributeValueB = new TrackedEntityAttributeValue( attributeB, entityInstanceA, "B" );
        attributeValueC = new TrackedEntityAttributeValue( attributeA, entityInstanceB, "C" );
    }

    @Test
    public void testSaveTrackedEntityAttributeValue()
    {

        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );

        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
    }

    @Test
    public void testUpdateTrackedEntityAttributeValue()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );

        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );

        attributeValueA.setValue( "B" );
        attributeValueService.updateTrackedEntityAttributeValue( attributeValueA );

        assertEquals( "B", attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ).getValue() );
    }

    @Test
    public void testDeleteTrackedEntityAttributeValue()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );

        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );

        attributeValueService.deleteTrackedEntityAttributeValue( attributeValueA );

        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );

        attributeValueService.deleteTrackedEntityAttributeValue( attributeValueB );

        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
    }

    @Test
    public void testDeleteTrackedEntityAttributeValueByEntityInstance()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );

        attributeValueService.deleteTrackedEntityAttributeValue( entityInstanceA );

        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );

        attributeValueService.deleteTrackedEntityAttributeValue( entityInstanceB );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );
    }

    @Test
    public void testDeleteTrackedEntityAttributeValueByAttribute()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );

        attributeValueService.deleteTrackedEntityAttributeValue( attributeA );

        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );
        assertNotNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );

        attributeValueService.deleteTrackedEntityAttributeValue( attributeB );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeB ) );
        assertNull( attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );

    }

    @Test
    public void testGetTrackedEntityAttributeValue()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        assertEquals( attributeValueA, attributeValueService.getTrackedEntityAttributeValue( entityInstanceA, attributeA ) );
        assertEquals( attributeValueC, attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA ) );
    }

    @Test
    public void testGetTrackedEntityAttributeValuesByEntityInstance()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService.getTrackedEntityAttributeValues( entityInstanceA );

        assertEquals( 2, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB ) );

        attributeValues = attributeValueService.getTrackedEntityAttributeValues( entityInstanceB );

        assertEquals( 1, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueC ) );
    }

    @Test
    public void testGetTrackedEntityAttributeValuesbyAttribute()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService
            .getTrackedEntityAttributeValues( attributeA );
        assertEquals( 2, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueA ) );
        assertTrue( attributeValues.contains( attributeValueC ) );

        attributeValues = attributeValueService.getTrackedEntityAttributeValues( attributeB );
        assertEquals( 1, attributeValues.size() );
        assertTrue( attributeValues.contains( attributeValueB ) );
    }

    @Test
    public void testGetTrackedEntityAttributeValuesbyEntityInstanceList()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityInstance> entityInstances = new HashSet<TrackedEntityInstance>();
        entityInstances.add( entityInstanceA );
        entityInstances.add( entityInstanceB );

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService.getTrackedEntityAttributeValues( entityInstances );
        assertEquals( 3, attributeValues.size() );
        assertTrue( equals( attributeValues, attributeValueA, attributeValueB, attributeValueC ) );
    }

    @Test
    public void testGetAllTrackedEntityAttributeValues()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        assertTrue( equals( attributeValueService.getAllTrackedEntityAttributeValues(), attributeValueA, attributeValueB,
            attributeValueC ) );
    }

    @Test
    public void testGetTrackedEntityAttributeValueMapForEntityInstances()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityInstance> entityInstances = new HashSet<TrackedEntityInstance>();
        entityInstances.add( entityInstanceA );
        entityInstances.add( entityInstanceB );

        Map<Integer, Collection<TrackedEntityAttributeValue>> attributeValueMap = attributeValueService
            .getAttributeValueMapForAttributeValues( entityInstances );

        assertEquals( 2, attributeValueMap.keySet().size() );
        assertTrue( equals( attributeValueMap.get( entityInstanceAId ), attributeValueA, attributeValueB ) );
        assertTrue( equals( attributeValueMap.get( entityInstanceBId ), attributeValueC ) );
    }

    @Test
    public void testGetTrackedEntityAttributeValueMapForEntityInstancesAttributes()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityInstance> entityInstances = new HashSet<TrackedEntityInstance>();
        entityInstances.add( entityInstanceA );
        entityInstances.add( entityInstanceB );

        Map<Integer, TrackedEntityAttributeValue> attributeValueMap = attributeValueService
            .getAttributeValueMapForAttributeValues( entityInstances, attributeA );

        assertEquals( 2, attributeValueMap.keySet().size() );
        assertEquals( attributeValueA, attributeValueMap.get( entityInstanceAId ) );
        assertEquals( attributeValueC, attributeValueMap.get( entityInstanceBId ) );
    }

    @Test
    public void testSearchTrackedEntityAttributeValue()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService.searchTrackedEntityAttributeValue(
            attributeA, "A" );
        assertTrue( equals( attributeValues, attributeValueA ) );
    }

    @Test
    public void testCopyTrackedEntityAttributeValues()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        attributeValueService.copyTrackedEntityAttributeValues( entityInstanceA, entityInstanceB );

        TrackedEntityAttributeValue attributeValue = attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeB );
        assertEquals( "B", attributeValue.getValue() );

        attributeValue = attributeValueService.getTrackedEntityAttributeValue( entityInstanceB, attributeA );
        assertNull( attributeValue );
    }

    @Test
    public void testGetEntityInstances()
    {
        attributeValueService.addTrackedEntityAttributeValue( attributeValueA );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueB );
        attributeValueService.addTrackedEntityAttributeValue( attributeValueC );

        Collection<TrackedEntityInstance> entityInstances = attributeValueService.getTrackedEntityInstance( attributeA, "A" );
        assertEquals( 1, entityInstances.size() );
        assertTrue( entityInstances.contains( entityInstanceA ) );
    }

}
