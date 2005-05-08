/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Actuate Corporation -
 * initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.report.engine.api;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.birt.report.engine.util.FileUtil;

/**
 * Default implementation for writing images in a form that is compatible with a
 * web browser's "HTML Complete" save option, i.e., writes images to a
 * predefined folder.
 */
public class HTMLCompleteImageHandler implements IHTMLImageHandler
{
	static int number = 0;
	
	static HashMap map = new HashMap();

	protected Logger log = Logger.getLogger(HTMLCompleteImageHandler.class
			.getName());
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api2.IHTMLImageHandler#onDesignImage(org.eclipse.birt.report.engine.api2.IImage,
	 *      java.lang.Object)
	 */
	public String onDesignImage(IImage image, Object context)
	{
		return handleImage(image, context, "design", true); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api2.IHTMLImageHandler#onDocImage(org.eclipse.birt.report.engine.api2.IImage,
	 *      java.lang.Object)
	 */
	public String onDocImage(IImage image, Object context)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api2.IHTMLImageHandler#onURLImage(org.eclipse.birt.report.engine.api2.IImage,
	 *      java.lang.Object)
	 */
	public String onURLImage(IImage image, Object context)
	{
		assert (image != null);
		return image.getID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.engine.api2.IHTMLImageHandler#onCustomImage(org.eclipse.birt.report.engine.api2.IImage,
	 *      java.lang.Object)
	 */
	public String onCustomImage(IImage image, Object context)
	{
		return handleImage(image, context, "custom", false); //$NON-NLS-1$
	}

	protected String getFile(String imageDir, String prefix)
	{
		File file = null;
		do
		{
			number++;
			file = new File(imageDir + "/" + prefix + number); //$NON-NLS-1$
		}
		while (file.exists());

		return prefix + number; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.engine.api2.IHTMLImageHandler#onFileImage(org.eclipse.birt.report.engine.api2.IImage, java.lang.Object)
	 */
	public String onFileImage(IImage image, Object context)
	{
		return handleImage(image, context, "file", true); //$NON-NLS-1$
	}
	
	protected String handleImage(IImage image, Object context, String prefix, boolean needMap)
	{
		String mapID = null;
		if(needMap)
		{
			mapID = getImageMapID(image);
			if(map.containsKey(mapID))
			{
				return (String)map.get(mapID);
			}
		}
		String ret = null;
		boolean returnRelativePath = true; 
		if (context != null
				&& (context instanceof HTMLRenderContext))
		{
			HTMLRenderContext myContext = (HTMLRenderContext) context;
			String imageURL = myContext.getBaseImageURL();
			String imageDir = myContext.getImageDirectory();
			String reportName = (String)image.getRenderOption().getOutputSetting().get(RenderOptionBase.OUTPUT_FILE_NAME);
			String reportBase = null;
			if(reportName != null)
			{
				reportBase = new File(new File(reportName).getAbsolutePath()).getParent();
			}
			else
			{
				reportBase = new File(".").getAbsolutePath();
			}
			String imageAbsoluteDir = null;
			if (imageDir == null)
			{
				imageAbsoluteDir = reportBase;
				imageURL = null;//return file path
				imageDir = "."; //$NON-NLS-1$
			}
			else
			{
				if(!FileUtil.isRelativePath(imageDir))
				{
					returnRelativePath = false;
					imageAbsoluteDir = imageDir;
				}
				else
				{
					imageAbsoluteDir = reportBase + "/" + imageDir; //$NON-NLS-1$
				}
			}
			String fileName; 
			File file;
			synchronized (HTMLCompleteImageHandler.class)
			{
				fileName = getFile(imageAbsoluteDir, prefix); //$NON-NLS-1$
				file = new File(imageAbsoluteDir, fileName); //$NON-NLS-1$
				try
				{
					image.writeImage(file);
				}
				catch (IOException e)
				{
					log.log(Level.SEVERE,e.getMessage(),e);
				}
			}
			if (imageURL != null)
			{
				ret = imageURL + "/" + fileName; //$NON-NLS-1$
			}
			else
			{
				if(returnRelativePath)
				{
					ret = imageDir + "/" + fileName; //$NON-NLS-1$
				}
				else
				{
					ret = file.getAbsolutePath(); //$NON-NLS-1$
				}
			}
			
			if(needMap)
			{
				map.put(mapID, ret);
			}

		}
		return ret;
	}
	
	protected String getImageMapID(IImage image)
	{
		if(image.getReportRunnable()!=null)
		{
			return image.getReportRunnable().hashCode() + image.getID();
		}
		return image.getID();
	}
	
	
}