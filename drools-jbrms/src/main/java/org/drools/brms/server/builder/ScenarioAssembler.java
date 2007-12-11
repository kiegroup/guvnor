package org.drools.brms.server.builder;

import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassTypeResolver;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.common.InternalWorkingMemory;
import org.drools.repository.PackageItem;
import org.drools.rule.Package;
import org.drools.testframework.ScenarioRunner;

public class ScenarioAssembler {


	public ScenarioAssembler(PackageItem item, Package pkg, Scenario sc) {
		List<JarInputStream> jars = BRMSPackageBuilder.getJars(item);
		ClassTypeResolver res = new ClassTypeResolver(pkg.getImports().keySet(), BRMSPackageBuilder.createClassLoader(jars));

//		RuleBase rb = RuleBaseFactory.newRuleBase();
//		rb.addPackage(pkg);
//		InternalWorkingMemory iwm = (InternalWorkingMemory) rb.newStatefulSession();
//
//		ScenarioRunner runner = new ScenarioRunner(sc, res, iwm);
	}

}
