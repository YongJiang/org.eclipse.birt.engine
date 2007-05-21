/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.core.data.ExpressionUtil;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.css.dom.StyleDeclaration;
import org.eclipse.birt.report.engine.css.engine.CSSEngine;
import org.eclipse.birt.report.engine.ir.ActionDesign;
import org.eclipse.birt.report.engine.ir.AutoTextItemDesign;
import org.eclipse.birt.report.engine.ir.BandDesign;
import org.eclipse.birt.report.engine.ir.CellDesign;
import org.eclipse.birt.report.engine.ir.ColumnDesign;
import org.eclipse.birt.report.engine.ir.DataItemDesign;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.DrillThroughActionDesign;
import org.eclipse.birt.report.engine.ir.DynamicTextItemDesign;
import org.eclipse.birt.report.engine.ir.EngineIRConstants;
import org.eclipse.birt.report.engine.ir.ExtendedItemDesign;
import org.eclipse.birt.report.engine.ir.FreeFormItemDesign;
import org.eclipse.birt.report.engine.ir.GraphicMasterPageDesign;
import org.eclipse.birt.report.engine.ir.GridItemDesign;
import org.eclipse.birt.report.engine.ir.GroupDesign;
import org.eclipse.birt.report.engine.ir.HighlightDesign;
import org.eclipse.birt.report.engine.ir.HighlightRuleDesign;
import org.eclipse.birt.report.engine.ir.ImageItemDesign;
import org.eclipse.birt.report.engine.ir.LabelItemDesign;
import org.eclipse.birt.report.engine.ir.ListBandDesign;
import org.eclipse.birt.report.engine.ir.ListGroupDesign;
import org.eclipse.birt.report.engine.ir.ListItemDesign;
import org.eclipse.birt.report.engine.ir.ListingDesign;
import org.eclipse.birt.report.engine.ir.MapDesign;
import org.eclipse.birt.report.engine.ir.MapRuleDesign;
import org.eclipse.birt.report.engine.ir.MasterPageDesign;
import org.eclipse.birt.report.engine.ir.PageSetupDesign;
import org.eclipse.birt.report.engine.ir.Report;
import org.eclipse.birt.report.engine.ir.ReportElementDesign;
import org.eclipse.birt.report.engine.ir.ReportItemDesign;
import org.eclipse.birt.report.engine.ir.RowDesign;
import org.eclipse.birt.report.engine.ir.SimpleMasterPageDesign;
import org.eclipse.birt.report.engine.ir.StyledElementDesign;
import org.eclipse.birt.report.engine.ir.TableBandDesign;
import org.eclipse.birt.report.engine.ir.TableGroupDesign;
import org.eclipse.birt.report.engine.ir.TableItemDesign;
import org.eclipse.birt.report.engine.ir.TemplateDesign;
import org.eclipse.birt.report.engine.ir.TextItemDesign;
import org.eclipse.birt.report.engine.ir.VisibilityDesign;
import org.eclipse.birt.report.engine.ir.VisibilityRuleDesign;
import org.eclipse.birt.report.model.api.ActionHandle;
import org.eclipse.birt.report.model.api.AutoTextHandle;
import org.eclipse.birt.report.model.api.CellHandle;
import org.eclipse.birt.report.model.api.ColumnHandle;
import org.eclipse.birt.report.model.api.ComputedColumnHandle;
import org.eclipse.birt.report.model.api.DataItemHandle;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.DesignVisitor;
import org.eclipse.birt.report.model.api.DimensionHandle;
import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.birt.report.model.api.FactoryPropertyHandle;
import org.eclipse.birt.report.model.api.FreeFormHandle;
import org.eclipse.birt.report.model.api.GraphicMasterPageHandle;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.GroupHandle;
import org.eclipse.birt.report.model.api.HideRuleHandle;
import org.eclipse.birt.report.model.api.HighlightRuleHandle;
import org.eclipse.birt.report.model.api.ImageHandle;
import org.eclipse.birt.report.model.api.LabelHandle;
import org.eclipse.birt.report.model.api.ListGroupHandle;
import org.eclipse.birt.report.model.api.ListHandle;
import org.eclipse.birt.report.model.api.ListingHandle;
import org.eclipse.birt.report.model.api.MapRuleHandle;
import org.eclipse.birt.report.model.api.MasterPageHandle;
import org.eclipse.birt.report.model.api.MemberHandle;
import org.eclipse.birt.report.model.api.ParamBindingHandle;
import org.eclipse.birt.report.model.api.PropertyHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.ReportElementHandle;
import org.eclipse.birt.report.model.api.ReportItemHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.birt.report.model.api.SlotHandle;
import org.eclipse.birt.report.model.api.StructureHandle;
import org.eclipse.birt.report.model.api.StyleHandle;
import org.eclipse.birt.report.model.api.TOCHandle;
import org.eclipse.birt.report.model.api.TableGroupHandle;
import org.eclipse.birt.report.model.api.TableHandle;
import org.eclipse.birt.report.model.api.TemplateReportItemHandle;
import org.eclipse.birt.report.model.api.TextDataHandle;
import org.eclipse.birt.report.model.api.TextItemHandle;
import org.eclipse.birt.report.model.api.core.UserPropertyDefn;
import org.eclipse.birt.report.model.api.elements.DesignChoiceConstants;
import org.eclipse.birt.report.model.api.elements.structures.HighlightRule;
import org.eclipse.birt.report.model.api.metadata.DimensionValue;
import org.eclipse.birt.report.model.api.metadata.IPropertyType;
import org.eclipse.birt.report.model.elements.Style;
import org.eclipse.core.runtime.Assert;

/**
 * Constructs an internal representation of the report design for report
 * geenration and presentation, based on the internal representation that design
 * engine creates. The DE IR services both the designer UI and factory, and has
 * certain features that are not quite suitable for FPE use. In particular, this
 * step of the reconstruction is needed for several reasons:
 * <p>
 * <li>Style handling: DE stores all styles in an unflatten version. Factory
 * needs to reference styles where the element hierarchy has been flattened.
 * <li>Faster lookup: DE stores various properties as property name/value
 * pairs. Factory IR might store them as structure. See
 * <code>createHighlightRule()</code> for an example.
 * <li>Merging properties: DE stores custom and default properties separately.
 * In FPE, they are merged.</li>
 * <p>
 * 
 * This class visits the Design Engine's IR to create a new IR for FPE. It is
 * usually used in the "Design Adaptation" phase of report generation, which is
 * also the first step in report generation after DE loads the report in.
 * 
 * <p>
 * special consideration in styles
 * <p>
 * BIRT uses a simlar style mode with CSS, but not exactly the same. The main
 * differences are:
 * <li> text-decoration is not inheraible which simplify the CSS standard. This
 * rules makes text-decroation are usless for all the containers. As the HTML
 * treat the text-decoration inheritable in block-level element, the ENGINE must
 * remove the text-decoration from the container's styles.
 * <li> BIRT doesn't define the body style, it uses a predefined style "report"
 * as the default style.
 * 
 */
public class EngineIRVisitor extends DesignVisitor
{
	/**
	 * The prefix of style name
	 */
	static final String PREFIX_STYLE_NAME = "style_"; //$NON-NLS-1$

	
	/**
	 * default master page name.
	 */
	static final String DEFAULT_MASTERPAGE_NAME = "NewSimpleMasterPage";
	
	/**
	 * The default value of masterPage's margin, in inch.
	 * See rom.ref in model: <PropertyGroup displayNameID="Element.MasterPage.margin">
	 */
	static final double DEFAULT_MASTERPAGE_TOP_MARGIN = 1; 
	static final double DEFAULT_MASTERPAGE_LEFT_MARGIN = 1.25; 
	static final double DEFAULT_MASTERPAGE_BOTTOM_MARGIN = 1; 
	static final double DEFAULT_MASTERPAGE_RIGHT_MARGIN = 1.25; 
	
	/**
	 * logger used to log the error.
	 */
	protected static Logger logger = Logger.getLogger( EngineIRVisitor.class
			.getName( ) );

	/**
	 * current report element created by visitor
	 */
	protected Object currentElement;


	/**
	 * Factory IR created by this visitor
	 */
	protected Report report;

	/**
	 * report design handle
	 */
	protected ReportDesignHandle handle;

	/**
	 * the CSSEngine
	 */
	protected CSSEngine cssEngine;

	/**
	 * the inheritable report style
	 */
	StyleDeclaration nonInheritableReportStyle;

	/**
	 * the non-inheritable report style
	 */
	StyleDeclaration inheritableReportStyle;

	/**
	 * Used to fix half-baked handle, such as:
	 *   fix the new added empty cell created in format irregular table or grid.
	 *   fix default master page.
	 */
	long newCellId = -1;
	
	/**
	 * constructor
	 * 
	 * @param handle
	 *            the entry point to the DE report design IR
	 * 
	 */
	public EngineIRVisitor( ReportDesignHandle handle )
	{
		super( );
		this.handle = handle;
	}

	/**
	 * translate the DE's IR to FPE's IR.
	 * 
	 * @return FPE's IR.
	 */
	public Report translate( )
	{
		report = new Report( );
		cssEngine = report.getCSSEngine( );
		report.setReportDesign( handle );
		apply( handle );
		return report;
	}
	
