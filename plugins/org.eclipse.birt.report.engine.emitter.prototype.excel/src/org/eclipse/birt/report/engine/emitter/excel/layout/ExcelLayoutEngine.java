
package org.eclipse.birt.report.engine.emitter.excel.layout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.birt.report.engine.content.IDataContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.emitter.excel.BookmarkDef;
import org.eclipse.birt.report.engine.emitter.excel.Data;
import org.eclipse.birt.report.engine.emitter.excel.DataCache;
import org.eclipse.birt.report.engine.emitter.excel.ExcelEmitter;
import org.eclipse.birt.report.engine.emitter.excel.ExcelUtil;
import org.eclipse.birt.report.engine.emitter.excel.HyperlinkDef;
import org.eclipse.birt.report.engine.emitter.excel.Span;
import org.eclipse.birt.report.engine.emitter.excel.StyleBuilder;
import org.eclipse.birt.report.engine.emitter.excel.StyleConstant;
import org.eclipse.birt.report.engine.emitter.excel.StyleEngine;
import org.eclipse.birt.report.engine.emitter.excel.StyleEntry;

public class ExcelLayoutEngine
{
	public final static String EMPTY = "";
	
	public final static int MAX_ROW = 65535;
	
	public static int MAX_COLUMN = 255;

	private DataCache cache;

	private AxisProcessor axis;

	private StyleEngine engine;	
	
	private ExcelEmitter emitter;

	private Stack<XlsContainer> containers = new Stack<XlsContainer>( );

	private Stack<XlsTable> tables = new Stack<XlsTable>( );

	private Hashtable<String, String> links = new Hashtable<String, String>( );
	
	ExcelContext context = null;

	public ExcelLayoutEngine( PageDef page, ExcelContext context,
			ExcelEmitter emitter )
	{
		this.context = context;
		this.emitter = emitter;
		initalize( page );
	}
	
	private void initalize(PageDef page)
	{
		axis = new AxisProcessor( );		
		axis.addCoordinate( page.contentwidth );

		setCacheSize();
		
		Rule rule = new Rule( 0, page.contentwidth );
		cache = new DataCache( MAX_COLUMN, MAX_ROW, emitter );
		engine = new StyleEngine( this );
		containers.push( createContainer( rule, page.style ) );
	}
	
	private void setCacheSize()
	{
		if(context.getOfficeVersion( ).equals( "office2007" ))
		{
			MAX_COLUMN = 10000;
		}
	}

	public XlsContainer getCurrentContainer( )
	{
		return (XlsContainer) containers.peek( );
	}

	public Stack<XlsContainer> getContainers( )
	{
		return containers;
	}

	public void addTable( TableInfo table, IStyle style )
	{
		Rule rule = getCurrentContainer( ).getRule( );

		int start = rule.getStart( );
		//npos is the start position of each column.
		int[] npos = new int[table.getColumnCount( ) + 1];
		npos[0] = start;

		for ( int i = 1; i <= table.getColumnCount( ); i++ )
		{
			npos[i] = npos[i - 1] + table.getColumnWidth( i - 1 );
		}

		int[] scale = axis.getRange( start, rule.getEnd( ) );

		for ( int i = 0; i < scale.length - 1; i++ )
		{
			int startPosition = scale[i];
			int endPostion = scale[i + 1];

			int[] range = inRange( startPosition, endPostion, npos );

			if ( range.length > 0 )
			{				
				int pos = axis.getCoordinateIndex( startPosition );
				cache.insertColumns( pos, range.length );

				for ( int j = 0; j < range.length; j++ )
				{
					axis.addCoordinate( range[j] );
				}
			}
		}

		XlsContainer container = createContainer( rule, style );
		XlsTable tcontainer = new XlsTable( table, container );
		addContainer( tcontainer );
		tables.push( tcontainer );
	}

	private int[] inRange( int start, int end, int[] data )
	{
		int[] range = new int[data.length];
		int count = 0;

		for ( int i = 0; i < data.length; i++ )
		{
			if ( ( data[i] > start ) && ( data[i] < end ) )
			{
				count++;
				range[count] = data[i];
			}
		}

		int[] result = new int[count];

		int j = 0;
		for ( int i = 0; i < range.length; i++ )
		{
			if ( range[i] != 0 )
			{
				result[j] = range[i];
				j++;
			}
		}

		return result;
	}

	public void addCell( int col, int span, IStyle style )
	{
		XlsTable table = tables.peek( );
		Rule rule = table.getColumnRule( col, span );
		addContainer( createContainer( rule, style ) );
	}

	public void endCell( )
	{
		endContainer( );
	}

