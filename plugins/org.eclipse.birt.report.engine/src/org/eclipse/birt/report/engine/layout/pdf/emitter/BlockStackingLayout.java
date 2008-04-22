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

import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.css.engine.StyleConstants;
import org.eclipse.birt.report.engine.layout.area.impl.AbstractArea;
import org.eclipse.birt.report.engine.layout.area.impl.AreaFactory;
import org.eclipse.birt.report.engine.layout.area.impl.ContainerArea;
import org.eclipse.birt.report.engine.layout.pdf.util.PropertyUtil;


public class BlockStackingLayout extends ContainerLayout
{

	public BlockStackingLayout( LayoutEngineContext context, ContainerLayout parentContext,
			IContent content )
	{
		super(context, parentContext, content );
	}
	
	protected void initialize( )
	{
		createRoot( );
		calculateSpecifiedWidth( );
		// initialize offsetX and offsetY
		offsetX = root.getContentX( );
		offsetY = root.getContentY( );

		if ( specifiedWidth > 0 )
		{
			root.setAllocatedWidth( specifiedWidth );
		}
		else
		{
			root.setAllocatedWidth( parent.getCurrentMaxContentWidth( ) );
		}
		maxAvaWidth = root.getContentWidth( );

		root.setAllocatedHeight( parent.getCurrentMaxContentHeight( ) );
		maxAvaHeight = root.getContentHeight( );

	}
	
	public boolean addArea(AbstractArea area)
	{
		root.addChild( area );
		area.setAllocatedPosition( currentIP + offsetX, currentBP + offsetY );
		currentBP += area.getAllocatedHeight( );
		if ( currentIP + area.getAllocatedWidth( ) > root.getContentWidth( ))
		{
			root.setNeedClip( true );
		}
		else if( currentBP > maxAvaHeight )
		{
			root.setNeedClip( true );
		}
		return true;
	}


	protected void closeLayout( )
	{
		if ( root == null )
		{
			return;
		}
		IStyle areaStyle = root.getStyle( );
		int height = getCurrentBP( )
				+ getOffsetY( )
				+ getDimensionValue( areaStyle
						.getProperty( StyleConstants.STYLE_PADDING_BOTTOM ) )
				+ getDimensionValue( areaStyle
						.getProperty( StyleConstants.STYLE_BORDER_BOTTOM_WIDTH ) );
		root.setHeight( height );
		if(parent!=null)
		{
			parent.addArea( root );
		}
		else
		{
			content.setExtension( IContent.LAYOUT_EXTENSION, root );
		}

	}

	protected void createRoot( )
	{
		root =  (ContainerArea)AreaFactory.createBlockContainer( content );
	}
	
	public int getLineHeight( )
	{
		if ( content != null )
		{
			IStyle contentStyle = content.getComputedStyle( );
			return PropertyUtil.getLineHeight( contentStyle.getLineHeight( ));
		}
		return 0;
	}
	
	public String getTextAlign( )
	{
		if ( content != null )
		{
			IStyle contentStyle = content.getComputedStyle( );
			return contentStyle.getTextAlign( );
		}
		return null;
	}

	
}
