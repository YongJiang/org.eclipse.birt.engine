/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/
package org.eclipse.birt.report.engine.layout.pdf.util;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.birt.report.engine.content.IContainerContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IForeignContent;
import org.eclipse.birt.report.engine.content.IImageContent;
import org.eclipse.birt.report.engine.content.ILabelContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.content.impl.ActionContent;
import org.eclipse.birt.report.engine.content.impl.ContainerContent;
import org.eclipse.birt.report.engine.content.impl.ImageContent;
import org.eclipse.birt.report.engine.content.impl.LabelContent;
import org.eclipse.birt.report.engine.content.impl.ReportContent;
import org.eclipse.birt.report.engine.content.impl.TextContent;
import org.eclipse.birt.report.engine.css.dom.StyleDeclaration;
import org.eclipse.birt.report.engine.css.engine.value.css.CSSValueConstants;
import org.eclipse.birt.report.engine.ir.DimensionType;
import org.eclipse.birt.report.engine.ir.EngineIRConstants;
import org.eclipse.birt.report.engine.parser.TextParser;
import org.eclipse.birt.report.engine.util.FileUtil;
import org.eclipse.birt.report.model.api.IResourceLocator;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSValue;

public class HTML2Content
{
	protected static final HashSet htmlDisplayMode = new HashSet( );
	
	protected static final HashMap textTypeMapping = new HashMap( );

