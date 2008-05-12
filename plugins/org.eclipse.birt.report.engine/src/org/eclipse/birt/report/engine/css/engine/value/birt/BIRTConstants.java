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
package org.eclipse.birt.report.engine.css.engine.value.birt;

import org.eclipse.birt.report.engine.css.engine.value.css.CSSConstants;

/**
 * Define BIRT constants, such as tag names, attribute names and URI
 *
 */
public interface BIRTConstants extends CSSConstants {

    /////////////////////////////////////////////////////////////////////////
    // BIRT attributes
    /////////////////////////////////////////////////////////////////////////
	String BIRT_BACKGROUND_POSITION_X_PROPERTY = "background-position-x";
	String BIRT_BACKGROUND_POSITION_Y_PROPERTY = "background-position-y";
	String BIRT_CAN_SHRINK_PROPERTY = "can-shrink"; //$NON-NLS-1$
	String BIRT_DATE_TIME_FORMAT_PROPERTY = "date-format"; //$NON-NLS-1$
	String BIRT_TIME_FORMAT_PROPERTY = "sql-time-format"; //$NON-NLS-1$
	String BIRT_DATE_FORMAT_PROPERTY = "sql-date-format"; //$NON-NLS-1$
	String BIRT_MASTER_PAGE_PROPERTY= "master-page"; //$NON-NLS-1$
	String BIRT_NUMBER_ALIGN_PROPERTY = "number-align";
	String BIRT_NUMBER_FORMAT_PROPERTY = "number-format"; //$NON-NLS-1$
	String BIRT_SHOW_IF_BLANK_PROPERTY= "show-if-blank"; //$NON-NLS-1$
	String BIRT_STRING_FORMAT_PROPERTY = "string-format"; //$NON-NLS-1$
	String BIRT_TEXT_UNDERLINE_PROPERTY = "text-underline"; //$NON-NLS-1$
	String BIRT_TEXT_OVERLINE_PROPERTY = "text-overline"; //$NON-NLS-1$
	String BIRT_TEXT_LINETHROUGH_PROPERTY = "text-linethrough"; //$NON-NLS-1$
	String BIRT_VISIBLE_FORMAT_PROPERTY = "visible-format";
	
    /////////////////////////////////////////////////////////////////////////
    // BIRT attribute value
    /////////////////////////////////////////////////////////////////////////
	
	String BIRT_TRUE_VALUE = "true";
	String BIRT_FALSE_VALUE = "false";
	String BIRT_ALL_VALUE = "all";
	String BIRT_SOFT_VALUE = "soft";
	String BIRT_LTR_VALUE = "Left To Right";
	String BIRT_RTL_VALUE = "Right To Left";
}
