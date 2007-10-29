package org.drools.brms.client;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.brms.client.packages.PackageManagerView;
import org.drools.brms.client.qa.QAManagerWidget;

/**
 * This is the package management feature. For managing packages (namespaces,
 * imports etc) for rule assets.
 *
 * This is also an alternative way of viewing packages.
 */
public class QAFeature extends JBRMSFeature {

	public static final String TESTING_AND_ANALYSIS = "QA";

	public static ComponentInfo init() {
		return new ComponentInfo(TESTING_AND_ANALYSIS,
				"Test, verify and analyse rules.") {
			public JBRMSFeature createInstance() {
				return new QAFeature();
			}
		};
	}

	public QAFeature() {
		initWidget(new QAManagerWidget());
	}

	public void onShow() {
	}
}