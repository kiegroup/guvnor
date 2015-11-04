package org.guvnor.asset.management.client.editors.forms.promote.mock;

import org.uberfire.ext.widgets.common.client.forms.GetFormParamsEvent;

import javax.enterprise.event.Event;
import java.lang.annotation.Annotation;

public class GetFormParamsEventMock implements Event<GetFormParamsEvent> {
    private GetFormParamsEvent firedEvent;

    @Override
    public void fire( GetFormParamsEvent getFormParamsEvent ) {
        firedEvent = getFormParamsEvent;
    }

    @Override
    public Event<GetFormParamsEvent> select( Annotation... annotations ) {
        return null;
    }

    @Override
    public <U extends GetFormParamsEvent> Event<U> select( Class<U> aClass, Annotation... annotations ) {
        return null;
    }

    public GetFormParamsEvent getFiredEvent() {
        return firedEvent;
    }
}
