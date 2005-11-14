/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.emitter.html;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.birt.report.engine.api.HTMLEmitterConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderContext;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IAction;
import org.eclipse.birt.report.engine.api.IHTMLActionHandler;
import org.eclipse.birt.report.engine.api.IHTMLImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.RenderOptionBase;
import org.eclipse.birt.report.engine.api.impl.Action;
import org.eclipse.birt.report.engine.api.impl.Image;
import org.eclipse.birt.report.engine.content.ICellContent;
import org.eclipse.birt.report.engine.content.IColumn;
import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IDataContent;
import org.eclipse.birt.report.engine.content.IForeignContent;
import org.eclipse.birt.report.engine.content.IHyperlinkAction;
import org.eclipse.birt.report.engine.content.IImageContent;
import org.eclipse.birt.report.engine.content.ILabelContent;
import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.content.IRowContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.content.ITableContent;
import org.eclipse.birt.report.engine.content.ITextContent;
import org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter;
import org.eclipse.birt.report.engine.emitter.IEmitterServices;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.EngineIRConstants;
import org.eclipse.birt.report.engine.ir.Report;
import org.eclipse.birt.report.engine.presentation.ContentEmitterVisitor;
import org.eclipse.birt.report.engine.util.FileUtil;

import sun.nio.cs.ThreadLocalCoders;
import sun.text.Normalizer;

/**
 * <code>HTMLReportEmitter</code> is a concrete class that implements
 * IReportEmitter interface to output IARD Report ojbects to HTML file. It
 * creates HTMLWriter and HTML related Emitters say, HTMLTextEmitter,
 * HTMLTableEmitter, etc. Only one copy of each Emitter class exists.
 * 
 * @version $Revision: 1.40 $ $Date: 2005/11/11 08:05:28 $
 */
public class HTMLReportEmitter extends ContentEmitterAdapter
{

	/**
	 * the output forma
	 */
	public static final String OUTPUT_FORMAT_HTML = "HTML"; //$NON-NLS-1$

	/**
	 * the default target report file name
	 */
	public static final String REPORT_FILE = "report.html"; //$NON-NLS-1$

	/**
	 * the default image folder
	 */
	public static final String IMAGE_FOLDER = "image"; //$NON-NLS-1$

	/**
	 * output stream
	 */
	protected OutputStream out = null;

	/**
	 * the report
	 */
	protected IReportContent report;

	/**
	 * the report runnable instance
	 */
	protected IReportRunnable runnable;

	/**
	 * the render options
	 */
	protected IRenderOption renderOption;

	/**
	 * Specifies if the HTML output is embeddable.
	 */
	protected boolean isEmbeddable = false;

	/**
	 * the url encoding
	 */
	protected String urlEncoding = null;

	/**
	 * specified the current page number, starting from 0
	 */
	protected int pageNo = 0;

	/**
	 * The
	 * <code>HTMLWriter<code> object that Emitters use to output HTML content.
	 */
	protected HTMLWriter writer;

	/**
	 * the image reder context
	 */
	protected Object renderContext;

	/** Indicates that the styled element is hidden or not */
	protected Stack stack = new Stack( );

	/**
	 * An Log object that <code>HTMLReportEmitter</code> use to log the error,
	 * debug, information messages.
	 */
	protected static Logger logger = Logger.getLogger( HTMLReportEmitter.class
			.getName( ) );

	/**
	 * html image handler
	 */
	protected IHTMLImageHandler imageHandler;

	/**
	 * html action handler
	 */
	protected IHTMLActionHandler actionHandler;

	/**
	 * emitter services
	 */
	protected IEmitterServices services;

	/**
	 * The <code>tagStack</code> that stores the tag names to be closed in
	 * <code>endContainer()</code>.
	 */
	private Stack tagStack = new Stack( );

	// /**
	// * a stack stores the tables' context
	// */
	// private Stack tableStack = new Stack( );
	//
	// /**
	// * specifies the current table context
	// */
	// private TableData currentData;
	//
	/**
	 * display type of Block
	 */
	protected static final int DISPLAY_BLOCK = 1;

	/**
	 * display type of Inline
	 */
	protected static final int DISPLAY_INLINE = 2;

	/**
	 * display type of Inline-Block
	 */
	protected static final int DISPLAY_INLINE_BLOCK = 4;

	/**
	 * display type of none
	 */
	protected static final int DISPLAY_NONE = 8;

	/**
	 * display flag which contains all display types
	 */
	protected static final int DISPLAY_FLAG_ALL = 0xffff;

	protected ContentEmitterVisitor contentVisitor;

	/**
	 * Create a new <code>HTMLReportEmitter</code> object to output all the
	 * pages.
	 * 
	 * @param repository
	 *            The resource manager.
	 * @param hyperlinkProcessor
	 *            The hyperlink transformation object.
	 */
	public HTMLReportEmitter( )
	{
		contentVisitor = new ContentEmitterVisitor( this );
	}

	public void initialize( IEmitterServices services )
	{
		this.services = services;

		Object fd = services.getOption( RenderOptionBase.OUTPUT_FILE_NAME );
		File file = null;
		try
		{
			if ( fd != null )
			{
				file = new File( fd.toString( ) );
				out = new BufferedOutputStream( new FileOutputStream( file ) );
			}
		}
		catch ( FileNotFoundException e )
		{
			logger.log( Level.WARNING, e.getMessage( ), e );
		}

		if ( out == null )
		{
			Object value = services.getOption( RenderOptionBase.OUTPUT_STREAM );
			if ( value != null && value instanceof OutputStream )
			{
				out = (OutputStream) value;
			}
			else
			{
				try
				{
					// FIXME
					file = new File( REPORT_FILE );
					out = new BufferedOutputStream( new FileOutputStream( file ) );
				}
				catch ( FileNotFoundException e )
				{
					// FIXME
					logger.log( Level.SEVERE, e.getMessage( ), e );
				}
			}
		}

		Object emitterConfig = services.getEmitterConfig( ).get( "html" ); //$NON-NLS-1$
		if ( emitterConfig != null
				&& emitterConfig instanceof HTMLEmitterConfig )
		{
			imageHandler = ( (HTMLEmitterConfig) emitterConfig )
					.getImageHandler( );
			actionHandler = ( (HTMLEmitterConfig) emitterConfig )
					.getActionHandler( );
		}

		Object im = services.getOption( HTMLRenderOption.IMAGE_HANDLER );
		if ( im != null && im instanceof IHTMLImageHandler )
		{
			imageHandler = (IHTMLImageHandler) im;
		}

		Object ac = services.getOption( HTMLRenderOption.ACTION_HANDLER );
		if ( ac != null && ac instanceof IHTMLActionHandler )
		{
			actionHandler = (IHTMLActionHandler) ac;
		}

		if ( services.getRenderContext( ) instanceof Map )
		{
			renderContext = ( (Map) services.getRenderContext( ) )
					.get( HTMLRenderContext.CONTEXT_NAME );
		}
		else
		{
			renderContext = services.getRenderContext( ); // Handle the
			// old-style render
			// context, follow
			// the same code
			// path as before.
		}

		renderOption = services.getRenderOption( );
		runnable = services.getReportRunnable( );
		if ( renderOption != null && renderOption instanceof HTMLRenderOption )
		{
			isEmbeddable = ( (HTMLRenderOption) renderOption ).getEmbeddable( );
			HashMap options = renderOption.getOutputSetting( );
			assert options != null;
			urlEncoding = (String) options.get( HTMLRenderOption.URL_ENCODING );
		}

		writer = new HTMLWriter( );
	}

