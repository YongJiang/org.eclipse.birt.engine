/*******************************************************************************
 * Copyright (c) 2004,2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.layout.pdf.font;

import org.eclipse.birt.report.engine.content.ITextContent;
import org.eclipse.birt.report.engine.layout.pdf.ISplitter;
import org.eclipse.birt.report.engine.layout.pdf.text.Chunk;

import com.ibm.icu.text.Bidi;

public class FontSplitter implements ISplitter
{
	/**
	 * If no font can display a character, replace the character with the MISSING_CHAR. 
	 * Make sure MISSING_CHAR can be displayed with DEFAUTL_FONT.
	 */
	public static final char MISSING_CHAR = '?';
	
	private FontHandler fh = null;
	private boolean fontSubstitution;
	
	private int baseLevel = Bidi.DIRECTION_LEFT_TO_RIGHT;
	private int runLevel = Bidi.DIRECTION_LEFT_TO_RIGHT;
	private int baseOffset = 0;
	private char[] chunkText = null;

	private int chunkStartPos = 0;
	private int currentPos = -1;

	private FontInfo lastFontInfo = null;
	
	private Chunk lineBreak = null;

	public FontSplitter( FontMappingManager fontManager, Chunk inputChunk,
			ITextContent textContent, boolean fontSubstitution )
	{
		this.fontSubstitution = fontSubstitution;
		this.chunkText = inputChunk.getText( ).toCharArray( );
		baseOffset = inputChunk.getOffset( );
		baseLevel = inputChunk.getBaseLevel( );
		runLevel = inputChunk.getRunLevel( );
		this.fh = new FontHandler( fontManager, textContent, fontSubstitution );
	}
	
	private Chunk buildChunk()
	{
		if (!fontSubstitution)
		{
			Chunk c = new Chunk(new String(chunkText),
					baseOffset, baseLevel, runLevel, fh.getFontInfo());
			chunkStartPos = chunkText.length;
			return c;	
		}
		
		if (lineBreak != null)
		{
			Chunk result = lineBreak;
			lineBreak = null;
			chunkStartPos ++;
			return result;
		}
		
		while (++currentPos < chunkText.length)
		{
			Chunk lineBreakChunk = processLineBreak();
			if ( lineBreakChunk != null )
			{
				return lineBreakChunk;
			}
			
			//We fail to find a font to display the character,
			//we replace this character with MISSING_CHAR defined in FontHander.
			if (!fh.selectFont(chunkText[currentPos]))
			{
				chunkText[currentPos] = MISSING_CHAR;
			}
			//If a character uses a font different from the previous character,
			//we split the chunk at the point.
			if (fh.isFontChanged())
			{
				//For the first character of the chunk, although the font has changed,
				//we will just omit it rather than build a blank chunk.
				if (null == lastFontInfo)
				{
					lastFontInfo = fh.getFontInfo();
					continue;
				}
				Chunk c = new Chunk(new String(chunkText, chunkStartPos, currentPos-chunkStartPos), 
						baseOffset + chunkStartPos, baseLevel, runLevel, lastFontInfo);
				chunkStartPos = currentPos;
				lastFontInfo = fh.getFontInfo();
				return c;
			}
		}
		
		//currentPos reaches the end of the input chunk. 
		if (currentPos >= chunkText.length -1)
		{
			Chunk c = new Chunk(new String(chunkText, chunkStartPos, chunkText.length - chunkStartPos),
					baseOffset + chunkStartPos, baseLevel, runLevel, lastFontInfo);
			chunkStartPos = currentPos + 1;
			return c;	
		}
		else
		{
			return null;
		}
	}

	private Chunk processLineBreak(Chunk lineBreakChunk )
	{
		int returnCharacterCount = lineBreakChunk.getLength();
		if (null == lastFontInfo)
		{
			chunkStartPos = currentPos + returnCharacterCount;
			return Chunk.HARD_LINE_BREAK;
		}
		lineBreak = lineBreakChunk;
		Chunk c = new Chunk(new String(chunkText, chunkStartPos, currentPos-chunkStartPos), 
		baseOffset + chunkStartPos, baseLevel, runLevel, lastFontInfo);
		currentPos = currentPos + returnCharacterCount - 1;
		chunkStartPos = currentPos;
		return c;
	}

	private Chunk processLineBreak( )
	{
		Chunk lineBreakChunk = null;
		if ( chunkText[currentPos] == '\n' )
		{
			lineBreakChunk = Chunk.HARD_LINE_BREAK;
			lineBreakChunk.setText("\n");
		}
		else if (chunkText[currentPos] == '\r' )
		{
			lineBreakChunk = Chunk.HARD_LINE_BREAK;
			if ( currentPos + 1 < chunkText.length && chunkText[currentPos + 1] == '\n' )
			{
				lineBreakChunk.setText("\r\n");
			}
			else
			{
				lineBreakChunk.setText("\r");
			}
		}
		
		if ( lineBreakChunk != null )
		{
			return processLineBreak(lineBreakChunk);
		}
		return null;
	}
	
	public boolean hasMore()
	{
		return chunkText.length > chunkStartPos;
	}
	
	public Chunk getNext()
	{
		return buildChunk();
	}
	
}
