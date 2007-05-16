
package org.eclipse.birt.report.engine.executor;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.IDataQueryDefinition;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ReportElementHandle;

abstract public class QueryItemExecutor extends StyledItemExecutor
{
	protected boolean rsetEmpty;

	protected QueryItemExecutor( ExecutorManager manager, int type )
	{
		super( manager, type );
	}
	
	/**
	 * close dataset if the dataset is not null:
	 * <p>
	 * <ul>
	 * <li>close the dataset.
	 * <li>exit current script scope.
	 * </ul>
	 * 
	 * @param ds
	 *            the dataset object, null is valid
	 */
	protected void closeQuery( )
	{
		if ( rset != null )
		{
			rset.close( );
			rset = null;
		}
	}

	/**
	 * register dataset of this item.
	 * <p>
	 * if dataset design of this item is not null, create a new
	 * <code>DataSet</code> object by the dataset design. open the dataset,
	 * move cursor to the first record , register the first row to script
	 * context, and return this <code>DataSet</code> object if dataset design
	 * is null, or open error, or empty resultset, return null.
	 * 
	 * @param item
	 *            the report item design
	 * @return the DataSet object if not null, else return null
	 */
	protected void executeQuery( )
	{
		rset = null;
		boolean useCache = false;
		IDataQueryDefinition query = design.getQuery( );
		if ( query == null )
		{
			DesignElementHandle elementHandle = design.getHandle( );
			if ( elementHandle instanceof ReportElementHandle )
			{
				IDataQueryDefinition[] queries = report.getDesign( )
						.getQueryByReportHandle(
								(ReportElementHandle) elementHandle );
				if ( queries != null && queries.length > 0 )
				{
					query = queries[0];
					useCache = true;
				}
			}
		}
		IBaseResultSet parentRset = getParentResultSet( );
		context.setResultSet( parentRset );
		if ( query != null )
		{
			try
			{
				rset = (IQueryResultSet) context.executeQuery( parentRset,
						query, useCache );
				context.setResultSet( rset );
				if ( rset != null )
				{
					rsetEmpty = !rset.next( );
					return;
				}
			}
			catch ( BirtException ex )
			{
				rsetEmpty = true;
				context.addException( ex );
			}
		}
	}

	protected void accessQuery( ReportItemDesign design, IContentEmitter emitter )
	{
	}

	public void close( )
	{
		rset = null;
		rsetEmpty = false;
		super.close( );
	}
}
