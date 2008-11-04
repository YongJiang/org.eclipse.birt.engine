/*******************************************************************************
 * Copyright (c) 2004,2007,2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.IBaseQueryResults;
import org.eclipse.birt.data.engine.api.IConditionalExpression;
import org.eclipse.birt.data.engine.api.IDataQueryDefinition;
import org.eclipse.birt.data.engine.api.IQueryDefinition;
import org.eclipse.birt.report.engine.api.DataID;
import org.eclipse.birt.report.engine.api.DataSetID;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.InstanceID;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IGroupContent;
import org.eclipse.birt.report.engine.content.IHyperlinkAction;
import org.eclipse.birt.report.engine.content.IReportContent;
import org.eclipse.birt.report.engine.content.impl.Column;
import org.eclipse.birt.report.engine.extension.IBaseResultSet;
import org.eclipse.birt.report.engine.extension.ICubeResultSet;
import org.eclipse.birt.report.engine.extension.IExecutorContext;
import org.eclipse.birt.report.engine.extension.IQueryResultSet;
import org.eclipse.birt.report.engine.extension.IReportItemExecutor;
import org.eclipse.birt.report.engine.i18n.MessageConstants;
import org.eclipse.birt.report.engine.ir.ActionDesign;
import org.eclipse.birt.report.engine.ir.ColumnDesign;
import org.eclipse.birt.report.engine.ir.DrillThroughActionDesign;
import org.eclipse.birt.report.engine.ir.Report;
import org.eclipse.birt.report.engine.ir.ReportElementDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.ir.VisibilityDesign;
import org.eclipse.birt.report.engine.ir.VisibilityRuleDesign;
import org.eclipse.birt.report.engine.script.internal.OnCreateScriptVisitor;
import org.eclipse.birt.report.engine.toc.TOCBuilder;
import org.eclipse.birt.report.engine.toc.TOCEntry;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.ReportElementHandle;

/**
 * Abstract class, Represents a report item executor. Report item executor
 * execute a report item design, generate a report item instance, and pass the
 * instance to <code>emitter</code>.
 * <p>
 * According to the report item design and current context information, executor
 * calculate expression in report item design, get data instance from data
 * source, and fill it into the report item instance, and set property for the
 * report item instance.
 * <p>
 * Reset the state of report item executor by calling <code>reset()</code>
 * 
 */
//TODO: can we reuse the content object instead of create different content object for the same report design element?
public abstract class ReportItemExecutor implements IReportItemExecutor
{
	/**
	 * executor manager used to create this executor.
	 */
	protected ExecutorManager manager;
	
	/**
	 * the type of the executor
	 */
	protected int type;

	/**
	 * the report content
	 */
	protected IReportContent report;

	/**
	 * the executor context
	 */
	protected ExecutionContext context;


	/**
	 * the executed report design
	 */
	protected ReportItemDesign design;
	
	/**
	 * the create report content
	 */
	protected IContent content;
	
	/**
	 * rset used to execute the item
	 */
	protected IQueryResultSet rset;
	
	/**
	 * toc created by this report item
	 */
	protected TOCEntry tocEntry;
	
	/**
	 * 
	 */
	protected OnCreateScriptVisitor onCreateVisitor;

	/**
	 * IExecutorContext
	 */
	protected IExecutorContext executorContext;

	/**
	 * model handle
	 */
	protected Object handle;

	/**
	 * parent executor
	 */
	protected ReportItemExecutor parent;
	
	/**
	 * the sequence id of the child executor
	 */
	protected long uniqueId;

	/**
	 * the instance id of the generated content.
	 */
	protected InstanceID instanceId;
	
	/**
	 * the current result set before execute this report item.
	 */
	protected IBaseResultSet[] parentRsets;

	/**
	 * construct a report item executor by giving execution context and report
	 * executor visitor
	 * 
	 * @param context
	 *            the executor context
	 * @param visitor
	 *            the report executor visitor
	 */
	protected ReportItemExecutor( ExecutorManager manager, int type )
	{
		this.manager = manager;
		this.type = type;
		this.context = manager.context;
		this.report = context.getReportContent( );
		this.onCreateVisitor = new OnCreateScriptVisitor( context );
		this.executorContext = null;
		this.parent = null;
		this.handle = null;
		this.design = null;
		this.content = null;
		this.rset = null;
		this.tocEntry = null;
		this.uniqueId = 0;
		this.instanceId = null;
		this.parentRsets = null;
	}
	
