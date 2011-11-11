/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.ide.common.client.modeldriven.brl.ActionExecuteWorkItem;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldList;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldValue;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertFact;
import org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact;
import org.drools.ide.common.client.modeldriven.brl.ActionRetractFact;
import org.drools.ide.common.client.modeldriven.brl.ActionSetField;
import org.drools.ide.common.client.modeldriven.brl.ActionUpdateField;
import org.drools.ide.common.client.modeldriven.brl.ActionWorkItemFieldValue;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.FieldConstraint;
import org.drools.ide.common.client.modeldriven.brl.FromEntryPointFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IAction;
import org.drools.ide.common.client.modeldriven.brl.IFactPattern;
import org.drools.ide.common.client.modeldriven.brl.IPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleAttribute;
import org.drools.ide.common.client.modeldriven.brl.RuleMetadata;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt52.ActionCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionRetractFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemInsertFactCol52;
import org.drools.ide.common.client.modeldriven.dt52.ActionWorkItemSetFieldCol52;
import org.drools.ide.common.client.modeldriven.dt52.AttributeCol52;
import org.drools.ide.common.client.modeldriven.dt52.ConditionCol52;
import org.drools.ide.common.client.modeldriven.dt52.DTCellValue52;
import org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52;
import org.drools.ide.common.client.modeldriven.dt52.LimitedEntryCol;
import org.drools.ide.common.client.modeldriven.dt52.MetadataCol52;
import org.drools.ide.common.client.modeldriven.dt52.Pattern52;
import org.drools.ide.common.server.util.GuidedDTDRLOtherwiseHelper.OtherwiseBuilder;

/**
 * This takes care of converting GuidedDT object to DRL (via the RuleModel).
 */
public class GuidedDTDRLPersistence {

    public static GuidedDTDRLPersistence getInstance() {
        return new GuidedDTDRLPersistence();
    }

    public String marshal(GuidedDecisionTable52 dt) {

        StringBuilder sb = new StringBuilder();

        List<List<DTCellValue52>> data = dt.getData();
        List<DTColumnConfig52> allColumns = dt.getAllColumns();

        for ( int i = 0; i < data.size(); i++ ) {
            List<DTCellValue52> row = data.get( i );
            BigDecimal num = row.get( 0 ).getNumericValue();
            String desc = row.get( 1 ).getStringValue();

            RuleModel rm = new RuleModel();
            rm.name = getName( dt.getTableName(),
                               num );

            doMetadata( allColumns,
                        dt.getMetadataCols(),
                        row,
                        rm );
            doAttribs( allColumns,
                       dt.getAttributeCols(),
                       row,
                       rm );
            doConditions( allColumns,
                          dt.getConditionPatterns(),
                          row,
                          data,
                          rm );
            doActions( allColumns,
                       dt.getActionCols(),
                       row,
                       rm );

            if ( dt.getParentName() != null ) {
                rm.parentName = dt.getParentName();
            }

            sb.append( "#from row number: " + (i + 1) + "\n" );
            if ( desc != null && desc.length() > 0 ) {
                sb.append( "#" + desc + "\n" );
            }
            String rule = BRDRLPersistence.getInstance().marshal( rm );
            sb.append( rule );
            sb.append( "\n" );
        }

        return sb.toString();

    }