	public void addRow( IStyle style )
	{
		XlsTable table = (XlsTable) containers.peek( );
		XlsContainer container = createContainer( table.getRule( ), style );
		container.setEmpty( false );
		addContainer( container );
	}

	public void endRow( )
	{
		synchronous( );
		endContainer( );
	}

	private void synchronous( )
	{
		Rule rule = getCurrentContainer( ).getRule( );
		int start = rule.getStart( );
		int end = rule.getEnd( );
		int startcol = axis.getCoordinateIndex( start );
		int endcol = axis.getCoordinateIndex( end );

		int max = 0;
		int len[] = new int[endcol - startcol];

		for ( int i = startcol; i < endcol; i++ )
		{			
			int columnsize = cache.getColumnSize( i );
			len[i - startcol] = columnsize;
			max = max > columnsize ? max : columnsize;
		}

		for ( int i = startcol; i < endcol; i++ )
		{
			int rowspan = max - len[i - startcol];
			int last = len[i - startcol] - 1;

			if ( rowspan > 0 )
			{
				Data data = null;				
				Data upstair = cache.getData( i, last );

				if ( upstair != null && upstair != Data.WASTE )
				{
					Data predata = upstair;
					int rs = predata.getRowSpan( ) + rowspan;
					predata.setRowSpan( rs );
					data = predata;
				}
				else
				{
					data = Data.WASTE;

				}

				for ( int p = 0; p < rowspan; p++ )
				{
					cache.addData( i, data );
				}
			}
		}
	}

	public void endTable( )
	{
		if ( !tables.isEmpty( ) )
		{
			tables.pop( );
			endContainer( );
		}
	}

	public void addContainer( IStyle style, HyperlinkDef link )
	{
		Rule rule = getCurrentContainer( ).getRule( );
		StyleEntry entry = engine.createEntry( rule, style );
		addContainer( new XlsContainer( entry, rule ) );
	}

	public void addContainer( XlsContainer container )
	{
		getCurrentContainer( ).setEmpty( false );
		int col = axis.getCoordinateIndex( container.getRule( ).getStart( ) );
		int pos = cache.getColumnSize( col );
		container.setStart( pos );
		containers.push( container );
	}

	public void endContainer( )
	{
		XlsContainer container = getCurrentContainer( );
		// Make sure there is an data in a container
		if ( container.isEmpty( ) )
		{
			Data data = new Data( EMPTY, container.getStyle( ), Data.STRING );
			data.setRule( container.getRule( ) );
			addData( data );
		}

		engine.removeContainerStyle( );
		containers.pop( );
	}

	public void addData( Object txt, IStyle style, HyperlinkDef link, BookmarkDef bookmark )
	{
		Rule rule = getCurrentContainer( ).getRule( );
		StyleEntry entry = engine.getStyle( style, rule );
		Data data = createData( txt, entry );
		data.setHyperlinkDef( link );
		data.setBookmark( bookmark );
		data.setRule( rule );

		addData( data );
	}
	
	public void addDateTime(Object txt, IStyle style, HyperlinkDef link, BookmarkDef bookmark)
	{
		Rule rule = getCurrentContainer( ).getRule( );
		StyleEntry entry = engine.getStyle( style, rule );
		Data data = null;
		
		txt = ((IDataContent)txt).getValue( );
		
		data = createDateData( txt, entry , style.getDateTimeFormat());
		
		data.setHyperlinkDef( link );
		data.setBookmark( bookmark );
		data.setRule( rule );

		addData( data );
	}

	public void addCaption( String text )
	{
		Rule rule = getCurrentContainer( ).getRule( );
		StyleEntry entry = StyleBuilder.createEmptyStyleEntry( );
		entry.setProperty( StyleEntry.H_ALIGN_PROP, "Center" );
		Data data = createData( text, entry );
		data.setRule( rule );

		addData( data );
	}

	public Data createData( Object txt, StyleEntry entry )
	{
		String type = Data.STRING;
		
		if ( Data.NUMBER.equals( ExcelUtil.getType( txt )))
		{
			String format = ExcelUtil.getPattern( txt, 
					entry.getProperty( StyleConstant.NUMBER_FORMAT_PROP ));
			entry.setProperty( StyleConstant.NUMBER_FORMAT_PROP, format );			
			type = Data.NUMBER;

		}
		else if ( Data.DATE.equals( ExcelUtil.getType( txt )))
		{
			String format = ExcelUtil.getPattern( txt, 
					entry.getProperty( StyleConstant.DATE_FORMAT_PROP ) );
			entry.setProperty( StyleConstant.DATE_FORMAT_PROP, format );			
			type = Data.DATE;
		}
		
		entry.setProperty( StyleConstant.DATA_TYPE_PROP, type );
		return new Data( txt, entry, type );
	}
	
