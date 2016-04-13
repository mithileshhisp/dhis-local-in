package org.hisp.dhis.ihrissyncmanager.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SyncIHRISMetaDataAction  implements Action
{

    // ------------------------------------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------------------------------------

    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    // ------------------------------------------------------------------------------------------------------
    // Web-Params
    // ------------------------------------------------------------------------------------------------------


    private String metaDataType;
    
    public void setMetaDataType( String metaDataType )
    {
        this.metaDataType = metaDataType;
    }
    
    private String dataElementCode;
    
    public void setDataElementCode( String dataElementCode )
    {
        this.dataElementCode = dataElementCode;
    }
    
    private String dataElementName;

    public void setDataElementName( String dataElementName )
    {
        this.dataElementName = dataElementName;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // ------------------------------------------------------------------------------------------------------
    // Implementation
    // ------------------------------------------------------------------------------------------------------

 
    // ------------------------------------------------------------------------------------------------------
    // Action Implementation
    // ------------------------------------------------------------------------------------------------------

    public String execute() throws Exception
    {

        System.out.println( "======================== Inside Synch Mera Data" );

        System.out.println( "* TEST AGG-DATAELEMENT ACTION  Meta Data Type:" + metaDataType + " ,Data Element Name :" + dataElementName + " ,Data Element Code:" + dataElementCode );

        message = "0";
        
        DataElementCategoryCombo categoryCombo = dataElementCategoryService.getDataElementCategoryCombo( 1 );
        
        if( metaDataType.equalsIgnoreCase( "DE" ) )
        {
            if( dataElementCode != null && dataElementCode.length() != 0 )
            {
                //DataElement checkIfDataElementExists = dataElementService.getDataElementByName( dataElementName );
                
                DataElement checkIfDataElementExists = dataElementService.getDataElement( dataElementCode );
                
                if ( checkIfDataElementExists == null )
                {
                    DataElement newAggDataElement = new DataElement();

                    newAggDataElement.setName( dataElementName );

                    newAggDataElement.setShortName( dataElementName );
                    
                    newAggDataElement.setCode( dataElementCode );
                    
                    newAggDataElement.setUid( dataElementCode );
                    
                    //newAggDataElement.setDisplayDescription( dataElementName );
                    
                    newAggDataElement.setDescription( dataElementName );
                    
                    newAggDataElement.setActive( true );
                    
                    newAggDataElement.setDomainType( "aggregate" );
                    
                    newAggDataElement.setType( "int" );
                    
                    newAggDataElement.setAggregationOperator( "sum" );
                    
                    newAggDataElement.setCategoryCombo( categoryCombo );
                    
                    newAggDataElement.setZeroIsSignificant( false );

                    dataElementService.addDataElement( newAggDataElement );
                    
                    message = "DataElement " + dataElementName + " Successfully added" ;
                    
                    System.out.println( "====================" + "DataElement " + dataElementName + " Successfully added"  );
                    
                }
                
                else
                {
                    //dataElementService.updateDataElement( checkIfDataElementExists );
                    message = "DataElement " + dataElementName + " already exist" ;
                    
                    System.out.println( message );
                    
                    //System.out.println( "====================" + "DataElement " + dataElementName + " Successfully updated"  );
                }
            }
            
            
            
        }
        
        return SUCCESS;
    }
}

