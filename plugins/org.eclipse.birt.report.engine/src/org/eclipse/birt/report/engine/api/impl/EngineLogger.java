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

import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * BIRT Logger is a logger associated with the global "org.eclipse.birt" name
 * space. "org.eclipse.birt" is the ancestor of all of the BIRT packages.
 * According to Java 1.4 Logging mechnism, by default all Loggers also send
 * their output to their parent Logger. Thus, in any BIRT package, if developer
 * uses Logger.getLogger( theBIRTClass.class.getName() ) to create a logger, the
 * logger will send the logging requests to the BIRTLogger. And BIRTLogger will
 * log the informatin into the global BIRT log file. The global log file is
 * specified by main application. If developer doesn't want the log of his
 * module is logged into the global BIRT log file, he can simply use
 * logger.setUseParentHandlers(false) to stop sending the logging request to the
 * BIRTLogger. <br>
 * Note: Because of a Java API's bug, an additional .lck file will be created
 * for each log file. Please see
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4775533 for detail.
 */
public class EngineLogger
{

	/**
	 * the logger
	 */
	protected static Logger logger = Logger.getLogger( EngineLogger.class.getName( ) );
	
	static private final String BIRT_NAME_SPACE = "org.eclipse.birt"; //$NON-NLS-1$;

	/**
	 * the handler used by "org.eclipse.birt"
	 */
	static private EngineLoggerHandler sharedHandler = null;

	/**
	 * the user defined logger.
	 */
	static private Logger userLogger = null;

	/**
	 * the user defined logger output directory.
	 */
	static private String dirName = null;
	
	/**
	 * the user defined logger output file.
	 */
	static private String fileName = null;

	/**
	 * the logger uses the file handler
	 */
	static private Logger fileLogger = null;

	/**
	 * the rolling file size for the log file.
	 */
	private static int logRollingSize = 0;
	
	/**
	 * the maximum number of backup files to keep around.
	 */
	private static int logMaxBackupIndex = 1;

	/**
	 * This function should only called by the main application that starts
	 * BIRT. It will add a new log handler to the global BIRT logger.
	 * 
	 * @param directoryName
	 *            - the directory name of the log file (e.g. C:\Log). The final
	 *            file name will be the directory name plus an unique file name.
	 *            For example, if the directory name is C:\Log, the log file
	 *            name will be something like
	 *            C:\Log\ReportEngine_2005_02_26_11_26_56.log
	 * @param logLevel
	 *            - the log level to be set. If logLevel is null, it will be
	 *            ignored.
	 * @param rollingSize
	 * @param maxBackupIndex
	 */
	public static void startEngineLogging( Logger logger, String directoryName,
			String file, Level logLevel, int rollingSize, int maxBackupIndex )
	{
		Logger rootLogger = Logger.getLogger( BIRT_NAME_SPACE );
		if ( sharedHandler == null )
		{
			sharedHandler = new EngineLoggerHandler( rootLogger );
			sharedHandler.setLevel( Level.ALL );
			rootLogger.addHandler( sharedHandler );
		}
		if ( fileLogger != null )
		{
			closeFileLogger( fileLogger );
			fileLogger = null;
		}
		if ( logger != null && isValidLogger( logger ) )
		{
			userLogger = logger;
			sharedHandler.setSharedLogger( userLogger );
		}
		else
		{
			if ( directoryName != null )
			{
				dirName = directoryName;
			}
			if ( file != null )
			{
				fileName = file;
			}
			if ( logLevel == null )
			{
				logLevel = rootLogger.getLevel( );
			}

			logRollingSize = rollingSize;
			logMaxBackupIndex = maxBackupIndex;
			
			if ( logLevel != Level.OFF
					&& ( dirName != null || fileName != null ) )
			{
				fileLogger = createFileLogger( dirName,
						fileName,
						logRollingSize,
						logMaxBackupIndex );
				sharedHandler.setSharedLogger( fileLogger );
			}
			rootLogger.setLevel( logLevel );
			
		}

		rootLogger.setUseParentHandlers( false );
	}

	public static boolean isValidLogger( Logger logger )
	{
		Logger rootLogger = Logger.getLogger( BIRT_NAME_SPACE );
		while ( logger != null )
		{
			if ( logger == rootLogger )
			{
				return false;
			}
			logger = logger.getParent( );
		}
		return true;
	}

