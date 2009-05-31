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

package org.eclipse.birt.report.engine.ir;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.engine.EngineCase;

/**
 * Base class of rule tests
 * 
 */
abstract public class RuleTestCase extends EngineCase
{

	protected RuleDesign rule;

	public RuleTestCase( RuleDesign rl )
	{
		rule = rl;
	}

	/**
	 * Test all get/set accessors in base class
	 * 
	 * set values of the rule
	 * 
	 * then get the values one by one to test if they work correctly
	 */
	public void testBaseRule( )
	{
		String[] operator = {EngineIRConstants.MAP_OPERATOR_ANY,
				EngineIRConstants.MAP_OPERATOR_BETWEEN,
				EngineIRConstants.MAP_OPERATOR_EQ,
				EngineIRConstants.MAP_OPERATOR_FALSE,
				EngineIRConstants.MAP_OPERATOR_GE,
				EngineIRConstants.MAP_OPERATOR_GT,
				EngineIRConstants.MAP_OPERATOR_LE,
				EngineIRConstants.MAP_OPERATOR_LIKE,
				EngineIRConstants.MAP_OPERATOR_LT,
				EngineIRConstants.MAP_OPERATOR_NE,
				EngineIRConstants.MAP_OPERATOR_NOT_BETWEEN,
				EngineIRConstants.MAP_OPERATOR_NOT_NULL,
				EngineIRConstants.MAP_OPERATOR_NULL,
				EngineIRConstants.MAP_OPERATOR_TRUE};

		Expression<String> exp1 = newConstant( "exp1" );
		Expression<String> exp2 = newConstant( "exp2" );
		for ( int i = 0; i < operator.length; i++ )
		{
			//Set
			rule.setExpression( operator[i], exp1,
					exp2 );

			//Get
			assertEquals( rule.getOperator( ), operator[i] );
			assertEquals( rule.getValue1( ), exp1 );
			assertEquals( rule.getValue2( ), exp2 );
		}
		
		// special operators
		// IN
		String operator1 = EngineIRConstants.MAP_OPERATOR_IN;
		ArrayList values = new ArrayList();
		values.add("exp1");
		values.add("exp2");
		values.add("exp3");
		values.add("exp4");
		
		rule.setExpression( operator1, newConstant( values ) );
		Expression<? extends List> vs = rule.getValue1List();
		
		assertEquals(rule.getOperator(), operator1);
		assertFalse( vs.isExpression( ) );
		assertEquals(values, vs.getValue( ));
	}
}