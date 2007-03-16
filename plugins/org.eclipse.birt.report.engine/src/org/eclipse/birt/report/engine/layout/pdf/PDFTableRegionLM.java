/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.layout.pdf;

import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.extension.IReportItemExecutor;
import org.eclipse.birt.report.engine.internal.executor.dom.DOMReportItemExecutor;
import org.eclipse.birt.report.engine.layout.IBlockStackingLayoutManager;
import org.eclipse.birt.report.engine.layout.area.impl.RowArea;

public class PDFTableRegionLM extends PDFTableLM
		implements
			IBlockStackingLayoutManager

{
	protected RowArea lastRow;
	
	public PDFTableRegionLM( PDFLayoutEngineContext context, IContent content,
			TableLayoutInfo layoutInfo )
	{
		super( context, null, content, null );
		this.layoutInfo = layoutInfo;
	}

	public void initialize( ITableBandContent content,  RowArea lastRowArea )
	{
		this.executor = new DOMReportItemExecutor( content );
		this.executor.execute( );
		this.lastRow = lastRowArea;
	}
	
	protected void initialize()
	{
		super.initialize( );
		//this.lastRowArea = lastRow;
	}
	protected int getAvaHeight( )
	{
		return Integer.MAX_VALUE;
	}

	protected void buildTableLayoutInfo( )
	{

	}

	protected IReportItemExecutor createExecutor( )
	{
		return this.executor;
	}

	protected void repeat( )
	{

	}
}
