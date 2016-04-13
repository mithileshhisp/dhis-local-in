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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityAttributeServiceTest.java Nov 5, 2013 3:42:42 PM $
 */
public class TrackedEntityAttributeServiceTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityAttributeStore attributeStore;

    @Autowired
    private TrackedEntityAttributeGroupService attributeGroupService;

    private TrackedEntityAttribute attributeA;

    private TrackedEntityAttribute attributeB;

    private TrackedEntityAttribute attributeC;

    private TrackedEntityAttributeGroup attributeGroup;

    @Override
    public void setUpTest()
    {
        attributeA = createTrackedEntityAttribute( 'A' );
        attributeB = createTrackedEntityAttribute( 'B' );
        attributeC = createTrackedEntityAttribute( 'C', TrackedEntityAttribute.TYPE_NUMBER );

        List<TrackedEntityAttribute> attributesA = new ArrayList<TrackedEntityAttribute>();
        attributesA.add( attributeA );
        attributesA.add( attributeB );
        attributeGroup = createTrackedEntityAttributeGroup( 'A', attributesA );
    }

    @Test
    public void testGetTrackedEntityAttributesByValueType()
    {
        attributeStore.save( attributeA );
        attributeStore.save( attributeB );
        attributeStore.save( attributeC );

        Collection<TrackedEntityAttribute> attributes = attributeStore.getByValueType( TrackedEntityAttribute.TYPE_STRING );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeStore.getByValueType( TrackedEntityAttribute.TYPE_NUMBER );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );

    }

    @Test
    public void testGetTrackedEntityAttributesWithoutGroup()
    {
        attributeStore.save( attributeA );
        attributeStore.save( attributeB );
        attributeStore.save( attributeC );

        attributeGroupService.addTrackedEntityAttributeGroup( attributeGroup );

        Collection<TrackedEntityAttribute> attributes = attributeStore.getOptionalAttributesWithoutGroup();
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

    @Test
    public void testGetTrackedEntityAttributesByDisplayOnVisitSchedule()
    {
        attributeA.setDisplayOnVisitSchedule( true );
        attributeB.setDisplayOnVisitSchedule( true );
        attributeC.setDisplayOnVisitSchedule( false );

        attributeStore.save( attributeA );
        attributeStore.save( attributeB );
        attributeStore.save( attributeC );

        Collection<TrackedEntityAttribute> attributes = attributeStore.getByDisplayOnVisitSchedule( true );
        assertEquals( 2, attributes.size() );
        assertTrue( attributes.contains( attributeA ) );
        assertTrue( attributes.contains( attributeB ) );

        attributes = attributeStore.getByDisplayOnVisitSchedule( false );
        assertEquals( 1, attributes.size() );
        assertTrue( attributes.contains( attributeC ) );
    }

}