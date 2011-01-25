/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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

    // No longer used by retained to enable XStream to de-serialise legacy
    // tables.
    // See http://xstream.codehaus.org/faq.html#Serialization
    @SuppressWarnings("unused")
    private transient int      descriptionWidth  = -1;

    // No longer used by retained to enable XStream to de-serialise legacy
    // tables.
    // See http://xstream.codehaus.org/faq.html#Serialization
    @SuppressWarnings("unused")
    private transient String   groupField;

    // metadata defined for table ( will be represented as a column per table
    // row of DATA
    private RowNumberCol       rowNumberCol;

    private DescriptionCol     descriptionCol;

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

    public GuidedDecisionTable() {
    }

    public List<ActionCol> getActionCols() {
        return actionCols;
    }

    public List<AttributeCol> getAttributeCols() {
        return attributeCols;
    }

    public List<ConditionCol> getConditionCols() {
        return conditionCols;
    }

    public String[][] getData() {
        return data;
    }

    public DescriptionCol getDescriptionCol() {
        // De-serialising old models sets this field to null
        if ( this.descriptionCol == null ) {
            this.descriptionCol = new DescriptionCol();
        }
        return this.descriptionCol;
    }

    public List<MetadataCol> getMetadataCols() {
        if ( null == metadataCols ) {
            metadataCols = new ArrayList<MetadataCol>();
        }
        return metadataCols;
    }

    public String getParentName() {
        return parentName;
    }

    public RowNumberCol getRowNumberCol() {
        // De-serialising old models sets this field to null
        if ( this.rowNumberCol == null ) {
            this.rowNumberCol = new RowNumberCol();
        }
        return this.rowNumberCol;
    }

    public String getTableName() {
        return tableName;
    }

    public String getType(DTColumnConfig col,
                          SuggestionCompletionEngine sce) {
        String type = null;
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            type = at.getAttribute();
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

    /**
     * This will return a list of valid values. if there is no such
     * "enumeration" of values, then it will return an empty array.
     */
    public String[] getValueList(DTColumnConfig col,
                                 SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            if ( "no-loop".equals( at.getAttribute() )
                 || "enabled".equals( at.getAttribute() ) ) {
                return new String[]{"true", "false"};
            }
        } else if ( col instanceof ConditionCol ) {
            // conditions: if its a formula etc, just return String[0],
            // otherwise check with the sce
            ConditionCol c = (ConditionCol) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_RET_VALUE
                    || c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
                return new String[0];
            } else {
                if ( c.getValueList() != null
                     && !"".equals( c.getValueList() ) ) {
                    return c.getValueList().split( "," );
                } else {
                    String[] r = sce.getEnumValues( c.getFactType(),
                                                    c.getFactField() );
                    return (r != null) ? r : new String[0];
                }
            }
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            if ( c.getValueList() != null
                 && !"".equals( c.getValueList() ) ) {
                return c.getValueList().split( "," );
            } else {
                String[] r = sce.getEnumValues(
                                                getBoundFactType( c.getBoundName() ),
                                                c.getFactField() );
                return (r != null) ? r : new String[0];
            }
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            if ( c.getValueList() != null
                 && !"".equals( c.getValueList() ) ) {
                return c.getValueList().split( "," );
            } else {
                String[] r = sce.getEnumValues( c.getFactType(),
                                                c.getFactField() );
                return (r != null) ? r : new String[0];
            }
        }

        return new String[0];
    }

    public boolean isBoolean(DTColumnConfig col,
                             SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            return "enabled".equals( at.getAttribute() )
                   || "no-loop".equals( at.getAttribute() )
                    || "auto-focus".equals( at.getAttribute() )
                    || "lock-on-active".equals( at.getAttribute() );
        } else {
            return isDataType( col,
                               sce,
                               SuggestionCompletionEngine.TYPE_BOOLEAN );
        }
    }

    public boolean isConstraintValid(DTColumnConfig col,
                                     SuggestionCompletionEngine sce) {
        if ( col instanceof RowNumberCol ) {
            return true;
        }
        if ( col instanceof DescriptionCol ) {
            return true;
        }
        if ( col instanceof MetadataCol ) {
            return true;
        }
        if ( col instanceof AttributeCol ) {
            return true;
        }
        if ( col instanceof ConditionCol ) {
            ConditionCol c = (ConditionCol) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
                if ( c.getFactField() == null
                     || c.getFactField().equals( "" ) ) {
                    return false;
                }
                if ( c.getOperator() == null
                     || c.getOperator().equals( "" ) ) {
                    return false;
                }
                return true;
            }
            return true;
        }
        if ( col instanceof ActionCol ) {
            return true;
        }
        return false;
    }

    public boolean isDate(DTColumnConfig col,
                          SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            return "date-effective".equals( at.getAttribute() )
                    || "date-expires".equals( at.getAttribute() );
        } else {
            return isDataType( col,
                               sce,
                               SuggestionCompletionEngine.TYPE_DATE );
        }
    }

    public boolean isNumeric(DTColumnConfig col,
                             SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol ) {
            AttributeCol at = (AttributeCol) col;
            return "salience".equals( at.getAttribute() )
                   || "duration".equals( at.getAttribute() );
        } else {
            return isDataType( col,
                               sce,
                               SuggestionCompletionEngine.TYPE_NUMERIC );
        }
    }

    public void setData(String[][] data) {
        this.data = data;
    }

    public void setDescriptionCol(DescriptionCol descriptionCol) {
        this.descriptionCol = descriptionCol;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setRowNumberCol(RowNumberCol rowNumberCol) {
        this.rowNumberCol = rowNumberCol;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String getBoundFactType(String boundName) {
        for ( Iterator<ConditionCol> iterator = getConditionCols().iterator(); iterator
                .hasNext(); ) {
            ConditionCol c = iterator.next();
            if ( c.getBoundName().equals( boundName ) ) {
                return c.getFactType();
            }
        }
        return null;
    }

    private boolean isDataType(DTColumnConfig col,
                               SuggestionCompletionEngine sce,
                               String dataType) {
        if ( col instanceof RowNumberCol ) {
            throw new IllegalArgumentException(
                                                "Only ConditionCol and Actions permitted. Consider using one of the public is<DataType> methods." );
        }
        if ( col instanceof DescriptionCol ) {
            throw new IllegalArgumentException(
                                                "Only ConditionCol and Actions permitted. Consider using one of the public is<DataType> methods." );
        }
        if ( col instanceof MetadataCol ) {
            throw new IllegalArgumentException(
                                                "Only ConditionCol and Actions permitted. Consider using one of the public is<DataType> methods." );
        }
        if ( col instanceof AttributeCol ) {
            throw new IllegalArgumentException(
                                                "Only ConditionCol and Actions permitted. Consider using one of the public is<DataType> methods." );
        }
        if ( col instanceof ConditionCol ) {
            ConditionCol c = (ConditionCol) col;
            if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
                if ( c.getOperator() == null
                     || "".equals( c.getOperator() ) ) {
                    return false;
                }
                String ft = sce.getFieldType( c.getFactType(),
                                              c.getFactField() );
                if ( ft != null
                     && ft.equals( dataType ) ) {
                    return true;
                }
            }
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            String ft = sce.getFieldType( getBoundFactType( c.getBoundName() ),
                                          c.getFactField() );
            if ( ft != null
                 && ft.equals( dataType ) ) {
                return true;
            }
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            String ft = sce.getFieldType( c.getFactType(),
                                          c.getFactField() );
            if ( ft != null
                 && ft.equals( dataType ) ) {
                return true;
            }
        }
        return false;
    }

}
