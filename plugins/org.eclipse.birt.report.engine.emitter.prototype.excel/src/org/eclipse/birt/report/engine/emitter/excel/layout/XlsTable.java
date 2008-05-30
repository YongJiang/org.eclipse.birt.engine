package org.eclipse.birt.report.engine.emitter.excel.layout;

import org.eclipse.birt.report.engine.emitter.excel.StyleEntry;


public class XlsTable extends XlsContainer
{
	private int[] columns;
	
	private int width;
	
	public XlsTable(StyleEntry entry, Rule rule)
	{
		super(entry, rule);
	}
	
	public XlsTable(TableInfo table, StyleEntry entry, Rule rule)
	{
		this(entry, rule);
		width = Math.min( table.getTableWidth( ), rule.getWidth() );
		this.columns = LayoutUtil.getColumnWidth( table, width );
		this.width = table.getTableWidth( );
	}
	
	public XlsTable(TableInfo table, XlsContainer container)
	{
		this(table, container.getStyle( ), container.getRule( ));
	}
	
	public Rule getColumnRule(int column, int span)
	{
		int sp = getRule().getStart( );
		
		for(int i = 0; i < column; i++)
		{
			sp += columns[i];
		}	
		
		int sw = 0;
		
		for(int i = column; i < column + span; i++)
		{
			sw += columns[i];
		}	
		
		return new Rule(sp, sw);
	}	
}