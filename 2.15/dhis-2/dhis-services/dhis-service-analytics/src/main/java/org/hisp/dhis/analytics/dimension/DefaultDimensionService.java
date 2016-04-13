package org.hisp.dhis.analytics.dimension;

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

import org.hisp.dhis.acl.AclService;
import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.common.DimensionType;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.DimensionalObjectUtils;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryDimension;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementOperandService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriodEnum;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.system.util.UniqueArrayList;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeDimension;
import org.hisp.dhis.trackedentity.TrackedEntityDataElementDimension;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.common.DimensionType.*;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.organisationunit.OrganisationUnit.*;

/**
 * @author Lars Helge Overland
 */
public class DefaultDimensionService
    implements DimensionService
{
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataElementOperandService operandService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private AclService aclService;

    @Autowired
    private CurrentUserService currentUserService;

    //--------------------------------------------------------------------------
    // DimensionService implementation
    //--------------------------------------------------------------------------

    @Override
    public DimensionalObject getDimension( String uid )
    {
        DataElementCategory cat = identifiableObjectManager.get( DataElementCategory.class, uid );

        if ( cat != null )
        {
            return cat;
        }

        DataElementGroupSet degs = identifiableObjectManager.get( DataElementGroupSet.class, uid );

        if ( degs != null )
        {
            return degs;
        }

        OrganisationUnitGroupSet ougs = identifiableObjectManager.get( OrganisationUnitGroupSet.class, uid );

        if ( ougs != null )
        {
            return ougs;
        }

        CategoryOptionGroupSet cogs = identifiableObjectManager.get( CategoryOptionGroupSet.class, uid );

        if ( cogs != null )
        {
            return cogs;
        }
        
        TrackedEntityAttribute tea = identifiableObjectManager.get( TrackedEntityAttribute.class, uid );
        
        if ( tea != null )
        {
            return tea;
        }
        
        DataElement de = identifiableObjectManager.get( DataElement.class, uid );
        
        if ( de != null )
        {
            return de;
        }
        
        return null;
    }

    public List<NameableObject> getCanReadDimensionItems( String uid )
    {
        DimensionalObject dimension = getDimension( uid );

        List<NameableObject> items = new ArrayList<NameableObject>();

        if ( dimension != null && dimension.getItems() != null )
        {            
            User user = currentUserService.getCurrentUser();

            items.addAll( filterCanRead( user, dimension.getItems() ) );
        }

        return items;
    }
    
    public <T extends IdentifiableObject> List<T> filterCanRead( User user, List<T> objects )
    {        
        List<T> list = new ArrayList<T>( objects );
        Iterator<T> iterator = list.iterator();
        
        while ( iterator.hasNext() )
        {
            T object = iterator.next();
            
            if ( !aclService.canRead( user, object ) )
            {
                iterator.remove();
            }
        }
        
        return list;
    }
        
    public DimensionType getDimensionType( String uid )
    {
        DataElementCategory cat = identifiableObjectManager.get( DataElementCategory.class, uid );

        if ( cat != null )
        {
            return DimensionType.CATEGORY;
        }

        DataElementGroupSet degs = identifiableObjectManager.get( DataElementGroupSet.class, uid );

        if ( degs != null )
        {
            return DimensionType.DATAELEMENT_GROUPSET;
        }

        OrganisationUnitGroupSet ougs = identifiableObjectManager.get( OrganisationUnitGroupSet.class, uid );

        if ( ougs != null )
        {
            return DimensionType.ORGANISATIONUNIT_GROUPSET;
        }

        CategoryOptionGroupSet cogs = identifiableObjectManager.get( CategoryOptionGroupSet.class, uid );

        if ( cogs != null )
        {
            return DimensionType.CATEGORYOPTION_GROUPSET;
        }

        TrackedEntityAttribute tea = identifiableObjectManager.get( TrackedEntityAttribute.class, uid );
        
        if ( tea != null )
        {
            return DimensionType.TRACKED_ENTITY_ATTRIBUTE;
        }
        
        DataElement de = identifiableObjectManager.get( DataElement.class, uid );
        
        if ( de != null )
        {
            return DimensionType.TRACKED_ENTITY_DATAELEMENT;
        }

        final Map<String, DimensionType> dimObjectTypeMap = new HashMap<String, DimensionType>();

        dimObjectTypeMap.put( DimensionalObject.DATA_X_DIM_ID, DimensionType.DATA_X );
        dimObjectTypeMap.put( DimensionalObject.INDICATOR_DIM_ID, DimensionType.INDICATOR );
        dimObjectTypeMap.put( DimensionalObject.DATAELEMENT_DIM_ID, DimensionType.DATAELEMENT );
        dimObjectTypeMap.put( DimensionalObject.DATASET_DIM_ID, DimensionType.DATASET );
        dimObjectTypeMap.put( DimensionalObject.DATAELEMENT_OPERAND_ID, DimensionType.DATAELEMENT_OPERAND );
        dimObjectTypeMap.put( DimensionalObject.PERIOD_DIM_ID, DimensionType.PERIOD );
        dimObjectTypeMap.put( DimensionalObject.ORGUNIT_DIM_ID, DimensionType.ORGANISATIONUNIT );

        return dimObjectTypeMap.get( uid );
    }

    @Override
    public List<DimensionalObject> getAllDimensions()
    {
        Collection<DataElementCategory> dcs = categoryService.getDataDimensionDataElementCategories();
        Collection<CategoryOptionGroupSet> cogs = categoryService.getDataDimensionCategoryOptionGroupSets();
        Collection<DataElementGroupSet> degs = dataElementService.getDataDimensionDataElementGroupSets();
        Collection<OrganisationUnitGroupSet> ougs = organisationUnitGroupService.getDataDimensionOrganisationUnitGroupSets();

        final List<DimensionalObject> dimensions = new ArrayList<DimensionalObject>();

        dimensions.addAll( dcs );
        dimensions.addAll( cogs );
        dimensions.addAll( degs );
        dimensions.addAll( ougs );

        User user = currentUserService.getCurrentUser();
        
        return filterCanRead( user, dimensions );
    }

    @Override
    public void mergeAnalyticalObject( BaseAnalyticalObject object )
    {
        if ( object != null )
        {
            object.clear();

            if ( object.getUser() != null )
            {
                object.setUser( identifiableObjectManager.get( User.class, object.getUser().getUid() ) );
            }
            else
            {
                object.setUser( currentUserService.getCurrentUser() );
            }

            mergeDimensionalObjects( object, object.getColumns() );
            mergeDimensionalObjects( object, object.getRows() );
            mergeDimensionalObjects( object, object.getFilters() );
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    /**
     * Sets persistent objects for dimensional associations on the given
     * BaseAnalyticalObject based on the given list of transient DimensionalObjects.
     * <p/>
     * Relative periods represented by enums are converted into a RelativePeriods
     * object. User organisation units represented by enums are converted and
     * represented by the user organisation unit persisted properties on the
     * BaseAnalyticalObject.
     *
     * @param object     the BaseAnalyticalObject to merge.
     * @param dimensions the
     */
    private void mergeDimensionalObjects( BaseAnalyticalObject object, List<DimensionalObject> dimensions )
    {
        if ( object == null || dimensions == null )
        {
            return;
        }

        for ( DimensionalObject dimension : dimensions )
        {
            DimensionType type = getDimensionType( dimension.getDimension() );

            String dimensionId = dimension.getDimension();

            List<NameableObject> items = dimension.getItems();

            if ( items != null )
            {
                List<String> uids = getUids( items );

                if ( INDICATOR.equals( type ) )
                {
                    object.getIndicators().addAll( identifiableObjectManager.getByUid( Indicator.class, uids ) );
                }
                else if ( DATAELEMENT.equals( type ) )
                {
                    object.getDataElements().addAll( identifiableObjectManager.getByUid( DataElement.class, uids ) );
                }
                else if ( DATAELEMENT_OPERAND.equals( type ) )
                {
                    object.getDataElementOperands().addAll( operandService.getDataElementOperandsByUid( uids ) );
                }
                else if ( DATASET.equals( type ) )
                {
                    object.getDataSets().addAll( identifiableObjectManager.getByUid( DataSet.class, uids ) );
                }
                else if ( PERIOD.equals( type ) )
                {
                    List<RelativePeriodEnum> enums = new ArrayList<RelativePeriodEnum>();
                    List<Period> periods = new UniqueArrayList<Period>();

                    for ( String isoPeriod : uids )
                    {
                        if ( RelativePeriodEnum.contains( isoPeriod ) )
                        {
                            enums.add( RelativePeriodEnum.valueOf( isoPeriod ) );
                        }
                        else
                        {
                            Period period = PeriodType.getPeriodFromIsoString( isoPeriod );

                            if ( period != null )
                            {
                                periods.add( period );
                            }
                        }
                    }

                    object.setRelatives( new RelativePeriods().setRelativePeriodsFromEnums( enums ) );
                    object.setPeriods( periodService.reloadPeriods( new ArrayList<Period>( periods ) ) );
                }
                else if ( ORGANISATIONUNIT.equals( type ) )
                {
                    for ( String ou : uids )
                    {
                        if ( KEY_USER_ORGUNIT.equals( ou ) )
                        {
                            object.setUserOrganisationUnit( true );
                        }
                        else if ( KEY_USER_ORGUNIT_CHILDREN.equals( ou ) )
                        {
                            object.setUserOrganisationUnitChildren( true );
                        }
                        else if ( KEY_USER_ORGUNIT_GRANDCHILDREN.equals( ou ) )
                        {
                            object.setUserOrganisationUnitGrandChildren( true );
                        }
                        else if ( ou != null && ou.startsWith( KEY_LEVEL ) )
                        {
                            int level = DimensionalObjectUtils.getLevelFromLevelParam( ou );

                            if ( level > 0 )
                            {
                                object.getOrganisationUnitLevels().add( level );
                            }
                        }
                        else if ( ou != null && ou.startsWith( KEY_ORGUNIT_GROUP ) )
                        {
                            String uid = DimensionalObjectUtils.getUidFromOrgUnitGroupParam( ou );

                            OrganisationUnitGroup group = identifiableObjectManager.get( OrganisationUnitGroup.class, uid );

                            if ( group != null )
                            {
                                object.getItemOrganisationUnitGroups().add( group );
                            }
                        }
                        else
                        {
                            OrganisationUnit unit = identifiableObjectManager.get( OrganisationUnit.class, ou );

                            if ( unit != null )
                            {
                                object.getOrganisationUnits().add( unit );
                            }
                        }
                    }
                }
                else if ( CATEGORY.equals( type ) )
                {
                    DataElementCategoryDimension categoryDimension = new DataElementCategoryDimension();
                    categoryDimension.setDimension( categoryService.getDataElementCategory( dimensionId ) );
                    categoryDimension.getItems().addAll( categoryService.getDataElementCategoryOptionsByUid( uids ) );

                    object.getCategoryDimensions().add( categoryDimension );
                }
                else if ( DATAELEMENT_GROUPSET.equals( type ) )
                {
                    object.getDataElementGroups().addAll( identifiableObjectManager.getByUid( DataElementGroup.class, uids ) );
                }
                else if ( ORGANISATIONUNIT_GROUPSET.equals( type ) )
                {
                    object.getOrganisationUnitGroups().addAll( identifiableObjectManager.getByUid( OrganisationUnitGroup.class, uids ) );
                }
                else if ( CATEGORYOPTION_GROUPSET.equals( type ) )
                {
                    object.getCategoryOptionGroups().addAll( identifiableObjectManager.getByUid( CategoryOptionGroup.class, uids ) );
                }
                else if ( TRACKED_ENTITY_ATTRIBUTE.equals( type ) )
                {
                    TrackedEntityAttributeDimension attributeDimension = new TrackedEntityAttributeDimension();
                    attributeDimension.setAttribute( identifiableObjectManager.get( TrackedEntityAttribute.class, dimensionId ) );
                    attributeDimension.setFilter( dimension.getFilter() );
                    
                    object.getAttributeDimensions().add( attributeDimension );
                }
                else if ( TRACKED_ENTITY_DATAELEMENT.equals( type ) )
                {
                    TrackedEntityDataElementDimension dataElementDimension = new TrackedEntityDataElementDimension();
                    dataElementDimension.setDataElement( identifiableObjectManager.get( DataElement.class, dimensionId ) );
                    dataElementDimension.setFilter( dimension.getFilter() );
                    
                    object.getDataElementDimensions().add( dataElementDimension );
                }
            }
        }
    }
}
