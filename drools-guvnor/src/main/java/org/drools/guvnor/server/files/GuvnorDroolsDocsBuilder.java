package org.drools.guvnor.server.files;

import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.drools.compiler.DroolsParserException;
import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;
import org.drools.doc.DroolsDocsBuilder;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.builder.BRMSPackageBuilder;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.VersionableItem;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class GuvnorDroolsDocsBuilder extends DroolsDocsBuilder {

    private static List<String> formats = new ArrayList<String>();

    static {
        formats.add( AssetFormats.DRL );
        formats.add( AssetFormats.BUSINESS_RULE );
    }

    private GuvnorDroolsDocsBuilder(PackageItem packageItem) throws DroolsParserException {
        super( createDrlPackageData( packageItem ) );
    }

    protected static DrlPackageParser createDrlPackageData(PackageItem packageItem) {

        List<DrlRuleParser> rules = new ArrayList<DrlRuleParser>();

        // Get And Fill Rule Data
        Iterator<AssetItem> assets = packageItem.getAssets();
        while ( assets.hasNext() ) {
            AssetItem assetItem = (AssetItem) assets.next();

            if ( formats.contains( assetItem.getFormat() ) && !assetItem.getDisabled() && !assetItem.isArchived() ) {
                String drl = getDRL( assetItem );
                if ( drl != null ) {
                    DrlRuleParser ruleData = DrlRuleParser.findRulesDataFromDrl( drl ).get( 0 );

                    // Add info about categories
                    List<String> categories = new ArrayList<String>();
                    for ( CategoryItem ci : assetItem.getCategories() ) {
                        categories.add( ci.getName() );
                    }

                    ruleData.getOtherInformation().put( "Categories",
                                                   categories );
                    ruleData.getMetadata().addAll( createMetaData( assetItem ) );

                    rules.add( ruleData );
                }
            }
        }

        String header = ServiceImplementation.getDroolsHeader( packageItem );
        List<String> globals = DrlPackageParser.findGlobals( header );

        // Get And Fill Package Data
        return new DrlPackageParser( packageItem.getName(),
                                   packageItem.getDescription(),
                                   rules,
                                   globals,
                                   createMetaData( packageItem ),
                                   new HashMap<String, List<String>>() );

    }

    private static List<String> createMetaData(VersionableItem versionableItem) {
        List<String> list = new ArrayList<String>();

        Format formatter = getFormatter();

        list.add( "Creator :" + versionableItem.getCreator() );
        list.add( "Created date :" + formatter.format( versionableItem.getCreatedDate().getTime() ) );
        list.add( "Last contributor :" + versionableItem.getLastContributor() );
        list.add( "Last modified :" + formatter.format( versionableItem.getLastModified().getTime() ) );

        return list;
    }

    public static GuvnorDroolsDocsBuilder getInstance(PackageItem packageItem) throws DroolsParserException {
        return new GuvnorDroolsDocsBuilder( packageItem );
    }

    private static String getDRL(AssetItem item) {
        ContentHandler handler = ContentManager.getHandler( item.getFormat() );
        StringBuffer buf = new StringBuffer();
        if ( handler.isRuleAsset() ) {

            BRMSPackageBuilder builder = new BRMSPackageBuilder();
            // now we load up the DSL files
            builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles( item.getPackage(),
                                                                        new BRMSPackageBuilder.DSLErrorEvent() {
                                                                            public void recordError(AssetItem asset,
                                                                                                    String message) {
                                                                                // ignore at this point...
                                                                            }
                                                                        } ) );
            ((IRuleAsset) handler).assembleDRL( builder,
                                                item,
                                                buf );
        } else {
            return null;
        }

        return buf.toString();
    }
}
