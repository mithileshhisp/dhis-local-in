package org.hisp.dhis.dxf2.filter;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.filter.ops.Op;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.PropertyIntrospectorService;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultFilterService implements FilterService
{
    @Autowired
    private ParserService parserService;

    @Autowired
    private PropertyIntrospectorService propertyIntrospectorService;

    @Override
    public <T extends IdentifiableObject> List<T> filterObjects( List<T> objects, List<String> filters )
    {
        if ( objects == null || objects.isEmpty() )
        {
            return Lists.newArrayList();
        }

        Filters parsed = parserService.parseObjectFilter( filters );

        List<T> list = Lists.newArrayList();

        for ( T object : objects )
        {
            if ( evaluateWithFilters( object, parsed ) )
            {
                list.add( object );
            }
        }

        return list;
    }

    @Override
    public <T extends IdentifiableObject> List<Object> filterProperties( List<T> objects, String include, String exclude )
    {
        List<Object> output = Lists.newArrayList();

        if ( objects.isEmpty() )
        {
            return output;
        }

        Map<String, Map> fieldMap = Maps.newHashMap();

        if ( include == null && exclude == null )
        {
            List<Property> properties = propertyIntrospectorService.getProperties( objects.get( 0 ).getClass() );

            for ( Property property : properties )
            {
                fieldMap.put( property.getName(), Maps.newHashMap() );
            }
        }
        else if ( include != null )
        {
            fieldMap = parserService.parsePropertyFilter( include );
        }
        else
        {
            List<Property> properties = propertyIntrospectorService.getProperties( objects.get( 0 ).getClass() );
            Map<String, Map> excludeMap = parserService.parsePropertyFilter( exclude );

            for ( Property property : properties )
            {
                if ( !excludeMap.containsKey( property.getName() ) )
                {
                    fieldMap.put( property.getName(), Maps.newHashMap() );
                }
            }
        }

        for ( Object object : objects )
        {
            output.add( buildObjectOutput( object, fieldMap ) );
        }

        return output;
    }

    @SuppressWarnings( "unchecked" )
    private Map<String, Object> buildObjectOutput( Object object, Map<String, Map> fieldMap )
    {
        if ( object == null )
        {
            return null;
        }

        Map<String, Object> output = Maps.newHashMap();
        Map<String, Property> propertiesMap = propertyIntrospectorService.getPropertiesMap( object.getClass() );

        for ( String key : fieldMap.keySet() )
        {
            if ( !propertiesMap.containsKey( key ) )
            {
                continue;
            }

            Map value = fieldMap.get( key );
            Property descriptor = propertiesMap.get( key );

            Object returned = ReflectionUtils.invokeMethod( object, descriptor.getGetterMethod() );

            if ( returned == null )
            {
                continue;
            }

            if ( value.isEmpty() )
            {
                if ( !descriptor.isIdentifiableObject() )
                {
                    output.put( key, returned );
                }
                else if ( !descriptor.isCollection() )
                {
                    Map<String, Object> properties = getIdentifiableObjectProperties( returned );
                    output.put( key, properties );
                }
                else
                {
                    List<Map<String, Object>> properties = getIdentifiableObjectCollectionProperties( returned );
                    output.put( key, properties );
                }
            }
            else
            {
                if ( descriptor.isCollection() )
                {
                    Collection<?> objects = (Collection<?>) returned;
                    List<Object> arrayList = Lists.newArrayList();
                    output.put( key, arrayList );

                    for ( Object obj : objects )
                    {
                        Map<String, Object> properties = buildObjectOutput( obj, value );

                        if ( !properties.isEmpty() )
                        {
                            arrayList.add( properties );
                        }
                    }
                }
                else
                {
                    Map<String, Object> properties = buildObjectOutput( returned, value );

                    if ( !properties.isEmpty() )
                    {
                        output.put( key, properties );
                    }
                }
            }
        }

        return output;
    }

    private List<Map<String, Object>> getIdentifiableObjectCollectionProperties( Object object )
    {
        List<String> fields = Lists.newArrayList( "id", "name", "code", "created", "lastUpdated" );
        return getIdentifiableObjectCollectionProperties( object, fields );
    }

    @SuppressWarnings( "unchecked" )
    private List<Map<String, Object>> getIdentifiableObjectCollectionProperties( Object object, List<String> fields )
    {
        List<Map<String, Object>> output = Lists.newArrayList();
        Collection<IdentifiableObject> identifiableObjects;

        try
        {
            identifiableObjects = (Collection<IdentifiableObject>) object;
        }
        catch ( ClassCastException ex )
        {
            ex.printStackTrace();
            return output;
        }

        for ( IdentifiableObject identifiableObject : identifiableObjects )
        {
            Map<String, Object> properties = getIdentifiableObjectProperties( identifiableObject, fields );
            output.add( properties );
        }

        return output;
    }

    private Map<String, Object> getIdentifiableObjectProperties( Object object )
    {
        List<String> fields = Lists.newArrayList( "id", "name", "code", "created", "lastUpdated" );
        return getIdentifiableObjectProperties( object, fields );
    }

    private Map<String, Object> getIdentifiableObjectProperties( Object object, List<String> fields )
    {
        Map<String, Object> idProps = Maps.newLinkedHashMap();
        Map<String, Property> propertiesMap = propertyIntrospectorService.getPropertiesMap( object.getClass() );

        for ( String field : fields )
        {
            Property descriptor = propertiesMap.get( field );

            if ( descriptor == null )
            {
                continue;
            }

            Object o = ReflectionUtils.invokeMethod( object, descriptor.getGetterMethod() );

            if ( o != null )
            {
                idProps.put( field, o );
            }
        }

        return idProps;
    }

    @SuppressWarnings( "unchecked" )
    private <T> boolean evaluateWithFilters( T object, Filters filters )
    {
        Map<String, Property> propertiesMap = propertyIntrospectorService.getPropertiesMap( object.getClass() );

        for ( String field : filters.getFilters().keySet() )
        {
            if ( !propertiesMap.containsKey( field ) )
            {
                System.err.println( "Skipping non-existent field: " + field );
                continue;
            }

            Property descriptor = propertiesMap.get( field );

            if ( descriptor == null )
            {
                continue;
            }

            Object value = ReflectionUtils.invokeMethod( object, descriptor.getGetterMethod() );

            Object filter = filters.getFilters().get( field );

            if ( FilterOps.class.isInstance( filter ) )
            {
                if ( evaluateFilterOps( value, (FilterOps) filter ) )
                {
                    return false;
                }
            }
            else
            {
                Map<String, Object> map = (Map<String, Object>) filters.getFilters().get( field );
                Filters f = new Filters();
                f.setFilters( map );

                if ( map.containsKey( "__self__" ) )
                {
                    if ( evaluateFilterOps( value, (FilterOps) map.get( "__self__" ) ) )
                    {
                        return false;
                    }

                    map.remove( "__self__" );
                }

                if ( !descriptor.isCollection() )
                {
                    if ( !evaluateWithFilters( value, f ) )
                    {
                        return false;
                    }
                }
                else
                {
                    Collection<?> objectCollection = (Collection<?>) value;

                    if ( objectCollection.isEmpty() )
                    {
                        return false;
                    }

                    boolean include = false;

                    for ( Object idObject : objectCollection )
                    {
                        if ( evaluateWithFilters( idObject, f ) )
                        {
                            include = true;
                        }
                    }

                    if ( !include )
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean evaluateFilterOps( Object value, FilterOps filterOps )
    {
        // filter through every operator treating multiple of same operator as OR
        for ( String operator : filterOps.getFilters().keySet() )
        {
            boolean include = false;

            List<Op> ops = filterOps.getFilters().get( operator );

            for ( Op op : ops )
            {
                switch ( op.evaluate( value ) )
                {
                    case INCLUDE:
                    {
                        include = true;
                    }
                }
            }

            if ( !include )
            {
                return true;
            }
        }

        return false;
    }
}