	public void setContext( IExecutorContext context )
	{
		this.executorContext = context;
	}

	public void setModelObject( Object handle )
	{
		this.handle = handle;
		if ( handle instanceof ReportItemDesign )
		{
			this.design = (ReportItemDesign) handle;
		}
		if ( handle instanceof ReportElementHandle )
		{
			ReportElementHandle element = (ReportElementHandle) handle;
			Report report = context.getReport( );
			this.design = report.findDesign( element );
		}
	}

	public void setParent( IReportItemExecutor parent )
	{
		assert parent instanceof ReportItemExecutor;
		this.parent = (ReportItemExecutor) parent;
	}

	public IExecutorContext getContext( )
	{
		return manager.getExecutorContext( );
	}

	public Object getModelObject( )
	{
		return handle;
	}

	public IReportItemExecutor getParent( )
	{
		return parent;
	}

	/**
	 * does the executor has child executor
	 * 
	 * @return
	 */
	public boolean hasNextChild( )
	{
		return false;
	}

	public IReportItemExecutor getNextChild( )
	{
		return null;
	}
	
	/**
	 * reset the state of the report item executor. This operation will reset
	 * all property of this object
	 * 
	 */
	public void close( )
	{
		this.executorContext = null;
		this.parent = null;
		this.handle = null;
		this.content = null;
		this.rset = null;
		this.tocEntry = null;
		this.uniqueId = 0;
		this.instanceId = null;
		this.design = null;
		this.parentRsets = null;

		manager.releaseExecutor( type, this );
	}

	IContent getParentContent()
	{
		if (parent != null)
		{
			return parent.getContent( );
		}
		return null;
	}
	ReportItemDesign getDesign()
	{
		return design;
	}
	
	public IContent getContent()
	{
		return content;
	}
	
	void setContent(IContent content)
	{
		context.setContent( content );
		this.content = content;
	}
	
	Object evaluate( String expr ) throws BirtException
	{
		return context.evaluate( expr );
	}	
	
	Object evaluate( IConditionalExpression expr ) throws BirtException
	{
		return context.evaluateCondExpr( expr );
	}

	/**
	 * Calculate the bookmark value which is set to
	 * <code>ReportItemContent</code> if the bookmark is not null
	 * 
	 * @param item
	 *            the ReportItemContent object
	 */
	protected void processBookmark( ReportItemDesign item, IContent itemContent )
	{
		String bookmarkExpr = item.getBookmark( );
		if ( bookmarkExpr != null )
		{
			try
			{
				Object tmp = evaluate( bookmarkExpr );
				if ( tmp != null && !tmp.equals( "" ) )
				{
					String bookmark = tmp.toString( );
					itemContent.setBookmark( bookmark );
					// we need also set the bookmark to the result set
					if ( rset != null )
					{
						IBaseQueryResults resultSet = rset.getQueryResults( );
						if ( resultSet != null )
						{
							IDataQueryDefinition query = item.getQuery( );
							if ( query instanceof IQueryDefinition )
							{
								resultSet.setName( bookmark );
							}
						}
					}
				}
			}
			catch ( BirtException ex )
			{
				context.addException( ex );
			}
		}
		String toc = item.getTOC( );
		if ( toc != null )
		{
			try
			{
				Object tmp = evaluate( toc );
				if ( tmp == null )
				{
					tmp = "";
				}
				itemContent.setTOC( tmp );
			}
			catch ( BirtException ex )
			{
				context.addException( ex );
			}
		}
	}

