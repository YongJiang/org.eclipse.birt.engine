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

package org.eclipse.birt.report.engine.extension;

import javax.olap.cursor.CubeCursor;

public interface ICubeResultSet extends IBaseResultSet
{

	CubeCursor getCubeCursor( );

	String getCellIndex( );

	void skipTo( String cellIndex );

}
