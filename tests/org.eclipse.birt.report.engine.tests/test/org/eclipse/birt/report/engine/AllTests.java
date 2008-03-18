/*******************************************************************************
 * Copyright (c) 2004, 2005 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 */

public class AllTests
{

	public static Test suite( )
	{
		TestSuite suite = new TestSuite(
				"Test for org.eclipse.birt.report.engine" );
		// $JUnit-BEGIN$

		// 136 testcases generated by AllTests.testLists
		suite.addTestSuite( org.eclipse.birt.report.engine.adapter.ExpressionUtilTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.CachedImageHandlerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.CancelOnErrorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.DataIDTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.DataSetIDTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.DataSourceCompareTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.EngineExceptionTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.EngineTaskCancelTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.EngintTaskLoggerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.GetParameterDefinitionTaskTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.GetParameterGroupDefnTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.HTMLActionHandlerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.HTMLCompleteImageHandlerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.HTMLServerImageHandlerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.InstanceIDTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.MutipleThreadRenderTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.PageHandlerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ParameterConverterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ProgressiveViewingTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.RelativeHyperlinkInReportDocumentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.RenderTaskTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ReportEngineFactoryTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ReportEngineTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ReportRunnableTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ReportRunnerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.ReportletTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.RunTaskTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.TOCNodeTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.TOCStyleTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.TOCTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.TimeZoneTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.document.ReportDocumentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.impl.DataExtractionTaskTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.impl.GetParameterDefinitionTaskTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.impl.ParameterPromptTextTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.impl.ReportDocumentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.iv.IVTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.script.RowDataTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.script.element.ElementTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.api.script.instance.InstanceTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.content.ReportContentReaderAndWriterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.css.CSSPaserTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.css.dom.StyleDeclarationTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.css.engine.PerfectHashTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.data.dte.DataEngineTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.data.dte.NamedExpressionTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.data.dte.ReportQueryBuilderTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.emitter.EmbeddedHyperlinkProcessorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.emitter.XMLWriterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.DataItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.ExecutorManagerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.GridItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.ImageItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.LabelItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.ListItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.ListingElementExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.MultiLineItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.TableItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.TextItemExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.buffermgr.TableTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.css.CssParserTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.css.HTMLProcessorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.template.TemplateExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.i18n.EngineResourceHandleTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.impl.ReportRunnerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.document.OffsetIndexReaderWriterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.document.v2.ContentTreeCacheTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.document.v2.PageHintTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.document.v2.ReportContentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.executor.doc.FragmentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.executor.doc.ReportPageReaderTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.executor.doc.SegmentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.executor.doc.TreeFragmentTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.internal.executor.load.PageSequenceIteratorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ActionTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.CellTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ColumnTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.DataItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.DimensionTypeTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.DrillThroughActionDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.DynamicTextItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.EngineIRIOTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.EngineIRReaderTest.class );

		suite.addTestSuite( org.eclipse.birt.report.engine.ir.FreeFormItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.GraphicMasterPageTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.GridItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.HighlightTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ImageItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.LabelItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ListBandTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ListGroupTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ListItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.MapRuleTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.MapTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.PageSequenceTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.PageSetupTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.ReportTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.RowTypeTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.SimpleMasterPageTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.TableBandTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.TableGroupTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.TableItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.ir.TextItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.content.BlockStackingExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.content.ListContainerExecutorTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.html.HTMLLayoutTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFImageLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFLineAreaLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFPageLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFTableGroupLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFTableLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.PDFTextLMTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.font.FontConfigReaderTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.layout.pdf.hyphen.DefaultWordRecognizerTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.DataDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.DynamicTextItemDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.FreeFormDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.GridItemDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.HighlightTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.ImageItemDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.LabelItemDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.ListDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.MapDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.PageSetupTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.StyleDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.SuppressDuplicateDataItemTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.TableItemDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.TextDesignTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.parser.TextParserTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.presentation.HtmlPaginateEmitterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.presentation.XMLContentReaderWriterTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.toc.TOCBuilderReadV1Test.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.toc.TOCBuilderTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.util.FileUtilTest.class );
		suite.addTestSuite( org.eclipse.birt.report.engine.executor.optimize.ExecutionOptimizeTest.class );
		// $JUnit-END$
		return suite;
	}

	static String[] skipTests = new String[]{
		"org.eclipse.birt.report.engine.layout.impl.AbstractLayoutManagerTest.class",
		"org.eclipse.birt.report.engine.layout.pdf.PDFLayoutTest.class"
		
	};

	public static void main( String args[] ) throws IOException
	{
		File root = new File( "./test/" );
		ArrayList test = new ArrayList( );

		listTests( test, root.getCanonicalPath( ), root );

		root = new File( "../org.eclipse.birt.report.engine.testhelper/test/" );
		listTests( test, root.getCanonicalPath( ), root );
		
		Object[] tests = test.toArray( );
		java.util.Arrays.sort( tests );

		System.out.println( "\t// " + tests.length
				+ " testcases generated by AllTests.testLists" );
		for ( int i = 0; i < tests.length; i++ )
		{
			System.out.println( "\t\tsuite.addTestSuite( " + tests[i] + " );" );
		}
	}

	static protected void listTests( java.util.ArrayList tests, String root,
			File folder ) throws IOException
	{
		if ( folder.isDirectory( ) )
		{
			File[] files = folder.listFiles( );
			for ( int i = 0; i < files.length; i++ )
			{
				listTests( tests, root, files[i] );
			}
		}
		else
		{
			String name = folder.getCanonicalPath( );
			if ( name.endsWith( "Test.java" ) )
			{
				name = name.substring( root.length( ) );
				name = name.replace( File.separatorChar, '.' );
				if ( name.charAt( 0 ) == '.' )
				{
					name = name.substring( 1 );
				}
				name = name.replaceAll( ".java", ".class" );
				if ( !isSkipped( name ) )
				{
					tests.add( name );
				}
			}
		}
	}

	static boolean isSkipped( String name )
	{
		for ( int i = 0; i < skipTests.length; i++ )
		{
			if ( skipTests[i].equals( name ) )
			{
				return true;
			}
		}
		return false;
	}
}
