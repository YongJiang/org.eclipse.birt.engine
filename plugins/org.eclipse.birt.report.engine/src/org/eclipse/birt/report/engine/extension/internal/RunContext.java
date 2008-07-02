/*******************************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.extension.internal;

import org.eclipse.birt.core.archive.IDocArchiveWriter;
import org.eclipse.birt.report.engine.api.impl.ReportDocumentWriter;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.extension.engine.IRunContext;
import org.eclipse.birt.report.engine.script.internal.ReportContextImpl;

public class RunContext extends ReportContextImpl implements IRunContext
{

	public RunContext( ExecutionContext context )
	{
		super( context );
	}

	public IDocArchiveWriter getWriter( )
	{
		ReportDocumentWriter writer = context.getReportDocWriter( );
		if ( writer != null )
		{
			return writer.getArchive( );
		}
		return null;
	}

	public ClassLoader getApplicationClassLoader( )
	{
		return context.getApplicationClassLoader( );
	}

	public IReportContent getReportContent( )
	{
		return context.getReportContent( );
	}
}