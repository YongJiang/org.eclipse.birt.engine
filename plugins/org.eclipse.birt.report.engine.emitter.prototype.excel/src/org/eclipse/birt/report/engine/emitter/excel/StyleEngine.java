/*******************************************************************************
 * Copyright (c) 2004, 2008Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.emitter.excel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.emitter.excel.layout.ContainerSizeInfo;
import org.eclipse.birt.report.engine.emitter.excel.layout.ExcelLayoutEngine;
import org.eclipse.birt.report.engine.emitter.excel.layout.XlsContainer;
import org.eclipse.birt.report.engine.layout.pdf.util.PropertyUtil;

/**
 * This class is used to caculate styles for Excel.
 * 
 * 
 */
public class StyleEngine
{
	public static final int DEFAULT_DATE_STYLE = 1;
	
	public static final int RESERVE_STYLE_ID = 20;

	private int styleID = RESERVE_STYLE_ID;	
	private Hashtable<StyleEntry,Integer> style2id = new Hashtable<StyleEntry,Integer>( );
	private ExcelLayoutEngine engine;
	private Stack<StyleEntry> containerStyles = new Stack<StyleEntry>( );

	// TODO: style ranges.
	// private List<ExcelRange> styleRanges;

	/**
	 * 
	 * @param dataMap
	 *            layout data
	 * @return a StyleEngine instance
	 */
	public StyleEngine( ExcelLayoutEngine engine )
	{
		this.engine = engine;

		style2id.put( getDefaultEntry( DEFAULT_DATE_STYLE ), new Integer(
				DEFAULT_DATE_STYLE ) );
		// TODO: style ranges.
		// styleRanges = new ArrayList<ExcelRange>( );
	}
	
	public StyleEntry getDefaultEntry( int id )
	{
		StyleEntry entry = new StyleEntry( );
		if ( id == DEFAULT_DATE_STYLE )
		{
			entry.setProperty( StyleConstant.DATE_FORMAT_PROP,
					"yyyy-M-d HH:mm:ss AM/PM" );
			entry.setProperty( StyleConstant.DATA_TYPE_PROP, SheetData.DATE );
		}
		return entry;
	}

	public StyleEntry createEntry(ContainerSizeInfo sizeInfo, IStyle style)
	{
		if ( style == null )
		{
			return StyleBuilder.createEmptyStyleEntry( );
		}	
		
		StyleEntry entry = initStyle( style, sizeInfo );
		return entry;
	}

	public StyleEntry createCellEntry( ContainerSizeInfo sizeInfo,
			IStyle style, String diagonalLineColor, String diagonalLineStyle,
			int diagonalLineWidth )
	{
		StyleEntry entry;
		if ( style == null )
		{
			entry = StyleBuilder.createEmptyStyleEntry( );
		}
		else
			entry = initStyle( style, sizeInfo );

		StyleBuilder.applyDiagonalLine( entry, PropertyUtil
				.getColor( diagonalLineColor ), diagonalLineStyle,
				diagonalLineWidth );

		return entry;
	}

	public StyleEntry createHorizontalStyle( ContainerSizeInfo rule )
	{
		StyleEntry entry = StyleBuilder.createEmptyStyleEntry( );
		
		if(engine.getContainers( ).size( ) > 0)
		{
			XlsContainer container = engine.getCurrentContainer( );
			ContainerSizeInfo crule = container.getSizeInfo( );
			StyleEntry cEntry = container.getStyle( );

			StyleBuilder.mergeInheritableProp( cEntry, entry );

			if ( rule.getStartCoordinate( ) == crule.getStartCoordinate( ) )
			{
				StyleBuilder.applyLeftBorder( cEntry, entry );
			}

			if ( rule.getEndCoordinate( ) == crule.getEndCoordinate( ) )
			{
				StyleBuilder.applyRightBorder( cEntry, entry );
			}
		}

		return entry;
	}

	public StyleEntry getStyle( IStyle style, ContainerSizeInfo rule )
	{
		// This style associated element is not in any container.
		return initStyle( style, null, rule );
	}

	public StyleEntry getStyle( IStyle style, ContainerSizeInfo childSizeInfo,
			ContainerSizeInfo parentSizeInfo )
	{
		return initStyle( style, childSizeInfo, parentSizeInfo );
	}

	private StyleEntry initStyle( IStyle style,
			ContainerSizeInfo childSizeInfo, ContainerSizeInfo parentSizeInfo )
	{
		StyleEntry entry = StyleBuilder.createStyleEntry( style );;
		if ( !containerStyles.isEmpty( ) )
		{
			StyleEntry centry = containerStyles.peek( );
			StyleBuilder.mergeInheritableProp( centry, entry );
		}
		if ( engine.getContainers( ).size( ) > 0 )
		{
			XlsContainer container = engine.getCurrentContainer( );
			StyleEntry cEntry = container.getStyle( );
			StyleBuilder.mergeInheritableProp( cEntry, entry );
			if ( childSizeInfo == null )
			{
				childSizeInfo = container.getSizeInfo( );
			}
			applyHBorders( cEntry, entry, childSizeInfo, parentSizeInfo );
			applyTopBorderStyle( entry );
		}
		return entry;
	}

	private void applyTopBorderStyle( StyleEntry childStyle )
	{
		XlsContainer container = engine.getCurrentContainer( );
		int rowIndex = container.getRowIndex( );
		XlsContainer parent = container;
		while ( parent != null && parent.getStartRowId( ) == rowIndex )
		{
			StyleBuilder.applyTopBorder( parent.getStyle( ), childStyle );
			parent = parent.getParent( );
		}
	}