	public ReportItemDesign translate( ReportElementHandle handle, Report report )
	{
		this.report = report;
		cssEngine = report.getCSSEngine( );
		newCellId = handle.getID( ) * -100000000;
		apply( handle );
		assert currentElement instanceof ReportItemDesign;
		return (ReportItemDesign)currentElement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.api.DesignVisitor#visitReportDesign(org.eclipse.birt.report.model.api.ReportDesignHandle)
	 */
	public void visitReportDesign( ReportDesignHandle handle )
	{
		setupNamedExpressions( handle, report.getNamedExpressions( ) );

		// INCLUDE LIBRARY
		// INCLUDE SCRIPT
		// CODE MODULES

		// Sets the report default style
		StyleHandle defaultStyle = handle.findStyle( "report" );//$NON-NLS-1$
		createReportDefaultStyles( defaultStyle );

		// TODO: add report style
		// report.addStyle( );

		// COLOR-PALETTE
		// METHOD
		// STYLES
		// We needn't handle the style slot, it will be handled for each
		// element.

		// Handle Master Page
		PageSetupDesign pageSetup = new PageSetupDesign( );
		SlotHandle pageSlot = handle.getMasterPages( );
		for ( int i = 0; i < pageSlot.getCount( ); i++ )
		{
			apply( pageSlot.get( i ) );
			if ( currentElement != null )
			{
				pageSetup.addMasterPage( (MasterPageDesign) currentElement );
			}
		}
		
		// If there is no master page, set a default one.
		if ( pageSlot.getCount( ) < 1 )
		{
			MasterPageDesign masterPage = new SimpleMasterPageDesign( );
			masterPage.setID( generateUniqueID( ) );
			masterPage.setName( DEFAULT_MASTERPAGE_NAME );
			masterPage.setPageType( DesignChoiceConstants.PAGE_SIZE_US_LETTER );		
			masterPage.setOrientation( DesignChoiceConstants.PAGE_ORIENTATION_AUTO );
						
			DimensionType top = new DimensionType( DEFAULT_MASTERPAGE_TOP_MARGIN, DimensionType.UNITS_IN );
			DimensionType left = new DimensionType( DEFAULT_MASTERPAGE_LEFT_MARGIN, DimensionType.UNITS_IN );
			DimensionType bottom = new DimensionType( DEFAULT_MASTERPAGE_BOTTOM_MARGIN, DimensionType.UNITS_IN );
			DimensionType right = new DimensionType( DEFAULT_MASTERPAGE_RIGHT_MARGIN, DimensionType.UNITS_IN );		
			masterPage.setMargin( top, left, bottom, right );	
			
			pageSetup.addMasterPage( masterPage );
		}
		
		// FIXME: add page sequence support
		// Handle Page Sequence
		// SlotHandle seqSlot = handle.getPageSequences( );
		// for ( int i = 0; i < seqSlot.getCount( ); i++ )
		// {
		// apply( seqSlot.get( i ) );
		// assert ( currentElement != null );
		// pageSetup.addPageSequence( (PageSequenceDesign) currentElement );
		// }

		report.setPageSetup( pageSetup );

		// COMPONENTS

		// Handle Report Body
		SlotHandle bodySlot = handle.getBody( );
		for ( int i = 0; i < bodySlot.getCount( ); i++ )
		{
			apply( bodySlot.get( i ) );
			if ( currentElement != null )
			{
				report.addContent( (ReportItemDesign) currentElement );
			}
		}

		// SCRATCH-PAD
		// CONFIG-VARS
		// TRANSLATIONS
		// IMAGES
		// CUSTOM
	}

	/**
	 * setup the named expression map
	 * 
	 * @param userProperties
	 *            user defined named expressions in design file
	 * @param namedExpressions
	 *            the data structure that hold named expressions
	 */
	private void setupNamedExpressions( DesignElementHandle handle,
			Map namedExpressions )
	{
		List userProperties = handle.getUserProperties( );
		if ( userProperties == null || namedExpressions == null )
			return;
		for ( int i = 0; i < userProperties.size( ); i++ )
		{
			UserPropertyDefn userDef = (UserPropertyDefn) userProperties
					.get( i );
			if ( userDef.getTypeCode( ) == IPropertyType.EXPRESSION_TYPE )
			{
				String name = userDef.getName( );
				String exprString = handle.getStringProperty( name );
				if ( exprString != null && !exprString.trim( ).equals( "" ) ) //$NON-NLS-1$
				{
					//Expression expression = new Expression( exprString );
					namedExpressions.put( name, exprString );
				}
			}
		}
	}

	/**
	 * setup the master page object from the base master page handle.
	 * 
	 * @param page
	 *            page object
	 * @param handle
	 *            page handle
	 */
	private void setupMasterPage( MasterPageDesign page, MasterPageHandle handle )
	{
		setupStyledElement( page, handle );

		page.setPageType( handle.getPageType( ) );

		// Master page width and height
		DimensionValue effectWidth = handle.getPageWidth();
		DimensionValue effectHeight = handle.getPageHeight();
		DimensionType width = null;
		DimensionType height = null;
		if ( effectWidth != null )
		{
			width = new DimensionType( effectWidth.getMeasure( ), effectWidth
					.getUnits( ) );
		}
		if ( effectHeight != null )
		{
			height = new DimensionType( effectHeight.getMeasure( ),
					effectHeight.getUnits( ) );
		}
		page.setPageSize( width, height );
		page.setOrientation( handle.getOrientation( ) );

		// Master page margins
		DimensionType top = createDimension( handle.getTopMargin( ), true );
		DimensionType left = createDimension( handle.getLeftMargin( ), true );
		DimensionType bottom = createDimension( handle.getBottomMargin( ), true  );
		DimensionType right = createDimension( handle.getRightMargin( ), true  );
		page.setMargin( top, left, bottom, right );
	}

	protected void visitDesignElement( DesignElementHandle obj )
	{
		// any unsupported element
		currentElement = null;
	}

	public void visitGraphicMasterPage( GraphicMasterPageHandle handle )
	{
		GraphicMasterPageDesign page = new GraphicMasterPageDesign( );

		setupMasterPage( page, handle );

		// Multi-column properties
		page.setColumns( handle.getColumnCount( ) );
		DimensionType spacing = createDimension( handle.getColumnSpacing( ), true );
		page.setColumnSpacing( spacing );

		// Master page content
		SlotHandle contentSlot = handle.getContent( );
		for ( int i = 0; i < contentSlot.getCount( ); i++ )
		{
			apply( contentSlot.get( i ) );
			if ( currentElement != null )
			{
				page.addContent( (ReportItemDesign) currentElement );
			}
		}

		currentElement = page;
		
		// We do not support graphic master page now.
		Assert.isTrue( false, "Graphic master page is not supported now!" );
	}

	public void visitSimpleMasterPage( SimpleMasterPageHandle handle )
	{
		SimpleMasterPageDesign page = new SimpleMasterPageDesign( );

		// setup the base master page property.
		setupMasterPage( page, handle );
		
		page.setHeaderHeight( createDimension( handle.getHeaderHeight( ), true  ));
		page.setFooterHeight( createDimension( handle.getFooterHeight( ), true  ) );
		page.setShowFooterOnLast( handle.showFooterOnLast( ) );
		page.setShowHeaderOnFirst( handle.showHeaderOnFirst( ) );
		page.setFloatingFooter( handle.isFloatingFooter( ) );
		SlotHandle headerSlot = handle.getPageHeader( );
		for ( int i = 0; i < headerSlot.getCount( ); i++ )
		{
			apply( headerSlot.get( i ) );
			if ( currentElement != null )
			{
				page.addHeader( (ReportItemDesign) currentElement );
			}
		}

		SlotHandle footerSlot = handle.getPageFooter( );
		for ( int i = 0; i < footerSlot.getCount( ); i++ )
		{
			apply( footerSlot.get( i ) );
			if ( currentElement != null )
			{
				page.addFooter( (ReportItemDesign) currentElement );
			}
		}
		
		currentElement = page;
	}

	public void visitList( ListHandle handle )
	{
		// Create ListItem
		ListItemDesign listItem = new ListItemDesign( );
		setupListingItem( listItem, handle );

		// Header
		SlotHandle headerSlot = handle.getHeader( );
		if ( headerSlot.getCount( ) > 0 )
		{
			ListBandDesign header = createListBand( headerSlot );
			header.setBandType( ListBandDesign.BAND_HEADER );
			listItem.setHeader( header );
			listItem.setRepeatHeader( handle.repeatHeader( ) );
		}

		// Multiple groups
		SlotHandle groupsSlot = handle.getGroups( );
		for ( int i = 0; i < groupsSlot.getCount( ); i++ )
		{
			apply( groupsSlot.get( i ) );
			if ( currentElement != null )
			{
				GroupDesign group = (GroupDesign) currentElement;
				group.setGroupLevel(i);
				listItem.addGroup( group );
			}
		}

		// List detail
		SlotHandle detailSlot = handle.getDetail( );
		if ( detailSlot.getCount( ) > 0 )
		{
			ListBandDesign detail = createListBand( detailSlot );
			detail.setBandType( ListBandDesign.BAND_DETAIL );
			listItem.setDetail( detail );
		}

		// List Footer
		SlotHandle footerSlot = handle.getFooter( );
		if ( footerSlot.getCount( ) > 0 )
		{
			ListBandDesign footer = createListBand( footerSlot );
			footer.setBandType( ListBandDesign.BAND_FOOTER );
			listItem.setFooter( footer );
		}

		currentElement = listItem;
	}

	public void visitFreeForm( FreeFormHandle handle )
	{
		// Create Free form element
		FreeFormItemDesign container = new FreeFormItemDesign( );
		setupReportItem( container, handle );

		// Set up each individual item in a free form container
		SlotHandle slot = handle.getReportItems( );
		for ( int i = 0; i < slot.getCount( ); i++ )
		{
			apply( slot.get( i ) );
			if ( currentElement != null )
			{
				container.addItem( (ReportItemDesign) currentElement );
			}
		}
		
		currentElement = container;
	}

	public void visitTextDataItem( TextDataHandle handle )
	{
		DynamicTextItemDesign dynamicTextItem = new DynamicTextItemDesign( );

		setupReportItem( dynamicTextItem, handle );

		String valueExpr = handle.getValueExpr( );
		String contentType = handle.getContentType( );
		dynamicTextItem.setContent( createExpression( valueExpr ) );
		dynamicTextItem.setContentType( contentType );
		setupHighlight( dynamicTextItem, valueExpr );
		setMap( dynamicTextItem, valueExpr );
		
		currentElement = dynamicTextItem;
	}

	public void visitLabel( LabelHandle handle )
	{
		// Create Label Item
		LabelItemDesign labelItem = new LabelItemDesign( );
		setupReportItem( labelItem, handle );

		// Text
		String text = handle.getText( );
		String textKey = handle.getTextKey( );

		labelItem.setText( textKey, text );

		// Handle Action
		ActionHandle action = handle.getActionHandle( );
		if ( action != null )
		{
			labelItem.setAction( createAction( action ) );
		}
		// Fill in help text
		labelItem.setHelpText( handle.getHelpTextKey( ), handle.getHelpText( ) );
		
		currentElement = labelItem;
	}
	
	public void visitAutoText( AutoTextHandle handle )
	{
		AutoTextItemDesign autoTextItem = new AutoTextItemDesign( );
		setupReportItem( autoTextItem, handle );

		autoTextItem.setType(handle.getAutoTextType());
		
		currentElement = autoTextItem;
	}

	public void visitDataItem( DataItemHandle handle )
	{
		// Create data item
		DataItemDesign data = new DataItemDesign( );
		setupReportItem( data, handle );

		// Fill in data expression, 
		//String expr = handle.getValueExpr( );
		String expr = handle.getResultSetColumn( );
		if ( expr != null && expr.trim( ).length( ) > 0 )
		{
			data.setBindingColumn( expr );
		}
		// Handle Action
		ActionHandle action = handle.getActionHandle( );
		if ( action != null )
		{
			data.setAction( createAction( action ) );
		}

		// Fill in help text
		data.setHelpText( handle.getHelpTextKey( ), handle.getHelpText( ) );

		setupHighlight( data, expr );
		setMap( data, expr );
		currentElement = data;
	}

	public void visitGrid( GridHandle handle )
	{
		// Create Grid Item
		GridItemDesign grid = new GridItemDesign( );
		setupReportItem( grid, handle );

		// Handle Columns
		SlotHandle columnSlot = handle.getColumns( );
		for ( int i = 0; i < columnSlot.getCount( ); i++ )
		{
			ColumnHandle columnHandle = (ColumnHandle) columnSlot.get( i );
			apply( columnHandle );
			if ( currentElement != null )
			{
				ColumnDesign columnDesign = (ColumnDesign) currentElement;
				for ( int j = 0; j < columnHandle.getRepeatCount( ); j++ )
				{
					grid.addColumn( columnDesign );
				}
			}
		}

		// Handle Rows
		SlotHandle rowSlot = handle.getRows( );
		for ( int i = 0; i < rowSlot.getCount( ); i++ )
		{
			apply( rowSlot.get( i ) );
			if ( currentElement != null )
			{
				grid.addRow( (RowDesign) currentElement );
			}
		}

		new TableItemDesignLayout( ).layout( grid, newCellId );
		applyColumnHighlight( grid );

		currentElement = grid;
	}

	public void visitImage( ImageHandle handle )
	{
		// Create Image Item
		ImageItemDesign image = new ImageItemDesign( );
		setupReportItem( image, handle );

		// Handle Action
		ActionHandle action = handle.getActionHandle( );
		if ( action != null )
		{
			image.setAction( createAction( action ) );
		}

		// Alternative text for image
		image.setAltText( handle.getAltTextKey( ), handle.getAltText( ) );

		// Help text for image
		image.setHelpText( handle.getHelpTextKey( ), handle.getHelpText( ) );

		// Handle Image Source
		String imageSrc = handle.getSource( );

		if ( EngineIRConstants.IMAGE_REF_TYPE_URL.equals( imageSrc ) )
		{
			image.setImageUri( createExpression( handle.getURL( ) ) );
		}
		else if ( EngineIRConstants.IMAGE_REF_TYPE_EXPR.equals( imageSrc ) )
		{
			String valueExpr = handle.getValueExpression( );
			String typeExpr = handle.getTypeExpression( );
			String imageValue = createExpression( valueExpr );
			String imageType = createExpression( typeExpr );
			image.setImageExpression( imageValue, imageType );
		}
		else if ( EngineIRConstants.IMAGE_REF_TYPE_EMBED.equals( imageSrc ) )
		{
			image.setImageName( handle.getImageName( ) );
		}
		else if ( EngineIRConstants.IMAGE_REF_TYPE_FILE.equals( imageSrc ) )
		{
			image.setImageFile( createExpression( handle.getFile( ) ) );
		}
		else
		{
			assert false;
		}

		currentElement = image;
	}

	public void visitTable( TableHandle handle )
	{
		// Create Table Item
		TableItemDesign table = new TableItemDesign( );
		table.setRepeatHeader( handle.repeatHeader( ) );

		setupListingItem( table, handle );

		// Handle table caption
		String caption = handle.getCaption( );
		String captionKey = handle.getCaptionKey( );
		if ( caption != null || captionKey != null )
		{
			table.setCaption( captionKey, caption );
		}

		// Handle table Columns
		SlotHandle columnSlot = handle.getColumns( );
		for ( int i = 0; i < columnSlot.getCount( ); i++ )
		{
			ColumnHandle columnHandle = (ColumnHandle) columnSlot.get( i );
			apply( columnHandle );
			if ( currentElement != null )
			{
				ColumnDesign columnDesign = (ColumnDesign) currentElement;
				for ( int j = 0; j < columnHandle.getRepeatCount( ); j++ )
				{
					table.addColumn( columnDesign );
				}
			}
		}

		// Handle Table Header
		SlotHandle headerSlot = handle.getHeader( );
		if ( headerSlot.getCount( ) > 0 )
		{
			TableBandDesign header = createTableBand( headerSlot );
			header.setBandType( TableBandDesign.BAND_HEADER );
			table.setHeader( header );
		}

		// Handle grouping in table
		SlotHandle groupSlot = handle.getGroups( );
		for ( int i = 0; i < groupSlot.getCount( ); i++ )
		{
			apply( groupSlot.get( i ) );
			if ( currentElement != null )
			{
				TableGroupDesign group = (TableGroupDesign) currentElement;
				group.setGroupLevel( i );
				table.addGroup( group );
			}
		}

		// Handle detail section
		SlotHandle detailSlot = handle.getDetail( );
		if ( detailSlot.getCount( ) > 0 )
		{
			TableBandDesign detail = createTableBand( detailSlot );
			detail.setBandType( TableBandDesign.BAND_DETAIL );
			table.setDetail( detail );
		}

		// Handle table footer
		SlotHandle footerSlot = handle.getFooter( );
		if ( footerSlot.getCount( ) > 0 )
		{
			TableBandDesign footer = createTableBand( footerSlot );
			footer.setBandType( TableBandDesign.BAND_FOOTER );
			table.setFooter( footer );
		}

		new TableItemDesignLayout( ).layout( table, newCellId );

		for ( int i = 0; i < table.getGroupCount( ); i++ )
		{
			TableGroupDesign group = (TableGroupDesign) table.getGroup( i );
			locateGroupIcon( group );
		}
		
		applyColumnHighlight( table );
		//setup the supressDuplicate property of the data items in the 
		//detail band		
		
		TableBandDesign detail = (TableBandDesign) table.getDetail( );
		if ( detail != null )
		{
			for ( int i = 0; i < detail.getRowCount( ); i++ )
			{
				RowDesign row = detail.getRow( i );
				for ( int j = 0; j < row.getCellCount( ); j++ )
				{
					CellDesign cell = row.getCell( j );
					ColumnDesign column = table.getColumn( cell.getColumn( ) );
					if ( column.getSuppressDuplicate( ) )
					{
						for ( int k = 0; k < cell.getContentCount( ); k++ )
						{
							ReportItemDesign item = cell.getContent( k );
							if ( item instanceof DataItemDesign )
							{
								DataItemDesign dataItem = (DataItemDesign) item;
								dataItem.setSuppressDuplicate( true );
							}
						}
					}
					if ( !column.hasDataItemsInDetail( ) )
					{
						for ( int k = 0; k < cell.getContentCount( ); k++ )
						{
							ReportItemDesign item = cell.getContent( k );
							if ( item instanceof DataItemDesign )
							{
								column.setHasDataItemsInDetail( true );
								break;
							}
						}
					}
				}
			}
		}
		
		currentElement = table;
	}

	private void locateGroupIcon( TableGroupDesign group )
	{
		GroupHandle groupHandle = (GroupHandle)group.getHandle( );
		TableHandle tableHandle = (TableHandle) groupHandle.getContainer( );
		String keyExpression = groupHandle.getKeyExpr();
		if ( keyExpression == null )
		{
			return;
		}
		keyExpression = keyExpression.trim( );
		BandDesign groupHeader = group.getHeader( );
		
		if( group.getHeader( ) == null )
		{
			return;
		}
		
		String columnBindingExpression = getColumnBinding( tableHandle,
				keyExpression );
		for ( int i = 0; i < groupHeader.getContentCount( ); i++ )
		{
			RowDesign row = (RowDesign) groupHeader.getContent( i );
			for ( int j = 0; j < row.getCellCount( ); j++)
			{
				CellDesign cell = row.getCell( j );
				for ( int k = 0; k < cell.getContentCount( ); k++ )
				{
					ReportItemDesign item = cell.getContent( k );
					if ( hasExpression( tableHandle, item, keyExpression,
							columnBindingExpression ) )
					{
						cell.setDisplayGroupIcon( true );
						return;
					}
				}
			}
		}
		// if the group icon hasn't been set, set the icon to the default cell.
		RowDesign row = (RowDesign) groupHeader.getContent( 0 );
		if( null != row )
		{
			CellDesign cell = row.getCell( 0 );
			if( null != cell )
			{
				cell.setDisplayGroupIcon( true );
			}
		}
	}

	private boolean hasExpression( TableHandle tableHandle,
			ReportItemDesign item, String keyExpression,
			String columnBindingExpression )
	{
		assert keyExpression != null;
		if ( item instanceof DataItemDesign )
		{
			DataItemDesign data = (DataItemDesign) item;
			String columnBinding = data.getBindingColumn( );
			String value = ExpressionUtil.createJSRowExpression( columnBinding );
			if ( value != null && keyExpression.equals( value.trim( ) ) )
			{
				return true;
			}
			columnBinding = getColumnBinding( tableHandle, value );
			if ( columnBinding != null && columnBindingExpression != null
					&& columnBindingExpression.equals( columnBinding ) )
			{
				return true;
			}
		}
		if ( item instanceof GridItemDesign )
		{
			GridItemDesign grid = (GridItemDesign) item;
			GridHandle gridHandle = (GridHandle) grid.getHandle( );
			PropertyHandle columnBindings = gridHandle.getColumnBindings( );
			if ( columnBindings != null && columnBindings.iterator( ).hasNext( ) )
			{
				return false;
			}
			for ( int i = 0; i < grid.getRowCount( ); i++ )
			{
				RowDesign row = grid.getRow( i );
				for ( int j = 0; j < row.getCellCount( ); j++ )
				{
					CellDesign cell = row.getCell( j );
					for ( int k = 0; k < cell.getContentCount( ); k++ )
					{
						ReportItemDesign reportItem = cell.getContent( k );
						if ( hasExpression( tableHandle, reportItem,
								keyExpression, columnBindingExpression ) )
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private String getColumnBinding( TableHandle tableHandle, String keyExpression )
	{
		String columnBindingName = null;
		try
		{
			columnBindingName = ExpressionUtil.getColumnBindingName( keyExpression);
		}
		catch ( BirtException e )
		{
			logger.log( Level.FINE, e.getMessage( ), e );
			return null;
		}
		return getColumnBindingByName( tableHandle, columnBindingName );
	}

	private String getColumnBindingByName( TableHandle tableHandle, String columnBindingName )
	{
		if ( columnBindingName == null )
		{
			return null;
		}
		Iterator iterator = tableHandle.columnBindingsIterator( );
		while( iterator.hasNext( ) )
		{
			ComputedColumnHandle columnBinding = (ComputedColumnHandle)iterator.next( );
			if ( columnBindingName.equals( columnBinding.getName( )) )
			{
				return columnBinding.getExpression( );
			}
		}
		return null;
	}

	private void applyColumnHighlight( TableItemDesign table )
	{
		applyColumnHighlight( table, table.getHeader( ) );
		applyColumnHighlight( table, table.getDetail( ) );
		applyColumnHighlight( table, table.getFooter( ) );
		for ( int i = 0; i < table.getGroupCount( ); i++ )
		{
			applyColumnHighlight( table, table.getGroup( i ).getHeader( ) );
			applyColumnHighlight( table, table.getGroup( i ).getFooter( ) );
		}
	}

	private void applyColumnHighlight( GridItemDesign grid )
	{
		for ( int i = 0; i < grid.getRowCount( ); i++ )
		{
			RowDesign row = grid.getRow( i );
			for ( int j = 0; j < row.getCellCount( ); j++ )
			{
				CellDesign cell = row.getCell( j );
				ColumnDesign column = grid.getColumn( cell.getColumn( ) );
				applyColumnHighlight( column, cell );
			}
		}
	}

	private void applyColumnHighlight( TableItemDesign table, BandDesign band )
	{
		if ( band == null )
		{
			return;
		}
		for ( int i = 0; i < band.getContentCount( ); i++ )
		{
			ReportItemDesign content = band.getContent( i ); 
			if ( content instanceof RowDesign)
			{
				RowDesign row = ( RowDesign ) content;
				for ( int j = 0; j < row.getCellCount( ); j++)
				{
					CellDesign cell = row.getCell( j );
					ColumnDesign column = table.getColumn( cell.getColumn( ) );
					applyColumnHighlight( column, cell);
				}
			}
		}
	}

	private void applyColumnHighlight( ColumnDesign column, CellDesign cell )
	{
		HighlightDesign columnHighlight = column.getHighlight( );
		if ( columnHighlight != null && columnHighlight.getRuleCount( ) > 0 )
		{
			HighlightDesign cellHighlight = cell.getHighlight( );
			if ( cellHighlight == null )
			{
				cellHighlight = new HighlightDesign( );
				cell.setHighlight( cellHighlight );
			}
			for ( int i = 0; i < columnHighlight.getRuleCount( ); i++ )
			{
				cellHighlight.addRule( new HighlightRuleDesign( columnHighlight.getRule( i ) ) );
			}
		}
	}

	public void visitColumn( ColumnHandle handle )
	{
		// Create a Column, mostly used in Table or Grid
		ColumnDesign col = new ColumnDesign( );
		setupStyledElement( col, handle );

		// Column Width
		DimensionType width = createDimension( handle.getWidth( ), false );
		col.setWidth( width );
		
		boolean supress = handle.suppressDuplicates( );
		col.setSuppressDuplicate( supress );
		
		// Visibility
		VisibilityDesign visibility = createVisibility( handle
				.visibilityRulesIterator( ) );
		col.setVisibility( visibility );

		setupHighlight( col, null );

		currentElement = col;
	}

	public void visitRow( RowHandle handle )
	{
		// Create a Row, mostly used in Table and Grid Item
		RowDesign row = new RowDesign( );
		setupStyledElement( row, handle );

		// Row Height
		DimensionType height = createDimension( handle.getHeight( ), false );
		row.setHeight( height );

		// Book mark
		String bookmark = handle.getBookmark( );
		row.setBookmark( createExpression( bookmark ) );

		// Visibility
		VisibilityDesign visibility = createVisibility( handle
				.visibilityRulesIterator( ) );
		row.setVisibility( visibility );

		// Cells in a row
		SlotHandle cellSlot = handle.getCells( );
		for ( int i = 0; i < cellSlot.getCount( ); i++ )
		{
			apply( cellSlot.get( i ) );
			if ( currentElement != null )
			{
				row.addCell( (CellDesign) currentElement );
			}
		}

		String onCreate = handle.getOnCreate( );
		row.setOnCreate( createExpression( onCreate ) );
		row.setOnRender( ( (RowHandle) handle ).getOnRender( ) );

		setupHighlight( row, null );
		/*
		 * model hasn't send onPageBreak to us
		row.setOnPageBreak( handle.getOnPageBreak( ) );
		*/
		
		currentElement = row;
	}

	private boolean isContainer( ReportElementHandle handle )
	{
		if ( handle instanceof TextItemHandle )
		{
			return false;
		}
		if ( handle instanceof DataItemHandle )
		{
			return false;
		}
		if ( handle instanceof LabelHandle )
		{
			return false;
		}
		if ( handle instanceof TextDataHandle )
		{
			return false;
		}
		if ( handle instanceof ExtendedItemHandle )
		{
			return false;
		}
		if ( handle instanceof ImageHandle )
		{
			return false;
		}
		return true;

	}

	/**
	 * Sets up cell element's style attribute.
	 * 
	 * @param cell
	 *            engine's styled cell element.
	 * @param handle
	 *            DE's styled cell element.
	 */
	protected void setupStyledElement( StyledElementDesign design,
			ReportElementHandle handle )
	{
		// Styled element is a report element
		setupReportElement( design, handle );

		StyleDeclaration style = createPrivateStyle( handle,
				isContainer( handle ) );
		if ( style != null && !style.isEmpty( ) )
		{
			design.setStyleName( assignStyleName( style ) );
		}
	}

	public void visitCell( CellHandle handle )
	{
		// Create a Cell
		CellDesign cell = new CellDesign( );
		setupStyledElement( cell, handle );

		// Cell contents
		SlotHandle contentSlot = handle.getContent( );
		for ( int i = 0; i < contentSlot.getCount( ); i++ )
		{
			apply( contentSlot.get( i ) );
			if ( currentElement != null )
			{
				cell.addContent( (ReportItemDesign) currentElement );
			}
		}

		// Span, Drop properties of a cell
		// FIXME: change the colspan/rowspan after MODEL fix the bug
		// cell.setColSpan( LayoutUtil.getEffectiveColumnSpan( handle ) );
		cell.setColSpan( handle.getColumnSpan( ) );
		int columnId = handle.getColumn( ) - 1;
		if ( columnId < 0 )
		{
			columnId = -1;
		}
		cell.setColumn( columnId );
		// cell.setRowSpan( LayoutUtil.getEffectiveRowSpan( handle ) );
		cell.setRowSpan( handle.getRowSpan( ) );
		if ( isCellInGroupHeader( handle ) )
		{
			cell.setDrop( handle.getDrop( ) );
		}

		String onCreate = handle.getOnCreate( );
		cell.setOnCreate( createExpression( onCreate ) );
		cell.setOnRender( handle.getOnRender( ) );

		setupHighlight( cell, null );
		/*
		 * model hasn't send onPageBreak to us
		cell.setOnPageBreak( handle.getOnPageBreak( ) );
		*/
		
		currentElement = cell;
	}
	
	private boolean isCellInGroupHeader( CellHandle cellHandle )
	{
		DesignElementHandle rowHandle = cellHandle.getContainer( );
		if ( rowHandle instanceof RowHandle )
		{
			DesignElementHandle groupHandle = rowHandle.getContainer( );
			if ( groupHandle instanceof TableGroupHandle )
			{
				SlotHandle slot = rowHandle.getContainerSlotHandle( );
				if ( slot != null )
				{
					if( slot.getSlotID( ) == GroupHandle.HEADER_SLOT )
					{
						return true;
					}
				}
			}
		}		
		return false;
	}

	/**
	 * create a list band using the items in slot.
	 * 
	 * @param elements
	 *            items in DE's IR
	 * @return ListBand.
	 */
	private ListBandDesign createListBand( SlotHandle elements )
	{
		ListBandDesign band = new ListBandDesign( );
		band.setID( generateUniqueID( ) );

		for ( int i = 0; i < elements.getCount( ); i++ )
		{
			apply( elements.get( i ) );
			if ( currentElement != null )
			{
				band.addContent( (ReportItemDesign) currentElement );
			}
		}

		return band;
	}

	/**
	 * create a list group using the DE's ListGroup.
	 * 
	 * @param handle
	 *            De's list group
	 * @return engine's list group
	 */
	public void visitListGroup( ListGroupHandle handle )
	{
		ListGroupDesign listGroup = new ListGroupDesign( );

		setupGroup( listGroup, handle );

		SlotHandle headerSlot = handle.getHeader( );
		if ( headerSlot.getCount( ) > 0 )
		{
			ListBandDesign header = createListBand( headerSlot );
			header.setBandType( ListBandDesign.GROUP_HEADER );
			header.setGroup( listGroup );
			listGroup.setHeader( header );
			listGroup.setHeaderRepeat( handle.repeatHeader( ) );

			// flatten TOC on group to the first report item in group header
			String toc = handle.getTocExpression( );
			if ( null != toc && !"".equals( toc.trim( ) ) ) //$NON-NLS-1$
			{
				listGroup.setTOC( createExpression( toc ) );
			}
		}

		SlotHandle footerSlot = handle.getFooter( );
		if ( footerSlot.getCount( ) > 0 )
		{
			ListBandDesign footer = createListBand( footerSlot );
			footer.setBandType( ListBandDesign.GROUP_FOOTER );
			footer.setGroup( listGroup );
			listGroup.setFooter( footer );
		}

		boolean hideDetail = handle.hideDetail( );
		listGroup.setHideDetail( hideDetail );
		
		currentElement = listGroup;
	}

	/**
	 * create a table group using the DE's TableGroup.
	 * 
	 * @param handle
	 *            De's table group
	 * @return engine's table group
	 */
	public void visitTableGroup( TableGroupHandle handle )
	{
		TableGroupDesign tableGroup = new TableGroupDesign( );

		setupGroup( tableGroup, handle );

		SlotHandle headerSlot = handle.getHeader( );
		if ( headerSlot.getCount( ) > 0 )
		{
			TableBandDesign header = createTableBand( handle.getHeader( ) );
			header.setBandType( TableBandDesign.GROUP_HEADER );
			header.setGroup( tableGroup );
			tableGroup.setHeader( header );
			tableGroup.setHeaderRepeat( handle.repeatHeader( ) );
			
			// flatten TOC on group to the first report item in group header
			String toc = handle.getTocExpression( );
			if ( null != toc && !"".equals( toc.trim( ) ) ) //$NON-NLS-1$
			{
				tableGroup.setTOC( createExpression( toc ) );
			}
		}

		SlotHandle footerSlot = handle.getFooter( );
		if ( footerSlot.getCount( ) > 0 )
		{
			TableBandDesign footer = createTableBand( handle.getFooter( ) );
			footer.setBandType( TableBandDesign.GROUP_FOOTER );
			footer.setGroup( tableGroup );
			tableGroup.setFooter( footer );
		}
		
		boolean hideDetail = handle.hideDetail( );
		tableGroup.setHideDetail( hideDetail );
		
		currentElement = tableGroup;
	}

	public void visitTextItem( TextItemHandle handle )
	{
		// Create Text Item
		TextItemDesign textItem = new TextItemDesign( );
		setupReportItem( textItem, handle );

		String contentType = handle.getContentType( );
		if ( contentType != null )
		{
			textItem.setTextType( contentType );
		}
		textItem.setText( handle.getContentKey( ), handle.getContent( ) );
		
		currentElement = textItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.model.api.DesignVisitor#visitExtendedItem(org.eclipse.birt.report.model.api.ExtendedItemHandle)
	 */
	protected void visitExtendedItem( ExtendedItemHandle obj )
	{
		ExtendedItemDesign extendedItem = new ExtendedItemDesign( );
		setupReportItem( extendedItem, obj );
		
		// Alternative text for extendedItem
		extendedItem.setAltText( obj.getAltTextKey( ), obj.getAltText( ) );
		
		currentElement = extendedItem;
	}

	public void visitTemplateReportItem( TemplateReportItemHandle obj )
	{
		TemplateDesign template = new TemplateDesign( );
		setupTemplateReportElement( template, obj );
		template.setPromptText( obj.getDescription( ) );
		template.setPromptTextKey( obj.getDescriptionKey( ) );
		template.setAllowedType( obj.getAllowedType( ) );
		
		currentElement = template;
	}

	protected void setupGroup( GroupDesign group, GroupHandle handle )
	{
		// name
		group.setID( handle.getID( ) );
		setupElementIDMap( group );
		group.setName( handle.getName( ) );
		String pageBreakBefore = handle
				.getStringProperty( StyleHandle.PAGE_BREAK_BEFORE_PROP );
		String pageBreakAfter = handle
				.getStringProperty( StyleHandle.PAGE_BREAK_AFTER_PROP );
		String pageBreakInside = handle
		.getStringProperty( StyleHandle.PAGE_BREAK_INSIDE_PROP );
		group.setPageBreakBefore( pageBreakBefore );
		group.setPageBreakAfter( pageBreakAfter );
		group.setPageBreakInside( pageBreakInside );
		
		// TODO: review: group should support OnCreate and OnRender. But model didn't support it now. 
		group.setOnPageBreak( handle.getOnPageBreak( ) );
		
		group.setHandle( handle );
		group.setJavaClass( handle.getEventHandlerClass( ) );
		
	}

	/**
	 * create a table band using the items in slot.
	 * 
	 * @param elements
	 *            items in DE's IR
	 * @return TableBand.
	 */
	private TableBandDesign createTableBand( SlotHandle elements )
	{
		TableBandDesign band = new TableBandDesign( );
		band.setID( generateUniqueID( ) );

		for ( int i = 0; i < elements.getCount( ); i++ )
		{
			apply( elements.get( i ) );
			if ( currentElement != null )
			{
				band.addRow( (RowDesign) currentElement );
			}
		}

		return band;
	}

	/**
	 * Creates the property visibility
	 * 
	 * @param visibilityRulesIterator
	 *            the handle's rules iterator
	 * @return null only if the iterator is null or it contains no rules,
	 *         otherwise VisibilityDesign
	 */
	protected VisibilityDesign createVisibility(
			Iterator visibilityRulesIterator )
	{
		if ( visibilityRulesIterator != null )
		{
			VisibilityDesign visibility = new VisibilityDesign( );
			while ( visibilityRulesIterator.hasNext( ) )
			{
				VisibilityRuleDesign hide = createHide( (HideRuleHandle) visibilityRulesIterator
						.next( ) );
				visibility.addRule( hide );
			}
			if ( visibility.count( ) == 0 )
			{
				return null;
			}
			return visibility;
		}
		return null;
	}

	/**
	 * Creates the visibility rule( i.e. the hide)
	 * 
	 * @param handle
	 *            the DE's handle
	 * @return the created visibility rule
	 */
	protected VisibilityRuleDesign createHide( HideRuleHandle handle )
	{
		VisibilityRuleDesign rule = new VisibilityRuleDesign( );
		rule.setExpression( createExpression( handle.getExpression( ) ) );
		String format = handle.getFormat( );
		if ( "html".equalsIgnoreCase( format ) ) //$NON-NLS-1$
		{
			format = EngineIRConstants.FORMAT_TYPE_VIEWER;
		}
		rule.setFormat( format );
		return rule;
	}

	/**
	 * setup the attribute of report item
	 * 
	 * @param item
	 *            Engine's Report Item
	 * @param handle
	 *            DE's report item.
	 */
	private void setupReportItem( ReportItemDesign item, ReportItemHandle handle )
	{
		setupStyledElement( item, handle );

		// x, y, width & height
		DimensionType height = createDimension( handle.getHeight( ), false );
		DimensionType width = createDimension( handle.getWidth( ), false );
		DimensionType x = createDimension( handle.getX( ), false );
		DimensionType y = createDimension( handle.getY( ), false );
		item.setHeight( height );
		item.setWidth( width );
		item.setX( x );
		item.setY( y );

		// setup TOC expression
		TOCHandle tocHandle = handle.getTOC( );
		if ( tocHandle != null )
		{
			String toc = tocHandle.getExpression( );
			item.setTOC( createExpression( toc ) );
		}

		// setup book mark
		String bookmark = handle.getBookmark( );
		item.setBookmark( createExpression( bookmark ) );

		String onCreate = handle.getOnCreate( );
		item.setOnCreate( createExpression( onCreate ) );

		item.setOnRender( handle.getOnRender( ) );
		
		item.setOnPageBreak( handle.getOnPageBreak( ) );

		// Sets up the visibility
		Iterator visibilityIter = handle.visibilityRulesIterator( );
		VisibilityDesign visibility = createVisibility( visibilityIter );
		item.setVisibility( visibility );
		
		setupHighlight( item, null );
	}

	/**
	 * setup report element attribute
	 * 
	 * @param elem
	 *            engine's report element
	 * @param handle
	 *            DE's report element
	 */
	private void setupReportElement( ReportElementDesign element,
			DesignElementHandle handle )
	{
		element.setHandle( handle );
		element.setName( handle.getName( ) );
		element.setID( handle.getID( ) );
				
		// handle the properties
		List list = handle.getUserProperties( );
		if ( list != null )
		{
			Iterator iter = list.iterator( );
			while ( iter.hasNext( ) )
			{
				UserPropertyDefn propDefn = (UserPropertyDefn) iter.next( );
				String propName = propDefn.getName( );
				PropertyHandle propHandle = handle.getPropertyHandle( propName );
				String propValue = propHandle.getStringValue( );
				element.getCustomProperties( ).put( propName, propValue );
			}
		}

		setupNamedExpressions( handle, element.getNamedExpressions( ) );

		setupElementIDMap( element );
		
		element.setJavaClass( handle.getEventHandlerClass( ) );
	}

	/**
	 * setup template report element attribute
	 * 
	 * @param elem
	 *            engine's template report element
	 * @param handle
	 *            DE's report element
	 */
	private void setupTemplateReportElement( ReportItemDesign element,
			TemplateReportItemHandle handle )
	{
		setupReportElement( element, handle );
		
		// Sets up the visibility
		Iterator visibilityIter = handle.visibilityRulesIterator( );
		VisibilityDesign visibility = createVisibility( visibilityIter );
		element.setVisibility( visibility );
	}

	protected String createExpression( String expr )
	{
		if ( expr != null && !expr.trim( ).equals( "" ) ) //$NON-NLS-1$
		{
			return expr;
		}
		return null;
	}

	/**
	 * create a Action.
	 * 
	 * @param handle
	 *            action in DE
	 * @return action in Engine.
	 */
	protected ActionDesign createAction( ActionHandle handle )
	{
		ActionDesign action = new ActionDesign( );
		String linkType = handle.getLinkType( );
		if ( EngineIRConstants.ACTION_LINK_TYPE_HYPERLINK.equals( linkType ) )
		{

			action.setHyperlink( createExpression( handle.getURI( ) ) );
			action.setTargetWindow( handle.getTargetWindow( ) );
		}
		else if ( EngineIRConstants.ACTION_LINK_TYPE_BOOKMARK_LINK
				.equals( linkType ) )
		{
			action
					.setBookmark( createExpression( handle.getTargetBookmark( ) ) );
		}
		else if ( EngineIRConstants.ACTION_LINK_TYPE_DRILL_THROUGH
				.equals( linkType ) )
		{
			action.setTargetWindow( handle.getTargetWindow( ) );
			action.setTargetFileType( handle.getTargetFileType( ) );
			DrillThroughActionDesign drillThrough = new DrillThroughActionDesign( );
			action.setDrillThrough( drillThrough );

			drillThrough.setReportName( handle.getReportName( ) );
			drillThrough.setFormat( handle.getFormatType( ) );
			drillThrough.setBookmark( createExpression( handle
					.getTargetBookmark( ) ) );
			drillThrough.setBookmarkType( !DesignChoiceConstants.ACTION_BOOKMARK_TYPE_TOC 
					.equals( handle.getTargetBookmarkType( ) ));
			Map params = new HashMap( );
			Iterator paramIte = handle.paramBindingsIterator( );
			while ( paramIte.hasNext( ) )
			{
				ParamBindingHandle member = (ParamBindingHandle) paramIte
						.next( );
				params.put( member.getParamName( ), createExpression( member
						.getExpression( ) ) );
			}
			drillThrough.setParameters( params );
			// XXX Search criteria is not supported yet.
			// Map search = new HashMap( );
			// Iterator searchIte = handle.searchIterator( );
			// while ( searchIte.hasNext( ) )
			// {
			// SearchKeyHandle member = (SearchKeyHandle) paramIte.next( );
			// params
			// .put( member., member
			// .getValue( ) );
			// }
			// drillThrough.setSearch( search );

		}
		else
		{
			assert ( false );
		}

		return action;
	}

	/**
	 * create a highlight rule from a structure handle.
	 * 
	 * @param ruleHandle
	 *            rule in the MODEL.
	 * @return rule design, null if exist any error.
	 */
	protected HighlightRuleDesign createHighlightRule(
			StructureHandle ruleHandle, String defaultStr )
	{
		HighlightRuleDesign rule = new HighlightRuleDesign( );

		MemberHandle hOperator = ruleHandle
				.getMember( HighlightRule.OPERATOR_MEMBER );
		MemberHandle hValue1 = ruleHandle
				.getMember( HighlightRule.VALUE1_MEMBER );
		MemberHandle hValue2 = ruleHandle
				.getMember( HighlightRule.VALUE2_MEMBER );
		MemberHandle hTestExpr = ruleHandle
				.getMember( HighlightRule.TEST_EXPR_MEMBER );

		String oper = hOperator.getStringValue( );
		String value1 = hValue1.getStringValue( );
		String value2 = hValue2.getStringValue( );
		String testExpr = hTestExpr.getStringValue( );

		rule.setExpression( oper, value1, value2 );
		if ( testExpr != null && testExpr.length( ) > 0 )
		{
			rule.setTestExpression( testExpr );
		}
		else if ( ( defaultStr != null ) && defaultStr.length( ) > 0 )
		{
			rule.setTestExpression( defaultStr );
		}
		else
		{
			// test expression is null
			return null;
		}

		// all other properties are style properties,
		// copy those properties into a style design.
		StyleDeclaration style = new StyleDeclaration( cssEngine );

		setupStyle( ruleHandle, style );

		// this rule is empty, so we can drop it safely.
		if ( style.isEmpty( ) )
		{
			return null;
		}
		rule.setStyle( style );
		return rule;
	}

	/**
	 * create highlight defined in the handle.
	 * 
	 * @param item
	 *            styled item.
	 */
	protected void setupHighlight( StyledElementDesign item, String defaultStr )
	{
		StyleHandle handle = item.getHandle( ).getPrivateStyle( );
		if ( handle == null )
		{
			return;
		}
		// hightlight Rules
		Iterator iter = handle.highlightRulesIterator( );

		if ( iter == null )
		{
			return;
		}
		HighlightDesign highlight = new HighlightDesign( );

		while ( iter.hasNext( ) )
		{
			HighlightRuleHandle ruleHandle = (HighlightRuleHandle) iter.next( );
			HighlightRuleDesign rule = createHighlightRule( ruleHandle,
					defaultStr );
			if ( rule != null )
			{
				highlight.addRule( rule );
			}
		}

		if ( highlight.getRuleCount( ) > 0 )
		{
			item.setHighlight( highlight );
		}
	}

	/**
	 * setup a Map.
	 * 
	 * @param item
	 *            styled item;
	 */
	protected void setMap( StyledElementDesign item, String defaultStr )
	{
		StyleHandle handle = item.getHandle( ).getPrivateStyle( );
		if ( handle == null )
		{
			return;
		}
		Iterator iter = handle.mapRulesIterator( );
		if ( iter == null )
		{
			return;
		}
		MapDesign map = new MapDesign( );

		while ( iter.hasNext( ) )
		{
			MapRuleHandle ruleHandle = (MapRuleHandle) iter.next( );
			MapRuleDesign rule = createMapRule( ruleHandle, defaultStr );
			if ( rule != null )
			{
				map.addRule( rule );
			}
		}

		if ( map.getRuleCount( ) > 0 )
		{
			item.setMap( map );
		}

	}

	/**
	 * create a map rule.
	 * 
	 * @param obj
	 *            map rule in DE.
	 * @return map rule in ENGINE.
	 */
	protected MapRuleDesign createMapRule( MapRuleHandle handle,
			String defaultStr )
	{
		MapRuleDesign rule = new MapRuleDesign( );
		rule.setExpression( handle.getOperator( ), handle.getValue1( ), handle
				.getValue2( ) );
		String displayText = handle.getDisplay( );
		rule.setDisplayText( handle.getDisplayKey( ), displayText == null
				? "" //$NON-NLS-1$
				: displayText );

		String testExpr = handle.getTestExpression( );
		if ( testExpr != null && testExpr.length( ) > 0 )
		{
			rule.setTestExpression( testExpr );
		}
		else if ( ( defaultStr != null ) && defaultStr.length( ) > 0 )
		{
			rule.setTestExpression( defaultStr );
		}
		else
		{
			// test expression is null
			return null;
		}

		return rule;
	}

	/**
	 * Checks if a given style is in report's style list, if not, assign a
	 * unique name to it and then add it to the style list.
	 * 
	 * @param style
	 *            The <code>StyleDeclaration</code> object.
	 * @return the name of the style.
	 */
	private String assignStyleName( StyleDeclaration style )
	{
		if ( style == null || style.isEmpty( ) )
		{
			return null;
		}

		// Check if the style is already in report's style list
		Map styles = report.getStyles( );
		Iterator iter = styles.entrySet( ).iterator( );
		while ( iter.hasNext( ) )
		{
			Map.Entry entry = (Map.Entry) iter.next( );
			// Cast the type mandatorily
			StyleDeclaration cachedStyle = (StyleDeclaration) entry.getValue( );
			if ( cachedStyle.equals( style ) )
			{
				// There exist a style which has same properties with this
				// one,
				style = cachedStyle;
				return (String) entry.getKey( );
			}
		}		

		// the style is a new style, we need create a unique name for
		// it, and
		// add it into the report's style list.
		String styleName = PREFIX_STYLE_NAME + styles.size( );
		report.addStyle( styleName, style );
		return styleName;
	}

	protected String getElementProperty( ReportElementHandle handle, String name )
	{
		return getElementProperty( handle, name, false );
	}

	protected String getElementProperty( ReportElementHandle handle,
			String name, boolean isColorProperty )
	{
		FactoryPropertyHandle prop = handle.getFactoryPropertyHandle( name );
		if ( prop != null && prop.isSet( ) )
		{
			if ( isColorProperty )
			{
				return prop.getColorValue( );
			}

			return prop.getStringValue( );
		}
		return null;
	}

	String getElementColorProperty( ReportElementHandle handle, String name )
	{
		FactoryPropertyHandle prop = handle.getFactoryPropertyHandle( name );
		if ( prop != null && prop.isSet( ) )
		{
			return prop.getColorValue( );
		}
		return null;
	}

	protected StyleDeclaration createPrivateStyle( ReportElementHandle handle )
	{
		return createPrivateStyle( handle, true );
	}

	protected String decodePageBreak( String pageBreak )
	{
		if ( pageBreak == null )
		{
			return null;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_AFTER_ALWAYS.equals( pageBreak ) )
		{
			return IStyle.CSS_ALWAYS_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_AFTER_ALWAYS_EXCLUDING_LAST
				.equals( pageBreak ) )
		{
			return IStyle.CSS_ALWAYS_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_AFTER_AUTO.equals( pageBreak ) )
		{
			return IStyle.CSS_AUTO_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_AFTER_AVOID.equals( pageBreak ) )
		{
			return IStyle.CSS_AVOID_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_BEFORE_ALWAYS.equals( pageBreak ) )
		{
			return IStyle.CSS_ALWAYS_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_BEFORE_ALWAYS_EXCLUDING_FIRST
				.equals( pageBreak ) )
		{
			return IStyle.CSS_ALWAYS_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_BEFORE_AUTO.equals( pageBreak ) )
		{
			return IStyle.CSS_AUTO_VALUE;
		}
		if ( DesignChoiceConstants.PAGE_BREAK_BEFORE_AVOID.equals( pageBreak ) )
		{
			return IStyle.CSS_AVOID_VALUE;
		}
		return IStyle.CSS_AUTO_VALUE;
	}

	protected StyleDeclaration createPrivateStyle( ReportElementHandle handle,
			boolean isContainer )
	{
		// Background
		StyleDeclaration style = new StyleDeclaration( cssEngine );

		style.setBackgroundColor( getElementProperty( handle,
				Style.BACKGROUND_COLOR_PROP, true ) );
		style.setBackgroundImage( getElementProperty( handle,
				Style.BACKGROUND_IMAGE_PROP ) );
		style.setBackgroundPositionX( getElementProperty( handle,
				Style.BACKGROUND_POSITION_X_PROP ) );
		style.setBackgroundPositionY( getElementProperty( handle,
				Style.BACKGROUND_POSITION_Y_PROP ) );
		style.setBackgroundRepeat( getElementProperty( handle,
				Style.BACKGROUND_REPEAT_PROP ) );

		// Text related
		style
				.setTextAlign( getElementProperty( handle,
						Style.TEXT_ALIGN_PROP ) );
		style
				.setTextIndent( getElementProperty( handle,
						Style.TEXT_INDENT_PROP ) );

			style.setTextUnderline( getElementProperty( handle,
					Style.TEXT_UNDERLINE_PROP ) );

			style.setTextLineThrough( getElementProperty( handle,
					Style.TEXT_LINE_THROUGH_PROP ) );
			style.setTextOverline( getElementProperty( handle,
					Style.TEXT_OVERLINE_PROP ) );
		
		style.setLetterSpacing( getElementProperty( handle,
				Style.LETTER_SPACING_PROP ) );
		style
				.setLineHeight( getElementProperty( handle,
						Style.LINE_HEIGHT_PROP ) );
		style.setOrphans( getElementProperty( handle, Style.ORPHANS_PROP ) );
		style.setTextTransform( getElementProperty( handle,
				Style.TEXT_TRANSFORM_PROP ) );
		style.setVerticalAlign( getElementProperty( handle,
				Style.VERTICAL_ALIGN_PROP ) );
		style
				.setWhiteSpace( getElementProperty( handle,
						Style.WHITE_SPACE_PROP ) );
		style.setWidows( getElementProperty( handle, Style.WIDOWS_PROP ) );
		style.setWordSpacing( getElementProperty( handle,
				Style.WORD_SPACING_PROP ) );

		// Section properties
		style.setDisplay( getElementProperty( handle, Style.DISPLAY_PROP ) );
		style
				.setMasterPage( getElementProperty( handle,
						Style.MASTER_PAGE_PROP ) );
		String pageBreakAfter = getElementProperty(handle, StyleHandle.PAGE_BREAK_AFTER_PROP);
		style.setPageBreakAfter( decodePageBreak(pageBreakAfter) );
		String pageBreakBefore = getElementProperty( handle,
				StyleHandle.PAGE_BREAK_BEFORE_PROP );
		style.setPageBreakBefore( decodePageBreak(pageBreakBefore) );
		 
		style.setPageBreakInside( getElementProperty( handle,
				Style.PAGE_BREAK_INSIDE_PROP ) );

		// Font related
		style
				.setFontFamily( getElementProperty( handle,
						Style.FONT_FAMILY_PROP ) );
		style.setColor( getElementProperty( handle, Style.COLOR_PROP, true ) );
		style.setFontSize( getElementProperty( handle, Style.FONT_SIZE_PROP ) );
		style
				.setFontStyle( getElementProperty( handle,
						Style.FONT_STYLE_PROP ) );
		style
				.setFontWeight( getElementProperty( handle,
						Style.FONT_WEIGHT_PROP ) );
		style.setFontVariant( getElementProperty( handle,
				Style.FONT_VARIANT_PROP ) );

		// Border
		style.setBorderBottomColor( getElementProperty( handle,
				Style.BORDER_BOTTOM_COLOR_PROP, true ) );
		style.setBorderBottomStyle( getElementProperty( handle,
				Style.BORDER_BOTTOM_STYLE_PROP ) );
		style.setBorderBottomWidth( getElementProperty( handle,
				Style.BORDER_BOTTOM_WIDTH_PROP ) );
		style.setBorderLeftColor( getElementProperty( handle,
				Style.BORDER_LEFT_COLOR_PROP, true ) );
		style.setBorderLeftStyle( getElementProperty( handle,
				Style.BORDER_LEFT_STYLE_PROP ) );
		style.setBorderLeftWidth( getElementProperty( handle,
				Style.BORDER_LEFT_WIDTH_PROP ) );
		style.setBorderRightColor( getElementProperty( handle,
				Style.BORDER_RIGHT_COLOR_PROP, true ) );
		style.setBorderRightStyle( getElementProperty( handle,
				Style.BORDER_RIGHT_STYLE_PROP ) );
		style.setBorderRightWidth( getElementProperty( handle,
				Style.BORDER_RIGHT_WIDTH_PROP ) );
		style.setBorderTopColor( getElementProperty( handle,
				Style.BORDER_TOP_COLOR_PROP, true ) );
		style.setBorderTopStyle( getElementProperty( handle,
				Style.BORDER_TOP_STYLE_PROP ) );
		style.setBorderTopWidth( getElementProperty( handle,
				Style.BORDER_TOP_WIDTH_PROP ) );

		// Margin
		style
				.setMarginTop( getElementProperty( handle,
						Style.MARGIN_TOP_PROP ) );
		style
				.setMarginLeft( getElementProperty( handle,
						Style.MARGIN_LEFT_PROP ) );
		style.setMarginBottom( getElementProperty( handle,
				Style.MARGIN_BOTTOM_PROP ) );
		style.setMarginRight( getElementProperty( handle,
				Style.MARGIN_RIGHT_PROP ) );

		// Padding
		style
				.setPaddingTop( getElementProperty( handle,
						Style.PADDING_TOP_PROP ) );
		style.setPaddingLeft( getElementProperty( handle,
				Style.PADDING_LEFT_PROP ) );
		style.setPaddingBottom( getElementProperty( handle,
				Style.PADDING_BOTTOM_PROP ) );
		style.setPaddingRight( getElementProperty( handle,
				Style.PADDING_RIGHT_PROP ) );

		// Data Formatting
		style.setNumberAlign( getElementProperty( handle,
				Style.NUMBER_ALIGN_PROP ) );
		style.setDateFormat( getElementProperty( handle,
				Style.DATE_TIME_FORMAT_PROP ) );
		style.setNumberFormat( getElementProperty( handle,
				Style.NUMBER_FORMAT_PROP ) );
		style.setStringFormat( getElementProperty( handle,
				Style.STRING_FORMAT_PROP ) );

		// Others
		style
				.setCanShrink( getElementProperty( handle,
						Style.CAN_SHRINK_PROP ) );
		style.setShowIfBlank( getElementProperty( handle,
				Style.SHOW_IF_BLANK_PROP ) );

		return style;

	}

	String getMemberProperty( StructureHandle handle, String name )
	{
		MemberHandle prop = handle.getMember( name );
		if ( prop != null )
		{
			return prop.getStringValue( );
		}
		return null;
	}

	IStyle setupStyle( StructureHandle highlight, IStyle style )
	{
		// Background
		style.setBackgroundColor( getMemberProperty( highlight,
				HighlightRule.BACKGROUND_COLOR_MEMBER ) );
		// style.setBackgroundPositionX(getMemberProperty(highlight,
		// HighlightRule.BACKGROUND_POSITION_X_MEMBER));
		// style.setBackgroundPositionY(getMemberProperty(highlight,
		// HighlightRule.BACKGROUND_POSITION_Y_MEMBER));
		// style.setBackgroundRepeat(getMemberProperty(highlight,
		// HighlightRule.BACKGROUND_REPEAT_MEMBER));

		// Text related
		style.setTextAlign( getMemberProperty( highlight,
				HighlightRule.TEXT_ALIGN_MEMBER ) );
		style.setTextIndent( getMemberProperty( highlight,
				HighlightRule.TEXT_INDENT_MEMBER ) );
		style.setTextUnderline( getMemberProperty( highlight,
				Style.TEXT_UNDERLINE_PROP ) );
		style.setTextLineThrough( getMemberProperty( highlight,
				Style.TEXT_LINE_THROUGH_PROP ) );
		style.setTextOverline( getMemberProperty( highlight,
				Style.TEXT_OVERLINE_PROP ) );
		// style.setLetterSpacing(getMemberProperty(highlight,
		// HighlightRule.LETTER_SPACING_MEMBER));
		// style.setLineHeight(getMemberProperty(highlight,
		// HighlightRule.LINE_HEIGHT_MEMBER));
		// style.setOrphans(getMemberProperty(highlight,
		// HighlightRule.ORPHANS_MEMBER));
		style.setTextTransform( getMemberProperty( highlight,
				HighlightRule.TEXT_TRANSFORM_MEMBER ) );
		// style.setVerticalAlign(getMemberProperty(highlight,
		// HighlightRule.VERTICAL_ALIGN_MEMBER));
		// style.setWhiteSpace(getMemberProperty(highlight,
		// HighlightRule.WHITE_SPACE_MEMBER));
		// style.setWidows(getMemberProperty(highlight,
		// HighlightRule.WIDOWS_MEMBER));
		// style.setWordSpacing(getMemberProperty(highlight,
		// HighlightRule.WORD_SPACING_MEMBER));

		// Section properties
		// style.setDisplay(getMemberProperty(highlight,
		// HighlightRule.DISPLAY_MEMBER));
		// style.setMasterPage(getMemberProperty(highlight,
		// HighlightRule.MASTER_PAGE_MEMBER));
		// style.setPageBreakAfter(getMemberProperty(highlight,
		// HighlightRule.PAGE_BREAK_AFTER_MEMBER));
		// style.setPageBreakBefore(getMemberProperty(highlight,
		// HighlightRule.PAGE_BREAK_BEFORE_MEMBER));
		// style.setPageBreakInside(getMemberProperty(highlight,
		// HighlightRule.PAGE_BREAK_INSIDE_MEMBER));

		// Font related
		style.setFontFamily( getMemberProperty( highlight,
				HighlightRule.FONT_FAMILY_MEMBER ) );
		style.setColor( getMemberProperty( highlight,
				HighlightRule.COLOR_MEMBER ) );
		style.setFontSize( getMemberProperty( highlight,
				HighlightRule.FONT_SIZE_MEMBER ) );
		style.setFontStyle( getMemberProperty( highlight,
				HighlightRule.FONT_STYLE_MEMBER ) );
		style.setFontWeight( getMemberProperty( highlight,
				HighlightRule.FONT_WEIGHT_MEMBER ) );
		style.setFontVariant( getMemberProperty( highlight,
				HighlightRule.FONT_VARIANT_MEMBER ) );

		// Border
		style.setBorderBottomColor( getMemberProperty( highlight,
				HighlightRule.BORDER_BOTTOM_COLOR_MEMBER ) );
		style.setBorderBottomStyle( getMemberProperty( highlight,
				HighlightRule.BORDER_BOTTOM_STYLE_MEMBER ) );
		style.setBorderBottomWidth( getMemberProperty( highlight,
				HighlightRule.BORDER_BOTTOM_WIDTH_MEMBER ) );
		style.setBorderLeftColor( getMemberProperty( highlight,
				HighlightRule.BORDER_LEFT_COLOR_MEMBER ) );
		style.setBorderLeftStyle( getMemberProperty( highlight,
				HighlightRule.BORDER_LEFT_STYLE_MEMBER ) );
		style.setBorderLeftWidth( getMemberProperty( highlight,
				HighlightRule.BORDER_LEFT_WIDTH_MEMBER ) );
		style.setBorderRightColor( getMemberProperty( highlight,
				HighlightRule.BORDER_RIGHT_COLOR_MEMBER ) );
		style.setBorderRightStyle( getMemberProperty( highlight,
				HighlightRule.BORDER_RIGHT_STYLE_MEMBER ) );
		style.setBorderRightWidth( getMemberProperty( highlight,
				HighlightRule.BORDER_RIGHT_WIDTH_MEMBER ) );
		style.setBorderTopColor( getMemberProperty( highlight,
				HighlightRule.BORDER_TOP_COLOR_MEMBER ) );
		style.setBorderTopStyle( getMemberProperty( highlight,
				HighlightRule.BORDER_TOP_STYLE_MEMBER ) );
		style.setBorderTopWidth( getMemberProperty( highlight,
				HighlightRule.BORDER_TOP_WIDTH_MEMBER ) );

		// Margin
		// style.setMarginTop(getMemberProperty(highlight,
		// HighlightRule.MARGIN_TOP_MEMBER));
		// style.setMarginLeft(getMemberProperty(highlight,
		// HighlightRule.MARGIN_LEFT_MEMBER));
		// style.setMarginBottom(getMemberProperty(highlight,
		// HighlightRule.MARGIN_BOTTOM_MEMBER));
		// style.setMarginRight(getMemberProperty(highlight,
		// HighlightRule.MARGIN_RIGHT_MEMBER));

		// Padding
		// style.setPaddingTop(getMemberProperty(highlight,
		// HighlightRule.PADDING_TOP_MEMBER));
		// style.setPaddingLeft(getMemberProperty(highlight,
		// HighlightRule.PADDING_LEFT_MEMBER));
		// style.setPaddingBottom(getMemberProperty(highlight,
		// HighlightRule.PADDING_BOTTOM_MEMBER));
		// style.setPaddingRight(getMemberProperty(highlight,
		// HighlightRule.PADDING_RIGHT_MEMBER));

		// Data Formatting
		style.setNumberAlign( getMemberProperty( highlight,
				HighlightRule.NUMBER_ALIGN_MEMBER ) );
		style.setDateFormat( getMemberProperty( highlight,
				HighlightRule.DATE_TIME_FORMAT_MEMBER ) );
		style.setNumberFormat( getMemberProperty( highlight,
				HighlightRule.NUMBER_FORMAT_MEMBER ) );
		style.setStringFormat( getMemberProperty( highlight,
				HighlightRule.STRING_FORMAT_MEMBER ) );

		// Others
		// style.setCanShrink(getMemberProperty(highlight,
		// HighlightRule.CAN_SHRINK_MEMBER));
		// style.setShowIfBlank(getMemberProperty(highlight,
		// HighlightRule.SHOW_IF_BLANK_MEMBER));

		return style;
	}

	
	
	protected DimensionType createDimension( DimensionHandle handle, boolean useDefault )
	{
		if ( handle == null  || !useDefault && !handle.isSet( ))
		{
			return null;
		}
		// Extended Choice
		if ( handle.isKeyword( ) )
		{
			return new DimensionType( handle.getStringValue( ) );
		}
		// set measure and unit
		double measure = handle.getMeasure( );
		String unit = handle.getUnits( );
		return new DimensionType( measure, unit );
	}

	protected void setupListingItem( ListingDesign listing, ListingHandle handle )
	{
		// setup related scripts
		setupReportItem( listing, handle );

		listing.setPageBreakInterval( handle.getPageBreakInterval( ) );
		// setup scripts
		// listing.setOnStart( handle.getOnStart( ) );
		// listing.setOnRow( handle.getOnRow( ) );
		// listing.setOnFinish( handle.getOnFinish( ) );
	}

	protected void addReportDefaultPropertyValue( String name,
			StyleHandle handle )
	{
		addReportDefaultPropertyValue( name, handle, false );
	}

	protected void addReportDefaultPropertyValue( String name,
			StyleHandle handle, boolean isColorProperty )
	{
		Object value = null;
		int index = StylePropertyMapping.getPropertyID( name );

		if ( StylePropertyMapping.canInherit( name ) )
		{
			if ( handle != null )
			{
				if ( isColorProperty )
				{
					value = handle.getColorProperty( name ).getStringValue( );
				}
				else
				{
					value = handle.getProperty( name );
				}
			}
			if ( value == null )
			{
				value = StylePropertyMapping.getDefaultValue( name );
			}

			inheritableReportStyle.setCssText( index, value == null
					? null
					: value.toString( ) );
		}
		else
		{
			value = StylePropertyMapping.getDefaultValue( name );
			nonInheritableReportStyle.setCssText( index, value == null
					? null
					: value.toString( ) );
		}

	}

	/**
	 * Creates Report default styles
	 */
	protected void createReportDefaultStyles( StyleHandle handle )
	{
		nonInheritableReportStyle = new StyleDeclaration( cssEngine );
		inheritableReportStyle = new StyleDeclaration( cssEngine );

		// Background
		addReportDefaultPropertyValue( Style.BACKGROUND_COLOR_PROP, handle,
				true );
		addReportDefaultPropertyValue( Style.BACKGROUND_IMAGE_PROP, handle );
		addReportDefaultPropertyValue( Style.BACKGROUND_POSITION_X_PROP, handle );
		addReportDefaultPropertyValue( Style.BACKGROUND_POSITION_Y_PROP, handle );
		addReportDefaultPropertyValue( Style.BACKGROUND_REPEAT_PROP, handle );

		// Text related
		addReportDefaultPropertyValue( Style.TEXT_ALIGN_PROP, handle );
		addReportDefaultPropertyValue( Style.TEXT_INDENT_PROP, handle );
		addReportDefaultPropertyValue( Style.LETTER_SPACING_PROP, handle );
		addReportDefaultPropertyValue( Style.LINE_HEIGHT_PROP, handle );
		addReportDefaultPropertyValue( Style.ORPHANS_PROP, handle );
		addReportDefaultPropertyValue( Style.TEXT_TRANSFORM_PROP, handle );
		addReportDefaultPropertyValue( Style.VERTICAL_ALIGN_PROP, handle );
		addReportDefaultPropertyValue( Style.WHITE_SPACE_PROP, handle );
		addReportDefaultPropertyValue( Style.WIDOWS_PROP, handle );
		addReportDefaultPropertyValue( Style.WORD_SPACING_PROP, handle );

		// Section properties
		addReportDefaultPropertyValue( Style.DISPLAY_PROP, handle );
		addReportDefaultPropertyValue( Style.MASTER_PAGE_PROP, handle );
		addReportDefaultPropertyValue( Style.PAGE_BREAK_AFTER_PROP, handle );
		addReportDefaultPropertyValue( Style.PAGE_BREAK_BEFORE_PROP, handle );
		addReportDefaultPropertyValue( Style.PAGE_BREAK_INSIDE_PROP, handle );

		// Font related
		addReportDefaultPropertyValue( Style.FONT_FAMILY_PROP, handle );
		addReportDefaultPropertyValue( Style.COLOR_PROP, handle, true );
		addReportDefaultPropertyValue( Style.FONT_SIZE_PROP, handle );
		addReportDefaultPropertyValue( Style.FONT_STYLE_PROP, handle );
		addReportDefaultPropertyValue( Style.FONT_WEIGHT_PROP, handle );
		addReportDefaultPropertyValue( Style.FONT_VARIANT_PROP, handle );

		// Text decoration
		addReportDefaultPropertyValue( Style.TEXT_LINE_THROUGH_PROP, handle );
		addReportDefaultPropertyValue( Style.TEXT_OVERLINE_PROP, handle );
		addReportDefaultPropertyValue( Style.TEXT_UNDERLINE_PROP, handle );

		// Border
		addReportDefaultPropertyValue( Style.BORDER_BOTTOM_COLOR_PROP, handle,
				true );
		addReportDefaultPropertyValue( Style.BORDER_BOTTOM_STYLE_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_BOTTOM_WIDTH_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_LEFT_COLOR_PROP, handle,
				true );
		addReportDefaultPropertyValue( Style.BORDER_LEFT_STYLE_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_LEFT_WIDTH_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_RIGHT_COLOR_PROP, handle,
				true );
		addReportDefaultPropertyValue( Style.BORDER_RIGHT_STYLE_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_RIGHT_WIDTH_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_TOP_COLOR_PROP, handle,
				true );
		addReportDefaultPropertyValue( Style.BORDER_TOP_STYLE_PROP, handle );
		addReportDefaultPropertyValue( Style.BORDER_TOP_WIDTH_PROP, handle );

		// Margin
		addReportDefaultPropertyValue( Style.MARGIN_TOP_PROP, handle );
		addReportDefaultPropertyValue( Style.MARGIN_LEFT_PROP, handle );
		addReportDefaultPropertyValue( Style.MARGIN_BOTTOM_PROP, handle );
		addReportDefaultPropertyValue( Style.MARGIN_RIGHT_PROP, handle );

		// Padding
		addReportDefaultPropertyValue( Style.PADDING_TOP_PROP, handle );
		addReportDefaultPropertyValue( Style.PADDING_LEFT_PROP, handle );
		addReportDefaultPropertyValue( Style.PADDING_BOTTOM_PROP, handle );
		addReportDefaultPropertyValue( Style.PADDING_RIGHT_PROP, handle );

		//Format
		addReportDefaultPropertyValue( Style.STRING_FORMAT_PROP, handle );
		addReportDefaultPropertyValue( Style.NUMBER_FORMAT_PROP, handle );
		addReportDefaultPropertyValue( Style.DATE_TIME_FORMAT_PROP, handle );
		
		report.setRootStyleName( assignStyleName( inheritableReportStyle ) );

	}

	/**
	 * Creates the body style for master page.
	 * 
	 * @param design
	 *            the master page design
	 * @return the content style
	 */
	protected String setupBodyStyle( MasterPageDesign design )
	{
		String styleName = design.getStyleName( );
		IStyle style = report.findStyle( styleName );
		if ( style == null || style.isEmpty( ) )
		{
			return null;
		}

		StyleDeclaration contentStyle = new StyleDeclaration( cssEngine );
		contentStyle.setProperty( IStyle.STYLE_BACKGROUND_COLOR, style
				.getProperty( IStyle.STYLE_BACKGROUND_COLOR ) );
		contentStyle.setProperty( IStyle.STYLE_BACKGROUND_IMAGE, style
				.getProperty( IStyle.STYLE_BACKGROUND_IMAGE ) );
		contentStyle.setProperty( IStyle.STYLE_BACKGROUND_POSITION_Y, style
				.getProperty( IStyle.STYLE_BACKGROUND_POSITION_Y ) );
		contentStyle.setProperty( IStyle.STYLE_BACKGROUND_POSITION_X, style
				.getProperty( IStyle.STYLE_BACKGROUND_POSITION_X ) );
		contentStyle.setProperty( IStyle.STYLE_BACKGROUND_REPEAT, style
				.getProperty( IStyle.STYLE_BACKGROUND_REPEAT ) );

		String bodyStyleName = assignStyleName( contentStyle );
		return bodyStyleName;
	}

	private void setupElementIDMap( ReportElementDesign rptElement )
	{
		report.setReportItemInstanceID( rptElement.getID( ), rptElement );
	}
	
	protected long generateUniqueID( )
	{
		newCellId = newCellId - 1;
		return newCellId;
	}
}
