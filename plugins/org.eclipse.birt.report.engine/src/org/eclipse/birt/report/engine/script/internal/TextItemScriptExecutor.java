/*******************************************************************************
 * Copyright (c) 2005, 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.script.internal;

import org.eclipse.birt.report.engine.api.script.element.ITextItem;
import org.eclipse.birt.report.engine.api.script.eventhandler.ITextItemEventHandler;
import org.eclipse.birt.report.engine.api.script.instance.ITextItemInstance;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IForeignContent;
import org.eclipse.birt.report.engine.content.ITextContent;
import org.eclipse.birt.report.engine.content.impl.ForeignContent;
import org.eclipse.birt.report.engine.content.impl.TextContent;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.script.internal.element.TextItem;
import org.eclipse.birt.report.engine.script.internal.instance.TextItemInstance;
import org.eclipse.birt.report.model.api.TextItemHandle;

public class TextItemScriptExecutor extends ScriptExecutor
{
	public static void handleOnPrepare( TextItemHandle textItemHandle,
			ExecutionContext context )
	{
		try
		{
			ITextItem textItem = new TextItem( textItemHandle );
			ITextItemEventHandler eh = getEventHandler( textItemHandle, context );
			if ( eh != null )
				eh.onPrepare( textItem, context.getReportContext( ) );
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public static void handleOnCreate( IContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign textItemDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			if ( !needOnCreate( textItemDesign ) )
			{
				return;
			}
			ITextItemInstance textItem = null;
			if ( content instanceof TextContent )
				textItem = new TextItemInstance( ( ITextContent ) content,
						context );
			else if ( content instanceof ForeignContent )
				textItem = new TextItemInstance( ( IForeignContent ) content,
						context );

			if ( handleJS( textItem, textItemDesign.getOnCreate( ), context )
					.didRun( ) )
				return;
			ITextItemEventHandler eh = getEventHandler( textItemDesign, context );
			if ( eh != null )
			{
				eh.onCreate( textItem, context.getReportContext( ) );
			}
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public static void handleOnRender( IContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign textItemDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			if ( !needOnRender( textItemDesign ) )
			{
				return;
			}
			ITextItemInstance textItem = null;
			if ( content instanceof TextContent )
				textItem = new TextItemInstance( ( ITextContent ) content,
						context );
			else if ( content instanceof ForeignContent )
				textItem = new TextItemInstance( ( IForeignContent ) content,
						context );
			if ( handleJS( textItem, textItemDesign.getOnRender( ), context )
					.didRun( ) )
				return;
			ITextItemEventHandler eh = getEventHandler( textItemDesign, context );
			if ( eh != null )
			{
				eh.onRender( textItem, context.getReportContext( ) );
			}
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public static void handleOnPageBreak( IContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign textItemDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			if ( !needOnPageBreak( textItemDesign ) )
			{
				return;
			}
			ITextItemInstance textItem = null;
			if ( content instanceof TextContent )
				textItem = new TextItemInstance( ( ITextContent ) content,
						context );
			else if ( content instanceof ForeignContent )
				textItem = new TextItemInstance( ( IForeignContent ) content,
						context );
			if ( handleJS( textItem, textItemDesign.getOnPageBreak( ), context )
					.didRun( ) )
				return;
			ITextItemEventHandler eh = getEventHandler( textItemDesign, context );
			if ( eh != null )
			{
				eh.onPageBreak( textItem, context.getReportContext( ) );
			}
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	private static ITextItemEventHandler getEventHandler(
			ReportItemDesign design, ExecutionContext context )
	{
		try
		{
			return (ITextItemEventHandler) getInstance( design, context );
		}
		catch ( ClassCastException e )
		{
			addClassCastException( context, e, design.getJavaClass( ),
					ITextItemEventHandler.class );
		}
		return null;
	}

	private static ITextItemEventHandler getEventHandler(
			TextItemHandle handle, ExecutionContext context )
	{
		try
		{
			return (ITextItemEventHandler) getInstance( handle, context );
		}
		catch ( ClassCastException e )
		{
			addClassCastException( context, e, handle.getEventHandlerClass( ),
					ITextItemEventHandler.class );
		}
		return null;
	}
}