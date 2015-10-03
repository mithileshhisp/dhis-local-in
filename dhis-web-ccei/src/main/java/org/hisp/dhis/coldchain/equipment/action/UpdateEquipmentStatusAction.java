package org.hisp.dhis.coldchain.equipment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class UpdateEquipmentStatusAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    /*
    private EquipmentAttributeValueService equipmentAttributeValueService;
    
    public void setEquipmentAttributeValueService( EquipmentAttributeValueService equipmentAttributeValueService )
    {
        this.equipmentAttributeValueService = equipmentAttributeValueService;
    }
    */
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output Setters & Getters
    // -------------------------------------------------------------------------
    
    private Integer equipmentId;
    
    public void setEquipmentId( Integer equipmentId )
    {
        this.equipmentId = equipmentId;
    }

    /*
    private String reportingDate;
    
    public void setReportingDate( String reportingDate )
    {
        this.reportingDate = reportingDate;
    }
    
    private String status;
    
    public void setStatus( String status )
    {
        this.status = status;
    }   
    */
    
    private String dateOfUpdation;
    
    public void setDateOfUpdation( String dateOfUpdation )
    {
        this.dateOfUpdation = dateOfUpdation;
    }
    
    private String description;
    
    public void setDescription( String description )
    {
        this.description = description;
    }
    
    private String dateOfService;
    
    public void setDateOfService( String dateOfService )
    {
        this.dateOfService = dateOfService;
    }
    
    private String serviceReportingDate;
    
    public void setServiceReportingDate( String serviceReportingDate )
    {
        this.serviceReportingDate = serviceReportingDate;
    }
    
    private String type;
    
    public void setType( String type )
    {
        this.type = type;
    }
    
    private String workingStatus;
    
    public void setWorkingStatus( String workingStatus )
    {
        this.workingStatus = workingStatus;
    }

    private String utilization;
    
    public void setUtilization( String utilization )
    {
        this.utilization = utilization;
    }
    
    private String reasonNotWorking;
    
    public void setReasonNotWorking( String reasonNotWorking )
    {
        this.reasonNotWorking = reasonNotWorking;
    }
    
    /*
    private String serviceType;
    
    public void setServiceType( String serviceType )
    {
        this.serviceType = serviceType;
    }
    */
    
    private List<String> serviceTypes;
    
    public void setServiceTypes( List<String> serviceTypes )
    {
        this.serviceTypes = serviceTypes;
    }

    private List<String> faultTypes;
    
    public void setFaultTypes( List<String> faultTypes )
    {
        this.faultTypes = faultTypes;
    }
    
    private List<String> partsReplaced;
    
    public void setPartsReplaced( List<String> partsReplaced )
    {
        this.partsReplaced = partsReplaced;
    }


    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        
       // EquipmentType equipmentType = equipment.getEquipmentType();
        
        List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        
        for( EquipmentType_Attribute equipmentType_Attribute : equipment.getEquipmentType().getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        /*
        EquipmentAttributeValue equipmentAttributeValue = new EquipmentAttributeValue();
        
        for ( EquipmentTypeAttribute equipmentTypeAttribute : equipmentTypeAttributes )
        {
            if( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( equipmentTypeAttribute.getValueType() ) )
            {
                if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( equipmentTypeAttribute.getDescription() ) )
                {
                    //System.out.println( "Inside Working Status" );
                    equipmentAttributeValue = equipmentAttributeValueService.getEquipmentAttributeValue( equipment, equipmentTypeAttribute );
                    
                    if( equipmentAttributeValue == null )
                    {
                        equipmentAttributeValue = new EquipmentAttributeValue();
                        
                        if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ))
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                        }
                        
                        else if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE ))
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE.trim() );
                        }
                        else
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                        }
                        
                        equipmentAttributeValueService.addEquipmentAttributeValue( equipmentAttributeValue );
                    }
                    else
                    {
                        if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ))
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                        }
                        
                        else if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE ))
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE.trim() );
                        }
                        else
                        {
                            equipmentAttributeValue.setValue( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                        }
                        equipmentAttributeValueService.updateEquipmentAttributeValue( equipmentAttributeValue );
                    }
                    
                }
            }
        }
        */
        
       
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        
        if( type.equalsIgnoreCase( "status" ) )
        {
            EquipmentStatus equipmentStatus = new EquipmentStatus();
            
            if( workingStatus != null && !workingStatus.trim().equals( "-1" ) )
            {
                equipmentStatus.setStatus( workingStatus );
                
                if( workingStatus.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ))
                {
                    equipment.setWorking( true );
                }
                
                else
                {
                    equipment.setWorking( false );
                    equipmentService.updateEquipment( equipment );
                }                
            }
            
            if( utilization != null && !utilization.trim().equals( "-1" ) )
            {
                equipmentStatus.setUtilization( utilization );
            }
            
            if( reasonNotWorking != null && !reasonNotWorking.trim().equals( "-1" ) )
            {
                equipmentStatus.setReasonNotWorking( reasonNotWorking );
            }
            
            /*
            if( serviceType != null && !serviceType.trim().equals( "-1" ) )
            {
                equipmentStatus.setServiceType( serviceType );
            }
            */
            
            //equipmentStatus.setStatus( status );
            equipmentStatus.setType( type );
            equipmentStatus.setReportingDate( format.parseDate( dateOfUpdation.trim() ) );
            equipmentStatus.setUpdationDate( format.parseDate( dateOfUpdation.trim() ) );
            
            String storedBy = currentUserService.getCurrentUsername();
            
            equipmentStatus.setDescription( description );
            
            equipmentStatus.setEquipment( equipment );
            
            equipmentStatus.setStoredBy( storedBy );

            equipmentStatusService.addEquipmentStatus( equipmentStatus );
            
            //equipment.getEquipmentStatus().getUpdationDate();
            
            if( equipment.getEquipmentStatus().getUpdationDate() != null )
            {
                Date updationDate = sdf.parse( dateOfUpdation.trim() );
                if(  updationDate.compareTo( equipment.getEquipmentStatus().getUpdationDate() ) >= 0 )
                {
                    equipment.setEquipmentStatus( equipmentStatus );
                    equipmentService.updateEquipment( equipment );
                }
            }
            
        }        
        else if( type.equalsIgnoreCase( "service" ))
        {
            EquipmentStatus equipmentStatus = new EquipmentStatus();
            
            if( workingStatus != null && !workingStatus.trim().equals( "-1" ) )
            {
                equipmentStatus.setStatus( workingStatus );
                
                if( workingStatus.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ))
                {
                    equipment.setWorking( true );
                }
                
                else
                {
                    equipment.setWorking( false );
                    equipmentService.updateEquipment( equipment );
                }                
            }
            
            if( utilization != null && !utilization.trim().equals( "-1" ) )
            {
                equipmentStatus.setUtilization( utilization );
            }
            
            if( reasonNotWorking != null && !reasonNotWorking.trim().equals( "-1" ) )
            {
                equipmentStatus.setReasonNotWorking( reasonNotWorking );
            }
            
            /*
            if( serviceType != null && !serviceType.trim().equals( "-1" ) )
            {
                equipmentStatus.setServiceType( serviceType );
            }
            */
            
            if( serviceTypes != null && serviceTypes.size() > 0 )
            {
                String tempServiceType ="";
                for( String serviceType : serviceTypes )
                {
                    tempServiceType += serviceType + ";";
                }
                
                equipmentStatus.setServiceType( tempServiceType );
            }
            
            equipmentStatus.setReportingDate( format.parseDate( dateOfService.trim() ) );
            equipmentStatus.setUpdationDate( format.parseDate( dateOfService.trim() ) );
            
            String storedBy = currentUserService.getCurrentUsername();
            
            equipmentStatus.setType( type );
            equipmentStatus.setDescription( description );
            
            equipmentStatus.setEquipment( equipment );
            
            equipmentStatus.setStoredBy( storedBy );

            equipmentStatusService.addEquipmentStatus( equipmentStatus );
            
            //equipment.getEquipmentStatus().getUpdationDate();
            
            if( equipment.getEquipmentStatus().getUpdationDate() != null )
            {
                Date updationDate = sdf.parse( dateOfService.trim() );
                if(  updationDate.compareTo( equipment.getEquipmentStatus().getUpdationDate() ) >= 0 )
                {
                    equipment.setEquipmentStatus( equipmentStatus );
                    equipmentService.updateEquipment( equipment );
                }
            }
            
        }        
        else if( type.equalsIgnoreCase( "repair" ))
        {
            EquipmentStatus equipmentStatus = new EquipmentStatus();
            
            if( workingStatus != null && !workingStatus.trim().equals( "-1" ) )
            {
                equipmentStatus.setStatus( workingStatus );
                
                if( workingStatus.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ))
                {
                    equipment.setWorking( true );
                }
                
                else
                {
                    equipment.setWorking( false );
                    equipmentService.updateEquipment( equipment );
                }                
            }
            
            if( utilization != null && !utilization.trim().equals( "-1" ) )
            {
                equipmentStatus.setUtilization( utilization );
            }
            
            if( reasonNotWorking != null && !reasonNotWorking.trim().equals( "-1" ) )
            {
                equipmentStatus.setReasonNotWorking( reasonNotWorking );
            }
            
            if( partsReplaced != null && partsReplaced.size() > 0 )
            {
                String replacedParts="";
                for( String replacedPart : partsReplaced )
                {
                    replacedParts += replacedPart + ";";
                }
                
                //System.out.println("ReplacedParts: " + replacedParts );
                equipmentStatus.setPartsReplaced( replacedParts );
            }
            
            if( faultTypes != null && faultTypes.size() > 0 )
            {
                String faults="";
                for( String faultType : faultTypes )
                {
                    faults += faultType + ";";
                }
                //System.out.println("FaultTypes: " + faults );
                
                equipmentStatus.setFaultTypes( faults );
            }

            equipmentStatus.setReportingDate( format.parseDate( dateOfService.trim() ) );
            
            if( serviceReportingDate != null && serviceReportingDate.trim().length() > 0 )
            {
                equipmentStatus.setUpdationDate( format.parseDate( serviceReportingDate.trim() ) );
            }
            
            String storedBy = currentUserService.getCurrentUsername();
            
            equipmentStatus.setType( type );
            equipmentStatus.setDescription( description );
            
            equipmentStatus.setEquipment( equipment );
            
            equipmentStatus.setStoredBy( storedBy );

            equipmentStatusService.addEquipmentStatus( equipmentStatus );
            
            if( equipment.getEquipmentStatus().getUpdationDate() != null )
            {
                Date updationDate = sdf.parse( dateOfService.trim() );
                if(  updationDate.compareTo( equipment.getEquipmentStatus().getUpdationDate() ) >= 0 )
                {
                    equipment.setEquipmentStatus( equipmentStatus );
                    equipmentService.updateEquipment( equipment );
                }
            }
            
        }
        
        return SUCCESS;
    }
}
