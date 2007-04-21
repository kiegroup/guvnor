package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentAssemblyError;
import org.drools.brms.server.builder.ContentPackageAssembler;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.ExpanderException;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DefaultExpander;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

public class DSLRuleContentHandler extends ContentHandler implements IRuleAsset {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        RuleContentText text = new RuleContentText();
        text.content = item.getContent();
        

        asset.content = text;
        
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {

        RuleContentText text = (RuleContentText) asset.content;
        repoAsset.updateContent( text.content );

    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) throws DroolsParserException,
                                                                    IOException {
        DefaultExpander expander = getExpander( builder, asset, logger );
        
        //add the rule keyword if its 'stand alone'
        String source = asset.getContent();
        if (DRLFileContentHandler.isStandAloneRule(source)) {
            source = wrapRule( asset, source );
        }
        
        //expand and check for errors
        String drl = expander.expand( source );
        if (expander.hasErrors()) {
            List exErrs = expander.getErrors();
            for ( Iterator iter = exErrs.iterator(); iter.hasNext(); ) {
                ExpanderException ex = (ExpanderException) iter.next();
                logger.logError( new ContentAssemblyError(asset, ex.getMessage()));
            }
            return;
        }
        
        
        builder.addPackageFromDrl( new StringReader(drl) );
    }

    private DefaultExpander getExpander(BRMSPackageBuilder builder, AssetItem asset, ContentPackageAssembler.ErrorLogger logger) {
        List<DSLMappingFile> dsls = builder.getDSLMappingFiles();
        if (dsls == null || dsls.size() == 0) {
            logger.logError( new ContentAssemblyError(asset, "This rule asset requires a DSL, yet none were configured in the package.") );
        }
        
        DefaultExpander expander = new DefaultExpander();
        for ( DSLMappingFile file : dsls ) {
            expander.addDSLMapping( file.getMapping() );
        }
        return expander;
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf) {
        //add the rule keyword if its 'stand alone'
        String source = asset.getContent();
        if (DRLFileContentHandler.isStandAloneRule(source)) {
            source = wrapRule( asset, source );
        }
        
        DefaultExpander expander = new DefaultExpander();
        for ( DSLMappingFile file : builder.getDSLMappingFiles()) {
            expander.addDSLMapping( file.getMapping() );
        }        
        buf.append( expander.expand( source ) );
        
    }

    private String wrapRule(AssetItem asset, String source) {
        return "rule '" + asset.getName() + "' \n" + source + "\nend";
    }


    

}