	/**
	 * Stop BIRT Logging and close all of the handlers. This function should
	 * only by called by the main application that started BIRT. Note: Because
	 * of a Java API's bug, an additional .lck file will be created for each log
	 * file. Please see
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4775533 for detail.
	 */
	public static void stopEngineLogging( )
	{
		java.security.AccessController
				.doPrivileged( new java.security.PrivilegedAction<Object>( ) {

					public Object run( )
					{
						doStopEngineLogging( );
						return null;
					}
				} );
	}
	
	private static void doStopEngineLogging( )
	{
		Logger rootLogger = Logger.getLogger( BIRT_NAME_SPACE );
		rootLogger.removeHandler( sharedHandler );
		if ( sharedHandler != null )
		{
			sharedHandler.close( );
			sharedHandler = null;
		}
		if ( fileLogger != null )
		{
			closeFileLogger( fileLogger );
			fileLogger = null;
		}
		userLogger = null;
	}

	/**
	 * Change the log level to the newLevel
	 * 
	 * @param newLevel -
	 *            new log level
	 */
	public static void changeLogLevel( Level newLevel )
	{
		if ( newLevel != null )
		{
			if ( userLogger != null )
			{
				if ( newLevel != Level.OFF
						&& fileLogger == null
						&& ( dirName != null || fileName != null ) )
				{
					fileLogger = createFileLogger( dirName,
							fileName,
							logRollingSize,
							logMaxBackupIndex );
					if ( fileLogger != null )
					{
						sharedHandler.setSharedLogger( fileLogger );
					}
				}
			}
			Logger rootLogger = Logger.getLogger( BIRT_NAME_SPACE );
			rootLogger.setLevel( newLevel );
		}
	}

	/**
	 * This is a utility function that will create an unique file name with the
	 * timestamp information in the file name and append the file name into the
	 * directory name. For example, if the directory name is C:\Log, the
	 * returned file name will be C:\Log\ReportEngine_2005_02_26_11_26_56.log.
	 * 
	 * @param directoryName -
	 *            the directory name of the log file.
	 * @return An unique Log file name which is the directory name plus the file
	 *         name.
	 */
	private static String generateUniqueLogFileName( String directoryName,
			String fileName )
	{
		if ( fileName == null )
		{
			SimpleDateFormat df = new SimpleDateFormat( "yyyy_MM_dd_HH_mm_ss" ); //$NON-NLS-1$
			String dateTimeString = df.format( new Date( ) );
			fileName = "ReportEngine_" + dateTimeString + ".log";
		}

		if ( directoryName == null )
			directoryName = ""; //$NON-NLS-1$
		else if ( directoryName.length( ) > 0 )
			directoryName += File.separator;

		return new String( directoryName + fileName ); //$NON-NLS-1$; $NON-NLS-2$;
	}

	private static Logger createFileLogger( String dirName, String fileName,
			int rollingSize, int logMaxBackupIndex )
	{
		try
		{
			if ( dirName != null )
			{
				File directory = new File( dirName );
				if ( !directory.exists( ) )
				{
					if ( !directory.mkdirs( ) )
						throw new IOException( "logDir \"" + dirName
								+ "\" doesn't exist and  be created" );
				}
				else
				{
					if ( directory.isFile( ) )
						throw new IOException( "logDir \"" + dirName
								+ "\" should be a folder instead of a file" );
				}
			}
			String logFileName = generateUniqueLogFileName( dirName, fileName );
			Handler logFileHandler = null;
			if ( rollingSize <= 0 )
			{
				logFileHandler = new FileHandler( logFileName, true );
			}
			else
			{
				logFileHandler = new FileHandler( logFileName,
						rollingSize,
						logMaxBackupIndex,
						true );
			}
			// In BIRT log, we should always use the simple format.
			logFileHandler.setFormatter( new SimpleFormatter( ) );
			logFileHandler.setLevel( Level.FINEST );
			Logger logger = Logger.getAnonymousLogger( );
			logger.addHandler( logFileHandler );
			return logger;
		}
		catch ( SecurityException e )
		{
			logger.log( Level.WARNING, e.getMessage( ), e );
		}
		catch ( IOException e )
		{
			logger.log( Level.WARNING, e.getMessage( ), e );
		}
		return null;
	}

	private static void closeFileLogger( Logger logger )
	{
		Handler[] handles = logger.getHandlers( );
		if ( handles != null )
		{
			for ( int i = 0; i < handles.length; i++ )
			{
				handles[i].close( );
			}
		}
	}
}