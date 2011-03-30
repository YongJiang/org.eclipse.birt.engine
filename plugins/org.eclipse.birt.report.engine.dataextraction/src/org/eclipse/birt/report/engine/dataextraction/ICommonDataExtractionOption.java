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

package org.eclipse.birt.report.engine.dataextraction;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Map;

import org.eclipse.birt.report.engine.api.IDataExtractionOption;

/**
 * Extends Data Extraction options for CSV format
 * 
 */
public interface ICommonDataExtractionOption extends IDataExtractionOption
{

	/**
	 * constants to indicate whether to output the column name, column display
	 * name or both of them.
	 */
	public static final int OPTION_COLUMN_NAME = 1;

	public static final int OPTION_COLUMN_DISPLAY_NAME = 2;

	public static final int OPTION_BOTH = 3;
	/**
	 * output locale
	 */
	public static final String OUTPUT_LOCALE = "Locale"; //$NON-NLS-1$

	/**
	 * output locale
	 */
	public static final String OUTPUT_TIMEZONE = "Timezone"; //$NON-NLS-1$
	
	/**
	 * output encoding
	 */
	public static final String OUTPUT_ENCODING = "Encoding"; //$NON-NLS-1$

	/**
	 * output the selected columns
	 */
	public static final String OUTPUT_SELECTED_COLUMNS = "SelectedColumns"; //$NON-NLS-1$

	/**
	 * indicates whether export data type
	 */
	public static final String OUTPUT_EXPORT_DATA_TYPE = "ExportDataType"; //$NON-NLS-1$
	
	/**
	 * indicates whether to export column header
	 */
	public static final String OUTPUT_EXPORT_COLUMN_HEADER = "ExportColumnHeader"; //$NON-NLS-1$
	
	/**
	 * indicates in what way the column name is outputted. It can be column id,
	 * column display name or both of them.
	 */
	public static final String LOCALIZE_COLUMN_NAME = "LocalizeColumnName";

	/**
	 * indicates whether export the locale neutral format value
	 */
	public static final String OUTPUT_LOCALE_NEUTRAL_FORMAT = "LocaleNeutralFormat"; //$NON-NLS-1$

	/**
	 * output date format
	 */
	public static final String OUTPUT_DATE_FORMAT = "DateFormat"; //$NON-NLS-1$

	/**
	 * map which can contain user-defined parameters
	 */
	public static final String USER_PARAMETERS = "UserParameters"; //$NON-NLS-1$

	/**
	 * UTF-8 encode constant.
	 */
	public static final String UTF_8_ENCODE = "UTF-8"; //$NON-NLS-1$

	/**
	 * UTF16LE encode constant.
	 */
	public static final String UTF_16LE_ENCODE = "UTF-16LE"; //$NON-NLS-1$
	
	/**
	 * ISO-8859-1 encode constant.
	 */
	public static final String ISO_8859_1_ENCODE = "ISO-8859-1"; //$NON-NLS-1$

	/**
	 * Sets the output locale
	 * 
	 * @param locale
	 */
	void setLocale( Locale locale );

	/**
	 * Returns the output locale
	 * 
	 * @return Locale
	 */
	Locale getLocale( );

	/**
	 * Sets the output time zone.
	 * 
	 * @param timeZone time zone
	 */
	void setTimeZone( TimeZone timeZone );

	/**
	 * Returns the output time zone.
	 * 
	 * @return Timezone
	 */
	TimeZone getTimeZone( );
	
	/**
	 * Sets the output encoding
	 * 
	 * @param encoding
	 */
	void setEncoding( String encoding );

	/**
	 * Returns the output encoding
	 * 
	 * @return String
	 */
	String getEncoding( );

	/**
	 * Sets the output selected columns
	 * 
	 * @param columnNames
	 */
	void setSelectedColumns( String[] columnNames );

	/**
	 * Returns the output selected columns
	 * 
	 * @return String[]
	 */
	String[] getSelectedColumns( );

	/**
	 * Sets the flag that indicates whether export data type.
	 * 
	 * @param isExportDataType
	 */
	void setExportDataType( boolean isExportDataType );

	/**
	 * Returns the flag that indicates whether export data type.
	 * 
	 * @return boolean
	 */
	boolean isExportDataType( );

	/**
	 * Sets the flag that indicates whether export column header.
	 * 
	 * @param isExportDataType
	 */
	void setExportColumnHeader( boolean isExportColumnHeader );

	/**
	 * Returns the flag that indicates whether export column header.
	 * 
	 * @return boolean
	 */
	boolean isExportColumnHeader( );
	
	/**
	 * Sets the flag that indicates whether export the locale neutral format
	 * value.
	 * 
	 * @param isLocaleNeutralFormat
	 */
	void setLocaleNeutralFormat( boolean isLocaleNeutralFormat );

	/**
	 * Returns the flag that indicates whether export the locale neutral format
	 * value.
	 * 
	 * @return boolean
	 */
	boolean isLocaleNeutralFormat( );

	/**
	 * Sets the output date format
	 * 
	 * @param dateFormat
	 */
	void setDateFormat( String dateFormat );

	/**
	 * Returns the output date format
	 * 
	 * @return String
	 */
	String getDateFormat( );

	/**
	 * Sets the user-defined parameter map
	 * 
	 * @param map
	 */
	void setUserParameters( Map map );

	/**
	 * Returns the user-defined parameter map
	 * 
	 * @return Map
	 */
	Map getUserParameters( );

	/**
	 * Sets the localize option for the columns.
	 * 
	 * @param option
	 */
	void setColumnLocalizeOption( int option );

	/**
	 * Returns the localize option for the columns
	 * 
	 * @return
	 */
	int getColumnLocalizeOption( );
}
