package org.eclipse.birt.report.engine.layout.content;

import java.util.Collection;

import org.eclipse.birt.report.engine.content.IBandContent;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IGroupContent;
import org.eclipse.birt.report.engine.content.IListBandContent;
import org.eclipse.birt.report.engine.content.IListContent;
import org.eclipse.birt.report.engine.content.IStyle;
import org.eclipse.birt.report.engine.executor.IReportItemExecutor;
import org.eclipse.birt.report.engine.internal.executor.dom.DOMReportItemExecutor;


public class ListContainerExecutor extends BlockStackingExecutor
		implements
			IReportItemExecutor
{
	protected boolean repeat = false;
	
	public ListContainerExecutor(IContent content, IReportItemExecutor executor)
	{
		super(content, null);
		this.executor = new ExecutorList(executor, content);
		if(content instanceof IGroupContent)
		{
			repeat = ((IGroupContent)content).isHeaderRepeat( );
		}
		else
		{
			if(content instanceof IListContent)
			{
				repeat =  ((IListContent)content).isHeaderRepeat( );
			}
		}
	}
	
	protected class ExecutorList implements IReportItemExecutor
	{
		protected IReportItemExecutor currentRunIn = null;
		protected IReportItemExecutor executor = null;
		protected IContent content = null;
		protected IReportItemExecutor childExecutor = null;
		protected IContent childContent = null;
		protected boolean hasNext = false;
		protected boolean needUpdate = true;
		
		public ExecutorList(IReportItemExecutor executor, IContent content)
		{
			this.content = content;
			this.executor = executor;
		}
		
		public void close( )
		{
			if(currentRunIn!=null)
			{
				currentRunIn.close( );
			}
			executor.close( );
		}

		public IContent execute( )
		{
			return content;
		}

		public IReportItemExecutor getNextChild( )
		{
			if(childContent!=null)
			{
				IReportItemExecutor ret = new ItemExecutorWrapper(childExecutor, childContent);
				childContent = null;
				childExecutor = null;
				needUpdate = true;
				return ret;
			}
			if(currentRunIn!=null)
			{
				needUpdate = true;
				IReportItemExecutor runInChild = currentRunIn.getNextChild( );
				if(runInChild!=null)
				{
					IContent runInContent = runInChild.execute( );
					if(runInContent!=null && 
							(runInContent.getChildren( )==null || runInContent.getChildren( ).size( )==0))
					{
						execute(runInChild, runInContent);
						runInChild.close( );
						runInChild = new DOMReportItemExecutor(runInContent);
					}
				}
				return runInChild;
			}
			assert(false);
			return null;
			
		}

		public boolean hasNextChild( )
		{
			if(!needUpdate)
			{
				return hasNext;
			}
			if(currentRunIn!=null)
			{
				if(currentRunIn.hasNextChild( ))
				{
					hasNext = true;
					needUpdate = false;
					return hasNext;
				}
				else
				{
					currentRunIn.close( );
				}
			}
			currentRunIn = null;
			while(executor.hasNextChild( ))
			{
				IReportItemExecutor next = executor.getNextChild( );
				IContent nextContent = next.execute( );
				if(nextContent instanceof IListBandContent)
				{
					IListBandContent band = (IListBandContent)nextContent;
					if(repeat && (band.getBandType( )==IBandContent.BAND_HEADER || band.getBandType( )==IBandContent.BAND_GROUP_HEADER))
					{
						executeHeader(next, nextContent);
						next.close( );
						next = new DOMReportItemExecutor(nextContent);
						next.execute( );
						add( nextContent.getParent( ).getChildren( ),
								nextContent );
					}
					if(next.hasNextChild( ))
					{
						currentRunIn = next;
						break;
					}
				}
				else
				{
					childExecutor = next;
					childContent = nextContent;
					break;
				}
			}
			if(currentRunIn!=null || childContent!=null)
			{
				hasNext = true;
			}
			else
			{
				hasNext = false;
			}
			needUpdate = false;
			return hasNext;
		}
		
		protected void execute(IReportItemExecutor executor, IContent content)
		{
			while(executor.hasNextChild( ))
			{
				IReportItemExecutor childExecutor = executor.getNextChild( );
				if(childExecutor!=null)
				{
					IContent childContent = childExecutor.execute( );
					add( content.getChildren( ), childContent );
					execute(childExecutor, childContent);
					childExecutor.close( );
				}
			}
		}
		
		protected void executeHeader(IReportItemExecutor executor, IContent content)
		{
			while(executor.hasNextChild( ))
			{
				IReportItemExecutor childExecutor = executor.getNextChild( );
				if(childExecutor!=null)
				{
					IContent childContent = childExecutor.execute( );
					removePageBreak(childContent);
					add( content.getChildren( ), childContent );
					execute(childExecutor, childContent);
					childExecutor.close( );
				}
			}
		}
		
		protected void removePageBreak(IContent content)
		{
			IStyle style = content.getStyle( );
			if(style!=null)
			{
				style.setProperty( IStyle.STYLE_PAGE_BREAK_AFTER, IStyle.AUTO_VALUE );
				style.setProperty( IStyle.STYLE_PAGE_BREAK_BEFORE, IStyle.AUTO_VALUE );
			}
		}
	}

	private void add(Collection collection, IContent content )
	{
		if ( !collection.contains( content ) )
		{
			collection.add( content );
		}
	}
}
