/*******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.script.internal.instance;

import org.eclipse.birt.report.engine.api.script.IScriptStyle;
import org.eclipse.birt.report.engine.api.script.instance.IGridRowInstance;
import org.eclipse.birt.report.engine.api.script.instance.ITableDetailRowInstance;
import org.eclipse.birt.report.engine.api.script.instance.ITableFooterRowInstance;
import org.eclipse.birt.report.engine.api.script.instance.ITableHeaderRowInstance;
import org.eclipse.birt.report.engine.content.impl.RowContent;
import org.eclipse.birt.report.engine.ir.DimensionType;

/**
 * A class representing the runtime state of a detail row
 */
public class RowInstance implements ITableDetailRowInstance,
		ITableHeaderRowInstance, ITableFooterRowInstance, IGridRowInstance
{

	private RowContent row;

	public RowInstance( RowContent row )
	{
		this.row = row;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IRowInstance#getStyle()
	 */
	public IScriptStyle getStyle( )
	{
		return new StyleInstance( row.getStyle( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IRowInstance#getBookmarkValue()
	 */
	public String getBookmarkValue( )
	{
		return row.getBookmark( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IRowInstance#setBookmark(java.lang.String)
	 */
	public void setBookmark( String bookmark )
	{
		row.setBookmark( bookmark );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IRowInstance#getHeight()
	 */
	public DimensionType getHeight( )
	{
		return row.getHeight( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IRowInstance#setHeight(java.lang.String)
	 */
	public void setHeight( String height )
	{
		row.setHeight( DimensionType.parserUnit( height ) );
	}
}
