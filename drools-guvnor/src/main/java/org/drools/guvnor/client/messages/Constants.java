package org.drools.guvnor.client.messages;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * This uses GWT to provide client side compile time resolving of locales.
 * See: http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-toolkit-doc-1-5&t=DevGuideInternationalization
 * (for more information).
 *
 * Each method name matches up with a key in Constants.properties (the proeprties file can still be used on the server).
 * To use this, use <code>GWT.create(Constants.class)</code>.
 *
 * @author Michael Neale
 */
public interface Constants extends ConstantsWithLookup {

    String ActionColumnConfigurationInsertingANewFact();

    String ChooseAPatternThatThisColumnAddsDataTo();

    String Pattern();

    String Assets();

    String CreateNew();

    String Category();

    String AddAnotherFieldToThisSoYouCanSetItsValue();

    String RemoveThisItem();

    String ChooseAMethodToInvoke();

    String AddField();

    String EditTheFieldThatThisColumnOperatesOn();

    String Field();

    String ValueList();

    String optionalValueList();

    String ValueListsExplanation();

    String ColumnHeaderDescription();

    String ApplyChanges();

    String YouMustEnterAColumnHeaderValueDescription();

    String ThatColumnNameIsAlreadyInUsePleasePickAnother();

    String OK();

    String pleaseChooseFactType();

    String ChooseExistingPatternToAddColumnTo();

    String ORwithEmphasis();

    String CreateNewFactPattern();

    String NewFactSelectTheType();

    String FactType();

    String name();

    String AddAField();

    String ColumnConfigurationSetAFieldOnAFact();

    String ChooseABoundFactThatThisColumnPertainsTo();

    String UpdateEngineWithChanges();

    String UpdateFact();

    String UpdateDescription();

    String pleaseChooseAFactPatternFirst();

    String ChooseFact();

    String pleaseChooseABoundFactForThisColumn();

    String setterLabel();

    String statusIs();

    String SaveChanges();

    String CommitAnyChangesForThisAsset();

    String Copy();

    String CopyThisAsset();

    String Archive();

    String ArchiveThisAssetThisWillNotPermanentlyDeleteIt();

    String AreYouSureYouWantToArchiveThisItem();

    String ArchivedItemOn();

    String Delete();

    String DeleteAssetTooltip();

    String DeleteAreYouSure();

    String ChangeStatus();

    String ChangeStatusTip();

    String CopyThisItem();

    String NewName();

    String CreateCopy();

    String AssetNameMustNotBeEmpty();

    String CreatedANewItemSuccess();

    String CheckInChanges();

    String Fact();

    String FieldValue();

    String LiteralValue();

    String LiteralValTip();

    String Literal();

    String AdvancedSection();

    String Formula();

    String FormulaTip();

    String Administration();

    String CategoryManager();

    String ArchivedManager();

    String StateManager();

    String ImportExport();

    String EventLog();

    String UserPermissionMappings();

    String About();

    String WebDAVURL();

    String Version();

    String Errors();

    String Warnings();

    String Notes();

    String ShowFactUsages();

    String FactUsages();

    String FieldsUsed();

    String ShowRulesAffected();

    String RulesAffected();

    String Reason();

    String Cause();

    String AnalysingPackage();

    String RunAnalysis();

    String AnalysingPackageRunning();

    String ArchivedItems();

    String RestoreSelectedPackage();

    String PermanentlyDeletePackage();

    String AreYouSurePackageDelete();

    String ArchivedPackagesList();

    String RestoreSelectedAsset();

    String PleaseSelectAnItemToRestore();

    String ItemRestored();

    String DeleteSelectedAsset();

    String PleaseSelectAnItemToPermanentlyDelete();

    String AreYouSureDeletingAsset();

    String ItemDeleted();

    String ArchivedAssets();

    String PackageDeleted();

    String PackageRestored();

    String noArchivedPackages();

    String analysisResultSummary();

    String Upload();

    String UploadNewVersion();

    String Download();

    String DownloadCurrentVersion();

    String FileWasUploadedSuccessfully();

    String UnableToUploadTheFile();

    String Uploading();

    String AddANewCategory();

    String RemoveThisCategory();

    String SelectCategoryToAdd();

    String ShowingNofXItems();

    String NItems();

    String refreshList();

    String openSelected();

    String Opening();

    String Next();

    String Previous();

    String goToFirst();

    String ImportOrExport();

    String ImportFromAnXmlFile();

    String ExportToAZipFile();

    String Export();

    String Import();

    String ImportConfirm();

    String ImportingInProgress();

    String ImportDone();

    String ImportFailed();

    String NoExportFilename();

