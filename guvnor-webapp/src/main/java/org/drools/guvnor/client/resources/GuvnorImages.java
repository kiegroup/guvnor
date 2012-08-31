package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import org.drools.guvnor.client.messages.Constants;

public class GuvnorImages {
    public static final GuvnorImages INSTANCE = new GuvnorImages();

    private GuvnorImages() {

    }

    public Image DeleteItemSmall() {
        Image image = new Image(Images.INSTANCE.deleteItemSmall());
        image.setAltText(Constants.INSTANCE.DeleteItem());
        return image;
    }

    public Image NewItem() {
        Image image = new Image(Images.INSTANCE.newItem());
        image.setAltText(Constants.INSTANCE.NewItem());
        return image;
    }

    public Image Trash() {
        Image image = new Image(Images.INSTANCE.trash());
        image.setAltText(Constants.INSTANCE.Trash());
        return image;
    }

    public Image Refresh() {
        Image image = new Image(Images.INSTANCE.refresh());
        image.setAltText(Constants.INSTANCE.Refresh());
        return image;
    }

    public Image Wizard() {
        Image image = new Image(Images.INSTANCE.newWiz());
        image.setAltText(Constants.INSTANCE.Wizard());
        return image;
    }

    public Image WarningImage() {
        Image image = new Image(Images.INSTANCE.warningLarge());
        image.setAltText(Constants.INSTANCE.Warning());
        return image;
    }

    public Image Snapshot() {
        Image image = new Image(Images.INSTANCE.snapshot());
        image.setAltText(Constants.INSTANCE.Snapshot());
        return image;
    }

    public Image Home() {
        Image image = new Image(Images.INSTANCE.homeIcon());
        image.setAltText(Constants.INSTANCE.Home());
        return image;
    }

    public Image RuleAsset() {
        Image image = new Image(Images.INSTANCE.ruleAsset());
        image.setAltText(Constants.INSTANCE.RuleAsset());
        return image;
    }

    public Image PackageBuilder() {
        Image image = new Image(Images.INSTANCE.packageBuilder());
        image.setAltText(Constants.INSTANCE.PackageBuilder());
        return image;
    }

    public Image NewItemBelow() {
        Image image = new Image(Images.INSTANCE.newItemBelow());
        image.setAltText(Constants.INSTANCE.NewItemBelow());
        return image;
    }

    public Image SuffleDown() {
        Image image = new Image(Images.INSTANCE.shuffleDown());
        image.setAltText(Constants.INSTANCE.SuffleDown());

        return image;
    }

    public Image SuffleUp() {
        Image image = new Image(Images.INSTANCE.shuffleUp());
        image.setAltText(Constants.INSTANCE.SuffleUp());
        return image;
    }

    public Image WarningSmall() {
        Image image = new Image(Images.INSTANCE.warning());
        image.setAltText(Constants.INSTANCE.Warning());
        return image;
    }

    public Image Error() {
        Image image = new Image(Images.INSTANCE.error());
        image.setAltText(Constants.INSTANCE.Error());
        return image;
    }

    public Image Edit() {
        Image image = new Image(Images.INSTANCE.edit());
        image.setAltText(Constants.INSTANCE.Edit());
        return image;
    }

    public Image EditDisabled() {
        Image image = new Image(Images.INSTANCE.editDisabled());
        image.setAltText(Constants.INSTANCE.EditDisabled());
        return image;
    }

    public Image NewDSLPattern() {
        Image image = new Image(Images.INSTANCE.newDSLPattern());
        image.setAltText(Constants.INSTANCE.AddANewCondition());
        return image;
    }

    public Image NewDSLAction() {
        Image image = new Image(Images.INSTANCE.newDSLAction());
        image.setAltText(Constants.INSTANCE.AddAnAction());
        return image;
    }

    public Image AddConnective() {
        Image image = new Image(Images.INSTANCE.addConnective());
        image.setAltText(Constants.INSTANCE.AddConnective());
        return image;
    }

    public Image AddFieldToFact() {
        Image image = new Image(Images.INSTANCE.addFieldToFact());
        image.setAltText(Constants.INSTANCE.AddAFieldToThisExpectation());
        return image;
    }

    public Image Upload() {
        Image image = new Image(Images.INSTANCE.upload());
        image.setAltText(Constants.INSTANCE.Upload());
        return image;
    }

    public Image NewItemDisabled() {
        Image image = new Image(Images.INSTANCE.newItemDisabled());
        image.setAltText(Constants.INSTANCE.NewItemDisabled());
        return image;
    }