	/**
	 * Calculate the action value which is set to <code>ReportItemContent</code>
	 * if the action is not null.
	 * 
	 * @param action
	 *            the action design object
	 * @param itemContent
	 *            create report item content object
	 */
	protected void processAction( ReportItemDesign item, IContent itemContent )
	{
		assert itemContent != null;

		ActionDesign action = item.getAction( );
		if ( action != null )
		{

			switch ( action.getActionType( ) )
			{
				case ActionDesign.ACTION_HYPERLINK :
					assert action.getHyperlink( ) != null;
					try
					{
						Object value = evaluate( action.getHyperlink( ) );
						if ( value != null )
						{
							IHyperlinkAction obj = report.createActionContent( );
							obj.setHyperlink( value.toString( ), action
									.getTargetWindow( ) );
							obj.setTooltip( action.getTooltip( ) );
							itemContent.setHyperlinkAction( obj );
						}
					}
					catch ( BirtException ex )
					{
						context.addException( ex );
					}
					break;
				case ActionDesign.ACTION_BOOKMARK :
					assert action.getBookmark( ) != null;
					try
					{
						Object value = evaluate( action.getBookmark( ) );
						if ( value != null && !value.equals( "" ) )
						{
							IHyperlinkAction obj = report.createActionContent( );
							obj.setBookmark( value.toString( ) );
							obj.setTooltip( action.getTooltip( ) );
							itemContent.setHyperlinkAction( obj );
						}
					}
					catch ( BirtException ex )
					{
						context.addException( ex );
					}
					break;
				case ActionDesign.ACTION_DRILLTHROUGH :
					assert action.getDrillThrough( ) != null;
					DrillThroughActionDesign drill = action.getDrillThrough( );
					String bookmark = null;
					String bookmarkExpr = drill.getBookmark( );
					if ( bookmarkExpr != null )
					{
						try
						{
							Object value = evaluate( drill.getBookmark( ) );
							if ( value != null )
							{
								bookmark = value.toString( );
							}
						}
						catch ( BirtException ex )
						{
							context.addException( ex );
						}
					}
					boolean isBookmark = drill.isBookmark( );
					Map paramsVal = new HashMap( );
					Map params = drill.getParameters( );
					if ( params != null )
					{
						Iterator paramsDesignIte = params.entrySet( )
								.iterator( );
						while ( paramsDesignIte.hasNext( ) )
						{
							Map.Entry entry = (Map.Entry) paramsDesignIte
									.next( );
							Object valueObj = entry.getValue( );
							if ( valueObj != null )
							{
								String valueExpr = valueObj.toString( );
								try
								{
									Object paramValue = evaluate( valueExpr );
									paramsVal.put( entry.getKey( ), paramValue );
								}
								catch ( BirtException ex )
								{
									context.addException( ex );
								}
							}
						}
					}

					String reportName = drill.getReportName( );
					/* we do not set absoluted path here. we now changed this in render time
					ReportDesignHandle design = context.getDesign( );
					if ( design != null )
					{
						URL reportURL = design.findResource( reportName,
								IResourceLocator.LIBRARY );
						if ( reportURL != null )
						{
							String reportFile = reportURL.getFile( );
							if ( reportFile != null )
							{
								reportName = reportFile;
							}
						}
					}
					*/
					String format = drill.getFormat( );
					// XXX Do not support Search criteria
					IHyperlinkAction obj = report.createActionContent( );
					obj.setDrillThrough( bookmark, isBookmark, reportName,
							paramsVal, null, action.getTargetWindow( ), format,
							action.getTargetFileType( ) );
					obj.setTooltip( action.getTooltip( ) );
					itemContent.setHyperlinkAction( obj );
					break;
				default :
					assert false;
			}

		}
	}