    void doActions(List<DTColumnConfig52> allColumns,
                   List<ActionCol52> actionCols,
                   List<DTCellValue52> row,
                   RuleModel rm) {
        List<LabelledAction> actions = new ArrayList<LabelledAction>();
        for ( ActionCol52 c : actionCols ) {

            int index = allColumns.indexOf( c );

            DTCellValue52 dcv = row.get( index );
            String cell = "";

            if ( c instanceof LimitedEntryCol ) {
                if ( dcv.getBooleanValue() == true ) {
                    LimitedEntryCol lec = (LimitedEntryCol) c;
                    cell = GuidedDTDRLUtilities.convertDTCellValueToString( lec.getValue() );
                }
            } else {
                cell = GuidedDTDRLUtilities.convertDTCellValueToString( dcv );
            }

            if ( !validCell( cell ) ) {
                cell = c.getDefaultValue();
            }

            if ( validCell( cell ) ) {
                if ( c instanceof ActionWorkItemInsertFactCol52 ) {
                    if ( Boolean.TRUE.equals( Boolean.parseBoolean( cell ) ) ) {
                        ActionWorkItemInsertFactCol52 ac = (ActionWorkItemInsertFactCol52) c;
                        LabelledAction a = findByLabelledAction( actions,
                                                                 ac.getBoundName() );
                        if ( a == null ) {
                            a = new LabelledAction();
                            a.boundName = ac.getBoundName();
                            if ( !ac.isInsertLogical() ) {
                                ActionInsertFact ins = new ActionInsertFact( ac.getFactType() );
                                ins.setBoundName( ac.getBoundName() );
                                a.action = ins;
                            } else {
                                ActionInsertLogicalFact ins = new ActionInsertLogicalFact( ac.getFactType() );
                                ins.setBoundName( ac.getBoundName() );
                                a.action = ins;
                            }
                            actions.add( a );
                        }
                        ActionInsertFact ins = (ActionInsertFact) a.action;
                        ActionWorkItemFieldValue val = new ActionWorkItemFieldValue( ac.getFactField(),
                                                                                     ac.getType(),
                                                                                     ac.getWorkItemName(),
                                                                                     ac.getWorkItemResultParameterName(),
                                                                                     ac.getParameterClassName() );
                        ins.addFieldValue( val );
                    }

                } else if ( c instanceof ActionInsertFactCol52 ) {
                    ActionInsertFactCol52 ac = (ActionInsertFactCol52) c;
                    LabelledAction a = findByLabelledAction( actions,
                                                             ac.getBoundName() );
                    if ( a == null ) {
                        a = new LabelledAction();
                        a.boundName = ac.getBoundName();
                        if ( !ac.isInsertLogical() ) {
                            ActionInsertFact ins = new ActionInsertFact( ac.getFactType() );
                            ins.setBoundName( ac.getBoundName() );
                            a.action = ins;
                        } else {
                            ActionInsertLogicalFact ins = new ActionInsertLogicalFact( ac.getFactType() );
                            ins.setBoundName( ac.getBoundName() );
                            a.action = ins;
                        }
                        actions.add( a );
                    }
                    ActionInsertFact ins = (ActionInsertFact) a.action;
                    ActionFieldValue val = new ActionFieldValue( ac.getFactField(),
                                                                 cell,
                                                                 ac.getType() );
                    ins.addFieldValue( val );

                } else if ( c instanceof ActionWorkItemSetFieldCol52 ) {
                    if ( Boolean.TRUE.equals( Boolean.parseBoolean( cell ) ) ) {
                        ActionWorkItemSetFieldCol52 sf = (ActionWorkItemSetFieldCol52) c;
                        LabelledAction a = findByLabelledAction( actions,
                                                                 sf.getBoundName() );
                        if ( a == null ) {
                            a = new LabelledAction();
                            a.boundName = sf.getBoundName();
                            if ( !sf.isUpdate() ) {
                                a.action = new ActionSetField( sf.getBoundName() );
                            } else {
                                a.action = new ActionUpdateField( sf.getBoundName() );
                            }
                            actions.add( a );
                        } else if ( sf.isUpdate() && !(a.action instanceof ActionUpdateField) ) {
                            // lets swap it out for an update as the user has asked for it.
                            ActionSetField old = (ActionSetField) a.action;
                            ActionUpdateField update = new ActionUpdateField( sf.getBoundName() );
                            update.fieldValues = old.fieldValues;
                            a.action = update;
                        }
                        ActionSetField asf = (ActionSetField) a.action;
                        ActionWorkItemFieldValue val = new ActionWorkItemFieldValue( sf.getFactField(),
                                                                                     sf.getType(),
                                                                                     sf.getWorkItemName(),
                                                                                     sf.getWorkItemResultParameterName(),
                                                                                     sf.getParameterClassName() );
                        asf.addFieldValue( val );
                    }

                } else if ( c instanceof ActionSetFieldCol52 ) {
                    ActionSetFieldCol52 sf = (ActionSetFieldCol52) c;
                    LabelledAction a = findByLabelledAction( actions,
                                                             sf.getBoundName() );
                    if ( a == null ) {
                        a = new LabelledAction();
                        a.boundName = sf.getBoundName();
                        if ( !sf.isUpdate() ) {
                            a.action = new ActionSetField( sf.getBoundName() );
                        } else {
                            a.action = new ActionUpdateField( sf.getBoundName() );
                        }
                        actions.add( a );
                    } else if ( sf.isUpdate() && !(a.action instanceof ActionUpdateField) ) {
                        // lets swap it out for an update as the user has asked for it.
                        ActionSetField old = (ActionSetField) a.action;
                        ActionUpdateField update = new ActionUpdateField( sf.getBoundName() );
                        update.fieldValues = old.fieldValues;
                        a.action = update;
                    }
                    ActionSetField asf = (ActionSetField) a.action;
                    ActionFieldValue val = new ActionFieldValue( sf.getFactField(),
                                                                 cell,
                                                                 sf.getType() );
                    asf.addFieldValue( val );

                } else if ( c instanceof ActionRetractFactCol52 ) {
                    LabelledAction a = new LabelledAction();
                    a.action = new ActionRetractFact( cell );
                    a.boundName = cell;
                    actions.add( a );
                } else if ( c instanceof ActionWorkItemCol52 ) {
                    if ( Boolean.TRUE.equals( Boolean.parseBoolean( cell ) ) ) {
                        ActionExecuteWorkItem aewi = new ActionExecuteWorkItem();
                        aewi.setWorkDefinition( ((ActionWorkItemCol52) c).getWorkItemDefinition() );
                        LabelledAction a = new LabelledAction();
                        a.action = aewi;
                        a.boundName = ((ActionWorkItemCol52) c).getWorkItemDefinition().getName();
                        actions.add( a );
                    }
                }
            }
        }

        rm.rhs = new IAction[actions.size()];
        for ( int i = 0; i < rm.rhs.length; i++ ) {
            rm.rhs[i] = actions.get( i ).action;
        }
    }

