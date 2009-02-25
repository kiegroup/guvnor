package org.drools.guvnor.server.builder;

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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarInputStream;

import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.IRuleAsset;
import org.drools.guvnor.server.selector.AssetSelector;
import org.drools.guvnor.server.selector.SelectorManager;
import org.drools.lang.descr.PackageDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.VersionableItem;
import org.drools.rule.Package;

/**
 * This assembles packages in the BRMS into binary package objects, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * package.
 * 
 * @author Michael Neale
 */
public class ContentPackageAssembler {

	private PackageItem pkg;

	/**
	 * We accumulate errors here. If they come from the builder, then we reset
	 * the builders errors so as to not double report. It also means we can
	 * track errors to the exact asset that caused it.
	 */
	private List<ContentAssemblyError> errors = new ArrayList<ContentAssemblyError>();

	BRMSPackageBuilder builder;

	private String selectorConfigName;

	/**
	 * Use this if you want to build the whole package.
	 * 
	 * @param pkg
	 *            The package.
	 */
	public ContentPackageAssembler(PackageItem pkg) {
		this(pkg, null);
	}

	/**
	 * @param pkg
	 *            The package.
	 * @param compile
	 *            true if we want to build it. False and its just for looking at
	 *            source.
	 */
	public ContentPackageAssembler(PackageItem pkg, boolean compile) {
		this(pkg, compile, null);
	}

	/**
	 * @param assetPackage
	 *            The package.
	 * @param compile
	 *            true if we want to build it. False and its just for looking at
	 *            source.
	 * @param selectorConfigName
	 */
	public ContentPackageAssembler(PackageItem assetPackage, boolean compile,
			String selectorConfigName) {

		this.pkg = assetPackage;
		this.selectorConfigName = selectorConfigName;

		createBuilder();

		if (compile && preparePackage()) {
			buildPackage();
		}
	}

	/**
	 * Use this if you want to build the whole package.
	 * 
	 * @param assetPackage
	 *            The package to build
	 * 
	 */
	public ContentPackageAssembler(PackageItem assetPackage,
			String selectorConfigName) {
		this(assetPackage, true, selectorConfigName);
	}

	/**
	 * Use this if you want to build and compile just the one asset.
	 */
	public ContentPackageAssembler(AssetItem assetToBuild) {
		this.pkg = assetToBuild.getPackage();
		createBuilder();

		if (preparePackage()) {
			buildAsset(assetToBuild);
		}
	}

	public void createBuilder() {
		List<JarInputStream> jars = BRMSPackageBuilder.getJars(pkg);
        Properties ps = new Properties();
        try {
            ps = loadConfProperties(pkg);
        } catch (IOException e) {
            throw new RulesRepositoryException("Unable to load configuration properties for package.", e);            
        }
        builder = BRMSPackageBuilder.getInstance(jars, ps);
	}


    /**
     * Load all the .properties and .conf files into one big happy Properties instance.
     */
    Properties loadConfProperties(PackageItem pkg) throws IOException {
        Properties ps = new Properties();
        AssetItemIterator iter = pkg.listAssetsByFormat(new String[] {"properties", "conf"});
        while(iter.hasNext()) {
            AssetItem conf = iter.next();
            conf.getContent();
            Properties p = new Properties();
            p.load(conf.getBinaryContentAttachment());
            ps.putAll(p);
        }
        return ps;
    }

    /**
	 * This will build the package - preparePackage would have been called first.
     * This will always prioritise DRL before other assets.
	 */
	private void buildPackage() {
		AssetSelector selector = SelectorManager.getInstance().getSelector(
				selectorConfigName);
		if (selector == null) {
			this.errors.add(new ContentAssemblyError(this.pkg,
					"The selector named " + selectorConfigName
							+ " is not available."));
			return;
		}
        
        Iterator<AssetItem> drls = pkg.listAssetsByFormat(new String[]{AssetFormats.DRL});
        while (drls.hasNext()) {
            AssetItem asset = (AssetItem) drls.next();
            if (!asset.isArchived() && (selector.isAssetAllowed(asset))) {
                buildAsset(asset);
            }
        }
		Iterator<AssetItem> it = pkg.getAssets();
		while (it.hasNext()) {
			AssetItem asset = (AssetItem) it.next();
			if (!asset.getFormat().equals(AssetFormats.DRL) && !asset.isArchived() && (selector.isAssetAllowed(asset))) {
				buildAsset(asset);
			}
		}
	}

	/**
	 * Builds assets that are "rule" assets (ie things that are not functions
	 * etc).
	 */
	private void buildAsset(AssetItem asset) {
		ContentHandler h = ContentManager.getHandler(asset.getFormat());
		if (h instanceof IRuleAsset && !asset.getDisabled()) {
			try {
				((IRuleAsset) h).compile(builder, asset, new ErrorLogger());
				if (builder.hasErrors()) {
					this.recordBuilderErrors(asset);
					// clear the errors, so we don't double report.
					builder.clearErrors();
				}
			} catch (DroolsParserException e) {
				throw new RulesRepositoryException(e);
			} catch (IOException e) {
				throw new RulesRepositoryException(e);
			}
		}
	}

