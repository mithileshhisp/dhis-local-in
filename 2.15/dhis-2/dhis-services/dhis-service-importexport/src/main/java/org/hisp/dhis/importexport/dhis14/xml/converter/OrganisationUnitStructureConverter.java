package org.hisp.dhis.importexport.dhis14.xml.converter;

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

import org.amplecode.staxwax.reader.XMLReader;
import org.amplecode.staxwax.writer.XMLWriter;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ImportParams;
import org.hisp.dhis.importexport.XMLConverter;
import org.hisp.dhis.importexport.importer.OrganisationUnitRelationshipImporter;

/**
 * @author Lars Helge Overland
 * @version $Id: OrganisationUnitHierarchyConverter.java 6455 2008-11-24
 *          08:59:37Z larshelg $
 */
public class OrganisationUnitStructureConverter
    extends OrganisationUnitRelationshipImporter
    implements XMLConverter
{
    public static final String ELEMENT_NAME = "OrgUnitStructure";

    private static final String FIELD_STRUCTUREID = "OrgUnitStructureID";

    private static final String FIELD_STRUCTURENAME = "OrgUnitStructureName";

    private static final String FIELD_STRUCTUREDESCRIPTION = "OrgUnitStructureDescription";

    private static final String FIELD_LEVELCOUNT = "OrgUnitLevelCount";

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructor for write operations.
     */
    public OrganisationUnitStructureConverter()
    {
    }

    // -------------------------------------------------------------------------
    // XMLConverter implementation
    // -------------------------------------------------------------------------

    public void write( XMLWriter writer, ExportParams params )
    {
        writer.openElement( ELEMENT_NAME );

        writer.writeElement( FIELD_STRUCTUREID, String.valueOf( 1 ) );
        writer.writeElement( FIELD_STRUCTURENAME, "Municipality Structure (2011)" );
        writer.writeElement( FIELD_STRUCTUREDESCRIPTION, "Municipality Structure(2011)" );
        writer.writeElement( FIELD_LEVELCOUNT, String.valueOf( 6 ) );
        writer.closeElement();
    }

    public void read( XMLReader reader, ImportParams params )
    {
        // Not implemented
    }
}