	/**
	 * @return the <code>Report</code> object.
	 */
	public IReportContent getReport( )
	{
		return report;
	}

	/**
	 * Pushes the Boolean indicating whether or not the item is hidden according
	 * to the StyledELementItem
	 * 
	 * @param item
	 *            the StyledElementContent
	 */
	public void push( IStyle style )
	{
		stack.push( new Boolean( peek( style ) ) );
	}

	/**
	 * Pops the peek element of the stack and returns
	 * 
	 * @return Returns the boolean indicating whether or not the item is hidden
	 */
	public boolean pop( )
	{
		return ( (Boolean) stack.pop( ) ).booleanValue( );
	}

	public boolean peek( IStyle style )
	{
		boolean isHidden = false;
		if ( !stack.empty( ) )
		{
			isHidden = ( (Boolean) stack.peek( ) ).booleanValue( );
		}
		if ( !isHidden )
		{
			String formats = style.getVisibleFormat( );
			if ( formats != null
					&& formats.indexOf( EngineIRConstants.FORMAT_TYPE_VIEWER ) >= 0 )
			{
				isHidden = true;
			}
		}
		return isHidden;
	}

	/**
	 * Checks if the current item is hidden
	 * 
	 * @return Returns the boolean
	 */
	public boolean isHidden( )
	{
		return ( (Boolean) stack.peek( ) ).booleanValue( );
	}

