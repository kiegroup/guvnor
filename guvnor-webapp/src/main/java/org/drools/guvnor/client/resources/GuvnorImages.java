package org.drools.guvnor.client.resources;

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

    public Image Edit() {
        Image image = new Image(Images.INSTANCE.edit());
        image.setAltText(Constants.INSTANCE.Edit());
        return image;
    }

    public Image Refresh() {
        Image image = new Image(Images.INSTANCE.refresh());
        image.setAltText(Constants.INSTANCE.Refresh());
        return image;
    }
}
