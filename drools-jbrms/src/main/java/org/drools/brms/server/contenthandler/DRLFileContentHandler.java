package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.compiler.DroolsParserException;
import org.drools.repository.AssetItem;

public class DRLFileContentHandler extends PlainTextContentHandler implements IRuleAsset {

    public void compile(BRMSPackageBuilder builder, AssetItem asset) throws DroolsParserException, IOException {
        String content = asset.getContent();
        if (isStandAloneRule( content )) {
            content = "rule '" + asset.getName() + "'\n"  + content + "\nend\n";
        } 
        builder.addPackageFromDrl( new StringReader(content) );
        
        
    }

    /**
     * This will try and sniff ouf if its a stand alone rule which
     * will use the asset name as the rule name, or if it should be treated as a package
     * (in the latter case, the content is passed as it to the compiler).
     */
    boolean isStandAloneRule(String content) {
        StringTokenizer st = new StringTokenizer(content, " ");
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equals( "package" ) || 
                    tok.equals( "rule" ) || 
                    tok.equals( "end" ) ||
                    tok.equals( "function" )) {
                return false;
            }
        }
        return true;
        
    }    
    
}
