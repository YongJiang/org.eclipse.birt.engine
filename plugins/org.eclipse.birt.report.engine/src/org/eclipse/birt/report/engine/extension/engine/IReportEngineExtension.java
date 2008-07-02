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

package org.eclipse.birt.report.engine.extension.engine;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportEngine;

/**
 * the report engine extension is used to extend the feature of the report
 * engine.
 * 
 * Each report engine create a extension instance, it is closed in engine's
 * shutdown.
 * 
 */
public interface IReportEngineExtension
{

	IReportEngine getReportEngine( );

	/**
	 * the extension name. The extension name is the unique identifier of the
	 * extension. It will be saved into the report design and report document.
	 * 
	 * @return extension name
	 */
	String getExtensionName( );

	/**
	 * create the generate extension.
	 * 
	 * @param context
	 *            the run context.
	 * @return the generate extension.
	 * @throws EngineException
	 */
	IGenerateExtension createGenerateExtension( IRunContext context )
			throws EngineException;

	/**
	 * create the extension to handle the extra document processing.
	 * 
	 * @param context
	 *            run context
	 * @return the document extension.
	 * @throws EngineException
	 */
	IDocumentExtension createDocumentExtension( IRunContext context )
			throws EngineException;

	/**
	 * create the render extension.
	 * 
	 * @param context
	 *            render context.
	 * @return the render extension.
	 * @throws EngineException
	 */
	IRenderExtension createRenderExtension( IRenderContext context )
			throws EngineException;

	/**
	 * release the extension.
	 * 
	 * The user should release the shared resource allocated for the instance.
	 */
	void close( );
}