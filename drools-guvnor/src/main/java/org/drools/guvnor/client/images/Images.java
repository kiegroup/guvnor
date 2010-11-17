/*
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

package org.drools.guvnor.client.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {
	@Source("rule_asset.gif")
	ImageResource ruleAsset();
	
	@Source("find.gif")
	ImageResource find();
	
	@Source("inbox.gif")
	ImageResource inbox();
	
	@Source("category_small.gif")
	ImageResource categorySmall();	
	
	@Source("status_small.gif")
	ImageResource statusSmall();	
	
	@Source("chart_organisation.gif")
	ImageResource chartOrganisation();
	
	@Source("business_rule.gif")
	ImageResource businessRule();
	
	@Source("spreadsheet_small.gif")
	ImageResource spreadsheetSmall();
	
	@Source("gdst.gif")
	ImageResource gdst();
	
	@Source("test_manager.gif")
	ImageResource testManager();
	
	@Source("package.gif")
	ImageResource packages();
		
	@Source("empty_package.gif")
	ImageResource emptyPackage();
	
	@Source("dsl.gif")
	ImageResource dsl();
	
	@Source("enumeration.gif")
	ImageResource enumeration();
	
	@Source("function_assets.gif")
	ImageResource functionAssets();	
	
	@Source("model_asset.gif")
	ImageResource modelAsset();
	
	@Source("workingset.gif")
	ImageResource workingset();
	
	@Source("new_file.gif")
	ImageResource newFile();
	
	@Source("ruleflow_small.gif")
	ImageResource ruleflowSmall();	
	
	@Source("technical_rule_assets.gif")
	ImageResource technicalRuleAssets();
	
	@Source("new_package.gif")
	ImageResource newPackage();
	
	@Source("new_enumeration.gif")
	ImageResource newEnumeration();
	
	@Source("refresh.gif")
	ImageResource refresh();
		
	@Source("new_template.gif")
	ImageResource newTemplate();
	
	@Source("analyze.gif")
	ImageResource analyze();
	
	@Source("deploy.gif")
	ImageResource deploy();
	
	@Source("snapshot_small.gif")
	ImageResource snapshotSmall();
	
	@Source("rules.gif")
	ImageResource rules();		
	
	@Source("tag.png")
	ImageResource tag();	
	
	@Source("backup_small.gif")
	ImageResource backupSmall();	
	
	@Source("error.gif")
	ImageResource error();	
	
	@Source("icoUsers_small.gif")
	ImageResource icoUsersSmall();	
	
	@Source("icoUsers.gif")
	ImageResource icoUsers();	
	
	@Source("save_edit.gif")
	ImageResource saveEdit();	
	
	@Source("rule_verification.png")
	ImageResource ruleVerification();	
	
	@Source("information.gif")
	ImageResource information();	
	
	@Source("config.png")
	ImageResource config();	
	
	@Source("close.gif")
	ImageResource close();	
	
	@Source("scrollleft.gif")
	ImageResource scrollLeft();
	
	@Source("scrollright.gif")
	ImageResource scrollRight();
}