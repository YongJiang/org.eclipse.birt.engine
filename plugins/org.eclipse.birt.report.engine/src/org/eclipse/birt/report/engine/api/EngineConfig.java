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
import java.util.logging.Level;

/**
 * Wraps around configuration settings for report engine. Allows developers to 
 * specify where to look for engine plugins, data drivers, and where to write
 * image files. Allows users to customize data-related properties (i.e., data engine).
 * Also allows engine to provide customized implementations for 
 * image handling, hyperlink handling and font handling, etc.   
 */
public class EngineConfig {

	public static final String CONFIG_VAR_ENGINE_HOME = "BIRT_HOME";		//$NON-NLS-1$
	protected static final String LOG_DESTINATION= "logDest";				//$NON-NLS-1$			
	protected static final String LOG_LEVEL= "logLevel";					//$NON-NLS-1$
	
	/**
	 * stores various configuration objects
	 */
	protected HashMap configObjects = new HashMap();
	
	/**
	 * stores app-wide, app-specific JS scriptable objects 
	 */
	protected HashMap scriptObjects = new HashMap();
	
	/**
	 * store emitter configuration
	 */
	protected HashMap emitterConfigs = new HashMap();
	
	protected IStatusHandler statusHandler = new IStatusHandler() {
		public void initialize(){}
		public void showStatus(String s) { System.out.println(s); }
		public void finish() {};
	};

	protected String tempDir;
	/**
	 * 
	 * @return
	 */
	public HashMap getEmitterConfigs()
	{
		return emitterConfigs;
	}
	/**
	 * constructor
	 */
	public EngineConfig()
	{	
		//set default configruation
		HTMLEmitterConfig emitterConfig = new HTMLEmitterConfig();
		emitterConfig.setActionHandler(new HTMLActionHandler());
		emitterConfig.setImageHandler(new HTMLCompleteImageHandler());
		emitterConfigs.put("html", emitterConfig); //$NON-NLS-1$
	}
	
	/**
	 * set the BIRT_HOME system property
	 * 
	 * @param birtHome the value for the BIRT_HOMT configuration variable
	 */
	public void setEngineHome(String birtHome)
	{
		System.setProperty(CONFIG_VAR_ENGINE_HOME, birtHome);
	}
	
	/**
	 * sets a configuration variable that is available through scripting in engine
	 * 
	 * @param name configuration variable name
	 * @param value configuration variable value
	 */
	public void setConfigurationVariable(String name, String value)
	{
		configObjects.put(name, value);
	}
	
	/**
	 * returns a hash map that contains all the configuration objects
	 * 
	 * @return the configuration object map
	 */
	public HashMap getConfigMap()
	{
		return configObjects;
	}

	/**
	 * returns a hash map that contains all the app-specific, app-wide scriptable Java
	 * objects
	 * 
	 * @return a hash map with all the app-specific, app-wide scriptable Java objects
	 * 
     * @uml.property name="scriptObjects"
	 */
	public HashMap getScriptObjects() {
		return scriptObjects;
	}

	/**
	 * defines an additional Java object that is exposed to BIRT scripting  

	 * @param jsName the name that the object is referenced in JavaScript
	 * @param obj the Java object that is wrapped and scripted
	 */
	public void addScriptableJavaObject(String jsName, Object obj)
	{
		scriptObjects.put(jsName, obj);
	}
	
	/**
	 * this function exists to resolve conflict when more than one extensions supports
	 * a specific format. The specified extensionID allows the user to specify a specific
	 * report emitter for an output format
	 * 
	 * @param extensionID the extension ID for a specific format
	 */
	//public void useExtension(String format, String extensionID)
	//{
		
	//}
	
	
	/**
	 * sets configuration for a specific extension to engine, i.e., an emitter extension
	 * 
	 * @param extensionID identifier for the emitter
	 * @param extensionConfig configuration object for the emitter
	 */
	public void setEmitterConfiguration(String format, Object emitterConfig)
	{
		emitterConfigs.put(format, emitterConfig);
	}
	
	/**
	 * 	sets the handler for reporting report running status.  
	 * 
	 * @param handler status handler
	 */
	public void setStatusHandler(IStatusHandler handler)
	{
		statusHandler = handler;
	}
	
	public IStatusHandler getStatusHandler()
	{
		return statusHandler;
	}
    /**
     * set log configuration, i.e., log file name prefix and log level
     * 
     * @param directoryName - the directory name of the log file(e.g C:\Log). 
     * 						  Engine appends a file name with date and time to the directory name (e.g. C:\Log\BIRT_Engine_2005_02_26_11_26_56.log). 
     * @param level the engine log level
     */
    public void setLogConfig(String directoryName, Level level)
    {
    	configObjects.put(LOG_DESTINATION, directoryName);
    	configObjects.put(LOG_LEVEL, level);
    }

	/**
	 * Sets the directory for the temporary files
	 * 
	 * @param tmpDir
	 *            the specified directory
	 */
	public void setTempDir( String tmpDir )
	{
		this.tempDir = tmpDir;
	}

	/**
	 * @return Returns the TempDir
	 */
	public String getTempDir( )
	{
		return this.tempDir;
	}
}
