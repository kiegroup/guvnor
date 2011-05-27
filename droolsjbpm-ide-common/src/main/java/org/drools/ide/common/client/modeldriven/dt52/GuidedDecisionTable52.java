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
package org.drools.ide.common.client.modeldriven.dt52;

import java.util.ArrayList;
import java.util.List;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.PortableObject;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

/**
 * This is a decision table model for a guided editor. It is not template or XLS
 * based. (template could be done relatively easily by taking a template, as a
 * String, and then String[][] data and driving the SheetListener interface in
 * the decision tables module).
 * 
 * This works by taking the column definitions, and combining them with the
 * table of data to produce rule models.
 */
public class GuidedDecisionTable52
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

    private String                  tableName;

    private String                  parentName;

    private RowNumberCol            rowNumberCol          = new RowNumberCol();

    private DescriptionCol          descriptionCol        = new DescriptionCol();

    private List<MetadataCol52>     metadataCols          = new ArrayList<MetadataCol52>();

    private List<AttributeCol52>    attributeCols         = new ArrayList<AttributeCol52>();

    private List<Pattern>           conditionPatterns     = new ArrayList<Pattern>();

    private List<ActionCol52>       actionCols            = new ArrayList<ActionCol52>();

    /**
     * First column is always row number. Second column is description.
     * Subsequent ones follow the above column definitions: attributeCols, then
     * conditionCols, then actionCols, in that order, left to right.
     */
    private List<List<DTCellValue>> data                  = new ArrayList<List<DTCellValue>>();

    public GuidedDecisionTable52() {
    }

    public List<ActionCol52> getActionCols() {
        return actionCols;
    }

    public List<AttributeCol52> getAttributeCols() {
        return attributeCols;
    }

    public List<Pattern> getConditionPatterns() {
        return conditionPatterns;
    }

    public Pattern getConditionPattern(String boundName) {
        for ( Pattern p : conditionPatterns ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p;
            }
        }
        return null;
    }

    public long getConditionsCount() {
        long size = 0;
        for ( Pattern p : this.conditionPatterns ) {
            size = size + p.getConditions().size();
        }
        return size;
    }

    public List<List<DTCellValue>> getData() {
        return data;
    }

    public List<DTColumnConfig52> getAllColumns() {
        List<DTColumnConfig52> columns = new ArrayList<DTColumnConfig52>();
        columns.add( rowNumberCol );
        columns.add( descriptionCol );
        columns.addAll( metadataCols );
        columns.addAll( attributeCols );
        for ( Pattern p : this.conditionPatterns ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                columns.add( c );
            }
        }
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

    public List<MetadataCol52> getMetadataCols() {
        if ( null == metadataCols ) {
            metadataCols = new ArrayList<MetadataCol52>();
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

    public String getType(DTColumnConfig52 col,
                          SuggestionCompletionEngine sce) {
        if ( col instanceof RowNumberCol ) {
            return getType( (RowNumberCol) col,
                            sce );
        } else if ( col instanceof DescriptionCol ) {
            return getType( (DescriptionCol) col,
                            sce );
        } else if ( col instanceof AttributeCol52 ) {
            return getType( (AttributeCol52) col,
                            sce );
        } else if ( col instanceof ConditionCol52 ) {
            return getType( (ConditionCol52) col,
                            sce );
        } else if ( col instanceof ActionSetFieldCol52 ) {
            return getType( (ActionSetFieldCol52) col,
                            sce );
        } else if ( col instanceof ActionInsertFactCol52 ) {
            return getType( (ActionInsertFactCol52) col,
                            sce );
        }
        return null;
    }

    private String getType(RowNumberCol col,
                           SuggestionCompletionEngine sce) {
        String type = SuggestionCompletionEngine.TYPE_NUMERIC;
        return type;
    }

    private String getType(DescriptionCol col,
                           SuggestionCompletionEngine sce) {
        String type = SuggestionCompletionEngine.TYPE_STRING;
        return type;
    }

    private String getType(AttributeCol52 col,
                           SuggestionCompletionEngine sce) {
        String type = null;
        String attrName = col.getAttribute();
        if ( attrName.equals( GuidedDecisionTable52.SALIENCE_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if ( attrName.equals( GuidedDecisionTable52.ENABLED_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.NO_LOOP_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.DURATION_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_NUMERIC;
        } else if ( attrName.equals( GuidedDecisionTable52.AUTO_FOCUS_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.LOCK_ON_ACTIVE_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_BOOLEAN;
        } else if ( attrName.equals( GuidedDecisionTable52.DATE_EFFECTIVE_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_DATE;
        } else if ( attrName.equals( GuidedDecisionTable52.DATE_EXPIRES_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_DATE;
        } else if ( attrName.equals( GuidedDecisionTable52.DIALECT_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_STRING;
        }
        return type;
    }

    private String getType(ConditionCol52 col,
                           SuggestionCompletionEngine sce) {
        String type = sce.getFieldType( col.getPattern().getFactType(),
                                        col.getFactField() );
        type = (assertDataType( col,
                                sce,
                                type ) ? type : null);
        return type;
    }

    private String getType(ActionSetFieldCol52 col,
                           SuggestionCompletionEngine sce) {
        String type = sce.getFieldType( getBoundFactType( col.getBoundName() ),
                                        col.getFactField() );
        type = (assertDataType( col,
                                sce,
                                type ) ? type : null);
        return type;
    }

    private String getType(ActionInsertFactCol52 col,
                           SuggestionCompletionEngine sce) {
        String type = sce.getFieldType( col.getFactType(),
                                        col.getFactField() );
        type = (assertDataType( col,
                                    sce,
                                    type ) ? type : null);
        return type;
    }

    public String[] getValueList(DTColumnConfig52 col,
                                 SuggestionCompletionEngine sce) {
        if ( col instanceof AttributeCol52 ) {
            return getValueList( (AttributeCol52) col,
                                 sce );
        } else if ( col instanceof ConditionCol52 ) {
            return getValueList( (ConditionCol52) col,
                                 sce );
        } else if ( col instanceof ActionSetFieldCol52 ) {
            return getValueList( (ActionSetFieldCol52) col,
                                 sce );
        } else if ( col instanceof ActionInsertFactCol52 ) {
            return getValueList( (ActionInsertFactCol52) col,
                                 sce );
        }
        return new String[0];
    }

    private String[] getValueList(AttributeCol52 col,
                                  SuggestionCompletionEngine sce) {
        if ( "no-loop".equals( col.getAttribute() )
                 || "enabled".equals( col.getAttribute() ) ) {
            return new String[]{"true", "false"};
        }
        return new String[0];
    }

    private String[] getValueList(ConditionCol52 col,
                                  SuggestionCompletionEngine sce) {
        // If its a formula etc, just return String[0] otherwise check with the sce
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_RET_VALUE
                    || col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            return new String[0];
        } else {
            if ( col.getValueList() != null
                     && !"".equals( col.getValueList() ) ) {
                return col.getValueList().split( "," );
            } else {
                String[] r = sce.getEnumValues( col.getPattern().getFactType(),
                                                col.getFactField() );
                return (r != null) ? r : new String[0];
            }
        }
    }

    private String[] getValueList(ActionSetFieldCol52 col,
                                  SuggestionCompletionEngine sce) {
        if ( col.getValueList() != null
                 && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        } else {
            String[] r = sce.getEnumValues( getBoundFactType( col.getBoundName() ),
                                                col.getFactField() );
            return (r != null) ? r : new String[0];
        }
    }

    private String[] getValueList(ActionInsertFactCol52 col,
                                  SuggestionCompletionEngine sce) {

        if ( col.getValueList() != null
                 && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        } else {
            String[] r = sce.getEnumValues( col.getFactType(),
                                                col.getFactField() );
            return (r != null) ? r : new String[0];
        }
    }

    public boolean isConstraintValid(DTColumnConfig52 col,
                                     SuggestionCompletionEngine sce) {
        if ( col instanceof RowNumberCol ) {
            return true;
        }
        if ( col instanceof DescriptionCol ) {
            return true;
        }
        if ( col instanceof MetadataCol52 ) {
            return true;
        }
        if ( col instanceof AttributeCol52 ) {
            return true;
        }
        if ( col instanceof ConditionCol52 ) {
            ConditionCol52 c = (ConditionCol52) col;
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
        if ( col instanceof ActionCol52 ) {
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
        for ( Pattern p : this.conditionPatterns ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p.getFactType();
            }
        }
        return null;
    }

    private boolean assertDataType(ConditionCol52 col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {
        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            if ( col.getOperator() == null || "".equals( col.getOperator() ) ) {
                return false;
            }
            String ft = sce.getFieldType( col.getPattern().getFactType(),
                                          col.getFactField() );
            if ( ft != null && ft.equals( dataType ) ) {
                return true;
            }
        }
        return false;
    }

    private boolean assertDataType(ActionSetFieldCol52 col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {
        String ft = sce.getFieldType( getBoundFactType( col.getBoundName() ),
                                      col.getFactField() );
        if ( ft != null && ft.equals( dataType ) ) {
            return true;
        }
        return false;
    }

    private boolean assertDataType(ActionInsertFactCol52 col,
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
