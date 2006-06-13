
package org.eclipse.birt.report.engine.internal.executor.dom;

import java.util.Iterator;

import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.executor.IReportItemExecutor;

public class DOMReportItemExecutor implements IReportItemExecutor
{

	IContent content;
	DOMReportItemExecutorManager manager;

	DOMReportItemExecutor( DOMReportItemExecutorManager manager )
	{
		this.manager = manager;
	}

	void setContent( IContent content )
	{
		this.content = content;
	}

	public void close( )
	{
		manager.releaseExecutor( this );
	}

	public IContent execute( )
	{
		childIterator = content.getChildren( ).iterator( );
		return content;
	}

	Iterator childIterator;

	public IReportItemExecutor getNextChild( )
	{
		if ( childIterator.hasNext( ) )
		{
			IContent child = (IContent) childIterator.next( );
			return manager.createExecutor( child );
		}
		return null;
	}

	public boolean hasNextChild( )
	{
		return childIterator.hasNext( );
	}

}
