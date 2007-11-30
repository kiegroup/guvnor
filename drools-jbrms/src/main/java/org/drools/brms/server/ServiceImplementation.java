package org.drools.brms.server;
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



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.log4j.Logger;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.testing.Scenario;
import org.drools.brms.client.rpc.BuilderResult;
import org.drools.brms.client.rpc.DetailedSerializableException;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;
import org.drools.brms.client.rpc.ValidatedResponse;
import org.drools.brms.server.builder.BRMSPackageBuilder;
import org.drools.brms.server.builder.ContentAssemblyError;
import org.drools.brms.server.builder.ContentPackageAssembler;
import org.drools.brms.server.contenthandler.ContentHandler;
import org.drools.brms.server.contenthandler.IRuleAsset;
import org.drools.brms.server.contenthandler.IValidating;
import org.drools.brms.server.util.BRMSSuggestionCompletionLoader;
import org.drools.brms.server.util.MetaDataMapper;
import org.drools.brms.server.util.TableDisplayHandler;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.repository.AssetHistoryIterator;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryAdministrator;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.StateItem;
import org.drools.repository.VersionableItem;
import org.drools.testframework.ScenarioRunner;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.security.Restrict;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is the implementation of the repository service to drive the GWT based front end.
 *
 * @author Michael Neale
 */
