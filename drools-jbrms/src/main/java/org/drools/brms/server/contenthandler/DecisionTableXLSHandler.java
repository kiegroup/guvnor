package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.io.StringReader;

import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentPackageAssembler.ErrorLogger;
import org.drools.compiler.DroolsParserException;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is for handling XLS content (classic decision tables).
 * 
 * @author Michael Neale
 */
public class DecisionTableXLSHandler extends ContentHandler implements IRuleAsset {


    public void retrieveAssetContent(RuleAsset asset,
                                     PackageItem pkg,
                                     AssetItem item) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void storeAssetContent(RuleAsset asset,
                                  AssetItem repoAsset) throws SerializableException {
        //do nothing, as we have an attachment
    }

    public void assembleDRL(BRMSPackageBuilder builder, AssetItem asset, StringBuffer buf) {
        SpreadsheetCompiler comp = new SpreadsheetCompiler();
        String drl = comp.compile( asset.getBinaryContentAttachment(), InputType.XLS );
        buf.append( drl );
    }

    public void compile(BRMSPackageBuilder builder, AssetItem asset, ErrorLogger logger) throws DroolsParserException,
                                                                                        IOException {
        SpreadsheetCompiler comp = new SpreadsheetCompiler();
        String drl = comp.compile( asset.getBinaryContentAttachment(), InputType.XLS );
        builder.addPackageFromDrl( new StringReader(drl) );
        
    }

}
