/*******************************************************************************
 * Copyright (c) 2004, 2007 Actuate Corporation.
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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.HTMLEmitterConfig;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IHTMLActionHandler;
import org.eclipse.birt.report.engine.api.IHTMLImageHandler;
import org.eclipse.birt.report.engine.api.IImage;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.InstanceID;
import org.eclipse.birt.report.engine.api.RenderOptionBase;
import org.eclipse.birt.report.engine.api.impl.Action;
import org.eclipse.birt.report.engine.api.impl.Image;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.content.ICellContent;
import org.eclipse.birt.report.engine.content.IColumn;
import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IDataContent;
import org.eclipse.birt.report.engine.content.IElement;
import org.eclipse.birt.report.engine.content.IForeignContent;
import org.eclipse.birt.report.engine.content.IGroupContent;
import org.eclipse.birt.report.engine.content.IHyperlinkAction;
import org.eclipse.birt.report.engine.content.IImageContent;
import org.eclipse.birt.report.engine.content.ILabelContent;
import org.eclipse.birt.report.engine.content.IListBandContent;
import org.eclipse.birt.report.engine.content.IListGroupContent;
import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.content.IRowContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.content.ITableContent;
import org.eclipse.birt.report.engine.content.ITableGroupContent;
import org.eclipse.birt.report.engine.content.ITextContent;
import org.eclipse.birt.report.engine.css.engine.value.birt.BIRTConstants;
import org.eclipse.birt.report.engine.css.engine.value.css.CSSConstants;
import org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter;
import org.eclipse.birt.report.engine.emitter.IEmitterServices;
import org.eclipse.birt.report.engine.emitter.html.util.HTMLEmitterUtil;
import org.eclipse.birt.report.engine.executor.ExecutionContext.ElementExceptionInfo;
import org.eclipse.birt.report.engine.executor.css.HTMLProcessor;
import org.eclipse.birt.report.engine.i18n.EngineResourceHandle;
import org.eclipse.birt.report.engine.i18n.MessageConstants;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.EngineIRConstants;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;
import org.eclipse.birt.report.engine.ir.Report;
import org.eclipse.birt.report.engine.ir.SimpleMasterPageDesign;
import org.eclipse.birt.report.engine.ir.TemplateDesign;
import org.eclipse.birt.report.engine.parser.TextParser;
import org.eclipse.birt.report.engine.presentation.ContentEmitterVisitor;
import org.eclipse.birt.report.model.api.IResourceLocator;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.core.IModuleModel;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSValue;

import com.ibm.icu.util.ULocale;

/**
 * <code>HTMLReportEmitter</code> is a subclass of
 * <code>ContentEmitterAdapter</code> that implements IContentEmitter
 * interface to output IARD Report ojbects to HTML file.
 * 
 * <br>
 * Metadata information:<br>
 * <table border="solid;1px">
 * <tr>
 * <td rowspan="2">Item</td>
 * <td colspan="2">Output position</td>
 * </tr>
 * <tr>
 * <td>EnableMetadata=true</td>
 * <td>EnableMetadata=false</td>
 * </tr>
 * <tr>
 * <td>Container</td>
 * <td>On self</td>
 * <td>On self</td>
 * <tr>
 * <td>Table</td>
 * <td>On self</td>
 * <td>On self</td>
 * </tr>
 * <tr>
 * <td>Image</td>
 * <td>On container( for select handle )</td>
 * <td>Only bookmark is output on self</td>
 * </tr>
 * <tr>
 * <td>Chart</td>
 * <td>On container( for select handle )</td>
 * <td>Only bookmark is output on self</td>
 * </tr>
 * <tr>
 * <td>Foreign</td>
 * <td>On container( for select handle )</td>
 * <td>Only bookmark is output on self</td>
 * </tr>
 * <tr>
 * <td>Label</td>
 * <td>On container( for select handle )</td>
 * <td>Only bookmark is output on self</td>
 * </tr>
 * <tr>
 * <td>Template Items( including template table, template label, etc.)</td>
 * <td>On container( for select handle )</td>
 * <td>Only bookmark is output on self</td>
 * </tr>
 * </table>
 * 
 */
public class HTMLReportEmitter extends ContentEmitterAdapter
{

	/**
	 * the output format
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
	 * the report content
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
	 * should output the page header & footer
	 */
	protected boolean outputMasterPageContent = true;

	/**
	 * specifies if the HTML output is embeddable
	 */
	protected boolean isEmbeddable = false;

	/**
	 * the url encoding
	 */
	protected String urlEncoding = null;
	
	/**
	 * should we output the report as Right To Left
	 */
	protected boolean htmlRtLFlag = false;
	
	protected boolean pageFooterFloatFlag = true;

	protected boolean enableMetadata = false;

	protected List ouputInstanceIDs = null;

	/**
	 * specified the current page number, starting from 0
	 */
	protected int pageNo = 0;

	/**
	 * the <code>HTMLWriter<code> object that is used to output HTML content
	 */
	protected HTMLWriter writer;

	/**
	 * the context used to execute the report
	 */
	protected IReportContext reportContext;

	/**
	 * indicates that the styled element is hidden or not
	 */
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

	/**
	 * content visitor that is used to handle page header/footer
	 */
	protected ContentEmitterVisitor contentVisitor;

	private MetadataEmitter metadataEmitter;
	
	private IDGenerator idGenerator = new IDGenerator( );
	
	static HashMap borderStyleMap = null;
	static
	{
		borderStyleMap = new HashMap( );
		borderStyleMap.put( CSSConstants.CSS_NONE_VALUE, new Integer( 0 ) );
		borderStyleMap.put( CSSConstants.CSS_INSET_VALUE, new Integer( 1 ) );
		borderStyleMap.put( CSSConstants.CSS_GROOVE_VALUE, new Integer( 2 ) );
		borderStyleMap.put( CSSConstants.CSS_OUTSET_VALUE, new Integer( 3 ) );
		borderStyleMap.put( CSSConstants.CSS_RIDGE_VALUE, new Integer( 4 ) );
		borderStyleMap.put( CSSConstants.CSS_DOTTED_VALUE, new Integer( 5 ) );
		borderStyleMap.put( CSSConstants.CSS_DASHED_VALUE, new Integer( 6 ) );
		borderStyleMap.put( CSSConstants.CSS_SOLID_VALUE, new Integer( 7 ) );
		borderStyleMap.put( CSSConstants.CSS_DOUBLE_VALUE, new Integer( 8 ) );
	}
	
	/**
	 * record the leaf cell
	 */
	private ICellContent leafCell;
	/**
	 * record the leaf cell is filled or not.
	 */
	private boolean cellFilled = false;
	
	private String layoutPreference;
	private boolean enableAgentStyleEngine;
	private HTMLEmitter htmlEmitter;
	
