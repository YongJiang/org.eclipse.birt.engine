/*******************************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.layout.pdf.font;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.report.engine.util.SecurityUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;

/**
 * FontMappingManagerFactory used to created all font mapping managers.
 * <p>
 * The user can define multiple font mapping configurations in
 * org.eclipse.birt.report.fonts fragment. The configuration has parent-child
 * relation ship. The configurations defined in the child configuration update
 * the one defined in the parent configurations. The inherit sequence of the
 * configurations are:
 * <li>format_os_language_country_variant</li>
 * <li>format_os_language_country</li>
 * <li>format_os_language</li>
 * <li>format_os</li>
 * <li>format</li>
 * <li>os_language_country_variant
 * <li>os_language_country</li>
 * <li>os_lanauge</li>
 * <li>os</li>
 * <li>fontsConfig</li>
 * </ul>
 * 
 * The OS name is getting from either osgi.os or os.name. If it is getting from
 * the osgi.os property, the names can be (see the
 * <code>Platform.knownOSValues()</code> ):
 * <ul>
 * <li>win32</li>
 * <li>linux</li>
 * <li>aix</li>
 * <li>solaris</li>
 * <li>hpux</li>
 * <li>qnx</li>
 * <li>macosx</li>
 * </ul>
 * 
 */
public class FontMappingManagerFactory
{

	/** the logger logging the error, debug, warning messages. */
	protected static Logger logger = Logger.getLogger( FontConfigReader.class
			.getName( ) );

	protected static FontMappingManagerFactory instance;

	public static synchronized FontMappingManagerFactory getInstance( )
	{
		if ( instance == null )
		{
			instance = new FontMappingManagerFactory( );
		}
		return instance;
	}

	/**
	 * all font paths registered by this factory
	 */
	protected HashSet fontPathes = new HashSet( );

	/**
	 * font encodings, it is used by iText to load the Type1 fonts
	 */
	protected HashMap fontEncodings = new HashMap( );

	/**
	 * all loaded configurations
	 * 
	 * the structure of the cache is:
	 * <ul>
	 * <li>key: configuration name</li>
	 * <li>value: FontMappingConfig</li>
	 * </ul>
	 */
	protected HashMap cachedConfigs = new HashMap( );

	/**
	 * all created mapping managers.
	 * 
	 * <p>
	 * The cache has two kind keys:
	 * <p>
	 * cached by the font mapping config
	 * <ul>
	 * <li> key: FontMappingConfig</li>
	 * <li>value: each value is a HashMap
	 * <ul>
	 * <li>key: String[] sequence</li>
	 * <li>value: FontMappingManager</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * cached by the format.
	 * <ul>
	 * <li>key: format</li>
	 * <li>value: HashMap
	 * <ul>
	 * <li>key: locale</li>
	 * <li>value: FontMappingManager</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 */
	protected HashMap cachedManagers = new HashMap( );

	protected FontMappingManagerFactory( )
	{
		// Register java fonts.
		registerJavaFonts();
		
		// register the embedded font directorys
		String embeddedFonts = getEmbededFontPath( );
		if ( embeddedFonts != null )
		{
			registerFontPath( embeddedFonts );
		}
	}

	public synchronized FontMappingManager getFontMappingManager(
			String format, Locale locale )
	{
		HashMap managers = (HashMap) cachedManagers.get( format );
		if ( managers == null )
		{
			managers = new HashMap( );
			cachedManagers.put( format, managers );
		}
		FontMappingManager manager = (FontMappingManager) managers.get( locale );
		if ( manager == null )
		{
			manager = createFontMappingManager( format, locale );
			managers.put( locale, manager );
		}
		return manager;
	}

	public FontMappingManager createFontMappingManager(
			FontMappingConfig config, Locale locale )
	{
		// Register the fonts defined in JRE fonts directory.
		registerJavaFonts( );
		
		// register the fonts defined in the configuration
		Iterator iter = config.fontPaths.iterator( );
		while ( iter.hasNext( ) )
		{
			String fontPath = (String) iter.next( );
			if ( !fontPathes.contains( fontPath ) )
			{
				fontPathes.add( fontPath );
				registerFontPath( fontPath );
			}
		}
		// add the font encodings to the global encoding
		fontEncodings.putAll( config.fontEncodings );

		return new FontMappingManager( this, null, config, locale );
	}

	private void registerJavaFonts( )
	{
		AccessController.doPrivileged( new PrivilegedAction<Object>( ) {

			public Object run( )
			{
				String javaHome = System.getProperty( "java.home" );
				String fontsFolder = javaHome + File.separatorChar + "lib"
						+ File.separatorChar + "fonts";
				FontFactory.registerDirectory( fontsFolder );
				return null;
			}
		} );
	}

