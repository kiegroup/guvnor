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
}
