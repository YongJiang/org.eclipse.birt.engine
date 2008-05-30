/*******************************************************************************
 * Copyright (c)2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;


public class SvgFile
{
	static boolean isSvg = false;
	
	public static boolean isSvg(String uri)
	{
		if ( uri != null && uri.endsWith( ".svg" ) )
		{
			isSvg = true;
		}
		else isSvg = false;
		return isSvg;
	}
	
	public static boolean isSvg(String mimeType,String uri,String extension)
	{
		isSvg = ( ( mimeType != null ) && mimeType.equalsIgnoreCase( "image/svg+xml" ) ) //$NON-NLS-1$
				|| ( ( uri != null ) && uri.toLowerCase( )
						.endsWith( ".svg" ) ) //$NON-NLS-1$
				|| ( ( extension != null ) && extension.toLowerCase( ).endsWith(".svg" ) ); //$NON-NLS-1$
	     return isSvg;
	}
	
	public static byte[] transSvgToArray( String uri ) throws IOException
	{
		InputStream in = null;
		in = new URL( uri ).openStream( );
		return transSvgToArray( in );
	}

	public static byte[] transSvgToArray( InputStream inputStream )
			throws IOException
	{
		JPEGTranscoder transcoder = new JPEGTranscoder( );
		// set the transcoding hints
		transcoder.addTranscodingHint( JPEGTranscoder.KEY_QUALITY, new Float(
				.8 ) );
		// create the transcoder input
		TranscoderInput input = new TranscoderInput( inputStream );
		// create the transcoder output
		ByteArrayOutputStream ostream = new ByteArrayOutputStream( );
		TranscoderOutput output = new TranscoderOutput( ostream );
		try
		{
			transcoder.transcode( input, output );
		}
		catch ( TranscoderException e )
		{
		}
		// flush the stream
		ostream.flush( );
		// use the output stream as Image input stream.
		return ostream.toByteArray( );
	}
}