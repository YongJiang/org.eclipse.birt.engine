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

package org.eclipse.birt.report.engine.emitter.pdf;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.report.engine.api.ITOCTree;
import org.eclipse.birt.report.engine.api.TOCNode;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.i18n.EngineResourceHandle;
import org.eclipse.birt.report.engine.i18n.MessageConstants;
import org.eclipse.birt.report.engine.internal.util.BundleVersionUtil;
import org.eclipse.birt.report.engine.layout.emitter.IPage;
import org.eclipse.birt.report.engine.layout.emitter.IPageDevice;

import com.ibm.icu.util.ULocale;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;


public class PDFPageDevice implements IPageDevice
{
	/**
	 * The pdf Document object created by iText
	 */
	private Document doc = null;

	/**
	 * The Pdf Writer
	 */
	private PdfWriter writer = null;
	
	private IReportContext context;
	
	private IReportContent report;
	
	private static Logger logger = Logger.getLogger( PDFPageDevice.class.getName( ) );

	private PDFPage currentPage = null;
	
	private HashMap<Float, PdfTemplate> templateMap = new HashMap<Float, PdfTemplate>();
	
	private HashMap<String, PdfTemplate> imageCache = new HashMap<String, PdfTemplate>( );
	
	/**
	 * the iText and Birt engine version info.
	 */
	private static String[] versionInfo = new String[]{
			BundleVersionUtil
					.getBundleVersion( "org.eclipse.birt.report.engine" ),
			BundleVersionUtil.getBundleVersion( "com.lowagie.itext" )};
	
	final static int MAX_PAGE_WIDTH = 14400000; //200 inch
	final static int MAX_PAGE_HEIGHT = 14400000; //200 inch

	public PDFPageDevice( OutputStream output, String title, String author, String subject, 
			String description, IReportContext context, IReportContent report )
	{
		this.context = context;
		this.report = report;
		doc = new Document( );
		try
		{
			writer = PdfWriter.getInstance( doc, new BufferedOutputStream(
					output ) );
			writer.setFullCompression( );
			writer.setRgbTransparencyBlending( true );
			EngineResourceHandle handle = new EngineResourceHandle(
					ULocale.forLocale( context.getLocale( ) ) );

			String creator = handle.getMessage( MessageConstants.PDF_CREATOR,
					versionInfo );
			doc.addCreator( creator );

			if ( null != author )
			{
				doc.addAuthor( author );
			}
			if ( null != title )
			{
				doc.addTitle( title );
			}
			if ( null != subject )
			{
				doc.addSubject( subject );
				doc.addKeywords( subject );	
			}
			if ( description != null )
			{
				doc.addHeader( "Description", description );
			}
		}
		catch( DocumentException de )
		{
			logger.log( Level.SEVERE, de.getMessage( ), de );
		}
	}
	
	/**
	 * constructor for test
	 * @param output
	 */
	public PDFPageDevice( OutputStream output )
	{
		doc = new Document();
		try
		{
			writer = PdfWriter.getInstance( doc, new BufferedOutputStream(
					output ) );
		}
		catch( DocumentException de )
		{
			logger.log( Level.SEVERE, de.getMessage( ), de );
		}
	}
	
	public void setPDFTemplate( Float scale, PdfTemplate totalPageTemplate )
	{
		templateMap.put( scale, totalPageTemplate );
	}

	public HashMap<Float, PdfTemplate> getTemplateMap( )
	{
		return templateMap;
	}

	public PdfTemplate getPDFTemplate( Float scale )
	{
		return templateMap.get( scale );
	}

	public boolean hasTemplate( Float scale )
	{
		return templateMap.containsKey( scale );
	}
		
	public HashMap<String, PdfTemplate> getImageCache( )
	{
		return imageCache;
	}
	
	public void close( ) throws Exception
	{
		writer.setPageEmpty( false );
		if(doc.isOpen( ))
		{
			doc.close();
		}
	}

	public IPage newPage( int width, int height, Color backgroundColor )
	{
		int w = Math.min( width, MAX_PAGE_WIDTH );
		int h = Math.min( height, MAX_PAGE_HEIGHT );
		currentPage = new PDFPage( w, h, doc, writer, this );
		currentPage.drawBackgroundColor( backgroundColor, 0, 0, w, h );
		return currentPage;
	}
	
	public void createTOC( Set bookmarks )
	{
		if ( bookmarks.isEmpty( ) )
		{
			writer.setViewerPreferences( PdfWriter.PageModeUseNone );
			return;
		}
		ULocale ulocale = null;
		Locale locale = context.getLocale( );
		if ( locale == null )
		{
			ulocale = ULocale.getDefault( );
		}
		else
		{
			ulocale = ULocale.forLocale( locale );
		}
		// Before closing the document, we need to create TOC.
		ITOCTree tocTree = report.getTOCTree( "pdf", //$NON-NLS-1$
				ulocale );
		if ( tocTree == null )
		{
			writer.setViewerPreferences( PdfWriter.PageModeUseNone );
		}
		else
		{
			TOCNode rootNode = tocTree.getRoot( );
			if ( rootNode == null || rootNode.getChildren( ).isEmpty( ) )
			{
				writer.setViewerPreferences( PdfWriter.PageModeUseNone );
			}
			else
			{
				writer.setViewerPreferences( PdfWriter.PageModeUseOutlines );
				TOCHandler tocHandler = new TOCHandler( rootNode, writer
						.getDirectContent( ).getRootOutline( ), bookmarks );
				tocHandler.createTOC( );
			}
		}
	}
}
