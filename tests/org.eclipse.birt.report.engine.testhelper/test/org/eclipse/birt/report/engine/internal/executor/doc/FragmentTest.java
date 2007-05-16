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

package org.eclipse.birt.report.engine.internal.executor.doc;

import junit.framework.TestCase;

public class FragmentTest extends TestCase
{

	public void testFragment( )
	{

		Fragment fragment = new Fragment( new LongComparator( ) );
		Object[] leftEdge = new Long[]{new Long( 0 )};
		Object[] rightEdge = new Long[]{new Long( 2 )};
		fragment.addFragment( leftEdge, rightEdge );

		leftEdge = new Long[]{new Long( 4 )};
		rightEdge = new Long[]{new Long( 5 )};
		fragment.addFragment( leftEdge, rightEdge );

		leftEdge = new Long[]{new Long( 7 )};
		rightEdge = new Long[]{new Long( 7 )};
		fragment.addFragment( leftEdge, rightEdge );

		assertTrue( fragment.inFragment( new Long( 0 ) ) );
		assertTrue( fragment.inFragment( new Long( 1 ) ) );
		assertTrue( fragment.inFragment( new Long( 2 ) ) );
		assertTrue( !fragment.inFragment( new Long( 3 ) ) );
		assertTrue( fragment.inFragment( new Long( 4 ) ) );
		assertTrue( fragment.inFragment( new Long( 5 ) ) );
		assertTrue( !fragment.inFragment( new Long( 6 ) ) );
		assertTrue( fragment.inFragment( new Long( 7 ) ) );

		assertEquals( new Long( 0 ),
				fragment.getFragment( new Long( 0 ) ).index );
		assertEquals( null, fragment.getFragment( new Long( 1 ) ) );
		assertEquals( new Long( 2 ),
				fragment.getFragment( new Long( 2 ) ).index );
		assertEquals( null, fragment.getFragment( new Long( 3 ) ) );
		assertEquals( new Long( 4 ),
				fragment.getFragment( new Long( 4 ) ).index );
		assertEquals( new Long( 5 ),
				fragment.getFragment( new Long( 5 ) ).index );
		assertEquals( null, fragment.getFragment( new Long( 6 ) ) );
		assertEquals( new Long( 7 ),
				fragment.getFragment( new Long( 7 ) ).index );

		assertEquals( new Long( 2 ),
				fragment.getNextFragment( new Long( 0 ) ).index );
		assertEquals( new Long( 2 ),
				fragment.getNextFragment( new Long( 1 ) ).index );
		assertEquals( new Long( 4 ),
				fragment.getNextFragment( new Long( 2 ) ).index );
		assertEquals( new Long( 4 ),
				fragment.getNextFragment( new Long( 3 ) ).index );
		assertEquals( new Long( 5 ),
				fragment.getNextFragment( new Long( 4 ) ).index );
		assertEquals( new Long( 7 ),
				fragment.getNextFragment( new Long( 5 ) ).index );
		assertEquals( new Long( 7 ),
				fragment.getNextFragment( new Long( 6 ) ).index );
		assertEquals( null, fragment.getNextFragment( new Long( 7 ) ) );
	}
}
