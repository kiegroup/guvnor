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


    String Assets();


    String Category();

    String AddAnotherFieldToThisSoYouCanSetItsValue();

    String RemoveThisItem();

    String ChooseAMethodToInvoke();

    String AddField();










    String OK();

    String pleaseChooseFactType();




    String NewFactSelectTheType();



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

    String Cancel();

    String CreateANewFactTemplate();

    String Name1();

    String FactAttributes();

    String SessionExpiredMessage();

    String DecisionTable();

    String ConditionColumns();

    String ActionColumns();

    String options();

    String none();

    String GroupByColumn();

    String Apply();

    String EditThisActionColumnConfiguration();

    String CreateANewActionColumn();

    String SetTheValueOfAField();

    String SetTheValueOfAFieldOnANewFact();

    String TypeOfActionColumn();

    String RemoveThisActionColumn();

    String AddANewConditionColumn();

    String EditThisColumnsConfiguration();

    String RemoveThisConditionColumn();

    String DeleteConditionColumnWarning();


    String Metadata();

    String Attributes();

    String AddANewAttributeMetadata();

    String AddAnOptionToTheRule();

    String AddMetadataToTheRule();

    String Metadata1();

    String Attribute();

    String AddAttributeMetadata();

    String RemoveThisAttribute();


    String DeleteActionColumnWarning();

    String RemoveThisMetadata();

    String ConfigureColumnsNote();

    String Items();

    String Item();

    String AddRow();

    String RemoveSelectedRowS();

    String AreYouSureYouWantToDeleteTheSelectedRowS();

    String CopySelectedRowS();

    String Modify();

    String ConditionColumnConfiguration();

    String ChooseAnExistingPatternThatThisColumnAddsTo();


    String Predicate();

    String Pattern();

    String CalculationType();

    String EditTheFieldThatThisColumnOperatesOn();


    String EditTheOperatorThatIsUsedToCompareDataWithThisField();

    String Operator();

    String ValueList();

    String ValueListsExplanation();

    String optionalValueList();

    String ColumnHeaderDescription();

    String ApplyChanges();

    String YouMustEnterAColumnHeaderValueDescription();


    String ThatColumnNameIsAlreadyInUsePleasePickAnother();



    String pleaseChooseAFieldFirst();


    String SetTheOperator();

    String noOperator();

    String notNeededForPredicate();

    String pleaseSelectAPatternFirst();

    String pleaseSelectAField();

    String Field();

    String ChooseExistingPatternToAddColumnTo();

    String ORwithEmphasis();

    String CreateNewFactPattern();

    String CreateANewFactPattern();

    String FactType();

    String name();

    String isEqualTo();

    String isNotEqualTo();

    String isLessThan();

    String lessThanOrEqualTo();

    String greaterThan();

    String greaterThanOrEqualTo();

    String orEqualTo();

    String orNotEqualTo();

    String andNotEqualTo();

    String andGreaterThan();


    String orGreaterThan();

    String orLessThan();

    String andLessThan();

    String orGreaterThanOrEqualTo();

    String orLessThanOrEqualTo();

    String andGreaterThanOrEqualTo();

    String andLessThanOrEqualTo();

    String andContains();

    String orContains();

    String andMatches();

    String orMatches();

    String orExcludes();

    String andExcludes();

    String soundsLike();

    String ThereIsNo();

    String ThereExists();

    String AnyOf1();

    String Insert();

    String LogicallyInsert();

    String Retract();

    String Set();

    String CallMethod();

    String LoadingUserPermissions();


    String WelcomeUser();

    String SignOut();

    String LoadingLogMessages();

    String Timestamp();

    String Message();

    String ShowRecentLogTip();

    String Reload();

    String showMoreInfo();

    String RenameThisAsset();

    String Title();

    String CategoriesMetaData();

    String ModifiedOnMetaData();

    String ModifiedByMetaData();

    String NoteMetaData();

    String CreatedOnMetaData();

    String CreatedByMetaData();

    String FormatMetaData();

    String PackageMetaData();

    String IsDisabledMetaData();

    String DisableTip();

    String OtherMetaData();

    String SubjectMetaData();

    String AShortDescriptionOfTheSubjectMatter();

    String TypeMetaData();

    String TypeTip();

    String ExternalLinkMetaData();

    String ExternalLinkTip();

    String SourceMetaData();

    String SourceMetaDataTip();

    String VersionHistory();

    String CurrentVersionNumber();

    String RenameThisItem();

    String NewNameAsset();

    String RenameItem();

    String ItemHasBeenRenamed();

    String MoveThisItemToAnotherPackage();

    String CurrentPackage();

    String NewPackage();

    String ChangePackage();

    String YouNeedToPickADifferentPackageToMoveThisTo();

    String MovedFromPackage();

    String NotCheckedInYet();


    String InitialCategory();

    String TypeFormatOfRule();

    String FileExtensionTypeFormat();

    String DSLMappingTip();

    String NewEnumDoco();

    String InitialDescription();

    String BusinessRuleGuidedEditor();

    String DSLBusinessRuleTextEditor();

    String DRLRuleTechnicalRuleTextEditor();

    String DecisionTableSpreadsheet();

    String DecisionTableWebGuidedEditor();

    String YouHaveToPickAnInitialCategory();

    String PleaseEnterAFormatFileType();

    String AssetNameAlreadyExistsPickAnother();

    String emptyNameIsNotAllowed();

    String NonValidJCRName();

    String CreateANewPackage();

    String CreateNewPackage();

    String ImportDRLDesc1();

    String ImportDRLDesc2();

    String ImportDRLDesc3();

    String NameColon();


    String PackageNameTip();

    String CreateNewPackageRadio();

    String ImportFromDrlRadio();

    String DRLFileToImport();

    String CreatePackage();

    String PackageNameCorrectHint();

    String CreatingPackagePleaseWait();

    String upload();

    String ImportMergeWarning();

    String ImportingDRLPleaseWait();

    String PackageWasImportedSuccessfully();

    String UnableToImportIntoThePackage0();

    String YouDidNotChooseADrlFileToImport();

    String YouCanOnlyImportDrlFiles();

    String WelcomeToGuvnor();

    String BrandNewRepositoryNote();

    String YesPleaseInstallSamples();

    String NoThanks();

    String AboutToInstallSampleRepositoryAreYouSure();

    String ImportingAndProcessing();

    String RepositoryInstalledSuccessfully();

    String BuildPackage();

    String ThisWillValidateAndCompileAllTheAssetsInAPackage();

    String OptionalSelectorName();

    String CustomSelector();

    String SelectorTip();

    String BuildBinaryPackage();

    String BuildingPackageNote();

    String CreateSnapshotForDeployment();

    String TakeSnapshot();

    String AssemblingPackageSource();

    String ViewingSourceFor0();

    String ReadOnlySourceNote();

    String ValidatingAndBuildingPackagePleaseWait();

    String PleaseWaitDotDotDot();

    String PackageBuiltSuccessfully();

    String DownloadBinaryPackage();

    String Format();

    String Message1();

    String LoadingExistingSnapshots();

    String CreateASnapshotForDeployment();

    String SnapshotDescription();

    String ChooseOrCreateSnapshotName();

    String NEW();

    String Comment();

    String CreateNewSnapshot();

    String YouHaveToEnterOrChoseALabelNameForTheSnapshot();

    String TheSnapshotCalled0WasSuccessfullyCreated();

    String PackageName();

    String ConfigurationSection();

    String Configuration();

    String DescriptionColon();

    String CategoryRules();

    String SaveAndValidateConfiguration();

    String BuildAndValidate();

    String InformationAndImportantURLs();

    String DateCreated();

    String ShowPackageSource();

    String URLForPackageSource();

    String URLSourceDescription();

    String URLForPackageBinary();

    String UseThisUrlInTheRuntimeAgentToFetchAPreCompiledBinary();

    String URLForRunningTests();

    String URLRunTestsRemote();

    String ChangeStatusDot();

    String Tip();

    String AllRulesForCategory0WillNowExtendTheRule1();

    String RemoveThisCategoryRule();

    String AddCatRuleToThePackage();

    String CategoryParentRules();

    String CatRulesInfo();

    String AddACategoryRuleToThePackage();

    String CreateCategoryRule();

    String AllTheRulesInFollowingCategory();

    String WillExtendTheFollowingRuleCalled();

    String ThereWereErrorsValidatingThisPackageConfiguration();

    String ViewErrors();

    String Rename();

    String AreYouSureYouWantToArchiveRemoveThisPackage();

    String RenameThePackage();

    String RenamePackageTip();


    String PackageRenamedSuccessfully();

    String CopyThePackage();

    String CopyThePackageTip();

    String NewPackageNameIs();

    String NotAValidPackageName();

    String PackageCopiedSuccessfully();

    String SavingPackageConfigurationPleaseWait();

    String PackageConfigurationUpdatedSuccessfullyRefreshingContentCache();

    String RefreshingPackageData();

    String ImportedTypes();

    String FactTypesJarTip();

    String AreYouSureYouWantToRemoveThisFactType();

    String Globals();

    String GlobalTypesAreClassesFromJarFilesThatHaveBeenUploadedToTheCurrentPackage();

    String AreYouSureYouWantToRemoveThisGlobal();

    String AdvancedView();

    String SwitchToTextModeEditing();

    String SwitchToAdvancedTextModeForPackageEditing();

    String ChooseAFactType();

    String loadingList();

    String TypesInThePackage();

    String IfNoTypesTip();

    String ChooseClassType();

    String GlobalName();

    String EnteringATypeClassName();

    String EnterTypeNameTip();

    String advancedClassName();

    String YouMustEnterAGlobalVariableName();

    String Packages();

    String CreateNew();

    String NewPackage1();


    String NewRule();

    String UploadPOJOModelJar();

    String NewModelArchiveJar();

    String NewDeclarativeModel();

    String NewDeclarativeModelUsingGuidedEditor();

    String NewFunction();

    String CreateANewFunction();

    String NewDSL();

    String CreateANewDSLConfiguration();

    String NewRuleFlow();

    String CreateANewRuleFlow();

    String NewEnumeration();

    String CreateANewEnumerationDropDownMapping();

    String NewTestScenario();

    String CreateATestScenario();

    String NewFile();

    String CreateAFile();

    String RebuildAllPackageBinariesQ();

    String RebuildConfirmWarning();

    String RebuildingPackageBinaries();

    String TipAuthEnable();

    String EnablingAuthorization();

    String EnablingAuthPopupTip();


    String UserName1();

    String Administrator();

    String HasPackagePermissions();

    String HasCategoryPermissions();

    String Reload1();

    String CurrentlyConfiguredUsers();

    String CreateNewUserMapping();

    String EnterNewUserName();

    String NewUserName();

    String DeleteSelectedUser();

    String AreYouSureYouWantToDeleteUser0();

    String LoadingUsersPermissions();

    String EditUser0();

    String UserAuthenticationTip();


    String Updating();

    String ThisUserIsAnAdministrator();

    String RemoveAdminRights();

    String AreYouSureYouWantToRemoveAdministratorPermissions();

    String RemovePermission();

    String AreYouSureYouWantToRemovePermission0();

    String AddANewPermission();

    String Loading();

    String PermissionType();

    String pleaseChoose1();

    String MakeThisUserAdmin();

    String SelectCategoryToProvidePermissionFor();

    String SelectPackageToApplyPermissionTo();

    String Yes();

    String PermissionDetails();

    String PermissionDetailsTip();
}
