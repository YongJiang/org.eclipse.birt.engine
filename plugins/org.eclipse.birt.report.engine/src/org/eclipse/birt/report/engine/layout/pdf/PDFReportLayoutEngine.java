/***********************************************************************
 * Copyright (c) 2004, 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.report.engine.layout.pdf;

import java.util.HashMap;
import java.util.Locale;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.format.NumberFormatter;
import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.content.Dimension;
import org.eclipse.birt.report.engine.content.IAutoTextContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.executor.IReportExecutor;
import org.eclipse.birt.report.engine.extension.IReportItemExecutor;
import org.eclipse.birt.report.engine.internal.executor.dom.DOMReportItemExecutor;
import org.eclipse.birt.report.engine.layout.ILayoutManager;
import org.eclipse.birt.report.engine.layout.ILayoutPageHandler;
import org.eclipse.birt.report.engine.layout.IReportLayoutEngine;
import org.eclipse.birt.report.engine.layout.PDFConstants;
import org.eclipse.birt.report.engine.layout.area.impl.AbstractArea;
import org.eclipse.birt.report.engine.layout.area.impl.AreaFactory;
import org.eclipse.birt.report.engine.layout.pdf.text.Chunk;
import org.eclipse.birt.report.engine.layout.pdf.text.ChunkGenerator;
import org.eclipse.birt.report.engine.presentation.IPageHint;

public class PDFReportLayoutEngine implements IReportLayoutEngine
{

	protected IReportExecutor executor;
	protected PDFLayoutEngineContext context;
	protected PDFLayoutManagerFactory factory;
	protected ILayoutPageHandler handle;
	protected HashMap options;
	protected Locale locale;
	protected long pageCount;
	protected IContentEmitter emitter;
	protected long totalPage = 0 ;
	

	public PDFReportLayoutEngine( )
	{
		options = new HashMap();
		context = new PDFLayoutEngineContext( this );
		factory = new PDFLayoutManagerFactory( context );
		context.setFactory( factory );
	}

	protected PDFLayoutManagerFactory createLayoutManagerFactory(
			PDFLayoutEngineContext context )
	{
		return new PDFLayoutManagerFactory( this.context );
	}

	protected void layoutReport( IReportContent report,
			IReportExecutor executor, IContentEmitter output )
			throws BirtException
	{
		if ( output == null )
		{
			return;
		}
		PDFPageLM pageLM = new PDFPageLM( this, context, report, output,
				executor );
		while ( pageLM.layout( ) );

	}

	public void layout( IReportExecutor executor, IReportContent report,
			IContentEmitter output, boolean pagination ) throws BirtException
	{
		context.setAllowPageBreak(pagination);
		this.executor = executor;
		this.emitter = output;
		context.setReport( report );
		setupLayoutOptions();
		if(locale!=null)
		{
			context.setLocale(locale);
		}
		else
		{
			context.setLocale(Locale.getDefault( ));
		}
		if ( output != null )
		{
			context.setFormat( output.getOutputFormat( ) );
		}
		layoutReport( report, executor, output );
		pageCount += context.getPageCount( );
		context.setPageNumber( pageCount + 1 );
		executor.close( );
		
	}
	
	protected void resolveTotalPage( IContentEmitter emitter )
			throws BirtException
	{
		IContent con = context.getUnresolvedContent( );
		if ( !( con instanceof IAutoTextContent ) )
		{
			return;
		}

		IAutoTextContent totalPageContent = (IAutoTextContent) con;
		if ( null != totalPageContent )
		{
			NumberFormatter nf = new NumberFormatter( );
			String patternStr = totalPageContent.getComputedStyle( )
					.getNumberFormat( );
			nf.applyPattern( patternStr );
			
			long totalPageCount = this.totalPage>0 ? totalPage : pageCount;
			totalPageContent.setText( nf.format( totalPageCount ));

			AbstractArea totalPageArea = null;
			ChunkGenerator cg = new ChunkGenerator( context.getFontManager( ),
					totalPageContent, true, true );
			if ( cg.hasMore( ) )
			{
				Chunk c = cg.getNext( );
				Dimension d = new Dimension(
						(int) ( c.getFontInfo( ).getWordWidth( c.getText( ) ) * PDFConstants.LAYOUT_TO_PDF_RATIO ),
						(int) ( c.getFontInfo( ).getWordHeight( ) * PDFConstants.LAYOUT_TO_PDF_RATIO ) );
				totalPageArea = (AbstractArea)AreaFactory.createTextArea( totalPageContent, c.getText( ), c.getFontInfo( ));
				totalPageArea.setWidth( Math.min( context.getMaxWidth( ), d.getWidth()) );
				totalPageArea.setHeight( Math.min( context.getMaxHeight( ), d.getHeight()) );
			}
			totalPageContent.setExtension( IContent.LAYOUT_EXTENSION,
					totalPageArea );
			emitter.startAutoText( totalPageContent );
		}
	}
	
	/**
	 * @deprecated
	 */
	public void layout( ILayoutManager parent, IReportItemExecutor executor,
			IContentEmitter emitter ) throws BirtException
	{
		IContent content = executor.execute( );
		PDFAbstractLM layoutManager = factory.createLayoutManager( (PDFStackingLM) parent,
				content, executor );
		layoutManager.layout( );
		layoutManager.close( );
	}

	/**
	 * @deprecated
	 */
	public void layout( ILayoutManager parent, IContent content,
			IContentEmitter output ) throws BirtException
	{
		IReportItemExecutor executor = new DOMReportItemExecutor( content );
		layout( parent, executor, output );
		executor.close( );
	}

	public void setPageHandler( ILayoutPageHandler handle )
	{
		this.handle = handle;
	}

	public ILayoutPageHandler getPageHandler( )
	{
		return this.handle;
	}
	
	public void cancel()
	{
		if(context!=null)
		{
			context.setCancel( true );
		}
	}
	
	protected void setupLayoutOptions()
	{
		Object fitToPage = options.get(IPDFRenderOption.FIT_TO_PAGE);
		if(fitToPage!=null && fitToPage instanceof Boolean)
		{
			if(((Boolean)fitToPage).booleanValue())
			{
				context.setFitToPage(true);
			}
		}
		Object pageBreakOnly = options.get(IPDFRenderOption.PAGEBREAK_PAGINATION_ONLY);
		if(pageBreakOnly!=null && pageBreakOnly instanceof Boolean)
		{
			if(((Boolean)pageBreakOnly).booleanValue())
			{
				context.setPagebreakPaginationOnly(true);
			}
		}
		Object pageOverflow = options.get(IPDFRenderOption.PAGE_OVERFLOW);
		if( pageOverflow!=null )
		{
			int pageOverflowType = ((Integer)pageOverflow).intValue();
			context.setPageOverflow(pageOverflowType);
		}
		else
		{
			if ( context.fitToPage() )
			{
				context.setPageOverflow(IPDFRenderOption.FIT_TO_PAGE_SIZE);
			}
		}
		Object outputDisplayNone = options
				.get( IPDFRenderOption.OUTPUT_DISPLAY_NONE );
		if ( outputDisplayNone instanceof Boolean )
		{
			if ( ( (Boolean) outputDisplayNone ).booleanValue( ) )
			{
				context.setOutputDisplayNone( true );
			}
		}

		Object textWrapping = options.get(IPDFRenderOption.PDF_TEXT_WRAPPING);
		if(textWrapping!=null && textWrapping instanceof Boolean)
		{
			if(!((Boolean)textWrapping).booleanValue())
			{
				context.setTextWrapping(false);
			}
		}
		Object fontSubstitution = options.get(IPDFRenderOption.PDF_FONT_SUBSTITUTION);
		if(fontSubstitution!=null && fontSubstitution instanceof Boolean)
		{
			if(!((Boolean)fontSubstitution).booleanValue())
			{
				context.setFontSubstitution(false);
			}
		}
		Object bidiProcessing = options.get(IPDFRenderOption.PDF_BIDI_PROCESSING);
		if(bidiProcessing!=null && bidiProcessing instanceof Boolean)
		{
			if(!((Boolean)bidiProcessing).booleanValue())
			{
				context.setBidiProcessing(false);
			}
		}
//		Object hyphenation = options.get(IPDFRenderOption.PDF_HYPHENATION);
//		if(hyphenation!=null && hyphenation instanceof Boolean)
//		{
//			if(!((Boolean)hyphenation).booleanValue())
//			{
//				context.setEnableHyphenation(false);
//			}
//		}
	}
	
	public void setOption(String name, Object value)
	{
		options.put(name, value);
	}
	
	public Object getOption(String name)
	{
		return options.get(name);
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public void setLayoutPageHint( IPageHint pageHint )
	{
		context.setLayoutPageHint( pageHint );
	}
	
	public long getPageCount( )
	{
		return pageCount;
	}

	public void close( ) throws BirtException
	{
		resolveTotalPage( emitter );
	}

	public void setTotalPageCount( long totalPage )
	{
		this.totalPage = totalPage;
		
	}
}