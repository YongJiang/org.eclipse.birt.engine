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

package org.eclipse.birt.report.engine.nLayout.area.impl;

import java.util.Iterator;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.nLayout.area.IArea;
import org.eclipse.birt.report.engine.nLayout.area.IContainerArea;

public class RegionArea extends BlockContainerArea implements IContainerArea
{
	public RegionArea( )
	{
		super( );
	}

	RegionArea( RegionArea area )
	{
		super( area );
	}
	
	public void initialize( ) throws BirtException
	{
		calculateSpecifiedWidth( content );
		calculateSpecifiedHeight( content );
	}

	public void close( ) throws BirtException
	{
		if ( specifiedHeight > currentBP )
		{
			setContentHeight( specifiedHeight );
		}
		else
		{
			setContentHeight( currentBP );
		}
		finished = true;
	}
	
	public void update( AbstractArea area ) throws BirtException
	{
		int aHeight = area.getAllocatedHeight( );
		currentBP += aHeight;
		if ( currentIP + area.getAllocatedWidth( ) > maxAvaWidth )
		{
			setNeedClip( true );
		}
	}

}
