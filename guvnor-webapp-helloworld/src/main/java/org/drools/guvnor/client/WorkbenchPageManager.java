package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventBus;

public class WorkbenchPageManager implements AddViewPartEvent.Handler {

    private WorkbenchPage workbenchPage;
    private final ViewRegistry viewRegistry;

    public WorkbenchPageManager(EventBus eventBus) {
        eventBus.addHandler(AddViewPartEvent.TYPE, this);
        viewRegistry = new ViewRegistry();
    }

    public WorkbenchPage getWorkbenchPage() {
        return workbenchPage;
    }

    void setWorkbenchPage(WorkbenchPage workbenchPage) {
        this.workbenchPage = workbenchPage;
    }

    @Override
    public void onAddViewPart(AddViewPartEvent event) {
        if (viewRegistry.keySet().contains(event.getViewPartId())) {
            ViewDescriptor viewDescriptor = viewRegistry.get(event.getViewPartId());
            workbenchPage.add(
                    viewDescriptor.getTitle(),
                    viewDescriptor.getWidget());
        }
    }
}
