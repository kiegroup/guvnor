package org.drools.guvnor.client.decisiontable.cells;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A Popup Editor.
 * 
 * @author manstis
 * 
 */
public abstract class AbstractPopupEditCell<C, V> extends
		AbstractEditableCell<C, V> {

	protected int offsetX = 5;
	protected int offsetY = 5;
	protected Object lastKey;
	protected Element lastParent;
	protected C lastValue;
	protected final PopupPanel panel;
	protected final VerticalPanel vPanel;
	protected final SafeHtmlRenderer<String> renderer;
	protected ValueUpdater<C> valueUpdater;

	/**
	 * Boiler-plate and scaffolding for a general "Popup". Subclasses should
	 * call this default constructor and append their specific child controls
	 * for the "Popup" to <code>vPanel</code>.
	 * 
	 * @param renderer
	 */
	public AbstractPopupEditCell() {
		super("dblclick", "keydown");
		this.renderer = SimpleSafeHtmlRenderer.getInstance();
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
					getTableCellElementAncestor(lastParent).focus();
				} else if (event.isAutoClosed()) {
					commit();
				}
				lastParent = null;
			}
		});

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
	public boolean isEditing(Element parent, C value, Object key) {
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
	public void onBrowserEvent(Element parent, C value, Object key,
			NativeEvent event, ValueUpdater<C> valueUpdater) {

		// KeyDown and "Enter" key-press is handled here
		super.onBrowserEvent(parent, value, key, event, valueUpdater);

		if (event.getType().equals("dblclick")) {
			this.lastKey = key;
			this.lastParent = parent;
			this.lastValue = value;
			this.valueUpdater = valueUpdater;
			startEditing(parent, value, key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#render(java.lang.Object,
	 * java.lang.Object, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public abstract void render(C value, Object key, SafeHtmlBuilder sb);

	// Find the nearest TableCellElement ancestor
	private Element getTableCellElementAncestor(Element e) {
		Element parent = e.getParentElement();
		while (!TableSectionElement.is(parent) && !TableCellElement.is(parent)) {
			parent = parent.getParentElement();
		}
		return parent;
	}

	/**
	 * Commit the change to the underlying model. Implementations should use the
	 * protected <code>valueUpdater</code> initialised in onBrowseEvent to pass
	 * new values to the model. Implementations should also invoke
	 * <code>setValue</code> to write the new value back to the Cell's HTML
	 */
	protected abstract void commit();

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
	protected void onEnterKeyDown(Element parent, C value, Object key,
			NativeEvent event, ValueUpdater<C> valueUpdater) {
		this.lastKey = key;
		this.lastParent = parent;
		this.lastValue = value;
		this.valueUpdater = valueUpdater;
		startEditing(parent, value, key);
	}

	/**
	 * Initiate editing within the "Popup". Implementations should populate the
	 * child controls within the "Popup" before showing the Popup
	 * <code>panel</code>
	 * 
	 * @param parent
	 * @param value
	 * @param key
	 */
	protected abstract void startEditing(final Element parent, C value,
			Object key);

}
