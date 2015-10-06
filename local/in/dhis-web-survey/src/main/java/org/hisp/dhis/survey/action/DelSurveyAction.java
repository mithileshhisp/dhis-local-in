package org.hisp.dhis.survey.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.survey.Survey;
import org.hisp.dhis.survey.SurveyService;

import com.opensymphony.xwork2.Action;

/**
 * @author Kristian
 * @version $Id: DelDataSetAction.java 3575 2007-09-28 17:22:01Z larshelg $
 */
public class DelSurveyAction
    implements Action
{
    private int surveyId;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SurveyService surveyService;

    public void setSurveyService( SurveyService surveyService )
    {
        this.surveyService = surveyService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public void setSurveyId( int surveyId )
    {
        this.surveyId = surveyId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private String status;

    public String getStatus()
    {
        return status;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        status = "success";
        
        Survey survey = surveyService.getSurvey( surveyId );
        
        int flag = surveyService.deleteSurvey( survey );
       // System.out.println( "flag is " + flag );
        if ( flag < 0 )
        {
            status = "error";
            String delMseg = i18n.getString( "not_del_contain_data" );
            message = survey.getName() + " : "+  delMseg;

            return ERROR;
        }
        
        return SUCCESS;
    }
}
