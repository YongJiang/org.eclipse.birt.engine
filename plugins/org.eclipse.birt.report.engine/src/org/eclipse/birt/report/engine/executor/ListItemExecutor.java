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

package org.eclipse.birt.report.engine.executor;

import java.util.logging.Level;

import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.impl.ContainerContent;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.ir.IReportItemVisitor;
import org.eclipse.birt.report.engine.ir.ListBandDesign;
import org.eclipse.birt.report.engine.ir.ListItemDesign;
import org.eclipse.birt.report.engine.ir.ListingDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.script.ListScriptExecutor;

/**
 * Defines execution logic for a List report item.
 * 
 * @version $Revision: 1.21 $ $Date: 2005/11/13 20:26:07 $
 */
public class ListItemExecutor extends ListingElementExecutor
{

	/**
	 * @param context
	 *            execution context
	 * @param visitor
	 *            visitor object for driving the execution
	 */
	protected ListItemExecutor( ExecutionContext context,
			IReportItemVisitor visitor )
	{
		super( context, visitor );
	}

	/**
	 * Execute a listint and create the contents.
	 * 
	 * List create a serials of contents.
	 * 
	 * The execution process is:
	 * 
	 * <li> create an container which will contain all the contents it creates.
	 * <li> push it into the stack
	 * <li> open query
	 * <li> process action, bookmark, style and visibility
	 * <li> call the onCreate if necessary
	 * <li> call emitter to start the list
	 * <li> access the query
	 * <li> call emitter to end the list
	 * <li> close the query.
	 * <li> pop up the container.
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ReportItemExecutor#execute(org.eclipse.birt.report.engine.ir.ReportItemDesign,
	 *      org.eclipse.birt.report.engine.emitter.IReportEmitter)
	 */
	public void execute( ReportItemDesign item, IContentEmitter emitter )
	{
		ListItemDesign list = ( ListItemDesign ) item;
		logger.log( Level.FINE, "start list item" ); //$NON-NLS-1$

		IContainerContent listContent = report.createContainerContent( );
		assert ( listContent instanceof ContainerContent );
		IContent parent = context.getContent( );
		context.pushContent( listContent );

		openResultSet( list );

		initializeContent( parent, item, listContent );

		processAction( item, listContent );
		processBookmark( item, listContent );
		processStyle( item, listContent );
		processVisibility( item, listContent );

		if ( context.isInFactory( ) )
		{
				ListScriptExecutor.handleOnCreate(
						( ContainerContent ) listContent, context );
		}

		if ( emitter != null )
		{
			emitter.startContainer( listContent );
		}

		logger.log( Level.FINE, "start get list data" ); //$NON-NLS-1$
		accessQuery( list, emitter );
		logger.log( Level.FINE, "end get list data" ); //$NON-NLS-1$

		if ( emitter != null )
		{
			emitter.endContainer( listContent );
		}

		closeResultSet( );
		context.popContent( );
		logger.log( Level.FINE, "end list item" ); //$NON-NLS-1$
	}

	/**
	 * access list band, such as list header, group header, detail etc
	 * 
	 * @param band
	 *            the list band
	 * @param emitter
	 *            the report emitter
	 * @param isDetail
	 *            true if it is detail band
	 */
	private void accessListBand( ListBandDesign band, IContentEmitter emitter )
	{
		if ( band != null && band.getContentCount( ) > 0 )
		{
			for ( int i = 0; i < band.getContentCount( ); i++ )
			{
				ReportItemDesign item = band.getContent( i );
				if ( context.isInFactory( ) )
				{
					//TODO: We need to handle onCreate for the detail row here
					//Where do we get the content object from??
				}
				if ( item != null )
				{
					item.accept( this.visitor, emitter );
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ListingElementExecutor#accessDetail(org.eclipse.birt.report.engine.ir.ListingDesign,
	 *      org.eclipse.birt.report.engine.emitter.IContentEmitter)
	 */
	protected void accessDetail( ListingDesign list, IContentEmitter emitter )
	{
		accessListBand( ( ( ListItemDesign ) list ).getDetail( ), emitter );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ListingElementExecutor#accessFooter(org.eclipse.birt.report.engine.ir.ListingDesign,
	 *      org.eclipse.birt.report.engine.emitter.IContentEmitter)
	 */
	protected void accessFooter( ListingDesign list, IContentEmitter emitter )
	{
		accessListBand( ( ( ListItemDesign ) list ).getFooter( ), emitter );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ListingElementExecutor#accessGroupFooter(org.eclipse.birt.report.engine.ir.ListingDesign,
	 *      int, org.eclipse.birt.report.engine.emitter.IContentEmitter)
	 */
	protected void accessGroupFooter( ListingDesign list, int index,
			IContentEmitter emitter )
	{
		accessListBand( ( ( ListItemDesign ) list ).getGroup( index )
				.getFooter( ), emitter );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ListingElementExecutor#accessGroupHeader(org.eclipse.birt.report.engine.ir.ListingDesign,
	 *      int, org.eclipse.birt.report.engine.emitter.IContentEmitter)
	 */
	protected void accessGroupHeader( ListingDesign list, int index,
			IContentEmitter emitter )
	{
		accessListBand( ( ( ListItemDesign ) list ).getGroup( index )
				.getHeader( ), emitter );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.executor.ListingElementExecutor#accessHeader(org.eclipse.birt.report.engine.ir.ListingDesign,
	 *      org.eclipse.birt.report.engine.emitter.IContentEmitter)
	 */
	protected void accessHeader( ListingDesign list, IContentEmitter emitter )
	{
		accessListBand( ( ( ListItemDesign ) list ).getHeader( ), emitter );
	}
}