package org.drools.guvnor.client.decisiontable.cells;

import java.util.Set;

import org.drools.guvnor.client.decisiontable.widget.CellValue;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Cell that casts values to whatever is appropriate for the wrapped Cell
 * 
 * @author manstis
 * 
 * @param <T>
 *            The data-type required by the wrapped cell
 */
public class DecisionTableCellValueAdaptor<T extends Comparable<T>> extends
        AbstractCell<CellValue< ? extends Comparable< ? >>> {

    // Really we want AbstractCell<?> but that leads to generics hell
    private AbstractCell<T>       cell;

    protected DecisionTableWidget dtable;

    /**
     * @param cell
     */
    public DecisionTableCellValueAdaptor(AbstractCell<T> cell) {
        super( cell.getConsumedEvents() );
        this.cell = cell;
    }

    /**
     * Inject a DecisionTableWidget to handle value updates
     * 
     * @param manager
     */
    public void setDecisionTableWidget(DecisionTableWidget dtable) {
        this.dtable = dtable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#dependsOnSelection()
     */
    @Override
    public boolean dependsOnSelection() {
        return cell.dependsOnSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#getConsumedEvents()
     */
    @Override
    public Set<String> getConsumedEvents() {
        return cell.getConsumedEvents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#handlesSelection()
     */
    @Override
    public boolean handlesSelection() {
        return cell.handlesSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#isEditing(com.google.gwt.dom.
     * client.Element, java.lang.Object, java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean isEditing(Element parent,
                             CellValue< ? extends Comparable< ? >> value,
                             Object key) {
        return cell.isEditing( parent,
                               (T) value.getValue(),
                               key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt
     * .dom.client.Element, java.lang.Object, java.lang.Object,
     * com.google.gwt.dom.client.NativeEvent,
     * com.google.gwt.cell.client.ValueUpdater)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBrowserEvent(Element parent,
                               CellValue< ? extends Comparable< ? >> value,
                               Object key,
                               NativeEvent event,
                               ValueUpdater<CellValue< ? extends Comparable< ? >>> valueUpdater) {

        // Updates are passed back to the SelectionManager where merged cells
        // are also updated. Override the Column's FieldUpdater because
        // a Horizontal Decision Table will potentially have a different
        // data-type per row.
        cell.onBrowserEvent( parent,
                             (T) value.getValue(),
                             key,
                             event,
                             new ValueUpdater<T>() {

                                 public void update(T value) {
                                     dtable.update( value );
                                 }

                             } );
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
     * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void render(CellValue< ? extends Comparable< ? >> value,
                       Object key,
                       SafeHtmlBuilder sb) {
        cell.render( (T) value.getValue(),
                     key,
                     sb );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#resetFocus(com.google.gwt.dom
     * .client.Element, java.lang.Object, java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean resetFocus(Element parent,
                              CellValue< ? extends Comparable< ? >> value,
                              Object key) {
        return cell.resetFocus( parent,
                                (T) value.getValue(),
                                key );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#setValue(com.google.gwt.dom.client
     * .Element, java.lang.Object, java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public void setValue(Element parent,
                         CellValue< ? extends Comparable< ? >> value,
                         Object key) {
        cell.setValue( parent,
                       (T) value.getValue(),
                       key );
    }

}
