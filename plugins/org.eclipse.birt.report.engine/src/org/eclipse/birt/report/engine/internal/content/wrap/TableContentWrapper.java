/*******************************************************************************
 * Copyright (c) 2004, 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.internal.content.wrap;

import java.util.List;

import org.eclipse.birt.report.engine.content.IColumn;
import org.eclipse.birt.report.engine.content.IContentVisitor;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.content.ITableContent;

/**
 * 
 * the table content object which contains columns object and row objects
 * 
 */
public class TableContentWrapper extends AbstractContentWrapper
		implements
			ITableContent
{

	protected ITableContent tableContent;

	protected TableBandContentWrapper footer;
	protected TableBandContentWrapper body;
	protected TableBandContentWrapper header;
	private List columns;
	
	/**
	 * constructor
	 * 
	 * @param item
	 *            the table deign
	 */
	public TableContentWrapper( ITableContent content, List columns )
	{
		super( content );
		this.tableContent = content;
		this.columns = columns;
	}

	public Object accept( IContentVisitor visitor, Object value )
	{
		return visitor.visitTable( this, value );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#addColumn(org.eclipse.birt.report.engine.content.IColumn)
	 */
	public void addColumn( IColumn column )
	{
		tableContent.addColumn( column );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getCaption()
	 */
	public String getCaption( )
	{
		return tableContent.getCaption( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getColumn(int)
	 */
	public IColumn getColumn( int index )
	{
		return (IColumn) columns.get( index );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getColumnCount()
	 */
	public int getColumnCount( )
	{
		return columns.size( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getFooter()
	 */
	public ITableBandContent getFooter( )
	{
		if ( footer == null )
		{
			footer = new TableBandContentWrapper( tableContent.getFooter( ) );
		}
		return footer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getHeader()
	 */
	public ITableBandContent getHeader( )
	{
		if ( header == null )
		{
			header = new TableBandContentWrapper( tableContent.getHeader( ) );
		}
		return header;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#isHeaderRepeat()
	 */
	public boolean isHeaderRepeat( )
	{
		return tableContent.isHeaderRepeat( );
	}

	public String getCaptionKey( )
	{
		return tableContent.getCaptionKey( );
	}

	public void setCaption( String caption )
	{
		tableContent.setCaption(caption);
	}

	public void setCaptionKey( String key )
	{
		tableContent.setCaptionKey(key);
	}

	public void setHeaderRepeat( boolean repeat )
	{
		tableContent.setHeaderRepeat( repeat );
	}

	public List getColumns( )
	{
		return tableContent.getColumns( );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ITableContent#getSummary()
	 */
	public String getSummary( )
	{
		return tableContent.getSummary( );
	}
	
	public void setSummary(String summary)
	{
		tableContent.setSummary( summary );
	}
	
}