	/**
	 * the constructor
	 */
	public HTMLReportEmitter( )
	{
		contentVisitor = new ContentEmitterVisitor( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#initialize(org.eclipse.birt.report.engine.emitter.IEmitterServices)
	 */
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
				File parent = file.getParentFile( );
				if ( parent != null && !parent.exists( ) )
				{
					parent.mkdirs( );
				}
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

		reportContext = services.getReportContext( );

		renderOption = services.getRenderOption( );
		runnable = services.getReportRunnable( );
		writer = new HTMLWriter( );
		if ( renderOption != null )
		{
			HTMLRenderOption htmlOption = new HTMLRenderOption( renderOption );
			isEmbeddable = htmlOption.getEmbeddable( );
			HashMap options = renderOption.getOutputSetting( );
			if ( options != null )
			{
				urlEncoding = (String) options
						.get( HTMLRenderOption.URL_ENCODING );
			}
			outputMasterPageContent = htmlOption.getMasterPageContent( );
			IHTMLActionHandler actHandler = htmlOption.getActionHandle( );
			if ( ac != null )
			{
				actionHandler = actHandler;
			}
			pageFooterFloatFlag = htmlOption.getPageFooterFloatFlag( );
			htmlRtLFlag = htmlOption.getHtmlRtLFlag( );
			enableMetadata = htmlOption.getEnableMetadata( );
			ouputInstanceIDs = htmlOption.getInstanceIDs( );
			metadataEmitter = new MetadataEmitter( writer, htmlOption, idGenerator );
			layoutPreference = htmlOption.getLayoutPreference( );
			enableAgentStyleEngine = htmlOption.getEnableAgentStyleEngine( );
		}
		if ( enableAgentStyleEngine )
		{
			htmlEmitter = new HTMLPerformanceOptimize( this,
					writer,
					isEmbeddable );
		}
		else
		{
			// we will use HTMLVisionOptimize as the default emitter.
			htmlEmitter = new HTMLVisionOptimize( this, writer, isEmbeddable );
		}
	}

	/**
	 * @return the <code>Report</code> object.
	 */
	public IReportContent getReport( )
	{
		return report;
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
		
		// If it is the body style and htmlRtLFlag has been set true, 
		// remove the text-align included in the style.
		if ( htmlRtLFlag )
		{
			String reportStyleName = report == null ? null : report.getDesign( )
					.getRootStyleName( );
			if ( reportStyleName != null )
			{
				IStyle style =  report.findStyle( reportStyleName );
				//style.removeProperty( "text-align" );
				style.setProperty(IStyle.STYLE_TEXT_ALIGN, IStyle.RIGHT_VALUE);
			}
		}
		
		if ( null == layoutPreference )
		{
			// get the layout preference from the report design.
			if ( report != null )
			{
				Report reportDesign = report.getDesign( );
				ReportDesignHandle designHandle = reportDesign.getReportDesign( );
				String reportLayoutPreference = designHandle.getLayoutPreference( );
				if ( DesignChoiceConstants.REPORT_LAYOUT_PREFERENCE_FIXED_LAYOUT.equals( reportLayoutPreference ) )
				{
					layoutPreference = HTMLRenderOption.LAYOUT_PREFERENCE_FIXED;
				}
				else if ( DesignChoiceConstants.REPORT_LAYOUT_PREFERENCE_AUTO_LAYOUT.equals( reportLayoutPreference ) )
				{
					layoutPreference = HTMLRenderOption.LAYOUT_PREFERENCE_AUTO;
				}
			}
		}
		
		if ( isEmbeddable )
		{
			fixTransparentPNG( );

			writer.openTag( HTMLTags.TAG_DIV );

			if ( report != null )
			{
				String reportStyleName = report.getDesign( ).getRootStyleName( );
				if ( reportStyleName != null )
				{
					IStyle style = report.findStyle( reportStyleName );
					StringBuffer styleBuffer = new StringBuffer( );
					AttributeBuilder.buildStyle( styleBuffer, style, this );
					writer.attribute( HTMLTags.ATTR_STYLE,
							styleBuffer.toString( ) );
				}
			}

			return;
		}

		writer.startWriter( );
		writer.openTag( HTMLTags.TAG_HTML );
		writer.openTag( HTMLTags.TAG_HEAD );
		
		// write the title of the report in html.
		Report reportDesign = null;	
		if ( report != null )
		{
			reportDesign = report.getDesign( );	
			ReportDesignHandle designHandle = reportDesign.getReportDesign( );
			String title = designHandle.getStringProperty(  IModuleModel.TITLE_PROP );
			if ( title == null )
			{
				// set the default title
				if ( renderOption != null )
				{
					HTMLRenderOption htmlOption = new HTMLRenderOption(
							renderOption );
					title = htmlOption.getHtmlTitle( );
				}
			}
			if ( title != null )
			{
				writer.openTag( HTMLTags.TAG_TITLE );
				writer.text( title );
				writer.closeTag( HTMLTags.TAG_TITLE );
			}
		}
				
		writer.openTag( HTMLTags.TAG_META );
		writer.attribute( HTMLTags.ATTR_HTTP_EQUIV, "Content-Type" ); //$NON-NLS-1$ 
		writer.attribute( HTMLTags.ATTR_CONTENT, "text/html; charset=UTF-8" ); //$NON-NLS-1$ 
		writer.closeNoEndTag( );

		writer.openTag( HTMLTags.TAG_STYLE );
		writer.attribute( HTMLTags.ATTR_TYPE, "text/css" ); //$NON-NLS-1$

		IStyle style;
		StringBuffer styleBuffer = new StringBuffer( );
		if ( report == null )
		{
			logger.log( Level.WARNING,
					"[HTMLReportEmitter] Report object is null." ); //$NON-NLS-1$
		}
		else
		{
			Map styles = reportDesign.getStyles( );
			Iterator iter = styles.entrySet( ).iterator( );
			while ( iter.hasNext( ) )
			{
				Map.Entry entry = (Map.Entry) iter.next( );
				String styleName = (String) entry.getKey( );
				style = (IStyle) entry.getValue( );
				
				styleBuffer.setLength( 0 );					
				AttributeBuilder.buildStyle( styleBuffer, style, this );
				// Build the vertical-align
				String value = style.getVerticalAlign( );
				if ( null != value )
				{
					styleBuffer.append( " vertical-align:" );
					styleBuffer.append( value );
					styleBuffer.append( ";" );
				}
				// Build the textAlign
				value = style.getTextAlign( );
				if ( null != value )
				{
					styleBuffer.append( " text-align:" );
					styleBuffer.append( value );
					styleBuffer.append( ";" );
				}
				writer.style( '.' + styleName, styleBuffer.toString( ) );
			}
		}

		writer.closeTag( HTMLTags.TAG_STYLE );
		fixTransparentPNG( );
		writer.closeTag( HTMLTags.TAG_HEAD );

		writer.openTag( HTMLTags.TAG_BODY );
		if ( report != null )
		{
			String reportStyleName = report.getDesign( ).getRootStyleName( );
			if ( reportStyleName != null )
			{
				writer.attribute( HTMLTags.ATTR_CLASS, reportStyleName );
				// remove the default margin of body
				writer.attribute( HTMLTags.ATTR_STYLE, " margin:0px;" );
			}
		}
	}

	private void appendErrorMessage(EngineResourceHandle rc, int index, ElementExceptionInfo info )
	{
		writer.writeCode( "			<div>" );
		writer.writeCode( "				<div  id=\"error_title\" style=\"text-decoration:underline\">" );
		String name = info.getName( );
		if ( name != null )
		{
			writer.text( rc.getMessage( MessageConstants.REPORT_ERROR_MESSAGE,
				new Object[]{info.getType( ), name } ), false );
		}
		else
		{
			writer.text( rc.getMessage( MessageConstants.REPORT_ERROR_MESSAGE_WITH_ID,
					new Object[]{info.getType( ), info.getID( ) } ), false );
		}
			writer.writeCode( "</div>" );//$NON-NLS-1$
			
		ArrayList errorList = info.getErrorList( );
		ArrayList countList = info.getCountList( );
		for ( int i = 0; i < errorList.size( ); i++ )
		{
			String errorId = "document.getElementById('error_detail" + index + "_" + i + "')";
			String errorIcon = "document.getElementById('error_icon" + index + "_" + i + "')";
			String onClick = "if (" + errorId + ".style.display == 'none') { "
					+ errorIcon + ".innerHTML = '- '; " + errorId
					+ ".style.display = 'block'; }" + "else { " + errorIcon
					+ ".innerHTML = '+ '; " + errorId + ".style.display = 'none'; }";
			writer.writeCode("<div>");
			BirtException ex = (BirtException) errorList.get( i );
			writer.writeCode( "<span id=\"error_icon" + index + "_" + i
					+ "\"  style=\"cursor:pointer\" onclick=\"" + onClick
					+ "\" > + </span>" );
			
			writer.text( ex.getLocalizedMessage( ) ); 
			
			
		
			writer.writeCode( "				<pre id=\"error_detail" + index + "_" + i //$NON-NLS-1$
				+ "\" style=\"display:none;\" >" );//$NON-NLS-1$
		
			

			String messageTitle = rc.getMessage(
					MessageConstants.REPORT_ERROR_ID, new Object[]{
							ex.getErrorCode( ) ,
							countList.get( i )} );
			String detailTag = rc
					.getMessage( MessageConstants.REPORT_ERROR_DETAIL );
			String messageBody = getDetailMessage( ex );
			boolean indent = writer.isIndent( );
			writer.setIndent( false );
			writer.text( messageTitle, false );
			writer.writeCode( "\r\n" );//$NON-NLS-1$
			writer.text( detailTag, false );
			writer.text( messageBody, false );
			writer.setIndent( indent );
			writer.writeCode( "				</pre>" ); //$NON-NLS-1$
			writer.writeCode("</div>");
		}
		
		writer.writeCode( "</div>" ); //$NON-NLS-1$
		writer.writeCode( "<br>" ); //$NON-NLS-1$
	}

	private String getDetailMessage( Throwable t )
	{
		StringWriter out = new StringWriter( );
		PrintWriter print = new PrintWriter( out );
		try
		{
			t.printStackTrace( print );
		}
		catch ( Throwable ex )
		{
		}
		print.flush( );
		return out.getBuffer( ).toString( );
	}

	protected boolean outputErrors( List errors )
	{
		// Outputs the error message at the end of the report
		if ( errors != null && !errors.isEmpty( ) )
		{
			writer.writeCode( "	<hr style=\"color:red\"/>" );
			writer.writeCode( "	<div style=\"color:red\">" );
			writer.writeCode( "		<div>" );
			Locale locale = reportContext.getLocale( );
			if(locale==null)
			{
				locale = Locale.getDefault();
			}
			EngineResourceHandle rc = new EngineResourceHandle( ULocale.forLocale(locale) );
			writer.text( rc.getMessage(
					MessageConstants.ERRORS_ON_REPORT_PAGE ), false );
			
			writer.writeCode( "</div>" );//$NON-NLS-1$
			writer.writeCode( "<br>") ;//$NON-NLS-1$
			Iterator it = errors.iterator( );
			int index = 0;
			while ( it.hasNext( ) )
			{
				appendErrorMessage(rc, index++, (ElementExceptionInfo) it.next( ) );
			}
			writer.writeCode( "</div>" );
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#end(org.eclipse.birt.report.engine.content.IReportContent)
	 */
	public void end( IReportContent report )
	{
		logger.log( Level.FINE, "[HTMLReportEmitter] End body." ); //$NON-NLS-1$
		if ( report != null )
		{
			List errors = report.getErrors( );
			if ( errors != null && !errors.isEmpty( ) )
			{
				outputErrors( errors );
			}
		}
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
		pageNo++;
		
		if ( pageNo > 1 && outputMasterPageContent == false )
		{
			writer.openTag( "hr" );
			writer.closeTag( "hr" );
		}

		// out put the page tag
		writer.openTag( HTMLTags.TAG_DIV );
		
		// out put the background and width
		if ( page != null )
		{
			IStyle classStyle = page.getStyle( );
			StringBuffer styleBuffer = new StringBuffer( );
			// build the background
			AttributeBuilder.buildBackgroundStyle( styleBuffer,
					classStyle,
					this );
			if ( HTMLRenderOption.LAYOUT_PREFERENCE_FIXED.equals( layoutPreference ) )
			{
				// build the width
				DimensionType width = page.getPageWidth( );
				if ( width != null )
				{
					styleBuffer.append( " width:" );
					styleBuffer.append( width.toString( ) );
					styleBuffer.append( ";" );
				}
			}
			// Build MasterPage's margin as padding.
			if ( HTMLRenderOption.LAYOUT_PREFERENCE_FIXED.equals( layoutPreference ) )
			{
				Object genBy = page.getGenerateBy( );
				if ( genBy instanceof SimpleMasterPageDesign )
				{
					SimpleMasterPageDesign SimpleMasterPage = (SimpleMasterPageDesign) genBy;
					String padding = SimpleMasterPage.getLeftMargin( )
							.toString( );
					if ( null != padding )
					{
						styleBuffer.append( "padding-left: " );
						styleBuffer.append( padding );
						styleBuffer.append( ";" );
					}
					padding = SimpleMasterPage.getTopMargin( ).toString( );
					if ( null != padding )
					{
						styleBuffer.append( "padding-top: " );
						styleBuffer.append( padding );
						styleBuffer.append( ";" );
					}
					padding = SimpleMasterPage.getRightMargin( ).toString( );
					if ( null != padding )
					{
						styleBuffer.append( "padding-right: " );
						styleBuffer.append( padding );
						styleBuffer.append( ";" );
					}
					padding = SimpleMasterPage.getBottomMargin( ).toString( );
					if ( null != padding )
					{
						styleBuffer.append( "padding-bottom: " );
						styleBuffer.append( padding );
						styleBuffer.append( ";" );
					}
				}
			}
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
		}
		
		if ( htmlRtLFlag )
		{
			writer.attribute( HTMLTags.ATTR_HTML_DIR, "RTL" );
		}
		
		if ( pageNo > 1 )
		{
			writer.attribute( HTMLTags.ATTR_STYLE, "page-break-before: always;" );
		}

		//output page header
		if ( page != null )
		{
			if ( outputMasterPageContent )
			{
				// output DIV for page header
				boolean showHeader = true;
				Object genBy = page.getGenerateBy( );
				if ( genBy instanceof SimpleMasterPageDesign )
				{
					SimpleMasterPageDesign masterPage = (SimpleMasterPageDesign) genBy;
					if ( !masterPage.isShowHeaderOnFirst( ) )
					{
						if ( page.getPageNumber( ) == 1 )
						{
							showHeader = false;
						}
					}
				}
				if ( showHeader )
				{
					writer.openTag( HTMLTags.TAG_DIV );
					
					StringBuffer styleBuffer = new StringBuffer( );
					if (isEmbeddable)
					{
						AttributeBuilder.buildPageStyle(styleBuffer, page.getStyle(), this);
					}
					else
					{
						writer.attribute(HTMLTags.ATTR_CLASS, page.getStyleClass());
						AttributeBuilder.buildPageStyle(styleBuffer, page.getInlineStyle(), this);
					}
					
					//output the page header attribute
					writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

					contentVisitor.visitChildren( page.getPageHeader( ), null );

					// close the page header
					writer.closeTag( HTMLTags.TAG_DIV );
				}
			}
		}
		
		// start output the page body , with the body style 
		writer.openTag( HTMLTags.TAG_DIV );

		if ( page != null )
		{
			Object genBy = page.getGenerateBy( );
			if ( genBy instanceof MasterPageDesign )
			{
				MasterPageDesign masterPage = (MasterPageDesign) genBy;
				StringBuffer styleBuffer = new StringBuffer( );
				if( !pageFooterFloatFlag )
				{
					htmlEmitter.buildSize( styleBuffer,
							HTMLTags.ATTR_MIN_HEIGHT,
							masterPage.getPageHeight( ) );
				}
				writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
			}
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

		//close the page body (DIV)		
		writer.closeTag( HTMLTags.TAG_DIV );
		
		//output page footer
		if ( page != null )
		{
			if ( outputMasterPageContent )
			{
				boolean showFooter = true;
				Object genBy = page.getGenerateBy( );
				if ( genBy instanceof SimpleMasterPageDesign )
				{
					SimpleMasterPageDesign masterPage = (SimpleMasterPageDesign) genBy;
					if ( !masterPage.isShowFooterOnLast( ) )
					{
						long totalPage = page.getPageNumber( );
						IReportContent report = page.getReportContent( );
						if ( report != null )
						{
							totalPage = report.getTotalPage( );
						}
						if ( page.getPageNumber( ) ==  totalPage)
						{
							showFooter = false;
						}
					}
				}
				if ( showFooter )
				{

					// start output the page footer
					writer.openTag( HTMLTags.TAG_DIV );
					
					//build page footer style
					StringBuffer styleBuffer = new StringBuffer( );
					if (isEmbeddable)
					{
						AttributeBuilder.buildPageStyle(styleBuffer, page.getStyle(), this);
					}
					else
					{
						writer.attribute(HTMLTags.ATTR_CLASS, page.getStyleClass());
						AttributeBuilder.buildPageStyle(styleBuffer, page.getInlineStyle(), this);
					}
					//output the page footer attribute
					writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

					contentVisitor.visitChildren( page.getPageFooter( ), null );

					// close the page footer
					writer.closeTag( HTMLTags.TAG_DIV );
				}
			}
		}
		
		// close the page tag ( DIV )
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

		// If the top level table has the property text-align, the table should
		// be align to the page box.
		if ( needImplementAlignTable( table ) )
		{
			writer.openTag( HTMLTags.TAG_DIV );
			writer.attribute( HTMLTags.ATTR_ALIGN, table.getStyle( )
					.getTextAlign( ) );
		}
		
		logger.log( Level.FINE, "[HTMLTableEmitter] Start table" ); //$NON-NLS-1$
		if ( enableMetadata )
		{
			metadataEmitter.startWrapTable( table );
		}
		writer.openTag( HTMLTags.TAG_TABLE );
		
		//output class attribute.
		setStyleName( table.getStyleClass( ) );

		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildTableStyle( table, styleBuffer, layoutPreference );
		// output style
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

		// bookmark
		String bookmark = table.getBookmark( );
		if ( bookmark == null )
		{
			bookmark = idGenerator.generateUniqueID( );
			table.setBookmark( bookmark );
		}
		HTMLEmitterUtil.setBookmark( writer, null, bookmark );

		// Add it to active id list, and output type ��iid to html
		HTMLEmitterUtil.setActiveIDTypeIID(writer, ouputInstanceIDs, table);

		// table caption
		String caption = table.getCaption( );
		if ( caption != null && caption.length( ) > 0 )
		{
			writer.openTag( HTMLTags.TAG_CAPTION );
			writer.text( caption );
			writer.closeTag( HTMLTags.TAG_CAPTION );
		}

		// include select handle table
		if ( enableMetadata )
		{
			metadataEmitter.startTable( table );
		}

		writeColumns( table );
	}

	protected void writeColumns( ITableContent table )
	{
		for ( int i = 0; i < table.getColumnCount( ); i++ )
		{
			IColumn column = table.getColumn( i );
			
			if ( isColumnHidden( column ) )
			{
				continue;
			}

			writer.openTag( HTMLTags.TAG_COL );

			//column style is output in Cell's columnRelatedStyle

			// width
			StringBuffer styleBuffer = new StringBuffer( );
			htmlEmitter.buildColumnStyle( column, styleBuffer );
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
			
			if ( enableMetadata )
			{
				// Instance ID
				InstanceID iid = column.getInstanceID( );			
				if ( iid != null )
				{
					writer.attribute( "iid", iid.toString( ) );
				}
			}
			
			writer.closeNoEndTag( );
		}
	}
	
	
	/**
	 * check whether to hide the column.
	 * @param column
	 * @return
	 */
	private boolean isColumnHidden( IColumn column )
	{
		String formats = column.getVisibleFormat( );
		if ( formats != null
				&& ( formats.indexOf( EngineIRConstants.FORMAT_TYPE_VIEWER ) >= 0 || formats
						.indexOf( BIRTConstants.BIRT_ALL_VALUE ) >= 0 ) )
		{
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTable(org.eclipse.birt.report.engine.content.ITableContent)
	 */
	public void endTable( ITableContent table )
	{

		//	include select handle table
		if ( enableMetadata )
		{
			metadataEmitter.endTable( table );
		}
				
		writer.closeTag( HTMLTags.TAG_TABLE );

		if ( enableMetadata )
		{
			metadataEmitter.endWrapTable( table );
		}
		
		if ( needImplementAlignTable( table ) )
		{
			writer.closeTag( HTMLTags.TAG_DIV );
		}
		
		logger.log( Level.FINE, "[HTMLTableEmitter] End table" ); //$NON-NLS-1$
	}
	
	/**
	 * Judge needing implement the align table or not.
	 * The align table should be align according to the page box.
	 * @param table
	 * @return
	 */
	protected boolean needImplementAlignTable( ITableContent table )
	{
		// the table should be the top level.
		if ( report.getRoot( ) == table.getParent( ) )
		{
			// The table must has the width, and the width is not 100%.
			DimensionType width = table.getWidth( );
			if ( null != width && !"100%".equals( width.toString( ) ) )
			{
				// The table must be a block table.
				IStyle style = table.getStyle( );
				CSSValue display = style.getProperty( IStyle.STYLE_DISPLAY );
				if ( null == display || IStyle.BLOCK_VALUE == display )
				{
					// The text-algin value must be center or right.
					CSSValue algin = style.getProperty( IStyle.STYLE_TEXT_ALIGN );
					if( IStyle.CENTER_VALUE == algin || IStyle.RIGHT_VALUE == algin)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableHeader(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableHeader( ITableBandContent band )
	{
		writer.openTag( HTMLTags.TAG_THEAD );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableHeader(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableHeader( ITableBandContent band )
	{
		writer.closeTag( HTMLTags.TAG_THEAD );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableBody(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableBody( ITableBandContent band )
	{
		writer.openTag( HTMLTags.TAG_TBODY );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableBody(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableBody( ITableBandContent band )
	{
		writer.closeTag( HTMLTags.TAG_TBODY );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startTableFooter(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableFooter( ITableBandContent band )
	{
		writer.openTag( HTMLTags.TAG_TFOOT );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endTableFooter(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableFooter( ITableBandContent band )
	{
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

		writer.openTag( HTMLTags.TAG_TR );
		if ( enableMetadata )
		{
			metadataEmitter.startRow( row );
			outputRowMetaData( row );
		}

		setStyleName( row.getStyleClass( ) );

		// bookmark
		HTMLEmitterUtil.setBookmark(  writer, null, row.getBookmark( ) );

		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildRowStyle( row, styleBuffer );
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
	}
	
	protected void outputRowMetaData( IRowContent rowContent )
	{
		Object parent = rowContent.getParent( );
		if ( parent instanceof ITableBandContent )
		{
			ITableBandContent bandContent = (ITableBandContent) parent;
			IGroupContent group = rowContent.getGroup( );
			String groupId = rowContent.getGroupId( );
			if ( groupId != null )
			{
				writer.attribute( HTMLTags.ATTR_GOURP_ID, groupId );
			}
			String rowType = null;
			String metaType = null;

			int bandType = bandContent.getBandType( );
			if ( bandType == ITableBandContent.BAND_HEADER )
			{
				metaType = "wrth";
				rowType = "header";
			}
			else if ( bandType == ITableBandContent.BAND_FOOTER )
			{
				metaType = "wrtf";
				rowType = "footer";
			}
			else if ( bandType == ITableBandContent.BAND_GROUP_HEADER )
			{
				rowType = "group-header";
				if ( group != null )
				{
					metaType = "wrgh" + group.getGroupLevel( );
				}
			}
			else if ( bandType == ITableBandContent.BAND_GROUP_FOOTER )
			{
				rowType = "group-footer";
				if ( group != null )
				{
					metaType = "wrgf" + group.getGroupLevel( );
				}
			}
			writer.attribute( HTMLTags.ATTR_TYPE, metaType );
			writer.attribute( HTMLTags.ATTR_ROW_TYPE, rowType );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endRow(org.eclipse.birt.report.engine.content.IRowContent)
	 */
	public void endRow( IRowContent row )
	{
		if ( enableMetadata )
		{
			metadataEmitter.endRow( row );
		}
		// assert currentData != null;
		//
		// currentData.adjustCols( );
		writer.closeTag( HTMLTags.TAG_TR );
	}

	private boolean isCellInTableHead( ICellContent cell )
	{
		IElement row = cell.getParent( );
		if ( row instanceof IRowContent )
		{
			IElement tableBand = row.getParent( );
			if ( tableBand instanceof ITableBandContent )
			{
				int type = ( (ITableBandContent)tableBand ).getBandType( ); 
				if ( type == ITableBandContent.BAND_HEADER )
				{
					return true;
				}
			}
		}		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startCell(org.eclipse.birt.report.engine.content.ICellContent)
	 */
	public void startCell( ICellContent cell )
	{				
		leafCell = cell;
		cellFilled = false;
		
		
		logger.log( Level.FINE, "[HTMLTableEmitter] Start cell." ); //$NON-NLS-1$
			
		// output 'th' tag in table head, otherwise 'td' tag
		boolean isInTableHead = isCellInTableHead( cell ); 
		if ( isInTableHead )
		{
			writer.openTag( HTMLTags.TAG_TH ); //$NON-NLS-1$
		}
		else
		{
			writer.openTag( HTMLTags.TAG_TD ); //$NON-NLS-1$
		}

		// set the 'name' property
		setStyleName( cell.getStyleClass( ) );

		// colspan
		int colSpan = cell.getColSpan( );
		
		if ( colSpan > 1 )
		{
			writer.attribute( HTMLTags.ATTR_COLSPAN, colSpan );
		}

		// rowspan
		int rowSpan = cell.getRowSpan( ); 
		if ( rowSpan > 1 )
		{
			writer.attribute( HTMLTags.ATTR_ROWSPAN, rowSpan );
		}

		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildCellStyle( cell, styleBuffer, isInTableHead );
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
		htmlEmitter.handleCellAlign( cell );

		if ( !startedGroups.isEmpty( ) )
		{
			Iterator iter = startedGroups.iterator( );
			while (iter.hasNext( ))
			{
				IGroupContent group = (IGroupContent) iter.next( );
				outputBookmark( group );
			}
			startedGroups.clear( );
		}
		
		if ( enableMetadata )
		{
			metadataEmitter.startCell( cell );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endCell(org.eclipse.birt.report.engine.content.ICellContent)
	 */
	public void endCell( ICellContent cell )
	{
		leafCell = null;
		cellFilled = false;
		
		logger.log( Level.FINE, "[HTMLReportEmitter] End cell." ); //$NON-NLS-1$

		if ( enableMetadata )
		{
			metadataEmitter.endCell( cell );
		}
		if ( isCellInTableHead( cell )	)
		{
			writer.closeTag( HTMLTags.TAG_TH );
		}
		else
		{
			writer.closeTag( HTMLTags.TAG_TD );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startContainer(org.eclipse.birt.report.engine.content.IContainerContent)
	 */
	public void startContainer( IContainerContent container )
	{
		IStyle mergedStyle = container.getStyle( );
		logger.log( Level.FINE, "[HTMLReportEmitter] Start container" ); //$NON-NLS-1$

		htmlEmitter.openContainerTag( container );

		// class
		setStyleName( container.getStyleClass( ) );

		// bookmark
		String bookmark = container.getBookmark( );

		if ( bookmark == null )
		{
			bookmark = idGenerator.generateUniqueID( );
			container.setBookmark( bookmark );
		}

		HTMLEmitterUtil.setBookmark(  writer, HTMLTags.TAG_DIV, bookmark );
		
		HTMLEmitterUtil
				.setActiveIDTypeIID( writer, ouputInstanceIDs, container );

		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildContainerStyle( container, styleBuffer );
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

		htmlEmitter.handleHorizontalAlign( mergedStyle );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#endContainer(org.eclipse.birt.report.engine.content.IContainerContent)
	 */
	public void endContainer( IContainerContent container )
	{
		htmlEmitter.closeContainerTag( );

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

		logger.log( Level.FINE, "[HTMLReportEmitter] Start text" ); //$NON-NLS-1$

		//Resize if the text generated by TemplateDesign
		//resizeTemplateElement( text);

		DimensionType x = text.getX( );
		DimensionType y = text.getY( );
		DimensionType width = text.getWidth( );
		DimensionType height = text.getHeight( );
		String textValue = text.getText( );
		if (textValue == null || textValue == "" ) //$NON-NLS-1$
		{
			textValue = " "; //$NON-NLS-1$
		}
		
		int display;
		// If the item is multi-line, we should check if it can be inline-block
		if ( textValue != null && textValue.indexOf( '\n' ) >= 0 )
		{
			display = getElementType( x, y, width, height, mergedStyle );
		}
		else
		{
			display = getElementType( x, y, null, null, mergedStyle );
		}

		// action
		String tagName;
		String url = validate( text.getHyperlinkAction( ) );
		boolean metadataOutput = false;
		if ( url != null )
		{
			//output select class
			if ( enableMetadata )
			{
				metadataOutput = metadataEmitter.startText( text,
						HTMLTags.TAG_SPAN );
			}
			tagName = HTMLTags.TAG_A;
			outputAction( text.getHyperlinkAction( ), url );
		}
		else
		{
			if ( enableMetadata )
			{
				metadataOutput = metadataEmitter.startText( text, HTMLEmitterUtil.getTagByType(
						display, DISPLAY_FLAG_ALL ) );
			}
			tagName = openTagByType( display, DISPLAY_FLAG_ALL );
		}

		setStyleName( text.getStyleClass( ) );

		// bookmark
		if ( !metadataOutput )
		{
			outputBookmark( text, tagName );
		}
		
		// title
		writer.attribute( HTMLTags.ATTR_TITLE, text.getHelpText( ) ); //$NON-NLS-1$
		
		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildTextStyle( text, styleBuffer, display, url );
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
		
		htmlEmitter.handleVerticalAlignBegine( text );
		writer.text( textValue );
		htmlEmitter.handleVerticalAlignEnd( text );
		
		writer.closeTag( tagName );
		if ( enableMetadata )
		{
			metadataEmitter.endText( text );
		}
		cellFilled = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startForeign(org.eclipse.birt.report.engine.content.IForeignContent)
	 */
	public void startForeign( IForeignContent foreign )
	{
		IStyle mergedStyle = foreign.getStyle( );

		logger.log( Level.FINE, "[HTMLReportEmitter] Start foreign" ); //$NON-NLS-1$

		resizeTemplateElement( foreign);
		
		DimensionType x = foreign.getX( );
		DimensionType y = foreign.getY( );
		DimensionType width = foreign.getWidth( );
		DimensionType height = foreign.getHeight( );

		int display;
		display = getElementType( x, y, width, height, mergedStyle );

		// create default bookmark if we need output metadata
		if ( foreign.getGenerateBy( ) instanceof TemplateDesign )
		{
			//FIXME: actually, it should be birt-template-design
			String bookmark = foreign.getBookmark( );
			if ( bookmark == null )
			{
				bookmark = idGenerator.generateUniqueID( );
				foreign.setBookmark( bookmark );
			}
		}
		
		boolean isTemplate = false;
		Object genBy = foreign.getGenerateBy( );
		if ( genBy instanceof TemplateDesign )
		{
			isTemplate = true;
			// all the template element should be horizontal center of it's
			// parent.
			writer.openTag( HTMLTags.TAG_DIV );
			writer.attribute( HTMLTags.ATTR_ALIGN, "middle" );
		}

		// action
		String tagName;
		String url = validate( foreign.getHyperlinkAction( ) );
		boolean metadataOutput = false;
		if ( url != null )
		{
			if ( enableMetadata )
			{
				metadataOutput = metadataEmitter.startForeign( foreign,
						HTMLTags.TAG_SPAN );
			}
			tagName = HTMLTags.TAG_A;
			outputAction( foreign.getHyperlinkAction( ), url );
		}
		else
		{
			if ( enableMetadata )
			{
				metadataOutput = metadataEmitter.startForeign( foreign, HTMLEmitterUtil.getTagByType(
						display, DISPLAY_FLAG_ALL ) );
			}
			tagName = openTagByType( display, DISPLAY_FLAG_ALL );
		}

		setStyleName( foreign.getStyleClass( ) );

		// bookmark
		if ( !metadataOutput )
		{
			outputBookmark( foreign, tagName );
		}

		// title
		writer.attribute( HTMLTags.ATTR_TITLE, foreign.getHelpText( ) );

		if( isTalbeTemplateElement( foreign ) )
		{
			//set lines to dotted lines
			mergedStyle.setProperty( IStyle.STYLE_BORDER_TOP_STYLE, IStyle.DOTTED_VALUE );
			mergedStyle.setProperty( IStyle.STYLE_BORDER_BOTTOM_STYLE, IStyle.DOTTED_VALUE );
			mergedStyle.setProperty( IStyle.STYLE_BORDER_LEFT_STYLE, IStyle.DOTTED_VALUE );
			mergedStyle.setProperty( IStyle.STYLE_BORDER_RIGHT_STYLE, IStyle.DOTTED_VALUE );
			mergedStyle.setProperty( IStyle.STYLE_FONT_FAMILY, IStyle.SANS_SERIF_VALUE );
		}
		
		StringBuffer styleBuffer = new StringBuffer( );
		htmlEmitter.buildForeignStyle( foreign, styleBuffer, display, url );
		writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

		String rawType = foreign.getRawType( );
		boolean isHtml = IForeignContent.HTML_TYPE.equalsIgnoreCase( rawType );
		if ( isHtml )
		{
			htmlEmitter.handleVerticalAlignBegine( foreign );
			outputHtmlText( foreign );
			htmlEmitter.handleVerticalAlignEnd( foreign );
		}

		writer.closeTag( tagName );
		if( isTemplate )
		{
			writer.openTag( HTMLTags.TAG_DIV );
		}
		if ( enableMetadata )
		{
			metadataEmitter.endForeign( foreign );
		}
		
		/**
		 * We suppose the foreign content will all occupy space now.
		 * In fact some foreign contents don't occupy space.
		 * For example: a empty html text will not occupy space in html.
		 * It needs to be solved in the future.
		 */
		cellFilled = true;
	}
	
	
	private void outputHtmlText(IForeignContent foreign)
	{
		Object rawValue = foreign.getRawValue( );
		String text = rawValue == null ? null : rawValue.toString( );
		Document doc = new TextParser( ).parse( text,
				TextParser.TEXT_TYPE_HTML );
		ReportDesignHandle design = (ReportDesignHandle) runnable
				.getDesignHandle( );
		HTMLProcessor htmlProcessor = new HTMLProcessor( design );

		HashMap styleMap = new HashMap( );

		Element body = null;
		if ( doc != null )
		{
			NodeList bodys = doc.getElementsByTagName( "body" );
			if ( bodys.getLength( ) > 0 )
			{
				body = (Element) bodys.item( 0 );
			}
		}
		if ( body != null )
		{
			htmlProcessor.execute( body, styleMap );
			processNodes( body, styleMap );
		}
	}

	/**
	 * Visits the children nodes of the specific node
	 * 
	 * @param visitor
	 *            the ITextNodeVisitor instance
	 * @param ele
	 *            the specific node
	 */
	private void processNodes( Element ele, HashMap cssStyles )
	{
		for ( Node node = ele.getFirstChild( ); node != null; node = node
				.getNextSibling( ) )
		{

			// At present we only deal with the text and element nodes
			if ( node.getNodeType( ) == Node.TEXT_NODE
					|| node.getNodeType( ) == Node.ELEMENT_NODE )
			{
				if ( !node.getNodeName( ).equals( "#text" ) )
				{
					startNode( node, cssStyles );
				}
				if ( node.getNodeType( ) == Node.TEXT_NODE )
				{
					if ( isScriptText( node ) )
					{
						textForScript( node.getNodeValue( ) );
					}
					else
					{
						// bug132213 in text item should only deal with the
						// escape special characters: < > &
						// writer.text( node.getNodeValue( ), false, true );
						writer.textForHtmlItem( node.getNodeValue( ) );
					}
				}
				else
				{
					processNodes( (Element) node, cssStyles );
				}
				if ( !node.getNodeName( ).equals( "#text" ) )
				{
					endNode( node );
				}
			}
		}
	}
	
	/**
	 * test if the text node is in the script
	 * @param node text node
	 * @return true if the text is a script, otherwise, false.
	 */
	private boolean isScriptText( Node node )
	{
		Node parent = node.getParentNode( );
		if ( parent != null )
		{
			if ( parent.getNodeType( ) == Node.ELEMENT_NODE )
			{
				String tag = parent.getNodeName( );
				if ( HTMLTags.TAG_SCRIPT.equalsIgnoreCase( tag ) )
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * the script is output directly.
	 * @param text
	 */
	private void textForScript( String text )
	{
		writer.text( text, false, false );
	}

	public void startNode( Node node, HashMap cssStyles )
	{
		String nodeName = node.getNodeName( );
		HashMap cssStyle = (HashMap) cssStyles.get( node );
		writer.openTag( nodeName );
		NamedNodeMap attributes = node.getAttributes( );
		if ( attributes != null )
		{
			for ( int i = 0; i < attributes.getLength( ); i++ )
			{
				Node attribute = attributes.item( i );
				String attrName = attribute.getNodeName( );
				String attrValue = attribute.getNodeValue( );

				if ( attrValue != null )
				{
					if ( "img".equalsIgnoreCase( nodeName )
							&& "src".equalsIgnoreCase( attrName ) )
					{
						String attrValueTrue = handleStyleImage( attrValue );
						if ( attrValueTrue != null )
						{
							attrValue = attrValueTrue;
						}
					}
					writer.attribute( attrName, attrValue );
				}
			}
		}
		if ( cssStyle != null )
		{
			StringBuffer buffer = new StringBuffer( );
			Iterator ite = cssStyle.entrySet( ).iterator( );
			while ( ite.hasNext( ) )
			{
				Map.Entry entry = (Map.Entry) ite.next( );
				Object keyObj = entry.getKey( );
				Object valueObj = entry.getValue( );
				if ( keyObj == null || valueObj == null )
				{
					continue;
				}
				String key = keyObj.toString( );
				String value = valueObj.toString( );
				buffer.append( key );
				buffer.append( ":" );
				if ( "background-image".equalsIgnoreCase( key ) )
				{
					String valueTrue = handleStyleImage( value );
					if ( valueTrue != null )
					{
						value = valueTrue;
					}
					buffer.append( "url(" );
					buffer.append( value );
					buffer.append( ")" );
				}
				else
				{
					buffer.append( value.replaceAll( " ", "" ) );
				}
				buffer.append( ";" );
			}
			if ( buffer.length( ) != 0 )
			{
				writer.attribute( "style", buffer.toString( ) );
			}
		}
	}

	public void endNode( Node node )
	{
		writer.closeTag( node.getNodeName( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startLabel(org.eclipse.birt.report.engine.content.ILabelContent)
	 */
	public void startLabel( ILabelContent label )
	{
		String bookmark = label.getBookmark( );
		if ( bookmark == null ) 
		{
			bookmark = idGenerator.generateUniqueID( );
			label.setBookmark( bookmark );
		}
		startText( label );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.IContentEmitter#startData(org.eclipse.birt.report.engine.content.IDataContent)
	 */
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


		logger.log( Level.FINE, "[HTMLImageEmitter] Start image" ); //$NON-NLS-1$ 

		StringBuffer styleBuffer = new StringBuffer( );
		int display = checkElementType( image.getX( ), image.getY( ),
				mergedStyle, styleBuffer );
		boolean isSelectHandleTableChart = false;
		if ( enableMetadata  )
		{
			isSelectHandleTableChart = metadataEmitter.startImage( image );
		}		

		String tag = openTagByType( display, DISPLAY_BLOCK );

		// action
		boolean hasAction = handleAction( image.getHyperlinkAction( ) );

		String imgUri = getImageURI( image );
		boolean useSVG = (( image.getMIMEType( ) != null )
				&& image.getMIMEType( ).equalsIgnoreCase( "image/svg+xml" )) //$NON-NLS-1$
				|| (( image.getExtension( ) != null )
				&& image.getExtension( ).equalsIgnoreCase( ".svg" )) //$NON-NLS-1$
				|| (( image.getURI( ) != null )
				&& image.getURI( ).toLowerCase( ).endsWith( ".svg" )); //$NON-NLS-1$
		if ( useSVG )
		{ // use svg
			writer.openTag( HTMLTags.TAG_EMBED );

			// bookmark
			String bookmark = image.getBookmark( );				
			
			if ( !isSelectHandleTableChart )
			{
				if ( bookmark == null )
				{
					bookmark = idGenerator.generateUniqueID( );
					image.setBookmark( bookmark );
				}
				outputBookmark( image, HTMLTags.ATTR_IMAGE ); //$NON-NLS-1$
			}

			//	onresize gives the SVG a change to change its content
			writer.attribute( "onresize", bookmark+".reload()"); //$NON-NLS-1$
			
			writer.attribute( HTMLTags.ATTR_TYPE, "image/svg+xml" ); //$NON-NLS-1$
			writer.attribute( HTMLTags.ATTR_SRC, imgUri );			

			// alternative text
			String altText = image.getAltText( );
			if ( altText == null )
			{
				writer.attributeAllowEmpty( HTMLTags.ATTR_ALT, "" );
			}
			else
			{
				writer.attribute( HTMLTags.ATTR_ALT, altText );
			}
			
			setStyleName( image.getStyleClass( ) );
			// build style
			htmlEmitter.buildImageStyle( image, styleBuffer, display );
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );
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
			
			// bookmark
			String bookmark = image.getBookmark( );				
			if ( !isSelectHandleTableChart )
			{
				if ( bookmark == null )
				{
					bookmark = idGenerator.generateUniqueID( );
					image.setBookmark( bookmark );
				}
				outputBookmark( image, HTMLTags.ATTR_IMAGE ); //$NON-NLS-1$
			}

			String ext = image.getExtension( );
			// FIXME special process, such as encoding etc
			writer.attribute( HTMLTags.ATTR_SRC, imgUri );

			if ( hasImageMap )
			{
				// BUGZILLA 119245 request chart (without hyperlink) can't have
				// borders, the BROWSER add blue-solid border to the image with
				// maps.
				if ( !hasAction )
				{
					// disable the border, if the user defines border with the
					// image, it will be overided by the following style setting
					IStyle style = image.getStyle( );
					if ( style.getBorderTopStyle( ) == null )
					{
						// user doesn't define the border, remove it.
						styleBuffer.append( "border-top-style:none;" );
					}
					else
					{
						// use define the border-style, but not define the
						// border color, use the default
						// color.
						if ( style.getBorderTopColor( ) == null )
						{
							styleBuffer.append( "border-top-color:black" );
						}
					}
					if ( style.getBorderBottomStyle( ) == null )
					{
						styleBuffer.append( "border-bottom-style:none;" );
					}
					else
					{
						if ( style.getBorderBottomColor( ) == null )
						{
							styleBuffer.append( "border-bottom-color:black" );
						}
					}
					if ( style.getBorderLeftStyle( ) == null )
					{
						styleBuffer.append( "border-left-style:none;" );
					}
					else
					{
						if ( style.getBorderLeftColor( ) == null )
						{
							styleBuffer.append( "border-left-color:black" );
						}
					}
					if ( style.getBorderRightStyle( ) == null )
					{
						styleBuffer.append( "border-right-style:none;" );
					}
					else
					{
						if ( style.getBorderRightColor( ) == null )
						{
							styleBuffer.append( "border-right-color:black" );
						}
					}
				}
				writer.attribute( HTMLTags.ATTR_USEMAP, "#" + imageMapId ); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// alternative text
			String altText = image.getAltText( );
			if ( altText == null )
			{
				writer.attributeAllowEmpty( HTMLTags.ATTR_ALT, "" );
			}
			else
			{
				writer.attribute( HTMLTags.ATTR_ALT, altText );
			}

			// help text
			writer.attribute( HTMLTags.ATTR_TITLE, image.getHelpText( ) );

			// build style
			htmlEmitter.buildImageStyle( image, styleBuffer, display );
			writer.attribute( HTMLTags.ATTR_STYLE, styleBuffer.toString( ) );

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
		
		// include	select handle chart
		if ( enableMetadata )
		{
			metadataEmitter.endImage( image );
		}
		cellFilled = true;
	}
	
	/**
	 * gets the image's URI
	 * 
	 * @param image
	 *            the image content
	 * @return image's URI
	 */
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
					imgUri = imageHandler.onDesignImage( img, reportContext );
					break;
				case IImage.URL_IMAGE :
					imgUri = imageHandler.onURLImage( img, reportContext );
					break;
				case IImage.REPORTDOC_IMAGE :
					imgUri = imageHandler.onDocImage( img, reportContext );
					break;
				case IImage.CUSTOM_IMAGE :
					imgUri = imageHandler.onCustomImage( img, reportContext );
					break;
				case IImage.FILE_IMAGE :
					imgUri = imageHandler.onFileImage( img, reportContext );
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
	 * @param styleName
	 *            the style name
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

	protected void outputBookmark( IContent content, String tagName )
	{
		String bookmark = content.getBookmark( );
		HTMLEmitterUtil.setBookmark(  writer, tagName, bookmark );
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
		//if ( x != null || y != null )
		//{
		//	styleBuffer.append( "position: absolute;" ); //$NON-NLS-1$
		//	AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_LEFT, x );
		//	AttributeBuilder.buildSize( styleBuffer, HTMLTags.ATTR_TOP, y );
		//}
		return getElementType( x, y, width, height, style);
	}

	public int getElementType( DimensionType x, DimensionType y,
			DimensionType width, DimensionType height, IStyle style )
	{
		return htmlEmitter.getElementType( x, y, width, height, style );
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
		String tag = HTMLEmitterUtil.getTagByType( display, mask );
		if ( tag != null)
		{
			writer.openTag( tag );
		}
		return tag;
	}

	/**
	 * Checks the Action object and then output corresponding tag and property.
	 * 
	 * @param action
	 *            The <code>IHyperlinkAction</code> object.
	 * @return A <code>boolean</code> value indicating whether the Action
	 *         object is valid or not.
	 */
	protected boolean handleAction( IHyperlinkAction action )
	{
		String url = validate( action );
		if ( url != null )
		{
			outputAction( action, url );
		}
		return url != null;
	}

	/**
	 * Outputs an hyperlink action.
	 * 
	 * @param action
	 *            the hyperlink action.
	 */
	protected void outputAction( IHyperlinkAction action, String url )
	{
		writer.openTag( HTMLTags.TAG_A );
		writer.attribute( HTMLTags.ATTR_HREF, url );
		writer.attribute( HTMLTags.ATTR_TARGET, action.getTargetWindow( ) );
	}

	/**
	 * Judges if a hyperlink is valid.
	 * 
	 * @param action
	 *            the hyperlink action
	 * @return
	 */
	private String validate( IHyperlinkAction action )
	{
		if ( action == null )
		{
			return null;
		}
		String systemId = runnable == null ? null : runnable.getReportName( );
		Action act = new Action( systemId, action );

		if ( actionHandler == null )
		{
			return null;
		}

		String link = actionHandler.getURL( act, reportContext );
		if ( link != null && !link.equals( "" ) )//$NON-NLS-1$
		{
			return link;
		}
		return null;
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
		ReportDesignHandle design = (ReportDesignHandle) runnable
				.getDesignHandle( );
		URL url = design.findResource( uri, IResourceLocator.IMAGE );
		if ( url == null )
		{
			return uri;
		}
		uri = url.toExternalForm( );
		Image image = new Image( uri );
		image.setReportRunnable( runnable );
		image.setRenderOption( renderOption );
		String imgUri = null;
		if ( imageHandler != null )
		{
			switch ( image.getSource( ) )
			{

				case IImage.URL_IMAGE :
					imgUri = imageHandler.onURLImage( image, reportContext );
					break;

				case IImage.FILE_IMAGE :
					imgUri = imageHandler.onFileImage( image, reportContext );
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

	/**
	 * Resize chart template and table template element.
	 * 
	 * @param content
	 *            the styled element content
	 */
	private void resizeTemplateElement( IContent content )
	{

			Object genBy = content.getGenerateBy( );
			if( genBy instanceof TemplateDesign )
			{
				TemplateDesign template = (TemplateDesign) genBy;
				
				String allowedType = template.getAllowedType( );
				if ( "ExtendedItem".equals(allowedType ) )
				{
					// Resize chart template element
					IStyle style = content.getStyle( );
					style.setProperty( IStyle.STYLE_CAN_SHRINK, IStyle.FALSE_VALUE );
					content.setWidth( new DimensionType( 3, DimensionType.UNITS_IN ) );
					content.setHeight( new DimensionType( 3, DimensionType.UNITS_IN ) );
				}
				else if ( "Table".equals(allowedType) )
				{
					// Resize table template element
					IStyle style = content.getStyle( );
					style.setProperty( IStyle.STYLE_CAN_SHRINK, IStyle.FALSE_VALUE );
					content.setWidth( new DimensionType( 5, DimensionType.UNITS_IN ) );
				}
		}
	}
	
	/**
	 *  judge the content is belong table template element or not.
	 * 
	 * @param content
	 *            the styled element content
	 */
	private boolean isTalbeTemplateElement( IContent content )
	{
		Object genBy = content.getGenerateBy( );
		if( genBy instanceof TemplateDesign )
		{
			TemplateDesign template = (TemplateDesign) genBy;
			String allowedType = template.getAllowedType( );
			if ( "Table".equals(allowedType) )
			{
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#endGroup(org.eclipse.birt.report.engine.content.IGroupContent)
	 */
	public void endGroup( IGroupContent group )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#endListBand(org.eclipse.birt.report.engine.content.IListBandContent)
	 */
	public void endListBand( IListBandContent listBand )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#endListGroup(org.eclipse.birt.report.engine.content.IListGroupContent)
	 */
	public void endListGroup( IListGroupContent group )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#endTableBand(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void endTableBand( ITableBandContent band )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#endTableGroup(org.eclipse.birt.report.engine.content.ITableGroupContent)
	 */
	public void endTableGroup( ITableGroupContent group )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#startGroup(org.eclipse.birt.report.engine.content.IGroupContent)
	 */
	public void startGroup( IGroupContent group )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#startListBand(org.eclipse.birt.report.engine.content.IListBandContent)
	 */
	public void startListBand( IListBandContent listBand )
	{
	}

	/**
	 * used to control the output of group bookmarks. 
	 * @see {@link #startTableGroup(ITableGroupContent)}
	 * @see {@link #startListGroup(IListGroupContent)}
	 */
	protected Stack startedGroups = new Stack(); 

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#startListGroup(org.eclipse.birt.report.engine.content.IListGroupContent)
	 */
	public void startListGroup( IListGroupContent group )
	{
		outputBookmark( group );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#startTableBand(org.eclipse.birt.report.engine.content.ITableBandContent)
	 */
	public void startTableBand( ITableBandContent band )
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.emitter.ContentEmitterAdapter#startTableGroup(org.eclipse.birt.report.engine.content.ITableGroupContent)
	 */
	public void startTableGroup( ITableGroupContent group )
	{
		startedGroups.push( group );
	}

	private void outputBookmark( IGroupContent group )
	{
		String bookmark = group.getBookmark( );
		if ( bookmark == null )
		{
			bookmark = idGenerator.generateUniqueID( );
			group.setBookmark( bookmark );
		}
		writer.openTag( HTMLTags.TAG_SPAN );
		writer.attribute( HTMLTags.ATTR_ID, group.getBookmark( ) );
		writer.closeTag( HTMLTags.TAG_SPAN );
	}
}

class IDGenerator
{
	protected int bookmarkId = 0;
	IDGenerator( )
	{
		this.bookmarkId = 0;
	}
	protected String generateUniqueID( )
	{
		bookmarkId ++;
		return "AUTOGENBOOKMARK_" + bookmarkId;
	}
}