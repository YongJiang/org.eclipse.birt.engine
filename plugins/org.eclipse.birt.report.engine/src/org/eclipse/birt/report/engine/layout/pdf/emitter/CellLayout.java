/***********************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.layout.pdf.emitter;

import org.eclipse.birt.report.engine.content.ICellContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.css.engine.StyleConstants;
import org.eclipse.birt.report.engine.layout.area.impl.AreaFactory;
import org.eclipse.birt.report.engine.layout.area.impl.CellArea;
import org.eclipse.birt.report.engine.layout.pdf.emitter.ContainerLayout.ContainerContext;


public class CellLayout extends BlockStackingLayout
{
	
	/**
	 * table layout manager of current cell
	 */
	protected TableLayout tableLayout;

	protected int columnWidth = 0;
	
	/**
	 * cell content
	 */
	private ICellContent cellContent;

	public CellLayout( LayoutEngineContext context,
			ContainerLayout parent, IContent content )
	{
		super( context, parent, content );
		tableLayout = getTableLayoutManager( );
		
		cellContent = (ICellContent) content;
		//tableLM.startCell( cellContent );

		// set max width constraint
		int startColumn = cellContent.getColumn( );
		int endColumn = startColumn + cellContent.getColSpan( );
		columnWidth = tableLayout.getCellWidth( startColumn, endColumn );
		
		boolean isLastColumn = (endColumn==tableLayout.getColumnCount());
		if(tableLayout.isInBlockStacking && isLastColumn)
		{
			isInBlockStacking = true;
		}
		else
		{
			isInBlockStacking = false;
		}
		

	}

	
	protected void createRoot( )
	{
		CellArea cell = AreaFactory.createCellArea( cellContent );
		cell.setRowSpan( cellContent.getRowSpan( ) );
		currentContext.root = cell;
		int startColumn = cellContent.getColumn( );
		int endColumn = startColumn + cellContent.getColSpan( );
		columnWidth = tableLayout.getCellWidth( startColumn, endColumn );
		tableLayout.resolveBorderConflict( (CellArea)currentContext.root, true);
		removeMargin( currentContext.root.getStyle( ) );
		currentContext.root.setWidth( columnWidth );
	}

	protected void initialize( )
	{
		currentContext = new ContainerContext( );
		contextList.add( currentContext );
		createRoot( );
		validateBoxProperty( currentContext.root.getStyle( ), columnWidth, context.getMaxHeight( ) );
		offsetX = currentContext.root.getContentX( );
		offsetY = currentContext.root.getContentY( );
		currentContext.maxAvaWidth = currentContext.root.getContentWidth( );
		currentContext.root.setAllocatedHeight( parent.getCurrentMaxContentHeight( ));
		currentContext.maxAvaHeight = currentContext.root.getContentHeight( );
	}

	protected void closeLayout(ContainerContext currentContext, int index, boolean finished )
	{
		currentContext.root.setHeight( currentContext.currentBP
				+ offsetY
				+ getDimensionValue( currentContext.root.getStyle( ).getProperty(
						StyleConstants.STYLE_PADDING_BOTTOM ) ) );
		parent.addToRoot( currentContext.root, index );

	}
	
	protected void closeLayout( )
	{
		super.closeLayout( );
		parent.gotoFirstPage();
	}
	

}
