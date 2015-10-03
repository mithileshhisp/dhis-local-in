package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class EquipmentMoveAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    /*
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    */
    
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
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        //organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Equipment equipment = equipmentService.getEquipment( equipmentID );
        
        //System.out.println(" organisationUnit : "+ organisationUnit.getName() + " -- EquipmentID : " + equipment.getId() );
        
        equipment.setOrganisationUnit( organisationUnit );
        
        equipmentService.updateEquipment( equipment );
        
        
        return SUCCESS;
    }

}


