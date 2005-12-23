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

import org.eclipse.birt.report.engine.api.script.element.IDynamicText;
import org.eclipse.birt.report.engine.api.script.eventhandler.IDynamicTextEventHandler;
import org.eclipse.birt.report.engine.api.script.instance.IDynamicTextInstance;
import org.eclipse.birt.report.engine.content.impl.ForeignContent;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.script.internal.element.DynamicText;
import org.eclipse.birt.report.engine.script.internal.instance.DynamicTextInstance;
import org.eclipse.birt.report.model.api.TextDataHandle;

public class DynamicTextScriptExecutor extends ScriptExecutor
{

	public static void handleOnPrepare( TextDataHandle textDataHandle,
			ExecutionContext context )
	{
		try
		{
			IDynamicText text = new DynamicText( textDataHandle );
			if ( handleJS( text, textDataHandle.getOnPrepare( ), context )
					.didRun( ) )
				return;
			IDynamicTextEventHandler eh = ( IDynamicTextEventHandler ) getInstance( textDataHandle );
			if ( eh != null )
				eh.onPrepare( text, context.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}

	public static void handleOnCreate( ForeignContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign textItemDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			IDynamicTextInstance text = new DynamicTextInstance( content,
					context );
			if ( handleJS( text, textItemDesign.getOnCreate( ), context )
					.didRun( ) )
				return;
			IDynamicTextEventHandler eh = ( IDynamicTextEventHandler ) getInstance( ( TextDataHandle ) textItemDesign
					.getHandle( ) );
			if ( eh != null )
				eh.onCreate( text, context.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}

	public static void handleOnRender( ForeignContent content,
			ExecutionContext context )
	{
		try
		{
			ReportItemDesign textItemDesign = ( ReportItemDesign ) content
					.getGenerateBy( );
			IDynamicTextInstance text = new DynamicTextInstance( content,
					context );
			if ( handleJS( text, textItemDesign.getOnRender( ), context )
					.didRun( ) )
				return;
			IDynamicTextEventHandler eh = ( IDynamicTextEventHandler ) getInstance( ( TextDataHandle ) textItemDesign
					.getHandle( ) );
			if ( eh != null )
				eh.onRender( text, context.getReportContext( ) );
		} catch ( Exception e )
		{
			log.log( Level.WARNING, e.getMessage( ), e );
		}
	}
}
