
package org.eclipse.birt.report.engine.executor;

import org.eclipse.birt.report.engine.api.DataID;
import org.eclipse.birt.report.engine.data.IResultSet;
import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.ir.IReportItemVisitor;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;

abstract public class QueryItemExecutor extends StyledItemExecutor
{

	/**
	 * result set used by this item.
	 */
	protected IResultSet rset;
	protected boolean rsetEmpty;

	protected QueryItemExecutor( ExecutionContext context,
			IReportItemVisitor visitor )
	{
		super( context, visitor );
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
	protected void closeResultSet( )
	{
		if ( rset != null )
		{
			rset.close( );
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
	protected void openResultSet( ReportItemDesign item )
	{
		rset = null;
		if ( item.getQuery( ) != null )
		{
			rset = context.getDataEngine( ).execute( item.getQuery( ) );
			if ( rset != null )
			{
				rsetEmpty = !rset.next( );
			}

		}
	}

	protected void accessQuery( ReportItemDesign design, IContentEmitter emitter )
	{
	}

	public DataID getDataID( )
	{
		IResultSet curRset = rset;
		if (curRset == null)
		{
			curRset = context.getDataEngine().getResultSet();
		}
		if ( curRset != null )
		{
			return new DataID( curRset.getID( ), curRset.getCurrentPosition( ) );
		}
		return null;
	}

	public void reset( )
	{
		rset = null;
		super.reset( );
	}
}
