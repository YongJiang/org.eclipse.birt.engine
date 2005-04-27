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

package org.eclipse.birt.report.engine.api.impl;

import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.model.elements.ReportDesign;

/**
 * Base class for parameter definition. 
 */
public class ParameterDefnBase implements IParameterDefnBase, Cloneable
{
	protected int 			parameterType;
	protected String 		displayName;
	protected String 		displayNameKey;
	protected String 		helpText;
	protected String 		helpTextKey;
	protected String 		name;
	
	protected ReportDesign 	reportDesign;
	protected Locale 		locale = null;
	
	/**
	 * @param reportDesign The reportDesign to set.
	 */
	public void setReportDesign(ReportDesign reportDesign)
	{
		this.reportDesign = reportDesign;
	}
	
	/**
	 * @param locale the locale under which the parameter display name, 
	 * 	help text need to be returned
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getParameterType()
	 */
	public int getParameterType()
	{
		return parameterType;
	}

	/**
	 * @param parameterType The parameterType to set.
	 */
	public void setParameterType(int parameterType)
	{
		this.parameterType = parameterType;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getDisplayName()
	 */
	public String getDisplayName( )
	{
		String ret = reportDesign.getMessage( displayNameKey, 
				(locale == null ) ? Locale.getDefault() : locale);
		if (ret == null)
			return displayName;
		return ret;		
	}
	
	/**
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public void setDisplayNameKey(String displayNameKey)
	{
		this.displayNameKey = displayNameKey;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getHelpText()
	 */
	public String getHelpText( )
	{
		String ret = reportDesign.getMessage( helpTextKey, 
				(locale == null ) ? Locale.getDefault() : locale);
		if (ret == null)
			return helpText;
		return ret;		
	}
	
	/**
	 * @param helpText The help text to set.
	 */
	public void setHelpText(String helpText)
	{
		this.helpText = helpText;
	}
	
	/**
	 * @param helpTextKey the message key for help text
	 */
	public void setHelpTextKey(String helpTextKey)
	{
		this.helpTextKey = helpTextKey;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getUserPropertyValues()
	 */
	public Map getUserPropertyValues()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IParameterDefnBase#getUserPropertyValue(java.lang.String)
	 */
	public String getUserPropertyValue(String name)
	{
		return null;
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
