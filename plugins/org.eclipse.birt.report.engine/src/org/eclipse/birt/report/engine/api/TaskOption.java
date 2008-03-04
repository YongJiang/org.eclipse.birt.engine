/*******************************************************************************
 * Copyright (c)2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.api;

import java.util.HashMap;
import java.util.Map;

public class TaskOption implements ITaskOption
{
	/**
	 * a hash map that stores the rendering options
	 */
	protected Map options;

	/**
	 * constructor
	 */
	public TaskOption( )
	{
		options = new HashMap( );
	}

	public TaskOption( Map options )
	{
		this.options = options;
	}

	/**
	 * set value for one rendering option
	 * 
	 * @param name
	 *            the option name
	 * @param value
	 *            value for the option
	 */
	public void setOption( String name, Object value )
	{
		options.put( name, value );
	}

	/**
	 * get option value for one rendering option
	 * 
	 * @param name
	 *            the option name
	 * @return the option value
	 */
	public Object getOption( String name )
	{
		return options.get( name );
	}

	/**
	 * Check if an option is defined.
	 */
	public boolean hasOption( String name )
	{
		return options.containsKey( name );
	}

	public Map getOptions( )
	{
		return options;
	}

	protected String getStringOption( String name )
	{
		Object value = options.get( name );
		if ( value instanceof String )
		{
			return (String) value;
		}
		return null;
	}

	protected boolean getBooleanOption( String name, boolean defaultValue )
	{
		Object value = options.get( name );
		if ( value instanceof Boolean )
		{
			return ( (Boolean) value ).booleanValue( );
		}
		else if ( value instanceof String )
		{
			return "true".equalsIgnoreCase( (String) value ); //$NON-NLS-1$
		}
		return defaultValue;
	}
}
