package org.hisp.dhis.trackedentity.hibernate;

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

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAudit;
import org.hisp.dhis.trackedentity.TrackedEntityAuditStore;

/**
 * @author Chau Thu Tran
 * 
 * @version HibernateTrackedEntityAuditStore.java 9:12:20 AM Sep 26, 2012 $
 */
public class HibernateTrackedEntityAuditStore
    extends HibernateGenericStore<TrackedEntityAudit>
    implements TrackedEntityAuditStore
{
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<TrackedEntityAudit> get( TrackedEntityInstance entityInstance )
    {
        return getCriteria( Restrictions.eq( "entityInstance", entityInstance ) ).addOrder( Order.desc( "date" ) ).list();
    }

    @Override
    public TrackedEntityAudit get( Integer entityInstanceId, String visitor, Date date, String accessedModule )
    {
        return (TrackedEntityAudit) getCriteria( Restrictions.eq( "entityInstance.id", entityInstanceId ),
            Restrictions.eq( "visitor", visitor ), Restrictions.eq( "date", date ),
            Restrictions.eq( "accessedModule", accessedModule ) ).uniqueResult();
    }
}
