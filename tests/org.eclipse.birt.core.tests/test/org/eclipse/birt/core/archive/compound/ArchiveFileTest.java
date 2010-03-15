
package org.eclipse.birt.core.archive.compound;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class ArchiveFileTest extends TestCase
{

	static final String ARCHIVE_FOLDER = "./utest/";
	static final String ARCHIVE_FILE = ARCHIVE_FOLDER + "archive.rptdocument";
	static final String TRANSIENT_ARCHIVE_FILE = ARCHIVE_FOLDER
			+ "t_archive.rptdocument";

	public void setUp( )
	{
		new File( ARCHIVE_FOLDER ).mkdirs( );
	}

	public void tearDown( )
	{
		new File( TRANSIENT_ARCHIVE_FILE ).delete( );
		new File( ARCHIVE_FILE ).delete( );
		new File( ARCHIVE_FOLDER ).delete( );
	}

	public void testArchiveFile( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( ARCHIVE_FILE, "rw" );
		archive.setCacheSize( 0 );
		archive.setCacheSize( 64 * 1024 );
		createArchive( archive );
		checkArchive( archive );
		assertTrue( archive.getUsedCache( ) > 0 );
		assertTrue( archive.getUsedCache( ) <= 64 * 1024 );
		archive.close( );
		assertTrue( archive.getUsedCache( ) == 0 );

		archive = new ArchiveFile( ARCHIVE_FILE, "r" );
		checkArchive( archive );
		archive.close( );
	}

	public void testArchiveFileNoCache( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( ARCHIVE_FILE, "rw" );
		archive.setCacheSize( 0 );
		createArchive( archive );
		checkArchive( archive );
		assertEquals( archive.getUsedCache( ), 0 );
		archive.close( );
		assertTrue( archive.getUsedCache( ) == 0 );

		archive = new ArchiveFile( ARCHIVE_FILE, "rw" );
		archive.setCacheSize( 64 * 1024 );
		createArchive( archive );
		checkArchive( archive );
		assertTrue( archive.getUsedCache( ) > 0 );
		archive.setCacheSize( 0 );
		createArchive( archive );
		checkArchive( archive );
		assertEquals( archive.getUsedCache( ), 0 );
		archive.close( );
		assertTrue( archive.getUsedCache( ) == 0 );
	}

	public void testTransient( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( TRANSIENT_ARCHIVE_FILE, "rwt" );
		createArchive( archive );
		checkArchive( archive );
		archive.close( );
		assertTrue( !new File( TRANSIENT_ARCHIVE_FILE ).exists( ) );

	}

	public void testAppend( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( ARCHIVE_FILE, "rw" );
		createArchive( archive );
		archive.close( );

		archive = new ArchiveFile( ARCHIVE_FILE, "rw+" );

		ArchiveEntry entry = archive.createEntry( "/append" );
		entry.write( 0, new byte[1], 0, 1 );
		entry.close( );

		checkArchive( archive );
		entry = archive.openEntry( "/append" );
		assertTrue( entry != null );
		assertEquals( 1, entry.getLength( ) );
		entry.close( );

		archive.close( );

		archive = new ArchiveFile( ARCHIVE_FILE, "r" );
		checkArchive( archive );
		entry = archive.openEntry( "/append" );
		assertTrue( entry != null );
		assertEquals( 1, entry.getLength( ) );
		entry.close( );
		archive.close( );
	}

	public void testSaveAs( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( TRANSIENT_ARCHIVE_FILE, "rwt" );
		createArchive( archive );
		archive.saveAs( ARCHIVE_FILE );
		archive.close( );

		archive = new ArchiveFile( ARCHIVE_FILE, "r" );
		checkArchive( archive );
		archive.close( );

	}

	public void testFlush( ) throws IOException
	{
		ArchiveFile archive = new ArchiveFile( TRANSIENT_ARCHIVE_FILE, "rw" );
		createArchive( archive );
		archive.flush( );

		ArchiveFile newArchive = new ArchiveFile( TRANSIENT_ARCHIVE_FILE, "r" );

		checkArchive( newArchive );
		newArchive.close( );
		archive.close( );
	}

	void createArchive( ArchiveFile archive ) throws IOException
	{
		int entryCount = 1024;
		byte[] b = new byte[entryCount];
		for ( int i = 0; i < entryCount; i++ )
		{
			ArchiveEntry entry = archive.createEntry( "/entry/" + i );
			entry.write( 0, b, 0, i );
			entry.close( );
		}
	}

	void checkArchive( ArchiveFile archive ) throws IOException
	{
		int entryCount = 1024;
		for ( int i = 0; i < entryCount; i++ )
		{
			ArchiveEntry entry = archive.openEntry( "/entry/" + i );
			try
			{
				assertTrue( entry != null );
				assertEquals( i, entry.getLength( ) );
			}
			finally
			{
				entry.close( );
			}
		}
	}
}
