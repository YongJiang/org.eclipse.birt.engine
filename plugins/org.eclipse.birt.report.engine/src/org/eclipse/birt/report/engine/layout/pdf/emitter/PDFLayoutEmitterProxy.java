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

import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.report.engine.api.IPDFRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.content.ICellContent;
import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IForeignContent;
import org.eclipse.birt.report.engine.content.IListBandContent;
import org.eclipse.birt.report.engine.content.IListGroupContent;
import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.content.IRowContent;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.content.ITableContent;
import org.eclipse.birt.report.engine.content.ITableGroupContent;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.emitter.IEmitterServices;
import org.eclipse.birt.report.engine.executor.IReportExecutor;

public class PDFLayoutEmitterProxy extends LayoutEmitterAdapter
		implements
			IContentEmitter
{

	private LayoutEmitterAdapter layoutEmitterImpl = null;
	private IContentEmitter outputEmitter = null;
	private IReportExecutor executor;
	private LayoutEngineContext context;

	public PDFLayoutEmitterProxy( IReportExecutor executor,
			IContentEmitter emitter, IRenderOption renderOptions,
			Locale locale, long totalPage )
	{
		this.executor = executor;
		this.outputEmitter = emitter;

		context = new LayoutEngineContext( );
		setupLayoutOptions( renderOptions );
		if ( renderOptions != null )
		{
			String format = renderOptions.getOutputFormat( );
			context.setFormat( format );
		}
		context.setLocale( locale );
		context.totalPage = totalPage;
		createLayoutEmitterImpl( context );
		context.setEmitter( layoutEmitterImpl );
	}

	public void initialize( IEmitterServices service )
	{
		layoutEmitterImpl.initialize( service );
	}

	private void createLayoutEmitterImpl( LayoutEngineContext context )
	{
		if ( context.autoPageBreak )
		{
			layoutEmitterImpl = new WrappedPDFLayoutEmitter( executor,
					outputEmitter, context );
		}
		else
		{
			layoutEmitterImpl = new PDFLayoutEmitter( executor, outputEmitter,
					context );
		}
	}

	public String getOutputFormat( )
	{
		return layoutEmitterImpl.getOutputFormat( );
	}

	protected void setupLayoutOptions( IRenderOption renderOptions )
	{
		Map options = null;
		if ( renderOptions != null )
		{
			options = renderOptions.getOptions( );
		}
		if ( options != null )
		{
			Object fitToPage = options.get( IPDFRenderOption.FIT_TO_PAGE );
			if ( fitToPage != null && fitToPage instanceof Boolean )
			{
				if ( ( (Boolean) fitToPage ).booleanValue( ) )
				{
					context.setFitToPage( true );
				}
			}
			Object pageBreakOnly = options
					.get( IPDFRenderOption.PAGEBREAK_PAGINATION_ONLY );
			if ( pageBreakOnly != null && pageBreakOnly instanceof Boolean )
			{
				if ( ( (Boolean) pageBreakOnly ).booleanValue( ) )
				{
					context.setPagebreakPaginationOnly( true );
				}
			}
			Object pageOverflow = options.get( IPDFRenderOption.PAGE_OVERFLOW );
			if ( pageOverflow != null && pageOverflow instanceof Integer )
			{
				int pageOverflowType = ( (Integer) pageOverflow ).intValue( );
				context.setPageOverflow( pageOverflowType );
				if ( pageOverflowType == IPDFRenderOption.OUTPUT_TO_MULTIPLE_PAGES )
				{
					context.setPagebreakPaginationOnly( false );
				}
				else
				{
					context.setPagebreakPaginationOnly( true );
				}
			}
			else
			{
				if ( context.fitToPage( ) )
				{
					context.setPageOverflow( IPDFRenderOption.FIT_TO_PAGE_SIZE );
					context.setPagebreakPaginationOnly( true );
				}
			}
			/*
			 * Object outputDisplayNone = options .get(
			 * IPDFRenderOption.OUTPUT_DISPLAY_NONE ); if ( outputDisplayNone
			 * instanceof Boolean ) { if ( ( (Boolean) outputDisplayNone
			 * ).booleanValue( ) ) { context.setOutputDisplayNone( true ); } }
			 */

			Object textWrapping = options
					.get( IPDFRenderOption.PDF_TEXT_WRAPPING );
			if ( textWrapping != null && textWrapping instanceof Boolean )
			{
				if ( !( (Boolean) textWrapping ).booleanValue( ) )
				{
					context.setTextWrapping( false );
				}
			}
			Object fontSubstitution = options
					.get( IPDFRenderOption.PDF_FONT_SUBSTITUTION );
			if ( fontSubstitution != null
					&& fontSubstitution instanceof Boolean )
			{
				if ( !( (Boolean) fontSubstitution ).booleanValue( ) )
				{
					context.setFontSubstitution( false );
				}
			}
			Object bidiProcessing = options
					.get( IPDFRenderOption.PDF_BIDI_PROCESSING );
			if ( bidiProcessing != null && bidiProcessing instanceof Boolean )
			{
				if ( !( (Boolean) bidiProcessing ).booleanValue( ) )
				{
					context.setBidiProcessing( false );
				}
			}
			/*
			 * bidi_hcg: Only disable Bidi processing when the rtl flag is null,
			 * i.e. Bidi support is disabled.
			 */
			// if ( options.get( IRenderOption.RTL_FLAG ) == null )
			// {
			// context.setBidiProcessing( false );
			// }
			Object hyhenation = options.get( IPDFRenderOption.PDF_HYPHENATION );
			if ( hyhenation != null && hyhenation instanceof Boolean )
			{
				if ( ( (Boolean) hyhenation ).booleanValue( ) )
				{
					context.setEnableHyphenation( true );
				}
			}

			Object dpi = options.get( IPDFRenderOption.DPI );
			if ( dpi != null && dpi instanceof Integer )
			{
				int screenDpi = ( (Integer) dpi ).intValue( );
				context.setDpi( screenDpi );
			}

			// Object rtlFlag = options.get( IRenderOption.RTL_FLAG );
			// if (rtlFlag != null && rtlFlag instanceof Boolean)
			// {
			// if (((Boolean)rtlFlag).booleanValue())
			// {
			// context.setRtl( true );
			// }
			// }
		}
	}

	public void start( IReportContent report )
	{
		layoutEmitterImpl.start( report );
	}

	public void end( IReportContent report )
	{
		layoutEmitterImpl.end( report );
	}
	
	public void startTable( ITableContent table )
	{
		layoutEmitterImpl.startTable( table );
	}

	public void endTable( ITableContent table )
	{
		layoutEmitterImpl.endTable( table );
	}

	public void startContainer( IContainerContent container )
	{
		layoutEmitterImpl.startContainer( container );
	}

	public void endContainer( IContainerContent container )
	{
		layoutEmitterImpl.endContainer( container );
	}

	public void startContent( IContent content )
	{
		layoutEmitterImpl.startContent( content );
	}

	public void endContent( IContent content )
	{
		layoutEmitterImpl.endContent( content );
	}

	public void startListBand( IListBandContent listBand )
	{
		layoutEmitterImpl.startListBand( listBand );
	}

	public void startListGroup( IListGroupContent listGroup )
	{
		layoutEmitterImpl.startListGroup( listGroup );
	}

	public void endListBand( IListBandContent listBand )
	{
		layoutEmitterImpl.endListBand( listBand );
	}
	
	public void outputPage( IPageContent page )
	{
		layoutEmitterImpl.outputPage( page );
	}

	public void startPage( IPageContent page )
	{
		layoutEmitterImpl.startPage( page );
	}

	public void endPage( IPageContent page )
	{
		layoutEmitterImpl.endPage( page );
	}

	public void startRow( IRowContent row )
	{
		layoutEmitterImpl.startRow( row );
	}

	public void endRow( IRowContent row )
	{
		layoutEmitterImpl.endRow( row );
	}

	public void startTableBand( ITableBandContent band )
	{
		layoutEmitterImpl.startTableBand( band );
	}

	public void startTableGroup( ITableGroupContent group )
	{
		layoutEmitterImpl.startTableGroup( group );
	}

	public void endTableBand( ITableBandContent band )
	{
		layoutEmitterImpl.endTableBand( band );
	}

	public void endTableGroup( ITableGroupContent group )
	{
		layoutEmitterImpl.endTableGroup( group );
	}

	public void startCell( ICellContent cell )
	{
		layoutEmitterImpl.startCell( cell );
	}

	public void endCell( ICellContent cell )
	{
		layoutEmitterImpl.endCell( cell );
	}

	public void startForeign( IForeignContent foreign )
	{
		layoutEmitterImpl.startForeign( foreign );
	}

}