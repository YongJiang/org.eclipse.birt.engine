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

package org.eclipse.birt.report.engine.dataextraction;



/**
 * Extends Data Extraction options for CSV format
 * 
 */
public class CSVDataExtractionOption extends CommonDataExtractionOption
		implements
			ICSVDataExtractionOption
{

	/**
	 * @see org.eclipse.birt.report.engine.dataextraction.ICSVDataExtractionOption#getSeparator()
	 */
	public String getSeparator( )
	{
		return getStringOption( OUTPUT_SEPARATOR );
	}

	/**
	 * @see org.eclipse.birt.report.engine.dataextraction.ICSVDataExtractionOption#setSeparator(java.lang.String)
	 */
	public void setSeparator( String sep )
	{
		setOption( OUTPUT_SEPARATOR, sep );
	}
}
