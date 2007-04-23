
package org.eclipse.birt.report.engine.executor.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.birt.core.format.DateFormatter;
import org.eclipse.birt.core.format.NumberFormatter;
import org.eclipse.birt.core.format.StringFormatter;
import org.eclipse.birt.core.template.TextTemplate;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.util.FileUtil;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.elements.structures.EmbeddedImage;

public class TemplateExecutor implements TextTemplate.Visitor
{

	protected StringBuffer buffer;
	protected HashMap values;
	protected ExecutionContext context;
	protected File imageFolder;
	protected HashMap imageCaches = new HashMap( );

	public TemplateExecutor( ExecutionContext context )
	{
		this.context = context;
		String tmpDir = null;
		if ( context != null )
		{
			IReportEngine engine = context.getEngine( );
			if ( engine != null )
			{
				EngineConfig config = engine.getConfig( );
				if ( config != null )
				{
					tmpDir = config.getTempDir( );
				}
			}
		}
		if ( tmpDir == null )
		{
			tmpDir = System.getProperty( "java.io.tmpdir" );
		}
		if ( tmpDir == null )
		{
			tmpDir = ".";
		}
		imageFolder = new File( tmpDir );
	}

	public String execute( TextTemplate template, HashMap values )
	{
		this.buffer = new StringBuffer( );
		this.values = values;

		if ( template == null )
		{
			return "";
		}

		ArrayList nodes = template.getNodes( );
		Iterator iter = nodes.iterator( );
		while ( iter.hasNext( ) )
		{
			TextTemplate.Node node = (TextTemplate.Node) iter.next( );
			node.accept( this, null );
		}

		return buffer.toString( );
	}

	public Object visitNode( TextTemplate.Node node, Object value )
	{
		return value;
	}

	public Object visitText( TextTemplate.TextNode node, Object value )
	{
		buffer.append( node.getContent( ) );
		return value;
	}

	public Object visitValue( TextTemplate.ValueNode node, Object value )
	{
		String text = "";
		String format = node.getFormat( );
		Object result = null;
		if ( values != null )
		{
			result = values.get( node.getValue( ) );
		}

		if ( "html".equalsIgnoreCase( format ) )
		{
			if ( result != null )
			{
				text = result.toString( );
			}
		}
		else
		{
			if ( result != null )
			{
				if ( result instanceof Number )
				{
					NumberFormatter fmt = context.getNumberFormatter( format );
					text = fmt.format( (Number) result );
				}
				else if ( result instanceof String )
				{
					StringFormatter fmt = context.getStringFormatter( format );
					text = fmt.format( (String) result );

				}
				else if ( result instanceof Date )
				{
					DateFormatter fmt = context.getDateFormatter( format );
					text = fmt.format( (Date) result );
				}
				else
				{
					text = result.toString( );
				}
			}
			text = encodeHtmlText( text );
		}
		buffer.append( text );
		return value;
	}

	protected String encodeHtmlText( String text )
	{
		return text.replaceAll( "<", "&lt;" );
	}

	public Object visitImage( TextTemplate.ImageNode node, Object value )
	{
		String imageName = null;
		String imageExt = null;
		Object imageContent = null;
		if ( TextTemplate.ImageNode.IMAGE_TYPE_EXPR == node.getType( ) )
		{
			imageContent = values.get( node.getExpr( ) );
		}
		else
		{
			imageName = node.getImageName( );
			if ( context != null )
			{
				ReportDesignHandle report = context.getDesign( );
				if ( report != null )
				{
					EmbeddedImage image = report.findImage( imageName );
					if ( image != null )
					{
						imageContent = image.getData( report.getModule( ) );
						imageExt = FileUtil.getExtFromFileName( imageName );
					}
				}
			}
		}
		if ( imageContent instanceof byte[] )
		{
			String src = saveToFile( imageName, imageExt, (byte[]) imageContent );
			if ( src != null )
			{
				buffer.append( "<img src=\"" );
				buffer.append( src );
				buffer.append( "\" " );
				Iterator iter = node.getAttributes( ).entrySet( ).iterator( );
				while ( iter.hasNext( ) )
				{
					Map.Entry entry = (Map.Entry) iter.next( );

					Object attrName = entry.getKey( );
					Object attrValue = entry.getValue( );
					if ( attrName != null && attrValue != null )
					{
						buffer.append( attrName.toString( ) );
						buffer.append( "=\"" );
						buffer.append( attrValue.toString( ) );
						buffer.append( "\" " );
					}
				}
				buffer.append( ">" );
			}
		}
		return value;
	}

	protected String saveToFile( String name, String ext, byte[] content )
	{
		if ( name != null )
		{
			String file = (String) imageCaches.get( name );
			if ( file != null )
			{
				return file;
			}
		}

		try
		{
			File imageFile = File.createTempFile( "img", ext, imageFolder );
			OutputStream out = new FileOutputStream( imageFile );
			out.write( content );
			out.close( );
			String fileName = imageFile.toURL( ).toExternalForm( );
			imageCaches.put( name, fileName );
			return fileName;
		}
		catch ( IOException ex )
		{
			ex.printStackTrace( );
		}
		return null;
	}
}
