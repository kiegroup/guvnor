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
    
    String CategoryColon();

    String AddAnotherFieldToThisSoYouCanSetItsValue();


    String ChooseAMethodToInvoke();

    String AddField();










    String OK();

    String pleaseChooseFactType();




    String NewFactSelectTheType();




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
    
    String BasicView();
    
    String SwitchToGuidedModeEditing();
    
    String SwitchToGuidedModeForPackageEditing();
        
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

    String TheVariableName0IsAlreadyTaken();

    String BindTheFieldCalled0ToAVariable();

    String ShowSubFields();

    String ApplyAConstraintToASubFieldOf0();

    String AddFieldsToThisConstraint();


    String AllOfAnd();

    String MultipleConstraintsTip();

    String MultipleFieldConstraint();

    String ModifyConstraintsFor0();

    String AddSubFieldConstraint();

    String AddARestrictionOnAField();


    String AnyOfOr();

    String MultipleFieldConstraints();

    String MultipleConstraintsTip1();




    String AddANewFormulaStyleExpression();


    String VariableName();

    String Add();

    String Clear();

    String Properties();

    String QA1();

    String AttributeSearch();

    String CreatedBy();

    String Format1();

    String Subject();

    String Type1();

    String ExternalLink();

    String Source();

    String Description1();

    String LastModifiedBy();

    String CheckinComment();

    String WildCardsSearchTip();

    String AfterColon();


    String Before();

    String DateCreated1();

    String BeforeColon();

    String LastModified1();

    String Search();

    String NameSearch();

    String TextSearch();

    String SearchFor();

    String Search1();

    String PleaseEnterSomeSearchText();

    String FindItemsWithANameMatching();

    String IncludeArchivedAssetsInResults();

    String EnterSearchString();

    String SearchingDotDotDot();

    String ThereAreMoreItemsTryNarrowingTheSearchTerms();

    String Metadata2();

    String Attributes1();

    String Choose();

    String RemoveThisRuleOption();

    String RuleDocHint();

    String documentationDefault();

    String RuleFlowUploadTip();

    String CalculatingSource();

    String ViewingDiagram();

    String Parameters();

    String CouldNotCreateTheRuleflowDiagramItIsPossibleThatTheRuleflowFileIsInvalid();

    String AddAConditionToThisRule();

    String AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted();



    String Metadata3();

    String Attribute1();

    String AddXToListY();

    String RemoveThisAction();

    String RemoveThisItem();

    String AddAConditionToTheRule();

    String ChooseFactType();

    String Fact1();

    String ChooseOtherConditionType();

    String FreeFormDrl();

    String ConditionTypeButton();



    String NoModelTip();

    String AddANewAction();

    String NotifyEngineOfChanges();

    String NotifyEngineOfChangesUpdateModify();

    String ModifyEngineTip();


    String SetFieldValues();

    String RetractTheFact();

    String DSLSentence();

    String AdvancedOptionsColon();

    String AddAnItemToACollection();

    String InsertANewFact();

    String LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved();

    String LogicallyInsertANewFact();

    String CallAMethodOnFollowing();

    String AddFreeFormDrl();

    String FreeFormAction();

    String ThisIsADrlExpressionFreeForm();

    String RemoveThisENTIREConditionAndAllTheFieldConstraintsThatBelongToIt();

    String RemoveThisEntireConditionQ();

    String CanTRemoveThatItemAsItIsUsedInTheActionPartOfTheRule();


    String NewBusinessRuleGuidedEditor();


    String NewRuleUsingDSL();


    String NewDRL();


    String NewDecisionTableSpreadsheet();


    String NewDecisionTableGuidedEditor();

    String TestScenario();


    String ViewSource();

    String Validate();

    String ValidatingItemPleaseWait();


    String ValidationResultsDotDot();

    String ItemValidatedSuccessfully();

    String ValidationResults();

    String packageConfigurationProblem();

    String SavingPleaseWait();

    String FailedToCheckInTheItemPleaseContactYourSystemAdministrator();

    String RefreshingContentAssistance();


    String RefreshingItem();

    String WARNINGUnCommittedChanges();

    String Discard();

    String AreYouSureYouWantToDiscardChanges();

    String ScenariosForPackage1();

    String RunAllScenarios();

    String BuildingAndRunningScenarios();

    String EXPECT();

    String DeleteItem();

    String AreYouSureYouWantToRemoveThisItem();

    String GIVEN();

    String AddInputDataAndExpectationsHere();

    String MoreDotDot();

    String AddAnotherSectionOfDataAndExpectations();

    String configuration();

    String globals();

    String AddANewGlobalToThisScenario();

    String NewGlobal();

    String TheName0IsAlreadyInUsePleaseChooseAnotherName();

    String GlobalColon();

    String AddANewDataInputToThisScenario();

    String NewInput();

    String YouMustEnterAValidFactName();

    String TheFactName0IsAlreadyInUsePleaseChooseAnotherName();

    String FactName();

    String InsertANewFact1();

    String ModifyAnExistingFactScenario();

    String RetractAnExistingFactScenario();

    String AddANewExpectation();

    String NewExpectation();

    String Rule();

    String FactValue();

    String AnyFactThatMatches();

    String DeleteTheExpectationForThisFact();

    String AreYouSureYouWantToRemoveThisExpectation();

    String EnterRuleNameScenario();

    String showListButton();

    String loadingList1();


    String ValueFor0();

    String globalForScenario();

    String modifyForScenario();

    String insertForScenario();


    String ChooseAFieldToAdd();

    String RemoveThisRow();

    String AreYouSureYouWantToRemoveThisRow();

    String RemoveTheColumnForScenario();

    String CanTRemoveThisColumnAsTheName0IsBeingUsed();

    String AreYouSureYouWantToRemoveThisColumn();

    String AddAField();

    String AddANewRule();

    String RemoveSelectedRule();

    String PleaseChooseARuleToRemove();

    String AllowTheseRulesToFire();

    String PreventTheseRulesFromFiring();

    String AllRulesMayFire();

    String SelectRule();

    String UseRealDateAndTime();

    String UseASimulatedDateAndTime();

    String property0RulesFiredIn1Ms();

    String ShowRulesFired();

    String RulesFired();

    String currentDateAndTime();

    String BadDateFormatPleaseTryAgainTryTheFormatOf0();

    String scenarioFactTypeHasValues();

    String AFactOfType0HasValues();

    String AddAFieldToThisExpectation();


    String equalsScenario();

    String doesNotEqualScenario();

    String RemoveThisFieldExpectation();

    String AreYouSureYouWantToRemoveThisFieldExpectation();


    String ExpectRules();

    String ActualResult();

    String firedAtLeastOnce();

    String didNotFire();

    String firedThisManyTimes();

    String ChooseDotDotDot();

    String RemoveThisRuleExpectation();

    String AreYouSureYouWantToRemoveThisRuleExpectation();

    String RetractFacts();

    String RemoveThisRetractStatement();

    String RunScenario();

    String RunScenarioTip();

    String BuildingAndRunningScenario();

    String packageConfigurationProblem1();

    String MaxRuleFiringsReachedWarning();

    String Results();

    String SummaryColon();

    String AuditLogColon();

    String ShowEventsButton();

    String ViewingSnapshot();

    String ForPackage();

    String clickHereToDownloadBinaryOrCopyURLForDeploymentAgent();

    String DeploymentURL();

    String SnapshotCreatedOn();

    String CommentColon();

    String SnapshotDeleteConfirm();

    String SnapshotWasDeleted();

    String CopySnapshotText();

    String ExistingSnapshots();

    String NewSnapshotNameIs();

    String CreatedSnapshot0ForPackage1();

    String Snapshot0ForPackage1WasCopiedFrom2();

    String PleaseEnterANonExistingSnapshotName();

    String SnapshotListingFor();

    String SnapshotItems();

    String NewSnapshot();


    String SnapshotRebuildWarning();

    String RebuildingSnapshotsPleaseWaitThisMayTakeSomeTime();

    String SnapshotsWereRebuiltSuccessfully();

    String Type2();

    String Priority();

    String ValueRuleFlow();

    String ManageStatuses();

    String StatusTagsAreForTheLifecycleOfAnAsset();

    String CurrentStatuses();

    String NewStatus();



    String PleaseSelectAStatusToRename();


    String PleaseSelectAStatusToRemove();

    String AddNewStatus();

    String StatusRemoved();

    String PleaseEnterTheNameYouWouldLikeToChangeThisStatusTo();

    String StatusRenamed();

    String LoadingStatuses();


    String PleaseWaitDotDotDot();

    String ChooseOne();

    String ChangeStatus();

    String UpdatingStatus();

    String CreateNewStatus();

    String StatusName();

    String CanTHaveAnEmptyStatusName();

    String CreatingStatus();

    String StatusWasNotSuccessfullyCreated();

    String UnableToGetContentAssistanceForThisRule();

    String UnableToValidatePackageForSCE();

    String Detail();

    String VersionHistory1();

    String NoHistory();

    String View();

    String LoadingVersionFromHistory();

    String VersionNumber0Of1();

    String RestoreThisVersion();


    String RestoreThisVersionQ();

    String NoteNewPackageDrlImportWarning();

    String PleaseEnterANameForFact();

    String PleaseEnterANameThatIsNotTheSameAsTheFactType();

    String ThatNameIsInUsePleaseTryAnother();

    String Browse();

    String KnowledgeBases();

    String DefaultValue();

    String HideThisColumn();

    String PleaseSelectOrEnterField();

    String PleaseSelectAnOperator();
    
    String January();

    String February();

    String March();

    String April();

    String May();

    String June();

    String July();

    String August();

    String September();

    String October();

    String November();

    String December();


    String SorryAnItemOfThatNameAlreadyExistsInTheRepositoryPleaseChooseAnother();

    String ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation();

    String WHEN();

    String THEN();

    String AddAnActionToThisRule();

    String optionsRuleModeller();

    String clickToAddPatterns();

    String ChangeFieldValuesOf0();

    String Retract0();

    String Modify0();

    String InsertFact0();

    String LogicallyInsertFact0();

    String Append0ToList1();

    String CallMethodOn0();

    String hide();

    String RemoveThisBlockOfData();

    String AreYouSureYouWantToRemoveThisBlockOfData();

    String PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern();
    
    String ImportedDRLContainsNoNameForThePackage();
}
