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
 */
public class TypeSafeGuidedDecisionTable
    implements
    PortableObject {

    private static final long       serialVersionUID      = 510l;

    /**
     * Number of internal elements before ( used for offsets in serialization )
     */
    public static final int         INTERNAL_ELEMENTS     = 2;

    /**
     * Various attribute names
     */
    public static final String      SALIENCE_ATTR         = "salience";
    public static final String      ENABLED_ATTR          = "enabled";
    public static final String      DATE_EFFECTIVE_ATTR   = "date-effective";
    public static final String      DATE_EXPIRES_ATTR     = "date-expires";
    public static final String      NO_LOOP_ATTR          = "no-loop";
    public static final String      AGENDA_GROUP_ATTR     = "agenda-group";
    public static final String      ACTIVATION_GROUP_ATTR = "activation-group";
    public static final String      DURATION_ATTR         = "duration";
    public static final String      AUTO_FOCUS_ATTR       = "auto-focus";
    public static final String      LOCK_ON_ACTIVE_ATTR   = "lock-on-active";
    public static final String      RULEFLOW_GROUP_ATTR   = "ruleflow-group";
    public static final String      DIALECT_ATTR          = "dialect";
    public static final String      NEGATE_RULE_ATTR      = "negate";

    /**
     * The name - obviously.
     */
    private String                  tableName;

    private String                  parentName;

    // metadata defined for table ( will be represented as a column per table row of DATA
    private RowNumberCol            rowNumberCol          = new RowNumberCol();

    private DescriptionCol          descriptionCol        = new DescriptionCol();

    private List<MetadataCol>       metadataCols          = new ArrayList<MetadataCol>();

    private List<AttributeCol>      attributeCols         = new ArrayList<AttributeCol>();

    private List<ConditionCol>      conditionCols         = new ArrayList<ConditionCol>();

    private List<ActionCol>         actionCols            = new ArrayList<ActionCol>();

    /**
     * First column is always row number. Second column is description.
     * Subsequent ones follow the above column definitions: attributeCols, then
     * conditionCols, then actionCols, in that order, left to right.
     */
    private List<List<DTCellValue>> data                  = new ArrayList<List<DTCellValue>>();

    public TypeSafeGuidedDecisionTable() {
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

    public List<List<DTCellValue>> getData() {
        return data;
    }

    public List<DTColumnConfig> getAllColumns() {
        List<DTColumnConfig> columns = new ArrayList<DTColumnConfig>();
        columns.add( rowNumberCol );
        columns.add( descriptionCol );
        columns.addAll( metadataCols );
        columns.addAll( attributeCols );
        columns.addAll( conditionCols );
        columns.addAll( actionCols );
        return columns;
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

        if ( col instanceof RowNumberCol ) {
            type = SuggestionCompletionEngine.TYPE_NUMERIC;

        } else if ( col instanceof DescriptionCol ) {
            type = SuggestionCompletionEngine.TYPE_STRING;

        } else if ( col instanceof AttributeCol ) {
            AttributeCol attrCol = (AttributeCol) col;
            String attrName = attrCol.getAttribute();
            if ( attrName.equals( TypeSafeGuidedDecisionTable.SALIENCE_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.ENABLED_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.NO_LOOP_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.DURATION_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.AUTO_FOCUS_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.LOCK_ON_ACTIVE_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.DATE_EFFECTIVE_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_DATE;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.DATE_EXPIRES_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_DATE;
            } else if ( attrName.equals( TypeSafeGuidedDecisionTable.DIALECT_ATTR ) ) {
                type = SuggestionCompletionEngine.TYPE_STRING;
            }
        } else if ( col instanceof ConditionCol ) {
            ConditionCol c = (ConditionCol) col;
            type = sce.getFieldType( c.getFactType(),
                                     c.getFactField() );
            type = (assertDataType( c,
                                    sce,
                                    type ) ? type : null);
        } else if ( col instanceof ActionSetFieldCol ) {
            ActionSetFieldCol c = (ActionSetFieldCol) col;
            type = sce.getFieldType( getBoundFactType( c.getBoundName() ),
                                     c.getFactField() );
            type = (assertDataType( c,
                                    sce,
                                    type ) ? type : null);
        } else if ( col instanceof ActionInsertFactCol ) {
            ActionInsertFactCol c = (ActionInsertFactCol) col;
            type = sce.getFieldType( c.getFactType(),
                                     c.getFactField() );
            type = (assertDataType( c,
                                    sce,
                                    type ) ? type : null);
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

    public void setData(List<List<DTCellValue>> data) {
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

    private boolean assertDataType(ConditionCol col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            if ( col.getOperator() == null || "".equals( col.getOperator() ) ) {
                return false;
            }
            String ft = sce.getFieldType( col.getFactType(),
                                          col.getFactField() );
            if ( ft != null && ft.equals( dataType ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean assertDataType(ActionSetFieldCol col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {
        String ft = sce.getFieldType( getBoundFactType( col.getBoundName() ),
                                      col.getFactField() );
        if ( ft != null && ft.equals( dataType ) ) {
            return true;
        }
        return false;
    }

    private boolean assertDataType(ActionInsertFactCol col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {
        String ft = sce.getFieldType( col.getFactType(),
                                      col.getFactField() );
        if ( ft != null && ft.equals( dataType ) ) {
            return true;
        }
        return false;
    }

}
