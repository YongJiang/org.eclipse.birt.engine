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

package org.eclipse.birt.report.engine.internal.index.v2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.birt.core.archive.IDocArchiveReader;
import org.eclipse.birt.core.archive.IDocArchiveWriter;
import org.eclipse.birt.core.archive.RAInputStream;
import org.eclipse.birt.core.archive.RAOutputStream;
import org.eclipse.birt.core.btree.BTree;
import org.eclipse.birt.core.btree.BTreeException;
import org.eclipse.birt.core.btree.BTreeFile;
import org.eclipse.birt.core.btree.BTreeOption;
import org.eclipse.birt.core.btree.BTreeSerializer;

class BTreeMap extends BTree<String, Long>
{

	int indexVersion;
	int indexType;

	static public BTreeMap openTreeMap( IDocArchiveReader archive, String name )
			throws IOException
	{
		BTreeOption<String, Long> option = new BTreeOption<String, Long>( );
		option.setReadOnly( true );
		option.setKeySerializer( new StringSerializer( ) );
		option.setHasValue( true );
		option.setAllowDuplicate( false );
		option.setValueSize( 8 );
		option.setValueSerializer( new LongSerializer( ) );
		option.setFile( new ArchiveInputFile( archive, name ) );
		return new BTreeMap( option );
	}

	static public BTreeMap createTreeMap( IDocArchiveWriter archive, String name )
			throws IOException
	{
		BTreeOption<String, Long> option = new BTreeOption<String, Long>( );
		option.setKeySerializer( new StringSerializer( ) );
		option.setHasValue( true );
		option.setAllowDuplicate( false );
		option.setValueSize( 8 );
		option.setValueSerializer( new LongSerializer( ) );
		option.setFile( new ArchiveOutputFile( archive, name ) );
		return new BTreeMap( option );
	}

	private BTreeMap( BTreeOption<String, Long> option ) throws IOException
	{
		super( option );
		indexVersion = IndexConstants.VERSION_0;
		indexType = IndexConstants.BTREE_MAP;
	}

	protected void readTreeHead( DataInput in ) throws IOException
	{
		indexVersion = in.readInt( );
		indexType = in.readInt( );
		super.readTreeHead( in );
	}

	protected void writeTreeHead( DataOutput out ) throws IOException
	{
		out.writeInt( indexVersion );
		out.writeInt( indexType );
		super.writeTreeHead( out );
	}

	public void close( ) throws BTreeException
	{
		super.close( );

		try
		{
			if ( file instanceof ArchiveInputFile )
			{
				( (ArchiveInputFile) file ).close( );
			}
			if ( file instanceof ArchiveOutputFile )
			{
				( (ArchiveOutputFile) file ).close( );
			}
		}
		catch ( IOException ex )
		{
			throw new BTreeException( ex );
		}
	}

	static private class StringSerializer implements BTreeSerializer<String>
	{

		public byte[] getBytes( String object ) throws IOException
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 1024 );
			DataOutput oo = new DataOutputStream( out );
			oo.writeUTF( object );
			return out.toByteArray( );
		}

		public String getObject( byte[] bytes ) throws IOException,
				ClassNotFoundException
		{
			DataInput input = new DataInputStream( new ByteArrayInputStream(
					bytes ) );
			return input.readUTF( );
		}
	}

	static private class LongSerializer implements BTreeSerializer<Long>
	{

		public byte[] getBytes( Long object ) throws IOException
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream( 1024 );
			DataOutput oo = new DataOutputStream( out );
			oo.writeLong( object.longValue( ) );
			return out.toByteArray( );
		}

		public Long getObject( byte[] bytes ) throws IOException,
				ClassNotFoundException
		{
			DataInput input = new DataInputStream( new ByteArrayInputStream(
					bytes ) );
			return Long.valueOf( input.readLong( ) );
		}
	}

	static private class ArchiveInputFile implements BTreeFile
	{

		IDocArchiveReader archive;
		String name;
		RAInputStream input;

		ArchiveInputFile( IDocArchiveReader archive, String name )
				throws IOException
		{
			this.archive = archive;
			this.name = name;
			this.input = archive.getInputStream( name );
		}

		public int allocBlock( ) throws IOException
		{
			throw new IOException( "read only stream" );
		}

		public int getTotalBlock( ) throws IOException
		{
			return (int) ( ( input.length( ) + BLOCK_SIZE - 1 ) / BLOCK_SIZE );
		}

		public Object lock( ) throws IOException
		{
			return archive.lock( name );
		}

		public void readBlock( int blockId, byte[] bytes ) throws IOException
		{
			input.seek( blockId * BLOCK_SIZE );
			input.read( bytes );
		}

		public void unlock( Object lock ) throws IOException
		{
			archive.unlock( lock );
		}

		public void writeBlock( int blockId, byte[] bytes ) throws IOException
		{
			throw new IOException( "read only stream" );
		}

		public void close( )
		{

		}
	}

	static private class ArchiveOutputFile implements BTreeFile
	{

		IDocArchiveWriter archive;
		String name;
		RAOutputStream output;
		RAInputStream input;
		int totalBlock;

		ArchiveOutputFile( IDocArchiveWriter archive, String name )
				throws IOException
		{
			this.archive = archive;
			this.name = name;
			output = archive.createOutputStream( name );
			input = archive.getInputStream( name );
			totalBlock = 0;
		}

		void close( ) throws IOException
		{
			if ( output != null )
			{
				output.close( );
			}
			if ( input != null )
			{
				input.close( );
			}
		}

		public int allocBlock( ) throws IOException
		{
			return totalBlock++;
		}

		public int getTotalBlock( ) throws IOException
		{
			return totalBlock;
		}

		public Object lock( ) throws IOException
		{
			return archive.lock( name );
		}

		public void readBlock( int blockId, byte[] bytes ) throws IOException
		{
			input.refresh( );
			input.seek( blockId * BLOCK_SIZE );
			input.read( bytes );
		}

		public void unlock( Object lock ) throws IOException
		{
			archive.unlock( lock );
		}

		public void writeBlock( int blockId, byte[] bytes ) throws IOException
		{
			if ( blockId >= totalBlock )
			{
				totalBlock = blockId + 1;
			}
			output.seek( blockId * BLOCK_SIZE );
			output.write( bytes );
			output.flush( );
		}
	}

}
