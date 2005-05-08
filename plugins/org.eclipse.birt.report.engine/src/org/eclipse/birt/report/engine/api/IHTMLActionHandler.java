/*******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.api;

/**
 * 
 */
public interface IHTMLActionHandler {

	/**
	 * generates a URL based on the action definition
	 * 
	 * @param actionDefn definition of an action
	 * @param context the context for craetingt he hyperlink
	 * @return the URL based on an action 
	 */
	public String getURL(IAction actionDefn, Object context);
}
