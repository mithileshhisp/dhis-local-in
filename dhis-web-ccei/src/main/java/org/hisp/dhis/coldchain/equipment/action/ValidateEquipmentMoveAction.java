package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidateEquipmentMoveAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Integer equipmentID;
    
    public void setEquipmentID( Integer equipmentID )
    {
        this.equipmentID = equipmentID;
    }
    
    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Equipment equipment = equipmentService.getEquipment( equipmentID );
        
        if ( organisationUnit == null )
        {
            message = i18n.getString( "please_select_new_organisationunit_to_move" );

            return INPUT;
        }
        
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) ); 
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );                              
       
        if ( ouGroup != null )
        {
            if ( !ouGroup.getMembers().contains( organisationUnit ) )
            {
                message = i18n.getString( "selected_organisationunit_not_regestring_any_equipments" );

                return INPUT;
            }
        }
        
        if ( organisationUnit.getId() == equipment.getOrganisationUnit().getId() )
        {
            message = i18n.getString( "please_select_new_organisationunit_to_move" );

            return INPUT;
        }
        
        //System.out.println( " organisationUnit : "+ organisationUnit.getName() + " -- EquipmentID : " + equipment.getId() + " -- OrganisationUnit Group Name : " + ouGroup.getName() );
        
        return SUCCESS;
    }

}

