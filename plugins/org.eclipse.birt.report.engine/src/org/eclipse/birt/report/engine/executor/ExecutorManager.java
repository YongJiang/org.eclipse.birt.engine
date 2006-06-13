/*
 * ExecutorManager.java Created on 2004-11-4
 *
 * Copyright (c) 2004 Actuate Corp.
 * 701 Gateway Blvd, South San Francisco, CA 94080, U.S.A.
 * All rights reserved.
 *
 * This software is confidential and proprietary information of 
 * Actuate Corp. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered
 * into with Actuate.
 */

package org.eclipse.birt.report.engine.executor;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.eclipse.birt.report.engine.emitter.IContentEmitter;
import org.eclipse.birt.report.engine.ir.AutoTextItemDesign;
import org.eclipse.birt.report.engine.ir.CellDesign;
import org.eclipse.birt.report.engine.ir.DataItemDesign;
import org.eclipse.birt.report.engine.ir.DefaultReportItemVisitorImpl;
import org.eclipse.birt.report.engine.ir.ExtendedItemDesign;
import org.eclipse.birt.report.engine.ir.FreeFormItemDesign;
import org.eclipse.birt.report.engine.ir.GridItemDesign;
import org.eclipse.birt.report.engine.ir.ImageItemDesign;
import org.eclipse.birt.report.engine.ir.LabelItemDesign;
import org.eclipse.birt.report.engine.ir.ListBandDesign;
import org.eclipse.birt.report.engine.ir.ListGroupDesign;
import org.eclipse.birt.report.engine.ir.ListItemDesign;
import org.eclipse.birt.report.engine.ir.MultiLineItemDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.ir.RowDesign;
import org.eclipse.birt.report.engine.ir.TableBandDesign;
import org.eclipse.birt.report.engine.ir.TableGroupDesign;
import org.eclipse.birt.report.engine.ir.TableItemDesign;
import org.eclipse.birt.report.engine.ir.TemplateDesign;
import org.eclipse.birt.report.engine.ir.TextItemDesign;

/**
 * 
 * report item executor manager
 * 
 * @author liugang
 * @version $Revision: 1.10 $ $Date: 2006/04/27 09:52:26 $
 */
public class ExecutorManager
{

	/**
	 * item executor type
	 */
	public static final int GRIDITEM = 0;
	public static final int IMAGEITEM = 1;
	public static final int LABELITEM = 2;
	public static final int LISTITEM = 3;
	public static final int TABLEITEM = 4;
	public static final int MULTILINEITEM = 5;
	public static final int TEXTITEM = 6;
	public static final int DATAITEM = 7;
	public static final int EXTENDEDITEM = 8;
	public static final int TEMPLATEITEM = 9;
	public static final int AUTOTEXTITEM = 10;
	public static final int LISTBANDITEM = 11;
	public static final int TABLEBANDITEM = 12;
	public static final int ROWITEM = 13;
	public static final int CELLITEM = 14;
	public static final int LISTGROUPITEM = 15;
	public static final int TABLEGROUPITEM = 16;

	/**
	 * the number of suppported executor
	 */
	public static final int NUMBER = 17;

	protected static Logger log = Logger.getLogger( ExecutorManager.class
			.getName( ) );

	/**
	 * execution context
	 */
	protected ExecutionContext context;
	
	/**
	 * the created content should pass out through this emitter
	 */
	protected IContentEmitter emitter;

	/**
	 * factory used to create the report executor
	 */
	protected ExecutorFactory executorFactory;
	/**
	 * array of free list
	 */
	protected LinkedList[] freeList = new LinkedList[NUMBER];

	/**
	 * constructor
	 * 
	 * @param context
	 * @param visitor
	 */
	public ExecutorManager( ExecutionContext context, IContentEmitter emitter )
	{
		this.context = context;
		this.emitter = emitter;
		for ( int i = 0; i < NUMBER; i++ )
		{
			freeList[i] = new LinkedList( );
		}
		executorFactory = new ExecutorFactory();
	}

