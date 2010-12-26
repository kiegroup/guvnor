/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testframework;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import org.drools.ClockType;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.time.impl.PseudoClockScheduler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * A class with some utilities for testing rules.
 * @author Michael Neale
 *
 */
public abstract class RuleUnit {

	/**
	 * Return a wm ready to go based on the rules in a drl at the specified uri (in the classpath).
	 */
	public StatefulSession getWorkingMemory(String uri)
			throws DroolsParserException, IOException, Exception {
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(new InputStreamReader(this.getClass()
				.getResourceAsStream(uri)));
		assertFalse(builder.getErrors().toString(), builder.hasErrors());
		RuleBase rb = RuleBaseFactory.newRuleBase();
		rb.addPackage(builder.getPackage());

		SessionConfiguration conf = new SessionConfiguration();
		conf.setClockType( ClockType.PSEUDO_CLOCK );
		StatefulSession wm = rb.newStatefulSession( conf, null );

        
		return wm;
	}
}
