package org.hisp.dhis.trackedentity.hibernate;

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

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdentifiers;
import static org.hisp.dhis.program.ProgramStageInstance.SKIPPED_STATUS;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;
import static org.hisp.dhis.system.util.TextUtils.getTokens;
import static org.hisp.dhis.system.util.TextUtils.removeLastAnd;
import static org.hisp.dhis.system.util.TextUtils.removeLastComma;
import static org.hisp.dhis.system.util.TextUtils.removeLastOr;
import static org.hisp.dhis.trackedentity.TrackedEntityInstance.PREFIX_PROGRAM;
import static org.hisp.dhis.trackedentity.TrackedEntityInstance.PREFIX_PROGRAM_EVENT_BY_STATUS;
import static org.hisp.dhis.trackedentity.TrackedEntityInstance.PREFIX_PROGRAM_INSTANCE;
import static org.hisp.dhis.trackedentity.TrackedEntityInstance.PREFIX_PROGRAM_STAGE;
import static org.hisp.dhis.trackedentity.TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.CREATED_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.LAST_UPDATED_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.ORG_UNIT_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.TRACKED_ENTITY_ID;
import static org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams.TRACKED_ENTITY_INSTANCE_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.common.QueryFilter;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.SetMap;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.system.util.Timer;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.validation.ValidationCriteria;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 */
@Transactional
public class HibernateTrackedEntityInstanceStore
    extends HibernateIdentifiableObjectStore<TrackedEntityInstance>
    implements TrackedEntityInstanceStore
{
    private static final Log log = LogFactory.getLog( HibernateTrackedEntityInstanceStore.class );

    private static final Map<ProgramStatus, Integer> PROGRAM_STATUS_MAP = new HashMap<ProgramStatus, Integer>()
    {
        {
            put( ProgramStatus.ACTIVE, ProgramInstance.STATUS_ACTIVE );
            put( ProgramStatus.COMPLETED, ProgramInstance.STATUS_COMPLETED );
            put( ProgramStatus.CANCELLED, ProgramInstance.STATUS_CANCELLED );
        }
    };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public List<Map<String, String>> getTrackedEntityInstances( TrackedEntityInstanceQueryParams params )
    {
        SqlHelper hlp = new SqlHelper();

        // ---------------------------------------------------------------------
        // Select clause
        // ---------------------------------------------------------------------

        String sql = "select tei.uid as " + TRACKED_ENTITY_INSTANCE_ID + ", " + "tei.created as " + CREATED_ID + ", "
            + "tei.lastupdated as " + LAST_UPDATED_ID + ", " + "ou.uid as " + ORG_UNIT_ID + ", " + "te.uid as "
            + TRACKED_ENTITY_ID + ", ";

        for ( QueryItem item : params.getAttributes() )
        {
            String col = statementBuilder.columnQuote( item.getItemId() );

            sql += col + ".value as " + col + ", ";
        }

        sql = removeLastComma( sql ) + " ";

        // ---------------------------------------------------------------------
        // From and where clause
        // ---------------------------------------------------------------------

        sql += getFromWhereClause( params, hlp );

        // ---------------------------------------------------------------------
        // Paging clause
        // ---------------------------------------------------------------------

        if ( params.isPaging() )
        {
            sql += "limit " + params.getPageSizeWithDefault() + " offset " + params.getOffset();
        }

        // ---------------------------------------------------------------------
        // Query
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        t.getTime( "Tracked entity instance query SQL: " + sql );

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        while ( rowSet.next() )
        {
            final Map<String, String> map = new HashMap<String, String>();

            map.put( TRACKED_ENTITY_INSTANCE_ID, rowSet.getString( TRACKED_ENTITY_INSTANCE_ID ) );
            map.put( CREATED_ID, rowSet.getString( CREATED_ID ) );
            map.put( LAST_UPDATED_ID, rowSet.getString( LAST_UPDATED_ID ) );
            map.put( ORG_UNIT_ID, rowSet.getString( ORG_UNIT_ID ) );
            map.put( TRACKED_ENTITY_ID, rowSet.getString( TRACKED_ENTITY_ID ) );

            for ( QueryItem item : params.getAttributes() )
            {
                map.put( item.getItemId(), rowSet.getString( item.getItemId() ) );
            }

            list.add( map );
        }

        return list;
    }

    @Override
    public int getTrackedEntityInstanceCount( TrackedEntityInstanceQueryParams params )
    {
        SqlHelper hlp = new SqlHelper();

        // ---------------------------------------------------------------------
        // Select clause
        // ---------------------------------------------------------------------

        String sql = "select count(tei.uid) as " + TRACKED_ENTITY_INSTANCE_ID + " ";

        // ---------------------------------------------------------------------
        // From and where clause
        // ---------------------------------------------------------------------

        sql += getFromWhereClause( params, hlp );

        // ---------------------------------------------------------------------
        // Query
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();

        Integer count = jdbcTemplate.queryForObject( sql, Integer.class );

        t.getTime( "Tracked entity instance count SQL: " + sql );

        return count;
    }

    /**
     * From, join and where clause. For attribute params, restriction is set in
     * inner join. For query params, restriction is set in where clause.
     */
    private String getFromWhereClause( TrackedEntityInstanceQueryParams params, SqlHelper hlp )
    {
        final String regexp = statementBuilder.getRegexpMatch();
        final String wordStart = statementBuilder.getRegexpWordStart();
        final String wordEnd = statementBuilder.getRegexpWordEnd();

        String sql = "from trackedentityinstance tei "
            + "inner join trackedentity te on tei.trackedentityid = te.trackedentityid "
            + "inner join organisationunit ou on tei.organisationunitid = ou.organisationunitid ";

        for ( QueryItem item : params.getAttributesAndFilters() )
        {
            final String col = statementBuilder.columnQuote( item.getItemId() );

            final String joinClause = item.hasFilter() ? "inner join" : "left join";

            sql += joinClause + " " + "trackedentityattributevalue as " + col + " " + "on " + col
                + ".trackedentityinstanceid = tei.trackedentityinstanceid " + "and " + col
                + ".trackedentityattributeid = " + item.getItem().getId() + " ";

            if ( !params.isOrQuery() && item.hasFilter() )
            {
                for ( QueryFilter filter : item.getFilters() )
                {
                    final String encodedFilter = statementBuilder.encode( filter.getFilter(), false );

                    final String queryCol = item.isNumeric() ? (col + ".value") : "lower(" + col + ".value)";

                    sql += "and " + queryCol + " " + filter.getSqlOperator() + " "
                        + StringUtils.lowerCase( filter.getSqlFilter( encodedFilter ) ) + " ";
                }
            }
        }

        if ( params.isOrganisationUnitMode( OrganisationUnitSelectionMode.DESCENDANTS ) )
        {
            sql += "left join _orgunitstructure ous on tei.organisationunitid = ous.organisationunitid ";
        }

        if ( params.hasTrackedEntity() )
        {
            sql += hlp.whereAnd() + " tei.trackedentityid = " + params.getTrackedEntity().getId() + " ";
        }

        if ( params.isOrganisationUnitMode( OrganisationUnitSelectionMode.DESCENDANTS ) )
        {
            SetMap<Integer, OrganisationUnit> levelOuMap = params.getLevelOrgUnitMap();

            for ( Integer level : levelOuMap.keySet() )
            {
                sql += hlp.whereAnd() + " ous.idlevel" + level + " in ("
                    + getCommaDelimitedString( getIdentifiers( levelOuMap.get( level ) ) ) + ") or ";
            }

            sql = removeLastOr( sql );
        }
        else if ( params.isOrganisationUnitMode( OrganisationUnitSelectionMode.ALL ) )
        {
        }
        else
        // SELECTED (default)
        {
            sql += hlp.whereAnd() + " tei.organisationunitid in ("
                + getCommaDelimitedString( getIdentifiers( params.getOrganisationUnits() ) ) + ") ";
        }

        if ( params.hasProgram() )
        {
            sql += hlp.whereAnd() + " exists (" + "select pi.trackedentityinstanceid " + "from programinstance pi ";

            if ( params.hasEventStatus() )
            {
                sql += "left join programstageinstance psi " + "on pi.programinstanceid = psi.programinstanceid ";
            }

            sql += "where pi.trackedentityinstanceid = tei.trackedentityinstanceid " + "and pi.programid = "
                + params.getProgram().getId() + " ";

            if ( params.hasProgramStatus() )
            {
                sql += "and pi.status = " + PROGRAM_STATUS_MAP.get( params.getProgramStatus() ) + " ";
            }

            if ( params.hasFollowUp() )
            {
                sql += "and pi.followup = " + params.getFollowUp() + " ";
            }

            if ( params.hasProgramStartDate() )
            {
                sql += "and pi.enrollmentdate >= '" + getMediumDateString( params.getProgramStartDate() ) + "' ";
            }

            if ( params.hasProgramEndDate() )
            {
                sql += "and pi.enrollmentdate <= '" + getMediumDateString( params.getProgramEndDate() ) + "' ";
            }

            if ( params.hasEventStatus() )
            {
                sql += getEventStatusWhereClause( params );
            }

            sql += ") ";
        }

        if ( params.isOrQuery() && params.hasAttributesOrFilters() )
        {
            sql += hlp.whereAnd() + " (";

            List<String> queryTokens = getTokens( params.getQuery() );

            for ( String queryToken : queryTokens )
            {
                final String query = statementBuilder.encode( queryToken, false );

                sql += "(";

                for ( QueryItem item : params.getAttributesAndFilters() )
                {
                    final String col = statementBuilder.columnQuote( item.getItemId() );

                    sql += "lower(" + col + ".value) " + regexp + " '" + wordStart + StringUtils.lowerCase( query )
                        + wordEnd + "' or ";
                }

                sql = removeLastOr( sql ) + ") and ";
            }

            sql = removeLastAnd( sql ) + ") ";
        }

        return sql;
    }

    private String getEventStatusWhereClause( TrackedEntityInstanceQueryParams params )
    {
        String start = getMediumDateString( params.getEventStartDate() );
        String end = getMediumDateString( params.getEventEndDate() );

        String sql = StringUtils.EMPTY;

        if ( params.isEventStatus( EventStatus.COMPLETED ) )
        {
            sql = "and psi.executiondate >= '" + start + "' and psi.executiondate <= '" + end + "' "
                + "and psi.completed = true ";
        }
        else if ( params.isEventStatus( EventStatus.VISITED ) )
        {
            sql = "and psi.executiondate >= '" + start + "' and psi.executiondate <= '" + end + "' "
                + "and psi.completed = false ";
        }
        else if ( params.isEventStatus( EventStatus.FUTURE_VISIT ) )
        {
            sql = "and psi.executiondate is null and psi.duedate >= '" + start + "' and psi.duedate <= '" + end + "' "
                + "and psi.status is not null and date(now()) <= date(psi.duedate) ";
        }
        else if ( params.isEventStatus( EventStatus.LATE_VISIT ) )
        {
            sql = "and psi.executiondate is null and psi.duedate >= '" + start + "' and psi.duedate <= '" + end + "' "
                + "and psi.status is not null and date(now()) > date(psi.duedate) ";
        }
        else if ( params.isEventStatus( EventStatus.SKIPPED ) )
        {
            sql = "and psi.duedate >= '" + start + "' and psi.duedate <= '" + end + "' " + "and psi.status = "
                + SKIPPED_STATUS + " ";
        }

        return sql;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<TrackedEntityInstance> getByOrgUnit( OrganisationUnit organisationUnit, Integer min, Integer max )
    {
        String hql = "select p from TrackedEntityInstance p where p.organisationUnit = :organisationUnit order by p.id DESC";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<TrackedEntityInstance> getByOrgUnitProgram( OrganisationUnit organisationUnit, Program program,
        Integer min, Integer max )
    {
        String hql = "select pt from TrackedEntityInstance pt inner join pt.programInstances pi "
            + "where pt.organisationUnit = :organisationUnit " + "and pi.program = :program "
            + "and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "organisationUnit", organisationUnit );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<TrackedEntityInstance> getByProgram( Program program, Integer min, Integer max )
    {
        String hql = "select pt from TrackedEntityInstance pt inner join pt.programInstances pi "
            + "where pi.program = :program and pi.status = :status";

        Query query = getQuery( hql );
        query.setEntity( "program", program );
        query.setInteger( "status", ProgramInstance.STATUS_ACTIVE );

        return query.list();
    }

    @Override
    public int countListTrackedEntityInstanceByOrgunit( OrganisationUnit organisationUnit )
    {
        Query query = getQuery( "select count(p.id) from TrackedEntityInstance p where p.organisationUnit.id=:orgUnitId " );

        query.setParameter( "orgUnitId", organisationUnit.getId() );

        Number rs = (Number) query.uniqueResult();

        return rs != null ? rs.intValue() : 0;
    }

    @Override
    public int countGetTrackedEntityInstancesByOrgUnitProgram( OrganisationUnit organisationUnit, Program program )
    {
        String sql = "select count(p.trackedentityinstanceid) from trackedentityinstance p join programinstance pi on p.trackedentityinstanceid=pi.trackedentityinstanceid "
            + "where p.organisationunitid="
            + organisationUnit.getId()
            + " and pi.programid="
            + program.getId()
            + " and pi.status=" + ProgramInstance.STATUS_ACTIVE;

        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<TrackedEntityInstance> getRepresentatives( TrackedEntityInstance instance )
    {
        String hql = "select distinct p from TrackedEntityInstance p where p.representative = :representative order by p.id DESC";

        return getQuery( hql ).setEntity( "representative", instance ).list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<TrackedEntityInstance> getByPhoneNumber( String phoneNumber, Integer min, Integer max )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "attributeValues", "attributeValue" );
        criteria.createAlias( "attributeValue.attribute", "attribute" );
        criteria.add( Restrictions.eq( "attribute.valueType", TrackedEntityAttribute.TYPE_PHONE_NUMBER ) );
        criteria.add( Restrictions.like( "attributeValue.value", phoneNumber ) );

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min );
            criteria.setMaxResults( max );
        }

        return criteria.list();
    }

    public String validate( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        if ( instance.getAttributeValues() != null && instance.getAttributeValues().size() > 0 )
        {

            for ( TrackedEntityAttributeValue attributeValue : instance.getAttributeValues() )
            {
                TrackedEntityAttribute attribute = attributeValue.getAttribute();

                if ( attribute.isUnique() )
                {
                    Criteria criteria = getCriteria();
                    criteria.add( Restrictions.ne( "id", instance.getId() ) );
                    criteria.createAlias( "attributeValues", "attributeValue" );
                    criteria.createAlias( "attributeValue.attribute", "attribute" );
                    criteria.add( Restrictions.eq( "attributeValue.value", attributeValue.getValue() ) );
                    criteria.add( Restrictions.eq( "attributeValue.attribute", attribute ) );

                    if ( attribute.getId() != 0 )
                    {
                        criteria.add( Restrictions.ne( "id", attribute.getId() ) );
                    }

                    if ( attribute.getOrgunitScope() )
                    {
                        criteria.add( Restrictions.eq( "organisationUnit", instance.getOrganisationUnit() ) );
                    }

                    if ( program != null && attribute.getProgramScope() )
                    {
                        criteria.createAlias( "programInstances", "programInstance" );
                        criteria.add( Restrictions.eq( "programInstance.program", program ) );
                    }

                    Number rs = (Number) criteria.setProjection(
                        Projections.projectionList().add( Projections.property( "attribute.id" ) ) ).uniqueResult();

                    if ( rs != null && rs.intValue() > 0 )
                    {
                        return TrackedEntityInstanceService.ERROR_DUPLICATE_IDENTIFIER
                            + TrackedEntityInstanceService.SAPERATOR + rs.intValue();
                    }
                }
            }
        }

        if ( program != null )
        {
            ValidationCriteria validationCriteria = validateEnrollment( instance, program, format );

            if ( validationCriteria != null )
            {
                return TrackedEntityInstanceService.ERROR_ENROLLMENT + TrackedEntityInstanceService.SAPERATOR
                    + validationCriteria.getId();
            }
        }

        return TrackedEntityInstanceService.ERROR_NONE + "";
    }

    public ValidationCriteria validateEnrollment( TrackedEntityInstance instance, Program program, I18nFormat format )
    {
        try
        {
            for ( ValidationCriteria criteria : program.getValidationCriteria() )
            {
                String value = "";
                for ( TrackedEntityAttributeValue attributeValue : instance.getAttributeValues() )
                {
                    if ( attributeValue.getAttribute().getUid().equals( criteria.getProperty() ) )
                    {
                        value = attributeValue.getValue();

                        String type = attributeValue.getAttribute().getValueType();
                        // For integer type
                        if ( type.equals( TrackedEntityAttribute.TYPE_NUMBER ) )
                        {
                            int value1 = Integer.parseInt( value );
                            int value2 = Integer.parseInt( criteria.getValue() );

                            if ( (criteria.getOperator() == ValidationCriteria.OPERATOR_LESS_THAN && value1 >= value2)
                                || (criteria.getOperator() == ValidationCriteria.OPERATOR_EQUAL_TO && value1 != value2)
                                || (criteria.getOperator() == ValidationCriteria.OPERATOR_GREATER_THAN && value1 <= value2) )
                            {
                                return criteria;
                            }
                        }
                        // For Date type
                        else if ( type.equals( TrackedEntityAttribute.TYPE_DATE ) )
                        {
                            Date value1 = format.parseDate( value );
                            Date value2 = format.parseDate( criteria.getValue() );
                            int i = value1.compareTo( value2 );
                            if ( i != criteria.getOperator() )
                            {
                                return criteria;
                            }
                        }
                        // For other types
                        else
                        {
                            if ( criteria.getOperator() == ValidationCriteria.OPERATOR_EQUAL_TO
                                && !value.equals( criteria.getValue() ) )
                            {
                                return criteria;
                            }

                        }

                    }
                }

            }

            // Return null if all criteria are met

            return null;
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<TrackedEntityAttribute> attributes, Integer statusEnrollment, Integer min,
        Integer max )
    {
        String sql = searchTrackedEntityInstanceSql( false, searchKeys, orgunits, followup, attributes,
            statusEnrollment, min, max );

        List<Integer> programStageInstanceIds = new ArrayList<Integer>();
        try
        {
            programStageInstanceIds = jdbcTemplate.query( sql, new RowMapper<Integer>()
            {
                public Integer mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return rs.getInt( "programstageinstanceid" );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return programStageInstanceIds;
    }

    private String searchTrackedEntityInstanceSql( boolean count, List<String> searchKeys,
        Collection<OrganisationUnit> orgunits, Boolean followup, Collection<TrackedEntityAttribute> attributes,
        Integer statusEnrollment, Integer min, Integer max )
    {
        String selector = count ? "count(*) " : "* ";
        String sql = "select " + selector + " from ( select distinct p.trackedentityinstanceid,";

        if ( attributes != null )
        {
            for ( TrackedEntityAttribute attribute : attributes )
            {
                sql += "(select value from trackedentityattributevalue where trackedentityinstanceid=p.trackedentityinstanceid and trackedentityattributeid="
                    + attribute.getId() + " ) as " + PREFIX_TRACKED_ENTITY_ATTRIBUTE + "_" + attribute.getId() + " ,";
            }
        }

        String instanceWhere = "";
        String instanceOperator = " where ";
        String instanceGroupBy = " GROUP BY  p.trackedentityinstanceid ";
        String otherWhere = "";
        String operator = " where ";
        String orderBy = "";
        boolean isSearchEvent = false;
        boolean isPriorityEvent = false;
        Collection<Integer> orgunitChilrenIds = null;

        if ( orgunits != null )
        {
            orgunitChilrenIds = getOrgunitChildren( orgunits );
        }

        for ( String searchKey : searchKeys )
        {
            String[] keys = searchKey.split( "_" );

            if ( keys.length <= 1 || keys[1] == null || keys[1].trim().isEmpty() || keys[1].equals( "null" ) )
            {
                continue;
            }

            String id = keys[1];
            String value = "";

            if ( keys.length >= 3 )
            {
                value = keys[2];
            }

            if ( keys[0].equals( PREFIX_TRACKED_ENTITY_ATTRIBUTE ) )
            {
                sql += "(select value from trackedentityattributevalue where trackedentityinstanceid=p.trackedentityinstanceid and trackedentityattributeid="
                    + id + " ) as " + PREFIX_TRACKED_ENTITY_ATTRIBUTE + "_" + id + ",";

                String[] keyValues = value.split( " " );
                otherWhere += operator + "(";
                String opt = "";

                for ( String v : keyValues )
                {
                    otherWhere += opt + " lower(" + PREFIX_TRACKED_ENTITY_ATTRIBUTE + "_" + id + ") like '%" + v + "%'";
                    opt = "or";
                }

                otherWhere += ")";
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM ) )
            {
                sql += "(select programid from programinstance pgi where trackedentityinstanceid=p.trackedentityinstanceid and programid="
                    + id;

                if ( statusEnrollment != null )
                {
                    sql += " and pgi.status=" + statusEnrollment;
                }

                sql += " limit 1 ) as " + PREFIX_PROGRAM + "_" + id + ",";
                otherWhere += operator + PREFIX_PROGRAM + "_" + id + "=" + id;
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_INSTANCE ) )
            {
                sql += "(select pi."
                    + id
                    + " from programinstance pi where trackedentityinstanceid=p.trackedentityinstanceid and pi.status=0 ";

                if ( keys.length == 5 )
                {
                    sql += " and pi.programid=" + keys[4];
                }
                else
                {
                    sql += " limit 1 ";
                }

                sql += ") as " + PREFIX_PROGRAM_INSTANCE + "_" + id + ",";
                otherWhere += operator + PREFIX_PROGRAM_INSTANCE + "_" + id + "='" + keys[2] + "'";
                operator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_EVENT_BY_STATUS ) )
            {
                isSearchEvent = true;
                isPriorityEvent = Boolean.parseBoolean( keys[5] );
                instanceWhere += instanceOperator + "pgi.trackedentityinstanceid=p.trackedentityinstanceid and ";
                instanceWhere += "pgi.programid=" + id + " and ";
                instanceWhere += "pgi.status=" + ProgramInstance.STATUS_ACTIVE;

                String operatorStatus = "";
                String condition = " and ( ";

                for ( int index = 6; index < keys.length; index++ )
                {
                    int statusEvent = Integer.parseInt( keys[index] );
                    switch ( statusEvent )
                    {
                    case ProgramStageInstance.COMPLETED_STATUS:
                        instanceWhere += condition + operatorStatus
                            + "( psi.executiondate is not null and  psi.executiondate>='" + keys[2]
                            + "' and psi.executiondate<='" + keys[3] + "' and psi.completed=true ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            instanceWhere += " and psi.organisationunitid in( "
                                + getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            instanceWhere += " and psi.organisationunitid=" + getOrgUnitId( keys );
                        }

                        instanceWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.VISITED_STATUS:
                        instanceWhere += condition + operatorStatus
                            + "( psi.executiondate is not null and psi.executiondate>='" + keys[2]
                            + "' and psi.executiondate<='" + keys[3] + "' and psi.completed=false ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            instanceWhere += " and psi.organisationunitid in( "
                                + getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            instanceWhere += " and psi.organisationunitid=" + getOrgUnitId( keys );
                        }

                        instanceWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.FUTURE_VISIT_STATUS:
                        instanceWhere += condition + operatorStatus + "( psi.executiondate is null and psi.duedate>='"
                            + keys[2] + "' and psi.duedate<='" + keys[3]
                            + "' and psi.status is not null and (DATE(now()) - DATE(psi.duedate) <= 0) ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            instanceWhere += " and p.organisationunitid in( "
                                + getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            instanceWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }

                        instanceWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.LATE_VISIT_STATUS:
                        instanceWhere += condition + operatorStatus + "( psi.executiondate is null and  psi.duedate>='"
                            + keys[2] + "' and psi.duedate<='" + keys[3]
                            + "' and psi.status is not null and (DATE(now()) - DATE(psi.duedate) > 0) ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            instanceWhere += " and p.organisationunitid in( "
                                + getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            instanceWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }

                        instanceWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    case ProgramStageInstance.SKIPPED_STATUS:
                        instanceWhere += condition + operatorStatus + "( psi.status=5 and  psi.duedate>='" + keys[2]
                            + "' and psi.duedate<='" + keys[3] + "' ";

                        // get events by orgunit children
                        if ( keys[4].equals( "-1" ) )
                        {
                            instanceWhere += " and psi.organisationunitid in( "
                                + getCommaDelimitedString( orgunitChilrenIds ) + " )";
                        }

                        // get events by selected orgunit
                        else if ( !keys[4].equals( "0" ) )
                        {
                            instanceWhere += " and p.organisationunitid=" + getOrgUnitId( keys );
                        }
                        instanceWhere += ")";
                        operatorStatus = " OR ";
                        condition = "";
                        continue;
                    default:
                        continue;
                    }
                }
                if ( condition.isEmpty() )
                {
                    instanceWhere += ")";
                }

                instanceWhere += " and pgi.status=" + ProgramInstance.STATUS_ACTIVE + " ";
                instanceOperator = " and ";
            }
            else if ( keys[0].equals( PREFIX_PROGRAM_STAGE ) )
            {
                isSearchEvent = true;
                instanceWhere += instanceOperator
                    + "pgi.trackedentityinstanceid=p.trackedentityinstanceid and psi.programstageid=" + id + " and ";
                instanceWhere += "psi.duedate>='" + keys[3] + "' and psi.duedate<='" + keys[4] + "' and ";
                instanceWhere += "psi.organisationunitid = " + keys[5] + " and ";

                int statusEvent = Integer.parseInt( keys[2] );
                switch ( statusEvent )
                {
                case ProgramStageInstance.COMPLETED_STATUS:
                    instanceWhere += "psi.completed=true";
                    break;
                case ProgramStageInstance.VISITED_STATUS:
                    instanceWhere += "psi.executiondate is not null and psi.completed=false";
                    break;
                case ProgramStageInstance.FUTURE_VISIT_STATUS:
                    instanceWhere += "psi.executiondate is null and psi.duedate >= now()";
                    break;
                case ProgramStageInstance.LATE_VISIT_STATUS:
                    instanceWhere += "psi.executiondate is null and psi.duedate < now()";
                    break;
                default:
                    break;
                }

                instanceWhere += " and pgi.status=" + ProgramInstance.STATUS_ACTIVE + " ";
                instanceOperator = " and ";
            }
        }

        if ( orgunits != null && !isSearchEvent )
        {
            sql += "(select organisationunitid from trackedentityinstance where trackedentityinstanceid=p.trackedentityinstanceid and organisationunitid in ( "
                + getCommaDelimitedString( getOrganisationUnitIds( orgunits ) ) + " ) ) as orgunitid,";
            otherWhere += operator + "orgunitid in ( " + getCommaDelimitedString( getOrganisationUnitIds( orgunits ) )
                + " ) ";
        }

        sql = sql.substring( 0, sql.length() - 1 ) + " "; // Removing last comma

        String from = " from trackedentityinstance p ";

        if ( isSearchEvent )
        {
            String subSQL = " , psi.programstageinstanceid as programstageinstanceid, pgs.name as programstagename, psi.duedate as duedate ";

            if ( isPriorityEvent )
            {
                subSQL += ",pgi.followup ";
                orderBy = " ORDER BY pgi.followup desc, p.trackedentityinstanceid, duedate asc ";
                instanceGroupBy += ",pgi.followup ";
            }
            else
            {
                orderBy = " ORDER BY p.trackedentityinstanceid, duedate asc ";
            }

            sql = sql + subSQL + from + " inner join programinstance pgi on "
                + " (pgi.trackedentityinstanceid=p.trackedentityinstanceid) "
                + " inner join programstageinstance psi on (psi.programinstanceid=pgi.programinstanceid) "
                + " inner join programstage pgs on (pgs.programstageid=psi.programstageid) ";

            instanceGroupBy += ",psi.programstageinstanceid, pgs.name, psi.duedate ";

            from = " ";
        }

        sql += from + instanceWhere;
        if ( followup != null )
        {
            sql += " AND pgi.followup=" + followup;
        }
        if ( isSearchEvent )
        {
            sql += instanceGroupBy;
        }
        sql += orderBy;
        sql += " ) as searchresult";
        sql += otherWhere;

        if ( min != null && max != null )
        {
            sql += " limit " + max + " offset " + min;
        }

        log.info( "Search tracked entity instance SQL: " + sql );

        return sql;
    }

    private Integer getOrgUnitId( String[] keys )
    {
        Integer orgUnitId;
        try
        {
            orgUnitId = Integer.parseInt( keys[4] );
        }
        catch ( NumberFormatException e )
        {
            // handle as uid
            OrganisationUnit ou = organisationUnitService.getOrganisationUnit( keys[4] );
            orgUnitId = ou.getId();
        }
        return orgUnitId;
    }

    private Collection<Integer> getOrgunitChildren( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        if ( orgunits != null )
        {
            for ( OrganisationUnit orgunit : orgunits )
            {
                orgUnitIds
                    .addAll( organisationUnitService.getOrganisationUnitHierarchy().getChildren( orgunit.getId() ) );
                orgUnitIds.remove( orgunit.getId() );
            }
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }

    private Collection<Integer> getOrganisationUnitIds( Collection<OrganisationUnit> orgunits )
    {
        Collection<Integer> orgUnitIds = new HashSet<Integer>();

        for ( OrganisationUnit orgUnit : orgunits )
        {
            orgUnitIds.add( orgUnit.getId() );
        }

        if ( orgUnitIds.size() == 0 )
        {
            orgUnitIds.add( 0 );
        }

        return orgUnitIds;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<TrackedEntityInstance> getByAttributeValue( String searchText, int attributeId, Integer min,
        Integer max )
    {
        String hql = "FROM TrackedEntityAttributeValue pav WHERE lower (pav.value) LIKE lower ('%" + searchText
            + "%') AND pav.attribute.id =:attributeId order by pav.entityInstance";

        Query query = getQuery( hql );

        query.setInteger( "attributeId", attributeId );

        if ( min != null && max != null )
        {
            query.setFirstResult( min ).setMaxResults( max );
        }

        List<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>();
        Collection<TrackedEntityAttributeValue> attributeValue = query.list();
        for ( TrackedEntityAttributeValue pv : attributeValue )
        {
            entityInstances.add( pv.getEntityInstance() );
        }

        return entityInstances;
    }

    @Override
    public Collection<TrackedEntityInstance> search( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<TrackedEntityAttribute> attributes, Integer statusEnrollment, Integer min,
        Integer max )
    {
        String sql = searchTrackedEntityInstanceSql( false, searchKeys, orgunits, followup, attributes,
            statusEnrollment, min, max );
        Collection<TrackedEntityInstance> instances = new HashSet<TrackedEntityInstance>();
        try
        {
            instances = jdbcTemplate.query( sql, new RowMapper<TrackedEntityInstance>()
            {
                public TrackedEntityInstance mapRow( ResultSet rs, int rowNum )
                    throws SQLException
                {
                    return get( rs.getInt( 1 ) );
                }
            } );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
        return instances;
    }

    @Override
    public int countSearch( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment )
    {
        String sql = searchTrackedEntityInstanceSql( true, searchKeys, orgunits, followup, null, statusEnrollment,
            null, null );
        return jdbcTemplate.queryForObject( sql, Integer.class );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<TrackedEntityInstance> get( TrackedEntity trackedEntity )
    {
        return getCriteria( Restrictions.eq( "trackedEntity", trackedEntity ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<TrackedEntityInstance> searchTrackedEntityByAttribute( OrganisationUnit orgunit,
        String attributeValue, Program program, Integer min, Integer max )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "attributeValues", "attributeValue" );
        criteria.add( Restrictions.ilike( "attributeValue.value", "%" + attributeValue + "%" ) );
        criteria.add( Restrictions.eq( "organisationUnit", orgunit ) );

        if ( program != null )
        {
            criteria.createAlias( "programInstances", "programInstance" );
            criteria.createAlias( "programInstance.program", "program" );
            criteria.add( Restrictions.eq( "program", program ) );
        }

        if ( min != null && max != null )
        {
            criteria.setFirstResult( min );
            criteria.setMaxResults( max );
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        criteria.addOrder( Order.asc( "lastUpdated" ) );
        return criteria.list();
    }

    @Override
    public int countTrackedEntityByAttribute( OrganisationUnit orgunit, String attributeValue, Program program )
    {
        Criteria criteria = getCriteria();
        criteria.createAlias( "attributeValues", "attributeValue" );
        criteria.add( Restrictions.eq( "organisationUnit", orgunit ) );

        if ( program != null )
        {
            criteria.createAlias( "programInstances", "programInstance" );
            criteria.createAlias( "programInstance.program", "program" );
            criteria.add( Restrictions.eq( "program", program ) );
        }
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();

        return (rs != null && rs.intValue() > 0) ? rs.intValue() : 0;
    }
}
