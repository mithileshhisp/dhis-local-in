package org.hisp.dhis.coldchain.equipment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentAttributeValue;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOptionService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.coldchain.model.Model;
import org.hisp.dhis.coldchain.model.ModelService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class AddEquipmentAction implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    private EquipmentTypeService equipmentTypeService;
    
    private EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService;
    
    private EquipmentService equipmentService;
    
    private ModelService modelService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    /*
    private Integer ouId;
    
    public void setOuId( Integer ouId )
    {
        this.ouId = ouId;
    }
    */
    private Integer itypeId;
    
    private String message;
    
    /*
    private Boolean workingStatus = false;
    
    public void setWorkingStatus( Boolean workingStatus )
    {
        this.workingStatus = workingStatus;
    }
    */
    private Integer model;
    
    public void setModel( Integer model )
    {
        this.model = model;
    }
    
    private Integer organisationUnit;
    
    public void setOrganisationUnit( Integer organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }
    
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    private String equipmentTrackingId;
    
    public void setEquipmentTrackingId( String equipmentTrackingId )
    {
        this.equipmentTrackingId = equipmentTrackingId;
    }

    private String workingStatus;
    
    private String utilization;
    
    private String reasonNotWorking;
    
    public void setWorkingStatus( String workingStatus )
    {
        this.workingStatus = workingStatus;
    }

    public void setUtilization( String utilization )
    {
        this.utilization = utilization;
    }

    public void setReasonNotWorking( String reasonNotWorking )
    {
        this.reasonNotWorking = reasonNotWorking;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {

        String equipmentName = "";
        
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouId );
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( organisationUnit );
        
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( itypeId );
        
        Model selModel = null;
        
        if( model != null )
        {    
            selModel = modelService.getModel( model );
        }
        
        // -----------------------------------------------------------------------------
        // Preparing Equipment
        // -----------------------------------------------------------------------------
        Equipment equipment = new Equipment();
        
        equipment.setEquipmentType( equipmentType );
        equipment.setOrganisationUnit( orgUnit );
        
        //equipment.setWorking( workingStatus );
        
        equipmentName = orgUnit.getName() +" : " + equipmentType.getName().substring( 0, 3 );
        
        if( selModel != null )
        {
            equipment.setModel( selModel );
            
            equipmentName += " : " + selModel.getName();
        }
        
        if( equipmentTrackingId != null )
        {
            equipment.setEquipmentTrackingID( equipmentTrackingId );
            equipmentName += " : " + equipmentTrackingId;
        }
        equipment.setName( equipmentName );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date regDate = new Date();
        try
        {
            regDate = simpleDateFormat.parse( registrationDate );
        }
        catch( Exception e )
        {
        }
        equipment.setRegistrationDate( new Date() );
        
        // -----------------------------------------------------------------------------
        // Preparing EquipmentAttributeValue Details
        // -----------------------------------------------------------------------------
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        List<EquipmentTypeAttribute> equipmentTypeAttributes = new ArrayList<EquipmentTypeAttribute>( );
        for( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
        {
            equipmentTypeAttributes.add( equipmentType_Attribute.getEquipmentTypeAttribute() );
        }
        
        List<EquipmentAttributeValue> equipmentAttributeValueDetailsList = new ArrayList<EquipmentAttributeValue>();
        
        EquipmentAttributeValue equipmentAttributeValueDetails = null;
        for ( EquipmentTypeAttribute attribute : equipmentTypeAttributes )
        {
            
            value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
            if ( StringUtils.isNotBlank( value ) )
            {
                equipmentAttributeValueDetails = new EquipmentAttributeValue();
                equipmentAttributeValueDetails.setEquipment( equipment );
                equipmentAttributeValueDetails.setEquipmentTypeAttribute( attribute );

                if ( EquipmentTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    
                    //EquipmentTypeAttributeOption option = equipmentTypeAttributeOptionService.getEquipmentTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                    
                    OptionSet equipmentTypeAttributeOption  = attribute.getOptionSet();
                    
                    if ( equipmentTypeAttributeOption != null )
                    {
                        equipmentAttributeValueDetails.setValue( value );
                    }
                    
                    /*
                    if ( option != null )
                    {
                        equipmentAttributeValueDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( option.getName() );
                        
                        if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                        {
                            System.out.println( "Option ID is  : " + option.getId() + "Option Name is : "+option.getName() );
                            
                            if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                            {
                                equipment.setWorking( false );
                            }
                            else
                            {
                                equipment.setWorking( true );
                            }
                        }
                        
                    }
                    */
                    
                    else
                    {
                        // Someone deleted this option ...
                    }
                }
                else if ( EquipmentTypeAttribute.TYPE_MODEL.equalsIgnoreCase( attribute.getValueType() ) )
                {
                    Model model = modelService.getModel( NumberUtils.toInt( value, 0 ) );
                    if ( model != null )
                    {
                        //equipmentDetails.setEquipmentTypeAttributeOption( option );
                        equipmentAttributeValueDetails.setValue( model.getName() );
                    }
                    else
                    {
                        // Someone deleted this model ...
                    }
                }
                else
                {
                    equipmentAttributeValueDetails.setValue( value.trim() );
                }
                equipmentAttributeValueDetailsList.add( equipmentAttributeValueDetails );
            }
        }
        
        
        EquipmentStatus equipmentStatus = new EquipmentStatus();
        
        String storedBy = currentUserService.getCurrentUsername();
        
        equipmentStatus.setEquipment( equipment );
        
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
        
        equipmentStatus.setReportingDate( regDate );
        equipmentStatus.setUpdationDate( regDate );
        equipmentStatus.setStoredBy( storedBy );
        
        // -----------------------------------------------------------------------------
        // Creating EquipmentAttributeValue Instance and saving equipmentAttributeValue data
        // -----------------------------------------------------------------------------
        //Integer id = equipmentService.createEquipment( equipment, equipmentAttributeValueDetailsList );
        
        Integer id = equipmentService.createEquipment( equipment, equipmentAttributeValueDetailsList, equipmentStatus );

        message = id + "";
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Setters and Getters
    // -------------------------------------------------------------------------

    public String getMessage()
    {
        return message;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setEquipmentTypeService( EquipmentTypeService equipmentTypeService )
    {
        this.equipmentTypeService = equipmentTypeService;
    }

    public void setEquipmentTypeAttributeOptionService(
        EquipmentTypeAttributeOptionService equipmentTypeAttributeOptionService )
    {
        this.equipmentTypeAttributeOptionService = equipmentTypeAttributeOptionService;
    }

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    public void setModelService( ModelService modelService )
    {
        this.modelService = modelService;
    }

    public void setItypeId( Integer itypeId )
    {
        this.itypeId = itypeId;
    }
}