	// public int getStyleID( SheetData data )
	// {
	// StyleEntry entry = data.getStyle( );
	// int styleId = getStyleId( entry );
		// TODO: style ranges.
		// if ( styleId >= RESERVE_STYLE_ID )
		// {
		// int rangeIndex = styleId - RESERVE_STYLE_ID;
		// ExcelRange range = null;
		// if ( rangeIndex < styleRanges.size( ) )
		// {
		// range = styleRanges.get( rangeIndex );
		// }
		// else
		// {
		// range = new ExcelRange( );
		// styleRanges.add( range );
		// }
		// int row = data.getRowIndex( );
		// int rowSpan = data.getRowSpan( );
		// Span span = data.getSpan( );
		// int col = span.getCol( );
		// int colSpan = span.getColSpan( );
		// ExcelArea area = new ExcelArea( row, col, rowSpan, colSpan );
		// range.addArea( area );
		// }
	// return styleId;
	// }

	public int getStyleId( StyleEntry entry )
	{
		if ( entry == null )
		{
			return 0;
		}
		int styleId = 0;
		if ( style2id.get( entry ) != null )
		{
			styleId = style2id.get( entry ).intValue( );
		}
		else
		{
			styleId = styleID;
			style2id.put( entry, new Integer( styleId ) );
			styleID++;
		}
		return styleId;
	}	

	public Map<StyleEntry,Integer> getStyleIDMap( )
	{
		return style2id;
	}

	private void applyHBorders( StyleEntry centry, StyleEntry entry,
			ContainerSizeInfo crule, ContainerSizeInfo rule )
	{
		if ( crule == null || rule == null )
		{
			return;
		}
		if ( crule.getStartCoordinate( ) == rule.getStartCoordinate( ) )
		{
			StyleBuilder.applyLeftBorder( centry, entry );
		}

		if ( crule.getEndCoordinate( ) == rule.getEndCoordinate( ) )
		{
			StyleBuilder.applyRightBorder( centry, entry );
		}
	}

	private StyleEntry initStyle( IStyle style, ContainerSizeInfo rule )
	{
		return initStyle( style, null, rule );
	}

	public void addContainderStyle( IStyle computedStyle )
	{
		StyleEntry entry = StyleBuilder.createStyleEntry( computedStyle );
		if ( !containerStyles.isEmpty( ) )
		{
			StyleEntry centry = containerStyles.peek( );
			StyleBuilder.mergeInheritableProp( centry, entry );
		}
		containerStyles.add( entry );
	}

	public void removeForeignContainerStyle( )
	{
		if ( !containerStyles.isEmpty( ) )
			containerStyles.pop( );
	}

	/**
	 * 
	 */
	public void applyContainerBottomStyle( )
	{
		applyContainerBottomStyle( engine.getCurrentContainer( ) );
	}

	public void applyContainerBottomStyle( XlsContainer container )
	{
		ContainerSizeInfo rule = container.getSizeInfo( );
		StyleEntry entry = container.getStyle( );
		int start = rule.getStartCoordinate( );
		int col = engine.getAxis( ).getColumnIndexByCoordinate( start );
		int span = engine.getAxis( ).getColumnIndexByCoordinate(
				rule.getEndCoordinate( ) );
		for ( int i = col; i < span; i++ )
		{
			SheetData data = engine.getColumnLastData( i );

			if ( data == null )
			{
				continue;
			}

			int styleId = data.getStyleId( );
			if ( styleId != -1 )
			{
				StyleEntry originalStyle = getStyle( styleId );
				StyleEntry newStyle = new StyleEntry( originalStyle );
				StyleBuilder.applyBottomBorder( entry, newStyle );
				data.setStyleId( getStyleId( newStyle ) );
			}
		}
	}

	public StyleEntry getStyle( int id )
	{
		for ( Entry<StyleEntry, Integer> entry : style2id.entrySet( ) )
		{
			if ( entry.getValue( ) == id )
			{
				return entry.getKey( );
			}
		}
		return null;
	}

	// TODO: style ranges.
	// public List<ExcelRange> getStyleRanges( )
	// {
	// return Collections.unmodifiableList( styleRanges );
	// }

	public static class ExcelRange
	{

		private Set<ExcelArea> areas;

		public ExcelRange( )
		{
			areas = new HashSet<ExcelArea>( );
		}

		public void addArea( ExcelArea area )
		{
			areas.add( area );
		}

		public Set<ExcelArea> getAreas( )
		{
			return Collections.unmodifiableSet( areas );
		}
	}

	public static class ExcelArea
	{

		public int row;

		public int col;

		public int rowSpan;

		public int colSpan;

		public ExcelArea( int row, int col, int rowSpan, int colSpan )
		{
			this.row = row;
			this.col = col;
			this.rowSpan = rowSpan;
			this.colSpan = colSpan;
		}

		public int hashCode( )
		{
			int result = 0;
			result = 31 * result + row;
			result = 31 * result + col;
			result = 31 * result + rowSpan;
			result = 31 * result + colSpan;
			return result;
		}

		public boolean equals( Object obj )
		{
			if ( obj == this )
			{
				return true;
			}
			if ( !( obj instanceof ExcelArea ) )
			{
				return false;
			}
			ExcelArea area = (ExcelArea) obj;
			return row == area.row && col == area.col
					&& rowSpan == area.rowSpan && colSpan == area.colSpan;
		}
	}
}
