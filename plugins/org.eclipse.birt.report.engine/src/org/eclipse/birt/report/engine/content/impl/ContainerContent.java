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

package org.eclipse.birt.report.engine.content.impl;

import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IReportContentVisitor;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.ir.StyleDesign;

/**
 * container content object
 * 
 * @version $Revision: 1.3 $ $Date: 2005/02/07 02:00:39 $
 */
public class ContainerContent extends ReportItemContent
		implements
			IContainerContent
{

	public final static int SECTION_CONTAINER = 0;
	public final static int REPORTITEM_CONTAINER = 1;

	/**
	 * the type of the container
	 */
	private int type;

	/**
	 * the style defined for the report item
	 */
	private StyleDesign style = null;

	/**
	 * constructor for REPORTITEM_CONTAINER
	 * 
	 * @param item
	 *            reference to the design object in engine IR
	 */
	public ContainerContent( ReportItemDesign item )
	{
		super( item );

		style = item.getStyle( );
		type = REPORTITEM_CONTAINER;
	}

	/**
	 * constructor for SECTION_CONTAINER
	 */
	public ContainerContent( )
	{
		super( );
		type = SECTION_CONTAINER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.content.ReportElementContent#accept(org.eclipse.birt.report.engine.content.ReportContentVisitor)
	 */
	public void accept( IReportContentVisitor visitor )
	{
		visitor.visitContainerContent( this );
	}

	/**
	 * @return Returns the style.
	 */
	public StyleDesign getStyle( )
	{
		return style;
	}

	/**
	 * @return the container type
	 */
	public int getType( )
	{
		return type;
	}

	/**
	 * @return the height of the container.
	 */
	public DimensionType getHeight( )
	{
		if ( type == REPORTITEM_CONTAINER )
			return super.getHeight( );
		else
			return null;
	}

	/**
	 * @return the width of the container.
	 */
	public DimensionType getWidth( )
	{
		if ( type == REPORTITEM_CONTAINER )
			return super.getWidth( );
		else
			return null;
	}

	/**
	 * @return the x position of the report item
	 */
	public DimensionType getX( )
	{
		if ( type == REPORTITEM_CONTAINER )
			return super.getX( );
		else
			return null;
	}

	/**
	 * @return the y position of the report item
	 */
	public DimensionType getY( )
	{
		if ( type == REPORTITEM_CONTAINER )
			return super.getY( );
		else
			return null;
	}
}