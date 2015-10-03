package org.hisp.dhis.coldchain.equipment;

import java.io.Serializable;
import java.util.Date;

public class EquipmentStatus
    implements Serializable
{
   
    /**
     * TODO
     * 
     * To show the prototype and get feedback have including all EquipmentStatus, Service and Repair as single object, this need to be refactored
     *  
     */
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;

    public static final String STATUS_WORKING = "WORKING";

    public static final String STATUS_NOTWORKING = "NOTWORKING";

    public static final String STATUS_REPAIR = "REPAIR";

    public static final String WORKING_STATUS = "WORKING_STATUS";

    /*
     * public static final String STATUS_NOT_WORKING = "Not working";
     * 
     * public static final String STATUS_WORKING_WELL = "Working well";
     * 
     * public static final String STATUS_WORKING_NEEDS_MAINTENANCE =
     * "Working but needs maintenance";
     */

    public static final String STATUS_NOT_WORKING = "Unservicable";

    public static final String STATUS_WORKING_WELL = "Functioning";

    public static final String STATUS_WORKING_NEEDS_MAINTENANCE = "AwaitingRepair";

    public static final String STATUS_IN_USE = "In Use";

    public static final String STATUS_NOT_IN_USE = "Not in use";

    public static final String STATUS_IN_STORE = "In store for allocation";

    public static final String STATUS_UNKNOWN = "UNKNOWN";

    //--------------------------------------------------------------------------
    // EquipmentStatus properties
    //--------------------------------------------------------------------------
    private int id;

    private Equipment equipment;

    private Date reportingDate;

    private Date updationDate;

    private String status;

    private String utilization;

    private String reasonNotWorking;
    
    private String type;

    private String description;

    private String storedBy;

    //--------------------------------------------------------------------------
    // Repair specific properties
    //--------------------------------------------------------------------------

    private String faultTypes;
    
    private String partsReplaced;
    
    //--------------------------------------------------------------------------
    // Repair specific properties
    //--------------------------------------------------------------------------
    private String serviceType;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentStatus()
    {

    }

    public EquipmentStatus( Equipment equipment, Date reportingDate, Date updationDate, String status )
    {
        this.equipment = equipment;
    }

    public EquipmentStatus( Equipment equipment, Date reportingDate, Date updationDate, String status,
        String utilization, String reasonNotWorking )
    {
        this.equipment = equipment;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof EquipmentStatus) )
        {
            return false;
        }

        final EquipmentStatus other = (EquipmentStatus) o;

        return equipment.equals( other.getEquipment() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipment.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Equipment getEquipment()
    {
        return equipment;
    }

    public void setEquipment( Equipment equipment )
    {
        this.equipment = equipment;
    }

    public Date getReportingDate()
    {
        return reportingDate;
    }

    public void setReportingDate( Date reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    public Date getUpdationDate()
    {
        return updationDate;
    }

    public void setUpdationDate( Date updationDate )
    {
        this.updationDate = updationDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public String getUtilization()
    {
        return utilization;
    }

    public void setUtilization( String utilization )
    {
        this.utilization = utilization;
    }

    public String getReasonNotWorking()
    {
        return reasonNotWorking;
    }

    public void setReasonNotWorking( String reasonNotWorking )
    {
        this.reasonNotWorking = reasonNotWorking;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getFaultTypes()
    {
        return faultTypes;
    }

    public void setFaultTypes( String faultTypes )
    {
        this.faultTypes = faultTypes;
    }

    public String getPartsReplaced()
    {
        return partsReplaced;
    }

    public void setPartsReplaced( String partsReplaced )
    {
        this.partsReplaced = partsReplaced;
    }

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType( String serviceType )
    {
        this.serviceType = serviceType;
    }
    
    
}
