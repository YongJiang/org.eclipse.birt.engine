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

package org.eclipse.birt.report.engine.api;

/**
 * Data Action is used to generate a URL used to reterive the data from the data
 * base.
 * 
 * It has following files:
 * 
 * <li> getDataType</li>
 * the output data type, such as csv, xml etc.
 * <li> getReportName</li>
 * the report document name, which is the data soruce.
 * <li> getBookmark</li>
 * the bookmark which define the result set to be exported.
 * 
 */
public interface IDataAction extends IAction
{
	
	/**
	 * data action, the user can safely type cast this object to IDataAction
	 */
	public final static int ACTION_DATA = 4;

	/**
	 * the output data type, such as csv, xml. the type should be registered by
	 * a IDataExtractionExtension.
	 * 
	 * @return the data type.
	 */
	String getDataType( );

}