@Name("org.drools.brms.client.rpc.RepositoryService")
@AutoCreate
public class ServiceImplementation
    implements
    RepositoryService {

    @In
    public RulesRepository repository;


    private static final long serialVersionUID = 400L;
    private static final DateFormat dateFormatter = DateFormat.getInstance();
    private static final Logger log = Logger.getLogger( ServiceImplementation.class );
    private MetaDataMapper metaDataMapper = new MetaDataMapper();

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] loadChildCategories(String categoryPath) {

        CategoryItem item = repository.loadCategory( categoryPath );
        List children = item.getChildTags();
        String[] list = new String[children.size()];
        for ( int i = 0; i < list.length; i++ ) {
            list[i] = ((CategoryItem) children.get( i )).getName();
        }
        return list;

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public Boolean createCategory(String path,
                                  String name,
                                  String description) {


        log.info( "USER:" + repository.getSession().getUserID()
                  + " CREATING cateogory: [" + name + "] in path [" + path + "]" );

        if (path == null || "".equals(path)) {
            path = "/";
        }

        CategoryItem item = repository.loadCategory( path );
        item.addCategory( name, description );
        repository.save();
        return Boolean.TRUE;
    }


    /**
     * This will create a new asset. It will be saved, but not checked in.
     * The initial state will be the draft state.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createNewRule(String ruleName,
                                 String description,
                                 String initialCategory,
                                 String initialPackage,
                                 String format) throws SerializableException {

        log.info( "USER:" + repository.getSession().getUserID() +
                           " CREATING new asset name [" + ruleName + "] in package [" + initialPackage + "]" );

        try {

            PackageItem pkg = repository.loadPackage( initialPackage );
            AssetItem asset = pkg.addAsset( ruleName, description, initialCategory, format );

            applyPreBuiltTemplates( ruleName,
                                    format,
                                    asset );
            repository.save();


            return asset.getUUID();
        } catch (RulesRepositoryException e) {
        	if (e.getCause() instanceof ItemExistsException) {
        		return "DUPLICATE";
        	} else {
        		throw new SerializableException(e.getMessage());
        	}
        }

    }


    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void deleteUncheckedRule(String uuid, String initialPackage) {
        AssetItem asset = repository.loadAssetByUUID( uuid );
        asset.remove();
        repository.save();
    }

    /**
     * For some format types, we add some sugar by adding a new template.
     */
    private void applyPreBuiltTemplates(String ruleName,
                                        String format,
                                        AssetItem asset) {
        if (format.equals( AssetFormats.DSL_TEMPLATE_RULE )) {
            asset.updateContent( "when\n\nthen\n" );
            asset.updateDescription( "A dsl is a language mapping from a domain specific language to the rule language." );
        } else if (format.equals( AssetFormats.FUNCTION )) {
            asset.updateContent( "function <returnType> " + ruleName + "(<args here>) {\n\n\n}" );
        } else if (format.equals( AssetFormats.DSL )) {
            asset.updateContent( "[when]Condition sentence template {var}=" +
                    "rule language mapping {var}\n" +
                    "[then]Action sentence template=rule language mapping");
        } else if (format.equals( AssetFormats.DECISION_SPREADSHEET_XLS )) {
            asset.updateBinaryContentAttachment( this.getClass().getResourceAsStream( "/SampleDecisionTable.xls" ) );
            asset.updateBinaryContentAttachmentFileName( "SampleDecisionTable.xls" );
        } else if (format.equals( AssetFormats.ENUMERATION )) {
            asset.updateDescription( "An enumeration is a mapping from fields to a list of values." +
                    "This will mean the rule editor will show a drop down for fields, instead of a text box." +
                    "The format of this is: 'FactType.fieldName ': ['Value1', 'Value2']\n" +
                    "You can add more mappings by adding in more lines. " +
                    "\nFor example:\n\n" +
                    "'Person.sex' : ['M', 'F']\n" +
                     "'Person.rating' : ['High', 'Low']\n\n" +
                     "You can also ad display aliases (so the value used in the rule is separate to the one displayed:\n"  +
                    "'Person.sex' : ['M=Male', 'F=Female']\n" +
                    "in the above case, the 'M=Male' means that 'Male' will be displayed as an item in a drop down box, but the value 'M' will be used in the rule. "
                    );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData[] listPackages() {
        Iterator pkgs = repository.listPackages();
        List<PackageConfigData> result = new ArrayList<PackageConfigData>();
        while(pkgs.hasNext()) {
            PackageItem pkg = (PackageItem) pkgs.next();

            PackageConfigData data = new PackageConfigData();
            data.uuid = pkg.getUUID();
            data.name = pkg.getName();

            result.add( data );
        }

        Collections.sort( result, new Comparator<Object>() {

            public int compare(final Object o1,
                               final Object o2) {
                final PackageConfigData d1 = (PackageConfigData) o1;
                final PackageConfigData d2 = (PackageConfigData) o2;
                return d1.name.compareTo( d2.name );
            }

        });
        PackageConfigData[] resultArr = result.toArray( new PackageConfigData[result.size()] );

        return resultArr;
    }


    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadRuleListForCategories(String categoryPath) throws SerializableException {
        long start = System.currentTimeMillis();

        List list = repository.findAssetsByCategory( categoryPath );
        TableDisplayHandler handler = new TableDisplayHandler(TableDisplayHandler.DEFAULT_TABLE_TEMPLATE);
        //log.info("time for load: " + (System.currentTimeMillis() - start) );
        return handler.loadRuleListTable( list.iterator(), -1 );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableConfig loadTableConfig(String listName) {
        TableDisplayHandler handler = new TableDisplayHandler(listName);
        return handler.loadTableConfig();
    }


    /**
     * This actually does the hard work of loading up an asset based
     * on its format.
     */
    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public RuleAsset loadRuleAsset(String uuid) throws SerializableException {

        AssetItem item = repository.loadAssetByUUID( uuid );
        RuleAsset asset = new RuleAsset();
        asset.uuid = uuid;


        //load standard meta data
        asset.metaData = populateMetaData( item );

        // get package header
        PackageItem pkgItem = repository.loadPackage( asset.metaData.packageName );

        //load the content
        ContentHandler handler = ContentHandler.getHandler( asset.metaData.format );
        handler.retrieveAssetContent(asset, pkgItem, item);

        return asset;
    }


    /**
     * read in the meta data, populating all dublin core and versioning stuff.
     */
    MetaData populateMetaData(VersionableItem item) {
        MetaData meta = new MetaData();

        meta.status = (item.getState() != null) ? item.getState().getName() : "";

        metaDataMapper.copyToMetaData( meta, item );

        meta.createdDate = calendarToDate(item.getCreatedDate());
        meta.lastModifiedDate = calendarToDate( item.getLastModified() );

        return meta;
    }

    /**
     * Populate meta data with asset specific info.
     */
    MetaData populateMetaData(AssetItem item) {
        MetaData meta = populateMetaData( (VersionableItem ) item);
        meta.packageName = item.getPackageName();

        List cats = item.getCategories();
        meta.categories = new String[cats.size()];
        for ( int i = 0; i < meta.categories.length; i++ ) {
            CategoryItem cat = (CategoryItem) cats.get(i);
            meta.categories[i] = cat.getFullPath();
        }
        meta.dateEffective = calendarToDate( item.getDateEffective() );
        meta.dateExpired = calendarToDate( item.getDateExpired() );
        return meta;

    }

    private Date calendarToDate(Calendar createdDate) {
        if (createdDate == null) return null;
        return createdDate.getTime();
    }

    private Calendar dateToCalendar(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        return cal;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String checkinVersion(RuleAsset asset) throws SerializableException {

    	        log.info( "USER:" + repository.getSession().getUserID() +
        " CHECKING IN asset: [" + asset.metaData.name + "] UUID: [" + asset.uuid + "]  ARCHIVED [" + asset.archived + "]");


        AssetItem repoAsset = repository.loadAssetByUUID( asset.uuid );
        if (asset.metaData.lastModifiedDate.before( repoAsset.getLastModified().getTime())  ) {
        	return "ERR: Unable to save this asset, as it has been recently updated by [" + repoAsset.getLastContributor() + "]";
        }


        repoAsset.archiveItem( asset.archived );
        MetaData meta = asset.metaData;

        metaDataMapper.copyFromMetaData( meta, repoAsset );

        repoAsset.updateDateEffective( dateToCalendar( meta.dateEffective ) );
        repoAsset.updateDateExpired( dateToCalendar( meta.dateExpired ) );


        repoAsset.updateCategoryList( meta.categories );
        ContentHandler handler = ContentHandler.getHandler( repoAsset.getFormat() );//new AssetContentFormatHandler();
        handler.storeAssetContent( asset, repoAsset );

        if (!(asset.metaData.format.equals(AssetFormats.TEST_SCENARIO))
        		||
        		asset.metaData.format.equals(AssetFormats.ENUMERATION)) {
        		repoAsset.getPackage().updateBinaryUpToDate(false);

        }

        repoAsset.checkin( meta.checkinComment );

        return repoAsset.getUUID();
    }





	@WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadAssetHistory(String uuid) throws SerializableException {

        List<TableDataRow> result = new ArrayList<TableDataRow>();

        AssetItem item = repository.loadAssetByUUID( uuid );
        AssetHistoryIterator it = item.getHistory();


        //MN Note: this uses the lazy iterator, but then loads the whole lot up, and returns it.
        //The reason for this is that the GUI needs to show things in numeric order by the version number.
        //When a version is restored, its previous version is NOT what you thought it was - due to how JCR works
        // (its more like CVS then SVN). So to get a linear progression of versions, we use the incrementing version number,
        // and load it all up and sort it. This is not ideal.
        // In future, we may do a "restore" instead just by copying content into a new version, not restoring a node,
        // in which case the iterator will be in order (or you can just walk all the way back).
        //So if there are performance problems with looking at lots of historical versions, look at this nasty bit of code.
        while ( it.hasNext() ) {
            AssetItem historical = (AssetItem) it.next();//new AssetItem(repo, element);
            long versionNumber = historical.getVersionNumber();
            if (! (versionNumber == 0)
                            && ! (versionNumber == item.getVersionNumber() ))
                {
                TableDataRow row = new TableDataRow();
                    row.id = historical.getVersionSnapshotUUID();
                    row.values = new String[4];
                    row.values[0] = Long.toString( historical.getVersionNumber());
                    row.values[1] = historical.getCheckinComment();
                    row.values[2] = dateFormatter.format( historical.getLastModified().getTime() );
                    row.values[3] = historical.getStateDescription();
                    result.add( row );
            }
        }

        if (result.size() == 0) return null;
        TableDataResult table = new TableDataResult();
        table.data = result.toArray(new TableDataRow[result.size()]);

        return table;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult loadArchivedAssets() throws SerializableException {

        List<TableDataRow> result = new ArrayList<TableDataRow>();

        AssetItemIterator it = repository.findArchivedAssets();

        while ( it.hasNext() ) {
            AssetItem archived = (AssetItem) it.next();

            TableDataRow row = new TableDataRow();
                row.id = archived.getUUID();
                row.values = new String[5];

                row.values[0] = archived.getFormat();
                row.values[1] = archived.getPackageName();
                row.values[2] = archived.getName();
                row.values[3] = archived.getLastContributor();
                row.values[4] = archived.getLastModified().getTime().toLocaleString();

                result.add( row );
        }

        if (result.size() == 0) {
            return null;
        }

        TableDataResult table = new TableDataResult();
        table.data = result.toArray(new TableDataRow[result.size()]);

        return table;
    }



    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void restoreVersion(String versionUUID,
                                 String assetUUID,
                                 String comment) {
        AssetItem old = repository.loadAssetByUUID( versionUUID );
        AssetItem head = repository.loadAssetByUUID( assetUUID );

        log.info( "USER:" + repository.getSession().getUserID() +
                           " RESTORE of asset: [" + head.getName() + "] UUID: [" + head.getUUID() + "] with historical version number: [" + old.getVersionNumber() );


        repository.restoreHistoricalAsset( old,
                                     head,
                                     comment );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public byte[] exportRepository() throws SerializableException {

        log.info( "USER:" + repository.getSession().getUserID() +
                           " EXPORTING repository");

        byte [] exportedOutput = null;
        try {
             exportedOutput =  repository.exportRulesRepository();
        } catch ( Exception e ) {
            throw new SerializableException( "Unable to export repository" );
        }
        return exportedOutput;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createPackage(String name,
                                String description) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() +
                           " CREATING package [" + name + "]" );
        PackageItem item = repository.createPackage( name, description );

        return item.getUUID();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public PackageConfigData loadPackageConfig(String uuid) {
        PackageItem item = repository.loadPackageByUUID( uuid );

        PackageConfigData data = new PackageConfigData();
        data.uuid = item.getUUID();
        data.header = item.getHeader();
        data.externalURI = item.getExternalURI();
        data.description = item.getDescription();
        data.name = item.getName();
        data.lastModified = item.getLastModified().getTime();
        data.dateCreated = item.getCreatedDate().getTime();
        data.checkinComment = item.getCheckinComment();
        data.lasContributor = item.getLastContributor();
        data.state = item.getStateDescription();
        data.isSnapshot = item.isSnapshot();
        if (data.isSnapshot) {
            data.snapshotName = item.getSnapshotName();
        }
        return data;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public ValidatedResponse savePackage(PackageConfigData data) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() +
                           " SAVING package [" + data.name + "]" );

        PackageItem item = repository.loadPackage( data.name );

        item.updateHeader( data.header );
        item.updateExternalURI( data.externalURI );
        item.updateDescription( data.description );
        item.archiveItem( data.archived );
        item.updateBinaryUpToDate(false);
        item.checkin( data.description );

        BRMSSuggestionCompletionLoader loader = new BRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine( item );

        ValidatedResponse res = new ValidatedResponse();
        if (loader.hasErrors()) {
            res.hasErrors = true;
            String err = "";
            for ( Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
                err += (String) iter.next();
                if (iter.hasNext()) err += "\n";
            }
            res.errorHeader  = "Package validation errors";
            res.errorMessage = err;
        }





        return res;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult listAssets(String uuid,
                                              String formats[],
                                              int numRows,
                                              int startRow) throws SerializableException {
        long start = System.currentTimeMillis();
        PackageItem pkg = repository.loadPackageByUUID( uuid );
        AssetItemIterator it = pkg.listAssetsByFormat( formats );
        if (numRows != -1) {
            it.skip( startRow );
        }
        TableDisplayHandler handler = new TableDisplayHandler(TableDisplayHandler.DEFAULT_TABLE_TEMPLATE);
        log.debug("time for asset list load: " + (System.currentTimeMillis() - start) );
        return handler.loadRuleListTable( it, numRows );
    }


    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String createState(String name) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() +
                           " CREATING state: [" + name + "]" );
        try {
            String uuid = repository.createState( name ).getNode().getUUID();
            repository.save();
            return uuid;
        } catch ( RepositoryException e ) {
            throw new SerializableException( "Unable to create the status." );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listStates() throws SerializableException {
        StateItem[] states = repository.listStates();
        String[] result = new String[states.length];
        for ( int i = 0; i < states.length; i++ ) {
            result[i] = states[i].getName();
        }
        return result;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void changeState(String uuid,
                            String newState,
                            boolean wholePackage) {

        if (!wholePackage) {

            AssetItem asset = repository.loadAssetByUUID( uuid );
            log.info( "USER:" + repository.getSession().getUserID() +
                               " CHANGING ASSET STATUS. Asset name, uuid: " +
                    "[" + asset.getName() + ", " +asset.getUUID() + "]"
                      +  " to [" + newState + "]");
            asset.updateState( newState );
        } else {
            PackageItem pkg = repository.loadPackageByUUID( uuid );
            log.info( "USER:" + repository.getSession().getUserID() +
            " CHANGING Package STATUS. Asset name, uuid: " +
                      "[" + pkg.getName() + ", " + pkg.getUUID() + "]"
                        +  " to [" + newState + "]");
            pkg.changeStatus(newState);
        }
        repository.save();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void changeAssetPackage(String uuid,
                                   String newPackage,
                                   String comment) {
        log.info( "USER:" + repository.getSession().getUserID() +
                           " CHANGING PACKAGE OF asset: [" + uuid + "] to [" + newPackage + "]");
        repository.moveRuleItemPackage( newPackage, uuid, comment );

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String copyAsset(String assetUUID,
                          String newPackage,
                          String newName) {
        return repository.copyAsset( assetUUID, newPackage, newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SnapshotInfo[] listSnapshots(String packageName) {

        String[] snaps = repository.listPackageSnapshots( packageName );
        SnapshotInfo[] res = new SnapshotInfo[snaps.length];
        for ( int i = 0; i < snaps.length; i++ ) {
            PackageItem snap = repository.loadPackageSnapshot( packageName, snaps[i] );
            SnapshotInfo info = new SnapshotInfo();
            res[i] = info;
            info.comment = snap.getCheckinComment();
            info.name = snaps[i];
            info.uuid = snap.getUUID();
        }
        return res;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void createPackageSnapshot(String packageName,
                                      String snapshotName,
                                      boolean replaceExisting,
                                      String comment) {
        log.info( "USER:" + repository.getSession().getUserID() +
         " CREATING PACKAGE SNAPSHOT for package: [" + packageName + "] snapshot name: [" + snapshotName );

        if (replaceExisting) {
            repository.removePackageSnapshot( packageName, snapshotName );
        }

        repository.createPackageSnapshot( packageName, snapshotName );
        PackageItem item = repository.loadPackageSnapshot( packageName, snapshotName );
        item.updateCheckinComment( comment );
        repository.save();

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void copyOrRemoveSnapshot(String packageName,
                                     String snapshotName,
                                     boolean delete,
                                     String newSnapshotName) throws SerializableException {

        if (delete) {
            log.info( "USER:" + repository.getSession().getUserID() +
            " REMOVING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "]" );
            repository.removePackageSnapshot( packageName, snapshotName );
        } else {
            if (newSnapshotName.equals( "" )) {
                throw new SerializableException("Need to have a new snapshot name.");
            }
            log.info( "USER:" + repository.getSession().getUserID() +
                               " COPYING SNAPSHOT for package: [" + packageName + "] snapshot: [" + snapshotName + "] to [" + newSnapshotName + "]" );

            repository.copyPackageSnapshot( packageName, snapshotName, newSnapshotName );
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public TableDataResult quickFindAsset(String searchText, int max, boolean searchArchived) {

        String search = Pattern.compile("*", Pattern.LITERAL).matcher(searchText).replaceAll(Matcher.quoteReplacement("%"));

        if (!search.endsWith( "%" )) {
            search += "%";
        }


        TableDataResult result = new TableDataResult();

        List<TableDataRow> resultList = new ArrayList<TableDataRow>();

        long start = System.currentTimeMillis();
        AssetItemIterator it = repository.findAssetsByName( search, searchArchived ); // search for archived items
        log.debug("Search time: " + (System.currentTimeMillis() - start));
        for(int i = 0; i < max; i++) {
            if (!it.hasNext()) {
                break;
            }

            AssetItem item = (AssetItem) it.next();
            TableDataRow row = new TableDataRow();
            row.id = item.getUUID();
            String desc = item.getDescription() + "";
            row.values = new String[] { item.getName(), desc.substring( 0, Math.min( 32, desc.length() ) ) };
            resultList.add( row );

        }

        if (it.hasNext()) {
            TableDataRow empty = new TableDataRow();
            empty.id = "MORE";
            resultList.add( empty );
        }

        result.data = resultList.toArray( new TableDataRow[resultList.size()] );
        return result;

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeCategory(String categoryPath) throws SerializableException {
        log.info( "USER:" + repository.getSession().getUserID() +
        " REMOVING CATEGORY path: [" + categoryPath + "]" );

        try {
            repository.loadCategory( categoryPath ).remove();
            repository.save();
        } catch (RulesRepositoryException e) {
            throw new SerializableException( e.getMessage() );
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void clearRulesRepository() {
        RulesRepositoryAdministrator admin = new RulesRepositoryAdministrator(repository.getSession());
        admin.clearRulesRepository();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializableException {
        try {

            PackageItem pkg = repository.loadPackage( packageName );
            BRMSSuggestionCompletionLoader loader = new BRMSSuggestionCompletionLoader();
            return loader.getSuggestionEngine( pkg );
        } catch (RulesRepositoryException e) {
            log.error( e );
            throw new SerializableException(e.getMessage());
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult[] buildPackage(String packageUUID, String selectorConfigName, boolean force) throws SerializableException {

        PackageItem item = repository.loadPackageByUUID( packageUUID );
        if (!force && item.isBinaryUpToDate()) {
        	//we can just return all OK if its up to date.
        	return null;
        }
        ContentPackageAssembler asm = new ContentPackageAssembler(item, selectorConfigName);
        if (asm.hasErrors()) {
            BuilderResult[] result = generateBuilderResults( asm );
            return result;
        } else {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bout);
                out.writeObject( asm.getBinaryPackage() );

                item.updateCompiledPackage( new ByteArrayInputStream( bout.toByteArray()) );
                out.flush();
                out.close();

                item.updateBinaryUpToDate(true);

                repository.save();
            } catch (IOException e) {
                log.error( e );
                throw new SerializableException(e.getMessage());
            }


            return null;

        }
    }



	private BuilderResult[] generateBuilderResults(ContentPackageAssembler asm) {
        BuilderResult[] result = new BuilderResult[asm.getErrors().size()];
        for ( int i = 0; i < result.length; i++ ) {
            ContentAssemblyError err = asm.getErrors().get( i );
            BuilderResult res = new BuilderResult();
            res.assetName = err.itemInError.getName();
            res.assetFormat = err.itemInError.getFormat();
            res.message = err.errorReport;
            res.uuid = err.itemInError.getUUID();
            result[i] = res;
        }
        return result;
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildPackageSource(String packageUUID) throws SerializableException {
        PackageItem item = repository.loadPackageByUUID( packageUUID );
        ContentPackageAssembler asm = new ContentPackageAssembler(item, false);
        return asm.getDRL();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String buildAssetSource(RuleAsset asset) throws SerializableException {
        AssetItem item = repository.loadAssetByUUID( asset.uuid );

        ContentHandler handler = ContentHandler.getHandler( item.getFormat() );//new AssetContentFormatHandler();
        handler.storeAssetContent( asset, item );
        StringBuffer buf = new StringBuffer();
        if (handler.isRuleAsset()) {

            BRMSPackageBuilder builder = new BRMSPackageBuilder(new PackageBuilderConfiguration());
            //now we load up the DSL files
            builder.setDSLFiles( BRMSPackageBuilder.getDSLMappingFiles( item.getPackage(), new BRMSPackageBuilder.DSLErrorEvent() {
                public void recordError(AssetItem asset, String message) {
                    //ignore at this point...
                }
            }));
            ((IRuleAsset) handler).assembleDRL( builder, item, buf );
        } else {
            return item.getContent();
        }

        return buf.toString();
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public BuilderResult[] buildAsset(RuleAsset asset) throws SerializableException {
        AssetItem item = repository.loadAssetByUUID( asset.uuid );

        ContentHandler handler = ContentHandler.getHandler( item.getFormat() );//new AssetContentFormatHandler();
        handler.storeAssetContent( asset, item );


        if (handler instanceof IValidating) {
            return ((IValidating) handler).validateAsset( item );
        } else {

            ContentPackageAssembler asm = new ContentPackageAssembler(item);
            if (!asm.hasErrors()) {
                return null;
            } else {
                return generateBuilderResults( asm );
            }
        }

    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void copyPackage(String sourcePackageName, String destPackageName) throws SerializableException {
        try {
            repository.copyPackage( sourcePackageName, destPackageName );
        } catch (RulesRepositoryException e) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renameAsset(String uuid, String newName) {
        return repository.renameAsset( uuid, newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void archiveAsset(String uuid, boolean value) {
        try {
            AssetItem item = repository.loadAssetByUUID( uuid );
            item.archiveItem( value );
            item.checkin( "unarchived" );

        } catch (RulesRepositoryException e) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void removeAsset(String uuid) {
        try {
            AssetItem item = repository.loadAssetByUUID( uuid );
            item.remove();
            repository.save();
        } catch (RulesRepositoryException e) {
            log.error( e );
            throw e;
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String renamePackage(String uuid, String newName) {
        return repository.renamePackage( uuid, newName );
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public void rebuildSnapshots() throws SerializableException {
        Iterator pkit = repository.listPackages();
        while(pkit.hasNext()) {
            PackageItem pkg = (PackageItem) pkit.next();
            String[] snaps = repository.listPackageSnapshots( pkg.getName() );
            for ( String snapName : snaps ) {
                PackageItem snap = repository.loadPackageSnapshot( pkg.getName(), snapName );
                BuilderResult[]  res = this.buildPackage( snap.getUUID(), "", true  ) ;
                if (res != null) {
                    StringBuffer buf = new StringBuffer();
                    for ( int i = 0; i < res.length; i++ ) {
                        buf.append( res[i].toString() );
                        buf.append( '\n' );
                    }
                    throw new DetailedSerializableException("Unable to rebuild snapshot [" + snapName, buf.toString() + "]" );
                }
            }
        }
    }

    @WebRemote
    @Restrict("#{identity.loggedIn}")
    public String[] listRulesInPackage(String packageName) throws SerializableException {

    	PackageItem item = repository.loadPackage(packageName);
        ContentPackageAssembler asm = new ContentPackageAssembler(item, false);
        List<String> result = new ArrayList<String>();
        DrlParser p = new DrlParser();
        try {
			PackageDescr pkg = p.parse(asm.getDRL());
			int count = 0;
			for (Iterator iterator = pkg.getRules().iterator(); iterator.hasNext();) {
				RuleDescr r = (RuleDescr) iterator.next();
				result.add(r.getName());
				count++;
				if (count == 5000) {
					result.add("More then 5000 rules.");
					break;
				}
			}
			return result.toArray(new String[result.size()]);
		} catch (DroolsParserException e) {
			log.error(e);
			return new String[0];
		}
    }






}