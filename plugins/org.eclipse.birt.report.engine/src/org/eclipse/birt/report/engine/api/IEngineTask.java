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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.core.archive.IDocArchiveReader;

import com.ibm.icu.util.ULocale;

/**
 * defines common features for an engine task. A task captures a set of operations that engine
 * performs to get a unit of work done.  
 */
public interface IEngineTask {
	/**
	 * sets the task locale
	 * @param locale the task locale
	 */
	public abstract void setLocale(Locale locale);

	/**
	 * sets the task locale
	 * @param locale the task locale
	 */
	public abstract void setLocale(ULocale locale);

	/**
	 * sets the task context.
	 * 
	 * this method must be called before the run/render/execute etc.
	 * 
	 * @param context -
	 *            task contexts in a map. The map contains name-value pairs
	 */
	public abstract void setAppContext( Map context );
	
	/**
	 * returns the locale for running the task
	 * @return the locale for running the task
	 */
	public abstract Locale getLocale();
	
	/**
	 * returns the locale for running the task
	 * @return the locale for running the task
	 */
	public abstract ULocale getULocale();
	
	/**
	 * returns the context objects for the task.
	 * 
	 * The return appContext is read only, the user should never try to modify
	 * the value.
	 * 
	 * @return the task contexts
	 */
	public abstract Map getAppContext();
	
	/**
	 * returns the report engine object
	 * @return the engine object
	 */
	public abstract IReportEngine getEngine();
	
	/**
	 * defines an additional Java object that is exposed to BIRT scripting at a per-task level
	 * @param jsName the name that the object is referenced in JavaScript
	 * @param obj the Java object that is wrapped and scripted
	 * @deprecated user should add it to appContext.
	 */
	public void addScriptableJavaObject(String jsName, Object obj);
	
	 /**
     * returns an identifier for the task. The identifier can be used to identify the
     * task, especially when writing logs in a multi-threaded environment.  
     * @return an identifier for the task.
     */
    public abstract int getID();

	/**
	 * returns the runnable report design object
	 * @return the runnable report design object
	 */
	public abstract IReportRunnable getReportRunnable();
	
	/**
	 * set all parameter valuess
	 * @param params a hash map with all parameters
	 */
	public abstract void setParameterValues(Map params);
	
	/**
	 * sets one parameter value
	 * @param name parameter name
	 * @param value parameter value
	 */
	public abstract void setParameterValue(String name, Object value);
	
	/**
	 * returns the parameter name/value collection
	 * @return the parameter names/values in a hash map
	 */
	public abstract HashMap getParameterValues();
	
	/**
     * returns the value of a parameter.
     * @return the parameter value.
     */
	public abstract Object getParameterValue(String name);
	
	/**
	 * @return whether the parameter validation succeeds <br>
	 */
	public boolean validateParameters( );
	
	/**
	 * Sets parameter value and display text.
	 * 
	 * @param name
	 *            parameter name.
	 * @param value
	 *            value.
	 * @param displayText
	 *            display text.
	 */
	public void setParameter( String name, Object value, String displayText );

	/**
	 * Gets parameter display text by parameter name.
	 * 
	 * @param name
	 *            parameter name.
	 * @return display text.
	 */
	public String getParameterDisplayText( String name);


	/**
	 * Sets display text of a parameter with specified name.
	 *
	 * @param name name of the parameter.
	 * @param displayText display text to set.
	 */
	public void setParameterDisplayText( String name, String displayText );

	/**
	 * set the cancel flag if the task is running.
	 * the task can re-run if it is cancedl.
	 */
	public void cancel();

	/**
	 * cancels the task with a signal.
	 * you can't call this method in the same thread with runTask
	 * The signal will be notified when the task finishes the cancel.
	 * @deprecated user should use cancel() instead.
	 */
	public void cancel( Object signal );

	/**
	 * return a flag if the user called cancel.
	 * 
	 * @return true the user has called cancel, false the user doesn't call
	 *         cancel.
	 */
	public boolean getCancelFlag( );

	/**
	 * the task is not running yet
	 */
	static final int STATUS_NOT_STARTED = 0;
	/**
	 * the task is running
	 */
	static final int STATUS_RUNNING = 1;
	/**
	 * the task is finished with sucessful
	 */
	static final int STATUS_SUCCEEDED = 2;
	/**
	 * the task is finished with errors
	 */
	static final int STATUS_FAILED = 3;
	/**
	 * the task is finished by cancled
	 */
	static final int STATUS_CANCELLED = 4;
	/**
	 * get the status of task
	 * @return the status
	 */
	int getStatus();

	/**
	 * continue the task execution if there is an error.
	 */
	static final int CONTINUE_ON_ERROR = 0;
	/**
	 * cancel the task execution if there is an error.
	 */
	static final int CANCEL_ON_ERROR = 1;

	/**
	 * set the error handling mode for the first error.
	 * 
	 * If the options is set to cancel_on_error, the task is cancelled just like
	 * the user calls cancel().
	 * 
	 * If the option is set the continue_on_erro, the task will continue and
	 * saves the error into the error list.
	 * 
	 * @param option
	 *            the error handling mode.
	 * @return
	 */
	void setErrorHandlingOption( int option );
	
	/**
	 * close the task, relese any resources. 
	 */
	public void close();
	
	/**
	 * set the data source used by the engine task.
	 * The dataSource is opend and closed by this task.
	 * @param dataSource data source archive.
	 */
	public void setDataSource( IDocArchiveReader dataSource );

	/**
	 * Gets all errors.
	 * 
	 * @return the errors list.
	 */
	public List getErrors( );
}