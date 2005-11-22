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

import java.util.Collection;
import org.eclipse.birt.data.engine.api.IResultIterator;
import org.eclipse.birt.report.engine.api.script.ExpressionResults;
import org.eclipse.birt.report.engine.data.dte.DteResultSet;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.ir.IReportItemVisitor;
import org.eclipse.birt.report.engine.ir.ListingDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;

/**
 * An abstract class that defines execution logic for a Listing element, which
 * is the base element for table and list items.
 */
public abstract class ListingElementExecutor extends QueryItemExecutor
{

	/**
	 * the cursor position in the query result.
	 */
	protected int rsetCursor;
	protected boolean needPageBreak;

	/**
	 * emitter used to output the content
	 */
	protected IContentEmitter outputEmitter;

	/**
	 * @param context
	 *            execution context
	 * @param visitor
	 *            the visitor object that drives exection
	 */
	protected ListingElementExecutor( ExecutionContext context,
			IReportItemVisitor visitor )
	{
		super( context, visitor );
	}

	/**
	 * access the query and create the contents. the execution process is:
	 * <li> the cursor is at the begin of result set.
	 * <li> call listing's onStart event
	 * <li> create the header
	 * <li> for each row:
	 * <ul>
	 * <li> call onRow event.
	 * <li> if the row start some groups, create the group header for that
	 * group.
	 * <li> create the detail row.
	 * <li> if the row end some groups, create the group footer for that group.
	 * </ul>
	 * <li> create the footer.
	 * <li> call the onFinish event.
	 */
	protected void accessQuery( ReportItemDesign design, IContentEmitter emitter )
	{
		ListingDesign listing = (ListingDesign) design;

		rsetCursor = -1;
		outputEmitter = emitter;

		int groupCount = listing.getGroupCount( );
		int NONE_GROUP = groupCount + 1;
		int groupIndex;
		int pageBreakInterval = listing.getPageBreakInterval( );

		if ( rset == null || rsetEmpty == true )
		{
			// empty rset
			accessHeader( listing, outputEmitter );

			accessFooter( listing, outputEmitter );
		}
		else
		{

			IResultIterator rsIterator = ( (DteResultSet) rset )
					.getResultIterator( );
			Collection rowExpressions = listing.getQuery( ).getRowExpressions( );

			ExpressionResults exprResults = new ExpressionResults( rsIterator,
					rowExpressions );

			accessHeader( listing, outputEmitter );
			if ( groupCount == 0 )
			{
				do
				{
					rsetCursor++;
					accessDetail( listing, outputEmitter, exprResults );
					if ( pageBreakInterval > 0 )
					{
						if ( ( rsetCursor + 1 ) % pageBreakInterval == 0 )
						{
							needPageBreak = true;
						}
					}
				} while ( rset.next( ) );
			}
			else
			{
				do
				{
					rsetCursor++;
					int startGroup = rset.getStartingGroupLevel( );
					if ( startGroup != NONE_GROUP )
					{
						// It start the group startGroup. It also start the
						// groups from startGroup to groupCount.
						groupIndex = startGroup - 1;
						if ( groupIndex < 0 )
						{
							groupIndex = 0;
						}
						while ( groupIndex < groupCount )
						{
							accessGroupHeader( listing, groupIndex,
									outputEmitter );
							groupIndex++;
						}
					}

					accessDetail( listing, outputEmitter, exprResults );
					int endGroup = rset.getEndingGroupLevel( );
					if ( endGroup != NONE_GROUP )
					{
						// the endGroup has terminate, it also termiate the
						// groups
						// from endGroup-1
						// to groupCount-1.
						endGroup = endGroup - 1;
						if ( endGroup < 0 )
						{
							endGroup = 0;
						}
						groupIndex = groupCount - 1;
						while ( groupIndex >= endGroup )
						{
							accessGroupFooter( listing, groupIndex,
									outputEmitter );
							groupIndex--;
						}
					}
					if ( pageBreakInterval > 0 )
					{
						if ( ( rsetCursor + 1 ) % pageBreakInterval == 0 )
						{
							needPageBreak = true;
						}
					}
				} while ( rset.next( ) );
			}
			accessFooter( listing, outputEmitter );
		}
	}

	/**
	 * create the group header
	 * 
	 * @param list
	 *            listing design
	 * @param index
	 *            group index.
	 * @param emitter
	 *            output emitter
	 */
	abstract protected void accessGroupHeader( ListingDesign list, int index,
			IContentEmitter emitter );

	/**
	 * create the group footer.
	 * 
	 * @param list
	 *            list design
	 * @param index
	 *            group index
	 * @param emitter
	 *            output emitter
	 */
	abstract protected void accessGroupFooter( ListingDesign list, int index,
			IContentEmitter emitter );

	/**
	 * create detail band.
	 * 
	 * @param list
	 *            listing design.
	 * @param emitter
	 *            output emitter
	 */
	abstract protected void accessDetail( ListingDesign list,
			IContentEmitter emitter, ExpressionResults expressionResults );

	/**
	 * create the header band
	 * 
	 * @param list
	 *            listing design
	 * @param emitter
	 *            output emitter
	 */
	abstract protected void accessHeader( ListingDesign list,
			IContentEmitter emitter );

	/**
	 * create the footer band.
	 * 
	 * @param list
	 *            listing design.
	 * @param emitter
	 *            output emitter
	 */
	abstract protected void accessFooter( ListingDesign list,
			IContentEmitter emitter );

	public void reset( )
	{
		this.rsetCursor = -1;
		super.reset( );
	}
}