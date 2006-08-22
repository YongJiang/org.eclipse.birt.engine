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

package org.eclipse.birt.report.engine.api;

import java.util.List;

/**
 * Represents a whole TOC tree.
 */
public interface ITOCTree
{
	/**
	 * Gets the TOCNode with the specified id. Root of the whole TOC tree will
	 * be returned if <code>tocId</code> is <code>null</code>.
	 * 
	 * @param tocNodeId
	 *            the id of the toc.
	 * @return TOCNode with specified Id. NULL if not found.
	 */
	TOCNode findTOC( String tocId );

	/**
	 * Gets the TOCNodes with specified TOC value.
	 * 
	 * @param tocValue
	 *            the name of the toc.
	 * @return List of all tocs with the specified name.
	 */
	List findTOCByValue( Object tocValue );

	/**
	 * Gets a TOC tree with specified format and locale. The returned tree
	 * contains all the TOC nodes which are visible under the specified
	 * format and the display string are localized using the specified locale.
	 * When <code>null</code> is provided as format, the returned TOCs will
	 * includes all the TOCs.
	 * 
	 * @param format
	 *            the format, "pdf", e.g..
	 * @param locale
	 *            the locale.
	 * @return a TOCNode tree root.
	 */
	TOCNode getRoot( );
}
