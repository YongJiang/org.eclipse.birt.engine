/*******************************************************************************
 * Copyright (c) 2009 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.core.archive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.birt.core.archive.compound.ArchiveEntry;
import org.eclipse.birt.core.archive.compound.ArchiveFile;

public class ArchiveFileCacheTest extends TestCase
{

	public void testMemoryCache( ) throws IOException
	{
		long used1 = ArchiveFile.getTotalUsedCache( );
		System.out.println( used1 );
		ArchiveFile[] afs = new ArchiveFile[100];
		for ( int i = 0; i < afs.length; i++ )
		{
			afs[i] = createArchiveFile( "utest/arc_" + i );
		}
		long used2 = ArchiveFile.getTotalUsedCache( );
		System.out.println( used2 );
		ArchiveEntry[] entries = openEntries( afs[0], 100 );
		long used3 = ArchiveFile.getTotalUsedCache( );
		System.out.println( used3 );

		closeEntries( entries );
		long used4 = ArchiveFile.getTotalUsedCache( );
		System.out.println( used4 );
		for ( int i = 0; i < afs.length; i++ )
		{
			afs[i].close( );
		}
		long used5 = ArchiveFile.getTotalUsedCache( );
		System.out.println( used5 );
	}

	protected ArchiveFile createArchiveFile( String name ) throws IOException
	{
		ArchiveFile af = new ArchiveFile( name, "rwt" );
		af.setCacheSize( 4096 * 10 );
		for ( int i = 0; i < 1000; i++ )
		{
			ArchiveEntry entry = af.createEntry( "STREAM_" + i );
			long position = 0;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream( );
			DataOutputStream output = new DataOutputStream( buffer );
			output.writeUTF( "STREAM_" + i + "[CONTENT]" );
			byte[] bytes = buffer.toByteArray( );
			for ( int j = 0; j < 4096; j++ )
			{
				entry.write( position, bytes, 0, bytes.length );
				position += bytes.length;
			}
			entry.close( );
		}
		return af;
	}

	protected ArchiveEntry[] openEntries( ArchiveFile af, int size )
			throws IOException
	{
		ArchiveEntry[] entries = new ArchiveEntry[size];
		for ( int i = 0; i < entries.length; i++ )
		{
			entries[i] = af.openEntry( "ENTRY_" + i );
		}
		return entries;
	}

	protected void closeEntries( ArchiveEntry[] entries ) throws IOException
	{
		for ( int i = 0; i < entries.length; i++ )
		{
			entries[i].close( );
		}
	}

}
