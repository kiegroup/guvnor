package org.drools.guvnor.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * This is used by GWT to provide localised strings at compile time.
 * @author Michael Neale
 */
public interface Messages extends com.google.gwt.i18n.client.Messages {
    String ActionColumnConfigurationInsertingANewFact();

    String ChooseAPatternThatThisColumnAddsDataTo();

    String Pattern();

    String Assets();

    String CreateNew();

    String Status();

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

    String setterLabel(String displayName, String variable);

    String statusIs(String statusName);

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

    String CreatedANewItemSuccess(String name, String packageName);

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

    String AnalysingPackage(String packageName);

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

    String analysisResultSummary(String msg, int num);

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

    String TestFailureBulkFailures(int failures, int total);

    String Open();

    String failuresOutOFExpectations(int totalFailures, int grandTotal);

    String OverallResult();

    String SuccessOverall();

    String FailureOverall();

    String Results();

    String RuleCoveragePercent(int percent);

    String RulesCovered();

    String UncoveredRules();

    String Scenarios();

    String Close();

    String BuildErrorsUnableToRunScenarios();
}
