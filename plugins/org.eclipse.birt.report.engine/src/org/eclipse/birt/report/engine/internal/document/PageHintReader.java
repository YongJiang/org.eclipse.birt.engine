/*******************************************************************************
 * Copyright (c) 2004,2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.internal.document;

import java.io.IOException;

import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.impl.ReportDocumentConstants;
import org.eclipse.birt.report.engine.internal.document.v1.PageHintReaderV1;
import org.eclipse.birt.report.engine.internal.document.v2.PageHintReaderV2;
import org.eclipse.birt.report.engine.internal.document.v3.PageHintReaderV3;
import org.eclipse.birt.report.engine.presentation.IPageHint;

/**
 * page hint reader
 * 
 * It can support mutiple versions.
 * 
 */
public class PageHintReader implements IPageHintReader
{

	IPageHintReader reader;

	public PageHintReader( IReportDocument document ) throws IOException
	{
		String version = document
				.getProperty( ReportDocumentConstants.PAGE_HINT_VERSION_KEY );

		if ( ReportDocumentConstants.PAGE_HINT_VERSION_1.equals( version ) )
		{
			this.reader = new PageHintReaderV1( document );
		}
		else if ( ReportDocumentConstants.PAGE_HINT_VERSION_2.equals( version ) )
		{
			this.reader = new PageHintReaderV2( document.getArchive( ) );
		}
		else
		{
			this.reader = new PageHintReaderV3( document.getArchive( ) );
		}
		
	}

	public int getVersion( )
	{
		return reader.getVersion( );
	}

	public void close( )
	{
		reader.close( );
	}

	public long getTotalPage( ) throws IOException
	{
		return reader.getTotalPage( );
	}

	public IPageHint getPageHint( long pageNumber ) throws IOException
	{
		return reader.getPageHint( pageNumber );
	}

	public long getPageOffset( long pageNumber, String masterPage )
			throws IOException
	{
		return reader.getPageOffset( pageNumber, masterPage );
	}
	
}