	public IEmitterServices getEmitterServices( )
	{
		return services;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#getOutputFormat()
	 */
	public String getOutputFormat( )
	{
		return OUTPUT_FORMAT_HTML;
	}

	protected void addExpandableErrorMsg( )
	{
		writer.writeCode( "<script>" ); //$NON-NLS-1$
		writer.writeCode( "function expand(index)" ); //$NON-NLS-1$
		writer
				.writeCode( "{var icon = document.getElementById(\"error_icon\" + index);" ); //$NON-NLS-1$
		writer
				.writeCode( "var detail = document.getElementById(\"error_detail\" + index);" ); //$NON-NLS-1$
		writer.writeCode( "if (icon != null && detail != null)" ); //$NON-NLS-1$
		writer.writeCode( "{var display = detail.style.display;" ); //$NON-NLS-1$
		writer.writeCode( "if (display == \"none\")" ); //$NON-NLS-1$
		writer
				.writeCode( "{icon.innerHTML = \" - \";detail.style.display = \"block\";}" ); //$NON-NLS-1$
		writer
				.writeCode( "else{icon.innerHTML = \" + \";detail.style.display = \"none\";}}}" ); //$NON-NLS-1$
		writer
				.writeCode( "for(var i=0; i<document.getElementsByName(\"error_title\").length; i++)" ); //$NON-NLS-1$
		writer
				.writeCode( "{document.getElementById(\"error_detail\" + i).style.display = \"none\";}" ); //$NON-NLS-1$
		writer.writeCode( "</script>" ); //$NON-NLS-1$
	}

	/**
	 * Fixes a PNG problem related to transparency. See
	 * http://homepage.ntlworld.com/bobosola/ for detail.
	 */
	protected void fixTransparentPNG( )
	{
		writer.writeCode( "<!--[if gte IE 5.5000]>" ); //$NON-NLS-1$
		writer
				.writeCode( "   <script language=\"JavaScript\"> var ie55up = true </script>" ); //$NON-NLS-1$
		writer.writeCode( "<![endif]-->" ); //$NON-NLS-1$
		writer.writeCode( "<script language=\"JavaScript\">" ); //$NON-NLS-1$
		writer
				.writeCode( "   function fixPNG(myImage) // correctly handle PNG transparency in Win IE 5.5 or higher." ); //$NON-NLS-1$
		writer.writeCode( "      {" ); //$NON-NLS-1$
		writer.writeCode( "      if (window.ie55up)" ); //$NON-NLS-1$
		writer.writeCode( "         {" ); //$NON-NLS-1$
		writer
				.writeCode( "         var imgID = (myImage.id) ? \"id='\" + myImage.id + \"' \" : \"\"" ); //$NON-NLS-1$
		writer
				.writeCode( "         var imgClass = (myImage.className) ? \"class='\" + myImage.className + \"' \" : \"\"" ); //$NON-NLS-1$
		writer
				.writeCode( "         var imgTitle = (myImage.title) ? \"title='\" + myImage.title + \"' \" : \"title='\" + myImage.alt + \"' \"" ); //$NON-NLS-1$
		writer
				.writeCode( "         var imgStyle = \"display:inline-block;\" + myImage.style.cssText" ); //$NON-NLS-1$
		writer
				.writeCode( "         var strNewHTML = \"<span \" + imgID + imgClass + imgTitle" ); //$NON-NLS-1$
		writer
				.writeCode( "         strNewHTML += \" style=\\\"\" + \"width:\" + myImage.width + \"px; height:\" + myImage.height + \"px;\" + imgStyle + \";\"" ); //$NON-NLS-1$
		writer
				.writeCode( "         strNewHTML += \"filter:progid:DXImageTransform.Microsoft.AlphaImageLoader\"" ); //$NON-NLS-1$
		writer
				.writeCode( "         strNewHTML += \"(src=\\'\" + myImage.src + \"\\', sizingMethod='scale');\\\"></span>\"" ); //$NON-NLS-1$
		writer.writeCode( "         myImage.outerHTML = strNewHTML" ); //$NON-NLS-1$
		writer.writeCode( "         }" ); //$NON-NLS-1$
		writer.writeCode( "      }" ); //$NON-NLS-1$
		writer.writeCode( "</script>" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#start(org.eclipse.birt.report.engine.content.IReportContent)
	 */
	public void start( IReportContent report )
	{
		logger.log( Level.FINE, "[HTMLReportEmitter] Start emitter." ); //$NON-NLS-1$

		this.report = report;
		writer.open( out, "UTF-8" ); //$NON-NLS-1$

		if ( isEmbeddable )
		{
			fixTransparentPNG( );
			return;
		}

		writer.startWriter( );
		writer.openTag( HTMLTags.TAG_HTML );
		writer.openTag( HTMLTags.TAG_HEAD );
		writer.openTag( HTMLTags.TAG_META );
		writer.attribute( HTMLTags.ATTR_HTTP_EQUIV, "Content-Type" ); //$NON-NLS-1$ 
		writer.attribute( HTMLTags.ATTR_CONTENT, "text/html; charset=UTF-8" ); //$NON-NLS-1$ 
		writer.closeNoEndTag( );

		writer.openTag( HTMLTags.TAG_STYLE );
		writer.attribute( HTMLTags.ATTR_TYPE, "text/css" ); //$NON-NLS-1$

		// output general styles
		writer.style(
				"*", "text-decoration: none; vertical-align: baseline;", true ); //$NON-NLS-1$ //$NON-NLS-2$ 
		writer.style(
				"table", "border-collapse: collapse; empty-cells: show;", true ); //$NON-NLS-1$ //$NON-NLS-2$ 
		writer.style( ":link, :visited", "text-decoration: underline;", true ); //$NON-NLS-1$ //$NON-NLS-2$ 

		IStyle style;
		StringBuffer styleBuffer = new StringBuffer( );
		if ( report == null )
		{
			logger.log( Level.WARNING,
					"[HTMLReportEmitter] Report object is null." ); //$NON-NLS-1$
		}
		else
		{
			Report reportDesign = report.getDesign( );

			Iterator itor = reportDesign.getStyleSet( ).iterator( );
			if ( itor != null )
			{
				while ( itor.hasNext( ) )
				{
					styleBuffer.delete( 0, styleBuffer.capacity( ) );
					Entry entry = (Entry) itor.next( );
					style = (IStyle) entry.getValue( );
					AttributeBuilder.buildStyle( styleBuffer, style, this );
					writer.style( entry.getKey( ).toString( ), styleBuffer
							.toString( ), false );
				}
			}
		}

		writer.closeTag( HTMLTags.TAG_STYLE );
		fixTransparentPNG( );
		writer.closeTag( HTMLTags.TAG_HEAD );

		String reportStyleName = report == null ? null : report.getDesign( )
				.getRootStyleName( );
		if ( !isEmbeddable )
		{
			writer.openTag( HTMLTags.TAG_BODY );
			if ( reportStyleName != null )
			{
				writer.attribute( HTMLTags.ATTR_CLASS, reportStyleName );
			}
		}
		else
		{
			writer.openTag( HTMLTags.TAG_DIV );
			if ( reportStyleName != null )
			{
				AttributeBuilder.buildStyle( styleBuffer, report
						.findStyle( reportStyleName ), this );
				writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#end(org.eclipse.birt.report.engine.content.IReportContent)
	 */
	public void end( IReportContent report )
	{
		logger.log( Level.FINE, "[HTMLReportEmitter] End body." ); //$NON-NLS-1$
		if ( !isEmbeddable )
		{
			writer.closeTag( HTMLTags.TAG_BODY );
			writer.closeTag( HTMLTags.TAG_HTML );
		}
		else
		{
			writer.closeTag( HTMLTags.TAG_DIV );
		}

		writer.endWriter( );
		writer.close( );
		if ( out != null )
		{
			try
			{
				out.close( );
			}
			catch ( IOException e )
			{
				logger.log( Level.WARNING, e.getMessage( ), e );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startPage(org.eclipse.birt.report.engine.content.IPageContent)
	 */
	public void startPage( IPageContent page )
	{
		writer.openTag( HTMLTags.TAG_DIV );
		IStyle contentStyle = page == null ? null : page.getContentStyle( );
		if ( contentStyle != null )
		{
			StringBuffer styleBuffer = new StringBuffer( );
			AttributeBuilder.buildStyle( styleBuffer, contentStyle, this );
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
		}

		if ( page != null )
		{
			contentVisitor.visitList( page.getHeader( ), null );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endPage(org.eclipse.birt.report.engine.content.IPageContent)
	 */
	public void endPage( IPageContent page )
	{
		logger.log( Level.FINE, "[HTMLReportEmitter] End page." ); //$NON-NLS-1$

		if ( page != null )
		{
			contentVisitor.visitList( page.getFooter( ), null );
		}
		writer.closeTag( HTMLTags.TAG_DIV );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTable(org.eclipse.birt.report.engine.content.ITableContent)
	 */
	public void startTable( ITableContent table )
	{
		assert table != null;

		IStyle mergedStyle = table.getStyle( );
		push( mergedStyle );
		if ( isHidden( ) )
		{
			return;
		}
		logger.log( Level.FINE, "[HTMLTableEmitter] Start table" ); //$NON-NLS-1$

		DimensionType x = table.getX( );
		DimensionType y = table.getY( );
		StringBuffer styleBuffer = new StringBuffer( );

		addDefaultTableStyles( styleBuffer );

		writer.openTag( HTMLTags.TAG_TABLE );

		// style string
		setStyleName( table.getStyleClass( ) );
		int display = checkElementType( x, y, mergedStyle, styleBuffer );
		setDisplayProperty( display, DISPLAY_INLINE, styleBuffer );

		handleShrink( DISPLAY_BLOCK, mergedStyle, table.getHeight( ), table
				.getWidth( ), styleBuffer );
		handleStyle( table, styleBuffer );

		// bookmark
		setBookmark( null, table.getBookmark( ) );

		// table caption
		String caption = table.getCaption( );
		if ( caption != null && caption.length( ) > 0 )
		{
			writer.openTag( HTMLTags.TAG_CAPTION );
			writer.text( caption );
			writer.closeTag( HTMLTags.TAG_CAPTION );
		}

		writeColumns( table );
		// currentData = new TableData( table.getColumnCount( ) );
		// tableStack.push( currentData );
	}

	protected void writeColumns( ITableContent table )
	{
		for ( int i = 0; i < table.getColumnCount( ); i++ )
		{
			IColumn column = table.getColumn( i );

			writer.openTag( HTMLTags.TAG_COL );

			setStyleName( column.getStyleClass( ) );

			// width
			StringBuffer styleBuffer = new StringBuffer( );
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_WIDTH,
					column.getWidth( ) );
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

			writer.closeNoEndTag( );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTable(org.eclipse.birt.report.engine.content.ITableContent)
	 */
	public void endTable( ITableContent table )
	{
		if ( pop( ) )
		{
			return;
		}

		writer.closeTag( HTMLTags.TAG_TABLE );
		// tableStack.pop( );
		// if ( tableStack.size( ) > 0 )
		// {
		// currentData = (TableData) tableStack.peek( );
		// }
		// else
		// {
		// currentData = null;
		// }

		logger.log( Level.FINE, "[HTMLTableEmitter] End table" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableHeader(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableHeader( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.openTag( HTMLTags.TAG_THEAD );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableHeader(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableHeader( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.closeTag( HTMLTags.TAG_THEAD );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableBody(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableBody( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.openTag( HTMLTags.TAG_TBODY );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableBody(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableBody( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.closeTag( HTMLTags.TAG_TBODY );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableFooter(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableFooter( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.openTag( HTMLTags.TAG_TFOOT );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableFooter(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableFooter( ITableBandContent band )
	{
		if ( isHidden( ) )
		{
			return;
		}
		writer.closeTag( HTMLTags.TAG_TFOOT );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startRow(org.eclipse.birt.report.engine.content.IRowContent)
	 */
	public void startRow( IRowContent row )
	{
		assert row != null;
		IStyle mergedStyle = row.getStyle( );
		push( mergedStyle );
		if ( isHidden( ) )
		{
			return;
		}
		writer.openTag( HTMLTags.TAG_TR );

		setStyleName( row.getStyleClass( ) );

		// bookmark
		setBookmark( null, row.getBookmark( ) );

		StringBuffer styleBuffer = new StringBuffer( );

		AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_HEIGHT, row
				.getHeight( ) ); //$NON-NLS-1$
		handleStyle( row, styleBuffer );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endRow(org.eclipse.birt.report.engine.content.IRowContent)
	 */
	public void endRow( IRowContent row )
	{
		if ( pop( ) )
		{
			return;
		}

		// assert currentData != null;
		//
		// currentData.adjustCols( );
		writer.closeTag( HTMLTags.TAG_TR );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startCell(org.eclipse.birt.report.engine.content.ICellContent)
	 */
	public void startCell( ICellContent cell )
	{
		if ( isHidden( ) )
		{
			return;
		}

		// int span;
		// int columnID;

		logger.log( Level.FINE, "[HTMLTableEmitter] Start cell." ); //$NON-NLS-1$

		if ( cell != null )
		{
			// assert currentData != null;
			//
			// // fill empty cell if needed
			// currentData.fillCells( cell.getColumn( ), cell.getRowSpan( ),
			// cell
			// .getColSpan( ) );

			// output 'td' tag
			writer.openTag( HTMLTags.TAG_TD ); //$NON-NLS-1$

			// set the 'name' property
			setStyleName( cell.getStyleClass( ) );

			// colspan
			if ( ( cell.getColSpan( ) ) > 1 )
			{
				writer.attribute( HTMLTags.ATTR_COLSPAN, cell.getColSpan( ) );
			}

			// rowspan
			if ( ( cell.getRowSpan( ) ) > 1 )
			{
				writer.attribute( HTMLTags.ATTR_ROWSPAN, cell.getRowSpan( ) );
			}

			IStyle mergedStyle = cell.getStyle( );
			StringBuffer styleBuffer = new StringBuffer( );
			if ( isEmbeddable
					&& ( mergedStyle == null || mergedStyle.getVerticalAlign( ) == null ) )
			{
				styleBuffer.append( "vertical-align: baseline;" ); //$NON-NLS-1$
			}

			handleStyle( cell, styleBuffer );
		}
		else
		{
			writer.openTag( HTMLTags.TAG_TD );
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endCell(org.eclipse.birt.report.engine.content.ICellContent)
	 */
	public void endCell( ICellContent cell )
	{
		if ( isHidden( ) )
		{
			return;
		}
		logger.log( Level.FINE, "[HTMLReportEmitter] End cell." ); //$NON-NLS-1$

		writer.closeTag( HTMLTags.TAG_TD );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startContainer(org.eclipse.birt.report.engine.content.IContainerContent)
	 */
	public void startContainer( IContainerContent container )
	{
		IStyle mergedStyle = container.getStyle( );
		push( mergedStyle );
		if ( isHidden( ) )
		{
			return;
		}
		logger.log( Level.FINE, "[HTMLReportEmitter] Start container" ); //$NON-NLS-1$

		String tagName;
		StringBuffer styleBuffer = new StringBuffer( );
		DimensionType x = container.getX( );
		DimensionType y = container.getY( );
		DimensionType width = container.getWidth( );
		DimensionType height = container.getHeight( );

		int display = checkElementType( x, y, width, height, mergedStyle,
				styleBuffer );
		tagName = openTagByType( display, DISPLAY_FLAG_ALL );
		tagStack.push( tagName );

		// class
		setStyleName( container.getStyleClass( ) );

		// bookmark
		setBookmark( tagName, container.getBookmark( ) );

		// output style
		if ( x == null && y == null )
		{
			styleBuffer.append( "position: relative;" ); //$NON-NLS-1$
		}

		setDisplayProperty( display, DISPLAY_INLINE_BLOCK, styleBuffer );

		handleShrink( display, mergedStyle, height, width, styleBuffer );

		handleStyle( container, styleBuffer );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endContainer(org.eclipse.birt.report.engine.content.IContainerContent)
	 */
	public void endContainer( IContainerContent container )
	{
		if ( pop( ) )
		{
			return;
		}

		writer.closeTag( (String) tagStack.pop( ) );

		logger.log( Level.FINE, "[HTMLContainerEmitter] End container" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startText(org.eclipse.birt.report.engine.content.ITextContent)
	 */
	public void startText( ITextContent text )
	{
		IStyle mergedStyle = text.getStyle( );
		if ( peek( mergedStyle ) )
		{
			return;
		}

		logger.log( Level.FINE, "[HTMLReportEmitter] Start text" ); //$NON-NLS-1$

		StringBuffer styleBuffer = new StringBuffer( );
		DimensionType x = text.getX( );
		DimensionType y = text.getY( );
		DimensionType width = text.getWidth( );
		DimensionType height = text.getHeight( );
		String textValue = text.getText( );

		int display;
		// If the item is multi-line, we should check if it can be inline-block
		if ( textValue != null && textValue.indexOf( '\n' ) >= 0 )
		{
			display = checkElementType( x, y, width, height, mergedStyle,
					styleBuffer );
		}
		else
		{
			display = checkElementType( x, y, mergedStyle, styleBuffer );
		}

		// action
		String tagName;
		if ( handleAction( text.getHyperlinkAction( ) ) )
		{
			tagName = HTMLTags.TAG_A;
			setDisplayProperty( display, DISPLAY_BLOCK | DISPLAY_INLINE_BLOCK,
					styleBuffer );
			AttributeBuilder.checkHyperlinkTextDecoration( mergedStyle,
					styleBuffer );
		}
		else
		{
			tagName = openTagByType( display, DISPLAY_FLAG_ALL );
			setDisplayProperty( display, DISPLAY_INLINE_BLOCK, styleBuffer );
		}

		setStyleName( text.getStyleClass( ) );

		// bookmark
		setBookmark( tagName, text.getBookmark( ) );

		// title
		writer.attribute( HTMLTags.ATTR_TITLE, text.getHelpText( ) ); //$NON-NLS-1$

		// check 'can-shrink' property
		handleShrink( display, mergedStyle, height, width, styleBuffer );
		handleStyle( text, styleBuffer );

		writer.text( textValue );

		writer.closeTag( tagName );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startForeign(org.eclipse.birt.report.engine.content.IForeignContent)
	 */
	public void startForeign( IForeignContent foreign )
	{
		IStyle mergedStyle = foreign.getStyle( );

		if ( peek( mergedStyle ) )
		{
			return;
		}

		logger.log( Level.FINE, "[HTMLReportEmitter] Start foreign" ); //$NON-NLS-1$

		StringBuffer styleBuffer = new StringBuffer( );
		DimensionType x = foreign.getX( );
		DimensionType y = foreign.getY( );
		DimensionType width = foreign.getWidth( );
		DimensionType height = foreign.getHeight( );

		int display;
		display = checkElementType( x, y, width, height, mergedStyle,
				styleBuffer );

		// action
		String tagName;
		if ( handleAction( foreign.getHyperlinkAction( ) ) )
		{
			tagName = HTMLTags.TAG_A;
			setDisplayProperty( display, DISPLAY_BLOCK | DISPLAY_INLINE_BLOCK,
					styleBuffer );
			AttributeBuilder.checkHyperlinkTextDecoration( mergedStyle,
					styleBuffer );
		}
		else
		{
			tagName = openTagByType( display, DISPLAY_FLAG_ALL );
			setDisplayProperty( display, DISPLAY_INLINE_BLOCK, styleBuffer );
		}

		setStyleName( foreign.getStyleClass( ) );

		// bookmark
		setBookmark( tagName, foreign.getBookmark( ) );

		// title
		writer.attribute( HTMLTags.ATTR_TITLE, foreign.getHelpText( ) );

		// check 'can-shrink' property
		handleShrink( display, mergedStyle, height, width, styleBuffer );
		handleStyle( foreign, styleBuffer );

		Object rawValue = foreign.getRawValue( );
		String rawType = foreign.getRawType( );
		boolean notHtml = !IForeignContent.HTML_TYPE.equalsIgnoreCase( rawType );
		String text = rawValue == null ? null : rawValue.toString( );

		if ( !notHtml )
		{
			text = text.trim( );
			if ( text.length( ) > 6 )
			{
				String htmlTag = text.substring( 0, 6 );
				if ( "<html>".equalsIgnoreCase( htmlTag ) )
				{
					text = text.substring( 6 );
				}
			}
			if ( text.length( ) > 7 )
			{
				String endTag = text.substring( text.length( ) - 7 );
				if ( "</html>".equalsIgnoreCase( endTag ) )
				{
					text = text.substring( 0, text.length( ) - 7 );
				}
			}
		}

		writer.text( text, notHtml, notHtml );

		writer.closeTag( tagName );

	}

	public void startLabel( ILabelContent label )
	{
		startText( label );
	}

	public void startData( IDataContent data )
	{
		startText( data );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startImage(org.eclipse.birt.report.engine.content.IImageContent)
	 */
	public void startImage( IImageContent image )
	{
		assert image != null;
		IStyle mergedStyle = image.getStyle( );

		if ( peek( mergedStyle ) )
		{
			return;
		}

		logger.log( Level.FINE, "[HTMLImageEmitter] Start image" ); //$NON-NLS-1$ 

		StringBuffer styleBuffer = new StringBuffer( );
		int display = checkElementType( image.getX( ), image.getY( ),
				mergedStyle, styleBuffer );
		String tag = openTagByType( display, DISPLAY_BLOCK );

		// action
		boolean hasAction = handleAction( image.getHyperlinkAction( ) );

		String imgUri = getImageURI( image );
		boolean useSVG = ( image.getMIMEType( ) != null )
				&& image.getMIMEType( ).equalsIgnoreCase( "image/svg+xml" ); //$NON-NLS-1$
		if ( useSVG )
		{ // use svg
			writer.openTag( HTMLTags.TAG_EMBED );
			writer.attribute( HTMLTags.ATTR_TYPE, "image/svg+xml" ); //$NON-NLS-1$
			writer.attribute( HTMLTags.ATTR_SRC, imgUri );
			writer.attribute( HTMLTags.ATTR_WIDTH, image.getWidth( ) );
			writer.attribute( HTMLTags.ATTR_HEIGHT, image.getHeight( ) );
			writer.closeNoEndTag( );
		}
		else
		{ // use img

			// write image map if necessary
			Object imageMapObject = image.getImageMap( );
			// use imgUri as the image ID. As we know only the CHART can have
			// image maps and each chart
			// will have differnt URI, so it is safe for CHART. (If the named
			// image also support image
			// map, then we must use another way to get the image ID.
			String imageMapId = imgUri;
			boolean hasImageMap = ( imageMapObject != null )
					&& ( imageMapObject instanceof String )
					&& ( ( (String) imageMapObject ).length( ) > 0 );
			if ( hasImageMap )
			{
				writer.openTag( HTMLTags.TAG_MAP );
				writer.attribute( HTMLTags.ATTR_NAME, imageMapId );
				writer.text( (String) imageMapObject, true, false );
				writer.closeTag( HTMLTags.TAG_MAP );
			}

			writer.openTag( HTMLTags.TAG_IMAGE ); //$NON-NLS-1$
			setStyleName( image.getStyleClass( ) );
			setDisplayProperty( display, 0, styleBuffer );
			setBookmark( HTMLTags.ATTR_IMAGE, image.getBookmark( ) ); //$NON-NLS-1$

			String ext = image.getExtension( );
			// FIXME special process, such as encoding etc
			writer.attribute( HTMLTags.ATTR_SRC, imgUri );

			if ( hasImageMap )
				writer.attribute( HTMLTags.ATTR_USEMAP, "#" + imageMapId ); //$NON-NLS-1$ //$NON-NLS-2$

			// alternative text
			writer.attribute( HTMLTags.ATTR_ALT, image.getAltText( ) );

			// help text
			writer.attribute( HTMLTags.ATTR_TITLE, image.getHelpText( ) );

			// image size
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_WIDTH, image
					.getWidth( ) ); //$NON-NLS-1$
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_HEIGHT,
					image.getHeight( ) ); //$NON-NLS-1$
			// handle style
			handleStyle( image, styleBuffer );

			if ( ".PNG".equalsIgnoreCase( ext ) && imageHandler != null ) //$NON-NLS-1$
			{
				writer.attribute( HTMLTags.ATTR_ONLOAD, "fixPNG(this)" ); //$NON-NLS-1$
			}

			writer.closeNoEndTag( );
		}

		if ( hasAction )
		{
			writer.closeTag( HTMLTags.TAG_A );
		}

		writer.closeTag( tag );
	}

	protected String getImageURI( IImageContent image )
	{
		String imgUri = null;
		if ( imageHandler != null )
		{

			Image img = new Image( image );
			img.setRenderOption( renderOption );
			img.setReportRunnable( runnable );
			switch ( img.getSource( ) )
			{
				case IImage.DESIGN_IMAGE :
					imgUri = imageHandler.onDesignImage( img, renderContext );
					break;
				case IImage.URL_IMAGE :
					imgUri = imageHandler.onURLImage( img, renderContext );
					break;
				case IImage.REPORTDOC_IMAGE :
					imgUri = imageHandler.onDocImage( img, renderContext );
					break;
				case IImage.CUSTOM_IMAGE :
					imgUri = imageHandler.onCustomImage( img, renderContext );
					break;
				case IImage.FILE_IMAGE :
					imgUri = imageHandler.onFileImage( img, renderContext );
					break;
				case IImage.INVALID_IMAGE :
					break;
			}
		}

		return imgUri;
	}

	/**
	 * Sets the <code>'class'</code> property and stores the style to styleMap
	 * object.
	 * 
	 * @param style
	 *            The <code>StyleDesign</code> object.
	 */
	protected void setStyleName( String styleName )
	{
		if ( isEmbeddable )
		{
			return;
		}

		if ( styleName != null )
		{
			writer.attribute( HTMLTags.ATTR_CLASS, styleName );
		}
	}

	/**
	 * Checks the 'CanShrink' property and sets the width and height according
	 * to the table below:
	 * <p>
	 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing
	 * symbol, location, localized, and meaning.">
	 * <tr bgcolor="#ccccff">
	 * <th align=left>CanShrink</th>
	 * <th align=left>Element Type</th>
	 * <th align=left>Width</th>
	 * <th align=left>Height</th>
	 * </tr>
	 * <tr valign=middle>
	 * <td rowspan="2"><code>true(by default)</code></td>
	 * <td>in-line</td>
	 * <td>ignor</td>
	 * <td>set</td>
	 * </tr>
	 * <tr valign=top bgcolor="#eeeeff">
	 * <td><code>block</code></td>
	 * <td>set</td>
	 * <td>ignor</td>
	 * </tr>
	 * <tr valign=middle>
	 * <td rowspan="2" bgcolor="#eeeeff"><code>false</code></td>
	 * <td>in-line</td>
	 * <td>replaced by 'min-width' property</td>
	 * <td>set</td>
	 * </tr>
	 * <tr valign=top bgcolor="#eeeeff">
	 * <td><code>block</code></td>
	 * <td>set</td>
	 * <td>replaced by 'min-height' property</td>
	 * </tr>
	 * </table>
	 * 
	 * @param type
	 *            The display type of the element.
	 * @param style
	 *            The style of an element.
	 * @param height
	 *            The height property.
	 * @param width
	 *            The width property.
	 * @param styleBuffer
	 *            The <code>StringBuffer</code> object that returns 'style'
	 *            content.
	 * @return A <code>boolean</code> value indicating 'Can-Shrink' property
	 *         is set to <code>true</code> or not.
	 */
	protected boolean handleShrink( int type, IStyle style,
			DimensionType height, DimensionType width, StringBuffer styleBuffer )
	{
		boolean canShrink = style == null
				|| !"false".equalsIgnoreCase( style.getCanShrink( ) ); //$NON-NLS-1$

		if ( ( type & DISPLAY_BLOCK ) > 0 )
		{
			AttributeBuilder
					.buildSize( styleBuffer, HTMLTags.ATTR_WIDTH, width );
			if ( !canShrink )
			{
				AttributeBuilder.buildSize( styleBuffer,
						HTMLTags.ATTR_MIN_HEIGHT, height );
			}
		}
		else if ( ( type & DISPLAY_INLINE ) > 0 )
		{
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_HEIGHT,
					height );
			if ( !canShrink )
			{
				AttributeBuilder.buildSize( styleBuffer,
						HTMLTags.ATTR_MIN_WIDTH, width );
			}

		}
		else
		{
			assert false;
		}
		return canShrink;
	}

	/**
	 * Outputs the 'bookmark' property. Destination anchors in HTML documents
	 * may be specified either by the A element (naming it with the 'name'
	 * attribute), or by any other elements (naming with the 'id' attribute).
	 * 
	 * @param tagName
	 *            The tag's name.
	 * @param bookmark
	 *            The bookmark value.
	 */
	protected void setBookmark( String tagName, String bookmark )
	{
		if ( tagName == null || !HTMLTags.TAG_A.equalsIgnoreCase( tagName ) )
		{
			writer.attribute( HTMLTags.ATTR_ID, bookmark );
		}
		else
		{
			writer.attribute( HTMLTags.ATTR_NAME, bookmark );
		}
	}

	/**
	 * Checks whether the element is block, inline or inline-block level. In
	 * BIRT, the absolute positioning model is used and a box is explicitly
	 * offset with respect to its containing block. When an element's x or y is
	 * set, it will be treated as a block level element regardless of the
	 * 'Display' property set in style. When designating width or height value
	 * to an inline element, it will be treated as inline-block.
	 * 
	 * @param x
	 *            Specifies how far a box's left margin edge is offset to the
	 *            right of the left edge of the box's containing block.
	 * @param y
	 *            Specifies how far an absolutely positioned box's top margin
	 *            edge is offset below the top edge of the box's containing
	 *            block.
	 * @param width
	 *            The width of the element.
	 * @param height
	 *            The height of the element.
	 * @param style
	 *            The <code>IStyle</code> object.
	 * @param styleBuffer
	 *            The <code>StringBuffer</code> object that returns 'style'
	 *            content.
	 * @return The display type of the element.
	 */
	protected int checkElementType( DimensionType x, DimensionType y,
			IStyle style, StringBuffer styleBuffer )
	{
		return checkElementType( x, y, null, null, style, styleBuffer );
	}

	protected int checkElementType( DimensionType x, DimensionType y,
			DimensionType width, DimensionType height, IStyle style,
			StringBuffer styleBuffer )
	{
		int type = 0;
		String display = null;
		if ( style != null )
		{
			display = style.getDisplay( );
		}

		if ( EngineIRConstants.DISPLAY_NONE.equalsIgnoreCase( display ) )
		{
			type |= DISPLAY_NONE;
		}

		if ( x != null || y != null )
		{
			styleBuffer.append( "position: absolute;" ); //$NON-NLS-1$
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_LEFT, x );
			AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_LEFT, y );
			return type | DISPLAY_BLOCK;
		}
		else if ( EngineIRConstants.DISPLAY_INLINE.equalsIgnoreCase( display ) )
		{
			type |= DISPLAY_INLINE;
			if ( width != null || height != null )
			{
				type |= DISPLAY_INLINE_BLOCK;
			}
			return type;
		}

		return type | DISPLAY_BLOCK;
	}

	/**
	 * Open a tag according to the display type of the element. Here is the
	 * mapping table:
	 * <p>
	 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing
	 * symbol, location, localized, and meaning.">
	 * <tr bgcolor="#ccccff">
	 * <th align=left>Display Type</th>
	 * <th align=left>Tag name</th>
	 * </tr>
	 * <tr valign=middle>
	 * <td>DISPLAY_BLOCK</td>
	 * <td>DIV</td>
	 * </tr>
	 * <tr valign=top bgcolor="#eeeeff">
	 * <td>DISPLAY_INLINE</td>
	 * <td>SPAN</td>
	 * </tr>
	 * </table>
	 * 
	 * @param display
	 *            The display type.
	 * @param mask
	 *            The mask value.
	 * @return Tag name.
	 */
	protected String openTagByType( int display, int mask )
	{
		int flag = display & mask;
		if ( ( flag & DISPLAY_BLOCK ) > 0 )
		{
			writer.openTag( HTMLTags.TAG_DIV );
			return HTMLTags.TAG_DIV;
		}

		if ( ( flag & DISPLAY_INLINE ) > 0 )
		{
			writer.openTag( HTMLTags.TAG_SPAN );
			return HTMLTags.TAG_SPAN;
		}

		return null;
	}

	/**
	 * Set the display property to style.
	 * 
	 * @param display
	 *            The display type.
	 * @param mask
	 *            The mask.
	 * @param styleBuffer
	 *            The <code>StringBuffer</code> object that returns 'style'
	 *            content.
	 */
	protected void setDisplayProperty( int display, int mask,
			StringBuffer styleBuffer )
	{
		int flag = display & mask;
		if ( ( display & DISPLAY_NONE ) > 0 )
		{
			styleBuffer.append( "display: none;" ); //$NON-NLS-1$
		}
		else if ( flag > 0 )
		{
			if ( ( flag & DISPLAY_BLOCK ) > 0 )
			{
				styleBuffer.append( "display: block;" ); //$NON-NLS-1$
			}
			else if ( ( flag & DISPLAY_INLINE_BLOCK ) > 0 )
			{
				styleBuffer.append( "display: inline-block;" ); //$NON-NLS-1$
			}
			else if ( ( flag & DISPLAY_INLINE ) > 0 )
			{
				styleBuffer.append( "display: inline;" ); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Checks the Action object and then output corresponding tag and property.
	 * 
	 * @param action
	 *            The <code>IAction</code> object.
	 * @return A <code>boolean</code> value indicating whether the Action
	 *         object is valid or not.
	 */
	protected boolean handleAction( IHyperlinkAction action )
	{
		if ( action == null )
		{
			return false;
		}
		Action act = new Action( action );
		String link = null;
		if ( actionHandler != null )
		{
			link = actionHandler.getURL( act, renderContext );
		}
		else
		{
			return false;
		}

		boolean ret = ( link != null && !link.equals( "" ) ); //$NON-NLS-1$

		if ( ret )
		{

			String href = ( action.getType( ) == IAction.ACTION_BOOKMARK )
					? ( "#" + link ) //$NON-NLS-1$
					: link;

			if ( urlEncoding != null )
			{
				href = URLEncoder.encode( href, urlEncoding );
			}

			writer.openTag( HTMLTags.TAG_A );

			writer.attribute( HTMLTags.ATTR_HREF, href );

			writer.attribute( HTMLTags.ATTR_TARGET, action.getTargetWindow( ) );
		}

		return ret;
	}

	/**
	 * handle style image
	 * 
	 * @param uri
	 *            uri in style image
	 * @return
	 */
	public String handleStyleImage( String uri )
	{
		String id = null;
		if ( FileUtil.isLocalResource( uri ) && FileUtil.isRelativePath( uri ) )
		{
			File path = null;
			String base = (String) runnable
					.getProperty( IReportRunnable.BASE_PROP );
			if ( base != null && !"".equals( base ) ) //$NON-NLS-1$
			{
				path = new File( base, uri );
			}
			else
			{
				String reportName = runnable.getReportName( );
				if ( reportName != null )
				{
					String parent = new File( new File( reportName )
							.getAbsolutePath( ) ).getParent( );
					path = new File( parent, uri );
				}
				else
				{
					// TO FIXME
				}

			}
			if ( path == null || !path.exists( ) )
			{
				logger
						.log(
								Level.SEVERE,
								"file {0} not found", path == null ? null : path.getAbsoluteFile( ) ); //$NON-NLS-1$ //$NON-NLS-2$
				return null;
			}

			id = path.getAbsolutePath( );
		}
		else
		{
			id = uri;
		}
		Image image = new Image( id );
		image.setReportRunnable( runnable );
		image.setRenderOption( renderOption );
		String imgUri = null;
		if ( imageHandler != null )
		{
			switch ( image.getSource( ) )
			{

				case IImage.URL_IMAGE :
					imgUri = imageHandler.onURLImage( image, renderContext );
					break;

				case IImage.FILE_IMAGE :
					imgUri = imageHandler.onFileImage( image, renderContext );
					break;

				case IImage.INVALID_IMAGE :
					break;

				default :
					assert ( false );
			}
			// imgUri = imgUri.replace( File.separatorChar, '/' );
		}
		return imgUri;
	}

	/*
	 * Determin if the browser is compatible with MSIE Those MSIE compatible
	 * browser contain "MSIE" in the user agent field of http header. Those
	 * browsers that doesn't compatible contains no such string. like firefox
	 * 1.0.3
	 */
	protected boolean isBrowserMSIECompatible( String agent )
	{
		if ( agent == null || agent.equals( "" ) ) //$NON-NLS-1$
			return true; // MSIE is deemed as default if not specified

		String userAgent = " " + agent + " "; // padding blank //$NON-NLS-1$
		// //$NON-NLS-2$
		Pattern pattern = Pattern.compile( "\\sMSIE[\\s+|\\/|\\d]", //$NON-NLS-1$
				Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
		Matcher m = pattern.matcher( userAgent );

		return m.find( );
	}

	// following code are copy from JDK src: java.net.URI.encode

	private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static void appendEscape( StringBuffer sb, byte b )
	{
		sb.append( '%' );
		sb.append( hexDigits[( b >> 4 ) & 0x0f] );
		sb.append( hexDigits[( b >> 0 ) & 0x0f] );
	}

	protected static String encodeURL( String s )
	{

		int n = s.length( );
		if ( n == 0 )
			return s;

		// First check whether we actually need to encode
		for ( int i = 0;; )
		{
			if ( s.charAt( i ) >= '\u0080' )
				break;
			if ( ++i >= n )
				return s;
		}

		String ns = Normalizer.normalize( s, Normalizer.COMPOSE, 0 );
		ByteBuffer bb = null;
		try
		{
			bb = ThreadLocalCoders.encoderFor( "UTF-8" ).encode( //$NON-NLS-1$
					CharBuffer.wrap( ns ) );
		}
		catch ( CharacterCodingException x )
		{
			assert false;
		}

		StringBuffer sb = new StringBuffer( );
		while ( bb.hasRemaining( ) )
		{
			int b = bb.get( ) & 0xff;
			if ( b >= 0x80 )
				appendEscape( sb, (byte) b );
			else
				sb.append( (char) b );
		}
		return sb.toString( );
	}

	/**
	 * Handles the style of the styled element content
	 * 
	 * @param element
	 *            the styled element content
	 * @param styleBuffer
	 *            the StringBuffer instance
	 */
	protected void handleStyle( IContent element, StringBuffer styleBuffer )
	{
		if ( isEmbeddable )
		{
			AttributeBuilder
					.buildStyle( styleBuffer, element.getStyle( ), this );
		}
		else
		{
			AttributeBuilder.buildStyle( styleBuffer,
					element.getInlineStyle( ), this );
		}

		// output in-line style
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
	}

	protected void addDefaultTableStyles( StringBuffer styleBuffer )
	{
		if ( isEmbeddable )
		{
			styleBuffer
					.append( "border-collapse: collapse; empty-cells: show;" ); //$NON-NLS-1$
		}
	}

	// /**
	// * <code>TableData</code> is a concrete class that stores necessary data
	// * so that <code>HTMLTableEmitter</code> can fill the missing cells, get
	// * the colAlign attribute for a cell, etc.
	// */
	// private class TableData
	// {
	//
	// /**
	// * Specifies the total column number.
	// */
	// private int columns;
	//
	// /**
	// * An integer array to store the row span of each column.
	// */
	// private int rowSpans[];
	//
	// /**
	// * The Column ID of last cell.
	// */
	// private int lastCol;
	//
	// /**
	// * The constructor.
	// */
	// public TableData( int columns )
	// {
	// this.columns = columns;
	// rowSpans = new int[columns];
	// for ( int n = 0; n < columns; n++ )
	// {
	// rowSpans[n] = 0;
	// }
	//
	// lastCol = 1;
	// }
	//
	// protected void ensureSize( int columnSize )
	// {
	// if ( rowSpans.length < columnSize )
	// {
	// int[] newRowSpans = new int[columnSize];
	// if ( rowSpans != null )
	// {
	// System.arraycopy( rowSpans, 0, newRowSpans, 0,
	// rowSpans.length );
	// rowSpans = newRowSpans;
	// }
	// }
	// }
	//
	// /**
	// * Adjusts the row spans of each column when a row ends. If lastCol is
	// * not bigger than column number, we may also need to fill the empty
	// * cells before ending the row.
	// */
	// public void adjustCols( )
	// {
	// for ( ; lastCol <= columns; lastCol++ )
	// {
	// if ( rowSpans[lastCol - 1] == 0 )
	// {
	// startCell( null );
	// endCell( null );
	// }
	// }
	//
	// lastCol = 1;
	// for ( int n = 0; n < columns; n++ )
	// {
	// if ( rowSpans[n] > 0 )
	// {
	// rowSpans[n]--;
	// }
	// }
	// }
	//
	// /**
	// * Fills the empty cells if needed. When a new cell's columnID is larger
	// * than lastCol and there are some empty cells (with rowspan=0) between
	// * them, we need to insert them before adding the new cell.
	// *
	// * @param columnID
	// * The column ID of the new cell.
	// * @param rowSpan
	// * The row span of the new cell.
	// * @param colSpan
	// * The column span of the new cell.
	// */
	// public void fillCells( int columnID, int rowSpan, int colSpan )
	// {
	// if ( columnID > 0 )
	// {
	// ensureSize( columnID + colSpan );
	// for ( ; lastCol < columnID; lastCol++ )
	// {
	// if ( rowSpans[lastCol - 1] == 0 )
	// {
	// startCell( null );
	// endCell( null );
	// }
	// }
	// }
	// else
	// {
	// while ( lastCol <= rowSpans.length && rowSpans[lastCol - 1] > 0 )
	// {
	// lastCol++;
	// }
	// }
	//
	// ensureSize( lastCol + colSpan );
	// for ( int n = 0; n < colSpan; n++, lastCol++ )
	// {
	// rowSpans[lastCol - 1] = rowSpan;
	// }
	// }
	// }
}