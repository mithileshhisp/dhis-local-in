package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusHistoryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }
    
    private List<EquipmentStatus> equipmentRepairHistory;
    
    public List<EquipmentStatus> getEquipmentRepairHistory()
    {
        return equipmentRepairHistory;
    }

    private List<EquipmentStatus> equipmentStatusHistory;
    
    public List<EquipmentStatus> getEquipmentStatusHistory()
    {
        return equipmentStatusHistory;
    }
    
    private List<EquipmentStatus> equipmentServiceHistory;
    
    public List<EquipmentStatus> getEquipmentServiceHistory()
    {
        return equipmentServiceHistory;
    }

    private Equipment equipment;
    
    public Equipment getEquipment()
    {
        return equipment;
    }



    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("inside ShowEquipmentStatusHistoryFormAction ");
        equipment = equipmentService.getEquipment( equipmentId );
         
        //equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) );
        
        //equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistoryDescOrder( equipment ) );
        equipmentRepairHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistoryByTypeDescOrder( equipment, "repair" ) );
        equipmentServiceHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistoryByTypeDescOrder( equipment, "service" ) );
        equipmentStatusHistory = new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistoryByTypeDescOrder( equipment, "status" ) );

        /*
        for( EquipmentStatus equipmentStatus  : equipmentRepairHistory)
        {
            //equipmentStatus.getServiceType().getPartsReplaced().getFaultTypes()
        }
        */
        
        return SUCCESS;
    }
}