    String PleaseSpecifyAValidRepositoryXmlFile();

    String ImportPackageConfirm();

    String ImportingPackage();

    String PackageImportDone();

    String PackageImportFailed();

    String PackageExportNoName();

    String PackageExportName();

    String ExportRepoWarning();

    String ExportRepoWait();

    String ExportThePackage();

    String PleaseWait();

    String TestFailureBulkFailures();

    String Open();

    String failuresOutOFExpectations();

    String OverallResult();

    String SuccessOverall();

    String FailureOverall();

    String Results();

    String RuleCoveragePercent();

    String RulesCovered();

    String UncoveredRules();

    String Scenarios();

    String Close();

    String BuildErrorsUnableToRunScenarios();

    String UserName();

    String Password();

    String Authenticating();

    String IncorrectUsernameOrPassword();

    String Login();


    String LastModified();

    String Name();

    String Description();

    String Status();

    String Package();

    String Categories();

    String LastContributor();

    String Created();

    String PackageName();

    String UnableToRunTests();

    String NoTestScenariosFound();

    String CategoryName();

    String CreateANewTopLevelCategory();

    String CreateNewCategoryUnder0();

    String CategoryWasNotSuccessfullyCreated();

    String CanNotHaveAnEmptyCategoryName();

    String NoCategoriesCreatedYetTip();

    String Refresh();

    String CategoriesPurposeTip();

    String EditCategories();

    String CurrentCategories();

    String NewCategory();

    String CreateANewCategory();

    String RenameSelected();

    String PleaseSelectACategoryToRename();

    String DeleteSelected();

    String PleaseSelectACategoryToDelete();

    String DeleteSelectedCat();

    String CategoryNewNamePleaseEnter();

    String CategoryRenamed();

    String AreYouSureYouWantToDeleteCategory();

    String AddAnOptionalCheckInComment();

    String CheckIn();

    String enterTextToFilterList();

    String AddFactToContraint();

    String Choose();

    String NewFactPattern();

    String chooseFactType();


    String FormulaEvaluateToAValue();

    String LiteralValueTip();

    String RefreshingList();

    String UnableToLoadList();


    String AdvancedOptions();

    String BoundVariable();

    String AVariable();

    String ABoundVariable();

    String BoundVariableTip();

    String NewFormula();


    String FormulaExpressionTip();

    String DecisionTableWidgetDescription();

    String UploadNewVersionDescription();


    String Deploy();

    String NewDeploymentSnapshot();

    String RebuildAllSnapshotBinaries();

    String PackageSnapshots();


    String DSLPopupHint();

    String AddANewCondition();

    String AddAnAction();

    String TheValue0IsNotValidForThisField();


    String AFormula();

    String Error();

    String ShowDetail();

    String Navigate();

    String BusinessRuleAssets();

    String TechnicalRuleAssets();

    String Functions();

    String DSLConfigurations();

    String Model();

    String RuleFlows();

    String Enumerations();

    String TestScenarios();

    String XMLProperties();

    String OtherAssetsDocumentation();

    String Admin();

    String UserPermission();

    String AssetsTreeView();

    String Find();

    String ByCategory();

    String ByStatus();

    String QA();

    String TestScenariosInPackages();

    String PleaseWaitDotDotDot();

    String Analysis();

    String ScenariosForPackage();

    String AnalysisForPackage();

    String AreYouSureCloseWarningUnsaved();

    String CloseAllItems();

    String AreYouSureYouWantToCloseOpenItems();

    String LoadingAsset();

    String LoadingPackageInformation();

    String LoadingSnapshot();

    String SnapshotLabel();

    String WholeNumberInteger();

    String TrueOrFalse();

    String Date();

    String DecimalNumber();

    String Text();


    String FieldName();

    String AreYouSureYouWantToRemoveTheField0();

    String AddNewFactType();

    String NewType();

    String EnterNewTypeName();

    String TypeNameExistsWarning();

    String chooseType();

    String FieldNameAttribute();

    String Type();

    String ChangeName();

    String NameTakenForModel();

    String ModelNameChangeWarning();

    String ChangeFactName();

    String AreYouSureYouWantToRemoveThisFact();

    String RemoveThisFactType();

    String RefreshingModel();

    String RemoveThisWholeRestriction();


    String AddAFieldToThisNestedConstraint();

    String AllOf();

    String AnyOf();

    String RemoveThisNestedRestriction();

    String RemoveThisItemFromNestedConstraint();

    String AddMoreOptionsToThisFieldsValues();

    String FormulaBooleanTip();

    String AddOrBindToCondition();

    String pleaseChoose();

    String GiveFieldVarName();

    String FactTypes();
}
