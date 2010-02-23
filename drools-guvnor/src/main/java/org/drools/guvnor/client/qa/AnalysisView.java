package org.drools.guvnor.client.qa;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.rpc.AnalysisReport;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;

/**
 * Viewer for, well, analysis !
 *
 * @author Michael Neale
 */
public class AnalysisView extends Composite {

    private VerticalPanel layout;
    private String        packageUUID;
    private Constants     constants = GWT.create( Constants.class );

    public AnalysisView(String packageUUID,
                        String packageName) {
        this.layout = new VerticalPanel();
        this.packageUUID = packageUUID;

        PrettyFormLayout pf = new PrettyFormLayout();

        VerticalPanel vert = new VerticalPanel();
        String m = Format.format( constants.AnalysingPackage(),
                                  new String[]{packageName} );
        vert.add( new HTML( m ) );
        Button run = new Button( constants.RunAnalysis() );
        run.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                runAnalysis();
            }
        } );
        vert.add( run );

        pf.addHeader( "images/analyse_large.png",
                      vert );
        layout.add( pf );

        layout.add( new Label() );

        layout.setWidth( "100%" );

        initWidget( layout );
    }

    private void runAnalysis() {
        LoadingPopup.showMessage( constants.AnalysingPackageRunning() );
        RepositoryServiceFactory.getService().analysePackage( packageUUID,
                                                              new GenericCallback() {
                                                                  public void onSuccess(Object data) {
                                                                      AnalysisReport rep = (AnalysisReport) data;
                                                                      AnalysisResultWidget w = new AnalysisResultWidget( rep,
                                                                                                                         true );
                                                                      w.setWidth( "100%" );
                                                                      layout.remove( 1 );
                                                                      layout.add( w );
                                                                      LoadingPopup.close();
                                                                  }
                                                              } );

    }

}
