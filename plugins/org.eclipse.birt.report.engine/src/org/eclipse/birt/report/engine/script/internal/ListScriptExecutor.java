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

package org.eclipse.birt.report.engine.script.internal;

import java.util.logging.Level;

import org.eclipse.birt.report.engine.api.script.eventhandler.IListEventHandler;
import org.eclipse.birt.report.engine.content.impl.ContainerContent;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.script.element.List;
import org.eclipse.birt.report.engine.script.internal.instance.ListInstance;
import org.eclipse.birt.report.model.api.ListHandle;

public class ListScriptExecutor extends ScriptExecutor
{
	public static void handleOnPrepare( ListHandle list,
			ExecutionContext context )
	{
		try
		{
			if ( handleJS( list.getOnPrepare( ), context ) )
				return;
			IListEventHandler eh = ( IListEventHandler ) getInstance( list );
			if ( eh != null )
				eh.onPrepare( new List( list ), context.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}

	public static void handleOnCreate( ContainerContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign listDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			if ( handleJS( listDesign.getOnCreate( ), context ) )
				return;
			IListEventHandler eh = ( IListEventHandler ) getInstance( ( ListHandle ) listDesign
					.getHandle( ) );
			if ( eh != null )
				eh.onCreate( new ListInstance( content ), context
						.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}

	public static void handleOnRender( ContainerContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign listDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			if ( handleJS( listDesign.getOnRender( ), context ) )
				return;
			IListEventHandler eh = ( IListEventHandler ) getInstance( ( ListHandle ) listDesign
					.getHandle( ) );
			if ( eh != null )
				eh.onRender( new ListInstance( content ), context
						.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}
}
