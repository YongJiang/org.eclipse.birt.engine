
package org.eclipse.birt.report.engine.layout.pdf;

import java.util.Iterator;

import org.eclipse.birt.report.engine.content.IBandContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IGroupContent;
import org.eclipse.birt.report.engine.content.ITableBandContent;
import org.eclipse.birt.report.engine.executor.IReportItemExecutor;
import org.eclipse.birt.report.engine.internal.executor.dom.DOMReportItemExecutor;
import org.eclipse.birt.report.engine.layout.IBlockStackingLayoutManager;
import org.eclipse.birt.report.engine.layout.area.impl.AreaFactory;
import org.eclipse.birt.report.engine.layout.area.impl.ContainerArea;
import org.eclipse.birt.report.engine.layout.area.impl.RowArea;
import org.eclipse.birt.report.engine.layout.area.impl.TableArea;

public class PDFTableGroupLM extends PDFGroupLM
		implements
			IBlockStackingLayoutManager
{

	protected PDFTableLM tableLM = null;
	protected boolean needRepeat = false;

	public PDFTableGroupLM( PDFLayoutEngineContext context,
			PDFStackingLM parent, IContent content, IReportItemExecutor executor )
	{
		super( context, parent, content, executor );
		tableLM = getTableLayoutManager( );
		tableLM.startGroup( (IGroupContent) content );
	}

	protected boolean traverseChildren( )
	{

		boolean childBreak = super.traverseChildren( );
		if ( !childBreak )
		{
			tableLM.endGroup( (IGroupContent) content );
		}
		return childBreak;
	}

	protected void createRoot( )
	{
		if ( root == null )
		{
			root = (ContainerArea) AreaFactory.createBlockContainer( content );
		}
	}

	protected void initialize( )
	{
		if ( root == null && keepWithCache.isEmpty( ) && !isFirst )
		{
			repeatCount = 0;
			needRepeat = true;
		}
		super.initialize( );

	}

	private void repeat( )
	{
		if ( isFirst || tableLM.isFirst )
		{
			isFirst = false;
			return;
		}
		if ( !needRepeat || !isCurrentDetailBand( ) )
		{
			return;
		}
		ITableBandContent header = (ITableBandContent) groupContent.getHeader( );
		if ( !isRepeatHeader( ) || header == null )
		{
			return;
		}
		if ( header.getChildren( ).isEmpty( ) )
		{
			return;
		}
		if ( child != null )
		{
			IContent content = child.getContent( );
			if ( content instanceof ITableBandContent )
			{
				if ( ( (ITableBandContent) content ).getBandType( ) == IBandContent.BAND_GROUP_HEADER )
				{
					return;
				}

			}
		}
		PDFReportLayoutEngine engine = context.getLayoutEngine( );
		PDFLayoutEngineContext con = new PDFLayoutEngineContext( engine );
		con.setFactory( new PDFLayoutManagerFactory( con ) );
		con.setFormat( context.getFormat( ) );
		con.setReport( context.getReport( ) );
		con.setMaxHeight( context.getMaxHeight( ) );
		con.setMaxWidth( context.getMaxWidth( ) );
		con.setAllowPageBreak( false );
		IReportItemExecutor headerExecutor = new DOMReportItemExecutor( header );
		headerExecutor.execute( );
		PDFTableRegionLM regionLM = new PDFTableRegionLM( con, tableLM
				.getContent( ), tableLM.getLayoutInfo( ) );
		regionLM.initialize( header, null );// tableLM.lastRowArea);
		regionLM.layout( );
		TableArea tableRegion = (TableArea) tableLM.getContent( ).getExtension(
				IContent.LAYOUT_EXTENSION );
		if ( tableRegion != null
				&& tableRegion.getHeight( ) < getCurrentMaxContentHeight( ) )
		{
			// add to root
			Iterator iter = tableRegion.getChildren( );
			RowArea row = null;
			int count = 0;
			while ( iter.hasNext( ) )
			{
				row = (RowArea) iter.next( );
				// FIXME should add to the first line of this group
				addArea( row, false, true );
				tableLM.addRow( row, true );
				count++;
			}

			repeatCount += count;
		}
		tableLM.getContent( ).setExtension( IContent.LAYOUT_EXTENSION, null );
		needRepeat = false;
	}

	protected void repeatHeader( )
	{
		repeat( );
		skipCachedRow( );
	}

	protected IReportItemExecutor createExecutor( )
	{
		return executor;
	}

	protected boolean isCurrentDetailBand( )
	{
		if ( child != null )
		{
			IContent c = child.getContent( );
			if ( c != null )
			{
				if ( c instanceof IGroupContent )
				{
					return true;
				}
				else if ( c instanceof IBandContent )
				{
					IBandContent band = (IBandContent) c;
					if ( band.getBandType( ) == IBandContent.BAND_DETAIL )
					{
						return true;
					}
				}
			}
		}
		else
		{
			return true;
		}
		return false;
	}

	protected void skipCachedRow( )
	{
		if ( keepWithCache.isEmpty( ) )
		{
			return;
		}
		Iterator iter = keepWithCache.getChildren( );
		while ( iter.hasNext( ) )
		{
			ContainerArea container = (ContainerArea) iter.next( );
			skip( container );
		}
	}

	protected void skip( ContainerArea area )
	{
		if ( area instanceof RowArea )
		{
			tableLM.skipRow( (RowArea) area );
		}
		else
		{
			Iterator iter = area.getChildren( );
			while ( iter.hasNext( ) )
			{
				ContainerArea container = (ContainerArea) iter.next( );
				skip( container );
			}
		}
	}

}