    //Labelled Actions are used to group actions on the same bound Fact. Only 
    //ActionSetField and ActionUpdateField need to be grouped in this manner.
    private LabelledAction findByLabelledAction(List<LabelledAction> actions,
                                                String boundName) {
        for ( LabelledAction labelledAction : actions ) {
            IAction action = labelledAction.action;
            if ( action instanceof ActionFieldList ) {
                if ( labelledAction.boundName.equals( boundName ) ) {
                    return labelledAction;
                }
            }
        }
        return null;
    }

    void doConditions(List<DTColumnConfig52> allColumns,
                      List<Pattern52> conditionPatterns,
                      List<DTCellValue52> row,
                      List<List<DTCellValue52>> data,
                      RuleModel rm) {

        List<IFactPattern> patterns = new ArrayList<IFactPattern>();

        for ( Pattern52 p : conditionPatterns ) {

            List<ConditionCol52> cols = p.getConditions();

            for ( ConditionCol52 c : cols ) {

                int index = allColumns.indexOf( c );

                DTCellValue52 dcv = row.get( index );
                String cell = "";

                if ( c instanceof LimitedEntryCol ) {
                    if ( dcv.getBooleanValue() == true ) {
                        LimitedEntryCol lec = (LimitedEntryCol) c;
                        DTCellValue52 value = lec.getValue();
                        if ( value != null ) {
                            cell = GuidedDTDRLUtilities.convertDTCellValueToString( value );
                        }
                    }
                } else {
                    cell = GuidedDTDRLUtilities.convertDTCellValueToString( dcv );
                }

                boolean isOtherwise = dcv.isOtherwise();
                boolean isValid = isOtherwise;

                //Otherwise values are automatically valid as they're constructed from the other rules
                if ( !isOtherwise ) {
                    isValid = validCell( cell );
                }

                //If operator is "== null" or "!= null" add constraint if table value is true
                if ( c.getOperator() != null && (c.getOperator().equals( "== null" ) || c.getOperator().equals( "!= null" )) ) {
                    isValid = dcv.getBooleanValue();
                }

                if ( isValid ) {

                    // get or create the pattern it belongs too
                    IFactPattern ifp = findByFactPattern( patterns,
                                                          p.getBoundName() );

                    //If the pattern does not exist create one suitable
                    if ( ifp == null ) {
                        FactPattern fp = new FactPattern( p.getFactType() );
                        fp.setBoundName( p.getBoundName() );
                        fp.setNegated( p.isNegated() );
                        fp.setWindow( p.getWindow() );
                        if ( p.getEntryPointName() != null && p.getEntryPointName().length() > 0 ) {
                            FromEntryPointFactPattern fep = new FromEntryPointFactPattern();
                            fep.setEntryPointName( p.getEntryPointName() );
                            fep.setFactPattern( fp );
                            patterns.add( fep );
                            ifp = fep;
                        } else {
                            patterns.add( fp );
                            ifp = fp;
                        }
                    }

                    //Extract the FactPattern from the IFactPattern
                    FactPattern fp;
                    if ( ifp instanceof FactPattern ) {
                        fp = (FactPattern) ifp;
                    } else if ( ifp instanceof FromEntryPointFactPattern ) {
                        FromEntryPointFactPattern fep = (FromEntryPointFactPattern) ifp;
                        fp = fep.getFactPattern();
                    } else {
                        throw new IllegalArgumentException( "Inexpected IFactPattern implementation found." );
                    }

                    //Add the constraint from this cell
                    switch ( c.getConstraintValueType() ) {
                        case BaseSingleFieldConstraint.TYPE_LITERAL :
                        case BaseSingleFieldConstraint.TYPE_RET_VALUE :
                            if ( !isOtherwise ) {
                                FieldConstraint fc = makeSingleFieldConstraint( c,
                                                                                cell );
                                fp.addConstraint( fc );
                            } else {
                                FieldConstraint fc = makeSingleFieldConstraint( c,
                                                                                allColumns,
                                                                                data );
                                fp.addConstraint( fc );
                            }
                            break;
                        case BaseSingleFieldConstraint.TYPE_PREDICATE :
                            SingleFieldConstraint pred = new SingleFieldConstraint();
                            pred.setConstraintValueType( c.getConstraintValueType() );
                            if ( c.getFactField() != null
                                 && c.getFactField().indexOf( "$param" ) > -1 ) {
                                // handle interpolation
                                pred.setValue( c.getFactField().replace( "$param",
                                                                         cell ) );
                            } else {
                                pred.setValue( cell );
                            }
                            fp.addConstraint( pred );
                            break;
                        default :
                            throw new IllegalArgumentException( "Unknown constraintValueType: "
                                                                + c.getConstraintValueType() );
                    }

                }
            }
        }
        rm.lhs = patterns.toArray( new IPattern[patterns.size()] );
    }

