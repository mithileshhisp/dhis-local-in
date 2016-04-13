package org.hisp.dhis.analytics.event.data;

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

import static org.hisp.dhis.analytics.AnalyticsService.NAMES_META_KEY;
import static org.hisp.dhis.analytics.AnalyticsService.OU_HIERARCHY_KEY;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.common.NameableObjectUtils.asTypedList;
import static org.hisp.dhis.organisationunit.OrganisationUnit.getParentGraphMap;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.analytics.AnalyticsSecurityManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.SortOrder;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.QueryFilter;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.Timer;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventAnalyticsService
    implements EventAnalyticsService
{    
    private static final String ITEM_EVENT = "psi";
    private static final String ITEM_PROGRAM_STAGE = "ps";
    private static final String ITEM_EXECUTION_DATE = "eventdate";
    private static final String ITEM_LONGITUDE = "longitude";
    private static final String ITEM_LATITUDE = "latitude";
    private static final String ITEM_ORG_UNIT_NAME = "ouname";
    private static final String ITEM_ORG_UNIT_CODE = "oucode";
    private static final String COL_NAME_EVENTDATE = "executiondate";

    private static final List<String> SORTABLE_ITEMS = Arrays.asList( 
        ITEM_EXECUTION_DATE, ITEM_ORG_UNIT_NAME, ITEM_ORG_UNIT_CODE );

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private EventAnalyticsManager analyticsManager;

    @Autowired
    private AnalyticsSecurityManager securityManager;
    
    @Autowired
    private EventQueryPlanner queryPlanner;

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    // -------------------------------------------------------------------------
    // EventAnalyticsService implementation
    // -------------------------------------------------------------------------

    // TODO order event analytics tables on execution date to avoid default
    // TODO sorting in queries

    public Grid getAggregatedEventData( EventQueryParams params )
    {
        securityManager.decideAccess( params );
        
        queryPlanner.validate( params );
        
        Grid grid = new ListGrid();

        int maxLimit = queryPlanner.getMaxLimit();
        
        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( DimensionalObject dimension : params.getDimensions() )
        {
            grid.addHeader( new GridHeader( dimension.getDimension(), dimension.getDisplayName(), String.class.getName(), false, true ) );
        }

        for ( QueryItem item : params.getItems() )
        {
            grid.addHeader( new GridHeader( item.getItem().getUid(), item.getItem().getName(), item.getTypeAsString(), false, true ) );
        }

        grid.addHeader( new GridHeader( "value", "Value" ) );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        List<EventQueryParams> queries = queryPlanner.planAggregateQuery( params );

        for ( EventQueryParams query : queries )
        {
            analyticsManager.getAggregatedEventData( query, grid, maxLimit );
        }
        
        if ( grid.getHeight() > maxLimit )
        {
            throw new IllegalQueryException( "Number of rows produced by query is larger than the max limit: " + maxLimit );
        }

        // ---------------------------------------------------------------------
        // Limit and sort - done again due to potential multiple partitions
        // ---------------------------------------------------------------------

        if ( params.hasSortOrder() )
        {            
            grid.sortGrid( 1, params.getSortOrderAsInt() );
        }
        
        if ( params.hasLimit() && grid.getHeight() > params.getLimit() )
        {
            grid.limitGrid( params.getLimit() );
        }
        
        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------
        
        if ( !params.isSkipMeta() )
        {
            Map<Object, Object> metaData = new HashMap<Object, Object>();
    
            Map<String, String> uidNameMap = getUidNameMap( params );
    
            metaData.put( NAMES_META_KEY, uidNameMap );
            metaData.put( PERIOD_DIM_ID, getUids( params.getDimensionOrFilter( PERIOD_DIM_ID ) ) );
            metaData.put( ORGUNIT_DIM_ID, getUids( params.getDimensionOrFilter( ORGUNIT_DIM_ID ) ) );
    
            if ( params.isHierarchyMeta() )
            {
                metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( asTypedList( 
                    params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ) ) );
            }

            grid.setMetaData( metaData );
        }

        return grid;
    }

    public Grid getEvents( EventQueryParams params )
    {
        securityManager.decideAccess( params );
        
        queryPlanner.validate( params );

        params.replacePeriodsWithStartEndDates();
        
        Grid grid = new ListGrid();
        
        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( ITEM_EVENT, "Event", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_PROGRAM_STAGE, "Program stage", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_EXECUTION_DATE, "Event date", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_LONGITUDE, "Longitude", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_LATITUDE, "Latitude", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_NAME, "Organisation unit name", String.class.getName(), false, true ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_CODE, "Organisation unit code", String.class.getName(), false, true ) );

        for ( DimensionalObject dimension : params.getDimensions() )
        {
            grid.addHeader( new GridHeader( dimension.getDimension(), dimension.getDisplayName(), String.class.getName(), false, true ) );
        }

        for ( QueryItem item : params.getItems() )
        {
            grid.addHeader( new GridHeader( item.getItem().getUid(), item.getItem().getName(), item.getTypeAsString() ) );
        }

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();

        params = queryPlanner.planEventQuery( params );

        t.getSplitTime( "Planned query, got partitions: " + params.getPartitions() );

        int count = 0;

        if ( params.getPartitions().hasAny() )
        {
            if ( params.isPaging() )
            {
                count += analyticsManager.getEventCount( params );
            }
    
            analyticsManager.getEvents( params, grid, queryPlanner.getMaxLimit() );
    
            t.getTime( "Queried events, got: " + grid.getHeight() );
        }
        
        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();

        Map<String, String> uidNameMap = getUidNameMap( params );

        metaData.put( NAMES_META_KEY, uidNameMap );

        if ( params.isHierarchyMeta() )
        {
            metaData.put( OU_HIERARCHY_KEY, getParentGraphMap( asTypedList( 
                params.getDimensionOrFilter( ORGUNIT_DIM_ID ), OrganisationUnit.class ) ) );
        }

        if ( params.isPaging() )
        {
            Pager pager = new Pager( params.getPageWithDefault(), count, params.getPageSizeWithDefault() );
            metaData.put( AnalyticsService.PAGER_META_KEY, pager );
        }

        grid.setMetaData( metaData );

        return grid;
    }

    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate,
        Set<String> dimension, Set<String> filter, boolean skipMeta, boolean hierarchyMeta, SortOrder sortOrder, Integer limit,
        I18nFormat format )
    {
        EventQueryParams params = getFromUrl( program, stage, startDate, endDate, dimension, filter, null, null, null,
            skipMeta, hierarchyMeta, false, null, null, format );
        
        params.setSortOrder( sortOrder );
        params.setLimit( limit );
        params.setAggregate( true );

        return params;
    }

    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate,
        Set<String> dimension, Set<String> filter, String ouMode, Set<String> asc, Set<String> desc,
        boolean skipMeta, boolean hierarchyMeta, boolean coordinatesOnly, Integer page, Integer pageSize, I18nFormat format )
    {
        EventQueryParams params = new EventQueryParams();

        Date date = new Date();

        Program pr = programService.getProgram( program );

        if ( pr == null )
        {
            throw new IllegalQueryException( "Program does not exist: " + program );
        }

        ProgramStage ps = programStageService.getProgramStage( stage );

        if ( stage != null && !stage.isEmpty() && ps == null )
        {
            throw new IllegalQueryException( "Program stage is specified but does not exist: " + stage );
        }

        Date start = null;
        Date end = null;

        if ( startDate != null && endDate != null )
        {
            try
            {
                start = DateUtils.getMediumDate( startDate );
                end = DateUtils.getMediumDate( endDate );
            }
            catch ( RuntimeException ex )
            {
                throw new IllegalQueryException( "Start date or end date is invalid: " + startDate + " - " + endDate );
            }
        }

        if ( dimension != null )
        {
            for ( String dim : dimension )
            {
                String dimensionId = DimensionalObjectUtils.getDimensionFromParam( dim );

                if ( ORGUNIT_DIM_ID.equals( dimensionId ) || PERIOD_DIM_ID.equals( dimensionId ) )
                {
                    List<String> items = DimensionalObjectUtils.getDimensionItemsFromParam( dim );
                    params.getDimensions().addAll( analyticsService.getDimension( dimensionId, items, date, format ) );
                }
                else
                {
                    params.getItems().add( getQueryItem( dim, pr ) );
                }
            }
        }

        if ( filter != null )
        {
            for ( String dim : filter )
            {
                String dimensionId = DimensionalObjectUtils.getDimensionFromParam( dim );

                if ( ORGUNIT_DIM_ID.equals( dimensionId ) || PERIOD_DIM_ID.equals( dimensionId ) )
                {
                    List<String> items = DimensionalObjectUtils.getDimensionItemsFromParam( dim );
                    params.getFilters().addAll( analyticsService.getDimension( dimensionId, items, date, format ) );
                }
                else
                {
                    params.getItemFilters().add( getQueryItem( dim, pr ) );
                }
            }
        }

        if ( params.hasDimensionOrFilter( ORGUNIT_DIM_ID ) )
        {
            for ( NameableObject object : params.getDimensionOrFilter( ORGUNIT_DIM_ID ) )
            {
                OrganisationUnit unit = (OrganisationUnit) object;
                unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getUid() ) );
            }
        }

        if ( asc != null )
        {
            for ( String sort : asc )
            {
                params.getAsc().add( getSortItem( sort, pr ) );
            }
        }

        if ( desc != null )
        {
            for ( String sort : desc )
            {
                params.getDesc().add( getSortItem( sort, pr ) );
            }
        }

        params.setProgram( pr );
        params.setProgramStage( ps );
        params.setStartDate( start );
        params.setEndDate( end );
        params.setOrganisationUnitMode( ouMode );
        params.setSkipMeta( skipMeta );
        params.setHierarchyMeta( hierarchyMeta );
        params.setCoordinatesOnly( coordinatesOnly );
        params.setPage( page );
        params.setPageSize( pageSize );
        params.setAggregate( false );
        
        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private QueryItem getQueryItem( String dimension, Program program )
    {
        String[] split = dimension.split( DimensionalObjectUtils.DIMENSION_NAME_SEP );
        
        if ( split == null || ( split.length % 2 != 1 ) )
        {
            throw new IllegalQueryException( "Query item or filter is invalid: " + dimension );
        }
        
        QueryItem queryItem = getQueryItem( program, split[0] );
        
        if ( split.length > 1 )
        {   
            for ( int i = 1; i < split.length; i += 2 )
            {
                queryItem.getFilters().add( new QueryFilter( split[i], split[i+1] ) );
            }
        }

        return queryItem;
    }

    private Map<String, String> getUidNameMap( EventQueryParams params )
    {
        Map<String, String> map = new HashMap<String, String>();

        Program program = params.getProgram();
        ProgramStage stage = params.getProgramStage();

        map.put( program.getUid(), program.getName() );

        if ( stage != null )
        {
            map.put( stage.getUid(), stage.getName() );
        }
        else
        {
            for ( ProgramStage st : program.getProgramStages() )
            {
                map.put( st.getUid(), st.getName() );
            }
        }

        for ( QueryItem item : params.getItems() )
        {
            map.put( item.getItem().getUid(), item.getItem().getDisplayName() );
        }

        for ( QueryItem item : params.getItemFilters() )
        {
            map.put( item.getItem().getUid(), item.getItem().getDisplayName() );
        }

        map.putAll( getUidNameMap( params.getDimensions(), params.isHierarchyMeta() ) );
        map.putAll( getUidNameMap( params.getFilters(), params.isHierarchyMeta() ) );

        return map;
    }

    private Map<String, String> getUidNameMap( List<DimensionalObject> dimensions, boolean hierarchyMeta )
    {
        Map<String, String> map = new HashMap<String, String>();

        for ( DimensionalObject dimension : dimensions )
        {
            boolean hierarchy = hierarchyMeta && DimensionType.ORGANISATIONUNIT.equals( dimension.getDimensionType() );

            for ( IdentifiableObject idObject : dimension.getItems() )
            {
                map.put( idObject.getUid(), idObject.getDisplayName() );

                if ( hierarchy )
                {
                    OrganisationUnit unit = (OrganisationUnit) idObject;

                    map.putAll( IdentifiableObjectUtils.getUidNameMap( unit.getAncestors() ) );
                }
            }
        }

        return map;
    }

    private String getSortItem( String item, Program program )
    {
        if ( !SORTABLE_ITEMS.contains( item.toLowerCase() ) && getQueryItem( program, item ) == null )
        {
            throw new IllegalQueryException( "Descending sort item is invalid: " + item );
        }

        item = ITEM_EXECUTION_DATE.equalsIgnoreCase( item ) ? COL_NAME_EVENTDATE : item;

        return item;
    }

    private QueryItem getQueryItem( Program program, String item )
    {
        DataElement de = dataElementService.getDataElement( item );

        if ( de != null && program.getAllDataElements().contains( de ) )
        {
            return new QueryItem( de, de.isNumericType() );
        }

        TrackedEntityAttribute at = attributeService.getTrackedEntityAttribute( item );

        if ( at != null && program.getTrackedEntityAttributes().contains( at ) )
        {
            return new QueryItem( at, at.isNumericType() );
        }

        throw new IllegalQueryException( "Item identifier does not reference any item part of the program: " + item );
    }
}
