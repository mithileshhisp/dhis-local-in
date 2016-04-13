package org.hisp.dhis.trackedentity;

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

import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAudit;
import org.hisp.dhis.trackedentity.TrackedEntityAuditService;
import org.hisp.dhis.trackedentity.TrackedEntityAuditStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * 
 * @version DefaultTrackedEntityAuditService.java 9:08:54 AM Sep 26, 2012 $
 */
@Transactional
public class DefaultTrackedEntityAuditService
    implements TrackedEntityAuditService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityAuditStore auditStore;

    public void setAuditStore( TrackedEntityAuditStore auditStore )
    {
        this.auditStore = auditStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    public int saveTrackedEntityAudit( TrackedEntityAudit audit )
    {
        return auditStore.save( audit );
    }

    @Override
    public void deleteTrackedEntityAudit( TrackedEntityAudit audit )
    {
        auditStore.delete( audit );
    }

    @Override
    public TrackedEntityAudit getTrackedEntityAudit( int id )
    {
        return auditStore.get( id );
    }

    @Override
    public Collection<TrackedEntityAudit> getAllTrackedEntityAudit()
    {
        return auditStore.getAll();
    }

    @Override
    public Collection<TrackedEntityAudit> getTrackedEntityAudits( TrackedEntityInstance instance )
    {
        return auditStore.get( instance );
    }

    @Override
    public TrackedEntityAudit getTrackedEntityAudit( Integer instanceId, String visitor, Date date, String accessedModule )
    {
        return auditStore.get( instanceId, visitor, date, accessedModule );
    }
}