	protected FontMappingManager createFontMappingManager( String format,
			Locale locale )
	{
		String formatString = format.toLowerCase();
		// we have max 19 configs
		String[] configNames = new String[19];
		int count = 0;
		String osgiName = getOSGIOSName( );
		String osName = getOSName( );
		String language = locale.getLanguage( );
		String country = locale.getCountry( );
		String variant = locale.getVariant( );

		StringBuffer sb = new StringBuffer( );
		// fontsConfig.xml
		configNames[count++] = sb.append( CONFIG_NAME ).toString( );
		// fontsConfig_<osgi-os>.xml
		if ( osgiName != null )
		{
			configNames[count++] = sb.append( '_' ).append( osgiName )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( language )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( country )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( variant )
					.toString( );
		}
		// fontsConfig_<java-os>.xml
		if ( osName != null && !osName.equals( osgiName ) )
		{
			sb.setLength( 0 );
			sb.append( CONFIG_NAME );
			configNames[count++] = sb.append( '_' ).append( osName ).toString( );
			configNames[count++] = sb.append( '_' ).append( language )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( country )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( variant )
					.toString( );
		}
		sb.setLength( 0 );
		// fontsConfig_format_<osgi-os>
		configNames[count++] = sb.append( CONFIG_NAME ).append( '_' ).append(
				formatString ).toString( );
		if ( osgiName != null )
		{
			configNames[count++] = sb.append( '_' ).append( osgiName )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( language )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( country )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( variant )
					.toString( );
		}
		// fongsConfig_format_<java-os>
		if ( osName != null && !osName.equals( osgiName ) )
		{
			sb.setLength( 0 );
			sb.append( CONFIG_NAME ).append( '_' ).append( formatString );
			configNames[count++] = sb.append( '_' ).append( osName ).toString( );
			configNames[count++] = sb.append( '_' ).append( language )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( country )
					.toString( );
			configNames[count++] = sb.append( '_' ).append( variant )
					.toString( );
		}

		FontMappingManager manager = null;
		for ( int i = 0; i < count; i++ )
		{
			FontMappingConfig config = loadFontMappingConfig( configNames[i] );
			if ( config != null )
			{
				manager = createFontMappingManager( manager, config, locale );
			}
		}
		return manager;
	}

	protected FontMappingManager createFontMappingManager(
			FontMappingManager parent, FontMappingConfig config, Locale locale )
	{
		HashMap managers = (HashMap) cachedManagers.get( config );
		if ( managers == null )
		{
			managers = new HashMap( );
			cachedManagers.put( config, managers );
		}
		FontMappingManager manager = (FontMappingManager) managers.get( locale );
		if ( manager == null )
		{
			manager = new FontMappingManager( this, parent, config, locale );
			managers.put( locale, manager );
		}
		return manager;
	}

	static final String CONFIG_NAME = "fontsConfig";

	private String getOSName( )
	{
		String osName = SecurityUtil.getSystemProperty( "os.name" );
		if ( osName != null )
		{
			return osName.replace( ' ', '_' );
		}
		return null;
	}

	private String getOSGIOSName( )
	{
		String osName = Platform.getOS( );
		if ( Platform.OS_UNKNOWN.equals( osName ) )
		{
			return null;
		}
		return osName;
	}

	protected FontMappingConfig getFontMappingConfig( String configName )
	{
		FontMappingConfig config = (FontMappingConfig) cachedConfigs
				.get( configName );
		if ( config == null )
		{
			if ( !cachedConfigs.containsKey( configName ) )
			{
				config = loadFontMappingConfig( configName );
				cachedConfigs.put( configName, config );
			}
		}
		return config;
	}

	/**
	 * load the configuration file in the following order:
	 * 
	 * @param format
	 */
	protected FontMappingConfig loadFontMappingConfig( String configName )
	{
		// try to load the format specific configuration
		URL url = getConfigURL( configName );
		if ( url != null )
		{
			try
			{
				long start = System.currentTimeMillis( );
				FontMappingConfig config = new FontConfigReader( )
						.parseConfig( url );
				long end = System.currentTimeMillis( );
				logger.info( "load font config in " + url + " cost " +
						( end - start ) + "ms" );
				if ( config != null )
				{
					// try to load the font in the fontPaths
					Iterator iter = config.fontPaths.iterator( );
					while ( iter.hasNext( ) )
					{
						String fontPath = (String) iter.next( );
						if ( !fontPathes.contains( fontPath ) )
						{
							fontPathes.add( fontPath );
							registerFontPath( fontPath );
						}
					}
					// add the font encodings to the global encoding
					fontEncodings.putAll( config.fontEncodings );
					return config;
				}
			}
			catch ( Exception ex )
			{
				logger.log( Level.WARNING, configName + ":" + ex.getMessage( ),
						ex );
			}
		}
		return null;
	}

