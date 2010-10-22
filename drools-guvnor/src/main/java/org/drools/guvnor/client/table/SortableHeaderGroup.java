package org.drools.guvnor.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.cellview.client.CellTable;

/**
 * @author Geoffrey De Smet
 */
public class SortableHeaderGroup<R extends Comparable> {

    private final CellTable<R> cellTable;
    // TODO change List into Deque after upgrade to java 6
    private List<SortableHeader<R, ?>> sortOrderList = new LinkedList<SortableHeader<R, ?>>();

    public SortableHeaderGroup(CellTable<R> cellTable) {

        this.cellTable = cellTable;
    }

    public void headerClicked(SortableHeader<R, ?> header) {
        updateSortOrder(header);
        cellTable.redrawHeaders();
        updateData();
    }

    private void updateSortOrder(SortableHeader<R, ?> header) {
        int index = sortOrderList.indexOf(header);
        if (index == 0) {
            if (header.getSortDirection() != SortDirection.ASCENDING) {
                header.setSortDirection(SortDirection.ASCENDING);
            } else {
                header.setSortDirection(SortDirection.DESCENDING);
            }
        } else {
            // Remove it if it's already sorted on this header later
            if (index > 0) {
                sortOrderList.remove(index);
            }
            header.setSortDirection(SortDirection.ASCENDING);
            // Bring this header to front // Deque.addFirst(sortableHeader)
            sortOrderList.add(0, header);
            // Update sortIndexes
            int sortIndex = 0;
            for (SortableHeader<R, ?> sortableHeader : sortOrderList) {
                sortableHeader.setSortIndex(sortIndex);
                sortIndex++;
            }
        }
    }

    private void updateData() {
        // TODO If paging is used, this should be a back-end call with a sorting meta data parameter
        List<R> displayedItems = new ArrayList(cellTable.getDisplayedItems());
        Collections.sort(displayedItems, new Comparator<R>() {
            public int compare(R left, R right) {
                for (SortableHeader<R, ?> sortableHeader : sortOrderList) {
                    Comparable leftValue = sortableHeader.getColumn().getValue(left);
                    Comparable rightValue = sortableHeader.getColumn().getValue(left);
                    int comparison = (leftValue == rightValue) ? 0
                            : (leftValue == null) ? -1
                            : (rightValue == null) ? 1
                            : leftValue.compareTo(rightValue);
                    if (comparison != 0) {
                        switch (sortableHeader.getSortDirection()) {
                            case ASCENDING:
                                break;
                            case DESCENDING:
                                comparison = -comparison;
                            default:
                                throw new IllegalStateException("Sorting can only be enabled for ASCENDING or" +
                                        " DESCENDING, not sortDirection (" + sortableHeader.getSortDirection() + ") .");
                        }
                        return comparison;
                    }
                }
                return left.compareTo(right);
            }
        });
        cellTable.setRowData(0, displayedItems);
        cellTable.redraw();
    }

}
