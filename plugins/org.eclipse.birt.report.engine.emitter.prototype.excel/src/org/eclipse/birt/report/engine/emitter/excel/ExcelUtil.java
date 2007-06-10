
package org.eclipse.birt.report.engine.emitter.excel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.birt.report.engine.emitter.excel.GroupInfo.Position;
import org.eclipse.birt.report.engine.ir.DimensionType;

public class ExcelUtil
{

	public static String ridQuote( String val )
	{
		if ( val.charAt( 0 ) == '"' && val.charAt( val.length( ) - 1 ) == '"' )
		{
			return val.substring( 1, val.length( ) - 1 );
		}

		return val;
	}

	// TODO
	public static String getValue( String val )
	{
		if ( val == null )
		{
			return StyleConstant.NULL;
		}
		if ( val.charAt( 0 ) == '"' && val.charAt( val.length( ) - 1 ) == '"' )
		{
			return val.substring( 1, val.length( ) - 1 );
		}

		return val;
	}

	public static int convertToPt( String size )
	{
		try
		{
			int s = Integer.valueOf( size.substring( 0, size.length( ) - 2 ) )
					.intValue( );
			if ( size.endsWith( "in" ) )
			{
				return s * 72;
			}
			else if ( size.endsWith( "cm" ) )
			{
				return (int) ( s / 2.54 * 72 );
			}
			else if ( size.endsWith( "mm" ) )
			{
				return (int) ( s * 10 / 2.54 * 72 );
			}
			else if ( size.endsWith( "pc" ) )
			{
				return s;
			}
			else
			{
				return s;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			return 0;
		}
	}

	public static String getColumnOfExp( String exp )
	{
		return exp.substring( exp.indexOf( "dataSetRow[" ), exp
				.lastIndexOf( "]" ) + 1 );
	}
	private static final int max_formula_length = 512;

	public static String createFormula( String txt, String exp, List positions )
	{
		exp = getFormulaName( exp );
		StringBuffer sb = new StringBuffer( exp + "(" );
		for ( int i = 0; i < positions.size( ); i++ )
		{
			Position p = (Position) positions.get( i );
			sb.append( "R" + p.row + "C" + p.column + "," );
		}
		sb.setCharAt( sb.length( ) - 1, ')' );

		if ( sb.length( ) > max_formula_length || positions.size( ) == 0 )
		{
			return txt;
		}
		return sb.toString( );
	}

	private static String getFormulaName( String expression )
	{
		if ( expression.startsWith( "Total.sum" ) )
		{
			return "=SUM";
		}
		else if ( expression.startsWith( "Total.ave" ) )
		{
			return "=AVERAGE";
		}
		else if ( expression.startsWith( "Total.max" ) )
		{
			return "=MAX";
		}
		else if ( expression.startsWith( "Total.min" ) )
		{
			return "=MIN";
		}
		else if ( expression.startsWith( "Total.count" ) )
		{
			return "=COUNT";
		}
		throw new RuntimeException( "Cannot parse the expression" + expression );
	}

	private static final String reg1 = "Total." + "(count|ave|sum|max|min)"
			+ "\\(", reg2 = "\\)", reg3 = "\\[", reg4 = "\\]";

	public static boolean isValidExp( String exp, String[] columnNames )
	{
		StringBuffer sb = new StringBuffer( );
		for ( int i = 0; i < columnNames.length; i++ )
		{
			sb.append( columnNames[i] + "|" );
		}
		String columnRegExp = "(" + sb.substring( 0, sb.length( ) - 1 ) + ")";
		columnRegExp = columnRegExp.replaceAll( reg3, "Z" );
		columnRegExp = columnRegExp.replaceAll( reg4, "Z" );

		String aggregateRegExp = reg1 + columnRegExp + reg2;

		exp = exp.replaceAll( reg3, "Z" );
		exp = exp.replaceAll( reg4, "Z" );

		Pattern p = Pattern.compile( aggregateRegExp );
		Matcher m = p.matcher( exp );
		boolean agg = m.matches( );

		p = Pattern.compile( columnRegExp );
		m = p.matcher( exp );
		return agg || m.matches( );
	}

	public static String expression( String val, String target, String[] res,
			boolean casesenstive )
	{
		boolean flag = casesenstive ? target.equals( val ) : target
				.equalsIgnoreCase( val );
		return flag ? res[1] : res[0];
	}

	public static int covertDimensionType( DimensionType value, int parent )
	{
		if ( DimensionType.UNITS_PERCENTAGE.equals( value.getUnits( ) ) )
		{
			return (int) value.getMeasure( ) / 100 * parent;
		}
		else
		{
			return (int) value.convertTo( DimensionType.UNITS_PT );
		}
	}	
}
