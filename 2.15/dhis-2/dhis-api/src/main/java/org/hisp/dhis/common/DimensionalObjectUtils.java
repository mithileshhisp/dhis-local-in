package org.hisp.dhis.common;

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

import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIMS;
import static org.hisp.dhis.common.DimensionalObject.DATA_X_DIM_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Lars Helge Overland
 */
public class DimensionalObjectUtils
{
    public static final String DIMENSION_NAME_SEP = ":";
    public static final String OPTION_SEP = ";";
    public static final String ITEM_SEP = "-";
    
    private static final Pattern INT_PATTERN = Pattern.compile( "^(0|-?[1-9]\\d*)$" );
    
    /**
     * Converts a concrete dimensional class identifier to a dimension identifier.
     * 
     * @param identifier the identifier.
     * @return a dimension identifier.
     */
    public static String toDimension( String identifier )
    {
        if ( DATA_X_DIMS.contains( identifier ) )
        {
            return DATA_X_DIM_ID;
        }
        
        return identifier;
    }
    
    /**
     * Creates a unique list of dimension identifiers based on the given list
     * of DimensionalObjects.
     * 
     * @param dimensions the list of DimensionalObjects.
     * @return list of dimension identifiers.
     */
    public static List<String> getUniqueDimensions( List<DimensionalObject> dimensions )
    {
        List<String> dims = new ArrayList<String>();
        
        if ( dimensions != null )
        {
            for ( DimensionalObject dimension : dimensions )
            {
                String dim = toDimension( dimension.getDimension() );
                
                if ( dim != null && !dims.contains( dim ) )
                {
                    dims.add( dim );
                }
            }
        }
        
        return dims;
    }

    /**
     * Creates a list of dimension identifiers based on the given list of 
     * DimensionalObjects.
     * 
     * @param dimensions the list of DimensionalObjects.
     * @return list of dimension identifiers.
     */
    public static List<String> getDimensions( List<DimensionalObject> dimensions )
    {
        List<String> dims = new ArrayList<String>();
        
        if ( dimensions != null )
        {
            for ( DimensionalObject dimension : dimensions )
            {
                dims.add( dimension.getDimension() );
            }
        }
        
        return dims;
    }

    /**
     * Creates a two-dimensional array of dimension items based on the list of
     * DimensionalObjects. I.e. the list of items of each DimensionalObject is
     * converted to an array and inserted into the outer array in the same order.
     * 
     * @param dimensions the list of DimensionalObjects.
     * @return a two-dimensional array of NameableObjects.
     */
    public static NameableObject[][] getItemArray( List<DimensionalObject> dimensions )
    {
        List<NameableObject[]> arrays = new ArrayList<NameableObject[]>();
        
        for ( DimensionalObject dimension : dimensions )
        {
            arrays.add( dimension.getItems().toArray( new NameableObject[0] ) );
        }
        
        return arrays.toArray( new NameableObject[0][] );
    }
    
    /**
     * Creates a map based on the given array of elements, where each pair of
     * elements are put on them map as a key-value pair.
     * 
     * @param elements the elements to put on the map.
     * @return a map.
     */
    public static <T> Map<T, T> asMap( T... elements )
    {
        Map<T, T> map = new HashMap<T, T>();
        
        if ( elements != null && ( elements.length % 2 == 0 ) )
        {
            for ( int i = 0; i < elements.length; i += 2 )
            {
                map.put( elements[i], elements[i+1] );
            }
        }
        
        return map;
    }

    /**
     * Retrieves the dimension name from the given string. Returns the part of
     * the string preceding the dimension name separator, or the whole string if
     * the separator is not present.
     */
    public static String getDimensionFromParam( String param )
    {
        if ( param == null )
        {
            return null;
        }
        
        return param.split( DIMENSION_NAME_SEP ).length > 0 ? param.split( DIMENSION_NAME_SEP )[0] : param;
    }
    
    /**
     * Retrieves the dimension options from the given string. Looks for the part
     * succeeding the dimension name separator, if exists, splits the string part
     * on the option separator and returns the resulting values. If the dimension
     * name separator does not exist an empty list is returned, indicating that
     * all dimension options should be used.
     */
    public static List<String> getDimensionItemsFromParam( String param )
    {
        if ( param == null )
        {
            return null;
        }
        
        if ( param.split( DIMENSION_NAME_SEP ).length > 1 )
        {
            return new ArrayList<String>( Arrays.asList( param.split( DIMENSION_NAME_SEP )[1].split( OPTION_SEP ) ) );
        }
        
        return new ArrayList<String>();
    }
    
    /**
     * Splits the given string on the ; character and returns the items in a 
     * list. Returns null if the given string is null.
     */
    public static List<String> getDimensionsFromParam( String param )
    {
        if ( param == null )
        {
            return null;
        }
        
        return new ArrayList<String>( Arrays.asList( param.split( OPTION_SEP ) ) );
    }

    /**
     * Indicates whether at least one of the given dimenions has at least one
     * item.
     */
    public static boolean anyDimensionHasItems( Collection<DimensionalObject> dimensions )
    {
        if ( dimensions == null || dimensions.isEmpty() )
        {
            return false;
        }
        
        for ( DimensionalObject dim : dimensions )
        {
            if ( dim.hasItems() )
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Retrieves the level from a level parameter string, which is on the format
     * LEVEL-<level>-<item> .
     */
    public static int getLevelFromLevelParam( String param )
    {
        if ( param == null )   
        {
            return 0;
        }
        
        String[] split = param.split( ITEM_SEP );
        
        if ( split.length > 1 && INT_PATTERN.matcher( split[1] ).matches() )
        {
            return Integer.parseInt( split[1] );
        }
        
        return 0;
    }
    
    /**
     * Retrieves the uid from an org unit group parameter string, which is on
     * the format OU_GROUP-<uid> .
     */
    public static String getUidFromOrgUnitGroupParam( String param )
    {
        if ( param == null )
        {
            return null;
        }
        
        String[] split = param.split( ITEM_SEP );
        
        if ( split.length > 1 && split[1] != null )
        {
            return String.valueOf( split[1] );
        }
        
        return null;
    }
}
