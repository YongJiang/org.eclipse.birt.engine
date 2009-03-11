/***********************************************************************
 * Copyright (c) 2009 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.nLayout.area.style;

import java.util.HashMap;

import org.eclipse.birt.report.engine.content.IStyle;
import org.w3c.dom.css.CSSValue;


public class BackgroundImageInfo
{
	public final static  int NO_REPEAT = 0;
	public final static int REPEAT_X = 1;
	public final static int REPEAT_Y = 2;
	public final static int REPEAT = 3;
	
	public static HashMap<CSSValue, Integer> repeatMap = new HashMap<CSSValue, Integer>();
	
	static
	{
		repeatMap.put( IStyle.NO_REPEAT_VALUE, NO_REPEAT );
		repeatMap.put( IStyle.REPEAT_X_VALUE, REPEAT_X );
		repeatMap.put( IStyle.REPEAT_Y_VALUE, REPEAT_Y );
		repeatMap.put( IStyle.REPEAT_VALUE, REPEAT );
	}
	
	
	protected int xOffset;
	protected int yOffset;
	protected int repeatedMode;
	
	protected String url;
	
	public BackgroundImageInfo(String url,  int repeatedMode, int xOffset,  int yOffset)
	{
		this.url = url;
		this.repeatedMode = repeatedMode;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
	
	public BackgroundImageInfo(String url,  CSSValue mode, int xOffset,  int yOffset)
	{
		this(url, repeatMap.get( mode), xOffset, yOffset);
	}
	
	public BackgroundImageInfo(String url)
	{
		this( url, 0, 0, 0 );
	}
	
	public int getXOffset( )
	{
		return xOffset;
	}

	
	public int getYOffset( )
	{
		return yOffset;
	}

	
	public int getRepeatedMode( )
	{
		return repeatedMode;
	}

	
	public String getUrl( )
	{
		return url;
	}
}