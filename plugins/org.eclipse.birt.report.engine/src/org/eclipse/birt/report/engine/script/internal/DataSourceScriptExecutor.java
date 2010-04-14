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

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.script.IBaseDataSourceEventHandler;
import org.eclipse.birt.data.engine.api.script.IDataSourceInstanceHandle;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.script.eventhandler.IAutoTextEventHandler;
import org.eclipse.birt.report.engine.api.script.eventhandler.IDataSourceEventHandler;
import org.eclipse.birt.report.engine.api.script.eventhandler.IScriptedDataSetEventHandler;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.script.internal.instance.DataSourceInstance;
import org.eclipse.birt.report.model.api.DataSourceHandle;
import org.mozilla.javascript.Scriptable;

public class DataSourceScriptExecutor extends DtEScriptExecutor implements
		IBaseDataSourceEventHandler
{

	protected DataSourceHandle dataSourceHandle;

	protected IDataSourceEventHandler eventHandler;

	private boolean useBeforeCloseEventHandler = false;
	private boolean useBeforeOpenEventHandler = false;
	private boolean useAfterOpenEventHandler = false;
	private boolean useAfterCloseEventHandler = false;

	public DataSourceScriptExecutor( DataSourceHandle dataSourceHandle,
			ExecutionContext context ) throws BirtException
	{
		super( context );
		this.dataSourceHandle = dataSourceHandle;
		String className = dataSourceHandle.getEventHandlerClass( );
		initEventHandler( className );
		this.useBeforeOpenEventHandler = ScriptTextUtil.isNullOrComments( dataSourceHandle.getBeforeOpen( ) );
		this.useBeforeCloseEventHandler = ScriptTextUtil.isNullOrComments( dataSourceHandle.getBeforeClose( ) );
		this.useAfterOpenEventHandler = ScriptTextUtil.isNullOrComments( dataSourceHandle.getAfterOpen( ) );
		this.useAfterCloseEventHandler = ScriptTextUtil.isNullOrComments( dataSourceHandle.getAfterClose( ) );

	}

	protected void initEventHandler( String className )
	{
		if ( className != null )
		{
			try
			{
				eventHandler = ( IDataSourceEventHandler ) getInstance(
						className, context );
			} catch ( ClassCastException e )
			{
				addClassCastException( context, e, dataSourceHandle,
						IScriptedDataSetEventHandler.class );
			} catch ( EngineException e )
			{
				addException( context, e, dataSourceHandle );
			}
		}
	}

	public void handleBeforeOpen( IDataSourceInstanceHandle dataSource )
	{
		if ( reportContext == null )
			return;
		try
		{
			if ( !this.useBeforeOpenEventHandler )
			{
				ScriptStatus status = handleJS( dataSource.getScriptScope( ),
						dataSource.getName( ),
						BEFORE_OPEN,
						dataSourceHandle.getBeforeOpen( ) );
				if ( status.didRun( ) )
					return;
			}
			if ( eventHandler != null )
				eventHandler.beforeOpen( new DataSourceInstance( dataSource ),
						reportContext );
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public void handleBeforeClose( IDataSourceInstanceHandle dataSource )
	{
		if ( reportContext == null )
			return;
		try
		{
			if ( !this.useBeforeCloseEventHandler )
			{
				ScriptStatus status = handleJS( dataSource.getScriptScope( ),
						dataSource.getName( ),
						BEFORE_CLOSE,
						dataSourceHandle.getBeforeClose( ) );
				if ( status.didRun( ) )
					return;
			}
			if ( eventHandler != null )
				eventHandler.beforeClose( new DataSourceInstance( dataSource ),
						reportContext );
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public void handleAfterOpen( IDataSourceInstanceHandle dataSource )
	{
		if ( reportContext == null )
			return;
		try
		{
			if ( !this.useAfterOpenEventHandler )
			{
				ScriptStatus status = handleJS( dataSource.getScriptScope( ),
						dataSource.getName( ),
						AFTER_OPEN,
						dataSourceHandle.getAfterOpen( ) );
				if ( status.didRun( ) )
					return;
			}
			if ( eventHandler != null )
				eventHandler.afterOpen( new DataSourceInstance( dataSource ),
						reportContext );
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	public void handleAfterClose( IDataSourceInstanceHandle dataSource )
	{
		if ( reportContext == null )
			return;
		try
		{
			if ( !this.useAfterCloseEventHandler )
			{
				ScriptStatus status = handleJS( dataSource.getScriptScope( ),
						dataSource.getName( ),
						AFTER_CLOSE,
						dataSourceHandle.getAfterClose( ) );
				if ( status.didRun( ) )
					return;
			}
			if ( eventHandler != null )
				eventHandler.afterClose( reportContext );
		} catch ( Exception e )
		{
			addException( context, e );
		}
	}

	protected ScriptStatus handleJS( Scriptable scope, String name,
			String method, String script )
	{
		return handleJS( scope, DATA_SOURCE, name, method, script );
	}

}