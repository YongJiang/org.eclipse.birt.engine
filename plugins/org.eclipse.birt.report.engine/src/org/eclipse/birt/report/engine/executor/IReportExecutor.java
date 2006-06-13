
package org.eclipse.birt.report.engine.executor;

import org.eclipse.birt.report.engine.content.IPageContent;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;

public interface IReportExecutor
{

	public IPageContent createPage(long pageNumber, MasterPageDesign pageDesign);
	
	public IReportContent execute( );

	/**
	 * close the executor, if the executor is closed, all sub executor will be
	 * termiante also.
	 */
	void close( );

	/**
	 * does the executor has child executor
	 * 
	 * @return
	 */
	boolean hasNextChild( );

	IReportItemExecutor getNextChild( );

}