	/**
	 * Sets the visibility property for ReportItem.
	 * 
	 * @param design
	 *            The <code>ReportItemDesign</code> object.
	 * @param content
	 *            The <code>ReportItemContent</code> object.
	 */
	protected void processVisibility( ReportItemDesign design, IContent content )
	{
		VisibilityDesign visibility = design.getVisibility( );
		if ( visibility != null )
		{
			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < visibility.count( ); i++ )
			{
				VisibilityRuleDesign rule = visibility.getRule( i );
				String expr = rule.getExpression( );
				if ( expr != null )
				{
					try
					{
						Object result = evaluate( expr );
						if ( result == null || !( result instanceof Boolean ) )
						{
							throw new EngineException(
									MessageConstants.EXPRESSION_EVALUATION_ERROR, //$NON-NLS-1$
									rule.getExpression( ) );
						}
						boolean isHidden = ( (Boolean) result ).booleanValue( );
						// The report element appears by default and if the
						// result is not hidden, then ignore it.
						if ( isHidden )
						{
							// we should use rule as the string as
							buffer.append( rule.getFormat( ) );
							buffer.append( "," ); //$NON-NLS-1$
						}
					}
					catch ( BirtException ex )
					{
						context.addException( ex );
					}
				}
			}
			if ( buffer.length( ) != 0 )
			{
				buffer.setLength( buffer.length( ) - 1 );
				content.getStyle( ).setVisibleFormat( buffer.toString( ) );
			}
		}
	}

	/**
	 * Sets the visibility property for column.
	 */
	protected void processColumnVisibility( ColumnDesign design, Column column )
	{
		VisibilityDesign visibility = design.getVisibility( );
		if ( visibility != null )
		{
			StringBuffer buffer = new StringBuffer( );
			for ( int i = 0; i < visibility.count( ); i++ )
			{
				VisibilityRuleDesign rule = visibility.getRule( i );
				String expr = rule.getExpression( );
				if ( expr != null )
				{
					try
					{
						Object result = evaluate( expr );
						if ( result == null || !( result instanceof Boolean ) )
						{
							throw new EngineException(
									MessageConstants.EXPRESSION_EVALUATION_ERROR, //$NON-NLS-1$
									rule.getExpression( ) );
						}
						boolean isHidden = ( (Boolean) result ).booleanValue( );
						// The report element appears by default and if the
						// result is not hidden, then ignore it.
						if ( isHidden )
						{
							buffer.append( rule.getFormat( ) );
							buffer.append( "," ); //$NON-NLS-1$
						}
					}
					catch ( BirtException ex )
					{
						context.addException( ex );
					}
				}
			}
			if ( buffer.length( ) != 0 )
			{
				buffer.setLength( buffer.length( ) - 1 );
				column.setVisibleFormat( buffer.toString( ) );
			}
		}
	}

	protected DataID getDataID( )
	{
		if ( parent != null )
		{
			//if result set is not null, it is a normal executor.
			IQueryResultSet prset = parent.rset;
			if ( prset != null )
			{
				DataSetID dataSetID = prset.getID( );
				long position = prset.getRowIndex( );
				if ( dataSetID == null )
				{
					return null;
				}
				return new DataID( dataSetID, position );
			}
			// the result set is NULL or it may be a extended item executor, try
			// getQueryResults
			IBaseResultSet[] rsets = parent.getQueryResults( );
			if ( ( rsets != null ) && ( rsets.length > 0 ) &&
					( rsets[0] != null ) )
			{
				if ( rsets[0] instanceof IQueryResultSet )
				{
					IQueryResultSet rset = (IQueryResultSet) rsets[0];
					DataSetID dataSetID = rset.getID( );
					long position = rset.getRowIndex( );
					return new DataID( dataSetID, position );
				}
				if ( rsets[0] instanceof ICubeResultSet )
				{
					ICubeResultSet rset = (ICubeResultSet) rsets[0];
					DataSetID dataSetID = rset.getID( );
					String cellId = rset.getCellIndex( );
					return new DataID( dataSetID, cellId );
				}
			}
		}
		return null;
	}
	
	protected long generateUniqueID( )
	{
		if ( parent != null )
		{
			return parent.uniqueId++;
		}
		return manager.generateUniqueID( );
	}
	
	
	protected long getElementId( )
	{
		if ( design != null )
		{
			return design.getID( );
		}
		if ( handle != null && handle instanceof DesignElementHandle )
		{
			return ( (DesignElementHandle) handle ).getID( );
		}
		return -1;
	}
	
	protected InstanceID getInstanceID( )
	{
		if ( instanceId == null )
		{
			InstanceID pid = parent == null ? null : parent.getInstanceID( );
			long uid = generateUniqueID( );
			long id = getElementId( );
			DataID dataId = getDataID( );
			instanceId = new InstanceID( pid, uid, id, dataId );
		}
		return instanceId;
	}

	protected void initializeContent( ReportElementDesign design,
			IContent content )
	{
		IContent parent = getParentContent( );
		if ( parent != null )
		{
			content.setParent( parent );
		}
		InstanceID id = getInstanceID( );
		content.setInstanceID( id );
		content.setGenerateBy( design );
	}

	
	TOCEntry getParentTOCEntry()
	{
		if (parent instanceof ReportItemExecutor)
		{
			ReportItemExecutor parentExecutor = (ReportItemExecutor)parent;
			if (parentExecutor.tocEntry != null)
			{
				return parentExecutor.tocEntry;
			}
			return parentExecutor.getParentTOCEntry();
		}
		return null;
	}

	/**
	 * starts a TOC entry, mostly used for non-leaf TOC entry, which can not be
	 * closed until its children have been written.
	 * 
	 * @param content
	 *            report item content object
	 */
	protected void startTOCEntry( IContent content )
	{
		TOCBuilder tocBuilder = context.getTOCBuilder( );
		if ( tocBuilder != null )
		{
			if ( content != null )
			{
				Object tocValue = content.getTOC( );
				if ( tocValue != null )
				{
					TOCEntry parentTOCEntry = getParentTOCEntry( );
					String hiddenFormats = content.getStyle( )
							.getVisibleFormat( );
					long elementId = getElementId( );
					String bookmark = content.getBookmark( );
					tocEntry = tocBuilder.startEntry( parentTOCEntry, tocValue,
							bookmark, hiddenFormats, elementId );
					String tocId = tocEntry.getNodeId( );
					if ( bookmark == null )
					{
						content.setBookmark( tocId );
					}
					return;
				}
				else
				{
					String hiddenFormats = content.getStyle( )
							.getVisibleFormat( );
					if ( hiddenFormats != null )
					{
						TOCEntry parentTOCEntry = getParentTOCEntry( );
						tocEntry = tocBuilder.startDummyEntry( parentTOCEntry,
								hiddenFormats );
					}
				}
			}
		}
	}

	/**
	 * finishes a TOC entry, mostly used for non-leaf TOC entry, which can not
	 * be closed until its children have been written.
	 * 
	 * @param content
	 *            report item content object
	 */
	protected void finishTOCEntry( )
	{
		TOCBuilder tocBuilder = context.getTOCBuilder( );
		if ( tocBuilder != null )
		{
			if (tocEntry != null)
			{
				tocBuilder.closeEntry( tocEntry );
			}
		}
	}

	protected void startGroupTOCEntry( IGroupContent group )
	{
		TOCBuilder tocBuilder = context.getTOCBuilder( );
		if ( tocBuilder != null )
		{
			TOCEntry entry = getParentTOCEntry();
			String hiddenFormats = group.getStyle( ).getVisibleFormat( );
			long elementId = getElementId( );
			tocEntry = tocBuilder.startGroupEntry( entry, group.getTOC( ),
					group.getBookmark( ), hiddenFormats, elementId );
			String tocId = tocEntry.getNodeId( );
			if ( group.getBookmark( ) == null )
			{
				group.setBookmark( tocId );
			}
		}
	}

	protected void finishGroupTOCEntry( )
	{
		TOCBuilder tocBuilder = context.getTOCBuilder( );
		if ( tocBuilder != null )
		{
			tocBuilder.closeGroupEntry( tocEntry );
		}
	}
	
	void setResultSet(IQueryResultSet rset)
	{
		this.rset = rset;
	}
	
	IQueryResultSet getResultSet()
	{
		return rset;
	}
	
	public IBaseResultSet[] getQueryResults( )
	{
		if ( rset != null )
		{
			return new IBaseResultSet[]{rset};
		}
		return null;
	}
	
	protected IBaseResultSet getParentResultSet( )
	{
		if ( parentRsets == null )
		{
			if ( parent != null )
			{
				if ( parent.rset == null )
				{
					IBaseResultSet[] pRsets = parent.getQueryResults( );
					if ( pRsets == null
							|| ( pRsets.length > 0 && pRsets[0] == null ) )
					{
						IBaseResultSet prset_ = parent.getParentResultSet( );
						if ( prset_ != null )
						{
							parentRsets = new IBaseResultSet[]{prset_};
						}
					}
					else
					{
						parentRsets = new IBaseResultSet[]{pRsets[0]};
					}
				}
				else
				{
					parentRsets = new IBaseResultSet[]{parent.rset};
				}
			}
		}

		if ( parentRsets != null )
		{
			return parentRsets[0];
		}
		else
		{
			return null;
		}
	}
	
	protected void restoreResultSet()
	{
		context.setResultSet( getParentResultSet( ) );
	}
	
	protected void handleOnCreate( IContent content )
	{
		if ( content.getGenerateBy( ) != null )
		{
			onCreateVisitor.onCreate( content );
		}
	}

	protected Logger getLogger( )
	{
		return context.getLogger( );
	}
}