package org.hisp.dhis.api.utils;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class InputUtils
{
    @Autowired
    private DataElementCategoryService categoryService;
    
    /**
     * Validates and retrieves the attribute option combo. 409 conflict as status
     * code along with a textual message will be set on the response in case of
     * invalid input.
     * 
     * @param response the servlet response.
     * @param cc the category combo identifier.
     * @param cp the category and option query string.
     * @return the attribute option combo identified from the given input, or null
     *         if the input was invalid.
     */
    public DataElementCategoryOptionCombo getAttributeOptionCombo( HttpServletResponse response, String cc, String cp )
    {
        List<String> opts = ContextUtils.getQueryParamValues( cp );

        // ---------------------------------------------------------------------
        // Attribute category combo validation
        // ---------------------------------------------------------------------

        if ( ( cc == null && opts != null || ( cc != null && opts == null ) ) )
        {
            ContextUtils.conflictResponse( response, "Both or none of category combination and category options must be present" );
            return null;
        }

        DataElementCategoryCombo categoryCombo = null;
        
        if ( cc != null && ( categoryCombo = categoryService.getDataElementCategoryCombo( cc ) ) == null )
        {
            ContextUtils.conflictResponse( response, "Illegal category combo identifier: " + cc );
            return null;
        }

        // ---------------------------------------------------------------------
        // Attribute category options validation
        // ---------------------------------------------------------------------

        DataElementCategoryOptionCombo attributeOptionCombo = null;

        if ( opts != null )
        {
            Set<DataElementCategoryOption> categoryOptions = new HashSet<DataElementCategoryOption>();

            for ( String id : opts )
            {
                DataElementCategoryOption categoryOption = categoryService.getDataElementCategoryOption( id );
                
                if ( categoryOption == null )
                {
                    ContextUtils.conflictResponse( response, "Illegal category option identifier: " + id );
                    return null;
                }
                
                categoryOptions.add( categoryOption );
            }
            
            attributeOptionCombo = categoryService.getDataElementCategoryOptionCombo( categoryCombo, categoryOptions );
            
            if ( attributeOptionCombo == null )
            {
                ContextUtils.conflictResponse( response, "Attribute option combo does not exist for given category combo and category options" );
                return null;
            }
        }

        if ( attributeOptionCombo == null )
        {
            attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        }
        
        if ( attributeOptionCombo == null )
        {
            ContextUtils.conflictResponse( response, "Default attribute option combo does not exist" );
            return null;
        }
        
        return attributeOptionCombo;
    }

    /**
     * Validates and retrieves a single category option group. 409 conflict as
     * status code along with a textual message will be set on the response in
     * case of invalid input.
     *
     * @param response the servlet response.
     * @param cog the category option group query string.
     * @return the set of category option groups, null if the input was
     *         missing or an empty set if the input was invalid.
     */
    public Set<CategoryOptionGroup> getAttributeOptionGroup( HttpServletResponse response, String cog )
    {
        Set<String> groups = new HashSet<String>();

        groups.add(cog);

        return getAttributeOptionGroups( response, groups );
    }

    /**
     * Validates and retrieves a set of category option groups. 409 conflict as
     * status code along with a textual message will be set on the response in
     * case of invalid input.
     *
     * @param response the servlet response.
     * @param cog the category option group query string set.
     * @return the set of category option groups, null if the input was
     *         missing or an empty set if the input was invalid.
     */
    public Set<CategoryOptionGroup> getAttributeOptionGroups( HttpServletResponse response, Set<String> cog )
    {
        Set<CategoryOptionGroup> groups = null;

        if ( cog != null )
        {
            groups = new HashSet<CategoryOptionGroup>();

            for ( String id : cog )
            {
                if ( "undefined".compareTo( id ) != 0 )
                {
                    CategoryOptionGroup categoryOptionGroup = categoryService.getCategoryOptionGroup( id );

                    if ( categoryOptionGroup == null )
                    {
                        ContextUtils.conflictResponse( response, "Illegal category option group identifier: " + cog );
                        return null;
                    }

                    groups.add( categoryOptionGroup );
                }
            }
        }

        return groups;
    }

    /**
     * Validates and retrieves a set of category options. 409 conflict as
     * status code along with a textual message will be set on the response in
     * case of invalid input.
     *
     * @param response the servlet response.
     * @param cp the category option query string.
     * @return the set of category option groups, null if the input was
     *         missing or an empty set if the input was invalid.
     */
    public Set<DataElementCategoryOption> getAttributeOptions( HttpServletResponse response, String cp )
    {
        Set<DataElementCategoryOption> options = null;

        if ( cp != null )
        {
            List<String> opts = ContextUtils.getQueryParamValues( cp );

            options = new HashSet<DataElementCategoryOption>();

            for ( String id : opts )
            {
                DataElementCategoryOption categoryOption = categoryService.getDataElementCategoryOption( id );

                if ( categoryOption == null )
                {
                    ContextUtils.conflictResponse( response, "Illegal category option identifier: " + id );
                    return null;
                }

                options.add( categoryOption );
            }
        }

        return options;
    }
}