    /**
     * take a CSV list and turn it into DRL syntax
     */
    String makeInList(String cell) {
        if ( cell.startsWith( "(" ) ) return cell;
        String res = "";
        StringTokenizer st = new StringTokenizer( cell,
                                                  "," );
        while ( st.hasMoreTokens() ) {
            String t = st.nextToken().trim();
            if ( t.startsWith( "\"" ) ) {
                res += t;
            } else {
                res += "\"" + t + "\"";
            }
            if ( st.hasMoreTokens() ) res += ", ";
        }
        return "(" + res + ")";
    }

    private boolean no(String operator) {
        return operator == null || "".equals( operator );
    }

    private IFactPattern findByFactPattern(List<IFactPattern> patterns,
                                           String boundName) {
        if ( boundName == null ) {
            return null;
        }

        for ( IFactPattern ifp : patterns ) {
            if ( ifp instanceof FactPattern ) {
                FactPattern fp = (FactPattern) ifp;
                if ( fp.getBoundName() != null && fp.getBoundName().equals( boundName ) ) {
                    return fp;
                }
            } else if ( ifp instanceof FromEntryPointFactPattern ) {
                FromEntryPointFactPattern fefp = (FromEntryPointFactPattern) ifp;
                FactPattern fp = fefp.getFactPattern();
                if ( fp.getBoundName() != null && fp.getBoundName().equals( boundName ) ) {
                    return fp;
                }
            }
        }
        return null;
    }

