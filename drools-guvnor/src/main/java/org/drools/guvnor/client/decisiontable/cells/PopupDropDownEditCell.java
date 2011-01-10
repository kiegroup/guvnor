package org.drools.guvnor.client.decisiontable.cells;

import org.drools.ide.common.client.modeldriven.ui.ConstraintValueEditorHelper;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Popup drop-down Editor ;-)
 * 
 * @author manstis
 * 
 */
public class PopupDropDownEditCell extends AbstractEditableCell<String, String> {

	private int offsetX = 5;
	private int offsetY = 5;
	private Object lastKey;
	private Element lastParent;
	private String lastValue;
	private final PopupPanel panel;
	private final ListBox listBox;
	private final VerticalPanel vPanel;
	private final SafeHtmlRenderer<String> renderer;
	private ValueUpdater<String> valueUpdater;

	public PopupDropDownEditCell() {
		this(SimpleSafeHtmlRenderer.getInstance());
	}

	public PopupDropDownEditCell(SafeHtmlRenderer<String> renderer) {
		super("dblclick", "keydown");
		if (renderer == null) {
			throw new IllegalArgumentException("renderer == null");
		}

		this.renderer = renderer;
		this.listBox = new ListBox();
		this.vPanel = new VerticalPanel();

		// Pressing ESCAPE dismisses the pop-up loosing any changes
		this.panel = new PopupPanel(true, true) {
			@Override
			protected void onPreviewNativeEvent(NativePreviewEvent event) {
				if (Event.ONKEYUP == event.getTypeInt()) {
					if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
						panel.hide();
					}
				}
			}

		};

		// Closing the pop-up commits the change
		panel.addCloseHandler(new CloseHandler<PopupPanel>() {
			public void onClose(CloseEvent<PopupPanel> event) {
				lastKey = null;
				lastValue = null;
				if (lastParent != null && !event.isAutoClosed()) {
					lastParent.focus();
				} else if (event.isAutoClosed()) {
					commit();
				}
				lastParent = null;
			}
		});

		// Tabbing out of the ListBox commits changes
		listBox.addKeyDownHandler(new KeyDownHandler() {

			public void onKeyDown(KeyDownEvent event) {
				boolean keyTab = event.getNativeKeyCode() == KeyCodes.KEY_TAB;
				boolean keyEnter = event.getNativeKeyCode() == KeyCodes.KEY_ENTER;
				if (keyEnter || keyTab) {
					commit();
				}
			}

		});

		vPanel.add(listBox);
		panel.add(vPanel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.cell.client.AbstractEditableCell#isEditing(com.google.
	 * gwt.dom.client.Element, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isEditing(Element parent, String value, Object key) {
		return lastKey != null && lastKey.equals(key);
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
	public void onBrowserEvent(Element parent, String value, Object key,
			NativeEvent event, ValueUpdater<String> valueUpdater) {

		// KeyDown and "Enter" key-press is handled here
		super.onBrowserEvent(parent, value, key, event, valueUpdater);

		if (event.getType().equals("dblclick")) {
			startEditing(parent, value, key, event, valueUpdater);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
	 * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(String value, Object key, SafeHtmlBuilder sb) {
		// Get the view data.
		String viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		String s = null;
		if (viewData != null) {
			s = viewData;
		} else if (value != null) {
			s = value;
		}
		if (s != null) {
			sb.append(renderer.render(s));
		}
	}

	public void setItems(String[] items) {
		for (int i = 0; i < items.length; i++) {
			String item = items[i].trim();
			if (item.indexOf('=') > 0) {
				String[] splut = ConstraintValueEditorHelper.splitValue(item);
				this.listBox.addItem(splut[1], splut[0]);
			} else {
				this.listBox.addItem(item, item);
			}
		}
	}

	// Commit the change
	private void commit() {
		// Hide pop-up
		Element cellParent = lastParent;
		String oldValue = lastValue;
		Object key = lastKey;
		panel.hide();

		String text = null;
		int selectedIndex = listBox.getSelectedIndex();
		if (selectedIndex >= 0) {
			text = listBox.getValue(selectedIndex);
		}

		// Update values
		setViewData(key, text);
		setValue(cellParent, oldValue, key);
		if (valueUpdater != null) {
			valueUpdater.update(text);
		}
	}

	// Start editing the cell
	private void startEditing(final Element parent, String value, Object key,
			NativeEvent event, ValueUpdater<String> valueUpdater) {

		this.lastKey = key;
		this.lastParent = parent;
		this.lastValue = value;
		this.valueUpdater = valueUpdater;

		String viewData = getViewData(key);
		String text = (viewData == null) ? value : viewData;

		// Select the appropriate item
		boolean emptyValue = (text == null);
		if (emptyValue) {
			listBox.setSelectedIndex(0);
		} else {
			for (int i = 0; i < listBox.getItemCount(); i++) {
				if (listBox.getValue(i).equals(text)) {
					listBox.setSelectedIndex(i);
					break;
				}
			}
		}

		panel.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				panel.setPopupPosition(parent.getAbsoluteLeft() + offsetX,
						parent.getAbsoluteTop() + offsetY);

				// Focus the first enabled control
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						listBox.setFocus(true);
					}

				});
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.cell.client.AbstractCell#onEnterKeyDown(com.google.gwt
	 * .dom.client.Element, java.lang.Object, java.lang.Object,
	 * com.google.gwt.dom.client.NativeEvent,
	 * com.google.gwt.cell.client.ValueUpdater)
	 */
	@Override
	protected void onEnterKeyDown(Element parent, String value, Object key,
			NativeEvent event, ValueUpdater<String> valueUpdater) {
		startEditing(parent, value, key, event, valueUpdater);
	}

}
