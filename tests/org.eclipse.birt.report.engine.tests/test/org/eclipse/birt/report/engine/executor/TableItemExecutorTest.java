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


/**
 * 
 * table item executor test
 * 
 * @version $Revision: 1.11 $ $Date: 2005/11/18 06:25:47 $
 */
public class TableItemExecutorTest extends ReportItemExecutorTestAbs
{

	/**
	 * test single table
	 * 
	 * @throws Exception
	 */
	public void testExcuteTable1( ) throws Exception
	{
		compare( "table1.xml", "table1.txt" );
	}
	
	public void testEmptyTable() throws Exception
	{
		compare("empty_table.xml", "empty_table.txt");
	}
}