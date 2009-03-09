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

package org.eclipse.birt.report.engine.ir;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.birt.report.engine.EngineCase;

/**
 * Test case
 * 
 * 
 */
public class DrillThroughActionDesignTest extends EngineCase
{

	public void testDrillThrough( )
	{
		DrillThroughActionDesign drillThrough = new DrillThroughActionDesign( );
		Expression<String> reportName = newConstant( "reportName" );
		Expression<String> bookmark = newConstant( "" );
		Map params = new HashMap( );
		//Adds
		drillThrough.setReportName( reportName );
		drillThrough.setBookmark( bookmark );
		drillThrough.setParameters( params );
		//Compares
		assertEquals( drillThrough.getReportName( ), reportName );
		assertEquals( drillThrough.getBookmark( ), bookmark );
		assertEquals( drillThrough.getParameters( ), params );

	}
}