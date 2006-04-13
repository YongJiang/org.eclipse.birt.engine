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

import java.util.Map;

import org.eclipse.birt.report.engine.api.script.IRowData;
import org.eclipse.birt.report.engine.api.script.ScriptException;
import org.eclipse.birt.report.engine.api.script.instance.IReportElementInstance;
import org.eclipse.birt.report.engine.api.script.instance.IScriptStyle;
import org.eclipse.birt.report.engine.content.impl.AbstractContent;
import org.eclipse.birt.report.engine.data.IResultSet;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.ReportElementDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.script.internal.ElementUtil;
import org.eclipse.birt.report.engine.script.internal.RowData;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.UserPropertyDefnHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;

public class ReportElementInstance implements IReportElementInstance
{

	protected AbstractContent content;

	private ExecutionContext context;

	private RowData rowData;

	public ReportElementInstance( AbstractContent content,
			ExecutionContext context )
	{
		this.content = content;
		this.context = context;
	}

	protected ReportElementInstance( ExecutionContext context )
	{
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IReportInstance#getStyle()
	 */
	public IScriptStyle getStyle( )
	{
		return new StyleInstance( content.getStyle( ) );
	}

	public Object getNamedExpressionValue( String name )
	{
		Object generatedBy = content.getGenerateBy( );
		if ( generatedBy instanceof ReportElementDesign )
		{
			ReportElementDesign design = (ReportElementDesign) generatedBy;
			Map m = design.getNamedExpressions( );
			String expr = (String) m.get( name );
			if ( expr == null )
				return null;
			context.newScope( this );
			try
			{
				return context.evaluate( expr );
			}
			finally
			{
				context.exitScope( );
			}
		}
		return null;
	}

	public Object getUserPropertyValue( String name )
	{
		Object generatedBy = content.getGenerateBy( );
		if ( generatedBy instanceof ReportElementDesign )
		{
			ReportElementDesign design = (ReportElementDesign) generatedBy;
			DesignElementHandle handle = design.getHandle( );
			UserPropertyDefnHandle prop = handle.getUserPropertyDefnHandle( name );
			if ( prop != null )
				return handle.getProperty( prop.getName( ) );
		}
		return null;
	}

	public void setUserPropertyValue( String name, Object value )
			throws ScriptException
	{
		Object generatedBy = content.getGenerateBy( );
		if ( generatedBy instanceof ReportElementDesign )
		{
			ReportElementDesign design = (ReportElementDesign) generatedBy;
			DesignElementHandle handle = design.getHandle( );
			UserPropertyDefnHandle prop = handle.getUserPropertyDefnHandle( name );
			if ( prop != null )
			{
				try
				{
					handle.setProperty( prop.getName( ), value );
				}
				catch ( SemanticException e )
				{
					throw new ScriptException( e.getLocalizedMessage( ) );
				}
			}
		}
	}

	public IReportElementInstance getParent( )
	{
		return ElementUtil.getInstance( content.getParent( ), context );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IReportInstance#getHorizontalPosition()
	 */
	public String getHorizontalPosition( )
	{
		return content.getX( ).toString( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IReportInstance#setHorizontalPosition(java.lang.String)
	 */
	public void setHorizontalPosition( String position )
	{
		content.setX( DimensionType.parserUnit( position ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IReportInstance#getVerticalPosition()
	 */
	public String getVerticalPosition( )
	{
		return content.getY( ).toString( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api.script.instance.IReportInstance#setVerticalPosition(java.lang.String)
	 */
	public void setVerticalPosition( String position )
	{
		content.setY( DimensionType.parserUnit( position ) );
	}

	public String getWidth( )
	{
		return content.getWidth( ).toString( );
	}

	public void setWidth( String width )
	{
		content.setWidth( DimensionType.parserUnit( width ) );
	}

	public String getHeight( )
	{
		return content.getHeight( ).toString( );
	}

	public void setHeight( String height )
	{
		content.setHeight( DimensionType.parserUnit( height ) );
	}

	public IRowData getRowData( )
	{
		if ( rowData == null )
		{
			// see if the report element has query
			Object objGen = content.getGenerateBy( );
			if ( objGen instanceof ReportItemDesign )
			{
				ReportItemDesign design = (ReportItemDesign) objGen;
				if ( design.getQuery( ) != null )
				{
					DesignElementHandle handle = design.getHandle( );
					if ( handle instanceof ReportItemHandle )
					{
						// get the current data set
						IResultSet rset = context.getDataEngine( )
								.getResultSet( );
						// using the handle and the rste to create the row data.
						rowData = new RowData( rset, (ReportItemHandle) handle );
					}
				}
			}
		}
		return rowData;
	}
}
