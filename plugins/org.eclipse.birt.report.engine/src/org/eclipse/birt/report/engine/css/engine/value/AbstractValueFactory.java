/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - modification of Batik's AbstractValueFactory.java to support BIRT's CSS rules
 *******************************************************************************/

package org.eclipse.birt.report.engine.css.engine.value;

import java.net.URI;

import org.w3c.dom.DOMException;

/**
 * This class provides a base implementation for the value factories.
 * 
 * @version $Id: AbstractValueFactory.java,v 1.2.14.1 2006/09/15 05:33:32 lyu Exp $
 */
public abstract class AbstractValueFactory
{

	/**
	 * Returns the name of the property handled.
	 */
	public abstract String getPropertyName( );

	/**
	 * Creates a DOM exception, given an invalid identifier.
	 */
	protected DOMException createInvalidIdentifierDOMException( String ident )
	{
		Object[] p = new Object[]{getPropertyName( ), ident};
		String s = Messages.formatMessage( "invalid.identifier", p );
		return new DOMException( DOMException.SYNTAX_ERR, s );
	}

	/**
	 * Creates a DOM exception, given an invalid lexical unit type.
	 */
	protected DOMException createInvalidLexicalUnitDOMException( short type )
	{
		Object[] p = new Object[]{getPropertyName( ), new Integer( type )};
		String s = Messages.formatMessage( "invalid.lexical.unit", p );
		return new DOMException( DOMException.NOT_SUPPORTED_ERR, s );
	}

	/**
	 * Creates a DOM exception, given an invalid float type.
	 */
	protected DOMException createInvalidFloatTypeDOMException( short t )
	{
		Object[] p = new Object[]{getPropertyName( ), new Integer( t )};
		String s = Messages.formatMessage( "invalid.float.type", p );
		return new DOMException( DOMException.INVALID_ACCESS_ERR, s );
	}

	/**
	 * Creates a DOM exception, given an invalid float value.
	 */
	protected DOMException createInvalidFloatValueDOMException( float f )
	{
		Object[] p = new Object[]{getPropertyName( ), new Float( f )};
		String s = Messages.formatMessage( "invalid.float.value", p );
		return new DOMException( DOMException.INVALID_ACCESS_ERR, s );
	}

	/**
	 * Creates a DOM exception, given an invalid string type.
	 */
	protected DOMException createInvalidStringTypeDOMException( short t )
	{
		Object[] p = new Object[]{getPropertyName( ), new Integer( t )};
		String s = Messages.formatMessage( "invalid.string.type", p );
		return new DOMException( DOMException.INVALID_ACCESS_ERR, s );
	}

	protected DOMException createMalformedLexicalUnitDOMException( )
	{
		Object[] p = new Object[]{getPropertyName( )};
		String s = Messages.formatMessage( "malformed.lexical.unit", p );
		return new DOMException( DOMException.INVALID_ACCESS_ERR, s );
	}

	protected DOMException createDOMException( )
	{
		Object[] p = new Object[]{getPropertyName( )};
		String s = Messages.formatMessage( "invalid.access", p );
		return new DOMException( DOMException.NOT_SUPPORTED_ERR, s );
	}

	protected static String resolveURI( URI base, String value )
	{
		try
		{
			return base.resolve( value ).toString( );
		}
		catch ( Exception ex )
		{
			return value;
		}
	}

}
