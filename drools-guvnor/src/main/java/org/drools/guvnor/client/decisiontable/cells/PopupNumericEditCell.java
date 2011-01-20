package org.drools.guvnor.client.decisiontable.cells;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.TextBox;

/**
 * A Popup Text Editor.
 * 
 * @author manstis
 * 
 */
public class PopupNumericEditCell extends
        AbstractPopupEditCell<Integer, Integer> {

    private final TextBox       textBox;

    // A valid number
    private static final RegExp VALID = RegExp.compile( "(^-{0,1}\\d*$)" );

    public PopupNumericEditCell() {
        super();
        this.textBox = new TextBox();

        // Tabbing out of the TextBox commits changes
        textBox.addKeyDownHandler( new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
                boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
                if ( keyEnter
                     || keyTab ) {
                    commit();
                }
            }

        } );

        // Restrict entry to navigation and numerics
        textBox.addKeyPressHandler( new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {

                // Permit navigation
                int keyCode = event.getNativeEvent().getKeyCode();
                if ( event.isControlKeyDown()
                        || keyCode == KeyCodes.KEY_BACKSPACE
                        || keyCode == KeyCodes.KEY_DELETE
                        || keyCode == KeyCodes.KEY_LEFT
                        || keyCode == KeyCodes.KEY_RIGHT
                        || keyCode == KeyCodes.KEY_TAB ) {
                    return;
                }

                // Get new value and validate
                int charCode = event.getCharCode();
                String oldValue = textBox.getValue();
                String newValue = oldValue.substring( 0,
                                                      textBox.getCursorPos() );
                newValue = newValue
                           + ((char) charCode);
                newValue = newValue
                           + oldValue.substring( textBox.getCursorPos()
                                                 + textBox.getSelectionLength() );
                if ( !VALID.test( String.valueOf( newValue ) ) ) {
                    event.preventDefault();
                }
            }

        } );

        vPanel.add( textBox );

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client
     * .Cell.Context, java.lang.Object,
     * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
     */
    @Override
    public void render(Context context,
                       Integer value,
                       SafeHtmlBuilder sb) {
        // Get the view data.
        Object key = context.getKey();
        Integer viewData = getViewData( key );
        if ( viewData != null
             && viewData.equals( value ) ) {
            clearViewData( key );
            viewData = null;
        }

        Integer i = null;
        if ( viewData != null ) {
            i = viewData;
        } else if ( value != null ) {
            i = value;
        }
        if ( i != null ) {
            sb.append( renderer.render( Integer.toString( i ) ) );
        }
    }

    // Commit the change
    @Override
    protected void commit() {
        // Hide pop-up
        Element cellParent = lastParent;
        Integer oldValue = lastValue;
        Context context = lastContext;
        Object key = context.getKey();
        panel.hide();

        // Update values
        String text = textBox.getValue();
        Integer number = null;
        if ( text.length() > 0 ) {
            number = Integer.parseInt( text );
        }
        setViewData( key,
                     number );
        setValue( context,
                  cellParent,
                  oldValue );
        if ( valueUpdater != null ) {
            valueUpdater.update( number );
        }
    }

    // Start editing the cell
    @Override
    protected void startEditing(final Element parent,
                                Integer value,
                                Context context) {

        Object key = context.getKey();
        Integer viewData = getViewData( key );
        Integer number = (viewData == null) ? value : viewData;
        textBox.setValue( (number == null ? "" : Integer.toString( number )) );

        panel.setPopupPositionAndShow( new PositionCallback() {
            public void setPosition(int offsetWidth,
                                    int offsetHeight) {
                panel.setPopupPosition( parent.getAbsoluteLeft()
                                                + offsetX,
                                        parent.getAbsoluteTop()
                                                + offsetY );

                // Focus the first enabled control
                Scheduler.get().scheduleDeferred( new ScheduledCommand() {

                    public void execute() {
                        String text = textBox.getValue();
                        textBox.setFocus( true );
                        textBox.setCursorPos( text.length() );
                        textBox.setSelectionRange( 0,
                                                   text.length() );
                    }

                } );
            }
        } );

    }

}
