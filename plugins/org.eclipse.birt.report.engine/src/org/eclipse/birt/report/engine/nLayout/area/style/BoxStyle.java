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

import java.awt.Color;

public class BoxStyle
{

	public final static BoxStyle DEFAULT = new BoxStyle( );

	protected Color backgroundColor = null;

	protected BackgroundImageInfo backgroundImage = null;

	protected BorderInfo topBorder = null;

	

	protected BorderInfo leftBorder = null;

	protected BorderInfo rightBorder = null;

	protected BorderInfo bottomBorder = null;

	public Color getBackgroundColor( )
	{
		return backgroundColor;
	}
	
	public BoxStyle()
	{
		
	}

	public BoxStyle( BoxStyle bs )
	{
		this.backgroundColor = bs.backgroundColor;
		if ( bs.topBorder != null )
		{
			this.topBorder = new BorderInfo( bs.topBorder );
		}
		if ( bs.leftBorder != null )
		{
			this.leftBorder = new BorderInfo( bs.leftBorder );
		}
		if ( bs.rightBorder != null )
		{
			this.rightBorder = new BorderInfo( bs.rightBorder );
		}
		if ( bs.bottomBorder != null )
		{
			this.bottomBorder = new BorderInfo( bs.bottomBorder );
		}
		if ( bs.backgroundImage != null )
		{
			this.backgroundImage = new BackgroundImageInfo( bs.backgroundImage );
		}
	}
	
	public void clearBorder()
	{
		topBorder = null;
		leftBorder = null;
		rightBorder = null;
		bottomBorder = null;
	}
	
	public BackgroundImageInfo getBackgroundImage( )
	{
		return backgroundImage;
	}

	public BorderInfo getTopBorder( )
	{
		return topBorder;
	}

	public BorderInfo getLeftBorder( )
	{
		return leftBorder;
	}

	public BorderInfo getRightBorder( )
	{
		return rightBorder;
	}

	public BorderInfo getBottomBorder( )
	{
		return bottomBorder;
	}

	public int getLeftBorderWidth( )
	{
		if ( leftBorder != null )
		{
			return leftBorder.getWidth( );
		}
		return 0;
	}

	public int getRightBorderWidth( )
	{
		if ( rightBorder != null )
		{
			return rightBorder.getWidth( );
		}
		return 0;
	}

	public int getTopBorderWidth( )
	{
		if ( topBorder != null )
		{
			return topBorder.getWidth( );
		}
		return 0;
	}

	public int getBottomBorderWidth( )
	{
		if ( bottomBorder != null )
		{
			return bottomBorder.getWidth( );
		}
		return 0;
	}

	public Color getLeftBorderColor( )
	{
		if ( leftBorder != null )
		{
			return leftBorder.getColor( );
		}
		return null;
	}

	public Color getRightBorderColor( )
	{
		if ( rightBorder != null )
		{
			return rightBorder.getColor( );
		}
		return null;
	}

	public Color getTopBorderColor( )
	{
		if ( topBorder != null )
		{
			return topBorder.getColor( );
		}
		return null;
	}

	public Color getBottomBorderColor( )
	{
		if ( bottomBorder != null )
		{
			return bottomBorder.getColor( );
		}
		return null;
	}

	public int getLeftBorderStyle( )
	{
		if ( leftBorder != null )
		{
			return leftBorder.getStyle( );
		}
		return 0;
	}

	public int getRightBorderStyle( )
	{
		if ( rightBorder != null )
		{
			return rightBorder.getStyle( );
		}
		return 0;
	}

	public int getTopBorderStyle( )
	{
		if ( topBorder != null )
		{
			return topBorder.getStyle( );
		}
		return 0;
	}

	public int getBottomBorderStyle( )
	{
		if ( bottomBorder != null )
		{
			return bottomBorder.getStyle( );
		}
		return 0;
	}
	public void setBackgroundColor( Color backgroundColor )
	{
		this.backgroundColor = backgroundColor;
	}

	
	public void setBackgroundImage( BackgroundImageInfo backgroundImage )
	{
		this.backgroundImage = backgroundImage;
	}

	
	public void setTopBorder( BorderInfo topBorder )
	{
		this.topBorder = topBorder;
	}

	
	public void setLeftBorder( BorderInfo leftBorder )
	{
		this.leftBorder = leftBorder;
	}

	
	public void setRightBorder( BorderInfo rightBorder )
	{
		this.rightBorder = rightBorder;
	}

	
	public void setBottomBorder( BorderInfo bottomBorder )
	{
		this.bottomBorder = bottomBorder;
	}

}
