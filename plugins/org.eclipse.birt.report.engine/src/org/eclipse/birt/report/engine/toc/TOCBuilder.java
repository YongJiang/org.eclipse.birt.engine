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

package org.eclipse.birt.report.engine.toc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.core.util.IOUtil;

/**
 * A class for building up TOC hierarchy
 */
public class TOCBuilder
{
	/**
	 * the root TOC entry
	 */
	private TOCTree tocTree;
	private TOCEntry rootEntry;

	private static final String VERSION = "__Version : 1.0";
	public static final String TOC_PREFIX = "__TOC";//

	/**
	 * @param tocTree
	 *            the root for the TOC tree
	 */
	public TOCBuilder( TOCTree tocTree )
	{
		this.tocTree = tocTree;
		TOCTreeNode root = tocTree.getTOCRoot( );
		rootEntry = new TOCEntry( null, root, root );
	}

	public TOCEntry startGroupEntry( TOCEntry parent, Object tocValue,
			String bookmark, String hiddenFormats )
	{
		return startEntry( parent, tocValue, bookmark, hiddenFormats, true );
	}

	private String mergeHideFormats( TOCEntry parent, String hiddenFormats )
	{
		String parentHiddenFormats = parent.getHideFormats( );
		if ( hiddenFormats == null )
		{
			return parentHiddenFormats;
		}
		if ( parentHiddenFormats == null )
		{
			return hiddenFormats;
		}
		return hiddenFormats + ", " + parentHiddenFormats ;
	}

	public void closeGroupEntry( TOCEntry group )
	{
	}

	/**
	 * @param displayString
	 *            display string for the TOC entry
	 * @param bookmark
	 */
	public TOCEntry startEntry( TOCEntry parent, Object tocValue,
			String bookmark, String hiddenFormats )
	{
		return startEntry( parent, tocValue, bookmark, hiddenFormats, false );
	}

	public TOCEntry startEntry( TOCEntry parent, Object tocValue,
			String bookmark, String hiddenFormats, boolean isGroupRoot )
	{
		if ( parent == null )
		{
			parent = rootEntry;
		}

		TOCTreeNode parentNode = parent.node;
		TOCTreeNode node = new TOCTreeNode( );
		String id = parentNode.getNodeID( );
		if ( id == null )
		{
			id = TOC_PREFIX;
		}
		id = id + "_" + parentNode.getChildren( ).size( );

		String formats = mergeHideFormats( parent, hiddenFormats);
		node.setNodeID( id );
		node.setBookmark( bookmark == null ? id : bookmark );
		node.setParent( parentNode );
		node.setHideFormats( formats );
		node.setIsGroupRoot( isGroupRoot );
		node.setTOCValue( tocValue );
		parentNode.getChildren( ).add( node );

		TOCEntry entry = new TOCEntry( parent, parent.getRoot( ), node );
		entry.setHideFormats( formats );
		return entry;
	}

	public TOCEntry startEntry( TOCEntry parent, Object tocValue, String bookmark )
	{
		return startEntry( parent, tocValue, bookmark, null );
	}

	public TOCEntry startDummyEntry( TOCEntry parent, String hiddenFormats )
	{
		if ( parent == null )
		{
			parent = rootEntry;
		}
		TOCEntry entry = new TOCEntry( parent, parent.getRoot( ), parent
				.getNode( ) );
		entry.setHideFormats( mergeHideFormats( parent, hiddenFormats ) );
		return entry;
	}

	public TOCEntry createEntry( TOCEntry parent, Object tocValue, String bookmark )
	{
		TOCEntry entry = startEntry( parent, tocValue, bookmark, null );
		closeEntry( entry );
		return entry;
	}

	/**
	 * close the entry. for top level toc, all entry must be put into the root
	 * entry. for group toc, we must create a root entry, and put all others
	 * into the root entry.
	 */
	public void closeEntry( TOCEntry entry )
	{
	}
	
	public TOCEntry getTOCEntry( )
	{
		return rootEntry;
	}
	
	public TOCTree getTOCTree( )
	{
		return tocTree;
	}

	static public void write( TOCTree tree, DataOutputStream out )
			throws IOException
	{
		IOUtil.writeString( out, VERSION );
		writeTOC( tree.getTOCRoot( ), out );
	}

	private static void writeTOC( TOCTreeNode root, DataOutputStream out )
			throws IOException
	{
		IOUtil.writeString( out, root.getNodeID( ) );
		IOUtil.writeString( out, root.getDisplayString( ) );
		IOUtil.writeString( out, root.getBookmark( ) );
		IOUtil.writeString( out, root.getHiddenFormats( ) );
		IOUtil.writeBool( out, root.isGroupRoot( ) );
		IOUtil.writeObject( out, root.getTOCValue( ) );
		List children = root.getChildren( );
		IOUtil.writeInt( out, children.size( ) );
		Iterator iter = children.iterator( );
		while ( iter.hasNext( ) )
		{
			TOCTreeNode child = (TOCTreeNode) iter.next( );
			writeTOC( child, out );
		}
		out.flush( );
		return;
	}

	public static void read( TOCTree tree, DataInputStream input ) throws IOException
	{
		TOCTreeNode node = tree.getTOCRoot( );
		String head = IOUtil.readString( input );
		if ( head == null || ! VERSION.equals( head ) )
		{
			readV0( node, input, head, true );
		}
		else
		{
			readV1( node, input );
		}
	}

	static public void readV0( TOCTreeNode node, DataInputStream input,
			String nodeId, boolean isRoot ) throws IOException
	{
		if ( !isRoot )
		{
			nodeId = IOUtil.readString( input );
		}
		String displayString = IOUtil.readString( input );
		String bookmark = IOUtil.readString( input );
		node.setNodeID( nodeId );
		node.setDisplayString( displayString );
		node.setBookmark( bookmark );
		int size = IOUtil.readInt( input );
		for ( int i = 0; i < size; i++ )
		{
			TOCTreeNode child = new TOCTreeNode( );
			readV0( child, input, null, false );
			child.setParent( node );
			node.getChildren( ).add( child );
		}
	}

	static public void readV1( TOCTreeNode node, DataInputStream input )
			throws IOException
	{
		String nodeId = IOUtil.readString( input );
		String displayString = IOUtil.readString( input );
		String bookmark = IOUtil.readString( input );
		String hiddenFormats = IOUtil.readString( input );
		boolean isGroupRoot = IOUtil.readBool( input );
		Object tocValue = IOUtil.readObject( input );
		node.setNodeID( nodeId );
		node.setDisplayString( displayString );
		node.setBookmark( bookmark );
		node.setHideFormats( hiddenFormats );
		node.setIsGroupRoot( isGroupRoot );
		node.setTOCValue( tocValue );
		int size = IOUtil.readInt( input );
		for ( int i = 0; i < size; i++ )
		{
			TOCTreeNode child = new TOCTreeNode( );
			readV1( child, input );
			child.setParent( node );
			node.getChildren( ).add( child );
		}
	}
}