	/**
	 * This prepares the package builder, loads the jars/classpath.
	 * 
	 * @return true if everything is good to go, false if its all gone horribly
	 *         wrong, and we can't even get the package header up.
	 */
	private boolean preparePackage() {

		// firstly we loadup the classpath
		builder.addPackage(new PackageDescr(pkg.getName()));

		loadDeclaredTypes();
		// now we deal with the header (imports, templates, globals).
		addDrl(ServiceImplementation.getDroolsHeader(pkg));
		if (builder.hasErrors()) {
			recordBuilderErrors(pkg);
			// if we have any failures, lets drop out now, no point in going
			// any further
			return false;
		}

		loadDSLFiles();

		// finally, any functions we will load at this point.
		AssetItemIterator it = this.pkg
				.listAssetsByFormat(new String[] { AssetFormats.FUNCTION });
		while (it.hasNext()) {
			AssetItem func = (AssetItem) it.next();
			addDrl(func.getContent());
			if (builder.hasErrors()) {
				recordBuilderErrors(func);
				builder.clearErrors();
			}
		}

		return errors.size() == 0;
	}

	private void loadDeclaredTypes() {
		AssetItemIterator it = this.pkg
				.listAssetsByFormat(new String[] { AssetFormats.DRL_MODEL });
		while (it.hasNext()) {
			AssetItem as = it.next();
			try {
                String content = as.getContent();
                if (nonEmpty(content)) {
				    builder.addPackageFromDrl(new StringReader(as.getContent()));
                }
			} catch (DroolsParserException e) {
				this.errors.add(new ContentAssemblyError(as,
						"Parser exception: " + e.getMessage()));
			} catch (IOException e) {
				this.errors.add(new ContentAssemblyError(as, "IOException: "
						+ e.getMessage()));
			}
		}

	}

    private boolean nonEmpty(String content) {
        return content != null && content.trim().length() > 0;
    }

    private void loadDSLFiles() {
		// now we load up the DSL files
		builder.setDSLFiles(BRMSPackageBuilder.getDSLMappingFiles(pkg,
				new BRMSPackageBuilder.DSLErrorEvent() {
					public void recordError(AssetItem asset, String message) {
						errors.add(new ContentAssemblyError(asset, message));
					}
				}));
	}

	/**
	 * This will return true if there is an error in the package configuration
	 * or functions.
	 * 
	 * @return
	 */
	public boolean isPackageConfigurationInError() {
		if (this.errors.size() > 0) {
			return this.errors.get(0).itemInError instanceof PackageItem;
		} else {
			return false;
		}
	}

	private void addDrl(String drl) {
		if ("".equals(drl)) {
			return;
		}
		try {
			builder.addPackageFromDrl(new StringReader(drl));
		} catch (DroolsParserException e) {
			throw new RulesRepositoryException(
					"Unexpected error when parsing package.", e);
		} catch (IOException e) {
			throw new RulesRepositoryException(
					"IO Exception occurred when parsing package.", e);
		}
	}

	/**
	 * This will accumulate the errors.
	 */
	private void recordBuilderErrors(VersionableItem asset) {
		DroolsError[] errs = builder.getErrors().getErrors();
		for (int i = 0; i < errs.length; i++) {
			this.errors.add(new ContentAssemblyError(asset, errs[i]
					.getMessage()));
		}

	}

	/**
	 * I've got a package people !
	 */
	public Package getBinaryPackage() {
		if (this.hasErrors()) {
			throw new IllegalStateException(
					"There is no package available, as there were errors.");
		}
		return builder.getPackage();
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}

	public List<ContentAssemblyError> getErrors() {
		return this.errors;
	}

	public BRMSPackageBuilder getBuilder() {
		return builder;
	}

	/**
	 * This is passed in to the compilers so extra errors can be added.
	 * 
	 * @author Michael Neale
	 */
	public class ErrorLogger {
		public void logError(ContentAssemblyError err) {
			errors.add(err);
		}
	}

	public String getDRL() {
		StringBuffer src = new StringBuffer();
		src.append("package " + this.pkg.getName() + "\n");
		src.append(ServiceImplementation.getDroolsHeader(this.pkg) + "\n\n");

		// now we load up the DSL files
		builder.setDSLFiles(BRMSPackageBuilder.getDSLMappingFiles(pkg,
				new BRMSPackageBuilder.DSLErrorEvent() {
					public void recordError(AssetItem asset, String message) {
						errors.add(new ContentAssemblyError(asset, message));
					}
				}));

		// do the functions and declared types.
		AssetItemIterator it = this.pkg.listAssetsByFormat(new String[] {
				AssetFormats.FUNCTION, AssetFormats.DRL_MODEL });
		while (it.hasNext()) {
			AssetItem func = (AssetItem) it.next();
			if (!func.isArchived()) {
				src.append(func.getContent() + "\n\n");
			}
		}

		// now the rules
		Iterator<AssetItem> iter = pkg.getAssets();
		while (iter.hasNext()) {
			AssetItem asset = (AssetItem) iter.next();
			if (!asset.isArchived() && !asset.getDisabled()) {

				ContentHandler h = ContentManager.getHandler(asset.getFormat());
				if (h instanceof IRuleAsset) {
					IRuleAsset ruleAsset = (IRuleAsset) h;
					ruleAsset.assembleDRL(builder, asset, src);
				}
				src.append("\n\n");
			}
		}

		return src.toString();
	}

}