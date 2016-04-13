package org.hisp.dhis.dataapproval.hibernate;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataapproval.DataApproval;
import org.hisp.dhis.dataapproval.DataApprovalStore;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * @author Jim Grace
 */
public class HibernateDataApprovalStore
    extends HibernateGenericStore<DataApproval>
    implements DataApprovalStore
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
    // DataApproval
    // -------------------------------------------------------------------------

    public void addDataApproval( DataApproval dataApproval )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );

        // ---------------------------------------------------------------------
        // In general null values do not violate a unique constraint,
        // so we check by hand if categoryOptionGroup has a null value,
        // that no identical record exists with a null value.
        // ---------------------------------------------------------------------

        if ( dataApproval.getCategoryOptionGroup() == null )
        {
            DataApproval duplicate = getDataApproval( dataApproval.getDataSet(),
                    dataApproval.getPeriod(), dataApproval.getOrganisationUnit(), null );

            if ( duplicate != null )
            {
                throw new DataIntegrityViolationException( dataApproval.toString() );
            }
        }

        save( dataApproval );
    }

    public void updateDataApproval( DataApproval dataApproval )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );

        update ( dataApproval );
    }
    
    public void deleteDataApproval( DataApproval dataApproval )
    {
        delete( dataApproval );
    }

    public DataApproval getDataApproval( DataSet dataSet, Period period, 
        OrganisationUnit organisationUnit, CategoryOptionGroup categoryOptionGroup )
    {
        Period storedPeriod = periodService.reloadPeriod( period );

        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
        if ( categoryOptionGroup != null )
        {
            criteria.add( Restrictions.eq( "categoryOptionGroup", categoryOptionGroup ) );
        }
        else
        {
            criteria.add( Restrictions.isNull( "categoryOptionGroup" ) );
        }

        return (DataApproval) criteria.uniqueResult();
    }
}
