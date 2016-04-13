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

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Abyot Asalefew Gizaw
 */
@JacksonXmlRootElement( localName = "trackedEntityInstance", namespace = DxfNamespaces.DXF_2_0 )
public class TrackedEntityInstance
    extends BaseIdentifiableObject
{
    public static String SEARCH_SAPERATE = "_";
    public static String PREFIX_TRACKED_ENTITY_ATTRIBUTE = "attr";
    public static String PREFIX_PROGRAM = "prg";
    public static String PREFIX_PROGRAM_INSTANCE = "pi";
    public static String PREFIX_PROGRAM_EVENT_BY_STATUS = "stat";
    public static String PREFIX_PROGRAM_STAGE = "prgst";

    private Set<TrackedEntityAttributeValue> attributeValues = new HashSet<TrackedEntityAttributeValue>();

    private Set<ProgramInstance> programInstances = new HashSet<ProgramInstance>();

    private OrganisationUnit organisationUnit;

    private TrackedEntityInstance representative;

    private TrackedEntity trackedEntity;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TrackedEntityInstance()
    {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public void addAttributeValue( TrackedEntityAttributeValue attributeValue )
    {
        attributeValues.add( attributeValue );
        attributeValue.setEntityInstance( this );
    }

    public void removeAttributeValue( TrackedEntityAttributeValue attributeValue )
    {
        attributeValues.remove( attributeValue );
        attributeValue.setEntityInstance( null );
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public Set<TrackedEntityAttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<TrackedEntityAttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    public Set<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    public void setProgramInstances( Set<ProgramInstance> programInstances )
    {
        this.programInstances = programInstances;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntityInstance getRepresentative()
    {
        return representative;
    }

    public void setRepresentative( TrackedEntityInstance representative )
    {
        this.representative = representative;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "trackedEntity", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "trackedEntity", namespace = DxfNamespaces.DXF_2_0 )
    public TrackedEntity getTrackedEntity()
    {
        return trackedEntity;
    }

    public void setTrackedEntity( TrackedEntity trackedEntity )
    {
        this.trackedEntity = trackedEntity;
    }
}
