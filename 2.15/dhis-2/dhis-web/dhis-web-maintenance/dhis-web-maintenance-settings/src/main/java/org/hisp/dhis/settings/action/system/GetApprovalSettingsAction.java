package org.hisp.dhis.settings.action.system;
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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.setting.SystemSettingManager;

import static org.hisp.dhis.setting.SystemSettingManager.*;

import java.util.List;

/**
 * @author Jim Grace
 */
public class GetApprovalSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private DataApprovalLevelService dataApprovalLevelService;

    public void setDataApprovalLevelService( DataApprovalLevelService dataApprovalLevelService )
    {
        this.dataApprovalLevelService = dataApprovalLevelService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private boolean keyHideUnapprovedDataInAnalytics;

    public boolean getKeyHideUnapprovedDataInAnalytics()
    {
        return keyHideUnapprovedDataInAnalytics;
    }

    private List<DataApprovalLevel> dataApprovalLevels;

    public List<DataApprovalLevel> getDataApprovalLevels()
    {
        return dataApprovalLevels;
    }

    private DataApprovalLevelService approvalLevelService;

    public DataApprovalLevelService getApprovalLevelService()
    {
        return approvalLevelService;
    }

    private boolean categoryOptionGroupSetsPresent;

    public boolean isCategoryOptionGroupSetsPresent()
    {
        return categoryOptionGroupSetsPresent;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        keyHideUnapprovedDataInAnalytics = (Boolean) systemSettingManager.getSystemSetting( KEY_HIDE_UNAPPROVED_DATA_IN_ANALYTICS, false );

        dataApprovalLevels = dataApprovalLevelService.getAllDataApprovalLevels();

        categoryOptionGroupSetsPresent = false;

        for ( DataApprovalLevel level : dataApprovalLevels )
        {
            if ( level.getCategoryOptionGroupSet() != null )
            {
                categoryOptionGroupSetsPresent = true;
                break;
            }
        }

        approvalLevelService = dataApprovalLevelService;

        return SUCCESS;
    }
}
