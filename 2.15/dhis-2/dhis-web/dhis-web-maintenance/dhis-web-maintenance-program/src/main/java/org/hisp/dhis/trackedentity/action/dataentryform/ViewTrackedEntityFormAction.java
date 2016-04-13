package org.hisp.dhis.trackedentity.action.dataentryform;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityForm;
import org.hisp.dhis.trackedentity.TrackedEntityFormService;
import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version ViewTrackedEntityFormAction.java 10:35:08 AM Jan 31, 2013 $
 */
public class ViewTrackedEntityFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private TrackedEntityFormService formService;

    public void setFormService( TrackedEntityFormService formService )
    {
        this.formService = formService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Collection<TrackedEntityAttribute> attributes = new HashSet<TrackedEntityAttribute>();

    public Collection<TrackedEntityAttribute> getAttributes()
    {
        return attributes;
    }

    private Collection<ProgramTrackedEntityAttribute> programAttributes = new ArrayList<ProgramTrackedEntityAttribute>();

    public Collection<ProgramTrackedEntityAttribute> getProgramAttributes()
    {
        return programAttributes;
    }

    private TrackedEntityForm registrationForm;

    public TrackedEntityForm getRegistrationForm()
    {
        return registrationForm;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private List<String> flags;

    public List<String> getFlags()
    {
        return flags;
    }

    private boolean autoSave;

    public boolean getAutoSave()
    {
        return autoSave;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        List<Program> programs = new ArrayList<Program>( programService.getAllPrograms() );

        programs.removeAll( programService.getPrograms( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );

        if ( programId == null )
        {
            registrationForm = formService.getCommonTrackedEntityForm();

            attributes = attributeService.getAllTrackedEntityAttributes();

            for ( Program program : programs )
            {
                attributes.removeAll( program.getAttributes() );
            }
        }
        else
        {
            program = programService.getProgram( programId );
            programAttributes = program.getAttributes();
            registrationForm = formService.getTrackedEntityForm( program );
        }

        // ---------------------------------------------------------------------
        // Get images
        // ---------------------------------------------------------------------

        flags = systemSettingManager.getFlags();

        autoSave = (Boolean) userSettingService.getUserSetting(
            UserSettingService.AUTO_SAVE_TRACKED_ENTITY_REGISTRATION_ENTRY_FORM, false );

        return SUCCESS;
    }
}
