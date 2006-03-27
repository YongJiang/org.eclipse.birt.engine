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

package org.eclipse.birt.report.engine.content;

/**
 * Provides the interfaces for the Row AbstractContent
 * 
 * 
 * @version $Revision: 1.7 $ $Date: 2005/11/17 01:40:45 $
 */
public interface IRowContent extends IContent
{
	public int getRowID();
	
	public void setRowID(int rowID);
	
	public int getRowType();
	
	public int getGroupLevel();
}