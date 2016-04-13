package org.hisp.dhis.resourcetable.jdbc;

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

import java.util.List;

import org.amplecode.quick.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.resourcetable.ResourceTableStore;
import org.hisp.dhis.resourcetable.statement.CreateCategoryOptionGroupSetTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateCategoryTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateDataElementGroupSetTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateIndicatorGroupSetTableStatement;
import org.hisp.dhis.resourcetable.statement.CreateOrganisationUnitGroupSetTableStatement;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Lars Helge Overland
 */
public class JdbcResourceTableStore
    implements ResourceTableStore
{
    private static final Log log = LogFactory.getLog( JdbcResourceTableStore.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private StatementBuilder statementBuilder;

    public void setStatementBuilder( StatementBuilder statementBuilder )
    {
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // ResourceTableStore implementation
    // -------------------------------------------------------------------------

    public void batchUpdate( int columns, String tableName, List<Object[]> batchArgs )
    {
        if ( columns == 0 || tableName == null )
        {
            return;
        }
        
        StringBuilder builder = new StringBuilder( "insert into " + tableName + " values (" );
        
        for ( int i = 0; i < columns; i++ )
        {
            builder.append( "?," );
        }
        
        builder.deleteCharAt( builder.length() - 1 ).append( ")" );
        
        jdbcTemplate.batchUpdate( builder.toString(), batchArgs );
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    public void createOrganisationUnitStructure( int maxLevel )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_ORGANISATION_UNIT_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }

        String quote = statementBuilder.getColumnQuote();
        
        StringBuilder sql = new StringBuilder();
        
        sql.append( "CREATE TABLE " ).append( TABLE_NAME_ORGANISATION_UNIT_STRUCTURE ).
            append( " ( organisationunitid INTEGER NOT NULL PRIMARY KEY, organisationunituid CHARACTER(11), level INTEGER" );
        
        for ( int k = 1 ; k <= maxLevel; k++ )
        {
            sql.append( ", " ).append( quote ).append( "idlevel" + k ).append( quote ).append (" INTEGER, " ).
                append( quote ).append( "uidlevel" + k ).append( quote ).append( " CHARACTER(11)" );
        }
        
        sql.append( ");" );
        
        log.info( "Create organisation unit structure table SQL: " + sql );
        
        jdbcTemplate.execute( sql.toString() );
        
        final String uidInSql = "create unique index in_orgunitstructure_organisationunituid on _orgunitstructure(organisationunituid)";
        
        jdbcTemplate.execute( uidInSql );
    }
    
    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------
    
    public void createDataElementCategoryOptionComboName()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY_OPTION_COMBO_NAME );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String sql = "CREATE TABLE " + TABLE_NAME_CATEGORY_OPTION_COMBO_NAME + 
            " ( categoryoptioncomboid INTEGER NOT NULL PRIMARY KEY, categoryoptioncomboname VARCHAR(250) )";
        
        log.info( "Create category option combo name table SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }

    // -------------------------------------------------------------------------
    // CategoryOptionGroupSetTable
    // -------------------------------------------------------------------------

    public void createCategoryOptionGroupSetStructure( List<CategoryOptionGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateCategoryOptionGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateCategoryOptionGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }
    
    // -------------------------------------------------------------------------
    // DataElementGroupSetTable
    // -------------------------------------------------------------------------

    public void createDataElementGroupSetStructure( List<DataElementGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateDataElementGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateDataElementGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }

    public void populateDataElementGroupSetStructure( List<DataElementGroupSet> groupSets )
    {
        String sql = 
            "insert into " + CreateDataElementGroupSetTableStatement.TABLE_NAME + " " +
            "select d.dataelementid as dataelementid, d.name as dataelementname, ";
        
        for ( DataElementGroupSet groupSet : groupSets )
        {
            sql += "(" +
                "select deg.name from dataelementgroup deg " +
                "inner join dataelementgroupmembers degm on degm.dataelementgroupid = deg.dataelementgroupid " +
                "inner join dataelementgroupsetmembers degsm on degsm.dataelementgroupid = degm.dataelementgroupid and degsm.dataelementgroupsetid = " + groupSet.getId() + " " +
                "where degm.dataelementid = d.dataelementid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getName() ) + ", ";
            
            sql += "(" +
                "select deg.uid from dataelementgroup deg " +
                "inner join dataelementgroupmembers degm on degm.dataelementgroupid = deg.dataelementgroupid " +
                "inner join dataelementgroupsetmembers degsm on degsm.dataelementgroupid = degm.dataelementgroupid and degsm.dataelementgroupsetid = " + groupSet.getId() + " " +
                "where degm.dataelementid = d.dataelementid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getUid() ) + ", ";            
        }

        sql = TextUtils.removeLastComma( sql ) + " ";
        sql += "from dataelement d";

        log.info( "Populate data element group set structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }
    
    // -------------------------------------------------------------------------
    // DataElementGroupSetTable
    // -------------------------------------------------------------------------

    public void createIndicatorGroupSetStructure( List<IndicatorGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateIndicatorGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateIndicatorGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }

    public void populateIndicatorGroupSetStructure( List<IndicatorGroupSet> groupSets )
    {
        String sql =
            "insert into " + CreateIndicatorGroupSetTableStatement.TABLE_NAME + " " +
             "select i.indicatorid as indicatorid, i.name as indicatorname, ";
        
        for ( IndicatorGroupSet groupSet : groupSets )
        {
            sql += "(" +
                "select ig.name from indicatorgroup ig " +
                "inner join indicatorgroupmembers igm on igm.indicatorgroupid = ig.indicatorgroupid " +
                "inner join indicatorgroupsetmembers igsm on igsm.indicatorgroupid = igm.indicatorgroupid and igsm.indicatorgroupsetid = " + groupSet.getId() + " " +
                "where igm.indicatorid = i.indicatorid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getName() ) + ", ";

            sql += "(" +
                "select ig.uid from indicatorgroup ig " +
                "inner join indicatorgroupmembers igm on igm.indicatorgroupid = ig.indicatorgroupid " +
                "inner join indicatorgroupsetmembers igsm on igsm.indicatorgroupid = igm.indicatorgroupid and igsm.indicatorgroupsetid = " + groupSet.getId() + " " +
                "where igm.indicatorid = i.indicatorid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getUid() ) + ", ";            
        }

        sql = TextUtils.removeLastComma( sql ) + " ";
        sql += "from indicator i";

        log.info( "Populate indicator group set structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }
    
    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSetTable
    // -------------------------------------------------------------------------

    public void createOrganisationUnitGroupSetStructure( List<OrganisationUnitGroupSet> groupSets )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateOrganisationUnitGroupSetTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateOrganisationUnitGroupSetTableStatement( groupSets, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }
    
    public void populateOrganisationUnitGroupSetStructure( List<OrganisationUnitGroupSet> groupSets )
    {
        String sql = 
            "insert into " + CreateOrganisationUnitGroupSetTableStatement.TABLE_NAME + " " +
            "select ou.organisationunitid as organisationunitid, ou.name as organisationunitname, ";            
        
        for ( OrganisationUnitGroupSet groupSet : groupSets )
        {
            sql += "(" + 
                "select oug.name from orgunitgroup oug " +
                "inner join orgunitgroupmembers ougm on ougm.orgunitgroupid = oug.orgunitgroupid " +
                "inner join orgunitgroupsetmembers ougsm on ougsm.orgunitgroupid = ougm.orgunitgroupid and ougsm.orgunitgroupsetid = " + groupSet.getId() + " " +
                "where ougm.organisationunitid = ou.organisationunitid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getName() ) + ", ";
            
            sql += "(" +
                "select oug.uid from orgunitgroup oug " +
                "inner join orgunitgroupmembers ougm on ougm.orgunitgroupid = oug.orgunitgroupid " +
                "inner join orgunitgroupsetmembers ougsm on ougsm.orgunitgroupid = ougm.orgunitgroupid and ougsm.orgunitgroupsetid = " + groupSet.getId() + " " +
                "where ougm.organisationunitid = ou.organisationunitid " +
                "limit 1) as " + statementBuilder.columnQuote( groupSet.getUid() ) + ", ";
        }
        
        sql = TextUtils.removeLastComma( sql ) + " ";
        sql += "from organisationunit ou";
        
        log.info( "Populate organisation unit group set structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }
    
    // -------------------------------------------------------------------------
    // CategoryTable
    // -------------------------------------------------------------------------

    public void createCategoryStructure( List<DataElementCategory> categories )
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + CreateCategoryTableStatement.TABLE_NAME );
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        Statement statement = new CreateCategoryTableStatement( categories, statementBuilder.getColumnQuote() );
        
        jdbcTemplate.execute( statement.getStatement() );
    }

    public void populateCategoryStructure( List<DataElementCategory> categories )
    {
        String sql = 
            "insert into " + CreateCategoryTableStatement.TABLE_NAME + " " +
            "select coc.categoryoptioncomboid as cocid, con.categoryoptioncomboname as cocname, ";
        
        for ( DataElementCategory category : categories )
        {
            sql += "(" +
                "select co.name from categoryoptioncombos_categoryoptions cocco " +
                "inner join dataelementcategoryoption co on cocco.categoryoptionid = co.categoryoptionid " +
                "inner join categories_categoryoptions cco on co.categoryoptionid = cco.categoryoptionid " +
                "where coc.categoryoptioncomboid = cocco.categoryoptioncomboid " +
                "and cco.categoryid = " + category.getId() + " " +
                "limit 1) as " + statementBuilder.columnQuote( category.getName() ) + ", ";

            sql += "(" +
                "select co.uid from categoryoptioncombos_categoryoptions cocco " +
                "inner join dataelementcategoryoption co on cocco.categoryoptionid = co.categoryoptionid " +
                "inner join categories_categoryoptions cco on co.categoryoptionid = cco.categoryoptionid " +
                "where coc.categoryoptioncomboid = cocco.categoryoptioncomboid " +
                "and cco.categoryid = " + category.getId() + " " +
                "limit 1) as " + statementBuilder.columnQuote( category.getUid() ) + ", ";
        }

        sql = TextUtils.removeLastComma( sql ) + " ";
        sql += 
            "from categoryoptioncombo coc " +
            "inner join _categoryoptioncomboname con on coc.categoryoptioncomboid = con.categoryoptioncomboid";
        
        log.info( "Populate category structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );        
    }
    
    // -------------------------------------------------------------------------
    // DataElementStructure
    // -------------------------------------------------------------------------

    public void createDataElementStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATA_ELEMENT_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String sql = "CREATE TABLE " + TABLE_NAME_DATA_ELEMENT_STRUCTURE + " ( " + 
            "dataelementid INTEGER NOT NULL PRIMARY KEY, " +
            "dataelementuid CHARACTER(255), " +
            "dataelementname VARCHAR(250), " +
            "datasetid INTEGER, " +
            "datasetuid CHARACTER(11), " +
            "datasetname VARCHAR(250), " +
            "periodtypeid INTEGER, " + 
            "periodtypename VARCHAR(250) )";
        
        log.info( "Create data element structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );        

        final String deUdInSql = "create unique index in_dataelementstructure_dataelementuid on _dataelementstructure(dataelementuid)";
        final String dsIdInSql = "create index in_dataelementstructure_datasetid on _dataelementstructure(datasetid)";
        final String dsUdInSql = "create index in_dataelementstructure_datasetuid on _dataelementstructure(datasetuid)";
        final String ptIdInSql = "create index in_dataelementstructure_periodtypeid on _dataelementstructure(periodtypeid)";
        
        jdbcTemplate.execute( deUdInSql );
        jdbcTemplate.execute( dsIdInSql );
        jdbcTemplate.execute( dsUdInSql );
        jdbcTemplate.execute( ptIdInSql );
    }
    
    // -------------------------------------------------------------------------
    // PeriodTable
    // -------------------------------------------------------------------------

    public void createDatePeriodStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATE_PERIOD_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        String quote = statementBuilder.getColumnQuote();
        
        String sql = "CREATE TABLE " + TABLE_NAME_DATE_PERIOD_STRUCTURE + " (dateperiod DATE NOT NULL PRIMARY KEY";
        
        for ( PeriodType periodType : PeriodType.PERIOD_TYPES )
        {
            sql += ", " + quote + periodType.getName().toLowerCase() + quote + " VARCHAR(15)";
        }
        
        sql += ")";
        
        log.info( "Create date period structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );
    }

    public void createPeriodStructure()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_PERIOD_STRUCTURE );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }

        String quote = statementBuilder.getColumnQuote();
        
        String sql = "CREATE TABLE " + TABLE_NAME_PERIOD_STRUCTURE + " (periodid INTEGER NOT NULL PRIMARY KEY, iso VARCHAR(15) NOT NULL, daysno INTEGER NOT NULL";
        
        for ( PeriodType periodType : PeriodType.PERIOD_TYPES )
        {
            sql += ", " + quote + periodType.getName().toLowerCase() + quote + " VARCHAR(15)";
        }
        
        sql += ")";
        
        log.info( "Create period structure SQL: " + sql );
        
        jdbcTemplate.execute( sql );

        final String isoInSql = "create unique index in_periodstructure_iso on _periodstructure(iso)";
        
        jdbcTemplate.execute( isoInSql );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboTable
    // -------------------------------------------------------------------------

    public void createAndGenerateDataElementCategoryOptionCombo()
    {
        try
        {
            jdbcTemplate.execute( "DROP TABLE IF EXISTS " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO );            
        }
        catch ( BadSqlGrammarException ex )
        {
            // Do nothing, table does not exist
        }
        
        final String create = "CREATE TABLE " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + 
            " (dataelementuid VARCHAR(255) NOT NULL, categoryoptioncombouid VARCHAR(11) NOT NULL)";
        
        jdbcTemplate.execute( create );
        
        log.info( "Create data element category option combo SQL: " + create );
        
        final String sql = 
            "insert into " + TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + " (dataelementuid, categoryoptioncombouid) " +
            "select de.uid as dataelementuid, coc.uid as categoryoptioncombouid " +
            "from dataelement de " +
            "join categorycombos_optioncombos cc on de.categorycomboid = cc.categorycomboid " +
            "join categoryoptioncombo coc on cc.categoryoptioncomboid = coc.categoryoptioncomboid";
        
        log.info( "Insert data element category option combo SQL: " + sql );
        
        jdbcTemplate.execute( sql );
        
        final String index = "CREATE INDEX dataelement_categoryoptioncombo ON " + 
            TABLE_NAME_DATA_ELEMENT_CATEGORY_OPTION_COMBO + " (dataelementuid, categoryoptioncombouid)";
        
        log.info( "Create data element category option combo index SQL: " + index );

        jdbcTemplate.execute( index );        
    }
}
