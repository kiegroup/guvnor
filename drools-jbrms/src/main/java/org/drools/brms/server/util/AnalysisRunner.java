package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.drools.analytics.Analyzer;
import org.drools.analytics.components.AnalyticsClass;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.Field;
import org.drools.analytics.dao.AnalyticsData;
import org.drools.analytics.dao.AnalyticsResult;
import org.drools.analytics.report.components.AnalyticsMessageBase;
import org.drools.analytics.report.components.Severity;
import org.drools.brms.client.rpc.AnalysisFactUsage;
import org.drools.brms.client.rpc.AnalysisFieldUsage;
import org.drools.brms.client.rpc.AnalysisReport;
import org.drools.brms.client.rpc.AnalysisReportLine;
import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.descr.PackageDescr;

import com.google.gwt.user.client.rpc.SerializableException;

public class AnalysisRunner {

	public AnalysisReport analyse(String drl) throws DroolsParserException, SerializableException {
		DrlParser p = new DrlParser();
		PackageDescr pkg = p.parse(drl);
		if (p.hasErrors()) {
			throw new DetailedSerializableException(
				"Unable to analyse rules due to syntax errors in the rules.",
				"Please correct syntax errors - build the package before trying the analysis again.");
		}
		Analyzer a = new Analyzer();
		a.addPackageDescr(pkg);
		a.fireAnalysis();
		AnalyticsResult res = a.getResult();

		AnalysisReport report = new AnalysisReport();
		report.errors = doLines(res.getBySeverity(Severity.ERROR));
		report.warnings = doLines(res.getBySeverity(Severity.WARNING));
		report.notes = doLines(res.getBySeverity(Severity.NOTE));
		report.factUsages = doFactUsage(res.getAnalyticsData());
		return report;
	}

	private AnalysisFactUsage[] doFactUsage(AnalyticsData analyticsData) {
		List<AnalysisFactUsage> factUsage = new ArrayList<AnalysisFactUsage>();
		Collection<AnalyticsClass> classes = analyticsData.getAllClasses();
		for (AnalyticsClass c : classes) {
			AnalysisFactUsage fact = new AnalysisFactUsage();
			fact.name = c.getName();
			List<AnalysisFieldUsage> fieldUsage = new ArrayList<AnalysisFieldUsage>();
			Set<Field> flds = c.getFields();
			for (Field f : flds) {
				AnalysisFieldUsage fu = new AnalysisFieldUsage();
				fu.name = f.getName();
				Collection<AnalyticsRule> cr = analyticsData.getRulesByFieldId(f.getId());
				List<String> ruleNames = new ArrayList<String>();
				for (AnalyticsRule analyticsRule : cr) {
					ruleNames.add(analyticsRule.getRuleName());
				}
				fu.rules = ruleNames.toArray(new String[ruleNames.size()]);
				fieldUsage.add(fu);
			}
			fact.fields = fieldUsage.toArray(new AnalysisFieldUsage[fieldUsage.size()]);
			factUsage.add(fact);
		}
		return factUsage.toArray(new AnalysisFactUsage[factUsage.size()]);
	}

	private AnalysisReportLine[] doLines(
			Collection<AnalyticsMessageBase> msgs) {
		List<AnalysisReportLine> lines = new ArrayList<AnalysisReportLine>();
		for (AnalyticsMessageBase m : msgs) {
			AnalysisReportLine line = new AnalysisReportLine();
			line.description = m.getMessage();
			line.reason = m.getFaulty().toString();
			Collection<?> causes = m.getCauses();
			List<String> causeList = new ArrayList<String>();
			for (Object c : causes) {
				causeList.add(c.toString());
			}
			line.cause = causeList.toArray(new String[causeList.size()]);
			lines.add(line);
		}
		return lines.toArray(new AnalysisReportLine[lines.size()]);
	}

}
