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

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author manstis
 * 
 */
public class VerticalDecisionTableHeaderWidget extends
		DecisionTableHeaderWidget {

	/**
	 * This is the actual header widget.
	 * 
	 * @author manstis
	 * 
	 */
	private class HeaderWidget extends Composite {

		private HorizontalPanel hp = new HorizontalPanel();
		private DynamicColumn visibleRowNumberCol = null;
		private DynamicColumn visibleDescriptionCol = null;
		private List<DynamicColumn> visibleMetadataCols = new ArrayList<DynamicColumn>();
		private List<DynamicColumn> visibleAttributeCols = new ArrayList<DynamicColumn>();
		private List<DynamicColumn> visibleConditionCols = new ArrayList<DynamicColumn>();
		private List<DynamicColumn> visibleActionCols = new ArrayList<DynamicColumn>();

		private int rowsVisible = 3;

		private HeaderWidget() {
			initWidget(hp);
		}

		// Redraw entire header
		private void redraw() {
			hp.clear();
			visibleRowNumberCol = null;
			visibleDescriptionCol = null;
			visibleMetadataCols.clear();
			visibleAttributeCols.clear();
			visibleConditionCols.clear();
			visibleActionCols.clear();
			for (int iCol = 0; iCol < dtable.getColumns().size(); iCol++) {
				DynamicColumn col = dtable.getColumns().get(iCol);
				if (col.isVisible()) {
					DTColumnConfig modelCol = col.getModelColumn();
					if (modelCol instanceof RowNumberCol) {
						visibleRowNumberCol = col;
					} else if (modelCol instanceof DescriptionCol) {
						visibleDescriptionCol = col;
					} else if (modelCol instanceof MetadataCol) {
						visibleMetadataCols.add(col);
					} else if (modelCol instanceof AttributeCol) {
						visibleAttributeCols.add(col);
					} else if (modelCol instanceof ConditionCol) {
						visibleConditionCols.add(col);
					} else if (modelCol instanceof ActionCol) {
						visibleActionCols.add(col);
					}
				}
			}

			if (visibleRowNumberCol != null) {
				hp.add(makeSimpleColumnHeader(visibleRowNumberCol, "#"));
			}
			if (visibleDescriptionCol != null) {
				hp.add(makeSimpleColumnHeader(visibleDescriptionCol,
						"Description"));
			}
			for (DynamicColumn col : visibleMetadataCols) {
				MetadataCol mdc = (MetadataCol) col.getModelColumn();
				hp.add(makeSimpleColumnHeader(col, mdc.attr));
			}
			for (DynamicColumn col : visibleAttributeCols) {
				AttributeCol ac = (AttributeCol) col.getModelColumn();
				hp.add(makeSimpleColumnHeader(col, ac.attr));
			}
			if (visibleConditionCols.size() > 0) {
				int width = 0;
				VerticalPanel cvp = new VerticalPanel();
				cvp.addStyleName(style.headerCellPrimary());
				HorizontalPanel chp1 = new HorizontalPanel();
				HorizontalPanel chp2 = new HorizontalPanel();
				HorizontalPanel chp3 = new HorizontalPanel();
				HorizontalPanel chp4 = new HorizontalPanel();
				chp1.setWidth("100%");
				chp2.setWidth("100%");
				chp3.setWidth("100%");
				chp4.setWidth("100%");

				chp2.setHeight(style.rowHeaderSplitterHeight() + "px");
				chp2.getElement().getStyle().setBackgroundColor("#5599ff");

				for (DynamicColumn col : visibleConditionCols) {
					width = width + col.getWidth();
					ConditionCol cc = (ConditionCol) col.getModelColumn();

					VerticalPanel hp1 = new VerticalPanel();
					hp1.add(new SortHeaderPanel(col));
					hp1.add(makeTextHeader(col, cc.getHeader(),
							style.headerCellSecondary()));
					chp1.add(hp1);

					Panel hp4 = makeTextHeader(col, cc.getFactField(),
							style.headerCellSecondary());
					chp4.add(hp4);
				}
				HorizontalPanel hp3 = makeConditionFieldTypePanel(visibleConditionCols);
				chp3.add(hp3);

				cvp.add(chp1);
				cvp.add(chp2);
				cvp.add(chp3);
				cvp.add(chp4);
				cvp.setWidth(width + "px");
				hp.add(cvp);
			}
			for (DynamicColumn col : visibleActionCols) {
				ActionCol ac = (ActionCol) col.getModelColumn();
				hp.add(makeSimpleColumnHeader(col, ac.getHeader()));
			}

		}

		private HorizontalPanel makeConditionFieldTypePanel(
				List<DynamicColumn> columns) {
			HorizontalPanel hp = new HorizontalPanel();
			for (int iCol = 0; iCol < columns.size(); iCol++) {
				DynamicColumn col = columns.get(iCol);
				ConditionCol condCol = (ConditionCol) col.getModelColumn();
				Panel htc = makeTextHeader(col, condCol.getFactType() + " ["
						+ condCol.getBoundName() + "]",
						style.headerCellSecondary());
				int width = col.getWidth();

				int iMergeCol = iCol + 1;
				while (iMergeCol < columns.size()) {
					DynamicColumn mergeCol = columns.get(iMergeCol);
					ConditionCol mergeCondCol = (ConditionCol) mergeCol
							.getModelColumn();

					if (mergeCondCol.getFactType()
							.equals(condCol.getFactType())
							&& mergeCondCol.getBoundName().equals(
									condCol.getBoundName())) {
						width = width + mergeCol.getWidth();
						iCol++;
						iMergeCol++;
					} else {
						break;
					}
				}
				htc.setWidth(width + "px");
				hp.add(htc);
			}
			return hp;
		}

		private class SortHeaderPanel extends FocusPanel {

			private final HorizontalPanel sortPanel = new HorizontalPanel();
			private final DynamicColumn col;

			SortHeaderPanel(final DynamicColumn col) {
				this.col = col;
				sortPanel.setHeight("16px");
				add(sortPanel);

				if (col.isSortable()) {
					setSortIcon();
					addClickHandler(new ClickHandler() {

						public void onClick(ClickEvent event) {
							updateSortOrder(col);
							setSortIcon();
							dtable.sort();
						}

					});
				}
			}

			void setSortIcon() {

				ImageResource ir = null;
				switch (col.getSortDirection()) {
				case ASCENDING:
					switch (col.getSortIndex()) {
					case 0:
						ir = resource.upArrow();
						break;
					default:
						ir = resource.smallUpArrow();
					}
					break;
				case DESCENDING:
					switch (col.getSortIndex()) {
					case 0:
						ir = resource.downArrow();
						break;
					default:
						ir = resource.smallDownArrow();
					}
					break;
				default:
					ir = null;
				}
				if (ir != null) {
					Image img = new Image(ir);
					sortPanel.clear();
					sortPanel.add(img);
				}
			}

		}

		private Panel makeSimpleColumnHeader(DynamicColumn col, String caption) {
			VerticalPanel vp = new VerticalPanel();
			vp.addStyleName(style.headerTable());
			vp.add(new SortHeaderPanel(col));
			vp.add(makeTextHeader(col, caption));
			return vp;
		}

		private Panel makeTextHeader(DynamicColumn col, String caption) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth(col.getWidth() + "px");
			Label label = new Label(caption);
			label.addStyleName(style.headerText());
			label.setWidth(col.getWidth() + "px");
			label.setHeight((style.rowHeaderHeight() * rowsVisible)
					+ style.rowHeaderSplitterHeight() + "px");
			label.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			vp.add(label);
			return vp;
		}

		private Panel makeTextHeader(DynamicColumn col, String caption,
				String styleName) {
			Panel hp = makeTextHeader(col, caption);
			hp.addStyleName(styleName);
			return hp;
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
