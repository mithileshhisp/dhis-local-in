package org.hisp.dhis.dataanalyser.ds.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetWeeklyPeriodsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    } 
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String year;
    
    public void setYear( String year )
    {
        this.year = year;
    }

    private String month;
    
    public void setMonth( String month )
    {
        this.month = month;
    }
    
    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }
    
    private String weeklyPeriodTypeName;
    private SimpleDateFormat simpleDateFormat;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        weeklyPeriodTypeName = WeeklyPeriodType.NAME;
        PeriodType periodType = periodService.getPeriodTypeByName( weeklyPeriodTypeName );
        
        periods = new ArrayList<Period>();
        periodNameList = new ArrayList<String>();
        
        if( year != null && month != null )
        {
            String isoPeriodString = year + month;
            
            //System.out.println("\n\n Iso Period : " + isoPeriodString );
            
            //periodService.reloadIsoPeriod( isoPeriodString );
            
            //Period period = periodService.getPeriod( isoPeriodString );
            Period period = periodService.reloadIsoPeriod( isoPeriodString );
            
            //System.out.println("\n\n Iso Period Id : " + period.getId() );
            
            if( period != null )
            {
                periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType, period.getStartDate(), period.getEndDate() ) );
            }
            
        }
        else
        {
            periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        }
        
        // remove future period
        Iterator<Period> periodIterator = periods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove();
            }

        }
        Collections.sort( periods, new PeriodComparator() );
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        for ( Period p1 : periods )
        {
            String tempPeriodName = simpleDateFormat.format( p1.getStartDate() ) + " - " + simpleDateFormat.format( p1.getEndDate() );
            periodNameList.add( tempPeriodName );
        }
        
        return SUCCESS;
    }
}
