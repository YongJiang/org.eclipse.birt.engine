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

/**
 * Action. Action include: hyperlink, drill through and bookmark.
 * 
 * @version $Revision: #1 $ $Date: 2005/01/21 $
 */
public class ActionDesign
{

	/**
	 * hyperlink action
	 */
	public final static int ACTION_HYPERLINK = 1;
	/**
	 * bookmark action
	 */
	public final static int ACTION_BOOKMARK = 2;
	/**
	 * drillthrough action
	 */
	public final static int ACTION_DRILLTHROUGH = 3;

	/**
	 * action type, one of the hyperlink, bookmark drillthrough.
	 */
	protected int actionType;
	/**
	 * hyper link
	 */
	protected Expression hyperlink;
	/**
	 * bookmark.
	 */
	protected Expression bookmark;

	/**
	 * drill through
	 */
	protected DrillThroughActionDesign drillThrough;

	/**
	 * @return Returns the bookmark.
	 */
	public Expression getBookmark( )
	{
		assert this.actionType == ACTION_BOOKMARK;
		return bookmark;
	}

	/**
	 * @param bookmark
	 *            The bookmark to set.
	 */
	public void setBookmark( Expression bookmark )
	{
		this.actionType = ActionDesign.ACTION_BOOKMARK;
		this.bookmark = bookmark;
	}

	/**
	 * @return Returns the hyperlink.
	 */
	public Expression getHyperlink( )
	{
		assert this.actionType == ACTION_HYPERLINK;
		return hyperlink;
	}

	/**
	 * @param hyperlink
	 *            The hyperlink to set.
	 */
	public void setHyperlink( Expression hyperlink )
	{
		this.hyperlink = hyperlink;
		this.actionType = ActionDesign.ACTION_HYPERLINK;
	}

	
	/**
	 * @return Returns the drillThrough.
	 */
	public DrillThroughActionDesign getDrillThrough( )
	{
		assert this.actionType == ACTION_DRILLTHROUGH;
		return drillThrough;
	}
	/**
	 * @param drillThrough The drillThrough to set.
	 */
	public void setDrillThrough( DrillThroughActionDesign drillThrough )
	{
		this.actionType = ACTION_DRILLTHROUGH;
		this.drillThrough = drillThrough;
	}
	/**
	 * @return Returns the type.
	 */
	public int getActionType( )
	{
		return actionType;
	}
}
