package org.hisp.dhis.pbf.lookup.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;
import org.hisp.dhis.system.paging.Paging;

import com.opensymphony.xwork2.Action;

public class GetAllLookupsAction
    extends ActionPagingSupport<Lookup>
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<Lookup> lookups;

    public List<Lookup> getLookups()
    {
        return lookups;
    }

    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        lookups = new ArrayList<Lookup>( lookupService.getAllLookups() );

        if ( isNotBlank( key ) )
        {
            lookupService.searchLookupByName( lookups, key );
        }

        this.paging = createPaging( lookups.size() );
        lookups = getBlockElement( lookups, paging.getStartPos(), paging.getPageSize() );

        Collections.sort( lookups );

        return SUCCESS;

    }
}
