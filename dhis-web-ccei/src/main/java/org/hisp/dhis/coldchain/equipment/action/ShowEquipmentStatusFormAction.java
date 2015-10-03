package org.hisp.dhis.coldchain.equipment.action;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ShowEquipmentStatusFormAction implements Action
{
    public static final String WORKING_STATUS_OPTION_SET = "Working Status";//43.0
    
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    @Autowired
    private LookupService lookupService;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    public Integer getEquipmentId()
    {
        return equipmentId;
    }
    
    private String type;
    
    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    private Equipment equipment;
    
    public Equipment getEquipment()
    {
        return equipment;
    }

    private OptionSet optionSet;
    
    public OptionSet getOptionSet()
    {
        return optionSet;
    }
    
    private OptionSet utilizationOptions;
    
    public OptionSet getUtilizationOptions()
    {
        return utilizationOptions;
    }

    private OptionSet workingStatusOptions;
    
    public OptionSet getWorkingStatusOptions()
    {
        return workingStatusOptions;
    }
    
    private OptionSet reasonNotWorkingOptions;
    
    public OptionSet getReasonNotWorkingOptions()
    {
        return reasonNotWorkingOptions;
    }
    
    private OptionSet serviceTypeOptions;
    
    public OptionSet getServiceTypeOptions()
    {
        return serviceTypeOptions;
    }

    private OptionSet faultTypeOptions;
    
    public OptionSet getFaultTypeOptions()
    {
        return faultTypeOptions;
    }

    private OptionSet partsReplacedOptions;
    
    public OptionSet getPartsReplacedOptions()
    {
        return partsReplacedOptions;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        if ( equipmentId != null )
        {
            equipment = equipmentService.getEquipment( equipmentId );
        }
        
        Constant optionSetConstant = constantService.getConstantByName( WORKING_STATUS_OPTION_SET );
        
        if( optionSetConstant != null )
        {
            optionSet = new OptionSet();
            optionSet = optionService.getOptionSet( (int) optionSetConstant.getValue() );
        }
        else
        {
            System.out.println( "constant for working status option set is not defined");
        }
        
        Lookup lookup = lookupService.getLookupByName( Lookup.CCEI_UTILIZATION_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            utilizationOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }
        
        lookup = lookupService.getLookupByName( Lookup.CCEI_WORKING_STATUS_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            workingStatusOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }
        
        lookup = lookupService.getLookupByName( Lookup.CCEI_REASON_NOT_WORKING_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            reasonNotWorkingOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }
        
        lookup = lookupService.getLookupByName( Lookup.CCEI_SERVICE_TYPE_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            serviceTypeOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }
        
        lookup = lookupService.getLookupByName( Lookup.CCEI_FAULT_TYPE_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            faultTypeOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }

        lookup = lookupService.getLookupByName( Lookup.CCEI_PARTS_REPLACED_OPTIONSET );
        
        if( lookup.getValue() != null )
        {
            partsReplacedOptions = optionService.getOptionSet( Integer.parseInt( lookup.getValue() ) );
        }

        //equipment.getOrganisationUnit().getName();
        //equipment.getModel().getName();
        
        return SUCCESS;
    }
}
