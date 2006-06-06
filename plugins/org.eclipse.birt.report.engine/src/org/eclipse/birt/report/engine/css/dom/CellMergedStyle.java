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

package org.eclipse.birt.report.engine.css.dom;

import org.eclipse.birt.report.engine.content.ICellContent;
import org.eclipse.birt.report.engine.content.IColumn;
import org.eclipse.birt.report.engine.content.IElement;
import org.eclipse.birt.report.engine.content.IRowContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.content.ITableContent;
import org.eclipse.birt.report.engine.css.engine.StyleConstants;
import org.w3c.dom.css.CSSValue;

/**
 * Represents style of cell with the style of column.
 *
 */
public class CellMergedStyle extends AbstractStyle
{
	IStyle cellStyle;
	IStyle rowStyle;
	IStyle columnStyle;

	/**
	 * Constructor.
	 *
	 * @param cell the cell.
	 */
	public CellMergedStyle( ICellContent cell )
	{
		super( cell.getCSSEngine( ) );
		this.cellStyle = cell.getStyle( );
		IRowContent row = (IRowContent) cell.getParent( );
		if ( row != null )
		{
			IElement parentElt = row.getParent( );
			rowStyle = row.getStyle();
			if ( parentElt instanceof ITableBandContent )
			{
				parentElt = parentElt.getParent( );
			}
			ITableContent table = (ITableContent) parentElt;
			if ( table != null )
			{
				int columnId = cell.getColumn( );
				if ( columnId >= 0 && columnId < table.getColumnCount( ) )
				{
					IColumn column = table.getColumn( columnId );
					columnStyle = column.getStyle( );
				}
			}
		}
	}

	/**
	 * 
	 * <li>if the property is not defined in the column, return null.
	 * 
	 * <li>the property has been defined in the cell style, return null.
	 * 
	 * <li> property which has been defined in the column but not defined in the
	 * cell.
	 * <li>if it is not inheritable attributes
	 * <ul>
	 * <li>background: return NULL if it has been defined in row.
	 * <li>otherwise: return the value defined in the column.
	 * </ul>
	 * <li>if it is inheritable attribute
	 * <ul>
	 * <li>if it is defined in the row, return NULL
	 * <li>otherwise, return the value defined in the column
	 * </ul>
	 * </ul>
	 */
	public CSSValue getProperty( int index )
	{
		if ( cellStyle != null && cellStyle.getProperty( index ) != null )
		{
			return null;
		}
		
		if ( columnStyle == null )
		{
			return null;
		}
		
		CSSValue value = columnStyle.getProperty( index );
		if ( value == null )
		{
			return null;
		}
		// value != null
		if ( !engine.isInheritedProperty( index ) )
		{
			if ( isBackgroundProperties( index ) )
			{
				if ( rowStyle != null )
				{
					CSSValue rowValue = rowStyle.getProperty( index );
					if ( rowValue != null )
					{
						return null;
					}
				}
			}
		}
		else
		{
			if ( rowStyle != null )
			{
				CSSValue rowValue = rowStyle.getProperty( index );
				if ( rowValue != null )
				{
					return null;
				}
			}
		}

		return value;
	}

	public boolean isEmpty( )
	{
		for ( int i = 0; i < StyleConstants.NUMBER_OF_STYLE; i++)
		{
			if ( getProperty ( i ) != null )
			{
				return false;
			}
		}
		return true;
	}

	public void setProperty( int index, CSSValue value )
	{
	}

	private boolean isBackgroundProperties( int index )
	{
		if (StyleConstants.STYLE_BACKGROUND_COLOR==index 
				||StyleConstants.STYLE_BACKGROUND_ATTACHMENT==index
				||StyleConstants.STYLE_BACKGROUND_IMAGE==index
				||StyleConstants.STYLE_BACKGROUND_REPEAT==index)
		{
			return true;
		}
		return false;
	}
}