	protected URL getConfigURL( String configName )
	{
		// try to load the format specific configuration
		Bundle bundle = Platform
				.getBundle( "org.eclipse.birt.report.engine.fonts" ); //$NON-NLS-1$
		if ( bundle != null )
		{
			return bundle.getEntry( configName + ".xml" );
		}
		return null;
	}

	/**
	 * All generated composite fonts.
	 * 
	 * <p>
	 * composite font are generated by the composite font configuration and the
	 * search sequence. Each composite font also contains a parent.
	 * 
	 * <p>
	 * the cache structures are:
	 * <ul>
	 * <li>key: composite font configuration</li>
	 * <li>value: HashMap which contains:
	 * <ul>
	 * <li>key: String[] search sequence</li>
	 * <li>value: Composite font object</li>
	 * </ul>
	 * </li>
	 * </ul>
	 */
	HashMap cachedCompositeFonts = new HashMap( );

	CompositeFont createCompositeFont( FontMappingManager manager,
			CompositeFontConfig fontConfig, String[] sequence )
	{
		HashMap fonts = (HashMap) cachedCompositeFonts.get( fontConfig );
		if ( fonts == null )
		{
			fonts = new HashMap( );
			cachedCompositeFonts.put( fontConfig, fonts );
		}
		CompositeFont font = (CompositeFont) fonts.get( sequence );
		if ( font == null )
		{
			font = new CompositeFont( manager, fontConfig, sequence );
			fonts.put( sequence, font );
		}
		return font;
	}

	private HashMap baseFonts = new HashMap( );

	/**
	 * Creates iText BaseFont with the given font family name.
	 * 
	 * @param ffn
	 *            the specified font family name.
	 * @return the created BaseFont.
	 */
	public BaseFont createFont( String familyName, int fontStyle )
	{
		String key = familyName + fontStyle;
		synchronized ( baseFonts )
		{
			BaseFont font = (BaseFont) baseFonts.get( key );
			if ( font == null )
			{
				try
				{
					String fontEncoding = (String) fontEncodings
							.get( familyName );
					if ( fontEncoding == null )
					{
						fontEncoding = BaseFont.IDENTITY_H;
					}
					// FIXME: code view verify if BaseFont.NOT_EMBEDDED or
					// BaseFont.EMBEDDED should be used.
					font = FontFactory.getFont( familyName, fontEncoding,
							BaseFont.NOT_EMBEDDED, 14, fontStyle )
							.getBaseFont( );
					if ( font != null )
					{
						baseFonts.put( key, font );
					}
				}
				catch ( Throwable de )
				{
					return null;
				}
			}
			return font;
		}
	}

	private void registerFontPath( final String fontPath )
	{
		AccessController.doPrivileged( new PrivilegedAction<Object>( ) {

			public Object run( )
			{
				long start = System.currentTimeMillis( );
				File file = new File( fontPath );
				if ( file.exists( ) )
				{
					if ( file.isDirectory( ) )
					{
						FontFactory.registerDirectory( fontPath );
					}
					else
					{
						FontFactory.register( fontPath );
					}
				}
				long end = System.currentTimeMillis( );
				logger.info( "register fonts in " + fontPath + " cost:"
						+ ( end - start ) + "ms" );
				return null;
			}
		} );
	}

	protected String getEmbededFontPath( )
	{
		Bundle bundle = Platform
				.getBundle( "org.eclipse.birt.report.engine.fonts" ); //$NON-NLS-1$
		Path path = new Path( "/fonts" ); //$NON-NLS-1$

		URL fileURL = FileLocator.find( bundle, path, null );
		if ( null == fileURL )
			return null;
		String fontPath = null;
		try
		{
			// 171369 patch provided by Arne Degenring <public@degenring.de>
			fontPath = FileLocator.toFileURL( fileURL ).getPath( );
			if ( fontPath != null && fontPath.length( ) >= 3 &&
					fontPath.charAt( 2 ) == ':' )
			{
				// truncate the first '/';
				return fontPath.substring( 1 );
			}
			else
			{
				return fontPath;
			}
		}
		catch ( IOException ioe )
		{
			logger.log( Level.WARNING, ioe.getMessage( ), ioe );
			return null;
		}
	}
}