	/**
	 * get item executor
	 * 
	 * @param type
	 *            the executor type
	 * @return item executor
	 */
	protected ReportItemExecutor getItemExecutor( int type )
	{
		assert ( type >= 0 ) && ( type < NUMBER );
		if ( !freeList[type].isEmpty( ) )
		{
			// the free list is non-empty
			return (ReportItemExecutor) freeList[type].removeFirst( );
		}
		switch ( type )
		{
			case GRIDITEM :
				return new GridItemExecutor( this );
			case IMAGEITEM :
				return new ImageItemExecutor( this );
			case LABELITEM :
				return new LabelItemExecutor( this );
			case LISTITEM :
				return new ListItemExecutor( this );
			case TABLEITEM :
				return new TableItemExecutor( this );
			case MULTILINEITEM :
				return new MultiLineItemExecutor( this );
			case TEXTITEM :
				return new TextItemExecutor( this );
			case DATAITEM :
				return new DataItemExecutor( this );
			case EXTENDEDITEM :
				return new ExtendedItemExecutor( this );
			case TEMPLATEITEM :
				return new TemplateExecutor( this );
			case AUTOTEXTITEM :
				return new AutoTextItemExecutor( this );
			case LISTBANDITEM :
				return new ListBandExecutor( this );
			case TABLEBANDITEM :
				return new TableBandExecutor( this );
			case ROWITEM :
				return new RowExecutor( this );
			case CELLITEM :
				return new CellExecutor( this );
			case LISTGROUPITEM:
				return new ListGroupExecutor( this );
			case TABLEGROUPITEM:
				return new TableGroupExecutor( this );
			default :
				throw new UnsupportedOperationException(
						"unsupported executor!" ); //$NON-NLS-1$
		}
	}

	public ReportItemExecutor createExecutor(ReportItemExecutor parent, ReportItemDesign design)
	{
		ReportItemExecutor executor = executorFactory.createExecutor( design );
		if (executor != null)
		{
			executor.setParent( parent );
			executor.setDesign( design );
		}
		return executor;
	}

	/**
	 * release item executor
	 * 
	 * @param type
	 *            the executor type
	 * @param itemExecutor
	 *            the item executor
	 */
	public void releaseExecutor( int type, ReportItemExecutor itemExecutor )
	{
		assert ( type >= 0 ) && ( type < NUMBER );
		itemExecutor.reset( );
		freeList[type].add( itemExecutor );
	}
	
	class ExecutorFactory extends DefaultReportItemVisitorImpl
	{
		public ReportItemExecutor createExecutor(ReportItemDesign design)
		{
			return (ReportItemExecutor)design.accept( this, null );
		}
		public Object visitAutoTextItem( AutoTextItemDesign autoText, Object value )
		{
			return getItemExecutor(AUTOTEXTITEM);
		}

		public Object visitCell( CellDesign cell, Object value )
		{
			return getItemExecutor(CELLITEM);
		}

		public Object visitDataItem( DataItemDesign data, Object value )
		{
			return getItemExecutor(DATAITEM);
		}

		public Object visitExtendedItem( ExtendedItemDesign item, Object value )
		{
			return getItemExecutor(EXTENDEDITEM);
		}

		public Object visitFreeFormItem( FreeFormItemDesign container, Object value )
		{
			return null;
		}

		public Object visitGridItem( GridItemDesign grid, Object value )
		{
			return getItemExecutor(GRIDITEM);
		}

		public Object visitImageItem( ImageItemDesign image, Object value )
		{
			return getItemExecutor(IMAGEITEM);
		}

		public Object visitLabelItem( LabelItemDesign label, Object value )
		{
			return getItemExecutor(LABELITEM);
		}

		public Object visitListBand( ListBandDesign band, Object value )
		{
			return getItemExecutor(LISTBANDITEM);
		}

		public Object visitListItem( ListItemDesign list, Object value )
		{
			return getItemExecutor(LISTITEM);
		}

		public Object visitMultiLineItem( MultiLineItemDesign multiLine, Object value )
		{
			return getItemExecutor(MULTILINEITEM);
		}

		public Object visitRow( RowDesign row, Object value )
		{
			return getItemExecutor(ROWITEM);
		}

		public Object visitTableBand( TableBandDesign band, Object value )
		{
			return getItemExecutor(TABLEBANDITEM);
		}

		public Object visitTableItem( TableItemDesign table, Object value )
		{
			return getItemExecutor(TABLEITEM);
		}

		public Object visitTemplate( TemplateDesign template, Object value )
		{
			return getItemExecutor(TEMPLATEITEM);
		}

		public Object visitTextItem( TextItemDesign text, Object value )
		{
			return getItemExecutor( TEXTITEM );
		}

		public Object visitListGroup( ListGroupDesign group, Object value )
		{
			return getItemExecutor( LISTGROUPITEM );
		}

		public Object visitTableGroup( TableGroupDesign group, Object value )
		{
			return getItemExecutor( TABLEGROUPITEM );
		}
		
	}
	
	

}