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

/**
 * This is a decision table model for a guided editor. It is not template or XLS
 * based. (template could be done relatively easily by taking a template, as a
 * String, and then String[][] data and driving the SheetListener interface in
 * the decision tables module). This works by taking the column definitions, and
 * combining them with the table of data to produce rule models.
 */
public class GuidedDecisionTable52
    implements
    PortableObject {

    private static final long         serialVersionUID      = 510l;

    /**
     * Number of internal elements before ( used for offsets in serialization )
     */
    public static final int           INTERNAL_ELEMENTS     = 2;

    /**
     * Various attribute names
     */
    public static final String        SALIENCE_ATTR         = "salience";
    public static final String        ENABLED_ATTR          = "enabled";
    public static final String        DATE_EFFECTIVE_ATTR   = "date-effective";
    public static final String        DATE_EXPIRES_ATTR     = "date-expires";
    public static final String        NO_LOOP_ATTR          = "no-loop";
    public static final String        AGENDA_GROUP_ATTR     = "agenda-group";
    public static final String        ACTIVATION_GROUP_ATTR = "activation-group";
    public static final String        DURATION_ATTR         = "duration";
    public static final String        TIMER_ATTR            = "timer";
    public static final String        CALENDARS_ATTR        = "calendars";
    public static final String        AUTO_FOCUS_ATTR       = "auto-focus";
    public static final String        LOCK_ON_ACTIVE_ATTR   = "lock-on-active";
    public static final String        RULEFLOW_GROUP_ATTR   = "ruleflow-group";
    public static final String        DIALECT_ATTR          = "dialect";
    public static final String        NEGATE_RULE_ATTR      = "negate";

    private String                    tableName;

    private String                    parentName;

    private RowNumberCol52            rowNumberCol          = new RowNumberCol52();

    private DescriptionCol52          descriptionCol        = new DescriptionCol52();

    private List<MetadataCol52>       metadataCols          = new ArrayList<MetadataCol52>();

    private List<AttributeCol52>      attributeCols         = new ArrayList<AttributeCol52>();

    private List<Pattern52>           conditionPatterns     = new ArrayList<Pattern52>();

    private List<ActionCol52>         actionCols            = new ArrayList<ActionCol52>();

    /**
     * First column is always row number. Second column is description.
     * Subsequent ones follow the above column definitions: attributeCols, then
     * conditionCols, then actionCols, in that order, left to right.
     */
    private List<List<DTCellValue52>> data                  = new ArrayList<List<DTCellValue52>>();

    public GuidedDecisionTable52() {
    }

    public List<ActionCol52> getActionCols() {
        return actionCols;
    }

    public List<AttributeCol52> getAttributeCols() {
        return attributeCols;
    }

    public List<Pattern52> getConditionPatterns() {
        return conditionPatterns;
    }

    public Pattern52 getConditionPattern(String boundName) {
        for ( Pattern52 p : conditionPatterns ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p;
            }
        }
        return null;
    }

    public Pattern52 getPattern(ConditionCol52 col) {
        for ( Pattern52 p : conditionPatterns ) {
            if ( p.getConditions().contains( col ) ) {
                return p;
            }
        }
        return new Pattern52();
    }

    public long getConditionsCount() {
        long size = 0;
        for ( Pattern52 p : this.conditionPatterns ) {
            size = size + p.getConditions().size();
        }
        return size;
    }

    public List<List<DTCellValue52>> getData() {
        return data;
    }

    public List<DTColumnConfig52> getAllColumns() {
        List<DTColumnConfig52> columns = new ArrayList<DTColumnConfig52>();
        columns.add( rowNumberCol );
        columns.add( descriptionCol );
        columns.addAll( metadataCols );
        columns.addAll( attributeCols );
        for ( Pattern52 p : this.conditionPatterns ) {
            for ( ConditionCol52 c : p.getConditions() ) {
                columns.add( c );
            }
        }
        columns.addAll( actionCols );
        return columns;
    }

    public DescriptionCol52 getDescriptionCol() {
        // De-serialising old models sets this field to null
        if ( this.descriptionCol == null ) {
            this.descriptionCol = new DescriptionCol52();
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

    public RowNumberCol52 getRowNumberCol() {
        // De-serialising old models sets this field to null
        if ( this.rowNumberCol == null ) {
            this.rowNumberCol = new RowNumberCol52();
        }
        return this.rowNumberCol;
    }

    public String getTableName() {
        return tableName;
    }

    public String getType(DTColumnConfig52 col,
                          SuggestionCompletionEngine sce) {
        if ( col instanceof RowNumberCol52 ) {
            return getType( (RowNumberCol52) col,
                            sce );
        } else if ( col instanceof DescriptionCol52 ) {
            return getType( (DescriptionCol52) col,
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

    private String getType(RowNumberCol52 col,
                           SuggestionCompletionEngine sce) {
        String type = SuggestionCompletionEngine.TYPE_NUMERIC;
        return type;
    }

    private String getType(DescriptionCol52 col,
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
        } else if ( attrName.equals( GuidedDecisionTable52.TIMER_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_STRING;
        } else if ( attrName.equals( GuidedDecisionTable52.CALENDARS_ATTR ) ) {
            type = SuggestionCompletionEngine.TYPE_STRING;
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

        Pattern52 pattern = getPattern( col );

        String type = sce.getFieldType( pattern.getFactType(),
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

        if ( col.getValueList() != null
                 && !"".equals( col.getValueList() ) ) {
            return col.getValueList().split( "," );
        } else {
            Pattern52 pattern = getPattern( col );
            String[] r = sce.getEnumValues( pattern.getFactType(),
                                            col.getFactField() );
            return (r != null) ? r : new String[0];
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
        if ( col instanceof RowNumberCol52 ) {
            return true;
        }
        if ( col instanceof DescriptionCol52 ) {
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

    public void setData(List<List<DTCellValue52>> data) {
        this.data = data;
    }

    public void setRowNumberCol(RowNumberCol52 rowNumberCol) {
        this.rowNumberCol = rowNumberCol;
    }

    public void setDescriptionCol(DescriptionCol52 descriptionCol) {
        this.descriptionCol = descriptionCol;
    }

    public void setMetadataCols(List<MetadataCol52> metadataCols) {
        this.metadataCols = metadataCols;
    }

    public void setAttributeCols(List<AttributeCol52> attributeCols) {
        this.attributeCols = attributeCols;
    }

    public void setConditionPatterns(List<Pattern52> conditionPatterns) {
        this.conditionPatterns = conditionPatterns;
    }

    public void setActionCols(List<ActionCol52> actionCols) {
        this.actionCols = actionCols;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String getBoundFactType(String boundName) {
        for ( Pattern52 p : this.conditionPatterns ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p.getFactType();
            }
        }
        return null;
    }

    private boolean assertDataType(ConditionCol52 col,
                                   SuggestionCompletionEngine sce,
                                   String dataType) {

        Pattern52 pattern = getPattern( col );

        if ( col.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL ) {
            if ( col.getOperator() == null || "".equals( col.getOperator() ) ) {
                return false;
            }
            String ft = sce.getFieldType( pattern.getFactType(),
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
