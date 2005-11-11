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
 * Provides the interfaces for the Table Content
 * 
 * 
 * @version $Revision: 1.4 $ $Date: 2005/05/08 06:59:45 $
 */
public interface ITableContent extends IContent
{

	int getColumnCount( );

	IColumn getColumn( int index );

	void addColumn( IColumn column );

	/**
	 * @return Returns the caption.
	 */
	public String getCaption( );

	/**
	 * @return
	 */
	public boolean isHeaderRepeat( );

	public ITableBandContent getHeader( );

	public ITableBandContent getBody( );

	public ITableBandContent getFooter( );
}