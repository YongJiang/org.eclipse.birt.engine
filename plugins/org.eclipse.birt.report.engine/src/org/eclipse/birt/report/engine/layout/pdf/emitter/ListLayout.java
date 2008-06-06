/***********************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.layout.pdf.emitter;

import org.eclipse.birt.report.engine.content.IBandContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IListBandContent;
import org.eclipse.birt.report.engine.content.IListContent;
import org.eclipse.birt.report.engine.layout.area.impl.AreaFactory;
import org.eclipse.birt.report.engine.layout.area.impl.ContainerArea;
import org.eclipse.birt.report.engine.layout.pdf.util.PropertyUtil;


public class ListLayout extends RepeatableLayout
{

	public ListLayout( LayoutEngineContext context, ContainerLayout parent,
			IContent content )
	{
		super( context, parent, content );
		boolean isBlock = !PropertyUtil.isInlineElement(content);
		isInBlockStacking &= isBlock;
	}
	
	protected void repeatHeader( )
	{
		if(bandStatus == IBandContent.BAND_HEADER )
		{
			return;
		}
		IListContent listContent = (IListContent)content;
		if (!listContent.isHeaderRepeat() )
		{
			return;
		}
		IListBandContent band = listContent.getHeader( );
		if ( band == null || band.getChildren().isEmpty())
		{
			return;
		}
		ContainerArea headerArea = (ContainerArea) AreaFactory
				.createLogicContainer( content.getReportContent( ) );
		headerArea.setAllocatedWidth( parent.getCurrentMaxContentWidth( ) );
		Layout regionLayout = new RegionLayout(context, band, headerArea);
		regionLayout.layout( );
		
		if ( headerArea.getAllocatedHeight( ) < getCurrentMaxContentHeight( ) )//FIXME need check
		{
			addArea( headerArea );
			repeatCount++;
		}
	}

}