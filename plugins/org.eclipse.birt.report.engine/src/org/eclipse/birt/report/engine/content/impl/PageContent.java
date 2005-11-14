
package org.eclipse.birt.report.engine.content.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.engine.content.IContentVisitor;
import org.eclipse.birt.report.engine.content.IImageContent;
import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;

public class PageContent extends AbstractContent implements IPageContent
{

	protected String orientation;
	protected String pageType;
	protected DimensionType pageHeight;
	protected DimensionType pageWidth;
	protected DimensionType headerHeight;
	protected DimensionType footerHeight;
	protected DimensionType leftWidth;
	protected DimensionType rightWidth;
	protected DimensionType marginTop;
	protected DimensionType marginLeft;
	protected DimensionType marginRight;
	protected DimensionType marginBottom;
	protected ArrayList header;
	protected ArrayList footer;
	protected IImageContent waterMark;
	protected long pageNumber;

	public PageContent( ReportContent report )
	{
		super( report );
	}

	public void setGenerateBy( Object design )
	{
		super.setGenerateBy( design );
		if ( design instanceof MasterPageDesign )
		{
			MasterPageDesign page = (MasterPageDesign) design;
			orientation = page.getOrientation( );
			pageType = page.getPageType( );
			pageHeight = page.getPageHeight( );
			pageWidth = page.getPageWidth( );
			marginTop = page.getTopMargin();
			marginLeft = page.getLeftMargin();
			marginRight = page.getRightMargin();
			marginBottom = page.getBottomMargin();
		}
	}

	public void accept( IContentVisitor visitor, Object value )
	{
		visitor.visitPage( this, value );
	}

	MasterPageDesign getMasterPage( )
	{
		return (MasterPageDesign) this.generateBy;
	}

	public String getOrientation( )
	{
		return orientation;
	}

	public String getPageType( )
	{
		return pageType;
	}

	public DimensionType getPageHeight( )
	{
		return pageHeight;
	}

	public DimensionType getPageWidth( )
	{
		return pageWidth;
	}

	public IImageContent getWaterMark( )
	{
		return waterMark;
	}

	public List getHeader( )
	{
		if ( header == null )
		{
			header = new ArrayList( );
		}
		return header;
	}

	public List getFooter( )
	{
		if ( footer == null )
		{
			footer = new ArrayList( );
		}
		return footer;
	}

	/**
	 * @param orientation
	 *            The orientation to set.
	 */
	public void setOrientation( String orientation )
	{
		this.orientation = orientation;
	}

	/**
	 * @param pageHeight
	 *            The pageHeight to set.
	 */
	public void setPageHeight( DimensionType pageHeight )
	{
		this.pageHeight = pageHeight;
	}

	/**
	 * @param pageType
	 *            The pageType to set.
	 */
	public void setPageType( String pageType )
	{
		this.pageType = pageType;
	}

	/**
	 * @param pageWidth
	 *            The pageWidth to set.
	 */
	public void setPageWidth( DimensionType pageWidth )
	{
		this.pageWidth = pageWidth;
	}

	/**
	 * @param waterMark
	 *            The waterMark to set.
	 */
	public void setWaterMark( IImageContent waterMark )
	{
		this.waterMark = waterMark;
	}

	public DimensionType getMarginTop( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DimensionType getMarginBottom( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DimensionType getMarginLeft( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DimensionType getMarginRight( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public DimensionType getHeaderHeight( )
	{
		return headerHeight;
	}

	public DimensionType getFooterHeight( )
	{
		return footerHeight;
	}

	public DimensionType getLeftWidth( )
	{
		return leftWidth;
	}

	public DimensionType getRightWidth( )
	{
		return rightWidth;
	}

	public IStyle getContentStyle( )
	{
		return ( (MasterPageDesign) generateBy ).getContentStyle( );
	}

	public void setPageNumber( long pn )
	{
		this.pageNumber = pn;
	}

	public long getPageNumber( )
	{
		return this.pageNumber;
	}
}
