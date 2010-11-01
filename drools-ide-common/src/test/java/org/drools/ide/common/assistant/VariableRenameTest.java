package org.drools.ide.common.assistant;

import junit.framework.TestCase;

import org.drools.ide.common.assistant.engine.DRLParserEngine;
import org.drools.ide.common.assistant.info.drl.DRLRuleRefactorInfo;
import org.drools.ide.common.assistant.info.drl.RuleBasicContentInfo;
import org.drools.ide.common.assistant.option.AssistantOption;
import org.drools.ide.common.assistant.option.RenameAssistantOption;
import org.drools.ide.common.assistant.refactor.drl.VariableRename;

public class VariableRenameTest extends TestCase {

    private DRLParserEngine engine;
    private DRLRuleRefactorInfo info;

    public void test() {
        String rule = "package com.sample\n\n";
        rule += "import com.sample.DroolsTest.Message;\n\n";
        rule += "rule \"Hello World\"\n";
        rule += "    when\n"; 
        rule += "        m : Message( $status : status == Message.HELLO, message :message )\n"; 
        rule += "        $m2 : Message( message2 :message )";
        rule += "    then\n";
        rule += "        System.out.println( message ); \n";
        rule += "        System.out.println(message ); \n";
        rule += "        System.out.println( message); \n";
        rule += "        System.out.println(message); \n";
        rule += "        $m.setMessage( \"Goodbye cruel world\", message);\n"; 
        rule += "        $m.setMessage( \"Goodbye cruel world\" +message);\n";
        rule += "        $m.setMessage( \"Goodbye cruel world\" + message );\n";
        rule += "        $m.setStatus( Message.GOODBYE );\n"; 
        rule += "        update( $m ) ;\n"; 
        rule += "end";

        engine = new DRLParserEngine(rule);
        info = (DRLRuleRefactorInfo) engine.parse();
        RuleBasicContentInfo content = info.getContentAt(87);

        RenameAssistantOption assistantOption = new RenameAssistantOption("rename variable", "message", content, 87);

        AssistantOption result = VariableRename.execute(assistantOption, "msg");

        assertTrue(result.getContent().contains("m : Message( $status : status == Message.HELLO, msg :message )"));
        assertTrue(result.getContent().contains("System.out.println( msg );"));
        assertTrue(result.getContent().contains("System.out.println(msg );"));
        assertTrue(result.getContent().contains("System.out.println( msg);"));
        assertTrue(result.getContent().contains("$m.setMessage( \"Goodbye cruel world\" + msg );"));

    }

}
