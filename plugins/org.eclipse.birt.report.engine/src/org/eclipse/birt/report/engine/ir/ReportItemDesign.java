/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.ir;

import java.util.ArrayList;

import org.eclipse.birt.data.engine.api.IBaseQueryDefinition;

/**
 * Report Item
 * 
 * @version $Revision: 1.6 $ $Date: 2005/05/08 06:08:26 $
 */
abstract public class ReportItemDesign extends StyledElementDesign
{

	/**
	 * x position
	 */
	protected DimensionType x;
	/**
	 * y position
	 */
	protected DimensionType y;
	/**
	 * width
	 */
	protected DimensionType width;
	/**
	 * height
	 */
	protected DimensionType height;

	/**
	 * query used to create the data set.
	 */
	protected IBaseQueryDefinition query;
	/**
	 * book-mark associated with this element.
	 */
	protected Expression bookmark;
	
	/**
	 * scripted called while on created
	 */
	protected String onCreate;
	
	/**
	 * script called while on render
	 */
	protected String onRender;
	

	/**
	 * parameter bindings associated with this item
	 */
	protected ArrayList paramBindings = new ArrayList();
	
	/**
	 * Visibility property.
	 */
	protected VisibilityDesign visibility;
	
	/**
	 * @return Returns the height.
	 */
	public DimensionType getHeight( )
	{
		return height;
	}

	/**
	 * @param height
	 *            The height to set.
	 */
	public void setHeight( DimensionType height )
	{
		this.height = height;
	}

	/**
	 * @return Returns the width.
	 */
	public DimensionType getWidth( )
	{
		return width;
	}

	/**
	 * @param width
	 *            The width to set.
	 */
	public void setWidth( DimensionType width )
	{
		this.width = width;
	}

	/**
	 * @return Returns the x.
	 */
	public DimensionType getX( )
	{
		return x;
	}

	/**
	 * @param x
	 *            The x to set.
	 */
	public void setX( DimensionType x )
	{
		this.x = x;
	}

	/**
	 * @return Returns the y.
	 */
	public DimensionType getY( )
	{
		return y;
	}

	/**
	 * @param y
	 *            The y to set.
	 */
	public void setY( DimensionType y )
	{
		this.y = y;
	}

	/**
	 * accept a visitor. see visit pattern.
	 * 
	 * @param visitor
	 */
	abstract public void accept( IReportItemVisitor visitor );

	
	/**
	 * @return Returns the boo-kmark.
	 */
	public Expression getBookmark( )
	{
		return bookmark;
	}
	/**
	 * @param bookmark The book-mark to set.
	 */
	public void setBookmark( Expression bookmark )
	{
		this.bookmark = bookmark;
	}
	
	/**
	 * @return Returns the query.
	 */
	public IBaseQueryDefinition getQuery( )
	{
		return query;
	}
	/**
	 * @param query The query to set.
	 */
	public void setQuery( IBaseQueryDefinition query )
	{
		this.query = query;
	}
	
	
	/**
	 * @return Returns the paramBindings.
	 */
	public ArrayList getParamBindings( )
	{
		return paramBindings;
	}
	
	
	/**
	 * @return Returns the onCreate.
	 */
	public String getOnCreate( )
	{
		return onCreate;
	}
	/**
	 * @param onCreate The onCreate to set.
	 */
	public void setOnCreate( String onCreate )
	{
		this.onCreate = onCreate;
	}
	/**
	 * @return Returns the onRender.
	 */
	public String getOnRender( )
	{
		return onRender;
	}
	/**
	 * @param onRender The onRender to set.
	 */
	public void setOnRender( String onRender )
	{
		this.onRender = onRender;
	}
	/**
	 * @return Returns the visibility.
	 */
	public VisibilityDesign getVisibility( )
	{
		return visibility;
	}
	/**
	 * @param visibility The visibility to set.
	 */
	public void setVisibility( VisibilityDesign visibility )
	{
		this.visibility = visibility;
	}
}
