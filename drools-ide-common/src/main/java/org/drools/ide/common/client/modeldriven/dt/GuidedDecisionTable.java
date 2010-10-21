/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ide.common.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;

/**
 * This is a decision table model for a guided editor. It is not template or XLS
 * based. (template could be done relatively easily by taking a template, as a
 * String, and then String[][] data and driving the SheetListener interface in
 * the decision tables module).
 * 
 * This works by taking the column definitions, and combining them with the
 * table of data to produce rule models.
 * 
 * 
 * @author Michael Neale
 */
public class GuidedDecisionTable
    implements
    PortableObject {

    private static final long  serialVersionUID  = 510l;

    /**
     * Number of internal elements before ( used for offsets in serialization )
     */
    public static final int    INTERNAL_ELEMENTS = 2;

    /**
     * The name - obviously.
     */
    private String             tableName;

    private String             parentName;

    // metadata defined for table ( will be represented as a column per table
    // row of DATA
    private List<MetadataCol>  metadataCols;

    private List<AttributeCol> attributeCols     = new ArrayList<AttributeCol>();

    private List<ConditionCol> conditionCols     = new ArrayList<ConditionCol>();

    private List<ActionCol>    actionCols        = new ArrayList<ActionCol>();

    /**
     * First column is always row number. Second column is description.
     * Subsequent ones follow the above column definitions: attributeCols, then
     * conditionCols, then actionCols, in that order, left to right.
     */
    private String[][]         data              = new String[0][0];

    /**
     * The width to display the description column.
     */
    private int                descriptionWidth  = -1;

    private String             groupField;

    public GuidedDecisionTable() {
    }

    /**
     * This will return a list of valid values. if there is no such
     * "enumeration" of values, then it will return an empty array.
     */
    public String[] getValueList(DTColumnConfig col,
                                 SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            if ( "no-loop".equals( at.attr ) || "enabled".equals( at.attr ) ) {
                return new String[]{"true", "false"};
            }
        } else if ( col instanceof ConditionCol ) {
            // conditions: if its a formula etc, just return String[0],
            // otherwise check with the sce
            ConditionCol c = (ConditionCol) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_RET_VALUE || c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                return new String[0];
            } else {
                if ( c.getValueList() != null && !"".equals( c.getValueList() ) ) {
                    return c.getValueList().split( "," );
                } else {
                    String[] r = sce.getEnumValues( c.getFactType(),
                                                    c.getFactField() );
                    return (r != null) ? r : new String[0];
                }
            }
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            if ( c.getValueList() != null && !"".equals( c.getValueList() ) ) {
                return c.getValueList().split( "," );
            } else {
                String[] r = sce.getEnumValues( getBoundFactType( c.getBoundName() ),
                                                c.getFactField() );
                return (r != null) ? r : new String[0];
            }
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            if ( c.getValueList() != null && !"".equals( c.getValueList() ) ) {
                return c.getValueList().split( "," );
            } else {
                String[] r = sce.getEnumValues( c.getFactType(),
                                                c.getFactField() );
                return (r != null) ? r : new String[0];
            }
        }

        return new String[0];
    }

    public String getType(DTColumnConfig col,
                          SuggestionCompletionEngine sce) {
        String type = null;
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            type = at.attr;
        } else if ( col instanceof ConditionCol ) {
            ConditionCol c = (ConditionCol) col;
            type = sce.getFieldType( c.getFactType(),
                                     c.getFactField() );
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            type = sce.getFieldType( getBoundFactType( c.getBoundName() ),
                                     c.getFactField() );
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            type = sce.getFieldType( c.getFactType(),
                                     c.getFactField() );
        }

        return type;
    }

    private String getBoundFactType(String boundName) {
        for ( Iterator<ConditionCol> iterator = getConditionCols().iterator(); iterator.hasNext(); ) {
            ConditionCol c = iterator.next();
            if ( c.getBoundName().equals( boundName ) ) {
                return c.getFactType();
            }
        }
        return null;
    }

    public boolean isNumeric(DTColumnConfig col,
                             SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            return "salience".equals( at.attr );
        } else if ( col instanceof ConditionCol ) {
            ConditionCol c = (ConditionCol) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
                if ( c.getOperator() == null || "".equals( c.getOperator() ) ) {
                    return false;
                }
                String ft = sce.getFieldType( c.getFactType(),
                                              c.getFactField() );
                if ( ft != null && ft.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                    return true;
                }
            }
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            String ft = sce.getFieldType( getBoundFactType( c.getBoundName() ),
                                          c.getFactField() );
            if ( ft != null && ft.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                return true;
            }
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            String ft = sce.getFieldType( c.getFactType(),
                                          c.getFactField() );
            if ( ft != null && ft.equals( SuggestionCompletionEngine.TYPE_NUMERIC ) ) {
                return true;
            }
        }
        // we can reuse text filter from guided editor to enforce this for data
        // entry.
        return false;
    }

    public void setMetadataCols(List<MetadataCol> metadataCols) {
        this.metadataCols = metadataCols;
    }

    public List<MetadataCol> getMetadataCols() {
        if ( null == metadataCols ) {
            metadataCols = new ArrayList<MetadataCol>();
        }
        return metadataCols;
    }

    /**
     * Locate index of attribute name if it exists
     * 
     * @param attributeName
     *            Name of metadata we are looking for
     * @return index of attribute name or -1 if not found
     */
    public int getMetadataColIndex(String attributeName) {

        for ( int i = 0; metadataCols != null && i < metadataCols.size(); i++ ) {
            if ( attributeName.equals( metadataCols.get( i ).attr ) ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Update all rows of metadata with value it attribute is present
     * 
     * @param attributeName
     *            Name of metadata we are looking for
     * @return true if values update, false if not
     */
    public boolean updateMetadata(String attributeName,
                                  String newValue) {

        // see if metaData exists for
        int metaIndex = getMetadataColIndex( attributeName );
        if ( metaIndex < 0 ) return false;

        for ( int i = 0; i < getData().length; i++ ) {

            String[] row = getData()[i];

            row[GuidedDecisionTable.INTERNAL_ELEMENTS + metaIndex] = newValue;
        }
        return true;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public String getGroupField() {
        return groupField;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setAttributeCols(List<AttributeCol> attributeCols) {
        this.attributeCols = attributeCols;
    }

    public List<AttributeCol> getAttributeCols() {
        return attributeCols;
    }

    public void setConditionCols(List<ConditionCol> conditionCols) {
        this.conditionCols = conditionCols;
    }

    public List<ConditionCol> getConditionCols() {
        return conditionCols;
    }

    public void setActionCols(List<ActionCol> actionCols) {
        this.actionCols = actionCols;
    }

    public List<ActionCol> getActionCols() {
        return actionCols;
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public String[][] getData() {
        return data;
    }

    public void setDescriptionWidth(int descriptionWidth) {
        this.descriptionWidth = descriptionWidth;
    }

    public int getDescriptionWidth() {
        return descriptionWidth;
    }

}