    public Image TrashDisabled() {
        Image image = new Image(Images.INSTANCE.trash());
        image.setAltText(Constants.INSTANCE.Trash());
        return image;
    }

/*    public Image RuleAsset() {
 Image image = new Image(Images.INSTANCE.ruleAsset());
 image.setAltText(Constants.INSTANCE.BusinessRuleAssets());
 return image;
}   */

    public Image SpreadsheetSmall() {
        Image image = new Image(Images.INSTANCE.spreadsheetSmall());
        image.setAltText(Constants.INSTANCE.BusinessRuleAssets());
        return image;
    }

    public Image Gdst() {
        Image image = new Image(Images.INSTANCE.gdst());
        image.setAltText(Constants.INSTANCE.BusinessRuleAssets());
        return image;
    }

    public Image TechnicalRuleAssets() {
        Image image = new Image(Images.INSTANCE.technicalRuleAssets());
        image.setAltText(Constants.INSTANCE.TechnicalRuleAssets());
        return image;
    }

    public Image FunctionAssets() {
        Image image = new Image(Images.INSTANCE.functionAssets());
        image.setAltText(Constants.INSTANCE.Functions());
        return image;
    }

    public Image Dsl() {
        Image image = new Image(Images.INSTANCE.dsl());
        image.setAltText(Constants.INSTANCE.DSLConfigurations());
        return image;
    }

    public Image ModelAsset() {
        Image image = new Image(Images.INSTANCE.modelAsset());
        image.setAltText(Constants.INSTANCE.Model());
        return image;
    }

    public Image RuleflowSmall() {
        Image image = new Image(Images.INSTANCE.ruleflowSmall());
        image.setAltText(Constants.INSTANCE.RuleFlows());
        return image;
    }

    public Image Enumeration() {
        Image image = new Image(Images.INSTANCE.enumeration());
        image.setAltText(Constants.INSTANCE.Enumerations());
        return image;
    }

    public Image TestManager() {
        Image image = new Image(Images.INSTANCE.testManager());
        image.setAltText(Constants.INSTANCE.TestScenarios());
        return image;
    }

    public Image NewFile() {
        Image image = new Image(Images.INSTANCE.newFile());
        image.setAltText(Constants.INSTANCE.OtherAssetsDocumentation());
        return image;
    }

    public Image Workingset() {
        Image image = new Image(Images.INSTANCE.workingset());
        image.setAltText(Constants.INSTANCE.WorkingSets());
        return image;
    }

    public Image EventLogSmall() {
        Image image = new Image(Images.INSTANCE.eventLogSmall());
        image.setAltText(Constants.INSTANCE.Documentation());
        return image;
    }

    public Image BackUp() {
        Image image = new Image(Images.INSTANCE.backupLarge());
        image.setAltText("");
        return image;
    }

    public Image UserPermissions() {
        Image image = new Image(Images.INSTANCE.userPermissionsLarge());
        image.setAltText("");
        return image;
    }

    public Image Status() {
        Image image = new Image(Images.INSTANCE.statusLarge());
        image.setAltText("");
        return image;
    }

    public Image Scenario() {
        Image image = new Image(Images.INSTANCE.scenarioLarge());
        image.setAltText("");
        return image;
    }

    public Image WorkspaceManager() {
        Image image = Status();
        image.setAltText("");
        return image;
    }

    public Image EventLog() {
        Image image = new Image(Images.INSTANCE.eventLogLarge());
        image.setAltText("");
        return image;
    }

    public Image Config() {
        Image image = new Image(Images.INSTANCE.config());
        image.setAltText("");
        return image;
    }

    public Image EditCategories() {
        Image image = new Image(Images.INSTANCE.editCategory());
        image.setAltText("");
        return image;
    }

    public Image RuleVerification() {
        Image image = new Image(Images.INSTANCE.ruleVerification());
        image.setAltText("");
        return image;
    }

    public Image Analyze() {
        Image image = new Image(Images.INSTANCE.analyzeLarge());
        image.setAltText("");
        return image;
    }

    public Image Collapse() {
        Image image = new Image(Images.INSTANCE.collapse());
        image.setAltText(Constants.INSTANCE.Collapse());
        return image;
    }
    
    public Image Feed() {
        Image image = new Image(Images.INSTANCE.feed());
        image.setAltText(Constants.INSTANCE.Feed());
        return image;
    }    
}
