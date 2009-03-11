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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.layout.pdf.util.PropertyUtil;
import org.eclipse.birt.report.engine.nLayout.LayoutContext;
import org.eclipse.birt.report.engine.nLayout.area.IArea;
import org.eclipse.birt.report.engine.nLayout.area.IAreaVisitor;
import org.eclipse.birt.report.engine.nLayout.area.IContainerArea;
import org.eclipse.birt.report.engine.nLayout.area.style.BackgroundImageInfo;
import org.eclipse.birt.report.engine.nLayout.area.style.BorderInfo;
import org.eclipse.birt.report.engine.nLayout.area.style.BoxStyle;
import org.w3c.dom.css.CSSValue;

public abstract class ContainerArea extends AbstractArea
		implements
			IContainerArea
{

	protected transient LocalProperties localProperties = LocalProperties.DEFAULT;

	protected BoxStyle boxStyle = BoxStyle.DEFAULT;

	protected transient int maxAvaWidth = 0;

	protected transient int currentBP = 0;

	protected transient int currentIP = 0;

	protected transient int specifiedHeight = 0;

	protected transient int specifiedWidth = 0;

	protected transient boolean canShrink = false;

	protected transient CSSValue textAlign = null;

	protected transient int textIndent = 0;

	protected boolean needClip;

	protected ArrayList<IArea> children = new ArrayList<IArea>( );

	protected transient boolean isInlineStacking = false;

	protected transient boolean hasStyle = true;

	protected transient IContent content;

	protected transient LayoutContext context;
	
	protected transient boolean isInInlineStacking = false;
	
	protected transient boolean finished = true;
	
	protected CSSValue pageBreakAfter = null;
	
	protected CSSValue pageBreakBefore = null;

	protected CSSValue pageBreakInside = null;
	


	
	public ContainerArea( ContainerArea parent, LayoutContext context,
			IContent content )
	{
		this.parent = parent;
		this.context = context;
		this.content = content;
		finished = false;
	}

	ContainerArea( )
	{
		super( );
	}

	ContainerArea( ContainerArea area )
	{
		super( area );
		this.boxStyle = area.getBoxStyle( );
		this.needClip = area.needClip( );
		this.hasStyle = area.hasStyle;
		this.localProperties = area.localProperties;
		this.content = area.content;
	}

	public void autoPageBreak( ) throws BirtException
	{
		if ( parent != null )
		{
			parent.autoPageBreak( );
		}
	}

	public int getAbsoluteBP( )
	{
		if ( parent != null )
		{
			return currentBP + getOffsetY() + parent.getAbsoluteBP( );
		}
		return currentBP;
	}

	public int getMaxAvaHeight( )
	{
		if ( parent != null )
		{
			return getContentHeight( parent.getMaxAvaHeight( ) );
		}
		else
		{
			return context.getMaxBP( );
		}
	}

	public int getCurrentBP( )
	{
		return this.currentBP;
	}

	public void setCurrentBP( int currentBP )
	{
		this.currentBP = currentBP;
	}

	public void setCurrentIP( int currentIP )
	{
		this.currentIP = currentIP;
	}

	public int getCurrentIP( )
	{
		return this.currentIP;
	}
	
	public void setMaxAvaWidth( int maxAvaWidth)
	{
		this.maxAvaWidth = maxAvaWidth;
	}

	public int getMaxAvaWidth( )
	{
		return maxAvaWidth;
	}

	public IContent getContent( )
	{
		return content;
	}

	public int getSpecifiedHeight( )
	{
		return specifiedHeight;
	}

	public int getSpecifiedWidth( )
	{
		return specifiedWidth;
	}

	public boolean isCanShrink( )
	{
		return canShrink;
	}

	public CSSValue getTextAlign( )
	{
		return textAlign;
	}

	public int getTextIndent( )
	{
		return textIndent;
	}

	public boolean isNeedClip( )
	{
		return needClip;
	}

	public void setInlineStacking( boolean isInlineStacking )
	{
		this.isInlineStacking = isInlineStacking;
	}

	public boolean isInlineStacking( )
	{
		return isInlineStacking;
	}

	public Iterator<IArea> getChildren( )
	{
		return children.iterator( );
	}

	public void addChild( IArea area )
	{
		children.add( area );
		
	}
	
	public IArea getChild(int index)
	{
		if(index>=0 && index<children.size( ))
		{
			return children.get( index );
		}
		return null;
	}

	public void addChild( int index, IArea area )
	{
		children.add( index, area );
	}

	public int indexOf( IArea area )
	{
		return children.indexOf( area );
	}

	public void removeAll( )
	{
		children.clear( );
	}

	public void removeChild( IArea area )
	{
		children.remove( area );
	}

	public void accept( IAreaVisitor visitor )
	{
		visitor.visitContainer( this );
	}

	public int getChildrenCount( )
	{
		return children.size( );
	}

	public boolean needClip( )
	{
		return needClip;
	}

	public void setNeedClip( boolean needClip )
	{
		this.needClip = needClip;
	}

	public BoxStyle getBoxStyle( )
	{
		return boxStyle;
	}

	public void setBoxStyle( BoxStyle boxStyle )
	{
		this.boxStyle = boxStyle;
	}
	
	public CSSValue getPageBreakAfter( )
	{
		return pageBreakAfter;
	}

	public void setPageBreakAfter( CSSValue pageBreakAfter )
	{
		this.pageBreakAfter = pageBreakAfter;
	}

	public CSSValue getPageBreakBefore( )
	{
		return pageBreakBefore;
	}

	public void setPageBreakBefore( CSSValue pageBreakBefore )
	{
		this.pageBreakBefore = pageBreakBefore;
	}

	public CSSValue getPageBreakInside( )
	{
		return pageBreakInside;
	}

	public void setPageBreakInside( CSSValue pageBreakInside )
	{
		this.pageBreakInside = pageBreakInside;
	}


	public abstract void close( ) throws BirtException;

	public abstract void initialize( ) throws BirtException;
		
	public abstract SplitResult splitLines( int lineCount ) throws BirtException;
		
	public abstract SplitResult split( int height, boolean force ) throws BirtException;

	public void relayout( )
	{
		// FIXME to implement
	}

	protected void calculateSpecifiedWidth( IContent content )
	{
		DimensionType width = content.getWidth( );
		if ( width != null )
		{
			if ( parent != null )
			{
				specifiedWidth = getDimensionValue( content,
						content.getWidth( ), parent.getWidth( ) );
			}
			else
			{
				specifiedWidth = getDimensionValue( content, content.getWidth( ) );
			}
		}
	}

	protected void calculateSpecifiedHeight( IContent content )
	{
		DimensionType height = content.getHeight( );
		if ( height != null )
		{
			specifiedHeight = getDimensionValue( content, height );
		}
	}

	public void setAllocatedY( int ay )
	{
		if ( hasStyle )
		{
			y = ay + localProperties.getMarginTop( );
		}
		else
		{
			y = ay;
		}
	}

	/**
	 * set allocated position
	 * 
	 * @param ax
	 * @param ay
	 */
	public void setAllocatedPosition( int ax, int ay )
	{
		if ( hasStyle )
		{
			x = ax + localProperties.getMarginLeft( );
			y = ay + localProperties.getMarginTop( );
		}
		else
		{
			x = ax;
			y = ay;
		}
	}

	/**
	 * set allocated height
	 * 
	 * @param aHeight
	 */
	public void setAllocatedHeight( int aHeight )
	{
		if ( hasStyle )
		{
			height = aHeight - localProperties.getMarginBottom( )
					- localProperties.getMarginTop( );
		}
		else
		{
			height = aHeight;
		}
	}

	public int getContentHeight( int allocatedHeight )
	{
		if ( hasStyle )
		{
			return allocatedHeight - localProperties.getPaddingBottom( )
					- localProperties.getPaddingTop( )
					- localProperties.getMarginTop( )
					- localProperties.getMarginBottom( )
					- boxStyle.getBottomBorderWidth( )
					- boxStyle.getTopBorderWidth( );
		}
		else
		{
			return allocatedHeight;
		}
	}

	public int getContentY( )
	{
		if ( hasStyle )
		{
			return localProperties.getPaddingTop( )
					+ boxStyle.getTopBorderWidth( );
		}
		return 0;
	}

	public int getContentX( )
	{
		if ( hasStyle )
		{
			return localProperties.getPaddingLeft( )
					+ boxStyle.getLeftBorderWidth( );
		}
		return 0;
	}

	public int getMaxYPosition( )
	{
		if ( hasStyle )
		{
			return y + height + localProperties.getMarginBottom( );
		}
		return y + height;
	}

	public int getMinYPosition( )
	{
		return y + height;
	}

	/**
	 * set allocated width
	 * 
	 * @param aWidth
	 */
	public void setAllocatedWidth( int aWidth )
	{
		if ( hasStyle )
		{
			int totalMarginWidth = localProperties.getMarginLeft( )
					+ localProperties.getMarginRight( );
			if ( totalMarginWidth >= aWidth )
			{
				localProperties.setMarginLeft( 0 );
				localProperties.setMarginRight( 0 );
				width = aWidth;
			}
			else
			{
				width = aWidth - totalMarginWidth;
			}
		}
		else
		{
			width = aWidth;
		}
	}

	public void setContentHeight( int cHeight )
	{
		if ( hasStyle )
		{
			height = cHeight + localProperties.getPaddingBottom( )
					+ localProperties.getPaddingTop( )
					+ boxStyle.getBottomBorderWidth( )
					+ boxStyle.getTopBorderWidth( );
		}
		else
		{
			height = cHeight;
		}
	}

	public void setContentWidth( int cWidth )
	{
		if ( hasStyle )
		{
			width = cWidth + localProperties.getPaddingLeft( )
					+ localProperties.getPaddingRight( )
					+ boxStyle.getLeftBorderWidth( )
					+ boxStyle.getRightBorderWidth( );
		}
		else
		{
			width = cWidth;
		}
	}

	/**
	 * set allocated X position
	 * 
	 * @return
	 */
	public int getAllocatedX( )
	{
		if ( hasStyle )
		{
			return x - localProperties.getMarginLeft( );
		}
		else
		{
			return x;
		}
	}

	/**
	 * set allocated Y position
	 * 
	 * @return
	 */
	public int getAllocatedY( )
	{
		if ( hasStyle )
		{
			return y - localProperties.getMarginTop( );
		}
		else
		{
			return y;
		}
	}

	/**
	 * get content width
	 * 
	 * @return
	 */
	public int getContentWidth( )
	{
		if ( hasStyle )
		{
			int totalPaddngWidth = localProperties.getPaddingLeft( )
					+ localProperties.getPaddingRight( );
			int totalBorderWidth = boxStyle.getLeftBorderWidth( )
					+ boxStyle.getRightBorderWidth( );
			if ( width <= totalPaddngWidth )
			{
				localProperties.setPaddingLeft( 0 );
				localProperties.setPaddingRight( 0 );
				return width - totalBorderWidth;
			}
			else
			{
				return width - totalPaddngWidth - totalBorderWidth;
			}
		}
		else
		{
			return width;
		}
	}

	/**
	 * get content height
	 * 
	 * @return
	 */
	public int getContentHeight( )
	{
		if ( hasStyle )
		{
			return height - boxStyle.getTopBorderWidth( )
					- boxStyle.getBottomBorderWidth( )
					- localProperties.getPaddingBottom( )
					- localProperties.getPaddingTop( );
		}
		else
		{
			return height;
		}
	}

	/**
	 * get allocated width
	 * 
	 * @return
	 */
	public int getAllocatedWidth( )
	{
		if ( hasStyle )
		{
			return width + localProperties.getMarginLeft( )
					+ localProperties.getMarginRight( );
		}
		else
		{
			return width;
		}
	}

	/**
	 * get allocated height
	 * 
	 * @return
	 */
	public int getAllocatedHeight( )
	{
		if ( hasStyle )
		{
			return height + localProperties.getMarginBottom( )
					+ localProperties.getMarginTop( );
		}
		else
		{
			return height;
		}
	}

	public int getCurrentMaxContentWidth( )
	{
		return maxAvaWidth - currentIP;
	}

	public boolean isEmpty( )
	{
		return children.size( ) == 0;
	}

	public abstract void update( AbstractArea area ) throws BirtException;
	
	public abstract void add(AbstractArea area);

	public int getOffsetX( )
	{
		if ( hasStyle )
		{
			return localProperties.getPaddingLeft( )
					+ boxStyle.getLeftBorderWidth( );
		}
		return 0;
	}

	public int getOffsetY( )
	{
		if ( hasStyle )
		{
			return localProperties.getPaddingTop( )
					+ boxStyle.getTopBorderWidth( );
		}
		return 0;
	}

	protected void buildProperties( IContent content, LayoutContext context )
	{
		// FIXME cache the LocalProperties and BoxStyle
		// FIXME validate box properties
		IStyle style = content.getComputedStyle( );
		localProperties = new LocalProperties( );
		int maw = parent.getMaxAvaWidth( );

		localProperties.setMarginBottom( getDimensionValue( style
				.getProperty( IStyle.STYLE_MARGIN_BOTTOM ), maw ) );
		localProperties.setMarginLeft( getDimensionValue( style
				.getProperty( IStyle.STYLE_MARGIN_LEFT ), maw ) );
		localProperties.setMarginTop( getDimensionValue( style
				.getProperty( IStyle.STYLE_MARGIN_TOP ), maw ) );
		localProperties.setMarginRight( getDimensionValue( style
				.getProperty( IStyle.STYLE_MARGIN_RIGHT ), maw ) );

		localProperties.setPaddingBottom( getDimensionValue( style
				.getProperty( IStyle.STYLE_PADDING_BOTTOM ), maw ) );
		localProperties.setPaddingLeft( getDimensionValue( style
				.getProperty( IStyle.STYLE_PADDING_LEFT ), maw ) );
		localProperties.setPaddingTop( getDimensionValue( style
				.getProperty( IStyle.STYLE_PADDING_TOP ), maw ) );
		localProperties.setPaddingRight( getDimensionValue( style
				.getProperty( IStyle.STYLE_PADDING_RIGHT ), maw ) );

		if ( !isInInlineStacking )
		{
			pageBreakAfter = style.getProperty( IStyle.STYLE_PAGE_BREAK_AFTER );
			pageBreakInside = style
					.getProperty( IStyle.STYLE_PAGE_BREAK_INSIDE );
			pageBreakBefore = style
					.getProperty( IStyle.STYLE_PAGE_BREAK_BEFORE );
		}

		this.boxStyle = new BoxStyle( );
		int borderWidth = getDimensionValue( style
				.getProperty( IStyle.STYLE_BORDER_LEFT_WIDTH ), maw );
		if ( borderWidth > 0 )
		{
			boxStyle
					.setLeftBorder( new BorderInfo(
							style.getProperty( IStyle.STYLE_BORDER_LEFT_COLOR ),
							style.getProperty( IStyle.STYLE_BORDER_LEFT_STYLE ),
							borderWidth ) );

		}

		borderWidth = getDimensionValue( style
				.getProperty( IStyle.STYLE_BORDER_RIGHT_WIDTH ), maw );
		if ( borderWidth > 0 )
		{
			boxStyle.setRightBorder( new BorderInfo( style
					.getProperty( IStyle.STYLE_BORDER_RIGHT_COLOR ), style
					.getProperty( IStyle.STYLE_BORDER_RIGHT_STYLE ),
					borderWidth ) );

		}
		borderWidth = getDimensionValue( style
				.getProperty( IStyle.STYLE_BORDER_TOP_WIDTH ), maw );
		if ( borderWidth > 0 )
		{
			boxStyle
					.setTopBorder( new BorderInfo( style
							.getProperty( IStyle.STYLE_BORDER_TOP_COLOR ),
							style.getProperty( IStyle.STYLE_BORDER_TOP_STYLE ),
							borderWidth ) );

		}

		borderWidth = getDimensionValue( style
				.getProperty( IStyle.STYLE_BORDER_BOTTOM_WIDTH ), maw );
		if ( borderWidth > 0 )
		{
			boxStyle.setBottomBorder( new BorderInfo( style
					.getProperty( IStyle.STYLE_BORDER_BOTTOM_COLOR ), style
					.getProperty( IStyle.STYLE_BORDER_BOTTOM_STYLE ),
					borderWidth ) );

		}

		Color color = PropertyUtil.getColor( style
				.getProperty( IStyle.STYLE_BACKGROUND_COLOR ) );
		if ( color != null )
		{
			boxStyle.setBackgroundColor( color );
		}
		CSSValue url = style.getProperty( IStyle.STYLE_BACKGROUND_IMAGE );
		if ( !IStyle.NONE_VALUE.equals( style.getProperty( IStyle.STYLE_BACKGROUND_IMAGE ) ))
		{
			boxStyle.setBackgroundImage( new BackgroundImageInfo( url.getCssText( ), style
					.getProperty( IStyle.STYLE_BACKGROUND_REPEAT ),
					getDimensionValue( style
							.getProperty( IStyle.STYLE_BACKGROUND_POSITION_X ),
							maw ), getDimensionValue( style
							.getProperty( IStyle.STYLE_BACKGROUND_POSITION_Y ),
							maw ) ) );
		}
		
		textAlign = style.getProperty( IStyle.STYLE_TEXT_ALIGN );
		action = content.getHyperlinkAction( );
		bookmark = content.getBookmark( );
	}

	protected void buildLogicContainerProperties( IContent content,
			LayoutContext context )
	{
		IStyle style = content.getStyle( );
		if ( style != null && !style.isEmpty( ) )
		{
			boxStyle = new BoxStyle( );
			Color color = PropertyUtil.getColor( style
					.getProperty( IStyle.STYLE_BACKGROUND_COLOR ) );

			if ( color != null )
			{
				boxStyle.setBackgroundColor( color );
			}

			String url = style.getBackgroundImage( );
			if ( url != null )
			{
				boxStyle
						.setBackgroundImage( new BackgroundImageInfo(
								url,
								style
										.getProperty( IStyle.STYLE_BACKGROUND_REPEAT ),
								getDimensionValue(
										style
												.getProperty( IStyle.STYLE_BACKGROUND_POSITION_X ),
										width ),
								getDimensionValue(
										style
												.getProperty( IStyle.STYLE_BACKGROUND_POSITION_Y ),
										width ) ) );

			}
			if ( !isInInlineStacking )
			{
				pageBreakAfter = style
						.getProperty( IStyle.STYLE_PAGE_BREAK_AFTER );
				pageBreakInside = style
						.getProperty( IStyle.STYLE_PAGE_BREAK_INSIDE );
				pageBreakBefore = style
						.getProperty( IStyle.STYLE_PAGE_BREAK_BEFORE );
			}

		}
		else
		{
			hasStyle = false;
			boxStyle = BoxStyle.DEFAULT;
			localProperties = LocalProperties.DEFAULT;
		}
		bookmark = content.getBookmark( );
		action = content.getHyperlinkAction( );
	}

	protected TableArea getTable( )
	{
		ContainerArea p = parent;
		while ( !( p instanceof TableArea ) )
		{
			p = p.getParent( );
		}
		return (TableArea) p;
	}
	
	public ContainerArea deepClone( )
	{
		ContainerArea result = (ContainerArea) cloneArea( );
		Iterator iter = children.iterator( );
		while ( iter.hasNext( ) )
		{
			AbstractArea child = (AbstractArea) iter.next( );
			AbstractArea cloneChild = child.deepClone( );
			result.children.add( cloneChild );
			cloneChild.setParent( result );
		}
		return result;
	}
	
	public boolean isPageBreakInsideAvoid( )
	{
		if ( localProperties != null )
		{
			return ( IStyle.AVOID_VALUE == pageBreakInside );
		}
		return false;
	}
	
	public boolean isPageBreakAfterAvoid()
	{
		if ( localProperties != null )
		{
			if(IStyle.AVOID_VALUE == pageBreakAfter)
			{
				return true;
			}
		}
		IArea lastChild = getLastChild();
		if(lastChild!=null && lastChild instanceof ContainerArea)
		{
			ContainerArea lastContainer = (ContainerArea)lastChild;
			if(!lastContainer.isInInlineStacking)
			{
				return lastContainer.isPageBreakAfterAvoid( );
			}
		}
		return false;
	}
	
	public boolean isPageBreakBeforeAvoid()
	{
		if ( localProperties != null )
		{
			if(IStyle.AVOID_VALUE == pageBreakBefore)
			{
				return true;
			}
		}
		IArea firstChild = getFirstChild();
		if(firstChild!=null && firstChild instanceof ContainerArea)
		{
			ContainerArea firstContainer = (ContainerArea)firstChild;
			if(!firstContainer.isInInlineStacking)
			{
				return firstContainer.isPageBreakBeforeAvoid( );
			}
		}
		return false;
	}
	
	public IArea getLastChild()
	{
		int size = children.size( );
		if(size>0)
		{
			return children.get( size-1 );
		}
		return null;
	}
	
	public IArea getFirstChild()
	{
		int size = children.size( );
		if(size>0)
		{
			return children.get( 0 );
		}
		return null;
	}
	
	
	protected static class SplitResult
	{
		public final static int SPLIT_SUCCEED_WITH_PART = 0;
		public final static int SPLIT_SUCCEED_WITH_NULL = 1;
		public final static int SPLIT_BREFORE_AVOID_WITH_NULL = 2;

		public static SplitResult BEFORE_AVOID_WITH_NULL = new SplitResult(null, SPLIT_BREFORE_AVOID_WITH_NULL);
		public static SplitResult SUCCEED_WITH_NULL =  new SplitResult(null, SPLIT_SUCCEED_WITH_NULL);
		
		public SplitResult(ContainerArea result, int status)
		{
			this.result = result;
			this.status = status;
		}
		protected ContainerArea result = null;
		protected int status;
		
		public ContainerArea getResult()
		{
			return result;
		}
		
		public boolean isEmpty()
		{
			return result==null;
		}
		
	}

}