package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadDataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private EquipmentDataValueService equipmentDataValueService;
    
    public void setEquipmentDataValueService( EquipmentDataValueService equipmentDataValueService )
    {
        this.equipmentDataValueService = equipmentDataValueService;
    }
    
    @Autowired
    private EquipmentStatusService equipmentStatusService;
    
    @Autowired
    private LookupService lookupService;
    
    @Autowired
    private OptionService optionService;
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
    

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private int equipmentId;
    
    public void setEquipmentId( int equipmentId )
    {
        this.equipmentId = equipmentId;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private Map<Integer, String> equipmentDataValueMap;
    
    public Map<Integer, String> getEquipmentDataValueMap()
    {
        return equipmentDataValueMap;
    }
    
    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
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
    
    private EquipmentStatus latestEquipmentStatus;
    
    public EquipmentStatus getLatestEquipmentStatus() 
    {
		return latestEquipmentStatus;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
    {
        //equipmentDataValueMap = null;
        
        Equipment equipment = equipmentService.getEquipment( equipmentId );
        
        //OrganisationUnit organisationUnit = equipment.getOrganisationUnit();
        //EquipmentType equipmentType =  equipment.getEquipmentType();
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
       // System.out.println( " ======  :" + period.getId() + " -- " + dataSet.getId());
        
        
        latestEquipmentStatus = equipmentStatusService.getEquipmentStatus( equipment, period.getEndDate() );
        
        //dataSet.getShortName();
        //DataSet dataSet = dataSetService.getDataSet( dataSetId, true, false, false );

        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Collections.sort( dataElements, dataElementComparator );
        
        
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
        
        
        //Collections.sort( modelTypeAttributes, new ModelTypeAttributeComparator() );
        
        List<EquipmentDataValue> equipmentDataValues = new ArrayList<EquipmentDataValue>( equipmentDataValueService.getEquipmentDataValues( equipment, period, dataElements ) );      
        
        //System.out.println( " Size of equipmentDataValues List is ======  :" + equipmentDataValues.size() );
        
        if ( equipmentDataValues != null && equipmentDataValues.size() > 0 )
        {
            
            equipmentDataValueMap = new HashMap<Integer, String>();
            
            for( EquipmentDataValue equipmentDataValue : equipmentDataValues )
            {
                equipmentDataValueMap.put( equipmentDataValue.getDataElement().getId(), equipmentDataValue.getValue() );
            }
        }
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        //sections.size();
        
        Collections.sort( sections, new SectionOrderComparator() );
        
        
        
        //System.out.println( " Size of equipmentDataValues Map is  List is ======  :" + equipmentDataValueMap.size() );
        /*
        System.out.println( " equipment Id is ======  :" + equipment.getId() );
        System.out.println( " Name of organisationUnit is ====  :" + organisationUnit.getName());
        System.out.println( " Name of equipmentType is ====  :" + equipmentType.getName());
        System.out.println( " Name of dataSet is ====  :" + dataSet.getName());
        System.out.println( " Name of period is ====  :" + selectedPeriodId );
        System.out.println( " Size of dataElements is ====  :" + dataElements.size() );
        
        
        for( DataElement dataElement : dataElements )
        {
            System.out.println( "DataElement Name :" + dataElement.getName() + "-- And value is :  "  + equipmentDataValueMap.get( dataElement.getId() ));
        }
        */
        return SUCCESS;
    }


}
