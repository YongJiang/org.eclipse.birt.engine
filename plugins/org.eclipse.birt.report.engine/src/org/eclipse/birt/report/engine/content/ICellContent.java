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
 * Provides the interfaces for Cell Content
 * 
 * @version $Revision$ $Date$
 */
public interface ICellContent extends IStyledElementContent
{

	/**
	 * 
	 * @return the column span
	 */
	public int getColSpan( );

	/**
	 * @return Returns the rowSpan.
	 */
	public int getRowSpan( );

	/**
	 * 
	 * @return the column number
	 */
	public int getColumn( );

	/**
	 * 
	 * @return drop type
	 */
	public String getDrop( );

	/**
	 * @param rowSpan
	 *            The rowSpan to set.
	 */
	public void setRowSpan( int rowSpan );
}