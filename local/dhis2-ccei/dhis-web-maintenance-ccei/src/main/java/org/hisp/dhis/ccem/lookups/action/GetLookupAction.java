package org.hisp.dhis.ccem.lookups.action;

import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;

import com.opensymphony.xwork2.Action;

public class GetLookupAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Lookup lookup;

    public Lookup getLookup()
    {
        return lookup;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        lookup = lookupService.getLookup( id );

        return SUCCESS;
    }

}
