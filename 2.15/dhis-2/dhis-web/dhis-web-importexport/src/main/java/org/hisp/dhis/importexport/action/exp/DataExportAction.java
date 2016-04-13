package org.hisp.dhis.importexport.action.exp;

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

import static org.hisp.dhis.system.util.DateUtils.getMediumDate;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataExportAction
    implements Action
{
    private static final String FILENAME = "Export_meta.zip";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ServiceProvider<ExportService> serviceProvider;

    public void setServiceProvider( ServiceProvider<ExportService> serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService)
    {
        this.currentUserService = currentUserService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String exportFormat;

    public String getExportFormat()
    {
        return exportFormat;
    }

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }
    
    //Data
    private boolean dataValue;

    public void setDataValue( boolean dataValue )
    {
        this.dataValue = dataValue;
    }

    private boolean dataValueDaily;

    public void setDataValueDaily( boolean dataValueDaily )
    {
        this.dataValueDaily = dataValueDaily;
    }
    
    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExportParams params = new ExportParams();

        if ( dataValue || dataValueDaily  )
        {
            params.setCategories( null );
            params.setCategoryCombos( null );
            params.setCategoryOptions( null );
            params.setCategoryOptionCombos( null );
            /*
            params.setDataElementGroups( null );
            params.setDataElementGroupSets( null );
            params.setIndicators( null );
            params.setIndicatorTypes( null );
            params.setIndicatorGroups( null );
            params.setIndicatorGroupSets( null );
            params.setDataDictionaries( null );
            params.setDataSets( null );
            params.setOrganisationUnitGroups( null );
            params.setOrganisationUnitGroupSets( null );
            params.setOrganisationUnitLevels( null );
            params.setValidationRules( null );
            params.setReports( null );
            params.setReportTables( null );
            params.setPeriods( null ); // TODO Include only relevant periods
            params.setCharts( null );
            params.setPeriods( null );*/
            
            Set<Integer> dataElementz = new HashSet<Integer>();
            
            Collection<DataElement> children = dataElementService.getAllDataElements();
            
            for ( DataElement child : children )
            {
            	dataElementz.add( child.getId() );
            }
            
            params.setDataElements( dataElementz );
            
            Set<Integer> orgUnits = new HashSet<Integer>();
            
            Collection<OrganisationUnit> orgChildren = organisationUnitService.getAllOrganisationUnits();
            
            for ( OrganisationUnit child : orgChildren )
            {
            	orgUnits.add( child.getId() );
            }
            
            params.setOrganisationUnits( orgUnits );
        }

        
        if(dataValue){
        	params.setMetaData( false );
        }
        
        if(dataValueDaily){
        	params.setMetaData( false );
        }

        System.out.println(dataValue+" "+dataValueDaily+" "+startDate+" "+endDate);
        //params.setMetaData( true );
        params.setIncludeDataValues( true );
        params.setCurrentUser( currentUserService.getCurrentUser() );
        params.setDataValue(dataValue);
        params.setDataValueDaily(dataValueDaily);
        
        params.setI18n( i18n );
        params.setFormat( format );
        params.setStartDate(getMediumDate( startDate ));
        params.setEndDate(getMediumDate( endDate ));
        
        
        ExportService exportService = serviceProvider.provide( exportFormat );

        inputStream = exportService.exportData( params );

        fileName = FILENAME;

        return SUCCESS;
    }
}
