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

package org.eclipse.birt.report.engine.emitter.postscript.device;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.birt.report.engine.emitter.postscript.PostscriptWriter;
import org.eclipse.birt.report.engine.layout.TextStyle;
import org.eclipse.birt.report.engine.layout.emitter.AbstractPage;

public class PostscriptPage extends AbstractPage
{
	private PostscriptWriter writer;
	private boolean isDisposed;

	public PostscriptPage( int pageWidth, int pageHeight, Color color,
			PostscriptWriter writer )
	{
		super( pageWidth, pageHeight );
		writer.startPage( this.pageWidth, this.pageHeight );
		writer.fillPage( color );
		this.writer = writer;
		this.isDisposed = false;
	}

	public void saveState( )
	{
		writer.clipSave( );
	}

	public void restoreState( )
	{
		writer.clipRestore( );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#dispose()
	 */
	public void dispose( )
	{
		if ( !isDisposed )
		{
			writer.endPage( );
			isDisposed = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#drawText(java.lang.String,
	 *      float, float,
	 *      org.eclipse.birt.report.engine.layout.pdf.font.FontInfo, float,
	 *      float, java.awt.Color, boolean, boolean, boolean)
	 */
	protected void drawText( String text, float textX, float textY, float baseline,
			float width, float height, TextStyle fontStyle )
	{
		writer.drawString( text, textX, textY + baseline, fontStyle.getFontInfo( ),
				convertToPoint( fontStyle.getLetterSpacing( ) ),
				convertToPoint( fontStyle.getWordSpacing( ) ), fontStyle
						.getColor( ), fontStyle.isLinethrough( ), fontStyle
						.isOverline( ), fontStyle.isUnderline( ), fontStyle
						.getAlign( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#drawImage(java.io.InputStream,
	 *      float, float, float, float)
	 */
	protected void drawImage( String imageId, InputStream input, float imageX, float imageY,
			float height, float width, String helpText ) throws Exception
	{
		writer.drawImage( imageId, input, imageX, imageY, width, height );
	}

	protected void drawImage( String imageId, byte[] imageData,
			String extension, float imageX, float imageY, float height,
			float width, String helpText ) throws Exception
	{
		InputStream input = new ByteArrayInputStream( imageData );
		drawImage( imageId, input, imageX, imageY, height, width, helpText );
	}

	protected void drawImage( String uri, String extension, float imageX,
			float imageY, float height, float width, String helpText )
			throws Exception
	{
		if ( uri == null )
		{
			return;
		}
		drawImage( uri, new URL( uri ).openStream( ), imageX, imageY, height,
				width, helpText );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#drawLine(float,
	 *      float, float, float, float, java.awt.Color, java.lang.String)
	 */
	protected void drawLine( float startX, float startY, float endX,
			float endY, float width, Color color, String lineStyle )
	{
		writer.drawLine( startX, startY, endX, endY, width, color, lineStyle );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#drawBackgroundColor(java.awt.Color,
	 *      float, float, float, float)
	 */
	protected void drawBackgroundColor( Color color, float x, float y,
			float width, float height )
	{
		if ( color != null )
		{
			writer.fillRect( x, y, width, height, color );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitter.postscript.page.IPageDevice#drawBackgroundImage(float,
	 *      float, float, float, java.lang.String, java.lang.String, float,
	 *      float)
	 */
	protected void drawBackgroundImage( float x, float y, float width,
			float height, String repeat, String imageUrl, float absPosX,
			float absPosY ) throws IOException
	{
		writer.drawBackgroundImage( imageUrl, x, y, width, height, absPosX,
				absPosY, repeat );
	}

	protected void clip( float startX, float startY, float width, float height )
	{
		writer.clipRect( startX, startY, width, height );
	}
}