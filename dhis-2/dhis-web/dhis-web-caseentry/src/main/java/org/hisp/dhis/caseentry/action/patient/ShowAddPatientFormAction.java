package org.hisp.dhis.caseentry.action.patient;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientRegistrationForm;
import org.hisp.dhis.patient.PatientRegistrationFormService;
import org.hisp.dhis.patient.comparator.PatientAttributeGroupSortOrderComparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ShowAddPatientFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientRegistrationFormService patientRegistrationFormService;

    public void setPatientRegistrationFormService( PatientRegistrationFormService patientRegistrationFormService )
    {
        this.patientRegistrationFormService = patientRegistrationFormService;
    }

    private PatientAttributeService attributeService;

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private PatientAttributeGroupService attributeGroupService;

    public void setAttributeGroupService( PatientAttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    public Integer getPatientId()
    {
        return patientId;
    }

    private String patientUid;

    public void setPatientUid( String patientUid )
    {
        this.patientUid = patientUid;
    }

    public String getPatientUid()
    {
        return patientUid;
    }

    private Integer relatedProgramId;

    public void setRelatedProgramId( Integer relatedProgramId )
    {
        this.relatedProgramId = relatedProgramId;
    }

    private Collection<User> healthWorkers;

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    private Collection<PatientIdentifierType> identifierTypes;

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private List<PatientAttribute> attributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getAttributes()
    {
        return attributes;
    }

    private Map<String, List<PatientAttribute>> attributesMap = new HashMap<String, List<PatientAttribute>>();

    public Map<String, List<PatientAttribute>> getAttributesMap()
    {
        return attributesMap;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Map<Integer, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<Integer, Collection<PatientAttribute>>();

    public Map<Integer, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }

    private String customRegistrationForm;

    public String getCustomRegistrationForm()
    {
        return customRegistrationForm;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private List<PatientAttributeGroup> attributeGroups;

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    private String orgunitCountIdentifier;

    public String getOrgunitCountIdentifier()
    {
        return orgunitCountIdentifier;
    }

    private PatientRegistrationForm patientRegistrationForm;

    public PatientRegistrationForm getPatientRegistrationForm()
    {
        return patientRegistrationForm;
    }

    private Program relatedProgram;

    public Program getRelatedProgram()
    {
        return relatedProgram;
    }

    private boolean related;

    public void setRelated( boolean related )
    {
        this.related = related;
    }

    private Collection<RelationshipType> relationshipTypes;

    public Collection<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        healthWorkers = organisationUnit.getUsers();

        if ( programId == null )
        {
            patientRegistrationForm = patientRegistrationFormService.getCommonPatientRegistrationForm();

            if ( patientRegistrationForm != null && patientRegistrationForm.getDataEntryForm() != null )
            {
                customRegistrationForm = patientRegistrationFormService.prepareDataEntryFormForAdd(
                    patientRegistrationForm.getDataEntryForm().getHtmlCode(), patientRegistrationForm.getProgram(),
                    healthWorkers, null, null, i18n, format );
            }
        }
        else
        {
            program = programService.getProgram( programId );
            patientRegistrationForm = patientRegistrationFormService.getPatientRegistrationForm( program );

            if ( patientRegistrationForm != null && patientRegistrationForm.getDataEntryForm() != null )
            {
                customRegistrationForm = patientRegistrationFormService.prepareDataEntryFormForAdd(
                    patientRegistrationForm.getDataEntryForm().getHtmlCode(), patientRegistrationForm.getProgram(),
                    healthWorkers, null, null, i18n, format );
            }
        }

        if ( customRegistrationForm == null )
        {
            attributeGroups = new ArrayList<PatientAttributeGroup>(
                attributeGroupService.getAllPatientAttributeGroups() );
            Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );

            if ( program == null )
            {
                identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
                attributes = new ArrayList<PatientAttribute>( attributeService.getAllPatientAttributes() );
                Collection<Program> programs = programService.getAllPrograms();
                
                for ( Program p : programs )
                {
                    identifierTypes.removeAll( p.getIdentifierTypes() );
                    attributes.removeAll( p.getAttributes() );
                }
            }
            else
            {
                identifierTypes = program.getIdentifierTypes();
                attributes = program.getAttributes();
            }

            for ( PatientAttribute attribute : attributes )
            {
                PatientAttributeGroup patientAttributeGroup = attribute.getPatientAttributeGroup();
                String groupName = (patientAttributeGroup == null) ? "" : patientAttributeGroup.getDisplayName();
                if ( attributesMap.containsKey( groupName ) )
                {
                    List<PatientAttribute> attrs = attributesMap.get( groupName );
                    attrs.add( attribute );
                }
                else
                {
                    List<PatientAttribute> attrs = new ArrayList<PatientAttribute>();
                    attrs.add( attribute );
                    attributesMap.put( groupName, attrs );
                }
            }

        }

        if ( relatedProgramId != null )
        {
            relatedProgram = programService.getProgram( relatedProgramId );
        }

        if ( related )
        {
            relationshipTypes = relationshipTypeService.getAllRelationshipTypes();
        }

        return SUCCESS;
    }

}
