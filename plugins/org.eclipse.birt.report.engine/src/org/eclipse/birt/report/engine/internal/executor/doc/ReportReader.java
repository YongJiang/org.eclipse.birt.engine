
package org.eclipse.birt.report.engine.internal.executor.doc;

import java.io.IOException;
import java.util.logging.Level;

import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.executor.ExecutionContext;
import org.eclipse.birt.report.engine.executor.IReportItemExecutor;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;

public class ReportReader extends AbstractReportReader
{

	public ReportReader( ExecutionContext context )
	{
		super( context );
	}

	public IReportContent execute( )
	{
		try
		{
			openReaders( );
		}
		catch ( IOException ex )
		{
			logger.log( Level.SEVERE, "Fail to open the readers", ex );
			closeReaders( );
		}
		return reportContent;
	}

	public void close( )
	{
		closeReaders( );
	}

	protected long offset;

	public IReportItemExecutor getNextChild( )
	{
		if ( hasNextChild( ) )
		{
			ReportItemReader childReader = manager
					.createExecutor( null, offset );
			offset = childReader.findNextSibling( );
			return childReader;
		}
		return null;
	}

	public boolean hasNextChild( )
	{
		return offset != -1;
	}

	public IPageContent createPage( long pageNumber, MasterPageDesign pageDesign )
	{
		return null;
	}
}
