package org.eclipse.birt.report.engine.api.script.element;

import org.eclipse.birt.report.engine.api.script.ScriptException;

/**
 * Represents a the design of a DataItem in the scripting environment
 */
public interface IDataItem extends IReportItem
{

	/**
	 * Returns a handle to work with the action property, action is a structure
	 * that defines a hyperlink.
	 * 
	 * @return a handle to the action property, return <code>null</code> if
	 *         the action has not been set on the data item.
	 * @see ActionHandle
	 */
	IAction getAction( );

	/**
	 * Returns the expression that gives the value that the data item displays.
	 * 
	 * @return the value expression
	 */
	String getValueExpr( );

	/**
	 * Sets the expression for the value that the data item is to display.
	 * 
	 * @param expr
	 *            the expression to set
	 * @throws ScriptException
	 *             If the property is locked.
	 */
	void setValueExpr( String expr ) throws ScriptException;

	/**
	 * Returns the help text of this data item.
	 * 
	 * @return the help text
	 */
	String getHelpText( );

	/**
	 * Sets the help text of this data item.
	 * 
	 * @param value
	 *            the help text
	 * 
	 * @throws ScriptException
	 *             if the property is locked.
	 */
	void setHelpText( String value ) throws ScriptException;

	/**
	 * Returns the help text resource key of this data item.
	 * 
	 * @return the help text key
	 */
	String getHelpTextKey( );

	/**
	 * Sets the resource key of the help text of this data item.
	 * 
	 * @param value
	 *            the resource key of the help text
	 * 
	 * @throws ScriptException
	 *             if the property is locked.
	 */
	void setHelpTextKey( String value ) throws ScriptException;

}