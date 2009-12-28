/*******************************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.emitter.config.excel;

import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.IExcelRenderOption;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.emitter.config.AbstractConfigurableOptionObserver;
import org.eclipse.birt.report.engine.emitter.config.AbstractEmitterDescriptor;
import org.eclipse.birt.report.engine.emitter.config.ConfigurableOption;
import org.eclipse.birt.report.engine.emitter.config.IConfigurableOption;
import org.eclipse.birt.report.engine.emitter.config.IConfigurableOptionObserver;
import org.eclipse.birt.report.engine.emitter.config.IOptionValue;
import org.eclipse.birt.report.engine.emitter.config.excel.i18n.Messages;

/**
 * This class is a descriptor of excel emitter.
 */
public class ExcelEmitterDescriptor extends AbstractEmitterDescriptor
{

	protected static final String TEXT_WRAPPING = "TextWrapping";
	protected static final String CHART_DPI = "ChartDpi";

	protected IConfigurableOption[] options;

	public ExcelEmitterDescriptor( )
	{
		initOptions( );
	}

	private void initOptions( )
	{
		// Initializes the option for WrappingText.
		ConfigurableOption wrappingText = initializeWrappingText( );
		
		// Initializes the option for chart DPI.
		ConfigurableOption chartDpi = new ConfigurableOption( CHART_DPI );
		chartDpi.setDisplayName( Messages
				.getString( "OptionDisplayValue.ChartDpi" ) ); //$NON-NLS-1$
		chartDpi.setDataType( IConfigurableOption.DataType.INTEGER );
		chartDpi
				.setDisplayType( IConfigurableOption.DisplayType.TEXT );
		chartDpi.setDefaultValue( new Integer( 192 ) );
		chartDpi.setToolTip( null );
		chartDpi.setDescription( Messages
				.getString( "OptionDescription.ChartDpi" ) ); //$NON-NLS-1$
		
		options = new IConfigurableOption[]{wrappingText, chartDpi};
	}

	protected ConfigurableOption initializeWrappingText( )
	{
		ConfigurableOption wrappingText = new ConfigurableOption( TEXT_WRAPPING );
		wrappingText.setDisplayName( Messages
				.getString( "OptionDisplayValue.WrappingText" ) ); //$NON-NLS-1$
		wrappingText.setDataType( IConfigurableOption.DataType.BOOLEAN );
		wrappingText.setDisplayType( IConfigurableOption.DisplayType.CHECKBOX );
		wrappingText.setDefaultValue( Boolean.TRUE );
		wrappingText.setToolTip( null );
		wrappingText.setDescription( Messages
				.getString( "OptionDescription.WrappingText" ) ); //$NON-NLS-1$
		return wrappingText;
	}

	@Override
	public IConfigurableOptionObserver createOptionObserver( )
	{
		return new ExcelOptionObserver( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.engine.emitter.config.AbstractEmitterDescriptor
	 * #getDescription()
	 */
	public String getDescription( )
	{
		return Messages.getString( "ExcelEmitter.Description" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.birt.report.engine.emitters.IEmitterDescriptor#getDisplayName
	 * ()
	 */
	public String getDisplayName( )
	{
		return Messages.getString( "ExcelEmitter.DisplayName" ); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.emitters.IEmitterDescriptor#getID()
	 */
	public String getID( )
	{
		return "org.eclipse.birt.report.engine.emitter.prototype.excel"; //$NON-NLS-1$
	}

	public String getRenderOptionName( String name )
	{
		assert name != null;
		if ( TEXT_WRAPPING.equals( name ) )
		{
			return IExcelRenderOption.WRAPPING_TEXT;
		}
		if ( CHART_DPI.equals( name ) )
		{
			return IRenderOption.CHART_DPI;
		}
		return name;
	}

	/**
	 * ExcelOptionObserver
	 */
	class ExcelOptionObserver extends AbstractConfigurableOptionObserver
	{

		public IConfigurableOption[] getOptions( )
		{
			return options;
		}

		public IRenderOption getPreferredRenderOption( )
		{
			EXCELRenderOption renderOption = new EXCELRenderOption( );

			renderOption.setEmitterID( getID( ) );
			renderOption.setOutputFormat( "xls" ); //$NON-NLS-1$
			
			for ( IOptionValue optionValue : values )
			{
				if ( optionValue != null )
				{
					renderOption.setOption( getRenderOptionName( optionValue
							.getName( ) ), optionValue.getValue( ) );
				}
			}

			return renderOption;
		}
	}
}
