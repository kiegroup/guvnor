package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.factconstraints.client.customform.CustomFormConfiguration;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.messages.Constants;

/**
 *
 * @author esteban
 */
public class CustomFormPopUp extends FormStylePopup{

    private Constants constants = ((Constants) GWT.create(Constants.class));

    private final CustomFormConfiguration configuration;
    private final Button okButton;
    private final Button cancelButton;
    private Frame externalFrame;

    public CustomFormPopUp(String image, String title, CustomFormConfiguration configuration) {
        super(image, title);
        this.configuration = configuration;



        this.externalFrame = new Frame();
        this.externalFrame.setWidth(configuration.getCustomFormWidth()+"px");
        this.externalFrame.setHeight(configuration.getCustomFormHeight()+"px");
//        this.externalFrame.setWidth("100%");
//        this.externalFrame.setHeight("100%");

        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.setHeight("100%");
        //vp.setHeight(configuration.getCustomFormHeight()+"px");
        vp.add(this.externalFrame);

        okButton = new Button(constants.OK());

        //cancel button with default handler
        cancelButton = new Button(constants.Cancel(),new ClickHandler() {
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        

        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        hp.add(okButton);
        hp.add(cancelButton);

        vp.add(hp);

        this.addRow(vp);

        
    }

    public void addOkButtonHandler(ClickHandler handler){
        this.okButton.addClickHandler(handler);
    }

    public void addCancelButtonHandler(ClickHandler handler){
        this.cancelButton.addClickHandler(handler);
    }

    public void show(String selectedId, String selectedValue){

        
        String url = configuration.getCustomFormURL();
        if (url == null || url.trim().equals("")){
            //TODO: show an error
            return;
        }else{
            String parameters = "cf_id="+selectedId+"&cf_value="+selectedValue+"&factType="+this.configuration.getFactType()+"&fieldName="+this.configuration.getFieldName();
            //advanced url parsing for adding attributes :P
            url = url +(url.contains("?")?"&":"?")+parameters;
            this.externalFrame.setUrl(url);
            this.show();
        }
    }

    private Element getExternalFrameElement(String id){
        IFrameElement iframe = IFrameElement.as(this.externalFrame.getElement());
        return iframe.getContentDocument().getElementById(id);
    }

    public String getFormId(){
        return this.getExternalFrameElement("cf_id").getPropertyString("value");
    }

    public String getFormValue(){
        return this.getExternalFrameElement("cf_value").getPropertyString("value");
    }


}