	static
	{
		// block-level elements
		htmlDisplayMode.add( "dd" ); //$NON-NLS-1$
		htmlDisplayMode.add( "div" ); //$NON-NLS-1$
		htmlDisplayMode.add( "dl" ); //$NON-NLS-1$
		htmlDisplayMode.add( "dt" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h1" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h2" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h3" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h4" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h5" ); //$NON-NLS-1$
		htmlDisplayMode.add( "h6" ); //$NON-NLS-1$
		htmlDisplayMode.add( "hr" ); //$NON-NLS-1$
		htmlDisplayMode.add( "ol" ); //$NON-NLS-1$
		htmlDisplayMode.add( "p" ); //$NON-NLS-1$
		htmlDisplayMode.add( "pre" ); //$NON-NLS-1$
		htmlDisplayMode.add( "ul" ); //$NON-NLS-1$
		htmlDisplayMode.add( "li" ); //$NON-NLS-1$
		htmlDisplayMode.add( "address" ); //$NON-NLS-1$
		htmlDisplayMode.add( "body" ); //$NON-NLS-1$
		htmlDisplayMode.add( "center" ); //$NON-NLS-1$
		htmlDisplayMode.add( "table" ); //$NON-NLS-1$

		textTypeMapping.put( IForeignContent.HTML_TYPE,
				TextParser.TEXT_TYPE_HTML );
		textTypeMapping.put( IForeignContent.TEXT_TYPE,
				TextParser.TEXT_TYPE_PLAIN );
		textTypeMapping.put( IForeignContent.UNKNOWN_TYPE,
				TextParser.TEXT_TYPE_AUTO );

	}

	public static void html2Content( IForeignContent foreign )
	{
		processForeignData( foreign );
	}

	protected static void processForeignData( IForeignContent foreign)
	{
		
		if(foreign.getChildren( )!=null && foreign.getChildren( ).size( )>0)
		{
			return;
		}
		
		HashMap styleMap = new HashMap( );
		ReportDesignHandle reportDesign = foreign.getReportContent( )
				.getDesign( ).getReportDesign( );
		HTMLStyleProcessor htmlProcessor = new HTMLStyleProcessor( reportDesign );
		Object rawValue = foreign.getRawValue( );
		Document doc = null;
		if ( null != rawValue )
		{
			doc = new TextParser( ).parse( foreign.getRawValue( ).toString( ), 
					( String )textTypeMapping.get( foreign.getRawType( ) ) );	
		}
		
		Element body = null;
		if ( doc != null )
		{
			Node node = doc.getFirstChild( );
			//The following must be true
			if ( node instanceof Element )
			{
				body = (Element) node;
			}
		}
		if ( body != null )
		{
			htmlProcessor.execute( body, styleMap );
			IContainerContent container = new ContainerContent((ReportContent)foreign.getReportContent());
			
			addChild( foreign, container, null );
			processNodes( body, styleMap, container, null, null );
		}
	}
	
	/**
	 * Visits the children nodes of the specific node
	 * 
	 * @param ele
	 *            the specific node
	 * @param needEscape
	 *            the flag indicating the content needs escaping
	 * @param cssStyles
	 * @param content 
	 * 			  the parent content of the element
	 * 		       
	 */
	static void processNodes( Element ele, 
			Map cssStyles, IContent content, IContent inlineParent,
			ActionContent action )
	{
		int level=0;
		for ( Node node = ele.getFirstChild( ); node != null; node = node
				.getNextSibling( ) )
		{
			if ( node.getNodeName( ).equals( "value-of" ) ) //$NON-NLS-1$
			{
				if ( node.getFirstChild( ) instanceof Element )
				{
					processNodes( (Element) node.getFirstChild( ),
							cssStyles, content, inlineParent, action );
				}
			}
			else if ( node.getNodeName( ).equals( "image" ) ) //$NON-NLS-1$
			{
				if ( node.getFirstChild( ) instanceof Element )
				{
					processNodes( (Element) node.getFirstChild( ), cssStyles,
							content, inlineParent, action );
				}
			}
			else if(node.getNodeType() == Node.TEXT_NODE)
			{
				ILabelContent label = new LabelContent((ReportContent)content.getReportContent());
				addChild(content, label, inlineParent );
				label.setText(node.getNodeValue());
				StyleDeclaration inlineStyle = new StyleDeclaration(content.getCSSEngine());
				inlineStyle.setProperty( IStyle.STYLE_DISPLAY, CSSValueConstants.INLINE_VALUE );
				label.setInlineStyle(inlineStyle);
				if(action!=null)
				{
					label.setHyperlinkAction(action);
				}
			}
			else if(//supportedHTMLElementTags.contains(node.getNodeName().toUpperCase()) && 
					 node.getNodeType()== Node.ELEMENT_NODE)
			{
				handleElement( (Element) node, cssStyles, content,
						inlineParent, action, ++level );
			}
		}
	}

	static void handleElement( Element ele, Map cssStyles,
			IContent content, IContent inlineParent, ActionContent action,
			int index )
	{
		IStyle cssStyle = ( IStyle ) cssStyles.get( ele );
		if ( cssStyle != null )
		{
			if ( "none".equals( cssStyle.getDisplay() ) ) //$NON-NLS-1$
			{
				//Check if the display mode is none.
				return;
			}
		}
		
		String tagName = ele.getTagName();
		if ( tagName.toLowerCase().equals( "a" ) ) //$NON-NLS-1$
		{
			IContainerContent container = new ContainerContent((ReportContent)content.getReportContent());
			setInlineParent( content, container, inlineParent );
			handleStyle(ele, cssStyles, container);
			ActionContent actionContent = handleAnchor( ele, container, action );
			processNodes( ele, cssStyles, content, container,
					actionContent );
		}
		else if(tagName.toLowerCase().equals( "img" )) //$NON-NLS-1$
		{
			outputImg( ele, cssStyles, content, inlineParent );
		}
		else if ( tagName.toLowerCase().equals( "br" ) ) //$NON-NLS-1$
		{
			
			ILabelContent label = new LabelContent((ReportContent)content.getReportContent());
			addChild(content, label, inlineParent );
			label.setText("\n"); //$NON-NLS-1$
			StyleDeclaration inlineStyle = new StyleDeclaration(content.getCSSEngine());
			inlineStyle.setProperty( IStyle.STYLE_DISPLAY, CSSValueConstants.INLINE_VALUE );
			label.setInlineStyle( inlineStyle );
		}
		else if (tagName.toLowerCase().equals("li") //$NON-NLS-1$
				&& ele.getParentNode().getNodeType() == Node.ELEMENT_NODE)
		{
			StyleDeclaration style = new StyleDeclaration(content.getCSSEngine());
			style.setProperty( IStyle.STYLE_DISPLAY, CSSValueConstants.BLOCK_VALUE );
			style.setProperty( IStyle.STYLE_VERTICAL_ALIGN, CSSValueConstants.MIDDLE_VALUE );
			IContainerContent container = new ContainerContent((ReportContent)content.getReportContent());
			container.setInlineStyle(style);
			addChild(content, container, inlineParent );
			handleStyle(ele, cssStyles, container);
			
			//fix scr  157259In PDF <li> effect is incorrect when page break happens.
			//add a container to number serial, keep consistent page-break
			style = new StyleDeclaration(content.getCSSEngine());
			style.setProperty( IStyle.STYLE_DISPLAY, CSSValueConstants.INLINE_VALUE );
			style.setProperty( IStyle.STYLE_VERTICAL_ALIGN, CSSValueConstants.TOP_VALUE );
			
			IContainerContent orderContainer = new ContainerContent((ReportContent)content.getReportContent());
			CSSValue fontSizeValue = content.getComputedStyle( ).getProperty( IStyle.STYLE_FONT_SIZE ); 
			orderContainer.setWidth( new DimensionType(2.1*PropertyUtil.getDimensionValue( fontSizeValue )/1000.0, EngineIRConstants.UNITS_PT) );
			orderContainer.setInlineStyle( style );
			addChild(container, orderContainer, inlineParent );
			TextContent text = new TextContent((ReportContent)content.getReportContent());
			addChild(orderContainer, text, inlineParent );
			if(ele.getParentNode().getNodeName().equals("ol")) //$NON-NLS-1$
			{
				text.setText(new Integer(index).toString()+"."); //$NON-NLS-1$
			}
			else if(ele.getParentNode().getNodeName().equals("ul")) //$NON-NLS-1$
			{
				text.setText(new String(new char[]{'\u2022'}));
			}

			text.setInlineStyle(style);
			
			IContainerContent childContainer = new ContainerContent((ReportContent)content.getReportContent());
			addChild(container, childContainer, inlineParent );
			childContainer.setInlineStyle(style);
			processNodes( ele, cssStyles, childContainer,
					inlineParent, action );
		}
		else if (tagName.toLowerCase().equals("dd") || tagName.toLowerCase().equals("dt")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			IContainerContent container = new ContainerContent((ReportContent)content.getReportContent());
			addChild(content, container, inlineParent );
			handleStyle(ele, cssStyles, container);
			
			if (tagName.toLowerCase().equals("dd")) //$NON-NLS-1$
			{
				StyleDeclaration style = new StyleDeclaration(content.getCSSEngine());
				style.setProperty( IStyle.STYLE_DISPLAY, CSSValueConstants.INLINE_VALUE );
				style.setProperty( IStyle.STYLE_VERTICAL_ALIGN, CSSValueConstants.TOP_VALUE );
				TextContent text = new TextContent((ReportContent) content
						.getReportContent());
				addChild( content, text, inlineParent );
				if (ele.getParentNode().getNodeName().equals("dl")) //$NON-NLS-1$
				{
					text.setText(""); //$NON-NLS-1$
				}
				style.setTextIndent("3em"); //$NON-NLS-1$
				text.setInlineStyle(style);
				
				IContainerContent childContainer = new ContainerContent((ReportContent)content.getReportContent());
				childContainer.setInlineStyle(style);
				addChild( container, childContainer, inlineParent );

				processNodes( ele, cssStyles, container,
						inlineParent, action );
				
			}
			else
			{
				processNodes( ele, cssStyles, container,
						inlineParent, action );
			}

		}
		else if ( "table".equals( tagName.toLowerCase( ) ) ) //$NON-NLS-1$
		{
			TableProcessor.processTable( ele, cssStyles, content, inlineParent,
					action );
		}
		else 
		{
			IContainerContent container = new ContainerContent((ReportContent)content.getReportContent());
			handleStyle(ele, cssStyles, container);
			if(htmlDisplayMode.contains(ele.getTagName()))
			{
				addChild( content, container, inlineParent );
				processNodes( ele, cssStyles, container, inlineParent, action );
			}
			else
			{
				setInlineParent( content, container, inlineParent );
				// handleStyle(ele, cssStyles, container);
				processNodes( ele, cssStyles, content, container, action );
			}
			
		}
	}
	
	/**
	 * Checks if the content inside the DOM should be escaped.
	 * 
	 * @param doc
	 *            the root of the DOM tree
	 * @return true if the content needs escaping, otherwise false.
	 */
	private static boolean checkEscapeSpace( Node doc )
	{
		String textType = null;
		if ( doc != null && doc.getFirstChild( ) != null
				&& doc.getFirstChild( ) instanceof Element )
		{
			textType = ( (Element) doc.getFirstChild( ) )
					.getAttribute( "text-type" ); //$NON-NLS-1$
			return ( !TextParser.TEXT_TYPE_HTML.equalsIgnoreCase( textType ) );
		}
		return true;
	}
	


	

	/**
	 * Outputs the A element
	 * 
	 * @param ele
	 *            the A element instance
	 */
	protected static ActionContent handleAnchor( Element ele, IContent content,
			ActionContent defaultAction )
	{
		//			If the "id" attribute is not empty, then use it,
		// otherwise use the "name" attribute.
		ActionContent result = defaultAction;
		if ( ele.getAttribute( "id" ).trim( ).length( ) != 0 ) //$NON-NLS-1$
		{
			content.setBookmark(ele.getAttribute( "id" )); //$NON-NLS-1$
		}
		else
		{
			content.setBookmark(ele.getAttribute( "name" ));//$NON-NLS-1$
		}
		
		if ( ele.getAttribute( "href" ).length( ) > 0 ) //$NON-NLS-1$
		{
			String href = ele.getAttribute( "href" ); //$NON-NLS-1$
			if(null!=href && !"".equals(href)) //$NON-NLS-1$
			{
				ActionContent action = new ActionContent();
				if(href.startsWith("#")) //$NON-NLS-1$
				{
					action.setBookmark(href.substring(1));
				}
				else
				{
					String target = ele.getAttribute("target");
					if ("".equals(target))
					{
						target = "_blank";
					}
					action.setHyperlink(href, target);
				}
				result = action;
			}
			
		}
		return result;
	}
	
	static void handleStyle( Element ele, Map cssStyles, IContent content )
	{
		String tagName = ele.getTagName();
		StyleDeclaration style = new StyleDeclaration(content.getCSSEngine());
		
		if("font".equals(tagName)) //$NON-NLS-1$
		{
			String attr = ele.getAttribute("size"); //$NON-NLS-1$
			if(null!=attr && !"".equals(attr)) //$NON-NLS-1$
			{
				style.setFontSize(attr);
			}
			attr = ele.getAttribute("color"); //$NON-NLS-1$
			if(null!=attr && !"".equals(attr)) //$NON-NLS-1$
			{
				style.setColor(attr);
			}
			attr = ele.getAttribute("face"); //$NON-NLS-1$
			if(null!=attr && !"".equals(attr)) //$NON-NLS-1$
			{
				style.setFontFamily(attr);
			}
		}
		if(htmlDisplayMode.contains(tagName))
		{
			style.setDisplay("block"); //$NON-NLS-1$
		}
		else
		{
			style.setDisplay("inline"); //$NON-NLS-1$
		}
		IStyle inlineStyle = (IStyle)cssStyles.get(ele);
		if(inlineStyle!=null)
		{
			style.setProperties(inlineStyle);
		}
		StyleProcessor tag2Style = StyleProcessor.getStyleProcess( tagName );
		if ( tag2Style != null )
		{
			tag2Style.process( style );
		}
		content.setInlineStyle( style );
	}

	/**
	 * Outputs the image
	 * 
	 * @param ele
	 *            the IMG element instance
	 */
	protected static void outputImg( Element ele, Map cssStyles,
			IContent content, IContent inlineParent )
	{
	 	String src = ele.getAttribute( "src" ); //$NON-NLS-1$
		if(src!=null)
		{		
			IImageContent image = new ImageContent(content);
			addChild( content, image, inlineParent );
			handleStyle(ele, cssStyles, image);
			
			if( !FileUtil.isLocalResource( src ) )
			{
				image.setImageSource(IImageContent.IMAGE_URL);
				image.setURI(src);
			}
			else
			{
				ReportDesignHandle handle = content.getReportContent( ).getDesign( ).getReportDesign( );
				URL url = handle.findResource( src, IResourceLocator.IMAGE );
				if(url!=null)
				{
					src = url.toString( );
				}
				image.setImageSource(IImageContent.IMAGE_FILE);
				image.setURI(src);
			}
			
			if(null!=ele.getAttribute("width") && !"".equals(ele.getAttribute("width"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				image.setWidth( PropertyUtil.getDimensionAttribute( ele, "width" ) ); //$NON-NLS-1$
			}
			if(ele.getAttribute("height")!=null &&! "".equals(ele.getAttribute("height"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				image.setHeight( PropertyUtil.getDimensionAttribute( ele, "height" ) ); //$NON-NLS-1$
			}
			if(ele.getAttribute("alt")!=null && !"".equals(ele.getAttribute("alt"))) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				image.setAltText(ele.getAttribute("alt")); //$NON-NLS-1$
			}
		}
	}

	protected static void addChild( IContent parent, IContent child, IContent inlineParent )
	{
		if(parent!=null && child!=null)
		{
			Collection children = parent.getChildren( );
			if(!children.contains( child ))
			{
				children.add( child );
				if ( inlineParent == null )
				{
					child.setParent( parent );
				}
				else
				{
					child.setParent( inlineParent );
				}
			}
		}
	}

	protected static void setInlineParent( IContent parent, IContent child,
			IContent inlineParent )
	{
		if(parent!=null && child!=null)
		{
			if ( inlineParent == null )
			{
				child.setParent( parent );
			}
			else
			{
				child.setParent( inlineParent );
			}
		}
	}
	
	
}
