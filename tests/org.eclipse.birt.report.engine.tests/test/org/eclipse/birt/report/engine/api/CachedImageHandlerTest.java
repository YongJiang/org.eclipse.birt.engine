
package org.eclipse.birt.report.engine.api;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.birt.report.engine.EngineCase;
import org.eclipse.birt.report.engine.api.script.IReportContext;

/**
 * unit test used to test if the cached image handle is called.
 */
public class CachedImageHandlerTest extends EngineCase
{

	static final String DESIGN_RESOURCE = "org/eclipse/birt/report/engine/api/cached-image-handler.rptdesign";
	static final String REPORT_DESIGN = "./utest/test.rptdesign";
	static final String REPORT_DOCUMENT = "./utest/test.rptdocument";
	static final String TEST_FOLDER = "./utest/";

	public void setUp( )
	{
		removeFile( TEST_FOLDER );
		copyResource( DESIGN_RESOURCE, REPORT_DESIGN );
	}

	public void tearDown( )
	{
		removeFile( TEST_FOLDER );
	}

	public void testRender( ) throws Exception
	{
		CachedImageHandler imageHandler = new CachedImageHandler( );

		EngineConfig config = new EngineConfig( );
		HTMLEmitterConfig emitterConfig = new HTMLEmitterConfig( );
		emitterConfig.setImageHandler( imageHandler );
		config.setEmitterConfiguration( "html", emitterConfig );
		IReportEngine engine = new ReportEngine( config );

		// first we need create the report document
		// open the report runnable to execute.
		IReportRunnable report = engine.openReportDesign( REPORT_DESIGN );
		IRunTask task = engine.createRunTask( report );
		task.run( REPORT_DOCUMENT );
		task.close( );

		IReportDocument document = engine.openReportDocument( REPORT_DOCUMENT );

		// then we need render the report, this time the image is cached.
		IRenderTask render = engine.createRenderTask( document );
		HTMLRenderOption options = new HTMLRenderOption( );
		options.setOutputFormat( "html" );
		options.setOutputStream( new ByteArrayOutputStream( ) );
		render.setRenderOption( options );
		render.render( );
		render.close( );

		assertEquals( 1, imageHandler.customImageCount );
		// render the report again, the cached image should be return.
		render = engine.createRenderTask( document );
		render.setRenderOption( options );
		render.render( );
		render.close( );

		assertEquals( 1, imageHandler.cachedImageCount );
		assertEquals( 1, imageHandler.customImageCount );
		assertEquals( 1, imageHandler.fileImageCount );
		document.close( );

		engine.shutdown( );
	}

	class CachedImageHandler extends HTMLImageHandler
	{

		int cachedImageCount = 0;
		int customImageCount = 0;
		int fileImageCount = 0;

		HashMap map = new HashMap( );

		public CachedImage getCachedImage( String id, int sourceType,
				IReportContext context )
		{
			String url = (String) map.get( id );
			if ( url != null )
			{
				cachedImageCount++;
				return new CachedImage( id, "CACHED_IMAGE:" + url );
			}
			return null;

		}

		public String onCustomImage( IImage image, IReportContext context )
		{
			customImageCount++;
			String date = new Date( ).toString( );
			map.put( image.getID( ), date );
			return "CACHED_IMAGE:" + date;
		}

		public String onFileImage( IImage image, IReportContext context )
		{
			fileImageCount++;
			return image.getID( );
		}
	}

}
