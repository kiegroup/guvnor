package org.kie.guvnor.testscenario.client.reporting;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.*;
import org.kie.guvnor.commons.ui.client.resources.CommonImages;
import org.kie.guvnor.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.guvnor.testscenario.client.service.TestRuntimeReportingService;
import org.kie.guvnor.testscenario.model.Failure;

import javax.inject.Inject;

public class TestRunnerReportingViewImpl
        extends Composite
        implements TestRunnerReportingView,
        RequiresResize {

    private static Binder uiBinder = GWT.create(Binder.class);
    private Presenter presenter;

    interface Binder extends UiBinder<Widget, TestRunnerReportingViewImpl> {

    }

    @UiField(provided = true)
    DataGrid<Failure> dataGrid;

    @UiField
    HorizontalPanel panel;

    @UiField
    TextArea explanationArea;


    @Inject
    public TestRunnerReportingViewImpl(TestRuntimeReportingService testRuntimeReportingService) {
        dataGrid = new DataGrid<Failure>();
        dataGrid.setWidth("100%");

        dataGrid.setAutoHeaderRefreshDisabled(true);

        dataGrid.setEmptyTableWidget(new Label("---"));

        setUpColumns();

//        testRuntimeReportingService.addDataDisplay(dataGrid);

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void onResize() {
        dataGrid.setPixelSize(getParent().getOffsetWidth(),
                getParent().getOffsetHeight());
        dataGrid.onResize();
    }

    private void setUpColumns() {
        addSuccessColumn();
        addTextColumn();
    }

    private void addSuccessColumn() {
        Column<Failure, ImageResource> column = new Column<Failure, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(Failure message) {
                return CommonImages.INSTANCE.error();
            }
        };
        dataGrid.addColumn(column);
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    private void addTextColumn() {
        Column<Failure, String> column = new Column<Failure, String>(new TextCell()) {
            @Override
            public String getValue(Failure failure) {
                return failure.getDisplayName();
            }
        };

        dataGrid.addColumn(column, TestScenarioConstants.INSTANCE.Text());
        dataGrid.setColumnWidth(column, 60, Style.Unit.PCT);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setExplanation(String explanation) {
        explanationArea.setText(explanation);
    }
}
