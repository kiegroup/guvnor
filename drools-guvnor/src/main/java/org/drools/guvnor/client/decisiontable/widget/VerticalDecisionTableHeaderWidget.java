package org.drools.guvnor.client.decisiontable.widget;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.table.SortDirection;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Header for a Vertical Decision Table
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableHeaderWidget extends
		DecisionTableHeaderWidget {

	/**
	 * This is the guts of the widget.
	 */
	private class HeaderWidget extends CellPanel implements HasResizeHandlers {

		/**
		 * A Widget to display sort order
		 */
		private class HeaderSorter extends FocusPanel {

			private final HorizontalPanel hp = new HorizontalPanel();
			private final DynamicColumn col;

			private HeaderSorter(final DynamicColumn col) {
				this.col = col;
				hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
				hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
				hp.setHeight(style.rowHeaderSorterHeight() + "px");
				hp.setWidth("100%");
				setIconImage();
				add(hp);

				// Ensure our icon is updated when the SortDirection changes
				col.addValueChangeHandler(new ValueChangeHandler<SortConfiguration>() {

					public void onValueChange(
							ValueChangeEvent<SortConfiguration> event) {
						setIconImage();
					}

				});
			}

			// Set icon's resource accordingly
			private void setIconImage() {
				hp.clear();
				switch (col.getSortDirection()) {
				case ASCENDING:
					switch (col.getSortIndex()) {
					case 0:
						hp.add(new Image(resource.upArrow()));
						break;
					default:
						hp.add(new Image(resource.smallUpArrow()));
					}
					break;
				case DESCENDING:
					switch (col.getSortIndex()) {
					case 0:
						hp.add(new Image(resource.downArrow()));
						break;
					default:
						hp.add(new Image(resource.smallDownArrow()));
					}
					break;
				}
			}

		}

		/**
		 * A Widget to split Conditions section
		 */
		private class HeaderSplitter extends FocusPanel {

			/**
			 * Animation to change the height of a row
			 */
			private class HeaderRowAnimation extends Animation {

				private TableRowElement tre;
				private int startHeight;
				private int endHeight;

				private HeaderRowAnimation(TableRowElement tre,
						int startHeight, int endHeight) {
					this.tre = tre;
					this.startHeight = startHeight;
					this.endHeight = endHeight;
				}

				// Set row height by setting height of children
				private void setHeight(int height) {
					for (int i = 0; i < tre.getChildCount(); i++) {
						tre.getChild(i).getFirstChild().<DivElement> cast()
								.getStyle().setHeight(height, Unit.PX);
					}

					// Decision Table and Sidebar need to know of new height
					ResizeEvent.fire(HeaderWidget.this, getBody()
							.getClientWidth(), getBody().getClientHeight());
				}

				@Override
				protected void onComplete() {
					super.onComplete();
					setHeight(endHeight);
				}

				@Override
				protected void onUpdate(double progress) {
					int height = (int) (startHeight + (progress * (endHeight - startHeight)));
					setHeight(height);
				}

			}

			private Element[] rowHeaders;
			private final HorizontalPanel hp = new HorizontalPanel();
			private final Image icon = new Image();
			private boolean isCollapsed = false;

			private HeaderSplitter(Element[] rowHeaders) {
				this.rowHeaders = rowHeaders;
				hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
				hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
				hp.setWidth("100%");
				setIconImage();
				hp.add(icon);
				add(hp);

				// Handle action
				addClickHandler(new ClickHandler() {

					public void onClick(ClickEvent event) {
						if (isCollapsed) {
							showRow(2);
							showRow(3);
						} else {
							hideRow(2);
							hideRow(3);
						}
						isCollapsed = !isCollapsed;
						setIconImage();
					}

				});
			}

			// Hide a row using our animation
			private void hideRow(int iRow) {
				TableRowElement tre = rowHeaders[iRow].<TableRowElement> cast();
				HeaderRowAnimation anim = new HeaderRowAnimation(tre,
						style.rowHeaderHeight(), 0);
				anim.run(250);
			}

			// Set icon's resource accordingly
			private void setIconImage() {
				if (isCollapsed) {
					icon.setResource(resource.smallUpArrow());
				} else {
					icon.setResource(resource.smallDownArrow());
				}
			}

			// Show a row using our animation
			private void showRow(int iRow) {
				TableRowElement tre = rowHeaders[iRow].<TableRowElement> cast();
				HeaderRowAnimation anim = new HeaderRowAnimation(tre, 0,
						style.rowHeaderHeight());
				anim.run(250);
			}

		}

		// UI Components
		private Element[] rowHeaders = new Element[5];
		private List<DynamicColumn> visibleConditionCols = new ArrayList<DynamicColumn>();

		// Constructor
		private HeaderWidget() {
			for (int iRow = 0; iRow < rowHeaders.length; iRow++) {
				rowHeaders[iRow] = DOM.createTR();
				getBody().appendChild(rowHeaders[iRow]);
				getBody().getParentElement().<TableElement> cast()
						.setCellSpacing(0);
				getBody().getParentElement().<TableElement> cast()
						.setCellPadding(0);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.event.logical.shared.HasResizeHandlers#addResizeHandler
		 * (com.google.gwt.event.logical.shared.ResizeHandler)
		 */
		public HandlerRegistration addResizeHandler(ResizeHandler handler) {
			return this.addHandler(handler, ResizeEvent.getType());
		}

		// Make default header label
		private Element makeLabel(String text, int width) {
			Element div = DOM.createDiv();
			div.getStyle().setWidth(width, Unit.PX);
			div.getStyle().setHeight(style.rowHeaderHeight(), Unit.PX);
			div.getStyle().setOverflow(Overflow.HIDDEN);
			Label caption = new Label(text);
			div.appendChild(caption.getElement());
			return div;
		}

		// Populate a default header element
		private void populateTableCellElement(DynamicColumn col, Element tce) {

			DTColumnConfig modelCol = col.getModelColumn();
			if (modelCol instanceof RowNumberCol) {
				tce.appendChild(makeLabel("#", col.getWidth()));
				tce.<TableCellElement> cast().setRowSpan(4);
				tce.addClassName(style.headerRowIntermediate());
			} else if (modelCol instanceof DescriptionCol) {
				tce.appendChild(makeLabel("Description", col.getWidth()));
				tce.<TableCellElement> cast().setRowSpan(4);
				tce.addClassName(style.headerRowIntermediate());
			} else if (modelCol instanceof MetadataCol) {
				tce.appendChild(makeLabel(((MetadataCol) modelCol).attr,
						col.getWidth()));
				tce.<TableCellElement> cast().setRowSpan(4);
				tce.addClassName(style.headerRowIntermediate());
			} else if (modelCol instanceof AttributeCol) {
				tce.appendChild(makeLabel(((AttributeCol) modelCol).attr,
						col.getWidth()));
				tce.<TableCellElement> cast().setRowSpan(4);
				tce.addClassName(style.headerRowIntermediate());
			} else if (modelCol instanceof ConditionCol) {
				tce.appendChild(makeLabel(
						((ConditionCol) modelCol).getHeader(), col.getWidth()));
			} else if (modelCol instanceof ActionCol) {
				tce.appendChild(makeLabel(((ActionCol) modelCol).getHeader(),
						col.getWidth()));
				tce.<TableCellElement> cast().setRowSpan(4);
				tce.addClassName(style.headerRowIntermediate());
			}

		}

		// Redraw entire header
		private void redraw() {

			// Extracting visible Condition columns makes life easier
			visibleConditionCols.clear();
			for (int iCol = 0; iCol < dtable.getColumns().size(); iCol++) {
				DynamicColumn col = dtable.getColumns().get(iCol);
				if (col.isVisible()) {
					DTColumnConfig modelCol = col.getModelColumn();
					if (modelCol instanceof ConditionCol) {
						visibleConditionCols.add(col);
					}
				}
			}

			// Draw rows
			for (int iRow = 0; iRow < rowHeaders.length; iRow++) {
				redrawHeaderRow(iRow);
			}

			// Schedule resize event after header has been drawn
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				public void execute() {
					ResizeEvent.fire(HeaderWidget.this, getBody()
							.getClientWidth(), getBody().getClientHeight());
				}
			});

		}

		// Redraw a single row obviously
		private void redrawHeaderRow(int iRow) {
			Element tce = null;
			Element tre = DOM.createTR();
			switch (iRow) {
			case 0:
				// General row, all visible cells included
				for (DynamicColumn col : dtable.getColumns()) {
					if (col.isVisible()) {
						tce = DOM.createTD();
						tce.getStyle().setWidth(col.getWidth(), Unit.PX);
						tce.addClassName(style.headerText());
						tre.appendChild(tce);
						populateTableCellElement(col, tce);
					}
				}
				break;

			case 1:
				// Splitter between "general" and "technical" condition details
				if (visibleConditionCols.size() > 0) {
					HeaderSplitter splitter = new HeaderSplitter(rowHeaders);
					tce = DOM.createTD();
					tce.<TableCellElement> cast().setColSpan(
							visibleConditionCols.size());
					tce.addClassName(style.headerSplitter());
					tre.appendChild(tce);
					add(splitter, tce);
				}
				break;

			case 2:
				// Condition FactType, merged between identical
				for (int iCol = 0; iCol < visibleConditionCols.size(); iCol++) {
					tce = DOM.createTD();
					tce.addClassName(style.headerText());
					tre.appendChild(tce);

					DynamicColumn col = visibleConditionCols.get(iCol);
					ConditionCol cc = (ConditionCol) col.getModelColumn();

					// Merging
					int colSpan = 1;
					int width = col.getWidth();
					while (iCol + colSpan < visibleConditionCols.size()) {
						DynamicColumn mergeCol = visibleConditionCols.get(iCol
								+ colSpan);
						ConditionCol mergeCondCol = (ConditionCol) mergeCol
								.getModelColumn();

						if (mergeCondCol.getFactType().equals(cc.getFactType())
								&& mergeCondCol.getBoundName().equals(
										cc.getBoundName())) {
							width = width + mergeCol.getWidth();
							colSpan++;
						} else {
							break;
						}
					}

					// Make cell
					iCol = iCol + colSpan - 1;
					tce.getStyle().setWidth(width, Unit.PX);
					tce.addClassName(style.headerRowIntermediate());
					tce.appendChild(makeLabel(
							cc.getFactType() + " [" + cc.getBoundName() + "]",
							width));
					tce.<TableCellElement> cast().setColSpan(colSpan);

				}
				break;

			case 3:
				// Condition FactField
				for (DynamicColumn col : visibleConditionCols) {
					tce = DOM.createTD();
					tce.getStyle().setWidth(col.getWidth(), Unit.PX);
					tce.addClassName(style.headerText());
					tce.addClassName(style.headerRowIntermediate());
					tre.appendChild(tce);
					ConditionCol cc = (ConditionCol) col.getModelColumn();
					tce.appendChild(makeLabel(cc.getFactField(), col.getWidth()));
				}
				break;

			case 4:
				// Sorters
				for (DynamicColumn col : dtable.getColumns()) {
					if (col.isVisible()) {
						final HeaderSorter shp = new HeaderSorter(col);
						final DynamicColumn sortableColumn = col;
						shp.addClickHandler(new ClickHandler() {

							public void onClick(ClickEvent event) {
								if (sortableColumn.isSortable()) {
									updateSortOrder(sortableColumn);
									// redrawHeaderRow(4);
									dtable.sort();
								}
							}

						});

						tce = DOM.createTD();
						tce.getStyle().setWidth(col.getWidth(), Unit.PX);
						tce.addClassName(style.headerRowBottom());
						tre.appendChild(tce);
						add(shp, tce);
					}
				}
				break;
			}

			getBody().replaceChild(tre, rowHeaders[iRow]);
			rowHeaders[iRow] = tre;
		}

		// Update sort order. The column clicked becomes the primary sort column
		// and the other, previously sorted, columns degrade in priority
		private void updateSortOrder(DynamicColumn column) {
			if (column.getSortIndex() == 0) {
				if (column.getSortDirection() != SortDirection.ASCENDING) {
					column.setSortDirection(SortDirection.ASCENDING);
				} else {
					column.setSortDirection(SortDirection.DESCENDING);
				}
			} else {
				column.setSortIndex(0);
				column.setSortDirection(SortDirection.ASCENDING);
				int sortIndex = 1;
				for (DynamicColumn sortableColumn : dtable.getColumns()) {
					if (!sortableColumn.equals(column)) {
						if (sortableColumn.getSortDirection() != SortDirection.NONE) {
							sortableColumn.setSortIndex(sortIndex);
							sortIndex++;
						}
					}
				}
			}
		}

	}

	// UI Components
	private HeaderWidget widget;

	/**
	 * Construct a "Header" for the provided DecisionTable
	 * 
	 * @param decisionTable
	 */
	public VerticalDecisionTableHeaderWidget(DecisionTableWidget dtable) {
		super(dtable);

		// Construct the Widget
		panel = new ScrollPanel();
		widget = new HeaderWidget();

		// We don't want scroll bars on the Header
		panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		panel.add(widget);
		initWidget(panel);

	}

	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return widget.addResizeHandler(handler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
	 * #redraw()
	 */
	@Override
	public void redraw() {
		widget.redraw();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.decisiontable.client.widget.DecisionTableHeaderWidget
	 * #setScrollPosition(int)
	 */
	@Override
	public void setScrollPosition(int position) {
		((ScrollPanel) this.panel).setHorizontalScrollPosition(position);
	}

}
