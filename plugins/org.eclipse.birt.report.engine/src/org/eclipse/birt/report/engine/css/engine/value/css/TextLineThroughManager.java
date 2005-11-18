package org.eclipse.birt.report.engine.css.engine.value.css;

import org.eclipse.birt.report.engine.css.engine.value.IdentifierManager;
import org.eclipse.birt.report.engine.css.engine.value.StringMap;
import org.eclipse.birt.report.engine.css.engine.value.Value;
import org.eclipse.birt.report.engine.css.engine.value.birt.BIRTConstants;

public class TextLineThroughManager extends IdentifierManager
{

	/**
	 * The identifier values.
	 */
	protected final static StringMap values = new StringMap( );
	static
	{
		values.put( CSSConstants.CSS_NONE_VALUE, CSSValueConstants.NONE_VALUE );
		values.put( CSSConstants.CSS_LINE_THROUGH_VALUE,
				CSSValueConstants.LINE_THROUGH_VALUE );
	}

	public StringMap getIdentifiers( )
	{
		return values;
	}

	public String getPropertyName( )
	{
		return BIRTConstants.BIRT_TEXT_LINETHROUGH_PROPERTY;
	}

	public boolean isInheritedProperty( )
	{
		return true;
	}

	public Value getDefaultValue( )
	{
		return CSSValueConstants.NONE_VALUE;
	}
}