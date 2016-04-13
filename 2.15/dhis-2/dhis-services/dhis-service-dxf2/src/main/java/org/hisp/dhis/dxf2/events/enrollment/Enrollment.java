package org.hisp.dhis.dxf2.events.enrollment;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dxf2.events.trackedentity.Attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@JacksonXmlRootElement( localName = "enrollment", namespace = DxfNamespaces.DXF_2_0 )
public class Enrollment
{
    private String enrollment;

    private String trackedEntityInstance;

    private String program;

    private EnrollmentStatus status;

    private Date dateOfEnrollment;

    private Date dateOfIncident;

    private List<Attribute> attributes = new ArrayList<Attribute>();

    public Enrollment()
    {
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance( String trackedEntityInstance )
    {
        this.trackedEntityInstance = trackedEntityInstance;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getEnrollment()
    {
        return enrollment;
    }

    public void setEnrollment( String enrollment )
    {
        this.enrollment = enrollment;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( isAttribute = true )
    public String getProgram()
    {
        return program;
    }

    public void setProgram( String program )
    {
        this.program = program;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public EnrollmentStatus getStatus()
    {
        return status;
    }

    public void setStatus( EnrollmentStatus status )
    {
        this.status = status;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getDateOfEnrollment()
    {
        return dateOfEnrollment;
    }

    public void setDateOfEnrollment( Date dateOfEnrollment )
    {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    @JsonProperty( required = true )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getDateOfIncident()
    {
        return dateOfIncident;
    }

    public void setDateOfIncident( Date dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public List<Attribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( List<Attribute> attributes )
    {
        this.attributes = attributes;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Enrollment that = (Enrollment) o;

        if ( attributes != null ? !attributes.equals( that.attributes ) : that.attributes != null ) return false;
        if ( dateOfEnrollment != null ? !dateOfEnrollment.equals( that.dateOfEnrollment ) : that.dateOfEnrollment != null ) return false;
        if ( dateOfIncident != null ? !dateOfIncident.equals( that.dateOfIncident ) : that.dateOfIncident != null ) return false;
        if ( enrollment != null ? !enrollment.equals( that.enrollment ) : that.enrollment != null ) return false;
        if ( trackedEntityInstance != null ? !trackedEntityInstance.equals( that.trackedEntityInstance ) : that.trackedEntityInstance != null )
            return false;
        if ( program != null ? !program.equals( that.program ) : that.program != null ) return false;
        if ( status != that.status ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = enrollment != null ? enrollment.hashCode() : 0;
        result = 31 * result + (trackedEntityInstance != null ? trackedEntityInstance.hashCode() : 0);
        result = 31 * result + (program != null ? program.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (dateOfEnrollment != null ? dateOfEnrollment.hashCode() : 0);
        result = 31 * result + (dateOfIncident != null ? dateOfIncident.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Enrollment{" +
            "enrollment='" + enrollment + '\'' +
            ", trackedEntityInstance='" + trackedEntityInstance + '\'' +
            ", program='" + program + '\'' +
            ", status=" + status +
            ", dateOfEnrollment=" + dateOfEnrollment +
            ", dateOfIncident=" + dateOfIncident +
            ", attributes=" + attributes +
            '}';
    }
}
