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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IParameterSelectionChoice;
import org.eclipse.birt.report.engine.api.IScalarParameterDefn;
import org.eclipse.birt.report.model.elements.ReportDesign;

/**
 * Wraps around a parameter selection choice
 */
public class ParameterSelectionChoice implements IParameterSelectionChoice, Cloneable
{
	protected Locale locale;
	protected ReportDesign design;
	protected String label;
	protected String labelKey;
	
	protected Object value;
	
	protected Logger log = Logger.getLogger( ParameterSelectionChoice.class.getName( ) );
	
	public ParameterSelectionChoice(ReportDesign design)
	{
		this.design = design;
	}
	
	/**
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}
	
	/**
	 * @param lableKey
	 * @param label
	 */
	public void setLabel(String lableKey, String label)
	{
		this.label = label;
		this.labelKey = lableKey;
	}
	
	/** 
	 * @param value
	 */
	public void setValue(String value, int type) {
		try {
			switch (type) {
				case IScalarParameterDefn.TYPE_BOOLEAN:
					this.value = DataTypeUtil.toBoolean(value);
					break;
				case IScalarParameterDefn.TYPE_DATE_TIME:
					this.value = DataTypeUtil.toDate(value);
					break;
				case IScalarParameterDefn.TYPE_DECIMAL:
					this.value = DataTypeUtil.toBigDecimal(value);
					break;
				case IScalarParameterDefn.TYPE_FLOAT:
					this.value = DataTypeUtil.toDouble(value);
					break;
				default:
					this.value = value;
					break;
			}
		} 
		catch (BirtException e) {
			log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			this.value = value;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api2.IParameterSelectionChoice#getLabel()
	 */
	public String getLabel()
	{
		if ( labelKey == null )
			return label;
		
		String ret = design.getMessage( labelKey, 
				(locale == null ) ? Locale.getDefault() : locale);
		return (ret == null) ? label : ret;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	/**
	 * @return returns the choice value
	 */
	public Object getValue()
	{
		return value;
	}
}