	private Data createDateData(Object txt , StyleEntry entry , String timeFormat)
	{
		
		timeFormat = ExcelUtil.parse( timeFormat );
		if ( timeFormat.equals( "" ) )
		{
			if ( txt instanceof java.sql.Date )
			{
				timeFormat = "MMM d, yyyy";
			}
			else if ( txt instanceof java.sql.Time )
			{
				timeFormat = "H:mm:ss AM/PM";
			}
			else
			{
				timeFormat = "MMM d, yyyy H:mm AM/PM";
			}
		}
		entry.setProperty( StyleConstant.DATE_FORMAT_PROP, timeFormat );
		entry.setProperty( StyleConstant.DATA_TYPE_PROP, Data.DATE );
		return new Data( txt, entry, Data.DATE );
	}

	private void addData( Data data )
	{
		getCurrentContainer( ).setEmpty( false );
		int col = axis.getCoordinateIndex( data.getRule( ).getStart( ) );
		int span = axis.getCoordinateIndex( data.getRule( ).getEnd( ) ) - col;
		addDatatoCache( col, data );

		for ( int i = col + 1; i < col + span; i++ )
		{
			addDatatoCache( i, Data.WASTE );
		}
	}

	public XlsContainer createContainer( Rule rule, IStyle style )
	{
		return new XlsContainer( engine.createEntry( rule, style ), rule );
	}

	public Map<StyleEntry,Integer> getStyleMap( )
	{
		return engine.getStyleIDMap( );
	}
	
	public List<BookmarkDef> getNamesRefer( )
	{
		return cache.getBookmarks( );
	}

	public int[] getCoordinates( )
	{
		int[] coord = axis.getCoordinates( );
		
		if(coord.length <= MAX_COLUMN) 
		{
			return coord;
		}	
		else 
		{
			int[] ncoord = new int[MAX_COLUMN]; 
			System.arraycopy( coord, 0, ncoord, 0, MAX_COLUMN );
			return ncoord;
		}		
	}

	public int getRowCount( )
	{
		int realcount = cache.getMaxRow( );
		return realcount;
	}

	public AxisProcessor getAxis( )
	{
		return axis;
	}

	public int getColumnSize( int column )
	{		
		return cache.getColumnSize( column );
	}

	public Data getData( int col, int row )
	{		
		Object object = cache.getData( col, row );
		return object == Data.WASTE ? null : (Data) object;
	}

	public Data[] getRow( int rownum )
	{
		Data[] row = cache.getRowData( rownum );
		List<Data> data = new ArrayList<Data>( );
		int width = Math.min( row.length, MAX_COLUMN - 1 );

		for ( int i = 0; i < width; i++ )
		{
			if ( Data.WASTE == row[i] )
			{
				continue;
			}

			Data d = (Data) row[i];

			if ( d.isProcessed( ) )
			{
				continue;
			}

			d.setProcessed( true );
			data.add( row[i] );
		}

		Data[] rowdata = new Data[data.size( )];
		data.toArray( rowdata );
		return rowdata;
	}

	private void addDatatoCache( int col, Data value )
	{
		cache.addData( col, value );
	}

	public void complete( )
	{
		int rowcount = cache.getMaxRow( );

		for ( int i = 0; i < rowcount; i++ )
		{
			Object[] row = cache.getRowData( i );

			for ( int j = 0; j < row.length; j++ )
			{
				if ( row[j] == Data.WASTE )
				{
					continue;
				}

				Data d = (Data) row[j];
				int styleid = engine.getStyleID( d.getStyleEntry( ) );
				d.setStyleId( styleid );
				Rule rule = d.getRule( );
				
				//Excel Cell Starts From 1
				int start = axis.getCoordinateIndex( rule.getStart( ) ) + 1;
				int end = axis.getCoordinateIndex( rule.getEnd( ) ) + 1;
				
				end = Math.min( end, MAX_COLUMN );
				int scount = Math.max(0, end - start - 1);
				//Excel Span Starts From 1
				Span span = new Span( start, scount );

				HyperlinkDef link = d.getHyperlinkDef( );

				if ( link != null && link.getBookmark( ) != null )
				{
					// Excel cell start is 1
					links.put( link.getBookmark( ), 
							   getCellName( i + 1,start + 1 ) );
				}

				d.setSpan( span );
			}
		}
	}

	private String getCellName( int row, int col )
	{
		char base = (char) ( col + 64 );
		Character chr = new Character( base );

		return chr.toString( ) + row;
	}
}