    void doAttribs(List<DTColumnConfig52> allColumns,
                   List<AttributeCol52> attributeCols,
                   List<DTCellValue52> row,
                   RuleModel rm) {
        List<RuleAttribute> attribs = new ArrayList<RuleAttribute>();
        for ( int j = 0; j < attributeCols.size(); j++ ) {
            AttributeCol52 at = attributeCols.get( j );
            int index = allColumns.indexOf( at );

            String cell = GuidedDTDRLUtilities.convertDTCellValueToString( row.get( index ) );

            if ( validCell( cell ) ) {

                //If instance of "otherwise" column then flag RuleModel as being negated
                if ( at.getAttribute().equals( GuidedDecisionTable52.NEGATE_RULE_ATTR ) ) {
                    rm.setNegated( Boolean.valueOf( cell ) );
                } else {
                    attribs.add( new RuleAttribute( at.getAttribute(),
                                                    cell ) );
                }
            } else if ( at.getDefaultValue() != null ) {
                attribs.add( new RuleAttribute( at.getAttribute(),
                                                at.getDefaultValue() ) );
            }
        }
        if ( attribs.size() > 0 ) {
            rm.attributes = attribs.toArray( new RuleAttribute[attribs.size()] );
        }
    }

    void doMetadata(List<DTColumnConfig52> allColumns,
                    List<MetadataCol52> metadataCols,
                    List<DTCellValue52> row,
                    RuleModel rm) {

        // setup temp list
        List<RuleMetadata> metadataList = new ArrayList<RuleMetadata>();

        for ( int j = 0; j < metadataCols.size(); j++ ) {
            MetadataCol52 meta = metadataCols.get( j );
            int index = allColumns.indexOf( meta );

            String cell = GuidedDTDRLUtilities.convertDTCellValueToString( row.get( index ) );

            if ( validCell( cell ) ) {
                metadataList.add( new RuleMetadata( meta.getMetadata(),
                                                    cell ) );
            }
        }
        if ( metadataList.size() > 0 ) {
            rm.metadataList = metadataList.toArray( new RuleMetadata[metadataList.size()] );
        }
    }

    String getName(String tableName,
                   Number num) {
        return "Row " + num.longValue() + " " + tableName;
    }

    boolean validCell(String c) {
        return (c != null) && (!c.trim().equals( "" ));
    }

    private class LabelledAction {
        String  boundName;
        IAction action;
    }

    //Build a normal SingleFieldConstraint for a non-otherwise cell value
    private FieldConstraint makeSingleFieldConstraint(ConditionCol52 c,
                                                      String cell) {

        SingleFieldConstraint sfc = new SingleFieldConstraint( c.getFactField() );

        //Condition columns can be defined as having no operator, in which case the operator
        //is taken from the cell's value. Pretty yucky really if we're to be able to perform
        //expansion and contraction of decision table columns.... this might have to go.
        if ( no( c.getOperator() ) ) {

            String[] a = cell.split( "\\s" );
            if ( a.length > 1 ) {
                sfc.setOperator( a[0] );
                sfc.setValue( a[1] );
            } else {
                sfc.setValue( cell );
            }
        } else {

            sfc.setOperator( c.getOperator() );
            if ( c.getOperator().equals( "in" ) ) {
                sfc.setValue( makeInList( cell ) );
            } else {
                if ( !c.getOperator().equals( "== null" ) && !c.getOperator().equals( "!= null" ) ) {
                    sfc.setValue( cell );
                }
            }

        }
        if ( c.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL && c.isBound() ) {
            sfc.setFieldBinding( c.getBinding() );
        }
        sfc.setParameters( c.getParameters() );
        sfc.setConstraintValueType( c.getConstraintValueType() );
        sfc.setFieldType( c.getFieldType() );
        return sfc;
    }

    //Build a SingleFieldConstraint for an otherwise cell value
    private FieldConstraint makeSingleFieldConstraint(ConditionCol52 c,
                                                      List<DTColumnConfig52> allColumns,
                                                      List<List<DTCellValue52>> data) {

        OtherwiseBuilder builder = GuidedDTDRLOtherwiseHelper.getBuilder( c );
        return builder.makeFieldConstraint( c,
                                            allColumns,
                                            data );
    }

}
