package org.eclipse.birt.report.engine.api.script.element;

import org.eclipse.birt.report.engine.api.script.ScriptException;

/**
 * Represents a row in the scripting environment
 */
public interface IRow extends IReportElement
{

	/**
	 * Gets a handle to deal with the row's height.
	 * 
	 * @return a DimensionHandle for the row's height.
	 */

	String getHeight( );

	/**
	 * Returns the bookmark of this row.
	 * 
	 * @return the bookmark of this row
	 */

	String getBookmark( );

	/**
	 * Sets the bookmark of this row.
	 * 
	 * @param value
	 *            the bookmark to set
	 * @throws ScriptException
	 *             if the property is locked.
	 */

	void setBookmark( String value ) throws ScriptException;

}