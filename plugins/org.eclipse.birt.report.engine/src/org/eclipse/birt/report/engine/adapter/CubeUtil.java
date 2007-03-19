/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.adapter;

import java.util.List;

import javax.olap.OLAPException;
import javax.olap.cursor.CubeCursor;
import javax.olap.cursor.EdgeCursor;

import org.eclipse.birt.report.engine.api.EngineException;

/**
 * This is a utility class which is used by Engine to create a unique
 * locator for a cube cursor.
 */

public class CubeUtil
{
	private static final String POSITION_DELIMITER = "::";
	/**
	 * Get the position id of a CubeCursor. The position id is decided by
	 * the combination of edge cursors.
	 * 
	 * @param cursor
	 * @return
	 * @throws OLAPException
	 */
	public static String getPositionID( CubeCursor cursor ) throws OLAPException
	{
		String result = "";
		List ordinateEdge = getAllEdges( cursor );

		for ( int i = 0; i < ordinateEdge.size( ); i++ )
		{
			EdgeCursor edge = (EdgeCursor) ordinateEdge.get( i );
			result += POSITION_DELIMITER + edge.getPosition( );
		}

		return result;
	}

	/**
	 * Get all EdgeCursor of a CubeCursor.
	 * 
	 * @param cursor
	 * @return
	 * @throws OLAPException
	 */
	private static List getAllEdges( CubeCursor cursor ) throws OLAPException
	{
		List ordinateEdge = cursor.getOrdinateEdge( );
		ordinateEdge.addAll( cursor.getPageEdge( ) );
		return ordinateEdge;
	}
	
	/**
	 * Set cube cursor to a given position. A cube cursor's position is decided by its edge cursors.
	 * @param cursor
	 * @param position
	 * @throws OLAPException 
	 * @throws EngineException 
	 */
	public static void positionCursor( CubeCursor cursor, String position ) throws OLAPException, EngineException
	{
		if ( position == null || position.trim( ).length( ) == 0 )
			return;
		String[] positions = position.split( "\\Q"+POSITION_DELIMITER+"\\E" );
		List edges = getAllEdges( cursor );
		
		if ( positions.length != edges.size( ) )
			throw new EngineException( "Invalid cube cursor position:" + position );
		
		for ( int i = 0; i < edges.size( ); i++ )
		{
			((EdgeCursor)edges.get( i )).setPosition( new Long( positions[i]).longValue( ) );
		}
	}
}
